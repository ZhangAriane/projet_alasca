package projet_alasca.equipements.panneauSolaire.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import projet_alasca.equipements.panneauSolaire.mil.events.PanneauSolaireNotProduce;
import projet_alasca.equipements.panneauSolaire.mil.events.PanneauSolaireProduce;


@ModelExternalEvents(imported = {PanneauSolaireProduce.class,
		 PanneauSolaireNotProduce.class},
exported = {PanneauSolaireProduce.class,
	     PanneauSolaireNotProduce.class}
)
public class PanneauSolaireStateModel
extends AtomicModel 
implements PanneauSolaireOperationI
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

	private static final long serialVersionUID = 1L;
	
	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String	URI = PanneauSolaireStateModel.class.
																getSimpleName();

	/** current state.														*/
	protected State				currentState = State.OFF;
	/** last received external event that must be propagated.				*/
	protected EventI			lastReceived;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public PanneauSolaireStateModel(
			String uri, 
			TimeUnit simulatedTimeUnit, 
			AtomicSimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public void startProduce() {
		if (this.currentState == State.OFF) {
			this.currentState = State.ON;
		} else {
			// if no state change is required, no need to propagate the event
			this.lastReceived = null;
		}		
	}

	@Override
	public void stopProduce() {
		if (this.currentState == State.ON) {
			this.currentState = State.OFF;
		} else {
			// if no state change is required, no need to propagate the event
			this.lastReceived = null;
		}			
	}
	
	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = State.OFF;
		this.lastReceived = null;
		super.initialiseState(initialTime);

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public ArrayList<EventI>	output() 
	{
		ArrayList<EventI> ret = new ArrayList<EventI>();
		if (this.lastReceived != null) {
			ret.add(this.lastReceived);
			this.lastReceived = null;
		}
		return ret;
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.lastReceived != null ) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}
	
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of currently received external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the current Panneau Solaire model, there must be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		this.lastReceived = (Event) currentEvents.get(0);

		// Tracing
		StringBuffer message =
				new StringBuffer("executes an external transition on event ");
		message.append(this.lastReceived.toString());
		message.append(")\n");
		this.logMessage(message.toString());

		// events have a method execute on to perform their effect on this
		// model
		this.lastReceived.executeOn(this);
	}
	
	@Override
	public void			endSimulation(Time endTime) 
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		// retrieve the reference to the owner component when passed as a
		// simulation run parameter
		if (simParams.containsKey(
				AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			/*AbstractComponent owner =
					(AbstractComponent) simParams.get(
							PanneauSolaireRTAtomicSimulatorPlugin.OWNER_RPNAME);*/
			// direct traces on the tracer of the owner component
			this.getSimulationEngine().setLogger(AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

}
