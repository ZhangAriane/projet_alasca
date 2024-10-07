package projet_alasca.equipements.chauffeEau.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.chauffeEau.ChauffeEauTemperatureI;
import projet_alasca.equipements.chauffeEau.ChauffeEauUserCI;
import projet_alasca.equipements.chauffeEau.ChauffeEauUserI;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlI;


public class ChauffeEauUserInboundPort
extends AbstractInboundPort
implements ChauffeEauUserCI {

	private static final long serialVersionUID = 1L;
	
	public ChauffeEauUserInboundPort(ComponentI owner)
			throws Exception {
		super(ChauffeEauUserCI.class, owner);
		assert	owner instanceof ChauffeEauUserI :
				new PreconditionException("owner instanceof ChauffeEauUserI");
	}

	public ChauffeEauUserInboundPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, ChauffeEauUserCI.class, owner);
		assert	owner instanceof ChauffeEauUserI :
			new PreconditionException("owner instanceof ChauffeEauUserI");
	}


	
	@Override
	public boolean		on() throws Exception
	{
		return this.getOwner().handleRequest(o -> ((ChauffeEauUserI)o).on());
	}

	@Override
	public void			switchOn() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((ChauffeEauUserI)o).switchOn();;
									return null;
							});
	}

	
	@Override
	public void			switchOff() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((ChauffeEauUserI)o).switchOff();;
									return null;
							});
	}

	
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((ChauffeEauUserI)o).
												setTargetTemperature(target);
									return null;
							});
	}

	@Override
	public double		getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((ChauffeEauTemperatureI)o).
													getTargetTemperature());
	}

	
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((ChauffeEauTemperatureI)o).
													getCurrentTemperature());
	}

	
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((ChauffeEauExternalControlI)o).
														getCurrentPowerLevel());
	}

	
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((ChauffeEauExternalControlI)o).
														getMaxPowerLevel());
	}

	
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		this.getOwner().handleRequest(
							o -> { ((ChauffeEauExternalControlI)o).
											setCurrentPowerLevel(powerLevel);
									return null;
							});
	}

	

}
