package projet_alasca.equipements.panneauSolaire.mil.events;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.panneauSolaire.mil.PanneauSolaireOperationI;

public class PanneauSolaireNotProduce
extends Event
implements PanneauSolaireEventI
{
	// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		// -------------------------------------------------------------------------
		// Constructors
		// -------------------------------------------------------------------------

		/**
		 * create a {@code ScolarNotProduce} event instance.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param timeOfOccurrence	time of occurrence of the event.
		 */
		public PanneauSolaireNotProduce(Time timeOfOccurrence) {
			super(timeOfOccurrence, null);
		}

		// -------------------------------------------------------------------------
		// Methods
		// -------------------------------------------------------------------------

		@Override
		public boolean		hasPriorityOver(EventI e)
		{
			return true;
		}

		/**
		 * execute this event on {@code model}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code model instanceof IndoorGardenElectricityModel}
		 * post	{@code true}	// no more postconditions.
		 * </pre>
		 * 
		 */
		@Override
		public void executeOn(AtomicModelI model) {
			assert	model instanceof PanneauSolaireOperationI :
					new AssertionError("Precondition violation: "
								+ "model instanceof PanneauSolairePanelOperationI");

			((PanneauSolaireOperationI) model).stopProduce();
		}
}
