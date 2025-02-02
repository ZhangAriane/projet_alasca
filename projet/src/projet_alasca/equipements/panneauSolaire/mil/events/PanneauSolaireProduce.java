package projet_alasca.equipements.panneauSolaire.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.panneauSolaire.mil.PanneauSolaireOperationI;

public class PanneauSolaireProduce 
extends ES_Event
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
		 * create a <code>ScolarProduce</code> event.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code timeOfOccurrence != null}
		 * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
		 * post	{@code this.getEventInformation.equals(content)}
		 * </pre>
		 *
		 * @param timeOfOccurrence	time of occurrence of the event.
		 */
		public PanneauSolaireProduce(
				Time timeOfOccurrence)
		{
			super(timeOfOccurrence, null);
		}


		// -------------------------------------------------------------------------
		// Methods
		// -------------------------------------------------------------------------

		@Override
		public boolean		hasPriorityOver(EventI e)
		{
			return false;
		}

		@Override
		public void			executeOn(AtomicModelI model)
		{
			assert	model instanceof PanneauSolaireOperationI :
				new AssertionError("Precondition violation: "
							+ "model instanceof PanneauSolaireOperationI");
			
			((PanneauSolaireOperationI) model).startProduce();
		}
}
