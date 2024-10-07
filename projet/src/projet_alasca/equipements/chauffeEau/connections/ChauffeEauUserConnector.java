package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.chauffeEau.ChauffeEauUserCI;

public class ChauffeEauUserConnector
extends AbstractConnector 
implements ChauffeEauUserCI 

{

	@Override
	public boolean		on() throws Exception
	{
		return ((ChauffeEauUserCI)this.offering).on();
	}

	
	@Override
	public void			switchOn() throws Exception
	{
		((ChauffeEauUserCI)this.offering).switchOn();
	}

	@Override
	public void			switchOff() throws Exception
	{
		((ChauffeEauUserCI)this.offering).switchOff();
	}

	
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((ChauffeEauUserCI)this.offering).setTargetTemperature(target);
	}

	
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((ChauffeEauUserCI)this.offering).getTargetTemperature();
	}

	
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((ChauffeEauUserCI)this.offering).getCurrentTemperature();
	}

	
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((ChauffeEauUserCI)this.offering).getMaxPowerLevel();
	}

	
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((ChauffeEauUserCI)this.offering).setCurrentPowerLevel(powerLevel);
	}

	
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((ChauffeEauUserCI)this.offering).getCurrentPowerLevel();
	}
	

}
