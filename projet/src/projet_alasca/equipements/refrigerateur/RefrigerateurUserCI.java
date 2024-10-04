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
	public void			setTargetTemperature(double target)
	throws Exception;
	
	@Override
	public void			setCongelateurTargetTemperature(double target)
			throws Exception;
	
	@Override
	public double		getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(double powerLevel)
			throws Exception;

	@Override
	public double		getCurrentPowerLevel() throws Exception;
}
