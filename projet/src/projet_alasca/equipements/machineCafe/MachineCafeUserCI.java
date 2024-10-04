package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface MachineCafeUserCI extends OfferedCI, RequiredCI, MachineCafeImplementationI{


	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------


	public CofeeMachineState	getState() throws Exception;

	public Button getButton() throws Exception;

	public void			turnOn() throws Exception;


	public void			turnOff() throws Exception;
}
