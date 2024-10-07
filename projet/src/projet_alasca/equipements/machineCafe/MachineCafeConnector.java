package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class MachineCafeConnector extends		AbstractConnector implements MachineCafeUserCI{

	@Override
	public CoffeeMachineState getState() throws Exception {
		return ((MachineCafeUserCI)this.offering).getState();
	}

	@Override
	public CoffeeMachineAction getMode() throws Exception {
		return ((MachineCafeUserCI)this.offering).getMode();
	}


	@Override
	public void setModeCoffee() throws Exception {
		((MachineCafeUserCI)this.offering).setModeCoffee();

	}

	@Override
	public void setModeLongCoffee() throws Exception {
		((MachineCafeUserCI)this.offering).setModeLongCoffee();

	}

	@Override
	public void setModeExpresso() throws Exception {
		((MachineCafeUserCI)this.offering).setModeExpresso();

	}

	@Override
	public void turnOn() throws Exception {
		((MachineCafeUserCI)this.offering).turnOn();

	}

	@Override
	public void turnOff() throws Exception {
		((MachineCafeUserCI)this.offering).turnOff();

	}



}
