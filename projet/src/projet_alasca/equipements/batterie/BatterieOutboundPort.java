package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class BatterieOutboundPort extends		AbstractOutboundPort implements BatterieCI{

	/*private static final long serialVersionUID = 1L;

	public BatterieOutboundPort(ComponentI owner)
			throws Exception {
		super(BatterieCI.class, owner);
	}

	public				BatterieOutboundPort(String uri, ComponentI owner)
			throws Exception
	{
		super(uri, BatterieCI.class, owner);
	}

	@Override
	public State getState() throws Exception {
		return ((BatterieCI)this.getConnector()).getState();
	}*/
	
	private static final long serialVersionUID = 1L;

	public BatterieOutboundPort(ComponentI owner) throws Exception {
		super(BatterieCI.class, owner);
	}

	public BatterieOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, BatterieCI.class, owner);
	}

	@Override
	public boolean isFull() throws Exception {
		return ((BatterieCI)this.getConnector()).isFull();
	}

	@Override
	public boolean isEmpty() throws Exception {
		return ((BatterieCI)this.getConnector()).isEmpty();
	}

	@Override
	public void ConsumeEnergy(double energy) throws Exception {
		((BatterieCI)this.getConnector()).ConsumeEnergy(energy);
	}

	@Override
	public void provideEnergy(double energy) throws Exception {
		((BatterieCI)this.getConnector()).provideEnergy(energy);
	}

	@Override
	public double getEnergyLevel() throws Exception {
		return ((BatterieCI)this.getConnector()).getEnergyLevel();
	}

	

}
