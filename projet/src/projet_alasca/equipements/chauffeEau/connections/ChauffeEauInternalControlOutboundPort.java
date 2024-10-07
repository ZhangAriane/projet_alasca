package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.chauffeEau.ChauffeEauInternalControlCI;

public class ChauffeEauInternalControlOutboundPort
extends AbstractOutboundPort
implements ChauffeEauInternalControlCI 
{
	private static final long serialVersionUID = 1L;

	public ChauffeEauInternalControlOutboundPort(ComponentI owner)
			throws Exception {
		super(ChauffeEauInternalControlCI.class, owner);
	}

	public ChauffeEauInternalControlOutboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri, ChauffeEauInternalControlCI.class, owner);
	}
	
	
	@Override
	public boolean		heating() throws Exception
	{
		return ((ChauffeEauInternalControlCI)this.getConnector()).heating();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((ChauffeEauInternalControlCI)this.getConnector()).
													getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((ChauffeEauInternalControlCI)this.getConnector()).
													getCurrentTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		((ChauffeEauInternalControlCI)this.getConnector()).startHeating();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		((ChauffeEauInternalControlCI)this.getConnector()).stopHeating();
	}

}
