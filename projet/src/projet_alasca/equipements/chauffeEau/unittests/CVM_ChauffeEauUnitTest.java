package projet_alasca.equipements.chauffeEau.unittests;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.equipements.chauffeEau.ChauffeEau;
import projet_alasca.equipements.chauffeEau.ChauffeEauController;
import projet_alasca.equipements.chauffeEau.ChauffeEauController.ControlMode;
import projet_alasca.equipements.chauffeEau.ChauffeEauUser;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauCoupledModel;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauUnitTesterModel;
import projet_alasca.etape3.CoordinatorComponent;
import projet_alasca.etape3.utils.ExecutionType;
import projet_alasca.etape3.utils.SimulationType;

public class CVM_ChauffeEauUnitTest extends AbstractCVM {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators; this delay is
	 *  estimated given the complexity of the initialisation but could also
	 *  need to be revised if the computer on which the application is run
	 *  is less powerful.													*/
	public static long				DELAY_TO_START = 3000L;
	/** duration of the execution when simulation is *not* used.			*/
	public static long				EXECUTION_DURATION = 5000L;
	/** delay before the CVM must stop the execution after the execution
	 *  of the tests scenarios and, possibly, the attached simulations.		*/
	public static long				DELAY_TO_STOP = 2000L;
	/** duration of the sleep at the end of the execution before exiting
	 *  the JVM.															*/
	public static long				END_SLEEP_DURATION = 10000L;

	/** delay to start the real time simulations on every atomic model at the
	 *  same moment (the order is delivered to the models during this delay;
	 *  this delay must be ample enough to give the time to notify all models
	 *  of their start time and to initialise them before starting, a value
	 *  that depends upon the complexity of the simulation architecture to be
	 *  traversed, the power of the computer on which it is run and the
	 *  type of component deployment (deployments on several JVM and even more
	 *  several computers require a larger delay.							*/
	public static long				DELAY_TO_START_SIMULATION = 3000L;
	/** start time of the simulation, in simulated logical time, if
	 *  relevant.															*/
	public static double 			SIMULATION_START_TIME = 0.0;
	/** duration of the simulation, in simulated time.						*/
	public static double			SIMULATION_DURATION = 3.0;
	/** time unit in which {@code SIMULATION_DURATION} is expressed.		*/
	public static TimeUnit			SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static double			ACCELERATION_FACTOR = 360.0;

	/** the type of execution, to select among the values of the
	 *  enumeration {@code ExecutionType}.									*/
	public static ExecutionType		CURRENT_EXECUTION_TYPE =
											//ExecutionType.STANDARD;
											ExecutionType.UNIT_TEST;
	/** the type of execution, to select among the values of the
	 *  enumeration {@code ExecutionType}.									*/
	public static SimulationType	CURRENT_SIMULATION_TYPE =
											//SimulationType.NO_SIMULATION;
											//SimulationType.MIL_SIMULATION;
											//SimulationType.MIL_RT_SIMULATION;
											SimulationType.SIL_SIMULATION;
	/** the control mode of the ChauffeEau controller for the next run.			*/
	public static ControlMode		CONTROL_MODE = ControlMode.PUSH;

	/** for unit tests and SIL simulation tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static String			CLOCK_URI = "hem-clock";
	/** start instant in test scenarios.									*/
	public static String			START_INSTANT = "2024-10-18T00:00:00.00Z";

	private boolean unittest;
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of CVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public				CVM_ChauffeEauUnitTest() throws Exception
	{
		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;
		ChauffeEauUnitTestsSupervisor.VERBOSE = true;
		ChauffeEauUnitTestsSupervisor.X_RELATIVE_POSITION = 1;
		ChauffeEauUnitTestsSupervisor.Y_RELATIVE_POSITION = 0;
		CoordinatorComponent.VERBOSE = true;
		CoordinatorComponent.X_RELATIVE_POSITION = 2;
		CoordinatorComponent.Y_RELATIVE_POSITION = 0;

		ChauffeEau.VERBOSE = true;
		ChauffeEau.X_RELATIVE_POSITION = 1;
		ChauffeEau.Y_RELATIVE_POSITION = 1;
		ChauffeEauController.VERBOSE = true;
		ChauffeEauController.X_RELATIVE_POSITION = 2;
		ChauffeEauController.Y_RELATIVE_POSITION = 1;
		ChauffeEauUser.VERBOSE = true;
		ChauffeEauUser.X_RELATIVE_POSITION = 0;
		ChauffeEauUser.Y_RELATIVE_POSITION = 1;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		assert	!CURRENT_EXECUTION_TYPE.isIntegrationTest() :
				new RuntimeException(
						"!CURRENT_EXECUTION_TYPE.isIntegrationTest()");
		assert	!CURRENT_EXECUTION_TYPE.isStandard() ||
									CURRENT_SIMULATION_TYPE.isNoSimulation() :
				new RuntimeException(
						"!CURRENT_EXECUTION_TYPE.isStandard() || "
						+ "CURRENT_SIMULATION_TYPE.isNoSimulation()");

		// Set the main execution run parameters, depending on the type of
		// execution that is required.

		// URI of the global simulation architecture for the current run,
		// if relevant.
		String globalArchitectureURI = "";
		// URI of the HairDryer local simulation architecture for the current
		// run, if relevant.
		String ChauffeEauLocalArchitectureURI = "";
		// URI of the HairDryerUser local simulation architecture for the
		// current run, if relevant.
		String ChauffeEauUserLocalArchitectureURI = "";

		long current = System.currentTimeMillis();
		// start time of the components in Unix epoch time in milliseconds.
		long unixEpochStartTimeInMillis = current + DELAY_TO_START;

		// URI of the simulation models and architectures differ among
		// simulation types merely to offer the possibility to the components
		// to create and store all of their possible simulation architectures
		// at the same time, hence avoiding confusion among them.
		switch (CURRENT_SIMULATION_TYPE) {
		case MIL_SIMULATION:
			globalArchitectureURI = ChauffeEauUnitTestsSupervisor.MIL_ARCHITECTURE_URI;
			ChauffeEauLocalArchitectureURI = ChauffeEauCoupledModel.MIL_URI;
			ChauffeEauUserLocalArchitectureURI = ChauffeEauUnitTesterModel.MIL_URI;
			break;
		case MIL_RT_SIMULATION:
			globalArchitectureURI = ChauffeEauUnitTestsSupervisor.MIL_RT_ARCHITECTURE_URI;
			ChauffeEauLocalArchitectureURI = ChauffeEauCoupledModel.MIL_RT_URI;
			ChauffeEauUserLocalArchitectureURI = ChauffeEauUnitTesterModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			globalArchitectureURI = ChauffeEauUnitTestsSupervisor.SIL_ARCHITECTURE_URI;
			ChauffeEauLocalArchitectureURI =  ChauffeEauCoupledModel.SIL_URI;
			ChauffeEauUserLocalArchitectureURI = "not-used";
			break;
		case NO_SIMULATION:
		default:
		}

		// Deploy the components

		AbstractComponent.createComponent(
				ChauffeEau.class.getCanonicalName(),
				new Object[]{ChauffeEau.REFLECTION_INBOUND_PORT_URI,
							 ChauffeEau.USER_INBOUND_PORT_URI,
							 ChauffeEau.INTERNAL_CONTROL_INBOUND_PORT_URI,
							 ChauffeEau.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							 ChauffeEau.SENSOR_INBOUND_PORT_URI,
							 ChauffeEau.ACTUATOR_INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 globalArchitectureURI,
							 ChauffeEauLocalArchitectureURI,
							 SIMULATION_TIME_UNIT,
							 ACCELERATION_FACTOR,
							 CLOCK_URI,
							 true});
		AbstractComponent.createComponent(
				ChauffeEauController.class.getCanonicalName(),
				new Object[]{ChauffeEau.SENSOR_INBOUND_PORT_URI,
							 ChauffeEau.ACTUATOR_INBOUND_PORT_URI,
							 ChauffeEauController.STANDARD_HYSTERESIS,
							 ChauffeEauController.STANDARD_CONTROL_PERIOD,
							 CONTROL_MODE,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 CLOCK_URI});
		AbstractComponent.createComponent(
				ChauffeEauUser.class.getCanonicalName(),
				new Object[]{ChauffeEau.USER_INBOUND_PORT_URI,
							 ChauffeEau.INTERNAL_CONTROL_INBOUND_PORT_URI,
							 ChauffeEau.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 globalArchitectureURI,
							 ChauffeEauUserLocalArchitectureURI,
							 SIMULATION_TIME_UNIT,
							 ACCELERATION_FACTOR,
							 CLOCK_URI});

		AbstractComponent.createComponent(
				ClocksServerWithSimulation.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
										 		unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						Instant.parse(START_INSTANT),
						ACCELERATION_FACTOR,
						DELAY_TO_START_SIMULATION,
						SIMULATION_START_TIME,
						SIMULATION_DURATION,
						SIMULATION_TIME_UNIT});

		if (CURRENT_SIMULATION_TYPE.isMilSimulation() ||
								CURRENT_SIMULATION_TYPE.isMILRTSimulation()) {
			// A supervisor component and a coordinator component are only
			// needed for MIL and MIL real time simulation because for the
			// hair dryer unit tests with SIL simulation executes only one
			// component simulator in the component HairDryer, which itself
			// starts the simulation without the need for supervision and
			// coordination.
			AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[]{});
			AbstractComponent.createComponent(
					ChauffeEauUnitTestsSupervisor.class.getCanonicalName(),
					new Object[]{CURRENT_SIMULATION_TYPE,
								 globalArchitectureURI});
		}

		super.deploy();
	}

	/**
	 * start the execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param args	command-line arguments.
	 */
	public static void	main(String[] args)
	{
		// Force the exceptions in BCM4Java and NeoSim4Java more explicit
		VerboseException.VERBOSE = true;
		VerboseException.PRINT_STACK_TRACE = true;
		NeoSim4JavaException.VERBOSE = true;
		NeoSim4JavaException.PRINT_STACK_TRACE = true;

		try {
			CVM_ChauffeEauUnitTest cvm = new CVM_ChauffeEauUnitTest();
			// compute the execution duration in milliseconds from the
			// simulation duration in hours and the acceleration factor
			// i.e., the simulation duration times 3600 seconds per hour
			// times 1000 milliseconds per second divided by the acceleration
			// factor
			long executionDurationInMillis = 0L;
			switch (CURRENT_SIMULATION_TYPE) {
			case MIL_SIMULATION:
				// In MIL simulations, simulations runs quickly enough that it
				// is sufficient to use EXECUTION_DURATION as the duration of
				// the application execution
				executionDurationInMillis =
					DELAY_TO_START + DELAY_TO_START_SIMULATION 
										+ EXECUTION_DURATION + DELAY_TO_STOP;
				break;
			case MIL_RT_SIMULATION:
			case SIL_SIMULATION:
				// IN MIL real time and SIL simulations, the duration of the
				// application execution must be the same as the duration of the
				// execution of the simulation, which is computed as the
				// simulation duration expressed in the simulation time unit
				// to which is applied the acceleration factor and then converted
				// to a time in milliseconds; for example a simulation duration
				// of one hour (simulation time unit) with acceleration factor
				// 60 will take one minute execution time converted to 60000
				// milliseconds.
				executionDurationInMillis =
					DELAY_TO_START + DELAY_TO_START_SIMULATION
						+ ((long)(((double)SIMULATION_TIME_UNIT.toMillis(1))
								* (SIMULATION_DURATION/ACCELERATION_FACTOR)))
						+ DELAY_TO_STOP;
				break;
			case NO_SIMULATION:
				executionDurationInMillis =
					DELAY_TO_START + EXECUTION_DURATION + DELAY_TO_STOP;
				break;
			default:
			}
			System.out.println("starting for " + executionDurationInMillis);
			cvm.startStandardLifeCycle(executionDurationInMillis);
			// delay to look at the results before closing the trace windows
			Thread.sleep(END_SLEEP_DURATION);
			// force the exit
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
