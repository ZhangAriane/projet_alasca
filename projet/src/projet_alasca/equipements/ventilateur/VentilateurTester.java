package projet_alasca.equipements.ventilateur;

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
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurMode;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutionException;

@RequiredInterfaces(required = {VentilateurUserCI.class , ClocksServerCI.class})
public class VentilateurTester
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
	protected VentilateurOutboundPort vop;					
	protected String 	VentilateurInboundPortURI;
	
	
	
	protected VentilateurTester(boolean isUnitTest) throws Exception 
	{
		this(isUnitTest, Ventilateur.INBOUND_PORT_URI);
	}

	public VentilateurTester(boolean isUnitTest, String VentilateurInboundPortURI) throws Exception
    {
		super(1, 0);
		assert	VentilateurInboundPortURI != null &&
				!VentilateurInboundPortURI.isEmpty() :
				new PreconditionException(
				"VentilateurInboundPortURI != null && "
				+ "!VentilateurInboundPortURI.isEmpty()");
		
		this.isUnitTest = isUnitTest;
		this.initialise(VentilateurInboundPortURI);
	}
	
	
	protected	VentilateurTester(
			boolean isUnitTest,
			String ventilateurInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

			this.isUnitTest = isUnitTest;
			this.initialise(ventilateurInboundPortURI);
		}
	
	protected void		initialise(
			String ventilateurInboundPortURI
			) throws Exception
		{
			this.VentilateurInboundPortURI = ventilateurInboundPortURI;
			this.vop = new VentilateurOutboundPort(this);
			this.vop.publishPort();

			if (VERBOSE) {
				this.tracer.get().setTitle("Ventilateur tester component");
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
				assertEquals(VentilateurState.OFF, this.vop.getState());
			} catch (Exception e) {
				this.logMessage("...KO.");
				assertTrue(false);
			}
			this.logMessage("...done.");
		}
		
		public void			testGetMode()
		{
			this.logMessage("testGetMode()... ");
			try {
				assertEquals(VentilateurMode.LOW, this.vop.getMode());
			} catch (Exception e) {
				assertTrue(false);
			}
			this.logMessage("...done.");
		}
		
		
		public void			testTurnOnOff()
		{
			this.logMessage("testTurnOnOff()... ");
			try {
				assertEquals(VentilateurState.OFF, this.vop.getState());
				this.vop.turnOn();
				assertEquals(VentilateurState.ON, this.vop.getState());
				assertEquals(VentilateurMode.LOW, this.vop.getMode());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				assertThrows(ExecutionException.class,
							 () -> this.vop.turnOn());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				this.vop.turnOff();
				assertEquals(VentilateurState.OFF, this.vop.getState());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				assertThrows(ExecutionException.class,
							 () -> this.vop.turnOff());
			} catch (Exception e) {
				assertTrue(false);
			}
			this.logMessage("...done.");
		}
		
		
		public void			testSetLowHigh()
		{
			this.logMessage("testSetLowHigh()... ");
			try {
				this.vop.turnOn();
				this.vop.setHigh();
				assertEquals(VentilateurState.ON, this.vop.getState());
				assertEquals(VentilateurMode.HIGH, this.vop.getMode());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				assertThrows(ExecutionException.class,
							 () -> this.vop.setHigh());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				this.vop.setLow();
				assertEquals(VentilateurState.ON, this.vop.getState());
				assertEquals(VentilateurMode.LOW, this.vop.getMode());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				assertThrows(ExecutionException.class,
							 () -> this.vop.setLow());
			} catch (Exception e) {
				assertTrue(false);
			}
			try {
				this.vop.turnOff();
			} catch (Exception e) {
				assertTrue(false);
			}
			this.logMessage("...done.");
		}
		
		protected void			runAllTests()
		{
			this.testGetState();
			this.testGetMode();
			this.testTurnOnOff();
			this.testSetLowHigh();
		}

		@Override
		public synchronized void	start()
		throws ComponentStartException
		{
			super.start();

			try {
				this.doPortConnection(
								this.vop.getPortURI(),
								VentilateurInboundPortURI,
								VentilateurConnector.class.getCanonicalName());
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
				this.traceMessage("Ventilateur Tester gets the clock.\n");
				AcceleratedClock ac =
						clocksServerOutboundPort.getClock(
											CVMIntegrationTest.CLOCK_URI);
				this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
				clocksServerOutboundPort.unpublishPort();
				clocksServerOutboundPort = null;

				this.traceMessage("Ventilateur Tester waits until start.\n");
				ac.waitUntilStart();
			}
			this.traceMessage("Ventilateur Tester starts the tests.\n");
			this.runAllTests();
			this.traceMessage("Ventilateur Tester ends.\n");
		}

		@Override
		public synchronized void	finalise() throws Exception
		{
			this.doPortDisconnection(this.vop.getPortURI());
			super.finalise();
		}
		
		@Override
		public synchronized void	shutdown() throws ComponentShutdownException
		{
			try {
				this.vop.unpublishPort();
			} catch (Exception e) {
				throw new ComponentShutdownException(e) ;
			}
			super.shutdown();
		}
		
}
