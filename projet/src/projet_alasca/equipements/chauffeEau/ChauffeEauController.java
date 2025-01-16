package projet_alasca.equipements.chauffeEau;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.utils.aclocks.AcceleratedAndSimulationClock;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationConnector;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationOutboundPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.equipements.chauffeEau.ChauffeEau.ChauffeEauState;
import projet_alasca.equipements.chauffeEau.ChauffeEauSensorDataCI.ChauffeEauSensorRequiredPullCI;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauActuatorConnector;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauActuatorOutboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauSensorDataConnector;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauSensorDataOutboundPort;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauCompoundMeasure;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauMeasureI;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauSensorData;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauStateMeasure;
import projet_alasca.etape3.utils.ExecutionType;
import projet_alasca.etape3.utils.SimulationType;;
// -----------------------------------------------------------------------------
/**
 * The class <code>ThermostatedChauffeEauController</code> implements a controller
 * component for the thermostated ChauffeEau.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The controller is a simple fixed period threshold-based controller with
 * hysteresis. It has two control modes. In a pull mode, it calls the pull
 * sensors of the ChauffeEau to get the target and current temperatures. In the
 * push mode, it sets the period for the ChauffeEau to push the temperatures data
 * towards it and perform once its control decision upon each reception. It
 * also uses a push pattern to receive changes in the state of the ChauffeEau. For
 * example, when the ChauffeEau is switched on, it sends a state data telling the
 * controller that it is now on so that the controller can begins its control
 * until the ChauffeEau is switched off.
 * </p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code hysteresis > 0.0}
 * invariant	{@code controlPeriod > 0}
 * invariant	{@code currentExecutionType.isStandard() || clockURI != null && !clockURI.isEmpty()}
 * invariant	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
 * invariant	{@code actuatorIBPURI != null && !actuatorIBPURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code STANDARD_HYSTERESIS > 0.0}
 * invariant	{@code STANDARD_CONTROL_PERIOD > 0}
 * </pre>
 * 
 * <p>Created on : 2022-10-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@RequiredInterfaces(required={ChauffeEauSensorRequiredPullCI.class,
							  ChauffeEauActuatorCI.class,
							  ClocksServerWithSimulationCI.class})
@OfferedInterfaces(offered={DataRequiredCI.PushCI.class})
//-----------------------------------------------------------------------------
public class			ChauffeEauController
extends		AbstractComponent
implements	ChauffeEauPushImplementationI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	public static enum	ControlMode {
		PULL,
		PUSH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, some methods trace their actions.						*/
	public static boolean		VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int			X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int			Y_RELATIVE_POSITION = 0;
	/** when true, some methods trace their actions.						*/
	public static boolean		DEBUG = false;

	/** the standard hysteresis used by the controller.						*/
	public static final double	STANDARD_HYSTERESIS = 0.1;
	/** standard control period in seconds.									*/
	public static final double	STANDARD_CONTROL_PERIOD = 60.0;

	/** URI of the sensor inbound port on the {@code ThermostatedChauffeEau}.	*/
	protected String							sensorIBP_URI;
	/** URI of the actuator inbound port on the {@code ThermostatedChauffeEau}.	*/
	protected String							actuatorIBPURI;
	/** sensor data outbound port connected to the {@code ChauffeEau}.			*/
	protected ChauffeEauSensorDataOutboundPort		sensorOutboundPort;
	/** actuator outbound port connected to the {@code ChauffeEau}.				*/
	protected ChauffeEauActuatorOutboundPort		actuatorOutboundPort;

	/** the actual hysteresis used in the control loop.						*/
	protected double							hysteresis;
	/* user set control period in seconds.									*/
	protected double							controlPeriod;
	/** control mode (push or pull) for the current execution.				*/
	protected final ControlMode					controlMode;
	/* actual control period, either in pure real time (not under test)
	 * or in accelerated time (under test), expressed in nanoseconds;
	 * used for scheduling the control task.								*/
	protected long								actualControlPeriod;
	/** the current state of the ChauffeEau as perceived through the sensor
	 *  data received from the {@code ThermostatedChauffeEau}.					*/
	protected ChauffeEauState						currentState;
	/** lock controlling the access to {@code currentState}.				*/
	protected final Object						stateLock;

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType				currentExecutionType;
	/** current type of simulation.											*/
	protected final SimulationType				currentSimulationType;

//	/** outbound port to connect to the centralised clock server.			*/
//	protected ClocksServerOutboundPort			clockServerOBP;
	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String						clockURI;
	/** accelerated clock governing the timing of actions in the test
	 *  scenarios.															*/
	protected final CompletableFuture<AcceleratedClock>	clock;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the glass-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the glass-box invariants are observed, false otherwise.
	 */
	protected static boolean	glassBoxInvariants(ChauffeEauController instance)
	{
		assert	instance != null :
				new PreconditionException("instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.hysteresis > 0.0,
					ChauffeEauController.class,
					instance,
					"hysteresis > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.controlPeriod > 0,
					ChauffeEauController.class,
					instance,
					"controlPeriod > 0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.currentExecutionType.isStandard() ||
						instance.clockURI != null && !instance.clockURI.isEmpty(),
					ChauffeEauController.class,
					instance,
					"currentExecutionType.isStandard() || "
					+ "clockURI != null && !clockURI.isEmpty()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.sensorIBP_URI != null &&
											!instance.sensorIBP_URI.isEmpty(),
					ChauffeEauController.class, instance,
					"sensorIBP_URI != null && !sensorIBP_URI.isEmpty()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.actuatorIBPURI != null &&
											!instance.actuatorIBPURI.isEmpty(),
					ChauffeEauController.class, instance,
					"actuatorIBPURI != null && !actuatorIBPURI.isEmpty()");
		return ret;
	}

	/**
	 * return true if the black-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	blackBoxInvariants(ChauffeEauController instance)
	{
		assert	instance != null :
				new PreconditionException("instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
					X_RELATIVE_POSITION >= 0,
					ChauffeEauController.class, instance,
					"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					Y_RELATIVE_POSITION >= 0,
					ChauffeEauController.class, instance,
					"Y_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					STANDARD_HYSTERESIS > 0.0,
					ChauffeEauController.class,
					instance,
					"STANDARD_HYSTERESIS > 0.0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					STANDARD_CONTROL_PERIOD > 0,
					ChauffeEauController.class,
					instance,
					"STANDARD_CONTROL_PERIOD > 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the controller component for standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI		URI of the ChauffeEau sensor inbound port.
	 * @param actuatorIBP_URI	URI of the ChauffeEau actuator inbound port.
	 * @throws Exception		<i>to do</i>.
	 */
	protected			ChauffeEauController(
		String sensorIBP_URI,
		String actuatorIBP_URI
		) throws Exception
	{
		this(sensorIBP_URI, actuatorIBP_URI,
			 STANDARD_HYSTERESIS, STANDARD_CONTROL_PERIOD,
			 ControlMode.PULL);
	}

	/**
	 * create the controller component for standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * pre	{@code hysteresis > 0.0}
	 * pre	{@code controlPeriod > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI		URI of the ChauffeEau sensor inbound port.
	 * @param actuatorIBP_URI	URI of the ChauffeEau actuator inbound port.
	 * @param hysteresis		control hysteresis around the target temperature.
	 * @param controlPeriod		control period in seconds.
	 * @param controlMode		control mode: {@code PULL} or {@code PUSH}.
	 * @throws Exception 		<i>to do</i>.
	 */
	protected			ChauffeEauController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode
		) throws Exception
	{
		this(sensorIBP_URI, actuatorIBP_URI, hysteresis, controlPeriod,
			 ControlMode.PULL, ExecutionType.STANDARD,
			 SimulationType.NO_SIMULATION, null);
	}

	/**
	 * create the controller component for test executions without simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * pre	{@code hysteresis > 0.0}
	 * pre	{@code controlPeriod > 0}
	 * pre	{@code currentExecutionType.isTest()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI			URI of the ChauffeEau sensor inbound port.
	 * @param actuatorIBP_URI		URI of the ChauffeEau actuator inbound port.
	 * @param hysteresis			control hysteresis around the target temperature.
	 * @param controlPeriod			control period in seconds.
	 * @param controlMode			control mode: {@code PULL} or {@code PUSH}.
	 * @param currentExecutionType	execution type for the next run.
	 * @throws Exception 			<i>to do</i>.
	 */
	protected			ChauffeEauController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode,
		ExecutionType currentExecutionType
		) throws Exception
	{
		this(sensorIBP_URI, actuatorIBP_URI, hysteresis, controlPeriod,
			 ControlMode.PULL, currentExecutionType,
			 SimulationType.NO_SIMULATION, null);

		assert	currentExecutionType.isTest() :
				new PreconditionException("currentExecutionType.isTest()");
	}

	/**
	 * create the controller component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * pre	{@code hysteresis > 0.0}
	 * pre	{@code controlPeriod > 0}
	 * pre	{@code currentExecutionType.isStandard() || clockURI != null && !clockURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI			URI of the ChauffeEau sensor inbound port.
	 * @param actuatorIBP_URI		URI of the ChauffeEau actuator inbound port.
	 * @param hysteresis			control hysteresis around the target temperature.
	 * @param controlPeriod			control period in seconds.
	 * @param controlMode			control mode: {@code PULL} or {@code PUSH}.
	 * @param currentExecutionType	execution type for the next run.
	 * @param currentSimulationType	simulation type for the next run.
	 * @param clockURI				URI of the clock to be used to synchronise the test scenarios and the simulation.
	 * @throws Exception 			<i>to do</i>.
	 */
	protected			ChauffeEauController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode,
		ExecutionType currentExecutionType,
		SimulationType currentSimulationType,
		String clockURI
		) throws Exception
	{
		// two standard threads in case the thread that runs the method execute
		// can be prevented to run by the thread running receiveRunningState
		// the schedulable thread pool is used to run the control task
		super(2, 1);

		assert	sensorIBP_URI != null && !sensorIBP_URI.isEmpty() :
				new PreconditionException(
						"sensorIBP_URI != null && !sensorIBP_URI.isEmpty()");
		assert	actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty() :
				new PreconditionException(
					"actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()");
		assert	hysteresis > 0.0 :
				new PreconditionException("hysteresis > 0.0");
		assert	controlPeriod > 0 :
				new PreconditionException("controlPeriod > 0");
		assert	currentExecutionType.isStandard() ||
									clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "clockURI != null && !clockURI.isEmpty()");

		this.sensorIBP_URI = sensorIBP_URI;
		this.actuatorIBPURI = actuatorIBP_URI;
		this.hysteresis = hysteresis;
		this.controlPeriod = controlPeriod;
		this.controlMode = controlMode;
		this.currentExecutionType = currentExecutionType;
		this.currentSimulationType = currentSimulationType;
		this.clockURI = clockURI;
		this.clock = new CompletableFuture<>();

		// just a common initialisation; if the run is in test mode, the
		// acceleration factor will be taken into account at start time
		// first convert to nanoseconds before casting to get a better
		// precision for fractional control periods
		this.actualControlPeriod =
				(long) (this.controlPeriod * TimeUnit.SECONDS.toNanos(1));
		this.stateLock = new Object();

		this.sensorOutboundPort =
				new ChauffeEauSensorDataOutboundPort(this);
		this.sensorOutboundPort.publishPort();
		this.actuatorOutboundPort =
				new ChauffeEauActuatorOutboundPort(this);
		this.actuatorOutboundPort.publishPort();

		if (VERBOSE || DEBUG) {
			this.tracer.get().setTitle("ChauffeEau controller component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		// Invariant checking
		assert	glassBoxInvariants(this) :
				new ImplementationInvariantException("glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
				new InvariantException("blackBoxInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.sensorOutboundPort.getPortURI(),
					sensorIBP_URI,
					ChauffeEauSensorDataConnector.class.getCanonicalName());
			this.doPortConnection(
					this.actuatorOutboundPort.getPortURI(),
					this.actuatorIBPURI,
					ChauffeEauActuatorConnector.class.getCanonicalName());

			synchronized (this.stateLock) {
				this.currentState = ChauffeEauState.OFF;
			}

			if (VERBOSE) {
				this.traceMessage("ChauffeEau controller starts.\n");
			}
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (!this.currentExecutionType.isStandard()) {
			ClocksServerWithSimulationOutboundPort clockServerOBP =
							new ClocksServerWithSimulationOutboundPort(this);
			clockServerOBP.publishPort();
			this.doPortConnection(
					clockServerOBP.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerWithSimulationConnector.class.getCanonicalName());
			AcceleratedAndSimulationClock clock =
					clockServerOBP.getClockWithSimulation(this.clockURI);
			this.doPortDisconnection(clockServerOBP.getPortURI());
			clockServerOBP.unpublishPort();
			clockServerOBP.destroyPort();
			// the accelerated period is in nanoseconds, hence first convert
			// the period to nanoseconds, perform the division and then
			// convert to long (hence providing a better precision than
			// first dividing and then converting to nanoseconds...)
			this.actualControlPeriod =
				(long)((this.controlPeriod * TimeUnit.SECONDS.toNanos(1))/
												clock.getAccelerationFactor());
			// release readers so that we make sure that actualControlPeriod
			// has also been properly set before
			this.clock.complete(clock);

			if (VERBOSE) {
				if (this.currentSimulationType.isMilSimulation()
							|| this.currentSimulationType.isMILRTSimulation()) {
					this.traceMessage(
						"ChauffeEauController has no MIL or MIL RT simulator yet.\n");
				} else if (this.currentSimulationType.isSILSimulation() ||
						this.currentSimulationType.isHILSimulation()) {
					this.traceMessage(
						"ChauffeEauController does not use a simulator in SIL or"
						+ " HIL simulations.\n");
				}
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("ChauffeEau controller ends.\n");
		}
		this.doPortDisconnection(this.sensorOutboundPort.getPortURI());
		this.doPortDisconnection(this.actuatorOutboundPort.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.sensorOutboundPort.unpublishPort();
			this.actuatorOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * receive and process the state data coming from the ChauffeEau component,
	 * starting the control loop if the state has changed from {@code OFF} to
	 * {@code ON}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sd != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sd		state data received from the ChauffeEau component.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void			receiveDataFromChauffeEau(DataRequiredCI.DataI sd)
	{
		assert	sd != null : new PreconditionException("sd != null");
		if (DEBUG) {
			this.traceMessage("receives ChauffeEau sensor data: " + sd + ".\n");
		}

		ChauffeEauMeasureI hm = ((ChauffeEauSensorData<ChauffeEauMeasureI>)sd).getMeasure();
		if (hm.isStateMeasure()) {
			ChauffeEauState hsd = ((ChauffeEauStateMeasure)hm).getData();
			if (this.clock != null) {
				try {
					// make sure that the clock and the accelerated control
					// period have been initialised
					this.clock.get();
					// sanity checking, the standard Java scheduler has a
					// precision no less than 10 milliseconds...
					if (this.actualControlPeriod <
										TimeUnit.MILLISECONDS.toNanos(10)) {
						System.out.println(
								"Warning: accelerated control period is "
										+ "too small ("
										+ this.actualControlPeriod +
										"), unexpected scheduling problems may"
										+ " occur!");
					}
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}

			// the current state is always updated, but only in the case
			// when the ChauffeEau is switched on that the controller begins to
			// perform the temperature control
			synchronized (this.stateLock) {
				ChauffeEauState oldState = this.currentState;
				this.currentState = hsd;
				if (hsd != ChauffeEauState.OFF && oldState == ChauffeEauState.OFF ) {
					if (this.controlMode == ControlMode.PULL) {
						if (VERBOSE) {
							this.traceMessage("start pull control.\n");
						}
						if (this.currentExecutionType.isTest() &&
								this.currentSimulationType.isNoSimulation()) {
							this.pullControLoop();
						} else {
							// if a state change has been detected from OFF to ON,
							// schedule a first execution of the control loop, which
							// in turn will schedule its next execution if needed
							this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										((ChauffeEauController)
												this.getTaskOwner()).
															pullControLoop();
										}
									},
								this.actualControlPeriod, 
								TimeUnit.NANOSECONDS);
						}
					} else {
						if (VERBOSE) {
							this.traceMessage("start push control.\n");
						}
						long cp =
							(long) (TimeUnit.SECONDS.toMillis(1)
														* this.controlPeriod);
						try {
							this.sensorOutboundPort.
									startTemperaturesPushSensor(
													cp, TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}
			}
		} else {
			assert	hm.isTemperatureMeasures();
			// in push control mode, execute one push control step
			this.pushControLoop((ChauffeEauCompoundMeasure)hm);
		}
	}

	/**
	 * implement the push control loop.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hcm	most recent temperatures measure from the ChauffeEau.
	 */
	protected void		pushControLoop(ChauffeEauCompoundMeasure hcm)
	{
		try {
			// execute the control only of the ChauffeEau is still ON
			ChauffeEauState s = ChauffeEauState.OFF;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			if (s != ChauffeEauState.OFF) {
				double current = hcm.getCurrentTemperature();
				double target = hcm.getTargetTemperature();
				this.oneControlStep(current, target, s);
			} else {
				// when the ChauffeEau is OFF, exit the control loop
				if (VERBOSE) {
					this.traceMessage("control is off.\n");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	protected void		oneControlStep(
		double current,
		double target,
		ChauffeEauState s
		) throws Exception
	{
		// tracing
		StringBuffer sb = new StringBuffer();
		if (current < target - this.hysteresis) {
			// the current room temperature is too low, start heating
			if (ChauffeEauState.HEATING != s) {
				// actuate
				this.actuatorOutboundPort.startHeating();;
			}
			if (VERBOSE) {
				StringBuffer temp = new StringBuffer();
				temp.append(current);
				temp.append(" < ");
				temp.append(target);
				if (ChauffeEauState.HEATING != s) {
					sb.append("start heating with ");
					sb.append(temp);
					sb.append(" - ");
				} else {
					sb.append("still heating with ");
					sb.append(temp);
					sb.append(" + ");
				}
				sb.append(this.hysteresis);
				sb.append(" at ");
				sb.append(this.clock.get().currentInstant());
				sb.append(".\n");
				this.traceMessage(sb.toString());
			}
		} else if (current > target + this.hysteresis) {
			// the current room temperature is high enough, stop heating
			// tracing
			if (ChauffeEauState.HEATING == s) {
				// actuate
				this.actuatorOutboundPort.stopHeating();;
			}
			StringBuffer temp = new StringBuffer();
			temp.append(current);
			temp.append(" > ");
			temp.append(target);
			if (VERBOSE) {
				if (ChauffeEauState.HEATING == s) {
					sb.append("stop heating with ");
					sb.append(temp);
					sb.append(" + ");
				} else {
					sb.append("still not heating with ");
					sb.append(temp);
					sb.append(" - ");
				}
				sb.append(this.hysteresis);
				sb.append(" at ");
				sb.append(this.clock.get().currentInstant());
				sb.append(".\n");
				this.traceMessage(sb.toString());
			}
		} else {
			if (VERBOSE) {
				if (ChauffeEauState.HEATING == s) {
					sb.append("still heating with ");
					sb.append(current);
					sb.append(" < ");
					sb.append(target);
					sb.append(" + ");
				} else {
					sb.append("still not heating with ");
					sb.append(current);
					sb.append(" > ");
					sb.append(target);
					sb.append(" - ");
			}
			sb.append(this.hysteresis);
			sb.append(" at ");
			sb.append(this.clock.get().currentInstant());
			sb.append(".\n");
				this.traceMessage(sb.toString());
			}
		}
	}

	/**
	 * implement the push control loop.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		pullControLoop()
	{
		this.traceMessage("executes a new pull control step.\n");
		try {
			// execute the control as long as the ChauffeEau is ON
			ChauffeEauState s = ChauffeEauState.OFF;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			if (s != ChauffeEauState.OFF) {
				// get the temperature data from the ChauffeEau
				@SuppressWarnings("unchecked")
				ChauffeEauSensorData<ChauffeEauCompoundMeasure> td =
						(ChauffeEauSensorData<ChauffeEauCompoundMeasure>)
										this.sensorOutboundPort.request();

				if (DEBUG) {
					this.traceMessage(td + "\n");
				}

				double current = td.getMeasure().getCurrentTemperature();
				double target = td.getMeasure().getTargetTemperature();
				this.oneControlStep(current, target, s);

				if (this.currentExecutionType.isStandard()
							|| this.currentSimulationType.isSILSimulation()
							|| this.currentSimulationType.isHILSimulation()) {
					// schedule the next execution of the loop only if the
					// current execution is standard or if it is a real time
					// simulation with code execution i.e., SIL or HIL
					// otherwise, perform only one call to pull sensors to
					// test the functionality
					this.scheduleTask(
							o -> ((ChauffeEauController)o).pullControLoop(),
							this.actualControlPeriod, 
							TimeUnit.NANOSECONDS);
				}
			} else {
				// when the ChauffeEau is OFF, exit the control loop
				if (VERBOSE) {
					this.traceMessage("exit the control.\n");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
