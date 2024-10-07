package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RefrigerateurInternalControlCI extends OfferedCI, RequiredCI, RefrigerateurInternalControlI {
	
	@Override
	public double		getRefrigeratorTargetTemperature() throws Exception;
	
	@Override
	public double		getFreezerTargetTemperature() throws Exception;
	
	@Override
	public double		getRefrigeratorCurrentTemperature() throws Exception;
	
	@Override
	public double		getFreezerCurrentTemperature() throws Exception;

	@Override
	public boolean		cooling()throws Exception;
	
	@Override
	public void			startCooling() throws Exception;

	@Override
	public void			stopCooling() throws Exception;
	
	@Override
	public boolean		freezing() throws Exception;

	@Override
	public void			startFreezing() throws Exception;

	@Override
	public void			stopFreezing() throws Exception;

}
