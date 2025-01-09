package projet_alasca.equipements.ventilateur.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.ventilateur.mil.VentilateurElectricityModel;
import projet_alasca.equipements.ventilateur.mil.VentilateurOperationI;
import projet_alasca.etape3.equipments.hairdryer.mil.HairDryerOperationI;

public class SetMediumVentilateur 
extends AbstractVentilateurEvent 
{

	// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		
		// Constructors 
		
		
	public SetMediumVentilateur(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		
	}
	
	//-------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------
	
	@Override
	public boolean			hasPriorityOver(EventI e)
	{
		// Medium mode has priority over Low but not over SwitchOn
        return !(e instanceof SwitchOnVentilateur);
	}
	
	
	@Override
    public void executeOn(AtomicModelI model) {
//        assert model instanceof VentilateurElectricityModel :
//            new AssertionError("Model must be an instance of VentilateurElectricityModel");
//
//        VentilateurElectricityModel m = (VentilateurElectricityModel) model;
//        // Set to MEDIUM if current state is LOW or HIGH
//        if (m.getState() == VentilateurElectricityModel.State.LOW || 
//            m.getState() == VentilateurElectricityModel.State.HIGH) {
//            m.setState(VentilateurElectricityModel.State.MEDIUM);
//            m.toggleConsumptionHasChanged();
//        }
		
		assert	model instanceof HairDryerOperationI;

		((VentilateurOperationI)model).setMedium();
    }
	
}
