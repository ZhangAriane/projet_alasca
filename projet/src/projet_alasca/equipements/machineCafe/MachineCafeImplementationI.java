package projet_alasca.equipements.machineCafe;


public interface MachineCafeImplementationI {


	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static enum	CoffeeMachineState
	{
		/** coffee machine is on.												*/
		ON,
		/** coffee machine is off.												*/
		OFF
	}


	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------



	public CoffeeMachineState	getState() throws Exception;

	public void			turnOn() throws Exception;

	public void			turnOff() throws Exception;

}
