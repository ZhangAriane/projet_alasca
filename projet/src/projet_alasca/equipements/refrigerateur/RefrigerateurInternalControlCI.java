package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RefrigerateurInternalControlCI extends OfferedCI, RequiredCI, RefrigerateurInternalControlI {

	@Override
	public boolean		heating() throws Exception;

	@Override
	public void			startHeating() throws Exception;

	@Override
	public void			stopHeating() throws Exception;
	
	@Override
	public double		getTargetTemperature() throws Exception;
	
	@Override
	public double		getCongelateurTargetTemperature() throws Exception;
	
	@Override
	public double		getCurrentTemperature() throws Exception;
	
	@Override
	public double		getCongelateurCurrentTemperature() throws Exception;
}
