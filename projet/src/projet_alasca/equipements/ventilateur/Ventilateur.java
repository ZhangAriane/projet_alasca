package projet_alasca.equipements.ventilateur;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.AcceleratedAndSimulationClock;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationConnector;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationOutboundPort;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.equipements.ventilateur.mil.LocalSimulationArchitectures;
import projet_alasca.equipements.ventilateur.mil.VentilateurStateModel;
import projet_alasca.equipements.ventilateur.mil.events.SetHighVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetLowVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetMediumVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOffVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOnVentilateur;
import projet_alasca.etape3.utils.ExecutionType;
import projet_alasca.etape3.utils.SimulationType;

@OfferedInterfaces(offered = {VentilateurUserCI.class})
@RequiredInterfaces(required = {ClocksServerWithSimulationCI.class})
public class Ventilateur
extends	   AbstractCyPhyComponent  
implements VentilateurImplementationI
{

	// Constants et variables : 
	public static final String			REFLECTION_INBOUND_PORT_URI =
			"VENTILATEUR-RIP-URI";	
	public static final String			INBOUND_PORT_URI =
			"VENTILATEUR-INBOUND-PORT-URI";							
	public static boolean	VERBOSE = false;

	public static int	X_RELATIVE_POSITION = 0;
	public static int	Y_RELATIVE_POSITION = 0;


	public static final VentilateurState	INITIAL_STATE = VentilateurState.OFF;

	public static final VentilateurMode	INITIAL_MODE = VentilateurMode.LOW;


	protected VentilateurState	currentState;	
	protected VentilateurMode	currentMode;

	protected VentilateurInboundPort	vip;


	//Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** current type of simulation.											*/
	protected final SimulationType		currentSimulationType;

	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String				clockURI;
	/** plug-in holding the local simulation architecture and simulators.	*/
	protected AtomicSimulatorPlugin		asp;
	/** URI of the global simulation architecture to be created or the
	 *  empty string if the component does not execute as a simulation.		*/
	protected final String				globalArchitectureURI;
	/** URI of the local simulation architecture used to compose the global
	 *  simulation architecture or the empty string if the component does
	 *  not execute as a simulation.										*/
	protected final String				localArchitectureURI;
	/** time unit in which {@code simulatedStartTime} and
	 *  {@code simulationDuration} are expressed.							*/
	protected final TimeUnit			simulationTimeUnit;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double					accFactor;



	protected static boolean	glassBoxInvariants(Ventilateur vt)
	{
		assert 	vt != null : new PreconditionException("hd != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				vt.currentState != null,
				Ventilateur.class, vt,
				"currentState != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				vt.currentMode != null,
				Ventilateur.class, vt,
				"currentMode != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				vt.currentExecutionType != null,
				Ventilateur.class, vt,
				"currentExecutionType != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				vt.currentSimulationType != null,
				Ventilateur.class, vt,
				"hcurrentSimulationType != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!vt.currentExecutionType.isStandard() ||
				vt.currentSimulationType.isNoSimulation(),
				Ventilateur.class, vt,
				"!currentExecutionType.isStandard() || "
						+ "currentSimulationType.isNoSimulation()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				vt.currentSimulationType.isNoSimulation() ||
				(vt.globalArchitectureURI != null &&
				!vt.globalArchitectureURI.isEmpty()),
				Ventilateur.class, vt,
				"currentSimulationType.isNoSimulation() || "
						+ "(globalArchitectureURI != null && "
						+ "!globalArchitectureURI.isEmpty())");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				vt.currentSimulationType.isNoSimulation() ||
				(vt.localArchitectureURI != null &&
				!vt.localArchitectureURI.isEmpty()),
				Ventilateur.class, vt,
				"currentSimulationType.isNoSimulation() || "
						+ "(localArchitectureURI != null && "
						+ "!localArchitectureURI.isEmpty())");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!vt.currentSimulationType.isSimulated() ||
				vt.simulationTimeUnit != null,
				Ventilateur.class, vt,
				"!currentSimulationType.isSimulated() || "
						+ "simulationTimeUnit != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!vt.currentSimulationType.isRealTimeSimulation() ||
				vt.accFactor > 0.0,
				Ventilateur.class, vt,
				"!vt.currentSimulationType.isRealTimeSimulation() || "
						+ "vt.accFactor > 0.0");
		return ret;
	}
	
	protected static boolean	blackBoxInvariants(Ventilateur vt)
	{
		assert 	vt != null : new PreconditionException("hd != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
					X_RELATIVE_POSITION >= 0,
							Ventilateur.class, vt,
					"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					Y_RELATIVE_POSITION >= 0,
							Ventilateur.class, vt,
					"Y_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					REFLECTION_INBOUND_PORT_URI != null &&
								!REFLECTION_INBOUND_PORT_URI.isEmpty(),
								Ventilateur.class, vt,
					"REFLECTION_INBOUND_PORT_URI != null && "
							+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
							Ventilateur.class, vt,
					"INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					INITIAL_STATE != null,
							Ventilateur.class, vt,
					"INITIAL_STATE != null");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					INITIAL_MODE != null,
							Ventilateur.class, vt,
					"INITIAL_MODE != null");
		return ret;
	}

	//---------------------------------------------------------
	protected Ventilateur()  throws Exception{
		this(INBOUND_PORT_URI);

	}


	protected	Ventilateur(
			String VentilateurInboundPortURI)
					throws Exception
	{
		this(VentilateurInboundPortURI,ExecutionType.STANDARD);
	}

	protected			Ventilateur(
			String ventilateurInboundPortURI,
			ExecutionType currentExecutionType
			) throws Exception
		{
			this(REFLECTION_INBOUND_PORT_URI, ventilateurInboundPortURI,
				 currentExecutionType, SimulationType.NO_SIMULATION,
				 null, null, null, 0.0, null);

			assert	currentExecutionType.isTest() :
					new PreconditionException("currentExecutionType.isTest()");
		}
	
	protected			Ventilateur(
			String reflectionInboundPortURI,
			String ventilateurInboundPortURI,
			ExecutionType currentExecutionType,
			SimulationType currentSimulationType,
			String globalArchitectureURI,
			String localArchitectureURI,
			TimeUnit simulationTimeUnit,
			double accFactor,
			String clockURI
			) throws Exception
		{
			// one thread for the method execute and one to answer the calls to
			// the component services
			super(reflectionInboundPortURI, 2, 0);

			assert	ventilateurInboundPortURI != null &&
												!ventilateurInboundPortURI.isEmpty() :
					new PreconditionException(
							"ventilateurInboundPortURI != null && "
							+ "!ventilateurInboundPortURI.isEmpty()");
			assert	currentExecutionType != null :
					new PreconditionException("currentExecutionType != null");
			assert	currentExecutionType.isStandard() ||
										clockURI != null && !clockURI.isEmpty() :
					new PreconditionException(
							"currentExecutionType.isStandard() || "
							+ "clockURI != null && !clockURI.isEmpty()");
			assert	!currentExecutionType.isStandard() ||
											currentSimulationType.isNoSimulation() :
					new PreconditionException(
							"!currentExecutionType.isStandard() || "
							+ "currentSimulationType.isNoSimulation()");
			assert	currentSimulationType.isNoSimulation() ||
							(globalArchitectureURI != null &&
												!globalArchitectureURI.isEmpty()) :
					new PreconditionException(
							"currentSimulationType.isNoSimulation() || "
							+ "(globalArchitectureURI != null && "
							+ "!globalArchitectureURI.isEmpty())");
			assert	currentSimulationType.isNoSimulation() ||
							(localArchitectureURI != null && 
												!localArchitectureURI.isEmpty()) :
					new PreconditionException(
							"currentSimulationType.isNoSimulation() || "
							+ "(localArchitectureURI != null && "
							+ "!localArchitectureURI.isEmpty())");
			assert	!currentSimulationType.isSimulated() ||
														simulationTimeUnit != null :
					new PreconditionException(
							"!currentSimulationType.isSimulated() || "
							+ "simulationTimeUnit != null");
			assert	!currentSimulationType.isRealTimeSimulation() ||
																accFactor > 0.0 :
					new PreconditionException(
							"!currentSimulationType.isRealTimeSimulation() || "
							+ "accFactor > 0.0");

			this.currentExecutionType = currentExecutionType;
			this.currentSimulationType = currentSimulationType;
			this.globalArchitectureURI = globalArchitectureURI;
			this.localArchitectureURI = localArchitectureURI;
			this.simulationTimeUnit = simulationTimeUnit;
			this.accFactor = accFactor;
			this.clockURI = clockURI;

			this.initialise(ventilateurInboundPortURI);

			assert	Ventilateur.glassBoxInvariants(this) :
					new ImplementationInvariantException(
							"Ventilateur.glassBoxInvariants(this)");
			assert	Ventilateur.blackBoxInvariants(this) :
					new InvariantException("Ventilateur.blackBoxInvariants(this)");
		}




	protected void	initialise(String ventilateurInboundPortURI)
			throws Exception
	{
		assert	ventilateurInboundPortURI != null :
			new PreconditionException(
					"VentilateurInboundPortURI != null");
		assert	!ventilateurInboundPortURI.isEmpty() :
			new PreconditionException(
					"!VentilateurInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.vip = new VentilateurInboundPort(ventilateurInboundPortURI, this);
		this.vip.publishPort();

		switch (this.currentSimulationType) {
		case MIL_SIMULATION:
			Architecture architecture = null;
			if (this.currentExecutionType.isUnitTest()) {
				architecture =
					LocalSimulationArchitectures.
						createVentilateurMILLocalArchitecture4UnitTest(
													this.localArchitectureURI,
													this.simulationTimeUnit);
			} else {
				assert	this.currentExecutionType.isIntegrationTest();
				architecture =
						LocalSimulationArchitectures.
							createVentilateurMILArchitecture4IntegrationTest(
													this.localArchitectureURI,
													this.simulationTimeUnit);
			}
			assert	architecture.getRootModelURI().equals(
												this.localArchitectureURI) :
					new BCMException(
							"local simulation architecture "
							+ this.localArchitectureURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.global2localSimulationArchitectureURIS.
					put(this.globalArchitectureURI, this.localArchitectureURI);
			break;
		case MIL_RT_SIMULATION:
			// in MIL RT simulations, the HairDryer component uses the same
			// simulators as in SIL simulations
		case SIL_SIMULATION:
			architecture = null;
			if (this.currentExecutionType.isUnitTest()) {
				architecture =
					LocalSimulationArchitectures.
								createVentilateurMIL_RT_Architecture4UnitTest(
									this.localArchitectureURI,
									this.simulationTimeUnit,
									this.accFactor);
			} else {
				assert	this.currentExecutionType.isIntegrationTest();
				architecture =
						LocalSimulationArchitectures.
							createVentilateurMIL_RT_Architecture4IntegrationTest(
													this.localArchitectureURI,
													this.simulationTimeUnit,
													this.accFactor);

			}
			assert	architecture.getRootModelURI().equals(
													this.localArchitectureURI) :
					new BCMException(
							"local simulation architecture "
							+ this.localArchitectureURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.global2localSimulationArchitectureURIS.
					put(this.globalArchitectureURI, this.localArchitectureURI);
			break;
		default:
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Ventilateur component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		// create the simulation plug-in given the current type of simulation
		// and its local architecture i.e., for the current execution
		try {
			switch (this.currentSimulationType) {
			case MIL_SIMULATION:
				this.asp = new AtomicSimulatorPlugin();
				String uri = this.global2localSimulationArchitectureURIS.
												get(this.globalArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulationArchitectures.get(uri);
				this.asp.setPluginURI(uri);
				this.asp.setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case MIL_RT_SIMULATION:
				// for the HairDryer, real time MIL and SIL use the same
				// simulation models
				this.asp = new RTAtomicSimulatorPlugin();
				uri = this.global2localSimulationArchitectureURIS.
												get(this.globalArchitectureURI);
				architecture =
					(Architecture) this.localSimulationArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case SIL_SIMULATION:
				// for the HairDryer, real time MIL and SIL use the same
				// simulation models
				this.asp = new RTAtomicSimulatorPlugin();
				uri = this.global2localSimulationArchitectureURIS.
												get(this.globalArchitectureURI);
				architecture =
					(Architecture) this.localSimulationArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case NO_SIMULATION:
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}		
	}

	@Override
	public void			execute() throws Exception
	{
		if (this.currentExecutionType.isUnitTest() &&
								this.currentSimulationType.isSILSimulation()) {
			// First, the component must synchronise with other components
			// to start the execution of the test scenario; we use a
			// time-triggered synchronisation scheme with the accelerated clock
			ClocksServerWithSimulationOutboundPort clocksServerOutboundPort =
					new ClocksServerWithSimulationOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerWithSimulationConnector.class.getCanonicalName());
			this.logMessage("Ventilateur gets the clock.");
			AcceleratedAndSimulationClock acceleratedClock =
				clocksServerOutboundPort.getClockWithSimulation(this.clockURI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();

			this.asp.createSimulator();
			this.asp.setSimulationRunParameters(new HashMap<>());
			this.asp.initialiseSimulation(
						new Time(acceleratedClock.getSimulatedStartTime(),
								 this.simulationTimeUnit),
						new Duration(acceleratedClock.getSimulatedDuration(),
									 this.simulationTimeUnit));
			// schedule the start of the SIL (real time) simulation
			this.asp.startRTSimulation(
					TimeUnit.NANOSECONDS.toMillis(
							acceleratedClock.getSimulationStartEpochNanos()),
					acceleratedClock.getSimulatedStartTime(),
					acceleratedClock.getSimulatedDuration());
			// wait until the simulation ends
			acceleratedClock.waitUntilSimulationEnd();
			// give some time for the end of simulation catering tasks
			Thread.sleep(200L);
			this.logMessage(this.asp.getFinalReport().toString());
		}
	}


	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.vip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	

	@Override
	public VentilateurState getState() throws Exception {

		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur returns its state : " +
					this.currentState + ".\n");
		}

		return this.currentState;
	}


	@Override
	public VentilateurMode getMode() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur returns its mode : " +
					this.currentMode + ".\n");
		}

		return this.currentMode;
	}


	@Override
	public void turnOn() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is turned on.\n");
		}

		assert	this.getState() == VentilateurState.OFF :
			new PreconditionException("getState() == VentilateurState.OFF");

		this.currentState = VentilateurState.ON;
		this.currentMode = VentilateurMode.LOW;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												VentilateurStateModel.SIL_URI,
												t -> new SwitchOnVentilateur(t));
		}
	}


	@Override
	public void turnOff() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is turned off.\n");
		}

		assert	this.getState() == VentilateurState.ON :
			new PreconditionException("getState() == VentilateurState.ON");

		this.currentState = VentilateurState.OFF;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to off.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												VentilateurStateModel.SIL_URI,
												t -> new SwitchOffVentilateur(t));
		}

	}


	@Override
	public void setHigh() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is set high.\n");
		}

		assert	this.getState() == VentilateurState.ON :
			new PreconditionException("getState() == VentilateurState.ON");
		assert	this.getMode() == VentilateurMode.MEDIUM || this.getMode() == VentilateurMode.LOW :
			new PreconditionException("getMode() == VentilateurMode.MEDIUM or getMode() == VentilateurMode.LOW ");

		this.currentMode = VentilateurMode.HIGH;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its mode to high.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												VentilateurStateModel.SIL_URI,
												t -> new SetHighVentilateur(t));
		}

	}


	@Override
	public void setMedium() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is set medium.\n");
		}
		assert	this.getState() == VentilateurState.ON :
			new PreconditionException("getState() == VentilateurState.ON");
		assert	this.getMode() == VentilateurMode.HIGH  || this.getMode() == VentilateurMode.LOW:
			new PreconditionException("getMode() == VentilateurMode.HIGH or getMode() == VentilateurMode.LOW");

		this.currentMode = VentilateurMode.MEDIUM;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its mode to low.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												VentilateurStateModel.SIL_URI,
												t -> new SetMediumVentilateur(t));
		}

	}


	@Override
	public void setLow() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is set low.\n");
		}

		assert	this.getState() == VentilateurState.ON :
			new PreconditionException("getState() == VentilateurState.ON");
		assert	this.getMode() == VentilateurMode.HIGH  || this.getMode() == VentilateurMode.MEDIUM:
			new PreconditionException("getMode() == VentilateurMode.HIGH or getMode() == VentilateurMode.MEDIUM");

		this.currentMode =VentilateurMode.LOW;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its mode to low.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												VentilateurStateModel.SIL_URI,
												t -> new SetLowVentilateur(t));
		}

	}








}
