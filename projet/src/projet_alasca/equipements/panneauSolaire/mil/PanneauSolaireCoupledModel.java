package projet_alasca.equipements.panneauSolaire.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

public class PanneauSolaireCoupledModel
extends CoupledModel
{

	// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		/** URI for an instance model; works as long as only one instance is
		 *  created.															*/
		public static final String		URI = PanneauSolaireCoupledModel.class.
																	getSimpleName();
		// -------------------------------------------------------------------------
		// Constructors
		// -------------------------------------------------------------------------

		/**
		 * creating the coupled model instance.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no more precondition.
		 * post	{@code true}	// no more postcondition.
		 * </pre>
		 *
		 * @param uri				URI of the coupled model to be created.
		 * @param simulatedTimeUnit	time unit used in the simulation by the model.
		 * @param simulationEngine	simulation engine enacting the model.
		 * @param submodels			array of submodels of the new coupled model.
		 * @param imported			map from imported event types to submodels consuming them.
		 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
		 * @param connections		map connecting event sources to arrays of event sinks among submodels.
		 * @throws Exception		<i>to do</i>.
		 */
		public PanneauSolaireCoupledModel(
				String uri, 
				TimeUnit simulatedTimeUnit, 
				CoordinatorI simulationEngine,
				ModelI[] submodels, 
				Map<Class<? extends EventI>, EventSink[]> imported,
				Map<Class<? extends EventI>, ReexportedEvent> reexported, 
				Map<EventSource, EventSink[]> connections)
				throws Exception 
		{
			super(uri, simulatedTimeUnit, simulationEngine, submodels, 
					imported, reexported, connections);
		}
}
