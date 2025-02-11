package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class BatterieInboundPort extends		AbstractInboundPort implements BatterieCI{

	/*private static final long serialVersionUID = 1L;

	public BatterieInboundPort( ComponentI owner)
			throws Exception {
		super(BatterieCI.class, owner);
		
	}

	public				BatterieInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, BatterieCI.class, owner);
		
	}

	@Override
	public State getState() throws Exception{
		return this.getOwner().handleRequest(
				o -> ((BatterieI)o).getState());
	}*/
	
	
	private static final long serialVersionUID = 1L;

	public BatterieInboundPort(ComponentI owner) throws Exception {
		super(BatterieCI.class, owner);
	}

	public BatterieInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, BatterieCI.class, owner);
	}

	@Override
	public boolean isFull() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatterieI)o).isFull());
	}

	@Override
	public boolean isEmpty() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatterieI)o).isEmpty());
	}

	@Override
	public void ConsumeEnergy(double energy) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatterieI)o).ConsumeEnergy(energy);
						return null;
					 });
	}

	@Override
	public void provideEnergy(double energy) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatterieI)o).provideEnergy(energy);
						return null;
					 });
	}

	@Override
	public double getEnergyLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatterieI)o).getEnergyLevel());
	}

	




}
