package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.chauffeEau.ChauffeEauInternalControlCI;

public class ChauffeEauInternalControlConnector 
extends AbstractConnector 
implements ChauffeEauInternalControlCI {

	@Override
	public boolean		heating() throws Exception
	{
		return ((ChauffeEauInternalControlCI)this.offering).heating();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((ChauffeEauInternalControlCI)this.offering).getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((ChauffeEauInternalControlCI)this.offering).getCurrentTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		((ChauffeEauInternalControlCI)this.offering).startHeating();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		((ChauffeEauInternalControlCI)this.offering).stopHeating();
	}

}
