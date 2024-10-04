package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;

//@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered={MachineCafeUserCI.class})
public class MachineCafe extends AbstractComponent implements MachineCafeImplementationI{

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------



	/** URI of the projector inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI = "PROJECTEUR-INBOUND-PORT-URI";

	/** when tracing, x coordinate of the window relative position.			*/
	public static int					X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int					Y_RELATIVE_POSITION = 0;

	/** initial state of the cofee machine.									*/
	public static final CofeeMachineState	INITIAL_STATE = CofeeMachineState.OFF;
	
	/** initial state of the projector.									*/
	public static final Button	INITIAL_BUTTON_ACTIVE = Button.None;

	/** current state (on, off) of the projector.							*/
	protected CofeeMachineState		currentState;
	
	/** current state (on, off) of the projector.							*/
	protected Button	buttonActive;
	
	/** inbound port offering the <code>ProjecteurCI</code> interface.		*/
	protected MachineCafeInboundPort	projecteurInboundPort;


	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	
	
	protected MachineCafe() throws Exception{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}
	
	protected MachineCafe(String MachineCafeInboundPortURI) {
		super(1,0);
		this.initialise(MachineCafeInboundPortURI);
	}

	private void initialise(String machineCafeInboundPortURI) {
		// TODO Auto-generated method stub
		
	}

	protected MachineCafe(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

	@Override
	public CofeeMachineState getState() throws Exception {
		return this.currentState;
	}
	
	public Button getButton() throws Exception{
		return this.buttonActive;
	}


	@Override
	public void turnOn() throws Exception {
		this.currentState = CofeeMachineState.ON;

	}

	@Override
	public void turnOff() throws Exception {
		this.currentState = CofeeMachineState.OFF;

	}

}
