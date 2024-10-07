package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlI;
import projet_alasca.equipements.chauffeEau.ChauffeEauTemperatureI;

public class ChauffeEauExternalControlInboundPort 
extends AbstractInboundPort
implements ChauffeEauExternalControlCI {
	
	private static final long serialVersionUID = 1L;

	public ChauffeEauExternalControlInboundPort(ComponentI owner)
			throws Exception {
		super(ChauffeEauExternalControlCI.class, owner);
		assert	owner instanceof ChauffeEauExternalControlI :
			new PreconditionException(
					"owner instanceof ChauffeEauExternalControlI");
	}

	public ChauffeEauExternalControlInboundPort(
			String uri, 
			ComponentI owner) throws Exception {
		super(uri, ChauffeEauExternalControlCI.class, owner);
		assert	owner instanceof ChauffeEauExternalControlI :
			new PreconditionException(
					"owner instanceof ChauffeEauExternalControlI");
	}

	
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
					o -> ((ChauffeEauTemperatureI)o).getTargetTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauTemperatureI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
					o -> ((ChauffeEauTemperatureI)o).getCurrentTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((ChauffeEauExternalControlI)o).
															getMaxPowerLevel());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((ChauffeEauExternalControlI)o).
											setCurrentPowerLevel(powerLevel);
									return null;
							});
	}

	/**
	 * @see fr.sorbonne_u.components.hem2024e1.equipments.ChauffeEau.ChauffeEauExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((ChauffeEauExternalControlI)o).
													getCurrentPowerLevel());
	}
	

}
