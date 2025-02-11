package projet_alasca.equipements.chauffeEau.mil;

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
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.InvariantChecking;
import projet_alasca.equipements.chauffeEau.mil.events.ChauffeEauEventI;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOnChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOffChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.Heat;
import projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauStateModel.State;

// -----------------------------------------------------------------------------
/**
 * The class <code>ChauffeEauElectricityModel</code> defines a simulation model
 * for the electricity consumption of the ChauffeEau.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric power consumption (in amperes) depends upon the state and the
 * current power level i.e., {@code State.OFF => consumption == 0.0},
 * {@code State.ON => consumption == NOT_HEATING_POWER} and
 * {@code State.HEATING => consumption >= NOT_HEATING_POWER && consumption <= MAX_HEATING_POWER}).
 * The state of the ChauffeEau is modified by the reception of external events
 * ({@code SwitchOnChauffeEau}, {@code SwitchOffChauffeEau}, {@code Heat} and
 * {@code DoNotHeat}). The power level is set through the external event
 * {@code SetPowerChauffeEau} that has a parameter defining the required power
 * level. The electric power consumption is stored in the exported variable
 * {@code currentIntensity}.
 * </p>
 * <p>
 * Initially, the mode is in state {@code State.OFF} and the electric power
 * consumption at 0.0.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnChauffeEau},
 *   {@code SwitchOffChauffeEau},
 *   {@code SetPowerChauffeEau},
 *   {@code Heat},
 *   {@code DoNotHeat}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentIntensity}, type = {@code Double}</li>
 * </ul>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code NOT_HEATING_POWER >= 0.0}
 * invariant	{@code MAX_HEATING_POWER > NOT_HEATING_POWER}
 * invariant	{@code TENSION > 0.0}
 * invariant	{@code currentState != null}
 * invariant	{@code totalConsumption >= 0.0}
 * invariant	{@code !currentHeatingPower.isInitialised() || currentHeatingPower.getValue() >= 0.0}
 * invariant	{@code !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * invariant	{@code NOT_HEATING_POWER_RUNPNAME != null && !NOT_HEATING_POWER_RUNPNAME.isEmpty()}
 * invariant	{@code MAX_HEATING_POWER_RUNPNAME != null && !MAX_HEATING_POWER_RUNPNAME.isEmpty()}
 * invariant	{@code TENSION_RUNPNAME != null && !TENSION_RUNPNAME.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(imported = {SwitchOnChauffeEau.class,
								 SwitchOffChauffeEau.class,
								 SetPowerChauffeEau.class,
								 Heat.class,
								 DoNotHeat.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentHeatingPower", type = Double.class)
//-----------------------------------------------------------------------------
public class			ChauffeEauElectricityModel
extends		AtomicHIOA
implements	ChauffeEauOperationI
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>State</code> defines the state in which the
	 * ChauffeEau can be from the electric power consumption perspective.
	 *
	 * <p>Created on : 2021-09-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	
	/** URI for a MIL model; works when only one instance is created.		*/
	public static final String	MIL_URI = ChauffeEauElectricityModel.class.
			getSimpleName() + "-MIL";
/** URI for a MIL real time model; works when only one instance is
*  created.															*/
public static final String	MIL_RT_URI = ChauffeEauElectricityModel.class.
			getSimpleName() + "-MIL-RT";
/** URI for a SIL model; works when only one instance is created.		*/
public static final String	SIL_URI = ChauffeEauElectricityModel.class.
			getSimpleName() + "-SIL";


	/** power of the ChauffeEau in watts.										*/
	protected static double		NOT_HEATING_POWER = 22.0;
	/** max power of the ChauffeEau in watts.										*/
	public static double		MAX_HEATING_POWER = 2000.0;
	/** nominal tension (in Volts) of the ChauffeEau.							*/
	protected static double		TENSION = 220.0;

	/** current state of the ChauffeEau.										*/
	protected State				currentState = State.OFF;
	/** true when the electricity consumption of the ChauffeEau has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean			consumptionHasChanged = false;

	/** total consumption of the ChauffeEau during the simulation in kwh.		*/
	protected double			totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current heating power between 0 and
	 *  {@code ChauffeEauElectricityModel.MAX_HEATING_POWER}.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentHeatingPower =
														new Value<Double>(this);
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
		ChauffeEauElectricityModel instance
		)
	{
		assert	instance != null :
			new NeoSim4JavaException("Precondition violation: "
					+ "instance != null");
		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
					NOT_HEATING_POWER >= 0.0,
					ChauffeEauElectricityModel.class,
					instance,
					"NOT_HEATING_POWER >= 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					MAX_HEATING_POWER > NOT_HEATING_POWER,
					ChauffeEauElectricityModel.class,
					instance,
					"MAX_HEATING_POWER > NOT_HEATING_POWER");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					TENSION > 0.0,
					ChauffeEauElectricityModel.class,
					instance,
					"TENSION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.currentState != null,
					ChauffeEauElectricityModel.class,
					instance,
					"currentState != null");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.totalConsumption >= 0.0,
					ChauffeEauElectricityModel.class,
					instance,
					"totalConsumption >= 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					!instance.currentHeatingPower.isInitialised() ||
								instance.currentHeatingPower.getValue() >= 0.0,
					ChauffeEauElectricityModel.class,
					instance,
					"!currentHeatingPower.isInitialised() || "
							+ "currentHeatingPower.getValue() >= 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
					!instance.currentIntensity.isInitialised() ||
									instance.currentIntensity.getValue() >= 0.0,
					ChauffeEauElectricityModel.class,
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
		ChauffeEauElectricityModel instance
		)
	{
		assert	instance != null :
			new NeoSim4JavaException("Precondition violation: "
					+ "instance != null");

	boolean ret = true;
	ret &= InvariantChecking.checkBlackBoxInvariant(
				MIL_URI != null && !MIL_URI.isEmpty(),
				ChauffeEauElectricityModel.class,
				instance,
				"MIL_URI != null && !MIL_URI.isEmpty()");
	ret &= InvariantChecking.checkBlackBoxInvariant(
				MIL_RT_URI != null && !MIL_RT_URI.isEmpty(),
				ChauffeEauElectricityModel.class,
				instance,
				"MIL_RT_URI != null && !MIL_RT_URI.isEmpty()");
	ret &= InvariantChecking.checkBlackBoxInvariant(
				SIL_URI != null && !SIL_URI.isEmpty(),
				ChauffeEauElectricityModel.class,
				instance,
				"SIL_URI != null && !SIL_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				NOT_HEATING_POWER_RUNPNAME != null &&
									!NOT_HEATING_POWER_RUNPNAME.isEmpty(),
				ChauffeEauElectricityModel.class,
				instance,
				"NOT_HEATING_POWER_RUNPNAME != null && "
				+ "!NOT_HEATING_POWER_RUNPNAME.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MAX_HEATING_POWER_RUNPNAME != null &&
									!MAX_HEATING_POWER_RUNPNAME.isEmpty(),
				ChauffeEauElectricityModel.class,
				instance,
				"MAX_HEATING_POWER_RUNPNAME != null && "
				+ "!MAX_HEATING_POWER_RUNPNAME.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				TENSION_RUNPNAME != null && !TENSION_RUNPNAME.isEmpty(),
				ChauffeEauElectricityModel.class,
				instance,
				"TENSION_RUNPNAME != null && !TENSION_RUNPNAME.isEmpty()");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a ChauffeEau MIL model instance.
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
	public				ChauffeEauElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.glassBoxInvariants(this)");
	assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.blackBoxInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the state of the ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 * @param t		time at which the state {@code s} is set.
	 */
	public void			setState(State s, Time t)
	{
		State old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.consumptionHasChanged = true;					
		}

		
	}

	/**
	 * return the state of the ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public State		getState()
	{
		return this.currentState;
	}

	/**
	 * set the current heating power of the ChauffeEau to {@code newPower}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_HEATING_POWER}
	 * post	{@code getCurrentHeatingPower() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the ChauffeEau.
	 * @param t			time at which the new power is set.
	 */
	public void			setCurrentHeatingPower(double newPower, Time t)
	{
		assert	newPower >= 0.0 &&
				newPower <= ChauffeEauElectricityModel.MAX_HEATING_POWER :
			new NeoSim4JavaException(
					"Precondition violation: newPower >= 0.0 && "
					+ "newPower <= ChauffeEauElectricityModel.MAX_HEATING_POWER,"
					+ " but newPower = " + newPower);
		
		double oldPower = this.currentHeatingPower.getValue();
		this.currentHeatingPower.setNewValue(newPower, t);
		if (newPower != oldPower) {
			this.consumptionHasChanged = true;
		}

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.glassBoxInvariants(this)");
	assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.blackBoxInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = State.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.glassBoxInvariants(this)");
	assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.blackBoxInvariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		Pair<Integer, Integer> ret = null;

		if (!this.currentIntensity.isInitialised() ||
								!this.currentHeatingPower.isInitialised()) {
			// initially, the ChauffeEau is off, so its consumption is zero.
			this.currentIntensity.initialise(0.0);
			this.currentHeatingPower.initialise(MAX_HEATING_POWER);

			StringBuffer sb = new StringBuffer("new consumption: ");
			sb.append(this.currentIntensity.getValue());
			sb.append(" amperes at ");
			sb.append(this.currentIntensity.getTime());
			sb.append(" seconds.\n");
			this.logMessage(sb.toString());
			ret = new Pair<>(2, 0);
		} else {
			ret = new Pair<>(0, 0);
		}

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.glassBoxInvariants(this)");
	assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.blackBoxInvariants(this)");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		Duration ret = null;

		if (this.consumptionHasChanged) {
			// When the consumption has changed, an immediate (delay = 0.0)
			// internal transition must be made to update the electricity
			// consumption.
			this.consumptionHasChanged = false;
			ret = Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// As long as the state does not change, no internal transition
			// is made (delay = infinity).
			ret = Duration.INFINITY;
		}



		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		if (this.currentState == State.ON) {
			this.currentIntensity.setNewValue(
					ChauffeEauElectricityModel.NOT_HEATING_POWER/
											ChauffeEauElectricityModel.TENSION,
					t);
		} else if (this.currentState == State.HEATING) {
			this.currentIntensity.setNewValue(
								this.currentHeatingPower.getValue()/
												ChauffeEauElectricityModel.TENSION,
								t);
		} else {
			assert	this.currentState == State.OFF;
			this.currentIntensity.setNewValue(0.0, t);
		}

		StringBuffer sb = new StringBuffer("new consumption: ");
		sb.append(this.currentIntensity.getValue());
		sb.append(" amperes at ");
		sb.append(this.currentIntensity.getTime());
		sb.append(" seconds.\n");
		this.logMessage(sb.toString());

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.glassBoxInvariants(this)");
	assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.blackBoxInvariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the ChauffeEau model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof ChauffeEauEventI;

		// compute the total consumption for the simulation report.
		this.totalConsumption +=
				Electricity.computeConsumption(
									elapsedTime,
									TENSION*this.currentIntensity.getValue());

		StringBuffer sb = new StringBuffer("execute the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());

		// the next call will update the current state of the ChauffeEau and if
		// this state has changed, it put the boolean consumptionHasChanged
		// at true, which in turn will trigger an immediate internal transition
		// to update the current intensity of the ChauffeEau electricity
		// consumption.
		ce.executeOn(this);

		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.glassBoxInvariants(this)");
	assert	blackBoxInvariants(this) :
			new NeoSim4JavaException(
					"ChauffeEauElectricityModel.blackBoxInvariants(this)");
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

	/** power of the ChauffeEau in watts.										*/
	public static final String	NOT_HEATING_POWER_RUNPNAME = "NOT_HEATING_POWER";
	/** power of the ChauffeEau in watts.										*/
	public static final String	MAX_HEATING_POWER_RUNPNAME = "MAX_HEATING_POWER";
	/** nominal tension (in Volts) of the ChauffeEau.							*/
	public static final String	TENSION_RUNPNAME = "TENSION";

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

		String notHeatingName =
			ModelI.createRunParameterName(getURI(), NOT_HEATING_POWER_RUNPNAME);
		if (simParams.containsKey(notHeatingName)) {
			NOT_HEATING_POWER = (double) simParams.get(notHeatingName);
		}
		String heatingName =
			ModelI.createRunParameterName(getURI(), MAX_HEATING_POWER_RUNPNAME);
		if (simParams.containsKey(heatingName)) {
			MAX_HEATING_POWER = (double) simParams.get(heatingName);
		}
		String tensionName =
			ModelI.createRunParameterName(getURI(), TENSION_RUNPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}


	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>ChauffeEauElectricityReport</code> implements the
	 * simulation report for the <code>ChauffeEauElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-09-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		ChauffeEauElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh


		public			ChauffeEauElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
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
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new ChauffeEauElectricityReport(this.getURI(), this.totalConsumption);
	}

	@Override
	public void setState(State s) {
		// TODO Auto-generated method stub
		
	}

	

	
}
// -----------------------------------------------------------------------------
