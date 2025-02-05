package projet_alasca.equipements.chauffeEau;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
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
import fr.sorbonne_u.components.hem2024e1.utils.Measure;
import fr.sorbonne_u.components.hem2024e1.utils.MeasurementUnit;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauActuatorInboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauExternalControlInboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauInternalControlInboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauSensorDataInboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauUserInboundPort;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauCompoundMeasure;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauStateMeasure;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauStateModel;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauTemperatureModel;
import projet_alasca.equipements.chauffeEau.mil.LocalSimulationArchitectures;
import projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat;
import projet_alasca.equipements.chauffeEau.mil.events.Heat;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau.PowerValue;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOffChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOnChauffeEau;
import projet_alasca.equipements.gestionEnergie.GestionEnergie;
import projet_alasca.equipements.gestionEnergie.RegistrationConnector;
import projet_alasca.equipements.gestionEnergie.RegistrationOutboundPort;
import projet_alasca.etape3.equipments.hem.HEM;
import projet_alasca.etape3.utils.ExecutionType;
import projet_alasca.etape3.utils.SimulationType;

import projet_alasca.equipements.chauffeEau.measures.ChauffeEauSensorData;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauStateMeasure;

@OfferedInterfaces(offered={ChauffeEauUserCI.class,
		ChauffeEauInternalControlCI.class,
		ChauffeEauExternalControlCI.class,
		ChauffeEauSensorDataCI.ChauffeEauSensorOfferedPullCI.class,
		ChauffeEauActuatorCI.class})
@RequiredInterfaces(required={DataOfferedCI.PushCI.class,
		  ClocksServerWithSimulationCI.class})
public class ChauffeEau 
extends AbstractCyPhyComponent 
implements ChauffeEauUserI, 
			ChauffeEauInternalControlI 
			
{

	public static enum	ChauffeEauState
	{
		/** ChauffeEau is on.													*/
		ON,
		/** ChauffeEau is heating.												*/
		HEATING,
		/** ChauffeEau is off.													*/
		OFF
	}

	
	public static enum		ChauffeEauSensorMeasures
	{
		/** heating status: a boolean sensor where true means currently
		 *  heating, false currently not heating.							*/
		HEATING_STATUS,
		/** the current target temperature.									*/
		TARGET_TEMPERATURE,
		/** the current room temperature.									*/
		CURRENT_TEMPERATURE,
		/** both the target and the current temperatures.					*/
		COMPOUND_TEMPERATURES
	}
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the ChauffeEau inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
															"ChauffeEau-RIP-URI";	
	/** max power level of the ChauffeEau, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;
	/** standard target temperature for the ChauffeEau.							*/
	protected static final double	STANDARD_TARGET_TEMPERATURE = 19.0;
	
	public static final MeasurementUnit	TEMPERATURE_UNIT =
			MeasurementUnit.CELSIUS;


	/** URI of the ChauffeEau port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"ChauffeEau-USER-INBOUND-PORT-URI";
	/** URI of the ChauffeEau port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"ChauffeEau-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the ChauffeEau port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"ChauffeEau-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	
	public static final String		SENSOR_INBOUND_PORT_URI =
			"ChauffeEau-SENSOR-INBOUND-PORT-URI";
public static final String		ACTUATOR_INBOUND_PORT_URI =
			"ChauffeEau-ACTUATOR-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static boolean			VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int				X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int				Y_RELATIVE_POSITION = 0;

	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 10.0;

	/** current state (on, off) of the ChauffeEau.								*/
	protected ChauffeEauState						currentState;
	/**	current power level of the ChauffeEau.									*/
	protected double							currentPowerLevel;
	/** target temperature for the heating.									*/
	protected double							targetTemperature;

	/** inbound port offering the <code>ChauffeEauUserCI</code> interface.		*/
	protected ChauffeEauUserInboundPort				hip;
	/** inbound port offering the <code>ChauffeEauInternalControlCI</code>
	 *  interface.															*/
	protected ChauffeEauInternalControlInboundPort	hicip;
	/** inbound port offering the <code>ChauffeEauExternalControlCI</code>
	 *  interface.															*/
	protected ChauffeEauExternalControlInboundPort	hecip;

	protected RegistrationOutboundPort rop;
	
	protected static final String registrationOutboundPortURI = "registrationOutboundPortURI";
	
	protected final String chauffeEauID = "chauffeEauID";
	
	
	// Sensors/actuators

		/** the inbound port through which the sensors are called.				*/
		protected ChauffeEauSensorDataInboundPort	sensorInboundPort;
		/** the inbound port through which the actuators are called.			*/
		protected ChauffeEauActuatorInboundPort		actuatorInboundPort;

		// Execution/Simulation

		/** URI of the clock to be used to synchronise the test scenarios and
		 *  the simulation.														*/
		protected final String				clockURI;
		/** accelerated clock governing the timing of actions in the test
		 *  scenarios; here the clock is needed to compute the real time
		 *  control period taking into account the acceleration factor
		 *  for the current execution.											*/
		protected final CompletableFuture<AcceleratedClock>	clock;

		/** current type of execution.											*/
		protected final ExecutionType		currentExecutionType;
		/** current type of simulation.											*/
		protected final SimulationType		currentSimulationType;

		/** plug-in holding the local simulation architecture and simulators.	*/
		protected AtomicSimulatorPlugin		asp;
		/** URI of the global simulation architecture to be created or the empty
		 *  string if the component does not execute as a simulation.			*/
		protected final String				globalArchitectureURI;
		/** URI of the local simulation architecture used to compose the global
		 *  simulation architecture.											*/
		protected final String				localArchitectureURI;
		/** time unit in which {@code simulatedStartTime} and
		 *  {@code simulationDuration} are expressed.							*/
		protected final TimeUnit			simulationTimeUnit;
		/** acceleration factor to be used when running the real time
		 *  simulation.															*/
		protected double					accFactor;

		protected static final String		CURRENT_TEMPERATURE_NAME =
															"currentTemperature";
		private boolean unittest;
		
		// -------------------------------------------------------------------------
		// Invariants
		// -------------------------------------------------------------------------

		/**
		 * return true if the glass-box invariants are observed, false otherwise.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code h != null}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param h	instance to be tested.
		 * @return	true if the glass-box invariants are observed, false otherwise.
		 */
		protected static boolean	glassBoxInvariants(ChauffeEau h)
		{
			assert	h != null : new PreconditionException("h != null");

			boolean ret = true;
			ret &= InvariantChecking.checkGlassBoxInvariant(
						MAX_POWER_LEVEL > 0.0,
						ChauffeEau.class, h,
						"MAX_POWER_LEVEL > 0.0");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.currentState != null,
						ChauffeEau.class, h,
						"h.currentState != null");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.targetTemperature >= -50.0 && h.targetTemperature <= 50.0,
						ChauffeEau.class, h,
						"h.targetTemperature >= -50.0 && h.targetTemperature <= 50.0");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.currentPowerLevel >= 0.0 &&
										h.currentPowerLevel <= MAX_POWER_LEVEL,
						ChauffeEau.class, h,
						"h.currentPowerLevel >= 0.0 && "
									+ "h.currentPowerLevel <= MAX_POWER_LEVEL");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.currentExecutionType != null,
						ChauffeEau.class, h,
						"currentExecutionType != null");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.currentSimulationType != null,
						ChauffeEau.class, h,
						"hcurrentSimulationType != null");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						!h.currentExecutionType.isStandard() ||
								h.currentSimulationType.isNoSimulation(),
						ChauffeEau.class, h,
						"!currentExecutionType.isStandard() || "
						+ "currentSimulationType.isNoSimulation()");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.currentSimulationType.isNoSimulation() ||
							(h.globalArchitectureURI != null &&
								!h.globalArchitectureURI.isEmpty()),
						ChauffeEau.class, h,
						"currentSimulationType.isNoSimulation() || "
						+ "(globalArchitectureURI != null && "
						+ "!globalArchitectureURI.isEmpty())");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						h.currentSimulationType.isNoSimulation() ||
							(h.localArchitectureURI != null &&
								!h.localArchitectureURI.isEmpty()),
						ChauffeEau.class, h,
						"currentSimulationType.isNoSimulation() || "
						+ "(localArchitectureURI != null && "
						+ "!localArchitectureURI.isEmpty())");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						!h.currentSimulationType.isSimulated() ||
												h.simulationTimeUnit != null,
						ChauffeEau.class, h,
						"!currentSimulationType.isSimulated() || "
						+ "simulationTimeUnit != null");
			ret &= InvariantChecking.checkGlassBoxInvariant(
						!h.currentSimulationType.isRealTimeSimulation() ||
														h.accFactor > 0.0,
						ChauffeEau.class, h,
						"!currentSimulationType.isRealTimeSimulation() || "
						+ "accFactor > 0.0");
			return ret;
		}

		/**
		 * return true if the black-box invariants are observed, false otherwise.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code h != null}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param h	instance to be tested.
		 * @return	true if the black-box invariants are observed, false otherwise.
		 */
		protected static boolean	blackBoxInvariants(ChauffeEau h)
		{
			assert	h != null : new PreconditionException("h != null");

			boolean ret = true;
			ret &= InvariantChecking.checkBlackBoxInvariant(
						REFLECTION_INBOUND_PORT_URI != null &&
										!REFLECTION_INBOUND_PORT_URI.isEmpty(),
						ChauffeEau.class, h,
						"REFLECTION_INBOUND_PORT_URI != null && "
									+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						TEMPERATURE_UNIT != null,
						ChauffeEau.class, h,
						"TEMPERATURE_UNIT != null");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						USER_INBOUND_PORT_URI != null &&
												!USER_INBOUND_PORT_URI.isEmpty(),
						ChauffeEau.class, h,
						"USER_INBOUND_PORT_URI != null && "
						+ "!USER_INBOUND_PORT_URI.isEmpty()");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
									!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
						ChauffeEau.class, h,
						"INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
								+ "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
									!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
						ChauffeEau.class, h,
						"EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&"
								+ "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						SENSOR_INBOUND_PORT_URI != null &&
											!SENSOR_INBOUND_PORT_URI.isEmpty(),
						ChauffeEau.class, h,
						"SENSOR_INBOUND_PORT_URI != null && "
						+ "!SENSOR_INBOUND_PORT_URI.isEmpty()");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						ACTUATOR_INBOUND_PORT_URI != null &&
											!ACTUATOR_INBOUND_PORT_URI.isEmpty(),
						ChauffeEau.class, h,
						"ACTUATOR_INBOUND_PORT_URI != null && "
						+ "!ACTUATOR_INBOUND_PORT_URI.isEmpty()");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						X_RELATIVE_POSITION >= 0,
						ChauffeEau.class, h,
						"X_RELATIVE_POSITION >= 0");
			ret &= InvariantChecking.checkBlackBoxInvariant(
						Y_RELATIVE_POSITION >= 0,
						ChauffeEau.class, h,
						"Y_RELATIVE_POSITION >= 0");
			return ret;
		}
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected			ChauffeEau() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI,SENSOR_INBOUND_PORT_URI,
			 ACTUATOR_INBOUND_PORT_URI);
	}

	/**
	 * create a new ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ChauffeEauUserInboundPortURI != null && !ChauffeEauUserInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauInternalControlInboundPortURI != null && !ChauffeEauInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauExternalControlInboundPortURI != null && !ChauffeEauExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param ChauffeEauUserInboundPortURI				URI of the inbound port to call the ChauffeEau component for user interactions.
	 * @param ChauffeEauInternalControlInboundPortURI	URI of the inbound port to call the ChauffeEau component for internal control.
	 * @param ChauffeEauExternalControlInboundPortURI	URI of the inbound port to call the ChauffeEau component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			ChauffeEau(
		String ChauffeEauUserInboundPortURI,
		String ChauffeEauInternalControlInboundPortURI,
		String ChauffeEauExternalControlInboundPortURI,
		String ChauffeEauSensorInboundPortURI,
		String ChauffeEauActuatorInboundPortURI
		
		) throws Exception
	{

		this(REFLECTION_INBOUND_PORT_URI,
				 ChauffeEauUserInboundPortURI,
				 ChauffeEauInternalControlInboundPortURI,
				 ChauffeEauExternalControlInboundPortURI,
				 ChauffeEauSensorInboundPortURI,
				 ChauffeEauActuatorInboundPortURI,
				 ExecutionType.STANDARD,
				 SimulationType.NO_SIMULATION,
				 null, null, null, 0.0, null);
	}

	/**
	 * create a new ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauUserInboundPortURI != null && !ChauffeEauUserInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauInternalControlInboundPortURI != null && !ChauffeEauInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauExternalControlInboundPortURI != null && !ChauffeEauExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param ChauffeEauUserInboundPortURI				URI of the inbound port to call the ChauffeEau component for user interactions.
	 * @param ChauffeEauInternalControlInboundPortURI	URI of the inbound port to call the ChauffeEau component for internal control.
	 * @param ChauffeEauExternalControlInboundPortURI	URI of the inbound port to call the ChauffeEau component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			ChauffeEau(
			String reflectionInboundPortURI,
			String ChauffeEauUserInboundPortURI,
			String ChauffeEauInternalControlInboundPortURI,
			String ChauffeEauExternalControlInboundPortURI,
			String ChauffeEauSensorInboundPortURI,
			String ChauffeEauActuatorInboundPortURI,
			ExecutionType currentExecutionType,
			SimulationType currentSimulationType,
			String globalArchitectureURI,
			String localArchitectureURI,
			TimeUnit simulationTimeUnit,
			double accFactor,
			String clockURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 2, 1);

			assert	currentExecutionType != null :
					new PreconditionException("currentExecutionType != null");
			assert	!currentExecutionType.isStandard() ||
										clockURI != null && !clockURI.isEmpty() :
					new PreconditionException(
							"!currentExecutionType.isStandard() || "
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
			assert	!currentSimulationType.isRealTimeSimulation() ||
																accFactor > 0.0 :
					new PreconditionException(
							"!currentExecutionType.isRealTimeSimulation() || "
							+ "accFactor > 0.0");

			this.currentExecutionType = currentExecutionType;
			this.currentSimulationType = currentSimulationType;
			this.globalArchitectureURI = globalArchitectureURI;
			this.localArchitectureURI = localArchitectureURI;
			this.simulationTimeUnit = simulationTimeUnit;
			this.accFactor = accFactor;
			this.clockURI = clockURI;
			this.clock = new CompletableFuture<AcceleratedClock>();

			this.initialise(ChauffeEauUserInboundPortURI,
							ChauffeEauInternalControlInboundPortURI,
							ChauffeEauExternalControlInboundPortURI,
							ChauffeEauSensorInboundPortURI,
							ChauffeEauActuatorInboundPortURI);

			assert	ChauffeEau.glassBoxInvariants(this) :
					new InvariantException("ChauffeEau.glassBoxInvariants(this)");
			assert	ChauffeEau.blackBoxInvariants(this) :
					new InvariantException("ChauffeEau.blackBoxInvariants(this)");
		}
	
	protected			ChauffeEau(
			String reflectionInboundPortURI,
			String ChauffeEauUserInboundPortURI,
			String ChauffeEauInternalControlInboundPortURI,
			String ChauffeEauExternalControlInboundPortURI,
			String ChauffeEauSensorInboundPortURI,
			String ChauffeEauActuatorInboundPortURI,
			ExecutionType currentExecutionType,
			SimulationType currentSimulationType,
			String globalArchitectureURI,
			String localArchitectureURI,
			TimeUnit simulationTimeUnit,
			double accFactor,
			String clockURI,
			boolean unittest
			) throws Exception
		{
			super(reflectionInboundPortURI, 2, 1);

			assert	currentExecutionType != null :
					new PreconditionException("currentExecutionType != null");
			assert	!currentExecutionType.isStandard() ||
										clockURI != null && !clockURI.isEmpty() :
					new PreconditionException(
							"!currentExecutionType.isStandard() || "
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
			assert	!currentSimulationType.isRealTimeSimulation() ||
																accFactor > 0.0 :
					new PreconditionException(
							"!currentExecutionType.isRealTimeSimulation() || "
							+ "accFactor > 0.0");

			this.currentExecutionType = currentExecutionType;
			this.currentSimulationType = currentSimulationType;
			this.globalArchitectureURI = globalArchitectureURI;
			this.localArchitectureURI = localArchitectureURI;
			this.simulationTimeUnit = simulationTimeUnit;
			this.accFactor = accFactor;
			this.clockURI = clockURI;
			this.clock = new CompletableFuture<AcceleratedClock>();

			this.initialise(ChauffeEauUserInboundPortURI,
							ChauffeEauInternalControlInboundPortURI,
							ChauffeEauExternalControlInboundPortURI,
							ChauffeEauSensorInboundPortURI,
							ChauffeEauActuatorInboundPortURI);
			this.unittest = unittest;

			assert	ChauffeEau.glassBoxInvariants(this) :
					new InvariantException("ChauffeEau.glassBoxInvariants(this)");
			assert	ChauffeEau.blackBoxInvariants(this) :
					new InvariantException("ChauffeEau.blackBoxInvariants(this)");
		}

	/**
	 * create a new thermostated ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ChauffeEauUserInboundPortURI != null && !ChauffeEauUserInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauInternalControlInboundPortURI != null && !ChauffeEauInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code ChauffeEauExternalControlInboundPortURI != null && !ChauffeEauExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ChauffeEauUserInboundPortURI				URI of the inbound port to call the ChauffeEau component for user interactions.
	 * @param ChauffeEauInternalControlInboundPortURI	URI of the inbound port to call the ChauffeEau component for internal control.
	 * @param ChauffeEauExternalControlInboundPortURI	URI of the inbound port to call the ChauffeEau component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	/*protected void		initialise(
		String ChauffeEauUserInboundPortURI,
		String ChauffeEauInternalControlInboundPortURI,
		String ChauffeEauExternalControlInboundPortURI,
		String ChauffeEauSensorInboundPortURI,
		String ChauffeEauActuatorInboundPortURI
		) throws Exception
	{
		assert	ChauffeEauUserInboundPortURI != null && !ChauffeEauUserInboundPortURI.isEmpty();
		assert	ChauffeEauInternalControlInboundPortURI != null && !ChauffeEauInternalControlInboundPortURI.isEmpty();
		assert	ChauffeEauExternalControlInboundPortURI != null && !ChauffeEauExternalControlInboundPortURI.isEmpty();

		this.currentState = ChauffeEauState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.hip = new ChauffeEauUserInboundPort(ChauffeEauUserInboundPortURI, this);
		this.hip.publishPort();
		this.hicip = new ChauffeEauInternalControlInboundPort(
									ChauffeEauInternalControlInboundPortURI, this);
		this.hicip.publishPort();
		this.hecip = new ChauffeEauExternalControlInboundPort(
									ChauffeEauExternalControlInboundPortURI, this);
		this.hecip.publishPort();
		this.rop = new RegistrationOutboundPort(this.registrationOutboundPortURI,this);
		this.rop.publishPort();
		
		

		if (VERBOSE) {
			this.tracer.get().setTitle("ChauffeEau component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}
	}*/
	
	
	protected void		initialise(
			String ChauffeEauUserInboundPortURI,
			String ChauffeEauInternalControlInboundPortURI,
			String ChauffeEauExternalControlInboundPortURI,
			String ChauffeEauSensorInboundPortURI,
			String ChauffeEauActuatorInboundPortURI
			) throws Exception
		{
			assert	ChauffeEauUserInboundPortURI != null &&
											!ChauffeEauUserInboundPortURI.isEmpty() :
					new PreconditionException(
							"ChauffeEauUserInboundPortURI != null && "
							+ "!ChauffeEauUserInboundPortURI.isEmpty()");
			assert	ChauffeEauInternalControlInboundPortURI != null &&
									!ChauffeEauInternalControlInboundPortURI.isEmpty() :
					new PreconditionException(
							"ChauffeEauInternalControlInboundPortURI != null && "
							+ "!ChauffeEauInternalControlInboundPortURI.isEmpty()");
			assert	ChauffeEauExternalControlInboundPortURI != null &&
									!ChauffeEauExternalControlInboundPortURI.isEmpty() :
					new PreconditionException(
							"ChauffeEauExternalControlInboundPortURI != null && "
							+ "!ChauffeEauExternalControlInboundPortURI.isEmpty()");
			assert	ChauffeEauSensorInboundPortURI != null &&
										!ChauffeEauSensorInboundPortURI.isEmpty() :
					new PreconditionException(
							"ChauffeEauSensorInboundPortURI != null &&"
							+ "!ChauffeEauSensorInboundPortURI.isEmpty()");
			assert	ChauffeEauActuatorInboundPortURI != null &&
										!ChauffeEauActuatorInboundPortURI.isEmpty() :
					new PreconditionException(
							"ChauffeEauActuatorInboundPortURI != null && "
							+ "!ChauffeEauActuatorInboundPortURI.isEmpty()");

			this.currentState = ChauffeEauState.OFF;
			this.currentPowerLevel = MAX_POWER_LEVEL;
			this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

			this.hip = new ChauffeEauUserInboundPort(ChauffeEauUserInboundPortURI, this);
			this.hip.publishPort();
			this.hicip = new ChauffeEauInternalControlInboundPort(
										ChauffeEauInternalControlInboundPortURI, this);
			this.hicip.publishPort();
			this.hecip = new ChauffeEauExternalControlInboundPort(
										ChauffeEauExternalControlInboundPortURI, this);
			this.hecip.publishPort();
			this.rop = new RegistrationOutboundPort(this.registrationOutboundPortURI,this);
			this.rop.publishPort();
			this.sensorInboundPort = new ChauffeEauSensorDataInboundPort(
										ChauffeEauSensorInboundPortURI, this);
			this.sensorInboundPort.publishPort();
			this.actuatorInboundPort = new ChauffeEauActuatorInboundPort(
										ChauffeEauActuatorInboundPortURI, this);
			this.actuatorInboundPort.publishPort();

			// create the simulation architecture given the current type of
			// simulation i.e., for the current execution
			switch (this.currentSimulationType) {
			case MIL_SIMULATION:
				Architecture architecture = null;
				if (this.currentExecutionType.isUnitTest()) {
					architecture =
						LocalSimulationArchitectures.
							createChauffeEauMILLocalArchitecture4UnitTest(
														this.localArchitectureURI,
														this.simulationTimeUnit);
				} else {
					architecture =
							LocalSimulationArchitectures.
								createChauffeEauMILLocalArchitecture4IntegrationTest(
														this.localArchitectureURI,
														this.simulationTimeUnit);
				}
				assert	architecture.getRootModelURI().equals(
														this.localArchitectureURI) :
						new BCMException(
								"local simulator " + this.localArchitectureURI
								+ " does not exist!");
				this.addLocalSimulatorArchitecture(architecture);
				this.global2localSimulationArchitectureURIS.
						put(this.globalArchitectureURI, this.localArchitectureURI);
				break;
			case MIL_RT_SIMULATION:
			case SIL_SIMULATION:
				architecture = null;
				if (this.currentExecutionType.isUnitTest()) {
					architecture =
						LocalSimulationArchitectures.
							createChauffeEau_RT_LocalArchitecture4UnitTest(
														this.currentSimulationType,
														this.localArchitectureURI,
														this.simulationTimeUnit,
														this.accFactor);
				} else {
					architecture =
						LocalSimulationArchitectures.
							createChauffeEau_RT_LocalArchitecture4IntegrationTest(
														this.currentSimulationType,
														this.localArchitectureURI,
														this.simulationTimeUnit,
														this.accFactor);
				}
				assert	architecture.getRootModelURI().equals(
													this.localArchitectureURI) :
						new BCMException(
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
				this.tracer.get().setTitle("ChauffeEau component");
				this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
													  Y_RELATIVE_POSITION);
				this.toggleTracing();		
			}
		}


	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------


	
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		// create the simulation plug-in given the current type of simulation
		// and its local architecture i.e., for the current execution
		try {
			/*this.doPortConnection(
					this.rop.getPortURI(),
					GestionEnergie.registrationInboundPortURI,
					RegistrationConnector.class.getCanonicalName());*/
			if(!this.unittest) {
			this.doPortConnection(
					this.rop.getPortURI(),
					HEM.registrationInboundPortURI,
					RegistrationConnector.class.getCanonicalName());
			}
			
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
				// For SIL simulations, we use the ModelStateAccessI protocol
				// to provide the access to the current temperature computed
				// by the ChauffeEauTemperatureModel.
				this.asp = new RTAtomicSimulatorPlugin() {
					private static final long serialVersionUID = 1L;
					/**
					 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
					 */
					@Override
					public Object	getModelStateValue(
						String modelURI,
						String name
						) throws Exception
					{
						assert	modelURI.equals(ChauffeEauTemperatureModel.SIL_URI);
						assert	name.equals(CURRENT_TEMPERATURE_NAME);
						return ((ChauffeEauTemperatureModel)
										this.atomicSimulators.get(modelURI).
												getSimulatedModel()).
														getCurrentTemperature();
					}
				};
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

			// the computable future clock is used to make sure that the
			// accelerated clock is available when the internal controller
			// wants to start the temperature sensor in push mode, as the
			// clock is needed to time stamp the sensor data properly
			this.clock.complete(clock);

			if (this.currentExecutionType.isUnitTest() &&
								this.currentSimulationType.isSILSimulation()) {
				this.asp.createSimulator();
				this.asp.setSimulationRunParameters(new HashMap<>());
				this.asp.initialiseSimulation(
								new Time(clock.getSimulatedStartTime(),
										 clock.getSimulatedTimeUnit()),
								new Duration(clock.getSimulatedDuration(),
											 clock.getSimulatedTimeUnit()));
				// compute the real time of start of the simulation using the
				// accelerated clock
				long simulationStartTimeInMillis = 
						TimeUnit.NANOSECONDS.toMillis(
										clock.getSimulationStartEpochNanos());
				assert	simulationStartTimeInMillis > System.currentTimeMillis() :
						new BCMException(
								"simulationStartTimeInMillis > "
								+ "System.currentTimeMillis()");
				this.asp.startRTSimulation(simulationStartTimeInMillis,
										   clock.getSimulatedStartTime(),
										   clock.getSimulatedDuration());
				// rather than waiting and sleeping, a task could also be
				// scheduled to free the current thread
				clock.waitUntilSimulationEnd();
				// leave some time for the simulators to perform end of
				// simulation catering tasks
				Thread.sleep(250L);
				this.logMessage(this.asp.getFinalReport().toString());
			}
		}
	}

	
	@Override
	public synchronized void	finalise() throws Exception
	{
		if(!this.unittest) {
		this.rop.doDisconnection();}
		super.finalise();
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hip.unpublishPort();
			this.hicip.unpublishPort();
			this.hecip.unpublishPort();
			this.rop.unpublishPort();
			this.sensorInboundPort.unpublishPort();
			this.actuatorInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau#on() returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == ChauffeEauState.ON ||
									this.currentState == ChauffeEauState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		assert	!on() : new PreconditionException("!on()");
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau switches on.\n");
		}


		this.currentState = ChauffeEauState.ON;
		if(!this.unittest) {
		this.rop.register(this.chauffeEauID, this.registrationOutboundPortURI, null);
		}

		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												ChauffeEauStateModel.SIL_URI,
												t -> new SwitchOnChauffeEau(t));
		}

		// this will send to the ChauffeEau internal controller the signal that the
		// ChauffeEau is now on, hence starting the control loop
		this.sensorInboundPort.send(
				new ChauffeEauSensorData<ChauffeEauStateMeasure>(
						new ChauffeEauStateMeasure(this.currentState)));
		
		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau switches off.\n");
		}

		

		this.currentState = ChauffeEauState.OFF;
		if(!this.unittest) {
		this.rop.unregister(chauffeEauID);}
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												ChauffeEauStateModel.SIL_URI,
												t -> new SwitchOffChauffeEau(t));
		}

		// this will send to the ChauffeEau internal controller the signal that the
		// ChauffeEau is now off, hence stopping the control loop
		this.sensorInboundPort.send(
				new ChauffeEauSensorData<ChauffeEauStateMeasure>(
						new ChauffeEauStateMeasure(this.currentState)));

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		assert	target >= -50.0 && target <= 50.0 :
			new PreconditionException("target >= -50.0 && target <= 50.0");
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau sets a new target "
										+ "temperature: " + target + ".\n");
		}


		this.targetTemperature = target;

		assert	this.getTargetTemperature() == target :
				new PostconditionException("getTargetTemperature() == target");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauTemperatureI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		
		double ret = this.targetTemperature;
		
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its target"
							+ " temperature " +ret + ".\n");
		}


		assert	ret >= -50.0 && ret <= 50.0 :
				new PostconditionException("return >= -50.0 && return <= 50.0");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauTemperatureI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, the value is got from the
			// ChauffeEauTemperatureModel through the ModelStateAccessI interface
			currentTemperature = 
					(double)((RTAtomicSimulatorPlugin)this.asp).
										getModelStateValue(
												ChauffeEauTemperatureModel.SIL_URI,
												CURRENT_TEMPERATURE_NAME);
		}
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its heating status " + 
						(this.currentState == ChauffeEauState.HEATING) + ".\n");
		}

	

		return this.currentState == ChauffeEauState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau starts heating.\n");
		}
	

		this.currentState = ChauffeEauState.HEATING;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												ChauffeEauStateModel.SIL_URI,
												t -> new Heat(t));
		}

		// this will send to the ChauffeEau internal controller the signal that the
		// ChauffeEau is now heating
		this.sensorInboundPort.send(
				new ChauffeEauSensorData<ChauffeEauStateMeasure>(
						new ChauffeEauStateMeasure(this.currentState)));

		assert	this.heating() : new PostconditionException("heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");
		
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau stops heating.\n");
		}
		

		this.currentState = ChauffeEauState.ON;
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												ChauffeEauStateModel.SIL_URI,
												t -> new DoNotHeat(t));
		}

		// this will send to the ChauffeEau internal controller the signal that the
		// ChauffeEau has now stopped heating
		this.sensorInboundPort.send(
				new ChauffeEauSensorData<ChauffeEauStateMeasure>(
						new ChauffeEauStateMeasure(this.currentState)));

		assert	!this.heating() : new PostconditionException("!heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau sets its power level to " + 
														powerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (powerLevel <= getMaxPowerLevel()) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}
		
		if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			PowerValue pv = new PowerValue(this.currentPowerLevel);
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												ChauffeEauStateModel.SIL_URI,
												t -> new SetPowerChauffeEau(t, pv));
		}


		assert	powerLevel > getMaxPowerLevel() ||
										getCurrentPowerLevel() == powerLevel :
				new PostconditionException(
						"powerLevel > getMaxPowerLevel() || "
						+ "getCurrentPowerLevel() == powerLevel");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double ret = this.currentPowerLevel;
		
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its current power level " + 
					ret + ".\n");
		}


		assert	ret >= 0.0 && ret <= getMaxPowerLevel() :
				new PostconditionException(
							"return >= 0.0 && return <= getMaxPowerLevel()");

		return this.currentPowerLevel;
	}
	
	// -------------------------------------------------------------------------
		// Component sensors
		// -------------------------------------------------------------------------

		/**
		 * return the heating status of the ChauffeEau as a sensor data.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code on()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				the heating status of the ChauffeEau as a sensor data.
		 * @throws Exception	<i>to do</i>.
		 */
		public ChauffeEauSensorData<Measure<Boolean>>	heatingPullSensor()
		throws Exception
		{
			return new ChauffeEauSensorData<Measure<Boolean>>(
											new Measure<Boolean>(this.heating()));
		}

		/**
		 * return the target temperature as a sensor data.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				the target temperature as a sensor data.
		 * @throws Exception	<i>to do</i>.
		 */
		public ChauffeEauSensorData<Measure<Double>>	targetTemperaturePullSensor()
		throws Exception
		{
			return new ChauffeEauSensorData<Measure<Double>>(
							new Measure<Double>(this.getTargetTemperature(),
												MeasurementUnit.CELSIUS));
		}

		/**
		 * return the current temperature as a sensor data.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code on()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				the current temperature as a sensor data.
		 * @throws Exception	<i>to do</i>.
		 */
		public ChauffeEauSensorData<Measure<Double>>	currentTemperaturePullSensor()
		throws Exception
		{
			return new ChauffeEauSensorData<Measure<Double>>(
							new Measure<Double>(this.getCurrentTemperature(),
												MeasurementUnit.CELSIUS));
		}

		/**
		 * start a sequence of temperatures pushes with the given period.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code controlPeriod > 0}
		 * pre	{@code tu != null}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param controlPeriod	period at which the pushes must be made.
		 * @param tu			time unit in which {@code controlPeriod} is expressed.
		 * @throws Exception	<i>to do</i>.
		 */
		public void			startTemperaturesPushSensor(
			long controlPeriod,
			TimeUnit tu
			) throws Exception
		{
			long actualControlPeriod = -1L;
			if (this.currentExecutionType.isStandard()) {
				actualControlPeriod = (long)(controlPeriod * tu.toNanos(1));
			} else {
				// this will synchronise the start of the push sensor with the
				// availability of the clock, required to compute the actual push
				// period with the correct acceleration factor
				AcceleratedClock ac = this.clock.get();
				// the accelerated period is in nanoseconds, hence first convert
				// the period to nanoseconds, perform the division and then
				// convert to long (hence providing a better precision than
				// first dividing and then converting to nanoseconds...)
				actualControlPeriod =
						(long)((controlPeriod * tu.toNanos(1))/
												ac.getAccelerationFactor());
				// sanity checking, the standard Java scheduler has a
				// precision no less than 10 milliseconds...
				if (actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
					System.out.println(
						"Warning: accelerated control period is "
								+ "too small ("
								+ actualControlPeriod +
								"), unexpected scheduling problems may"
								+ " occur!");
				}
			}
			this.temperaturesPushSensorTask(actualControlPeriod);
		}

		/**
		 * if the ChauffeEau is not off, perform one push and schedule the next.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code actualControlPeriod > 0}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param actualControlPeriod	period at which the push sensor must be triggered.
		 * @throws Exception			<i>to do</i>.
		 */
		protected void		temperaturesPushSensorTask(long actualControlPeriod)
		throws Exception
		{
			assert	actualControlPeriod > 0 :
					new PreconditionException("actualControlPeriod > 0");

			if (this.currentState != ChauffeEauState.OFF) {
				this.traceMessage("ChauffeEau performs a new temperatures push.\n");
				this.temperaturesPushSensor();
				if (this.currentExecutionType.isStandard()
						|| this.currentSimulationType.isSILSimulation()
						|| this.currentSimulationType.isHILSimulation()) {
					// schedule the next execution of the loop only if the
					// current execution is standard or if it is a real time
					// simulation with code execution i.e., SIL or HIL
					// otherwise, perform only one call to push sensors to
					// test the functionality
					this.scheduleTaskOnComponent(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									temperaturesPushSensorTask(actualControlPeriod);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						},
						actualControlPeriod,
						TimeUnit.NANOSECONDS);
				}
			}
		}

		/**
		 * sends the compound measure of the target and the current temperatures
		 * through the push sensor interface.
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
		protected void		temperaturesPushSensor() throws Exception
		{
			this.sensorInboundPort.send(
						new ChauffeEauSensorData<ChauffeEauCompoundMeasure>(
							new ChauffeEauCompoundMeasure(
								this.targetTemperaturePullSensor().getMeasure(),
								this.currentTemperaturePullSensor().getMeasure())));
		}
}
