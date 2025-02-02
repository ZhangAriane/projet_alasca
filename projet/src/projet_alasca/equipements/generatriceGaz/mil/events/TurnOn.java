package projet_alasca.equipements.generatriceGaz.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.generatriceGaz.mil.GeneratriceGazOperationI;
import projet_alasca.equipements.ventilateur.mil.VentilateurOperationI;

public class TurnOn extends AbstractGeneratriceGazEvent {
	
	private static final long serialVersionUID = 1L;

	/**
	 * create a TurnOn event.
	 *
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 */
	public TurnOn(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String eventAsString() {
		return "TurnOn petrol generator(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
		assert model instanceof GeneratriceGazOperationI;

		((GeneratriceGazOperationI)model).turnOn();
	}

}
