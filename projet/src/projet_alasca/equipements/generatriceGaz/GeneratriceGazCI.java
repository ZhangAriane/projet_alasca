package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;



public interface GeneratriceGazCI 
extends OfferedCI,
		RequiredCI,
		 GeneratriceGazI
{
	@Override
	public GeneratriceGazState getState() throws Exception;
	
	
	@Override
	public void	turnOn() throws Exception;
	
	@Override
	public void	turnOff() throws Exception;
	
}
