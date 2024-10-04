package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RefrigerateurExternalControlCI extends OfferedCI, RequiredCI, RefrigerateurExternalControlI {

	@Override
	public double		getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(double powerLevel)
			throws Exception;


	@Override
	public double		getCurrentPowerLevel() throws Exception;
	
	@Override
	public double		getTargetTemperature() throws Exception;
	
	@Override
	public double		getCongelateurTargetTemperature() throws Exception;
	
	@Override
	public double		getCurrentTemperature() throws Exception;
	
	@Override
	public double		getCongelateurCurrentTemperature() throws Exception;

}
