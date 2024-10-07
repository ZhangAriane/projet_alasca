package projet_alasca.equipements.chauffeEau;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ChauffeEauInternalControlCI
extends OfferedCI,
		RequiredCI,
		ChauffeEauInternalControlI 
{

	@Override
	public double		getTargetTemperature() throws Exception;

	@Override
	public double		getCurrentTemperature() throws Exception;

	
	@Override
	public boolean		heating() throws Exception;

	
	@Override
	public void			startHeating() throws Exception;

	@Override
	public void			stopHeating() throws Exception;
}
