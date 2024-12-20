package projet_alasca.equipements.panneauSolaire;

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
import projet_alasca.equipements.panneauSolaire.PanneauSolaireI.PanneauSolaireState;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurMode;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutionException;

@RequiredInterfaces(required = {PanneauSolaireCI.class , ClocksServerCI.class})
public class PanneauSolaireTester
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
	protected PanneauSolaireOutboundPort psop;					
	protected String 	panneauSolaireInboundPortURI;



	protected PanneauSolaireTester(boolean isUnitTest) throws Exception 
	{
		this(isUnitTest, PanneauSolaire.INBOUND_PORT_URI);
	}

	public PanneauSolaireTester(boolean isUnitTest, String panneauSolaireInboundPortURI) throws Exception
	{
		super(1, 0);
		assert	panneauSolaireInboundPortURI != null &&
				!panneauSolaireInboundPortURI.isEmpty() :
					new PreconditionException(
							"panneauSolaireInboundPortURI != null && "
									+ "!panneauSolaireInboundPortURI.isEmpty()");

		this.isUnitTest = isUnitTest;
		this.initialise(panneauSolaireInboundPortURI);
	}


	protected	PanneauSolaireTester(
			boolean isUnitTest,
			String panneauSolaireInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(panneauSolaireInboundPortURI);
	}

	protected void		initialise(
			String panneauSolaireInboundPortURI
			) throws Exception
	{
		this.panneauSolaireInboundPortURI = panneauSolaireInboundPortURI;
		this.psop = new PanneauSolaireOutboundPort(this);
		this.psop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Panneau solaire tester component");
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
			assert( this.psop.isOn());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}


	public void			testSwitchState() throws Exception
	{
		this.logMessage("testSwitchState()... ");
		if(this.psop.isOn()) {
			this.psop.stopProduce();;
			assert( this.psop.isOn());
		}
		else {
			this.psop.startProduce();
			assert(!this.psop.isOn());
		}
		this.logMessage("...done.");
	}



	protected void			runAllTests() throws Exception
	{
		this.testGetState();
		this.testSwitchState();
		this.testSwitchState();
	}

	@Override
	public synchronized void	start()
			throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.psop.getPortURI(),
					panneauSolaireInboundPortURI,
					PanneauSolaireConnector.class.getCanonicalName());
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

			this.traceMessage("Panneau solaire Tester waits until start.\n");
			ac.waitUntilStart();
		}
		this.traceMessage("Panneau solaire  Tester starts the tests.\n");
		this.runAllTests();
		this.traceMessage("Panneau solaire  Tester ends.\n");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.psop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.psop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

}
