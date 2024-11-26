package projet_alasca.equipements.batterie;

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
import projet_alasca.equipements.batterie.BatterieI.BatterieState;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredInterfaces(required = {BatterieCI.class , ClocksServerCI.class})
public class BatterieTester
extends AbstractComponent
{

	/** when true, methods trace their actions.								*/
	public static boolean	VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int	X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int	Y_RELATIVE_POSITION = 0;

	/* when true, the component performs a unit test.						*/
	protected final boolean	isUnitTest;				
	protected BatterieOutboundPort batterieOutboundPort;					
	protected String 	batterieInboundPortURI;



	protected BatterieTester(boolean isUnitTest) throws Exception 
	{
		this(isUnitTest, Batterie.INBOUND_PORT_URI);
	}

	public BatterieTester(boolean isUnitTest, String batterieInboundPortURI) throws Exception
	{
		super(1, 0);
		assert	batterieInboundPortURI != null &&
				!batterieInboundPortURI.isEmpty() :
					new PreconditionException(
							"batterieInboundPortURI != null && "
									+ "!batterieInboundPortURI.isEmpty()");

		this.isUnitTest = isUnitTest;
		this.initialise(batterieInboundPortURI);
	}


	protected	BatterieTester(
			boolean isUnitTest,
			String batterieInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(batterieInboundPortURI);
	}

	protected void		initialise(
			String ventilateurInboundPortURI
			) throws Exception
	{
		this.batterieInboundPortURI = ventilateurInboundPortURI;
		this.batterieOutboundPort = new BatterieOutboundPort(this);
		this.batterieOutboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Batterie tester component");
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
			assertEquals(BatterieState.PRODUCT, this.batterieOutboundPort.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}



	public void			testSwitchState() throws Exception
	{
		this.logMessage("testSwitchState()... ");
		if(this.batterieOutboundPort.getState().equals(BatterieState.PRODUCT)) {
			this.batterieOutboundPort.swicthConsume();
			assertEquals(BatterieState.CONSUME, this.batterieOutboundPort.getState());
		}
		else {
			this.batterieOutboundPort.switchProduct();;;
			assertEquals(BatterieState.PRODUCT, this.batterieOutboundPort.getState());
		}
		this.logMessage("...done.");
	}



	protected void			runAllTests() throws Exception
	{
		this.testGetState();
		this.testSwitchState();
	}

	@Override
	public synchronized void	start()
			throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.batterieOutboundPort.getPortURI(),
					batterieInboundPortURI,
					BatterieConnector.class.getCanonicalName());
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
			this.traceMessage("Batterie Tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
							CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			this.traceMessage("Batterie Tester waits until start.\n");
			ac.waitUntilStart();
		}
		this.traceMessage("Batterie Tester starts the tests.\n");
		this.runAllTests();
		this.traceMessage("Batterie Tester ends.\n");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.batterieOutboundPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.batterieOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

}
