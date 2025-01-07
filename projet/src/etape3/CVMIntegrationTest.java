package etape3;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import etape3.equipments.hairdryer.HairDryer;
import etape3.equipments.hairdryer.HairDryerUser;
import etape3.equipments.hairdryer.mil.HairDryerStateModel;
import etape3.equipments.hairdryer.mil.HairDryerUserModel;
import etape3.equipments.heater.Heater;
import etape3.equipments.heater.HeaterController;
import etape3.equipments.heater.HeaterUser;
import etape3.equipments.heater.HeaterController.ControlMode;
import etape3.equipments.heater.mil.HeaterCoupledModel;
import etape3.equipments.heater.mil.HeaterUnitTesterModel;
import etape3.equipments.hem.HEM;
import etape3.equipments.meter.ElectricMeter;
import etape3.equipments.meter.sim.ElectricMeterCoupledModel;
import etape3.equipments.meter.unittests.ElectricMeterUnitTester;
import etape3.utils.ExecutionType;
import etape3.utils.SimulationType;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMIntegrationTest</code> defines an execution CVM script to
 * test the HEM application.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * HEM integration tests can be performed in different execution and simulation
 * types that {@code CVMIntegrationTest} must organise. Basically, the
 * execution must organise the following steps:
 * </p>
 * <ol>
 * <li>Creation and initialisation of the components.</li>
 * <li>Start of the test scenarios.</li>
 * <li>Start of the simulation scenarios after the creation and initialisation
 *   of the local simulation architectures in every component and of the global
 *   component simulation architecture by the supervisor component.</li>
 * <li>When simulating, the recovery of the simulation reports if needed.</li>
 * <li>The cutoff of the CVM, when everything else has finished.</li>
 * </ol>
 * <p>
 * The alignment of the components required to perform the above actions in
 * synchrony follows a time-triggered synchronisation scheme using two means:
 * </p>
 * <ol>
 * <li>The accelerated clock time-triggered synchronisation offered by
 *   BCM4Java.</li>
 * <li>The NeoSim4Java time triggered start of the individual simulators in
 *   real time simulations (MIL real time and SIL).</li>
 * </ol>
 * <p>
 * The points of synchronisation are defined by delays from the start of the
 * application before their occurrence and durations of the activities:
 * </p>
 * <ol>
 * <li>{@code DELAY_TO_START} is the delay given to the creation and
 *   initialisation of the components before starting the execution of the
 *   test scenarios. It is ensured by an accelerated clock shared among
 *   the components. It is expressed in milliseconds.</li>
 * <li>{@code EXECUTION_DURATION} is the duration of the execution of the
 *   test scenarios when no simulation are used. It is also expressed in
 *   milliseconds.</li>
 * <li>{@code DELAY_TO_START_SIMULATION} is the delay after the beginning of
 *   the test scenarios but before the start of the simulators.  It is also
 *   expressed in milliseconds.</li>
 * <li>{@code SIMULATION_DURATION} is the duration of the global simulation
 *   <i>i.e.</i>, for all of the simulators in all of the components. It is
 *   expressed in simulation time unit (here hours).</li>
 * <li>{@code DELAY_TO_STOP} is the delay after the end of the execution
 *   <i>i.e.</i>, after the end of the {@code EXECUTION_DURATION} or
 *   {@code SIMULATION_DURATION}, and before shutting down the components and
 *   the component virtual machine.</li>
 * </ol>
 * <p>
 * In real time simulations, an {@code ACCELERATION_FACTOR} can be applied to
 * the simulation duration, which must also be followed by the test scenarios
 * and the computation of the application execution duration. Both the BCM4Java
 * accelerated clock and the NeoSim4Java real time facilities cope with such an
 * acceleration factor, but it is of course required to have the same factor in
 * both when performing SIL simulations and tests.
 * </p>
 * 
 * <p>Simulation architectures</p>
 * 
 * <p>
 * For the integration tests, there are two component simulation
 * architectures that can be used. The one for MIL and MIL real time simulations
 * is:
 * </p>
 * <p><img src="../../../../../../../../images/hem-2024-e3/.png"/></p>
 * <p>
 * The second component simulation architecture is for SIL simulations:
 * </p>
 * <p><img src="../../../../../../../../images/hem-2024-e3/.png"/></p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVMIntegrationTest
extends		AbstractCVM
{
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

	/** delay to start the real time simulations on every model at the
	 *  same moment (the order is delivered to the models during this
	 *  delay; this delay must be ample enough to give the time to notify
	 *  all models of their start time and to initialise them before starting,
	 *  a value that depends upon the complexity of the simulation architecture
	 *  to be traversed and the component deployment (deployments on several
	 *  JVM and even more several computers require a larger delay.			*/
	public static long				DELAY_TO_START_SIMULATION = 5000L;
	/** start time of the simulation, in simulated logical time, if
	 *  relevant.															*/
	public static double 			SIMULATION_START_TIME = 0.0;
	/** duration  of the simulation, in simulated time.						*/
	public static double			SIMULATION_DURATION = 3.0;
	/** time unit in which {@code SIMULATION_DURATION} is expressed.		*/
	public static TimeUnit			SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static double			ACCELERATION_FACTOR = 180.0;

	/** the type of execution, to select among the values of the
	 *  enumeration {@code ExecutionType}.									*/
	public static ExecutionType		CURRENT_EXECUTION_TYPE =
											//ExecutionType.STANDARD;
											ExecutionType.INTEGRATION_TEST;
	/** the type of execution, to select among the values of the
	 *  enumeration {@code ExecutionType}.									*/
	public static SimulationType	CURRENT_SIMULATION_TYPE =
											//SimulationType.NO_SIMULATION;
											//SimulationType.MIL_SIMULATION;
											//SimulationType.MIL_RT_SIMULATION;
											SimulationType.SIL_SIMULATION;

	/** for unit tests and SIL simulation tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static String			CLOCK_URI = "hem-clock";
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static String			START_INSTANT = "2023-11-22T00:00:00.00Z";

	/** the control mode of the heater controller for the next run.			*/
	public static ControlMode		CONTROL_MODE = ControlMode.PULL;

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
	public				CVMIntegrationTest() throws Exception
	{
		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;
		GlobalSupervisor.VERBOSE = true;
		GlobalSupervisor.X_RELATIVE_POSITION = 1;
		GlobalSupervisor.Y_RELATIVE_POSITION = 0;
		CoordinatorComponent.VERBOSE = true;
		CoordinatorComponent.X_RELATIVE_POSITION = 2;
		CoordinatorComponent.Y_RELATIVE_POSITION = 0;

		HEM.VERBOSE = true;
		HEM.X_RELATIVE_POSITION = 0;
		HEM.Y_RELATIVE_POSITION = 1;
		ElectricMeter.VERBOSE = true;
		ElectricMeter.X_RELATIVE_POSITION = 1;
		ElectricMeter.Y_RELATIVE_POSITION = 1;
		ElectricMeterUnitTester.VERBOSE = true;
		ElectricMeterUnitTester.X_RELATIVE_POSITION = 2;
		ElectricMeterUnitTester.Y_RELATIVE_POSITION = 1;

		HairDryer.VERBOSE = true;
		HairDryer.X_RELATIVE_POSITION = 1;
		HairDryer.Y_RELATIVE_POSITION = 2;
		HairDryerUser.VERBOSE = true;
		HairDryerUser.X_RELATIVE_POSITION = 0;
		HairDryerUser.Y_RELATIVE_POSITION = 2;
		
		Heater.VERBOSE = true;
		Heater.X_RELATIVE_POSITION = 1;
		Heater.Y_RELATIVE_POSITION = 3;
		HeaterUser.VERBOSE = true;
		HeaterUser.X_RELATIVE_POSITION = 0;
		HeaterUser.Y_RELATIVE_POSITION = 3;
		HeaterController.VERBOSE = true;
		HeaterController.X_RELATIVE_POSITION = 2;
		HeaterController.Y_RELATIVE_POSITION = 3;
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
		assert	!CURRENT_EXECUTION_TYPE.isUnitTest() :
				new PreconditionException(
						"!CURRENT_EXECUTION_TYPE.isUnitTest()");

		// Set the main execution run parameters, depending on the type of
		// execution that is required.
		// URI of the simulation architecture for the current run, if relevant.
		String globalArchitectureURI = "";
		// URI of the HairDryer local simulation architecture for the current
		// run, if relevant.
		String hairDryerLocalArchitectureURI = "";
		// URI of the HairDryerUser local simulation architecture for the
		// current run, if relevant.
		String hairDryerUserLocalArchitectureURI = "";
		// URI of the Heater local simulation architecture for the current
		// run, if relevant.
		String heaterLocalArchitectureURI = "";
		// URI of the HeaterUser local simulation architecture for the current
		// run, if relevant.
		String heaterUserLocalArchitectureURI = "";
		// URI of the ElectricMeter local simulation architecture for the
		// current run, if relevant.
		String meterLocalArchitectureURI = "";

		long current = System.currentTimeMillis();
		// start time of the components in Unix epoch time in milliseconds.
		long unixEpochStartTimeInMillis = current + DELAY_TO_START;
		// start instant used for time-triggered synchronisation in unit tests
		// and SIL simulation runs.
		Instant	startInstant = Instant.parse(START_INSTANT);

		// URI of the simulation models and architectures differ among
		// simulation types merely to offer the possibility to the components
		// to create and store all of their possible simulation architectures
		// at the same time, hence avoiding confusion among them.
		switch (CURRENT_SIMULATION_TYPE) {
		case MIL_SIMULATION:
			globalArchitectureURI = GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI;
			hairDryerLocalArchitectureURI = HairDryerStateModel.MIL_URI;
			hairDryerUserLocalArchitectureURI = HairDryerUserModel.MIL_URI;
			heaterLocalArchitectureURI = HeaterCoupledModel.MIL_URI;
			heaterUserLocalArchitectureURI = HeaterUnitTesterModel.MIL_URI;
			meterLocalArchitectureURI = ElectricMeterCoupledModel.MIL_URI;
			break;
		case MIL_RT_SIMULATION:
			globalArchitectureURI = GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI;
			hairDryerLocalArchitectureURI = HairDryerStateModel.MIL_RT_URI;
			hairDryerUserLocalArchitectureURI = HairDryerUserModel.MIL_RT_URI;
			heaterLocalArchitectureURI = HeaterCoupledModel.MIL_RT_URI;
			heaterUserLocalArchitectureURI = HeaterUnitTesterModel.MIL_RT_URI;
			meterLocalArchitectureURI = ElectricMeterCoupledModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			globalArchitectureURI = GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI;
			hairDryerLocalArchitectureURI = HairDryerStateModel.SIL_URI;
			hairDryerUserLocalArchitectureURI = "not-used";
			heaterLocalArchitectureURI = HeaterCoupledModel.SIL_URI;
			heaterUserLocalArchitectureURI = "not-used";
			meterLocalArchitectureURI = ElectricMeterCoupledModel.SIL_URI;
			break;
		case NO_SIMULATION:
		default:
		}
		
		AbstractComponent.createComponent(
				HairDryer.class.getCanonicalName(),
				new Object[]{HairDryer.REFLECTION_INBOUND_PORT_URI,
							 HairDryer.INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 globalArchitectureURI,
							 hairDryerLocalArchitectureURI,
							 SIMULATION_TIME_UNIT,
							 ACCELERATION_FACTOR,
							 CLOCK_URI});
		AbstractComponent.createComponent(
				HairDryerUser.class.getCanonicalName(),
				new Object[]{HairDryerUser.REFLECTION_INBOUND_PORT_URI,
							 HairDryer.INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 globalArchitectureURI,
							 hairDryerUserLocalArchitectureURI,
							 SIMULATION_TIME_UNIT,
							 ACCELERATION_FACTOR,
							 CLOCK_URI});

		AbstractComponent.createComponent(
				Heater.class.getCanonicalName(),
				new Object[]{Heater.REFLECTION_INBOUND_PORT_URI,
							 Heater.USER_INBOUND_PORT_URI,
							 Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
							 Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							 Heater.SENSOR_INBOUND_PORT_URI,
							 Heater.ACTUATOR_INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 globalArchitectureURI,
							 heaterLocalArchitectureURI,
							 SIMULATION_TIME_UNIT,
							 ACCELERATION_FACTOR,
							 CLOCK_URI});
		AbstractComponent.createComponent(
				HeaterController.class.getCanonicalName(),
				new Object[]{Heater.SENSOR_INBOUND_PORT_URI,
							 Heater.ACTUATOR_INBOUND_PORT_URI,
							 HeaterController.STANDARD_HYSTERESIS,
							 HeaterController.STANDARD_CONTROL_PERIOD,
							 CONTROL_MODE,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 CLOCK_URI});
		AbstractComponent.createComponent(
				HeaterUser.class.getCanonicalName(),
				new Object[]{Heater.USER_INBOUND_PORT_URI,
						 	 Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
						 	 Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						 	 CURRENT_EXECUTION_TYPE,
						 	 CURRENT_SIMULATION_TYPE,
						 	 globalArchitectureURI,
						 	 heaterUserLocalArchitectureURI,
						 	 SIMULATION_TIME_UNIT,
						 	 ACCELERATION_FACTOR,
						 	 CLOCK_URI});

		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{ElectricMeter.REFLECTION_INBOUND_PORT_URI,
							 ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 CURRENT_SIMULATION_TYPE,
							 globalArchitectureURI,
							 meterLocalArchitectureURI,
							 SIMULATION_TIME_UNIT,
							 ACCELERATION_FACTOR,
						 	 CLOCK_URI});

		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{CURRENT_SIMULATION_TYPE});

		AbstractComponent.createComponent(
				ClocksServerWithSimulation.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
										 		unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						startInstant,
						ACCELERATION_FACTOR,
						DELAY_TO_START_SIMULATION,
						SIMULATION_START_TIME,
						SIMULATION_DURATION,
						SIMULATION_TIME_UNIT});

		if (CURRENT_SIMULATION_TYPE.isSimulated()) {
			AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[]{});
			AbstractComponent.createComponent(
					GlobalSupervisor.class.getCanonicalName(),
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
	 * @param args	commend-line arguments.
	 */
	public static void	main(String[] args)
	{
		// Force the exceptions in BCM4Java and NeoSim4Java more explicit
		VerboseException.VERBOSE = true;
		VerboseException.PRINT_STACK_TRACE = true;
		NeoSim4JavaException.VERBOSE = true;
		NeoSim4JavaException.PRINT_STACK_TRACE = true;

		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
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
// -----------------------------------------------------------------------------
