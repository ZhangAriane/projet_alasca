package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;

public class ChauffeEauExternalControlConnector 
extends AbstractConnector
implements ChauffeEauExternalControlCI 
{

	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.offering).getCurrentTemperature();
	}
	
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void		setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((ChauffeEauExternalControlCI)this.offering).
										setCurrentPowerLevel(powerLevel);
	}
	
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.offering).getCurrentPowerLevel();
	}

}
