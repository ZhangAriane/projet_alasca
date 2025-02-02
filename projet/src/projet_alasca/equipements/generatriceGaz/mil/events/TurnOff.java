package projet_alasca.equipements.generatriceGaz.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.generatriceGaz.mil.GeneratriceGazOperationI;
import projet_alasca.equipements.ventilateur.mil.VentilateurOperationI;

public class TurnOff 
extends AbstractGeneratriceGazEvent 
{
	private static final long serialVersionUID = 1L;

	/**
	 * create a TurnOff event.
	 *
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 */
	public TurnOff(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String eventAsString() {
		return "TurnOff Generatrice gaz(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(AtomicModelI)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
		assert model instanceof GeneratriceGazOperationI;
		((GeneratriceGazOperationI)model).turnOn();
			

	}

}
