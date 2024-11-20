package projet_alasca.equipements.refrigerateur;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2024e1.equipments.hem.AdjustableOutboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import projet_alasca.equipements.chauffeEau.ChauffeEau;
import projet_alasca.equipements.gestionEnergie.ChauffeEauConnector;
import projet_alasca.equipements.gestionEnergie.GestionEnergie;
import projet_alasca.equipements.gestionEnergie.RefrigerateurConnector;
import projet_alasca.equipements.gestionEnergie.RegistrationConnector;
import projet_alasca.equipements.gestionEnergie.RegistrationOutboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurExternalControlInboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurInternalControlInboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurUserInboundPort;
import projet_alasca.equipements.refrigerateur.connections.RegistrationInboundPort;

@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered={RefrigerateurUserCI.class, RefrigerateurInternalControlCI.class, RefrigerateurExternalControlCI.class})
public class Refrigerateur extends AbstractComponent implements RefrigerateurUserI, RefrigerateurInternalControlI{

	protected static enum	RefrigeratorState
	{
		/** refrigerator is on.													*/
		ON,
		/** refrigerator is off.													*/
		OFF
	}
	
	protected static enum CompressorState{
		ON,
		OFF
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final String REFRIGERATEUR_ID ="REFRIGERATEUR-ID";
	
	/** URI of the refrigerator inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
			"REFRIGERATEUR-RIP-URI";	
	/** max power level of the refrigerator, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 800.0;
	/** standard target temperature for the refrigerator.							*/
	protected static final double	STANDARD_TARGET_TEMPERATURE = 1.0;

	/** URI of the refrigerator port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
			"REFRIGERATEUR-USER-INBOUND-PORT-URI";
	/** URI of the refrigerator port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
			"REFRIGERATEUR-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the refrigerator port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
			"REFRIGERATEUR-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the refrigerator port for internal control.						*/
	public static final String		REGISTRATION_OUTBOUND_PORT_URI =
			"REGISTRATION-OUTBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static boolean			VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int				X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int				Y_RELATIVE_POSITION = 0;

	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 5.0;

	/** current state (on, off) of the refrigerator.								*/
	protected RefrigeratorState						currentState;
	/** target temperature for the cooling.									*/
	protected double							targetTemperature;
	/**	current power level of the refrigerator.									*/
	protected double							currentPowerLevel;
	/** current state (on, off) of the refrigerator compressor.								*/
	protected CompressorState refrigeratorCompressor;
	/** current state (on, off) of the freezer compressor								*/
	protected CompressorState freezerCompressor;

	/** inbound port offering the <code>RefrigerateurUserCI</code> interface.		*/
	protected RefrigerateurUserInboundPort				refrigerateurUserInboundPort;
	/** inbound port offering the <code>RefrigerateurInternalControlCI</code>
	 *  interface.															*/
	protected RefrigerateurInternalControlInboundPort	refrigerateurInternalControlInboundPort;
	/** inbound port offering the <code>RefrigerateurExternalControlCI</code>
	 *  interface.															*/
	protected RefrigerateurExternalControlInboundPort	refrigerateurExternalControlInboundPort;
	/** inbound port offering the <code>RegistrationCI</code>
	 *  interface.															*/
	protected RegistrationOutboundPort registrationOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			Refrigerateur() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
				EXTERNAL_CONTROL_INBOUND_PORT_URI,REGISTRATION_OUTBOUND_PORT_URI);
	}

	protected			Refrigerateur(
			String refrigerateurUserInboundPortURI,
			String refrigerateurInternalControlInboundPortURI,
			String refrigerateurExternalControlInboundPortURI,
			String registrationOutboundPortURI
			) throws Exception
	{
		super(1, 0);
		this.initialise(refrigerateurUserInboundPortURI,
				refrigerateurInternalControlInboundPortURI,
				refrigerateurExternalControlInboundPortURI,
				registrationOutboundPortURI);
	}


	protected			Refrigerateur(
			String reflectionInboundPortURI,
			String refrigerateurUserInboundPortURI,
			String refrigerateurInternalControlInboundPortURI,
			String refrigerateurExternalControlInboundPortURI,
			String registrationOutboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.initialise(refrigerateurUserInboundPortURI,
				refrigerateurInternalControlInboundPortURI,
				refrigerateurExternalControlInboundPortURI,
				registrationOutboundPortURI);
	}


	protected void		initialise(
			String refrigerateurUserInboundPortURI,
			String refrigerateurInternalControlInboundPortURI,
			String refrigerateurExternalControlInboundPortURI,
			String registrationOutboundPortURI
			) throws Exception
	{
		assert	refrigerateurUserInboundPortURI != null && !refrigerateurUserInboundPortURI.isEmpty();
		assert	refrigerateurInternalControlInboundPortURI != null && !refrigerateurInternalControlInboundPortURI.isEmpty();
		assert	refrigerateurExternalControlInboundPortURI != null && !refrigerateurExternalControlInboundPortURI.isEmpty();

		this.currentState = RefrigeratorState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.refrigerateurUserInboundPort = new RefrigerateurUserInboundPort(refrigerateurUserInboundPortURI, this);
		this.refrigerateurUserInboundPort.publishPort();
		this.refrigerateurInternalControlInboundPort = new RefrigerateurInternalControlInboundPort(
				refrigerateurInternalControlInboundPortURI, this);
		this.refrigerateurInternalControlInboundPort.publishPort();
		this.refrigerateurExternalControlInboundPort = new RefrigerateurExternalControlInboundPort(
				refrigerateurExternalControlInboundPortURI, this);
		this.refrigerateurExternalControlInboundPort.publishPort();
		
		this.registrationOutboundPort = new RegistrationOutboundPort(registrationOutboundPortURI,this);
		this.registrationOutboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Refrigerator component");
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
			
			try {
				this.doPortConnection(
						this.registrationOutboundPort.getPortURI(),
						GestionEnergie.registrationInboundPortURI,
						RegistrationConnector.class.getCanonicalName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.registrationOutboundPort.doDisconnection();
		super.finalise();
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.refrigerateurUserInboundPort.unpublishPort();
			this.refrigerateurInternalControlInboundPort.unpublishPort();
			this.refrigerateurExternalControlInboundPort.unpublishPort();
			this.registrationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public boolean on() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its state: " +
											this.currentState + ".\n");
		}
		
		return this.currentState == RefrigeratorState.ON;
									
	}

	@Override
	public void switchOn() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = RefrigeratorState.ON;
		
		

		
		this.registrationOutboundPort.register(REFRIGERATEUR_ID, STANDARD_SCHEDULABLE_HANDLER_URI, "refrigerateur-adapter.xml");
		assert	 this.on() : new PostconditionException("on()");

	}

	@Override
	public void switchOff() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = RefrigeratorState.OFF;
		
		this.registrationOutboundPort.unregister(REFRIGERATEUR_ID);

		assert	 !this.on() : new PostconditionException("!on()");

	}

	@Override
	public void setTargetTemperature(double target) throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	target >= 1.0 && target <= 7.0 :
				new PreconditionException("target >= 1.0 && target <= 7.0");

		this.targetTemperature = target;

		assert	this.getTargetTemperature() == target :
				new PostconditionException("getTargetTemperature() == target");

	}

	
	@Override
	public double getTargetTemperature() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		double ret = this.targetTemperature;

		assert	ret >= 1.0 && ret <= 7.0 :
				new PostconditionException("return >= 1.0 && return <= 7.0");

		return ret;
	}


	@Override
	public double getCurrentTemperature() throws Exception {
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}


	@Override
	public boolean cooling() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its cooling status " + 
					(this.currentState == RefrigeratorState.ON) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.refrigeratorCompressor == CompressorState.ON;
	}

	@Override
	public void startCooling() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator starts cooling.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.cooling() : new PreconditionException("!cooling()");

		this.refrigeratorCompressor = CompressorState.ON;

		assert	this.cooling() : new PostconditionException("cooling()");

	}

	@Override
	public void stopCooling() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator stops cooling.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.cooling() : new PreconditionException("cooling()");

		this.refrigeratorCompressor = CompressorState.OFF;

		assert	this.cooling() : new PostconditionException("!cooling()");

	}

	
	@Override
	public double getMaxPowerLevel() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator sets its power level to " + 
														powerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (powerLevel <= getMaxPowerLevel()) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}

		assert	powerLevel > getMaxPowerLevel() ||
										getCurrentPowerLevel() == powerLevel :
				new PostconditionException(
						"powerLevel > getMaxPowerLevel() || "
						+ "getCurrentPowerLevel() == powerLevel");

	}

	@Override
	public double getCurrentPowerLevel() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its current power level " + 
					this.currentPowerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		double ret = this.currentPowerLevel;

		assert	ret >= 0.0 && ret <= getMaxPowerLevel() :
				new PostconditionException(
							"return >= 0.0 && return <= getMaxPowerLevel()");

		return this.currentPowerLevel;
	}


	
	
}
