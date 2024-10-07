package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.chauffeEau.ChauffeEauUserCI;

public class ChauffeEauUserOutboundPort
extends AbstractOutboundPort 
implements ChauffeEauUserCI 
{
	private static final long serialVersionUID = 1L;

	public ChauffeEauUserOutboundPort(ComponentI owner)
			throws Exception {
		super(ChauffeEauUserCI.class, owner);
	}

	public ChauffeEauUserOutboundPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, ChauffeEauUserCI.class, owner);
	}

	
	
	@Override
	public boolean		on() throws Exception
	{
		return ((ChauffeEauUserCI)this.getConnector()).on();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		((ChauffeEauUserCI)this.getConnector()).switchOn();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		((ChauffeEauUserCI)this.getConnector()).switchOff();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((ChauffeEauUserCI)this.getConnector()).setTargetTemperature(target);
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((ChauffeEauUserCI)this.getConnector()).getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((ChauffeEauUserCI)this.getConnector()).getCurrentTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((ChauffeEauUserCI)this.getConnector()).getMaxPowerLevel();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((ChauffeEauUserCI)this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((ChauffeEauUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
