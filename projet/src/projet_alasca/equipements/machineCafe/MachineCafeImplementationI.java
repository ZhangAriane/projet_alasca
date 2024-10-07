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


	public static enum CoffeeMachineAction{
		NONE,
		COFFEE,
		LONG_COFFEE,
		EXPRESSO
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------



	public CoffeeMachineState	getState() throws Exception;

	public CoffeeMachineAction getMode() throws Exception;

	public void setModeCoffee() throws Exception;

	public void setModeLongCoffee() throws Exception;

	public void setModeExpresso() throws Exception;

	public void			turnOn() throws Exception;

	public void			turnOff() throws Exception;

}
