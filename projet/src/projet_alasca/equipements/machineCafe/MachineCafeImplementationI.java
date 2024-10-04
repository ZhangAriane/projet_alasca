package projet_alasca.equipements.machineCafe;


public interface MachineCafeImplementationI {


	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static enum	CofeeMachineState
	{
		/** projector is on.												*/
		ON,
		/** projector is off.												*/
		OFF
	}
	
	
	public static enum Button{
		None,
		Cofee,
		LongCofee,
		Expresso
	}
	
	// -------------------------------------------------------------------------
		// Component services signatures
		// -------------------------------------------------------------------------

	

	public CofeeMachineState	getState() throws Exception;
	
	public Button getButton() throws Exception;

	public void			turnOn() throws Exception;
	
	
	public void			turnOff() throws Exception;

}
