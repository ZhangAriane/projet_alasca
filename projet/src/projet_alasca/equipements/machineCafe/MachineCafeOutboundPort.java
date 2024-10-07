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
	public CoffeeMachineAction getMode() throws Exception {
		return ((MachineCafeUserCI)this.getConnector()).getMode();
	}


	@Override
	public void setModeCoffee() throws Exception {
		((MachineCafeUserCI)this.getConnector()).setModeCoffee();

	}

	@Override
	public void setModeLongCoffee() throws Exception {
		((MachineCafeUserCI)this.getConnector()).setModeLongCoffee();

	}

	@Override
	public void setModeExpresso() throws Exception {
		((MachineCafeUserCI)this.getConnector()).setModeExpresso();

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
