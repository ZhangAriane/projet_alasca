package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RefrigerateurExternalControlCI extends OfferedCI, RequiredCI, RefrigerateurExternalControlI {

	@Override
	public double		getRefrigeratorTargetTemperature() throws Exception;
	
	@Override
	public double		getFreezerTargetTemperature() throws Exception;
	
	@Override
	public double		getRefrigeratorCurrentTemperature() throws Exception;
	
	@Override
	public double		getFreezerCurrentTemperature() throws Exception;
	
	@Override
	public double		getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(double powerLevel)
			throws Exception;

	@Override
	public double		getCurrentPowerLevel() throws Exception;
}