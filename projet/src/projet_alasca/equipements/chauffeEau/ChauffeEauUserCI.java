package projet_alasca.equipements.chauffeEau;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;



public interface ChauffeEauUserCI 
extends OfferedCI,
		RequiredCI,
		ChauffeEauUserI 
{

	
	@Override
	public double		getTargetTemperature() throws Exception ;
	
	// température de l'eau 

	@Override
	public double		getCurrentTemperature() throws Exception;

	@Override
	public boolean		on() throws Exception;

	@Override
	public void			switchOn() throws Exception;

	@Override
	public void			switchOff() throws Exception;
	
	@Override
	public void			setTargetTemperature(double target) throws Exception;

	
	@Override
	public double		getMaxPowerLevel() throws Exception;

	
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception;

	
	@Override
	public double		getCurrentPowerLevel() throws Exception;
	
}
