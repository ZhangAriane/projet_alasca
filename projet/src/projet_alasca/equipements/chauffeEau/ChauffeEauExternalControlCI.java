package projet_alasca.equipements.chauffeEau;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ChauffeEauExternalControlCI 
extends OfferedCI,
		RequiredCI,
		ChauffeEauExternalControlI
{

	@Override
	public double		getTargetTemperature() throws Exception;

	@Override
	public double		getCurrentTemperature() throws Exception;
	
	@Override
	public double		getMaxPowerLevel() throws Exception;
	
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception;
	
	@Override
	public double		getCurrentPowerLevel() throws Exception;
}
