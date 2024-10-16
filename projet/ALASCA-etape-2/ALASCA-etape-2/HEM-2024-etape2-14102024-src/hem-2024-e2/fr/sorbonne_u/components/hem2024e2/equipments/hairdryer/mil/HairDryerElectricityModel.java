package fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil;

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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2024e2.HEM_ReportI;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.AbstractHairDryerEvent;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SetHighHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SetLowHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.components.hem2024e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.InvariantChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerElectricity_MILModel</code> defines a MIL model
 * of the electricity consumption of a hair dryer.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The hair dryer can be switched on and off, and when switched on, it can be
 * either in a low mode, with lower electricity consumption, or a high mode,
 * with a higher electricity consumption.
 * </p>
 * <p>
 * The electricity consumption is represented as a variable of type double that
 * has to be exported towards the electric meter MIL model in order to be summed
 * up to get the global electricity consumption of the house.
 * </p>
 * <p>
 * To model the user actions, four events are defined to be imported and the
 * external transitions upon the reception of these events force the hair
 * dryer electricity model in the corresponding mode with the corresponding
 * electricity consumption.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnHairDryer},
 *   {@code SwitchOffHairDryer},
 *   {@code SetLowHairDryer},
 *   {@code SetHighHairDryer}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentIntensity}, type = {@code Double}</li>
 * </ul>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code LOW_MODE_CONSUMPTION > 0.0}
 * invariant	{@code LOW_MODE_CONSUMPTION <= HIGH_MODE_CONSUMPTION}
 * invariant	{@code TENSION > 0.0}
 * invariant	{@code totalConsumption >= 0.0}
 * invariant	{@code currentState != null}
 * invariant	{@code !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * invariant	{@code LOW_MODE_CONSUMPTION_RPNAME != null && !LOW_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code HIGH_MODE_CONSUMPTION_RPNAME != null && !HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(imported = {SwitchOnHairDryer.class,
								 SwitchOffHairDryer.class,
								 SetLowHairDryer.class,
								 SetHighHairDryer.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class			HairDryerElectricityModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>State</code> describes the discrete states or
	 * modes of the hair dryer.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * The hair dryer can be <code>OFF</code> or on, and then it is either in
	 * <code>LOW</code> mode (less hot and less consuming) or in
	 * <code>HIGH</code> mode (hotter and more consuming).
	 * 
	 * <p>Created on : 2019-10-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum State {
		OFF,
		/** low mode is less hot and less consuming.						*/
		LOW,			
		/** high mode is hotter and more consuming.							*/
		HIGH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String		URI = HairDryerElectricityModel.class.
																getSimpleName();

	/** energy consumption (in Watts) of the hair dryer in LOW mode.		*/
	protected static double			LOW_MODE_CONSUMPTION = 660.0; // Watts
	/** energy consumption (in Watts) of the hair dryer in HIGH mode.		*/
	protected static double			HIGH_MODE_CONSUMPTION = 1100.0; // Watts
	/** nominal tension (in Volts) of the hair dryer.						*/
	protected static double			TENSION = 220.0; // Volts

	/** current state (OFF, LOW, HIGH) of the hair dryer.					*/
	protected State					currentState = State.OFF;
	/** true when the electricity consumption of the dryer has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean				consumptionHasChanged = false;

	/** total consumption of the hair dryer during the simulation in kwh.	*/
	protected double				totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

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
	protected static boolean	glassBoxInvariants(
		HairDryerElectricityModel instance
		)
	{
		assert	instance != null :
				new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				LOW_MODE_CONSUMPTION > 0.0,
				HairDryerElectricityModel.class,
				instance,
				"LOW_MODE_CONSUMPTION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				LOW_MODE_CONSUMPTION <= HIGH_MODE_CONSUMPTION,
				HairDryerElectricityModel.class,
				instance,
				"LOW_MODE_CONSUMPTION <= HIGH_MODE_CONSUMPTION");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				TENSION > 0.0,
				HairDryerElectricityModel.class,
				instance,
				"TENSION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				instance.totalConsumption >= 0.0,
				HairDryerElectricityModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				instance.currentState != null,
				HairDryerElectricityModel.class,
				instance,
				"currentState != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!instance.currentIntensity.isInitialised() ||
									instance.currentIntensity.getValue() >= 0.0,
				HairDryerElectricityModel.class,
				instance,
				"!currentIntensity.isInitialised() || "
				+ "currentIntensity.getValue() >= 0.0");
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
	protected static boolean	blackBoxInvariants(
		HairDryerElectricityModel instance
		)
	{
		assert	instance != null :
				new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				URI != null && !URI.isEmpty(),
				HairDryerElectricityModel.class,
				instance,
				"URI != null && !URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				LOW_MODE_CONSUMPTION_RPNAME != null &&
										!LOW_MODE_CONSUMPTION_RPNAME.isEmpty(),
				HairDryerElectricityModel.class,
				instance,
				"LOW_MODE_CONSUMPTION_RPNAME != null && "
								+ "!LOW_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				HIGH_MODE_CONSUMPTION_RPNAME != null &&
									!HIGH_MODE_CONSUMPTION_RPNAME.isEmpty(),
				HairDryerElectricityModel.class,
				instance,
				"HIGH_MODE_CONSUMPTION_RPNAME != null && "
							+ "!HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty(),
				HairDryerElectricityModel.class,
				instance,
				"TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				HairDryerElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the state of the hair dryer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code getState() == s}
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void			setState(State s)
	{
		this.currentState = s;
	}

	/**
	 * return the state of the hair dryer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the state of the hair dryer.
	 */
	public State		getState()
	{
		return this.currentState;
	}

	/**
	 * toggle the value of the state of the model telling whether the
	 * electricity consumption level has just changed or not; when it changes
	 * after receiving an external event, an immediate internal transition
	 * is triggered to update the level of electricity consumption.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			toggleConsumptionHasChanged()
	{
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
		} else {
			this.consumptionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);

		// initially the hair dryer is off and its electricity consumption is
		// not about to change.
		this.currentState = State.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		// initially, the hair dryer is off, so its consumption is zero.
		this.currentIntensity.initialise(0.0);

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// the model does not export events.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		Duration ret = null;
		// to trigger an internal transition after an external transition, the
		// variable consumptionHasChanged is set to true, hence when it is true
		// return a zero delay otherwise return an infinite delay (no internal
		// transition expected)
		if (this.consumptionHasChanged) {
			// after triggering the internal transition, toggle the boolean
			// to prepare for the next internal transition.
			this.toggleConsumptionHasChanged();
			ret = new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// set the current electricity consumption from the current state
		Time t = this.getCurrentStateTime();
		switch (this.currentState)
		{
			case OFF : this.currentIntensity.setNewValue(0.0, t); break;
			case LOW :
				this.currentIntensity.
							setNewValue(LOW_MODE_CONSUMPTION/TENSION, t);
				break;
			case HIGH :
				this.currentIntensity.
							setNewValue(HIGH_MODE_CONSUMPTION/TENSION, t);
		}

		// Tracing
		StringBuffer message =
				new StringBuffer("executes an internal transition ");
		message.append("with current consumption ");
		message.append(this.currentIntensity.getValue());
		message.append(" at ");
		message.append(this.currentIntensity.getTime());
		message.append(".\n");
		this.logMessage(message.toString());

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of currently received external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the current hair dryer model, there must be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		// optional: compute the total consumption (in kwh) for the simulation
		// report.
		this.totalConsumption +=
				Electricity.computeConsumption(
									elapsedTime,
									TENSION*this.currentIntensity.getValue());

		// Tracing
		StringBuffer message =
				new StringBuffer("executes an external transition ");
		message.append(ce.toString());
		message.append(")\n");
		this.logMessage(message.toString());

		assert	ce instanceof AbstractHairDryerEvent :
				new RuntimeException(
						ce + " is not an event that an HairDryerElectricityModel"
						+ " can receive and process.");
		// events have a method execute on to perform their effect on this
		// model
		ce.executeOn(this);

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(
									d,
									TENSION*this.currentIntensity.getValue());

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code LOW_MODE_CONSUMPTION}.				*/
	public static final String		LOW_MODE_CONSUMPTION_RPNAME =
												URI + ":LOW_MODE_CONSUMPTION";
	/** run parameter name for {@code HIGH_MODE_CONSUMPTION}.				*/
	public static final String		HIGH_MODE_CONSUMPTION_RPNAME =
												URI + ":HIGH_MODE_CONSUMPTION";
	/** run parameter name for {@code TENSION}.								*/
	public static final String		TENSION_RPNAME = URI + ":TENSION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String lowName =
			ModelI.createRunParameterName(getURI(),
										  LOW_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			LOW_MODE_CONSUMPTION = (double) simParams.get(lowName);
		}
		String highName =
			ModelI.createRunParameterName(getURI(),
										  HIGH_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			HIGH_MODE_CONSUMPTION = (double) simParams.get(highName);
		}
		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariant violation!");
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>HairDryerElectricityReport</code> implements the
	 * simulation report for the <code>HairDryerElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
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
	 * <p>Created on : 2023-09-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		HairDryerElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public				HairDryerElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String		getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String		printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption in kwh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return this.printout("");
			
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new HairDryerElectricityReport(this.getURI(),
											  this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
