package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class BatterieOutboundPort extends		AbstractOutboundPort implements BatterieCI{

	private static final long serialVersionUID = 1L;

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
	public BatterieState getState() throws Exception {
		return ((BatterieCI)this.getConnector()).getState();
	}

	@Override
	public void swicthConsume() throws Exception {
		((BatterieCI)this.getConnector()).swicthConsume();
		
	}

	@Override
	public void switchProduct() throws Exception {
		((BatterieCI)this.getConnector()).switchProduct();
		
	}

	

}
