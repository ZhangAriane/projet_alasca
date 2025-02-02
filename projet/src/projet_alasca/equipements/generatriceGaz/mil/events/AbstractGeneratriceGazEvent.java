package projet_alasca.equipements.generatriceGaz.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class AbstractGeneratriceGazEvent 
extends ES_Event
{
	private static final long serialVersionUID = 1L;

	/**
	 * used to create an event used by the petrol generator simulation model.
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 * @param content          content (data) associated with the event.
	 */
	public AbstractGeneratriceGazEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}

}
