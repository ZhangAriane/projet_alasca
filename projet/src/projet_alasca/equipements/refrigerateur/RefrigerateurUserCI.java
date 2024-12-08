package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RefrigerateurUserCI extends OfferedCI, RequiredCI, RefrigerateurUserI{

	@Override
	public boolean		on() throws Exception;

	@Override
	public void			switchOn() throws Exception;

	@Override
	public void			switchOff() throws Exception;

	@Override
	public void			setRefrigeratorTargetTemperature(double target)
	throws Exception;
	
	@Override
	public void			setFreezerTargetTemperature(double target)
			throws Exception;
	
	@Override
	public double		getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(double powerLevel)
			throws Exception;

	@Override
	public double		getCurrentPowerLevel() throws Exception;
	
	@Override
	public void switchOnRefrigeratorCompressor() throws Exception;

	@Override
	public void switchOffRefrigeratorCompressor() throws Exception;

	@Override
	public void switchOnFreezerCompressor() throws Exception;

	@Override
	public void switchOffFreezerCompressor() throws Exception;
	
	@Override
	public boolean onRefrigeratorCompressor() throws Exception;

	@Override
	public boolean onFreezerCompressor() throws Exception;
	
	@Override
	public double		getRefrigeratorTargetTemperature() throws Exception;
	
	@Override
	public double		getFreezerTargetTemperature() throws Exception;
	
	@Override
	public double		getRefrigeratorCurrentTemperature() throws Exception;
	
	@Override
	public double		getFreezerCurrentTemperature() throws Exception;
}