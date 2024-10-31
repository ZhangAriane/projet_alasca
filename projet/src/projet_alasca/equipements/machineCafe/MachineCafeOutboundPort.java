package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class MachineCafeOutboundPort extends		AbstractOutboundPort implements MachineCafeUserCI{

	private static final long serialVersionUID = 1L;

	public MachineCafeOutboundPort(ComponentI owner)
			throws Exception {
		super(MachineCafeUserCI.class, owner);
	}

	public				MachineCafeOutboundPort(String uri, ComponentI owner)
			throws Exception
	{
		super(uri, MachineCafeUserCI.class, owner);
	}

	@Override
	public CoffeeMachineState getState() throws Exception {

		return ((MachineCafeUserCI)this.getConnector()).getState();
	}

	@Override
	public void turnOn() throws Exception {
		((MachineCafeUserCI)this.getConnector()).turnOn();

	}

	@Override
	public void turnOff() throws Exception {
		((MachineCafeUserCI)this.getConnector()).turnOff();

	}



}
