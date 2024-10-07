package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;

public class ChauffeEauExternalControlOutboundPort 
extends AbstractOutboundPort
implements ChauffeEauExternalControlCI 
{
	
	private static final long serialVersionUID = 1L;

	public ChauffeEauExternalControlOutboundPort(ComponentI owner)
			throws Exception {
		super(ChauffeEauExternalControlCI.class, owner);
		
	}

	public ChauffeEauExternalControlOutboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri, ChauffeEauExternalControlCI.class, owner);
	}

	
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.getConnector()).
														getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauTemperatureI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.getConnector()).
														getCurrentTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.getConnector()).
													getMaxPowerLevel();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		((ChauffeEauExternalControlCI)this.getConnector()).
											setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((ChauffeEauExternalControlCI)this.getConnector()).
											getCurrentPowerLevel();
	}
}
