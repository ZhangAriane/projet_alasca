package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class MachineCafeConnector extends		AbstractConnector implements MachineCafeUserCI{

	@Override
	public CoffeeMachineState getState() throws Exception {
		return ((MachineCafeUserCI)this.offering).getState();
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
