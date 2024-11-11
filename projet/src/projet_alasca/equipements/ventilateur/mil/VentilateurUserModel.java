package projet_alasca.equipements.ventilateur.mil;

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
import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.InvariantChecking;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOnVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOffVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetLowVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetMediumVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetHighVentilateur;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerUserModel</code> defines a very simple user
 * model for the ventilateur.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is meant to illustrate how to program MIL models simulating user
 * actions by sending events to other models.
 * </p>
 * <p>
 * Here, we use an event scheduling atomic model to output events at random
 * time intervals in a predefined cycle to test all of the different modes in
 * the ventilateur. Note that the exported events are indeed subclasses of
 * event scheduling events {@code ES_Event}. Hence, this example also shows
 * how to program this type of event scheduling simulation models.
 * </p>
 * <p>
 * Event scheduling models are constructed around an event list that contains
 * events scheduled to be executed at future time in the simulation time. The
 * class {@code AtomicES_Model} hence defines the main methods to execute
 * the transitions. Internal transitions simply occurs at the time of the next
 * event in the event list and then, if the event is internal, execute that
 * event on the model (by calling the method {@code executeOn} defined on the
 * event. If the next event is external, it must be emitted towards other
 * models. This is performed by the method {@code AtomicES_Model#output}
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 *   {@code SwitchOnHairDryer},
 *   {@code SwitchOffHairDryer},
 *   {@code SetLowHairDryer},
 *   {@code SetHighHairDryer}</li>
 * </ul>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code STEP_MEAN_DURATION > 0.0}
 * invariant	{@code DELAY_MEAN_DURATION > 0.0}
 * invariant	{@code rg != null}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * invariant	{@code MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty()}
 * invariant	{@code MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(exported = {SwitchOnVentilateur.class,
								 SwitchOffVentilateur.class,
								 SetLowVentilateur.class,
								 SetMediumVentilateur.class,
								 SetHighVentilateur.class})
// -----------------------------------------------------------------------------
public class			VentilateurUserModel
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String	URI = VentilateurUserModel.class.getSimpleName();

	/** time interval between event outputs in hours.						*/
	protected static double		STEP_MEAN_DURATION = 5.0/60.0; // 5 minutes
	/** time interval between ventilateur usages in hours.					*/
	protected static double		DELAY_MEAN_DURATION = 4.0;

	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg ;

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
	protected static boolean	glassBoxInvariants(VentilateurUserModel instance)
	{
		assert	instance != null :
				new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				STEP_MEAN_DURATION > 0.0,
				VentilateurUserModel.class,
				instance,
				"STEP_MEAN_DURATION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				DELAY_MEAN_DURATION > 0.0,
				VentilateurUserModel.class,
				instance,
				"DELAY_MEAN_DURATION > 0.0");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				instance.rg != null,
				VentilateurUserModel.class,
				instance,
				"rg != null");
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
	protected static boolean	blackBoxInvariants(VentilateurUserModel instance)
	{
		assert	instance != null :
				new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				URI != null && !URI.isEmpty(),
				VentilateurUserModel.class,
				instance,
				"URI != null && !URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty(),
				VentilateurUserModel.class,
				instance,
				"MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty(),
				VentilateurUserModel.class,
				instance,
				"MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty()");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a ventilateur user MIL model instance.
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
	 * @throws Exception		<i>to do.</i>
	 */
	public				VentilateurUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	blackBoxInvariants(this) :
				new AssertionError("Black-box invariants violation!");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * generate the next event in the test scenario; current implementation
	 * cycles through {@code SwitchOnHairDryer}, {@code SetHighHairDryer},
	 * {@code SetLowHairDryer} and {@code SwitchOffHairDryer} in this order
	 * at a random time interval following a gaussian distribution with
	 * mean {@code STEP_MEAN_DURATION} and standard deviation
	 * {@code STEP_MEAN_DURATION/2.0}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code eventList.peek() != null}
	 * post	{@code eventList.peek() != null}
	 * </pre>
	 *
	 */
	protected void		generateNextEvent()
	{
		EventI current = this.eventList.peek();
		// compute the next event type given the current event
		ES_EventI nextEvent = null;
		if (current instanceof SwitchOffVentilateur) {
			// compute the time of occurrence for the next ventilateur usage
			Time t2 = this.computeTimeOfNextUsage(current.getTimeOfOccurrence());
			// after switching off the ventilateur, the next event must be a
			// switch on
			nextEvent = new SwitchOnVentilateur(t2);
		} else {
			// compute the time of occurrence for the next event
			Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
			// establish a sequence of actions i.e., events, that tests all
			// states and modes of the ventilateur
			if (current instanceof SwitchOnVentilateur) {
			    nextEvent = new SetLowVentilateur(t); // Start in LOW mode
			} else if (current instanceof SetLowVentilateur) {
			    nextEvent = new SetMediumVentilateur(t);
			} else if (current instanceof SetMediumVentilateur) {
			    nextEvent = new SetHighVentilateur(t);
			} else if (current instanceof SetHighVentilateur) {
			    nextEvent = new SetMediumVentilateur(t); // Step down to MEDIUM
			} else if (current instanceof SetMediumVentilateur) {
			    nextEvent = new SetLowVentilateur(t); // Step down to LOW
			} else if (current instanceof SetLowVentilateur) {
			    nextEvent = new SwitchOffVentilateur(t); // Finally, turn off
			}

			
		}
		// schedule the event to be executed by this model
		this.scheduleEvent(nextEvent);
	}

	/**
	 * compute the time of the next event, adding a random delay to
	 * {@code from}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code from != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param from	time from which a delay will be added to get the time of the next event.
	 * @return		the time of the next event, adding a random delay to {@code from}.
	 */
	protected Time		computeTimeOfNextEvent(Time from)
	{
		assert	from != null;

		// generate randomly the next time interval but force it to be
		// greater than 0 by returning at least 0.1 
		double delay = Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
													 STEP_MEAN_DURATION/2.0),
								0.1);
		// compute the new time by adding the delay to from
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	/**
	 * compute the time of the next ventilateur usage, adding a random delay to
	 * {@code from}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code from != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param from	time from which a delay will be added to get the time of the next event.
	 * @return		the time of the next event, adding a random delay to {@code from}.
	 */
	protected Time		computeTimeOfNextUsage(Time from)
	{
		assert	from != null;

		// generate randomly the next time interval but force it to be
		// greater than 0 by returning at least 0.1 
		double delay = Math.max(this.rg.nextGaussian(DELAY_MEAN_DURATION,
													 DELAY_MEAN_DURATION/10.0),
								0.1);
		// compute the new time by adding the delay to from
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		// reinitialise the random number generator to get good samples
		this.rg.reSeedSecure();

		// compute the time of occurrence for the first event
		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		// schedule the first event
		this.scheduleEvent(new SwitchOnVentilateur(t));
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance());

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// generate and schedule the next event
		if (this.eventList.peek() != null) {
			this.generateNextEvent();
		}
		// this will extract the next event from the event list and emit it
		return super.output();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String		MEAN_STEP_RPNAME = "STEP_MEAN_DURATION";
	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String		MEAN_DELAY_RPNAME = "STEP_MEAN_DURATION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String stepName =
				ModelI.createRunParameterName(getURI(), MEAN_STEP_RPNAME);
		if (simParams.containsKey(stepName)) {
			STEP_MEAN_DURATION = (double) simParams.get(stepName);
		}
		String delayName =
				ModelI.createRunParameterName(getURI(), MEAN_DELAY_RPNAME);
		if (simParams.containsKey(delayName)) {
			DELAY_MEAN_DURATION = (double) simParams.get(delayName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------
