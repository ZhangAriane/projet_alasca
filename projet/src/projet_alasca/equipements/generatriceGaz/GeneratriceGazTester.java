package projet_alasca.equipements.generatriceGaz;

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
import projet_alasca.equipements.generatriceGaz.GeneratriceGazI.GeneratriceGazState;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurMode;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutionException;

@RequiredInterfaces(required = {GeneratriceGazCI.class , ClocksServerCI.class})
public class GeneratriceGazTester
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
	protected GeneratriceGazOutboundPort generatriceGazOutboundPort;					
	protected String 	generatriceGazInboundPortURI;



	protected GeneratriceGazTester(boolean isUnitTest) throws Exception 
	{
		this(isUnitTest, GeneratriceGaz.INBOUND_PORT_URI);
	}

	public GeneratriceGazTester(boolean isUnitTest, String generatriceGazInboundPortURI) throws Exception
	{
		super(1, 0);
		assert	generatriceGazInboundPortURI != null &&
				!generatriceGazInboundPortURI.isEmpty() :
					new PreconditionException(
							"generatriceGazInboundPortURI != null && "
									+ "!generatriceGazInboundPortURI.isEmpty()");

		this.isUnitTest = isUnitTest;
		this.initialise(generatriceGazInboundPortURI);
	}


	protected	GeneratriceGazTester(
			boolean isUnitTest,
			String generatriceGazInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(generatriceGazInboundPortURI);
	}

	protected void		initialise(
			String generatriceGazInboundPortURI
			) throws Exception
	{
		this.generatriceGazInboundPortURI = generatriceGazInboundPortURI;
		this.generatriceGazOutboundPort = new GeneratriceGazOutboundPort(this);
		this.generatriceGazOutboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Generatrice Gaz tester component");
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
			assertEquals(GeneratriceGazState.OFF, this.generatriceGazOutboundPort.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}


	public void			testTurnOnOff() throws Exception
	{
		this.logMessage("testTurnOnOff()... ");
		if(this.generatriceGazOutboundPort.getState().equals(GeneratriceGazState.OFF)) {
			this.generatriceGazOutboundPort.turnOn();
			assertEquals(GeneratriceGazState.ON, this.generatriceGazOutboundPort.getState());
		}
		else {
			this.generatriceGazOutboundPort.turnOff();
			assertEquals(GeneratriceGazState.OFF, this.generatriceGazOutboundPort.getState());
		}
		this.logMessage("...done.");
	}



	protected void			runAllTests() throws Exception
	{
		this.testGetState();
		this.testTurnOnOff();
	}

	@Override
	public synchronized void	start()
			throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.generatriceGazOutboundPort.getPortURI(),
					generatriceGazInboundPortURI,
					GeneratriceGazConnector.class.getCanonicalName());
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
			this.traceMessage("Generatrice Gaz Tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
							CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			this.traceMessage("Generatrice Gaz Tester waits until start.\n");
			ac.waitUntilStart();
		}
		this.traceMessage("Generatrice Gaz Tester starts the tests.\n");
		this.runAllTests();
		this.traceMessage("Generatrice Gaz Tester ends.\n");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.generatriceGazOutboundPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.generatriceGazOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

}
