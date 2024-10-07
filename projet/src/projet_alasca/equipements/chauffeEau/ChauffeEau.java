package projet_alasca.equipements.chauffeEau;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauExternalControlInboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauInternalControlInboundPort;
import projet_alasca.equipements.chauffeEau.connections.ChauffeEauUserInboundPort;

@OfferedInterfaces(offered={ChauffeEauUserCI.class, ChauffeEauInternalControlCI.class,
		ChauffeEauExternalControlCI.class})
public class ChauffeEau 
extends AbstractComponent 
implements ChauffeEauUserI, 
			ChauffeEauInternalControlI 
			
{

	protected static enum	ChauffeEauState
	{
		/** ChauffeEau is on.													*/
		ON,
		/** ChauffeEau is heating.												*/
		HEATING,
		/** ChauffeEau is off.													*/
		OFF
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

	/** URI of the ChauffeEau port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"ChauffeEau-USER-INBOUND-PORT-URI";
	/** URI of the ChauffeEau port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"ChauffeEau-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the ChauffeEau port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"ChauffeEau-EXTERNAL-CONTROL-INBOUND-PORT-URI";

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
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
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
		String ChauffeEauExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(ChauffeEauUserInboundPortURI,
						ChauffeEauInternalControlInboundPortURI,
						ChauffeEauExternalControlInboundPortURI);
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
		String ChauffeEauExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.initialise(ChauffeEauUserInboundPortURI,
						ChauffeEauInternalControlInboundPortURI,
						ChauffeEauExternalControlInboundPortURI);
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
	protected void		initialise(
		String ChauffeEauUserInboundPortURI,
		String ChauffeEauInternalControlInboundPortURI,
		String ChauffeEauExternalControlInboundPortURI
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
			this.traceMessage("ChauffeEau returns its state: " +
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
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = ChauffeEauState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = ChauffeEauState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	target >= -50.0 && target <= 50.0 :
				new PreconditionException("target >= -50.0 && target <= 50.0");

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
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		double ret = this.targetTemperature;

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
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its heating status " + 
						(this.currentState == ChauffeEauState.HEATING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == ChauffeEauState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau starts heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");

		this.currentState = ChauffeEauState.HEATING;

		assert	this.heating() : new PostconditionException("heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau stops heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");

		this.currentState = ChauffeEauState.ON;

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
		if (ChauffeEau.VERBOSE) {
			this.traceMessage("ChauffeEau returns its current power level " + 
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
