package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RefrigerateurInternalControlCI extends OfferedCI, RequiredCI, RefrigerateurInternalControlI {
	
	@Override
	public double		getTargetTemperature() throws Exception;

	
	@Override
	public double		getCurrentTemperature() throws Exception;


	@Override
	public boolean		cooling()throws Exception;
	
	@Override
	public void			startCooling() throws Exception;

	@Override
	public void			stopCooling() throws Exception;

}
