package fr.sorbonne_u.components.hem2024e1.equipments.heater;

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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterExternalControlInboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterInternalControlInboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterUserInboundPort;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Heater</code> implements a heater component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code MAX_POWER_LEVEL > 0.0}
 * invariant	{@code currentState != null}
 * invariant	{@code targetTemperature >= -50.0 && targetTemperature <= 50.0}
 * invariant	{@code currentPowerLevel >= 0.0 && currentPowerLevel <= MAX_POWER_LEVEL}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()}
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2023-09-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={HeaterUserCI.class, HeaterInternalControlCI.class,
							HeaterExternalControlCI.class})
public class			Heater
extends		AbstractComponent
implements	HeaterUserI,
			HeaterInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>HeaterState</code> describes the operation
	 * states of the heater.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static enum	HeaterState
	{
		/** heater is on.													*/
		ON,
		/** heater is heating.												*/
		HEATING,
		/** heater is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the heater inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
															"Heater-RIP-URI";	
	/** max power level of the heater, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;
	/** standard target temperature for the heater.							*/
	protected static final double	STANDARD_TARGET_TEMPERATURE = 19.0;

	/** URI of the heater port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"HEATER-USER-INBOUND-PORT-URI";
	/** URI of the heater port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"HEATER-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the heater port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"HEATER-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static boolean			VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int				X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int				Y_RELATIVE_POSITION = 0;

	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 10.0;

	/** current state (on, off) of the heater.								*/
	protected HeaterState						currentState;
	/**	current power level of the heater.									*/
	protected double							currentPowerLevel;
	/** target temperature for the heating.									*/
	protected double							targetTemperature;

	/** inbound port offering the <code>HeaterUserCI</code> interface.		*/
	protected HeaterUserInboundPort				hip;
	/** inbound port offering the <code>HeaterInternalControlCI</code>
	 *  interface.															*/
	protected HeaterInternalControlInboundPort	hicip;
	/** inbound port offering the <code>HeaterExternalControlCI</code>
	 *  interface.															*/
	protected HeaterExternalControlInboundPort	hecip;

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
	protected static boolean	glassBoxInvariants(Heater h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				MAX_POWER_LEVEL > 0.0,
				Heater.class, h,
				"MAX_POWER_LEVEL > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				h.currentState != null,
				Heater.class, h,
				"h.currentState != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				h.targetTemperature >= -50.0 && h.targetTemperature <= 50.0,
				Heater.class, h,
				"h.targetTemperature >= -50.0 && h.targetTemperature <= 50.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				h.currentPowerLevel >= 0.0 &&
									h.currentPowerLevel <= MAX_POWER_LEVEL,
				Heater.class, h,
				"h.currentPowerLevel >= 0.0 && "
								+ "h.currentPowerLevel <= MAX_POWER_LEVEL");
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
	protected static boolean	blackBoxInvariants(Heater h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				Heater.class, h,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				Heater.class, h,
				"USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Heater.class, h,
				"INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
							+ "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Heater.class, h,
				"EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&"
							+ "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				X_RELATIVE_POSITION >= 0,
				Heater.class, h,
				"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				Y_RELATIVE_POSITION >= 0,
				Heater.class, h,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new heater.
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
	protected			Heater() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param heaterInternalControlInboundPortURI	URI of the inbound port to call the heater component for internal control.
	 * @param heaterExternalControlInboundPortURI	URI of the inbound port to call the heater component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Heater(
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(heaterUserInboundPortURI,
						heaterInternalControlInboundPortURI,
						heaterExternalControlInboundPortURI);
	}

	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param heaterInternalControlInboundPortURI	URI of the inbound port to call the heater component for internal control.
	 * @param heaterExternalControlInboundPortURI	URI of the inbound port to call the heater component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Heater(
		String reflectionInboundPortURI,
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.initialise(heaterUserInboundPortURI,
						heaterInternalControlInboundPortURI,
						heaterExternalControlInboundPortURI);
	}

	/**
	 * create a new thermostated heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param heaterInternalControlInboundPortURI	URI of the inbound port to call the heater component for internal control.
	 * @param heaterExternalControlInboundPortURI	URI of the inbound port to call the heater component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI
		) throws Exception
	{
		assert	heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty();
		assert	heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty();
		assert	heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty();

		this.currentState = HeaterState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.hip = new HeaterUserInboundPort(heaterUserInboundPortURI, this);
		this.hip.publishPort();
		this.hicip = new HeaterInternalControlInboundPort(
									heaterInternalControlInboundPortURI, this);
		this.hicip.publishPort();
		this.hecip = new HeaterExternalControlInboundPort(
									heaterExternalControlInboundPortURI, this);
		this.hecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Heater component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}

		assert	Heater.glassBoxInvariants(this) :
				new InvariantException("Heater.glassBoxInvariants(this)");
		assert	Heater.blackBoxInvariants(this) :
				new InvariantException("Heater.blackBoxInvariants(this)");
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
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterUserI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == HeaterState.ON ||
									this.currentState == HeaterState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterUserI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = HeaterState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterUserI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = HeaterState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterUserI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	target >= -50.0 && target <= 50.0 :
				new PreconditionException("target >= -50.0 && target <= 50.0");

		this.targetTemperature = target;

		assert	this.getTargetTemperature() == target :
				new PostconditionException("getTargetTemperature() == target");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterTemperatureI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		double ret = this.targetTemperature;

		assert	ret >= -50.0 && ret <= 50.0 :
				new PostconditionException("return >= -50.0 && return <= 50.0");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterTemperatureI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its heating status " + 
						(this.currentState == HeaterState.HEATING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == HeaterState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater starts heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");

		this.currentState = HeaterState.HEATING;

		assert	this.heating() : new PostconditionException("heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater stops heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");

		this.currentState = HeaterState.ON;

		assert	!this.heating() : new PostconditionException("!heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater sets its power level to " + 
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
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its current power level " + 
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
// -----------------------------------------------------------------------------
