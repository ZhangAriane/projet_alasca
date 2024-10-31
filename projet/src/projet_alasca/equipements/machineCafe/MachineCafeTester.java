package projet_alasca.equipements.machineCafe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import projet_alasca.equipements.machineCafe.MachineCafeImplementationI.CoffeeMachineState;

@RequiredInterfaces(required = {MachineCafeUserCI.class, ClocksServerCI.class})
public class MachineCafeTester extends AbstractComponent{

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean				VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int					X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int					Y_RELATIVE_POSITION = 0;

	/* when true, the component performs a unit test.						*/
	protected final boolean				isUnitTest;
	/* outbound port connecting to the coffee machine component.				*/
	protected MachineCafeOutboundPort		machineCafeOutboundPort;
	/* URI of the coffee machine inbound port to connect to.					*/
	protected String					machineCafeInboundPortURI;



	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			MachineCafeTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, MachineCafe.INBOUND_PORT_URI);
	}

	protected			MachineCafeTester(
			boolean isUnitTest,
			String machineCafeInboundPortURI
			) throws Exception
	{
		super(1, 0);

		assert	machineCafeInboundPortURI != null &&
				!machineCafeInboundPortURI.isEmpty() :
					new PreconditionException(
							"machineCafeInboundPortURI != null && "
									+ "!machineCafeInboundPortURI.isEmpty()");

		this.isUnitTest = isUnitTest;
		this.initialise(machineCafeInboundPortURI);
	}


	protected			MachineCafeTester(
			boolean isUnitTest,
			String machineCafeInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(machineCafeInboundPortURI);
	}

	protected void		initialise(
			String machineCafeInboundPortURI
			) throws Exception
	{
		this.machineCafeInboundPortURI = machineCafeInboundPortURI;
		this.machineCafeOutboundPort = new MachineCafeOutboundPort(this);
		this.machineCafeOutboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Machine Cafe tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}


	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void			testGetState()
	{
		this.logMessage("testGetState()... ");
		try {
			assertEquals(CoffeeMachineState.OFF, this.machineCafeOutboundPort.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}


	public void			testTurnOnOff() throws Exception
	{
		this.logMessage("testTurnOnOff()... ");

		if(this.machineCafeOutboundPort.getState().equals(CoffeeMachineState.OFF)) {
			this.machineCafeOutboundPort.turnOn();
			assertEquals(CoffeeMachineState.ON, this.machineCafeOutboundPort.getState());
		}
		else {
			this.machineCafeOutboundPort.turnOff();
			assertEquals(CoffeeMachineState.OFF, this.machineCafeOutboundPort.getState());
		}
		this.logMessage("...done.");
	}



	protected void			runAllTests() throws Exception
	{
		this.testTurnOnOff();
		this.testTurnOnOff();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start()
			throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.machineCafeOutboundPort.getPortURI(),
					machineCafeInboundPortURI,
					MachineCafeConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void execute() throws Exception
	{
		if (!this.isUnitTest) {
			ClocksServerOutboundPort clocksServerOutboundPort =
					new ClocksServerOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.traceMessage("Coffee Machine Tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
							CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			this.traceMessage("Coffee Machine Tester waits until start.\n");
			ac.waitUntilStart();
		}
		this.traceMessage("Coffee Machine Tester starts the tests.\n");
		this.runAllTests();
		this.traceMessage("Coffee Machine Tester ends.\n");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.machineCafeOutboundPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.machineCafeOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

}
