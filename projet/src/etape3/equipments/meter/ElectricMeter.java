package etape3.equipments.meter;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterInboundPort;
import fr.sorbonne_u.components.hem2024e1.utils.Measure;
import fr.sorbonne_u.components.hem2024e1.utils.MeasurementUnit;
import fr.sorbonne_u.components.hem2024e1.utils.SensorData;
import etape3.equipments.meter.sim.LocalSimulationArchitectures;
import etape3.utils.ExecutionType;
import etape3.utils.SimulationType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeter</code> implements a simplified electric meter
 * component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric meter gathers the electric power consumption of all appliances
 * and the electric power production of all production units to provide these
 * metrics to the household energy manager.
 * </p>
 * <p>
 * This implementation of the electric meter is complicated by the objective to
 * show the entire spectrum of possible execution and simulation modes.
 * There are three execution types defined by {@code ExecutionType}:
 * </p>
 * <ol>
 * <li>{@code STANDARD} means that the component would execute in normal
 *   operational conditions, on the field (which is not used in this project
 *   but would be in an industrial project).</li>
 * <li>{@code UNIT_TEST} means that the component executes in unit tests where
 *   it is the sole appliance but cooperates with the {@code HairDryerUser}
 *   component.</li>
 * <li>{@code INTEGRATION_TEST} means that the component executes in integration
 *   tests where other appliances coexist and where it must cooperates with the
 *   {@code HairDryerUser} and also the {@code ElectricMeter} components.</li>
 * </ol>
 * <p>
 * There are also four distinct types of simulations defined by
 * {@code SimulationType}:
 * </p>
 * <ol>
 * <li>{@code NO_SIMULATION} means that the component does not execute a
 *   simulator, a type necessarily used in {@code STANDARD} executions but also
 *   for {@code UNIT_TEST} with no simulation.</li>
 * <li>{@code MIL_SIMULATION} means that only MIL simulators will run; it is
 *   meant to be used early stages of a project in {@code UNIT_TEST} and
 *   {@code INTEGRATION_TEST} before implementing the code of the components.
 *   </li>
 * <li>{@code MIL_RT_SIMULATION} is similar to {@code MIL_SIMULATION} but
 *   simulates in real time or an accelerated real time; it is more a step
 *   towards SIL simulations than an actual interesting simulation type
 *   in an industrial project.</li>
 * <li>{@code SIL_SIMULATION} means that the simulators are implemented so
 *   that they can execute with the component software in software-in-the-loop
 *   simulations; it can be used in {@code UNIT_TEST} executions to test
 *   the component software in isolation and then in {@code INTEGRATION_TEST}
 *   executions to test the entire application at once.</li>
 * </ol>
 * <p>
 * In this implementation of the {@code ElectricMeter} component, the standard
 * execution type is not really implemented as the software is not embedded in
 * any real appliances. Unit tests are relatively meaningless for the
 * {@code ElectricMeter} component as it is only useful when appliances and
 * production units are present in the execution. In integration tests with no
 * simulation, the component is totally passive as its methods are called by the
 * {@code HEM} component. In MIL, MIL real time and SIL simulations, the
 * component simply creates and installs its local simulation architecture,
 * which execution will be started by a supervisor component.
 * </p>
 * <p>
 * For SIL simulations in integration tests, the {@code ElectricMeter} component
 * creates the following local component simulation architecture:
 * </p>
 * <p><img src="../../../../../../../images/hem-2024-e3/ElectricMeterIntegrationTestComponentArchitecture.png"/></p>
 * <p>
 * Every electricity model of every appliance and production unit will have to
 * added to the local SIL simulation architecture of the {@code ElectricMeter},
 * which will therefore also receive all of the relevant events coming from the
 * local architectures of the appliances and production unit to propagate them
 * to the appropriate electricity models.
 * </p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code currentExecutionType != null}
 * invariant	{@code currentSimulationType != null}
 * invariant	{@code !currentExecutionType.isStandard() || currentSimulationType.isNoSimulation()}
 * invariant	{@code currentSimulationType.isNoSimulation() || (globalArchitectureURI != null && !globalArchitectureURI.isEmpty())}
 * invariant	{@code currentSimulationType.isNoSimulation() || (localArchitectureURI != null && !localArchitectureURI.isEmpty())}
 * invariant	{@code !currentSimulationType.isSimulated() || simulatedStartTime >= 0.0}
 * invariant	{@code !currentSimulationType.isSimulated() || simulationDuration > 0.0}
 * invariant	{@code !currentSimulationType.isSimulated() || simulationTimeUnit != null}
 * invariant	{@code !currentSimulationType.isRealTimeSimulation() || accFactor > 0.0}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code ELECTRIC_METER_VOLTAGE != null && ELECTRIC_METER_VOLTAGE.getData() > 0.0}
 * invariant	{@code REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code ELECTRIC_METER_INBOUND_PORT_URI != null && !ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2023-09-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={ElectricMeterCI.class})
@RequiredInterfaces(required = {ClocksServerCI.class})
public class			ElectricMeter
extends		AbstractCyPhyComponent
implements	ElectricMeterImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the voltage at which the meter is operating.						*/
	public static final Measure<Double>	ELECTRIC_METER_VOLTAGE =
							new Measure<Double>(220.0, MeasurementUnit.VOLTS);
	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String	REFLECTION_INBOUND_PORT_URI =
													"ELECTRIC-METER-RIP-URI";	
	/** URI of the electric meter inbound port used in tests.				*/
	public static final String	ELECTRIC_METER_INBOUND_PORT_URI =
															"ELECTRIC-METER";
	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = true;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int			X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** inbound port offering the <code>ElectricMeterCI</code> interface.	*/
	protected ElectricMeterInboundPort	emip;

	/** current total electric power consumption measured at the electric
	 *  meter in amperes.												 	*/
	protected AtomicReference<SensorData<Measure<Double>>>
											currentPowerConsumption;
	/** current total electric power production measured at the electric
	 *  meter in watts.													 	*/
	protected AtomicReference<SensorData<Measure<Double>>>
											currentPowerProduction;
	
	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** current type of simulation.											*/
	protected final SimulationType		currentSimulationType;

	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String				clockURI;
	/** accelerated clock, in integration and SIL simulation tests.			*/
	protected AcceleratedClock			clock;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String				globalArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.														*/
	protected final String				localArchitectureURI;
	/** time unit in which times and durations are expressed in the
	 *  simulators.															*/
	protected final TimeUnit			simulationTimeUnit;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double					accFactor;

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
	protected static boolean	glassBoxInvariants(ElectricMeter instance)
	{
		assert 	instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.currentExecutionType != null,
					ElectricMeter.class, instance,
					"currentExecutionType != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.currentSimulationType != null,
					ElectricMeter.class, instance,
					"hcurrentSimulationType != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					!instance.currentExecutionType.isStandard() ||
								instance.currentSimulationType.isNoSimulation(),
					ElectricMeter.class, instance,
					"!currentExecutionType.isStandard() || "
					+ "currentSimulationType.isNoSimulation()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.currentSimulationType.isNoSimulation() ||
						(instance.globalArchitectureURI != null &&
							!instance.globalArchitectureURI.isEmpty()),
					ElectricMeter.class, instance,
					"currentSimulationType.isNoSimulation() || "
					+ "(globalArchitectureURI != null && "
					+ "!globalArchitectureURI.isEmpty())");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.currentSimulationType.isNoSimulation() ||
						(instance.localArchitectureURI != null &&
							!instance.localArchitectureURI.isEmpty()),
					ElectricMeter.class, instance,
					"currentSimulationType.isNoSimulation() || "
					+ "(localArchitectureURI != null && "
					+ "!localArchitectureURI.isEmpty())");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					!instance.currentSimulationType.isSimulated() ||
												instance.simulationTimeUnit != null,
					ElectricMeter.class, instance,
					"!currentSimulationType.isSimulated() || "
					+ "simulationTimeUnit != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					!instance.currentSimulationType.isRealTimeSimulation() ||
														instance.accFactor > 0.0,
					ElectricMeter.class, instance,
					"!hd.currentSimulationType.isRealTimeSimulation() || "
					+ "hd.accFactor > 0.0");
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
	protected static boolean	blackBoxInvariants(ElectricMeter instance)
	{
		assert 	instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
					ELECTRIC_METER_VOLTAGE != null &&
										ELECTRIC_METER_VOLTAGE.getData() > 0.0,
					ElectricMeter.class, instance,
					"ELECTRIC_METER_VOLTAGE != null && "
					+ "ELECTRIC_METER_VOLTAGE.getData() > 0.0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					REFLECTION_INBOUND_PORT_URI != null &&
								!REFLECTION_INBOUND_PORT_URI.isEmpty(),
					ElectricMeter.class, instance,
					"REFLECTION_INBOUND_PORT_URI != null && "
							+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					ELECTRIC_METER_INBOUND_PORT_URI != null &&
									!ELECTRIC_METER_INBOUND_PORT_URI.isEmpty(),
					ElectricMeter.class, instance,
					"ELECTRIC_METER_INBOUND_PORT_URI != null && "
					+ "!ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					X_RELATIVE_POSITION >= 0,
					ElectricMeter.class, instance,
					"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
					Y_RELATIVE_POSITION >= 0,
					ElectricMeter.class, instance,
					"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an electric meter component for standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ELECTRIC_METER_INBOUND_PORT_URI != null && !ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected			ElectricMeter() throws Exception
	{
		this(ELECTRIC_METER_INBOUND_PORT_URI);
	}

	/**
	 * create an electric meter component for standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String electricMeterInboundPortURI
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, electricMeterInboundPortURI,
			 ExecutionType.STANDARD, SimulationType.NO_SIMULATION,
			 null, null, null, 0.0, null);
	}

	/**
	 * create an electric meter component for test executions without simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param currentExecutionType			execution type for the next run.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String electricMeterInboundPortURI,
		ExecutionType currentExecutionType
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, electricMeterInboundPortURI,
			 currentExecutionType, SimulationType.NO_SIMULATION,
			 null, null, null, 0.0, null);

		assert	currentExecutionType.isTest() :
				new PreconditionException("currentExecutionType.isTest()");
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isStandard() || currentSimulationType.isNoSimulation()}
	 * pre	{@code currentSimulationType.isNoSimulation() || (globalArchitectureURI != null && !globalArchitectureURI.isEmpty())}
	 * pre	{@code currentSimulationType.isNoSimulation() || (localArchitectureURI != null && !localArchitectureURI.isEmpty())}
	 * pre	{@code !currentSimulationType.isSimulated() || simulationTimeUnit != null}
	 * pre	{@code !currentSimulationType.isRealTimeSimulation() || accFactor > 0.0}
	 * pre	{@code !currentExecutionType.isUnitTest() || currentSimulationType.isNoSimulation()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the reflection innbound port of the component.
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param currentExecutionType			current execution type for the next run.
	 * @param currentSimulationType			simulation type for the next run.
	 * @param globalArchitectureURI			URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localArchitectureURI			URI of the local simulator to be used in the simulation architecture.
	 * @param simulationTimeUnit			time unit in which {@code simulatedStartTime} and {@code simulationDuration} are expressed.
	 * @param accFactor						acceleration factor for the simulation.
	 * @param clockURI						URI of the clock to be used to synchronise the test scenarios and the simulation.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String reflectionInboundPortURI,
		String electricMeterInboundPortURI,
		ExecutionType currentExecutionType,
		SimulationType currentSimulationType,
		String globalArchitectureURI,
		String localArchitectureURI,
		TimeUnit simulationTimeUnit,
		double accFactor,
		String clockURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0);

		assert	electricMeterInboundPortURI != null &&
										!electricMeterInboundPortURI.isEmpty() :
				new PreconditionException(
						"electricMeterInboundPortURI != null && "
						+ "!electricMeterInboundPortURI.isEmpty()");
		assert	currentExecutionType != null :
				new PreconditionException("currentExecutionType != null");
		assert	currentSimulationType.isNoSimulation() ||
							(globalArchitectureURI != null &&
										!globalArchitectureURI.isEmpty()) :
				new PreconditionException(
						"currentSimulationType.isNoSimulation() ||  "
						+ "(globalArchitectureURI != null && "
						+ "!globalArchitectureURI.isEmpty())");
		assert	currentSimulationType.isNoSimulation() ||
							(localArchitectureURI != null &&
											!localArchitectureURI.isEmpty()) :
				new PreconditionException(
						"currentSimulationType.isNoSimulation() ||  "
						+ "(localArchitectureURI != null && "
						+ "!localArchitectureURI.isEmpty())");
		assert	!currentSimulationType.isSimulated() ||
													simulationTimeUnit != null :
				new PreconditionException(
						"!currentSimulationType.isSimulated() || "
						+ "simulationTimeUnit != null");
		assert	!currentSimulationType.isRealTimeSimulation() || accFactor > 0.0 :
				new PreconditionException(
						"!currentSimulationType.isRealTimeSimulation() || "
						+ "accFactor > 0.0");
		// doing simulations for unit test makes no sense as the meter must
		// gather the power consumption of other appliances and the power
		// production of production units
		assert	!currentExecutionType.isUnitTest() ||
									currentSimulationType.isNoSimulation() :
				new PreconditionException(
						"!currentExecutionType.isUnitTest() || "
						+ "currentSimulationType.isNoSimulation()");

		this.currentExecutionType = currentExecutionType;
		this.currentSimulationType = currentSimulationType;
		this.globalArchitectureURI = globalArchitectureURI;
		this.localArchitectureURI = localArchitectureURI;
		this.simulationTimeUnit = simulationTimeUnit;
		this.clockURI = clockURI;
		this.accFactor = accFactor;

		this.initialise(electricMeterInboundPortURI);

		assert	ElectricMeter.glassBoxInvariants(this) :
				new ImplementationInvariantException(
						"ElectricMeter.glassBoxInvariants(this)");
		assert	ElectricMeter.blackBoxInvariants(this) :
				new InvariantException("ElectricMeter.blackBoxInvariants(this)");
	}

	/**
	 * initialise an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @throws Exception					<i>to do</i>.
	 */
	protected void		initialise(String electricMeterInboundPortURI)
	throws Exception
	{
		assert	electricMeterInboundPortURI != null &&
										!electricMeterInboundPortURI.isEmpty() :
				new PreconditionException(
						"electricMeterInboundPortURI != null && "
						+ "!electricMeterInboundPortURI.isEmpty()");

		this.emip =
				new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
		this.emip.publishPort();

		this.currentPowerConsumption =
			new AtomicReference<>(
					new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES)));
		this.currentPowerProduction =
			new AtomicReference<>(
					new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES)));

		switch (this.currentSimulationType) {
		case MIL_SIMULATION:
			Architecture architecture =
					LocalSimulationArchitectures.
							createElectricMeterMILArchitecture4IntegrationTests(
									this.globalArchitectureURI,
									this.simulationTimeUnit);
			assert	architecture.getRootModelURI().equals(this.localArchitectureURI) :
					new AssertionError(
							"local simulator " + this.localArchitectureURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.global2localSimulationArchitectureURIS.
						put(this.globalArchitectureURI, this.localArchitectureURI);
			break;
		case MIL_RT_SIMULATION:
			architecture =
					LocalSimulationArchitectures.
						createElectricMeter_RT_Architecture4IntegrationTests(
									SimulationType.MIL_RT_SIMULATION,
									this.globalArchitectureURI,
									this.simulationTimeUnit,
									this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localArchitectureURI) :
					new AssertionError(
							"local simulator " + this.localArchitectureURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.global2localSimulationArchitectureURIS.
						put(this.globalArchitectureURI, this.localArchitectureURI);
			break;
		case SIL_SIMULATION:
			architecture =
					LocalSimulationArchitectures.
						createElectricMeter_RT_Architecture4IntegrationTests(
									SimulationType.SIL_SIMULATION,
									this.globalArchitectureURI,
									this.simulationTimeUnit,
									this.accFactor);
			assert	architecture.getRootModelURI().equals(
													this.localArchitectureURI) :
					new AssertionError(
							"local simulator " + this.localArchitectureURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.global2localSimulationArchitectureURIS.
					put(this.globalArchitectureURI, this.localArchitectureURI);
			break;
		case NO_SIMULATION:
		default:
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * set the current power consumption, a method that is meant to be called
	 * only by the simulator in SIL runs, otherwise a hardware sensor would be
	 * used in standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code power != null}
	 * pre	{@code power.getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param power	electric power consumption in amperes.
	 */
	public void			setCurrentPowerConsumption(Measure<Double> power)
	{
		
		assert	power != null : new PreconditionException("power != null");
		assert	power.getMeasurementUnit().equals(MeasurementUnit.AMPERES) :
				new PreconditionException(
						"power.getMeasurementUnit().equals("
						+ "MeasurementUnit.AMPERES)");

		double old = 0.0;
		double i;
		if (VERBOSE) {
			old = ((Measure<Double>)this.currentPowerConsumption.get().
														getMeasure()).getData();
		}

		SensorData<Measure<Double>> sd = null;
		if (this.currentSimulationType.isSILSimulation()) {
			// in SIL simulation, an accelerated clock is used as time reference
			// for measurements and sensor data time stamps
			sd = new SensorData<>(this.clock, 
								  new Measure<Double>(
										  this.clock,
										  power.getData(),
										  power.getMeasurementUnit()));
		} else {
			sd = new SensorData<>(power);
		}
		this.currentPowerConsumption.set(sd);

		if (VERBOSE) {
			i = power.getData();
			if (Math.abs(old - i) > 0.000001) {
				this.traceMessage(
					"Electric meter sets its current consumption at "
					+ this.currentPowerConsumption.get().getMeasure() + ".\n");
			}
		}
	}

	/**
	 * set the current power consumption, a method that is meant to be called
	 * only by the simulator in SIL runs, otherwise a hardware sensor would be
	 * used in standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code power != null}
	 * pre	{@code power.getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param power	electric power production in amperes.
	 */
	public void			setCurrentPowerProduction(Measure<Double> power)
	{
		assert	power != null : new PreconditionException("power != null");
		assert	power.getMeasurementUnit().equals(MeasurementUnit.AMPERES) :
				new PreconditionException(
						"power.getMeasurementUnit().equals("
						+ "MeasurementUnit.AMPERES)");

		SensorData<Measure<Double>> sd = null;
		if (this.currentSimulationType.isSILSimulation()) {
			// in SIL simulation, an accelerated clock is used as time reference
			// for measurements and sensor data time stamps
			sd = new SensorData<>(this.clock,
								  new Measure<Double>(
										  this.clock,
										  power.getData(),
										  power.getMeasurementUnit()));
		} else {
			sd = new SensorData<>(power);
		}
		this.currentPowerProduction.set(sd);

		if (VERBOSE) {
			this.traceMessage(
					"Electric meter sets its current production at "
					+ this.currentPowerProduction.get().getMeasure() + ".\n");
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

		try {
			switch (this.currentSimulationType) {
			case MIL_SIMULATION:
				AtomicSimulatorPlugin asp = new AtomicSimulatorPlugin();
				String uri = this.global2localSimulationArchitectureURIS.
												get(this.globalArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulationArchitectures.get(uri);
				asp.setPluginURI(uri);
				asp.setSimulationArchitecture(architecture);
				this.installPlugin(asp);
				break;
			case MIL_RT_SIMULATION:
				RTAtomicSimulatorPlugin rtasp = new RTAtomicSimulatorPlugin();
				uri = this.global2localSimulationArchitectureURIS.
												get(this.globalArchitectureURI);
				architecture =
					(Architecture) this.localSimulationArchitectures.get(uri);
				rtasp.setPluginURI(architecture.getRootModelURI());
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
				break;
			case SIL_SIMULATION:
				rtasp = new RTAtomicSimulatorPlugin();
				uri = this.global2localSimulationArchitectureURIS.
												get(this.globalArchitectureURI);
				architecture =
					(Architecture) this.localSimulationArchitectures.get(uri);
				rtasp.setPluginURI(uri);
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
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
		// In the electric meter, the accelerated clock is only used to get
		// properly time stamped sensor data in simulated time instants.
		this.clock = null;
		if (this.currentExecutionType.isIntegrationTest() ||
								this.currentSimulationType.isSILSimulation()) {
			ClocksServerOutboundPort clocksServerOutboundPort =
											new ClocksServerOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("ElectricMeter gets the clock.");
			this.clock =
				clocksServerOutboundPort.getClock(this.clockURI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			this.logMessage("ElectricMeter got the clock "
											+ this.clock.getClockURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.emip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterImplementationI#getCurrentConsumption()
	 */
	@Override
	public SensorData<Measure<Double>>	getCurrentConsumption() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage(
					"Electric meter returns its current consumption.\n");
		}

		SensorData<Measure<Double>> ret = null;
		if (this.currentSimulationType.isSILSimulation()) {
			ret = this.currentPowerConsumption.get();
		} else {
			if (this.clock != null) {
				ret = new SensorData<>(
						this.clock,
						new Measure<Double>(this.clock,
											0.0,
											MeasurementUnit.AMPERES));
			} else {
				ret = new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES));
			}
		}

		assert	ret != null : new PostconditionException("return != null");
		assert	ret.getMeasure().isScalar() :
				new PostconditionException("return.getMeasure().isScalar()");
		assert	((Measure<?>)ret.getMeasure()).getData() instanceof Double :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getData() "
						+ "instanceof Double");
		assert	((Measure<Double>)ret.getMeasure()).getData() >= 0.0 :
				new PostconditionException(
						"((Measure<Double>)return.getMeasure())."
						+ "getData() >= 0.0");
		assert	((Measure<?>)ret.getMeasure()).getMeasurementUnit().
											equals(MeasurementUnit.AMPERES) :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getMeasurementUnit()."
						+ "equals(MeasurementUnit.AMPERES)");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterImplementationI#getCurrentProduction()
	 */
	@Override
	public SensorData<Measure<Double>>	getCurrentProduction() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Electric meter returns its current production.\n");
		}

		SensorData<Measure<Double>> ret = null;
		if (this.currentSimulationType.isSILSimulation()) {
			ret = this.currentPowerProduction.get();
		} else {
			if (this.clock != null) {
				ret = new SensorData<>(
						this.clock,
						new Measure<Double>(this.clock,
											0.0,
											MeasurementUnit.AMPERES));
			} else {
				ret = new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES));
			}
		}

		assert	ret != null : new PostconditionException("return != null");
		assert	ret.getMeasure().isScalar() :
				new PostconditionException("return.getMeasure().isScalar()");
		assert	((Measure<?>)ret.getMeasure()).getData() instanceof Double :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getData() "
						+ "instanceof Double");
		assert	((Measure<Double>)ret.getMeasure()).getData() >= 0.0 :
				new PostconditionException(
						"((Measure<Double>)return.getMeasure())."
						+ "getData() >= 0.0");
		assert	((Measure<?>)ret.getMeasure()).getMeasurementUnit().
											equals(MeasurementUnit.AMPERES) :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getMeasurementUnit()."
						+ "equals(MeasurementUnit.AMPERES)");

		return ret;
	}
}
// -----------------------------------------------------------------------------
