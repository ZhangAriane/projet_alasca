package projet_alasca.equipements.machineCafe.mil;

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

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2024e2.HEM_ReportI;
import fr.sorbonne_u.components.hem2024e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
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
import projet_alasca.equipements.machineCafe.mil.events.AbstractMachineCafeEvent;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOnMachineCafe;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOffMachineCafe;


@ModelExternalEvents(imported = {SwitchOnMachineCafe.class,
		SwitchOffMachineCafe.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class			MachineCafeElectricityModel
extends		AtomicHIOA
implements MachineCafeOperationI
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------


	public static enum State {
		OFF,
		ON
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;



	/** URI for an instance model in MIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	MIL_URI = MachineCafeElectricityModel.class.
			getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as  only one instance is created.								*/
	public static final String	MIL_RT_URI = MachineCafeElectricityModel.class.
			getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *   only one instance is created. It is the same value as for MIL
	 *  real time simulations as the same models are used in both cases.	*/
	public static final String	SIL_URI = MachineCafeElectricityModel.class.
			getSimpleName() + "-MIL_RT";

	protected static double			CONSUMPTION = 660.0; // Watts

	protected static double			TENSION = 220.0; // Volts


	protected State					currentState = State.OFF;

	protected boolean				consumptionHasChanged = false;

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
			MachineCafeElectricityModel instance
			)
	{
		assert	instance != null :
			new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				CONSUMPTION > 0.0,
				MachineCafeElectricityModel.class,
				instance,
				"CONSUMPTION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				TENSION > 0.0,
				MachineCafeElectricityModel.class,
				instance,
				"TENSION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				instance.totalConsumption >= 0.0,
				MachineCafeElectricityModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				instance.currentState != null,
				MachineCafeElectricityModel.class,
				instance,
				"currentState != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				!instance.currentIntensity.isInitialised() ||
				instance.currentIntensity.getValue() >= 0.0,
				MachineCafeElectricityModel.class,
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
			MachineCafeElectricityModel instance
			)
	{
		assert	instance != null :
			new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MIL_URI != null && !MIL_URI.isEmpty(),
				MachineCafeElectricityModel.class,
				instance,
				"MIL_URI != null && !MIL_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MIL_RT_URI != null && !MIL_RT_URI.isEmpty(),
				MachineCafeElectricityModel.class,
				instance,
				"MIL_RT_URI != null && !MIL_RT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				SIL_URI != null && !SIL_URI.isEmpty(),
				MachineCafeElectricityModel.class,
				instance,
				"SIL_URI != null && !SIL_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				CONSUMPTION_RPNAME != null &&
				!CONSUMPTION_RPNAME.isEmpty(),
				MachineCafeElectricityModel.class,
				instance,
				"CONSUMPTION_RPNAME != null && "
						+ "!CONSUMPTION_RPNAME.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty(),
				MachineCafeElectricityModel.class,
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
	public				MachineCafeElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.VentilateurOperationI.equipments.hairdryer.mil.HairDryerOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (this.currentState == MachineCafeElectricityModel.State.OFF) {
			// then put it in the state LOW
			this.currentState = MachineCafeElectricityModel.State.ON;
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			this.toggleConsumptionHasChanged();
		}
	}

	/**
	 * @see fr.sorbonne_u.components.VentilateurOperationI.equipments.hairdryer.mil.HairDryerOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		// a SwitchOff event can be executed when the state of the hair
		// dryer model is *not* in the state OFF
		if (this.currentState == MachineCafeElectricityModel.State.ON) {
			// then put it in the state OFF
			this.currentState = MachineCafeElectricityModel.State.OFF;
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			this.toggleConsumptionHasChanged();
		}
	}


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
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");
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
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");
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
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");


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
		case ON :
			this.currentIntensity.
			setNewValue(CONSUMPTION/TENSION, t);
			break;

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
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");
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

		assert	ce instanceof AbstractMachineCafeEvent ;

		// events have a method execute on to perform their effect on this
		// model
		ce.executeOn(this);

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");
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
	public static final String		CONSUMPTION_RPNAME = "CONSUMPTION";

	/** run parameter name for {@code TENSION}.								*/
	public static final String		TENSION_RPNAME = "TENSION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
			Map<String, Object> simParams
			) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		// this gets the reference on the owner component which is required
		// to have simulation models able to make the component perform some
		// operations or tasks or to get the value of variables held by the
		// component when necessary.
		if (simParams.containsKey(
				AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			// by the following, all of the logging will appear in the owner
			// component logger
			this.getSimulationEngine().setLogger(
					AtomicSimulatorPlugin.createComponentLogger(simParams));
		}

		String consumptionName =
				ModelI.createRunParameterName(getURI(),
						CONSUMPTION_RPNAME);
		if (simParams.containsKey(consumptionName)) {
			CONSUMPTION = (double) simParams.get(consumptionName);
		}
		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"MachineCafeElectricityModel.blackBoxInvariants(this)");
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
	public static class		MachineCafeElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public				MachineCafeElectricityReport(
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
		return new MachineCafeElectricityReport(this.getURI(),
				this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
