package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class MachineCafeInboundPort extends		AbstractInboundPort implements MachineCafeUserCI{

	private static final long serialVersionUID = 1L;

	public MachineCafeInboundPort( ComponentI owner)
			throws Exception {
		super(MachineCafeUserCI.class, owner);
		assert	owner instanceof MachineCafeImplementationI :
			new PreconditionException(
					"owner instanceof ProjecteurImplementationI");
	}

	public				MachineCafeInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, MachineCafeUserCI.class, owner);
		assert	owner instanceof MachineCafeImplementationI :
			new PreconditionException(
					"owner instanceof ProjecteurImplementationI");
	}

	@Override
	public CoffeeMachineState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((MachineCafeImplementationI)o).getState());
	}

	@Override
	public CoffeeMachineAction getMode() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((MachineCafeImplementationI)o).getMode());
	}


	@Override
	public void setModeCoffee() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MachineCafeImplementationI)o).setModeCoffee();
				return null;
				});

	}

	@Override
	public void setModeLongCoffee() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MachineCafeImplementationI)o).setModeLongCoffee();
				return null;
				});

	}

	@Override
	public void setModeExpresso() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MachineCafeImplementationI)o).setModeExpresso();
				return null;
				});

	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MachineCafeImplementationI)o).turnOn();
				return null;
				});

	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MachineCafeImplementationI)o).turnOff();
				return null;
				});
	}



}
