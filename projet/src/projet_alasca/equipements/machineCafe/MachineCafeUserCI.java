package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface MachineCafeUserCI extends OfferedCI, RequiredCI, MachineCafeImplementationI{


	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------


	@Override
	public CoffeeMachineState	getState() throws Exception;

	@Override
	public CoffeeMachineAction getMode() throws Exception;

	@Override
	public void setModeCoffee() throws Exception;

	@Override
	public void setModeLongCoffee() throws Exception;

	@Override
	public void setModeExpresso() throws Exception;

	@Override
	public void			turnOn() throws Exception;

	@Override
	public void			turnOff() throws Exception;
}
