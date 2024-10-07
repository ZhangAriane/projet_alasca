package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.chauffeEau.ChauffeEauInternalControlCI;
import projet_alasca.equipements.chauffeEau.ChauffeEauInternalControlI;

public class ChauffeEauInternalControlInboundPort 
extends AbstractInboundPort 
implements ChauffeEauInternalControlCI 
{

	private static final long serialVersionUID = 1L;
	
	public ChauffeEauInternalControlInboundPort(ComponentI owner)
			throws Exception {
		super(ChauffeEauInternalControlCI.class, owner);
		assert	owner instanceof ChauffeEauInternalControlI :
			new PreconditionException(
					"owner instanceof ChauffeEauInternalControlI");
	}

	public ChauffeEauInternalControlInboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri, ChauffeEauInternalControlCI.class, owner);
		assert	owner instanceof ChauffeEauInternalControlI :
			new PreconditionException(
					"owner instanceof ChauffeEauInternalControlI");
	}

	@Override
	public boolean		heating() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((ChauffeEauInternalControlI)o).heating());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((ChauffeEauInternalControlI)o).
														getTargetTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((ChauffeEauInternalControlI)o).
														getCurrentTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((ChauffeEauInternalControlI)o).
																startHeating();
										return null;
								});
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauInternalControlCI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((ChauffeEauInternalControlI)o).
																stopHeating();
										return null;
								});
	}

}
