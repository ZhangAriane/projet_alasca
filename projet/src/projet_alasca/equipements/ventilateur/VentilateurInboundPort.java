package projet_alasca.equipements.ventilateur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;


public class VentilateurInboundPort 
extends AbstractInboundPort
implements VentilateurUserCI
{

	private static final long serialVersionUID = 1L;
	
	
	
	public VentilateurInboundPort(ComponentI owner) throws Exception 
	{
		super(VentilateurUserCI.class, owner);
		assert	owner instanceof VentilateurImplementationI :
			new PreconditionException(
					"owner instanceof VentilateurImplementationI");
	}
	
	
	
	public				VentilateurInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, VentilateurUserCI.class, owner);
			assert	owner instanceof VentilateurImplementationI :
					new PreconditionException(
							"owner instanceof VentilateurImplementationI");
		}


	
	
	
	@Override
	public VentilateurState getState() throws Exception 
	{
		return this.getOwner().handleRequest(
				o -> ((VentilateurImplementationI)o).getState());

	}

	@Override
	public VentilateurMode getMode() throws Exception 
	{
		return this.getOwner().handleRequest(
				o -> ((VentilateurImplementationI)o).getMode());
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((VentilateurImplementationI)o).turnOn();
						return null;
				});
		
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((VentilateurImplementationI)o).turnOff();
						return null;
				});
		
	}

	@Override
	public void setHigh() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((VentilateurImplementationI)o).setHigh();;
						return null;
				});
		
	}

	@Override
	public void setMedium() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((VentilateurImplementationI)o).setMedium();
						return null;
				});
		
	}

	@Override
	public void setLow() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((VentilateurImplementationI)o).setLow();
						return null;
				});
		
	}

}
