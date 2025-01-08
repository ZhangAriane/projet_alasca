package projet_alasca.equipements.machineCafe;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
import projet_alasca.equipements.machineCafe.mil.LocalSimulationArchitectures;
import projet_alasca.etape3.utils.ExecutionType;
import projet_alasca.etape3.utils.SimulationType;

@OfferedInterfaces(offered={MachineCafeUserCI.class})
@RequiredInterfaces(required = {ClocksServerWithSimulationCI.class})
public class MachineCafe extends AbstractCyPhyComponent implements MachineCafeImplementationI{

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	public static final String			REFLECTION_INBOUND_PORT_URI =
			"MACHINE-CAFE-RIP-URI";	
	/** URI of the coffee machine inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI = "MACHINE-CAFE-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static boolean				VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int					X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int					Y_RELATIVE_POSITION = 0;

	/** initial state of the coffee machine.									*/
	public static final CoffeeMachineState	INITIAL_STATE = CoffeeMachineState.OFF;

	/** current state (on, off) of the coffee machine .							*/
	protected CoffeeMachineState		currentState;


	/** inbound port offering the <code>MachineCafeCI</code> interface.		*/
	protected MachineCafeInboundPort	machineCafeInboundPort;

	// Execution/Simulation

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


	protected static boolean	glassBoxInvariants(MachineCafe mc)
	{
		assert 	mc != null : new PreconditionException("mc != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				mc.currentState != null,
				MachineCafe.class, mc,
				"currentState != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				mc.currentExecutionType != null,
				MachineCafe.class, mc,
				"currentExecutionType != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				mc.currentSimulationType != null,
				MachineCafe.class, mc,
				"mc.urrentSimulationType != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!mc.currentExecutionType.isStandard() ||
				mc.currentSimulationType.isNoSimulation(),
				MachineCafe.class, mc,
				"!currentExecutionType.isStandard() || "
						+ "currentSimulationType.isNoSimulation()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				mc.currentSimulationType.isNoSimulation() ||
				(mc.globalArchitectureURI != null &&
				!mc.globalArchitectureURI.isEmpty()),
				MachineCafe.class, mc,
				"currentSimulationType.isNoSimulation() || "
						+ "(globalArchitectureURI != null && "
						+ "!globalArchitectureURI.isEmpty())");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				mc.currentSimulationType.isNoSimulation() ||
				(mc.localArchitectureURI != null &&
				!mc.localArchitectureURI.isEmpty()),
				MachineCafe.class, mc,
				"currentSimulationType.isNoSimulation() || "
						+ "(localArchitectureURI != null && "
						+ "!localArchitectureURI.isEmpty())");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!mc.currentSimulationType.isSimulated() ||
				mc.simulationTimeUnit != null,
				MachineCafe.class, mc,
				"!currentSimulationType.isSimulated() || "
						+ "simulationTimeUnit != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!mc.currentSimulationType.isRealTimeSimulation() ||
				mc.accFactor > 0.0,
				MachineCafe.class, mc,
				"!mc.currentSimulationType.isRealTimeSimulation() || "
						+ "mc");
		return ret;
	}

	protected static boolean	blackBoxInvariants(MachineCafe mc)
	{
		assert 	mc != null : new PreconditionException("mc != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				X_RELATIVE_POSITION >= 0,
				MachineCafe.class, mc,
				"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				Y_RELATIVE_POSITION >= 0,
				MachineCafe.class, mc,
				"Y_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
				!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				MachineCafe.class, mc,
				"REFLECTION_INBOUND_PORT_URI != null && "
						+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
				MachineCafe.class, mc,
				"INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				INITIAL_STATE != null,
				MachineCafe.class, mc,
				"INITIAL_STATE != null");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------



	protected MachineCafe() throws Exception{
		this(INBOUND_PORT_URI);
	}

	protected MachineCafe(String machineCafeInboundPortURI) throws Exception{
		this(machineCafeInboundPortURI, ExecutionType.STANDARD);
	}


	protected			MachineCafe(
			String machineCafeInboundPortURI,
			ExecutionType currentExecutionType
			) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI,machineCafeInboundPortURI,
				currentExecutionType, SimulationType.NO_SIMULATION,
				null,null,null,0.0,null);


		assert	currentExecutionType.isTest() :
			new PreconditionException("currentExecutionType.isTest()");
	}



	protected			MachineCafe(
			String reflectionInboundPortURI,
			String machineCafeInboundPortURI,
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

		assert	machineCafeInboundPortURI != null &&
				!machineCafeInboundPortURI.isEmpty() :
					new PreconditionException(
							"machineCafeInboundPortURI != null && "
									+ "!machineCafeInboundPortURI.isEmpty()");
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

		this.initialise(machineCafeInboundPortURI);

		assert	MachineCafe.glassBoxInvariants(this) :
			new ImplementationInvariantException(
					"MachineCafe.glassBoxInvariants(this)");
		assert	MachineCafe.blackBoxInvariants(this) :
			new InvariantException("MachineCafe.blackBoxInvariants(this)");
	}

	protected void		initialise(String machineCafeInboundPortURI)
			throws Exception
	{
		assert	machineCafeInboundPortURI != null :
			new PreconditionException("machineCafeInboundPortURI != null");
		assert	!machineCafeInboundPortURI.isEmpty() :
			new PreconditionException(
					"!machineCafeInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.machineCafeInboundPort = new MachineCafeInboundPort(machineCafeInboundPortURI, this);
		this.machineCafeInboundPort.publishPort();

		// create the simulation architecture given the current type of
		// simulation i.e., for the current execution
		switch (this.currentSimulationType) {
		case MIL_SIMULATION:
			Architecture architecture = null;
			if (this.currentExecutionType.isUnitTest()) {
				architecture =
						LocalSimulationArchitectures.
						createMachineCafeMILLocalArchitecture4UnitTest(
								this.localArchitectureURI,
								this.simulationTimeUnit);
			} else {
				assert	this.currentExecutionType.isIntegrationTest();
				architecture =
						LocalSimulationArchitectures.
						createMachineCafeMILArchitecture4IntegrationTest(
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
						createMachineCafeMIL_RT_Architecture4UnitTest(
								this.localArchitectureURI,
								this.simulationTimeUnit,
								this.accFactor);
			} else {
				assert	this.currentExecutionType.isIntegrationTest();
				architecture =
						LocalSimulationArchitectures.
						createMachineCafeMIL_RT_Architecture4IntegrationTest(
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
			this.tracer.get().setTitle("Machine cafe component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
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

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
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
			this.logMessage("MachineCafe gets the clock.");
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
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.machineCafeInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------


	@Override
	public CoffeeMachineState getState() throws Exception {
		if (MachineCafe.VERBOSE) {
			this.traceMessage("Coffee machine returns its state : " +
					this.currentState + ".\n");
		}
		return this.currentState;
	}


	@Override
	public void turnOn() throws Exception {
		if (MachineCafe.VERBOSE) {
			this.traceMessage("Coffee machine is turned on.\n");
		}

		assert	this.getState() == CoffeeMachineState.OFF :
			new PreconditionException("getState() == CoffeeMachineState.OFF");

		this.currentState = CoffeeMachineState.ON;

	}

	@Override
	public void turnOff() throws Exception {
		if (MachineCafe.VERBOSE) {
			this.traceMessage("Coffee machine is turned off.\n");
		}

		assert	this.getState() == CoffeeMachineState.ON :
			new PreconditionException("getState() == CoffeeMachineState.ON");

		this.currentState = CoffeeMachineState.OFF;

	}



}
