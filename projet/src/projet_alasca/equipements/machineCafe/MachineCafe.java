package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered={MachineCafeUserCI.class})
public class MachineCafe extends AbstractComponent implements MachineCafeImplementationI{

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/** URI of the coffee machine inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI = "MACHINE-CAFE-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static boolean				VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int					X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int					Y_RELATIVE_POSITION = 0;

	/** initial state of the coffee machine.									*/
	public static final CoffeeMachineState	INITIAL_STATE = CoffeeMachineState.OFF;

	/** current state (on, off) of the coffee machine .							*/
	protected CoffeeMachineState		currentState;


	/** inbound port offering the <code>MachineCafeCI</code> interface.		*/
	protected MachineCafeInboundPort	machineCafeInboundPort;


	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------



	protected MachineCafe() throws Exception{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}

	protected MachineCafe(String MachineCafeInboundPortURI) throws Exception{
		super(1,0);
		this.initialise(MachineCafeInboundPortURI);
	}


	protected			MachineCafe(
			String MachineCafeInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(MachineCafeInboundPortURI);
	}




	protected void		initialise(String MachineCafeInboundPortURI)
			throws Exception
	{
		assert	MachineCafeInboundPortURI != null :
			new PreconditionException(
					"machineCafeInboundPort != null");
		assert	!MachineCafeInboundPortURI.isEmpty() :
			new PreconditionException(
					"!machineCafeInboundPort.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.machineCafeInboundPort = new MachineCafeInboundPort(MachineCafeInboundPortURI, this);
		this.machineCafeInboundPort.publishPort();

		if (MachineCafe.VERBOSE) {
			this.tracer.get().setTitle("Coffee machine component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}


	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.machineCafeInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------


	@Override
	public CoffeeMachineState getState() throws Exception {
		if (MachineCafe.VERBOSE) {
			this.traceMessage("Coffee machine returns its state : " +
					this.currentState + ".\n");
		}
		return this.currentState;
	}


	@Override
	public void turnOn() throws Exception {
		if (MachineCafe.VERBOSE) {
			this.traceMessage("Coffee machine is turned on.\n");
		}

		assert	this.getState() == CoffeeMachineState.OFF :
			new PreconditionException("getState() == CoffeeMachineState.OFF");

		this.currentState = CoffeeMachineState.ON;

	}

	@Override
	public void turnOff() throws Exception {
		if (MachineCafe.VERBOSE) {
			this.traceMessage("Coffee machine is turned off.\n");
		}

		assert	this.getState() == CoffeeMachineState.ON :
			new PreconditionException("getState() == CoffeeMachineState.ON");

		this.currentState = CoffeeMachineState.OFF;

	}



}
