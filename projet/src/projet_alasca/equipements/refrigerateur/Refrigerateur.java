package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.gestionEnergie.RegistrationOutboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurExternalControlInboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurInternalControlInboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurUserInboundPort;

@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered={RefrigerateurUserCI.class, RefrigerateurInternalControlCI.class, RefrigerateurExternalControlCI.class})
public class Refrigerateur extends AbstractComponent implements RefrigerateurUserI, RefrigerateurInternalControlI{

	protected static enum	RefrigeratorState
	{
		/** refrigerator is on.													*/
		ON,
		/** refrigerator is off.													*/
		OFF,
		COOLING,
		FREEZING
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the refrigerator inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
			"REFRIGERATEUR-RIP-URI";	
	/** max power level of the refrigerator, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 800.0;
	/** standard target temperature for the refrigerator.							*/
	protected static final double	STANDARD_REFRIGERATOR_TARGET_TEMPERATURE = 1.0;
	
	/** standard target temperature for the freezer.							*/
	protected static final double	STANDARD_FREEZER_TARGET_TEMPERATURE = -20.0;

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
	public static final double		FAKE_REFRIGERATOR_CURRENT_TEMPERATURE = 5.0;
	
	/** fake freezer current 	*/
	public static final double		FAKE_FREEZER_CURRENT_TEMPERATURE = -20.0;

	/** current state (on, off) of the refrigerator.								*/
	protected RefrigeratorState						currentRefrigeratorState;
	/** current state (on, off) of the freezer.								*/
	protected RefrigeratorState						currentFreezerState;
	/**	current power level of the refrigerator.									*/
	protected double							currentPowerLevel;
	/** target temperature for the cooling.									*/
	protected double							targetRefrigeratorTemperature;
	/** target temperature for the freezing.									*/
	protected double							targetFreezerTemperature;

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

		this.currentRefrigeratorState = RefrigeratorState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetRefrigeratorTemperature = STANDARD_REFRIGERATOR_TARGET_TEMPERATURE;
		this.targetFreezerTemperature = STANDARD_FREEZER_TARGET_TEMPERATURE;

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
											this.currentRefrigeratorState + ".\n");
		}
		
		return this.currentRefrigeratorState == RefrigeratorState.ON && 
				this.currentFreezerState == RefrigeratorState.ON;
									
	}

	@Override
	public void switchOn() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentRefrigeratorState = RefrigeratorState.ON;
		this.currentFreezerState = RefrigeratorState.ON;

		assert	 this.on() : new PostconditionException("on()");

	}

	@Override
	public void switchOff() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentRefrigeratorState = RefrigeratorState.OFF;
		this.currentFreezerState = RefrigeratorState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");

	}

	@Override
	public void setRefrigeratorTargetTemperature(double target) throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	target >= 1.0 && target <= 7.0 :
				new PreconditionException("target >= 1.0 && target <= 7.0");

		this.targetRefrigeratorTemperature = target;

		assert	this.getRefrigeratorTargetTemperature() == target :
				new PostconditionException("getTargetTemperature() == target");

	}

	@Override
	public void setFreezerTargetTemperature(double target) throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Freezer sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	target >= -23.0 && target <= -15.0 :
				new PreconditionException("target >= -23.0 && target <= -15.0");

		this.targetFreezerTemperature = target;

		assert	this.getFreezerTargetTemperature() == target :
				new PostconditionException("getFreezerTargetTemperature() == target");

	}
	
	@Override
	public double getRefrigeratorTargetTemperature() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its target"
							+ " temperature " + this.targetRefrigeratorTemperature + ".\n");
		}

		double ret = this.targetRefrigeratorTemperature;

		assert	ret >= 1.0 && ret <= 7.0 :
				new PostconditionException("return >= 1.0 && return <= 7.0");

		return ret;
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Freezer returns its target"
							+ " temperature " + this.targetFreezerTemperature + ".\n");
		}

		double ret = this.targetFreezerTemperature;

		assert	ret >= -23.0 && ret <= -15.0 :
				new PostconditionException("return >= -23.0 && return <= -15.0");

		return ret;
	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_REFRIGERATOR_CURRENT_TEMPERATURE;
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentFreezerTemperature = FAKE_FREEZER_CURRENT_TEMPERATURE;
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Freezer returns the current"
							+ " temperature " + currentFreezerTemperature + ".\n");
		}

		return  currentFreezerTemperature;
	}

	@Override
	public boolean cooling() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator returns its cooling status " + 
						(this.currentRefrigeratorState == RefrigeratorState.COOLING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentRefrigeratorState == RefrigeratorState.COOLING;
	}

	@Override
	public void startCooling() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator starts cooling.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.cooling() : new PreconditionException("!cooling()");

		this.currentRefrigeratorState = RefrigeratorState.COOLING;

		assert	this.cooling() : new PostconditionException("cooling()");

	}

	@Override
	public void stopCooling() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Refrigerator stops cooling.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.cooling() : new PreconditionException("cooling()");

		this.currentRefrigeratorState = RefrigeratorState.ON;

		assert	this.cooling() : new PostconditionException("!cooling()");

	}

	@Override
	public boolean freezing() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Freezer returns its freezing status " + 
						(this.currentFreezerState == RefrigeratorState.FREEZING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentFreezerState == RefrigeratorState.FREEZING;
	}

	@Override
	public void startFreezing() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Freezer starts freezing.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.freezing() : new PreconditionException("!freezing()");

		this.currentFreezerState = RefrigeratorState.FREEZING;

		assert	this.freezing() : new PostconditionException("freezing()");

	}

	@Override
	public void stopFreezing() throws Exception {
		if (Refrigerateur.VERBOSE) {
			this.traceMessage("Freezer stops freezing.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.freezing() : new PreconditionException("freezing()");

		this.currentFreezerState = RefrigeratorState.ON;

		assert	this.freezing() : new PostconditionException("!freezing()");

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
