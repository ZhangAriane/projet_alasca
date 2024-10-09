package projet_alasca.equipements.refrigerateur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurExternalControlConnector;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurExternalControlOutboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurInternalControlConnector;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurInternalControlOutboundPort;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurUserConnector;
import projet_alasca.equipements.refrigerateur.connections.RefrigerateurUserOutboundPort;
import projet_alasca.equipements.generateurConnecteur.GenerateurConnecteur;
import projet_alasca.equipements.generateurConnecteur.GenereRefrigerateurConnecteur;

@RequiredInterfaces(required={RefrigerateurUserCI.class,
		RefrigerateurInternalControlCI.class,
		RefrigerateurExternalControlCI.class,
		ClocksServerCI.class})
public class RefrigerateurTester extends AbstractComponent {


	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/**	in clock-driven scenario, the delay from the start instant at which
	 *  the refrigerator is switched on.											*/
	public static final int		SWITCH_ON_DELAY = 2;
	/**	in clock-driven scenario, the delay from the start instant at which
	 *  the refrigerator is switched off.											*/
	public static final int		SWITCH_OFF_DELAY = 8;

	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int			X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean		isUnitTest;
	/** URI of the user component interface inbound port.					*/
	protected String			refrigerateurUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			refrigerateurInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			refrigerateurExternalControlInboundPortURI;

	/** user component interface inbound port.								*/
	protected RefrigerateurUserOutboundPort			refrigerateurUserOutboundPort;
	/** internal control component interface inbound port.					*/
	protected RefrigerateurInternalControlOutboundPort	refrigerateurInternalControlOutboundPort;
	/** external control component interface inbound port.					*/
	protected RefrigerateurExternalControlOutboundPort	refrigerateurExternalControlOutboundPort;





	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------


	protected			RefrigerateurTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
				Refrigerateur.USER_INBOUND_PORT_URI,
				Refrigerateur.INTERNAL_CONTROL_INBOUND_PORT_URI,
				Refrigerateur.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}


	protected			RefrigerateurTester(
			boolean isUnitTest,
			String refrigerateurUserInboundPortURI,
			String refrigerateurInternalControlInboundPortURI,
			String refrigerateurExternalControlInboundPortURI
			) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(refrigerateurUserInboundPortURI,
				refrigerateurInternalControlInboundPortURI,
				refrigerateurExternalControlInboundPortURI);
	}


	protected			RefrigerateurTester(
			boolean isUnitTest,
			String reflectionInboundPortURI,
			String refrigerateurUserInboundPortURI,
			String refrigerateurInternalControlInboundPortURI,
			String refrigerateurExternalControlInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(refrigerateurUserInboundPortURI,
				refrigerateurInternalControlInboundPortURI,
				refrigerateurExternalControlInboundPortURI);
	}

	protected void		initialise(
			String refrigerateurUserInboundPortURI,
			String refrigerateurInternalControlInboundPortURI,
			String refrigerateurExternalControlInboundPortURI
			) throws Exception
	{
		this.refrigerateurUserInboundPortURI = refrigerateurUserInboundPortURI;
		this.refrigerateurUserOutboundPort = new RefrigerateurUserOutboundPort(this);
		this.refrigerateurUserOutboundPort.publishPort();
		this.refrigerateurInternalControlInboundPortURI =
				refrigerateurInternalControlInboundPortURI;
		this.refrigerateurInternalControlOutboundPort = new RefrigerateurInternalControlOutboundPort(this);
		this.refrigerateurInternalControlOutboundPort.publishPort();
		this.refrigerateurExternalControlInboundPortURI =
				refrigerateurExternalControlInboundPortURI;
		this.refrigerateurExternalControlOutboundPort = new RefrigerateurExternalControlOutboundPort(this);
		this.refrigerateurExternalControlOutboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Refrigerator tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}



	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void		testSwitchOnSwitchOff() throws Exception
	{
		this.traceMessage("testSwitchOnSwitchOff...\n");

		if(!this.refrigerateurUserOutboundPort.on()) {
			this.refrigerateurUserOutboundPort.switchOn();
		}
		else {
			this.refrigerateurUserOutboundPort.switchOff();
		}

		this.traceMessage("...testSwitchOnSwitchOff() done.\n");
	}


	protected void		testOn() throws Exception
	{
		this.traceMessage("testOn()...\n");
		this.refrigerateurUserOutboundPort.on();
		this.traceMessage("...testOn() done.\n");
	}

	protected void		testTargetTemperature()
	{
		this.traceMessage("testTargetTemperature()...\n");
		try {
			this.logMessage("target :" + this.refrigerateurUserOutboundPort.getRefrigeratorTargetTemperature());
			this.refrigerateurUserOutboundPort.setRefrigeratorTargetTemperature(6.0);
//			this.logMessage("target :" + this.refrigerateurUserOutboundPort.getRefrigeratorTargetTemperature());
//			assertEquals(6.0, this.refrigerateurUserOutboundPort.getRefrigeratorTargetTemperature());
//			this.refrigerateurUserOutboundPort.setRefrigeratorTargetTemperature(Refrigerateur.STANDARD_REFRIGERATOR_TARGET_TEMPERATURE);

//			this.refrigerateurUserOutboundPort.setFreezerTargetTemperature(-15.0);
//			assertEquals(-15.0, this.refrigerateurUserOutboundPort.getFreezerTargetTemperature());
//			this.refrigerateurUserOutboundPort.setFreezerTargetTemperature(Refrigerateur.STANDARD_FREEZER_TARGET_TEMPERATURE);

		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		this.traceMessage("...testTargetTemperature() done.\n");

	}

	protected void		testCurrentTemperature()
	{
		this.traceMessage("testCurrentTemperature()...\n");
		try {
			this.refrigerateurUserOutboundPort.switchOn();
			this.logMessage("fake :" + Refrigerateur.FAKE_REFRIGERATOR_CURRENT_TEMPERATURE);
			this.logMessage("current :" + this.refrigerateurUserOutboundPort.getRefrigeratorCurrentTemperature());
			assertEquals(Refrigerateur.FAKE_REFRIGERATOR_CURRENT_TEMPERATURE,
					this.refrigerateurUserOutboundPort.getRefrigeratorCurrentTemperature());

			assertEquals(Refrigerateur.FAKE_FREEZER_CURRENT_TEMPERATURE,
					this.refrigerateurUserOutboundPort.getFreezerCurrentTemperature());
			this.refrigerateurUserOutboundPort.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		this.traceMessage("...testCurrentTemperature() done.\n");
	}

	protected void		testPowerLevel()
	{
		this.traceMessage("testPowerLevel()...\n");
		try {
			assertEquals(Refrigerateur.MAX_POWER_LEVEL,
					this.refrigerateurUserOutboundPort.getMaxPowerLevel());
			this.refrigerateurUserOutboundPort.switchOn();
			this.refrigerateurUserOutboundPort.setCurrentPowerLevel(Refrigerateur.MAX_POWER_LEVEL/2.0);
			assertEquals(Refrigerateur.MAX_POWER_LEVEL/2.0,
					this.refrigerateurUserOutboundPort.getCurrentPowerLevel());
			this.refrigerateurUserOutboundPort.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		this.traceMessage("...testPowerLevel() done.\n");
	}

	protected void		testInternalControl()
	{
		this.traceMessage("testInternalControl()...\n");
		try {
			assertEquals(Refrigerateur.STANDARD_REFRIGERATOR_TARGET_TEMPERATURE,
					this.refrigerateurInternalControlOutboundPort.getRefrigeratorTargetTemperature());
			assertEquals(Refrigerateur.STANDARD_FREEZER_TARGET_TEMPERATURE,
					this.refrigerateurInternalControlOutboundPort.getFreezerTargetTemperature());

			this.refrigerateurUserOutboundPort.switchOn();
			assertEquals(true, this.refrigerateurUserOutboundPort.on());

			assertEquals(Refrigerateur.FAKE_REFRIGERATOR_CURRENT_TEMPERATURE,
					this.refrigerateurInternalControlOutboundPort.getRefrigeratorCurrentTemperature());
			this.refrigerateurInternalControlOutboundPort.startCooling();
			assertTrue(this.refrigerateurInternalControlOutboundPort.cooling());
			this.refrigerateurInternalControlOutboundPort.stopCooling();
			assertTrue(!this.refrigerateurInternalControlOutboundPort.cooling());

			assertEquals(Refrigerateur.STANDARD_FREEZER_TARGET_TEMPERATURE,
					this.refrigerateurInternalControlOutboundPort.getFreezerCurrentTemperature());
			this.refrigerateurInternalControlOutboundPort.startFreezing();
			assertTrue(this.refrigerateurInternalControlOutboundPort.freezing());
			this.refrigerateurInternalControlOutboundPort.stopFreezing();
			assertTrue(!this.refrigerateurInternalControlOutboundPort.freezing());

		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		this.traceMessage("...testInternalControl() done.\n");
	}


	protected void		testExternalControl()
	{
		this.traceMessage("testExternalControl()...\n");
		try {
			assertEquals(Refrigerateur.MAX_POWER_LEVEL,
					this.refrigerateurExternalControlOutboundPort.getMaxPowerLevel());
			this.refrigerateurExternalControlOutboundPort.setCurrentPowerLevel(Refrigerateur.MAX_POWER_LEVEL/2.0);
			assertEquals(Refrigerateur.MAX_POWER_LEVEL/2.0,
					this.refrigerateurExternalControlOutboundPort.getCurrentPowerLevel());
		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		this.traceMessage("...testExternalControl() done.\n");
	}


	protected void		runAllTests() throws Exception
	{
		this.testSwitchOnSwitchOff();
		this.testOn();
		this.testTargetTemperature();
		this.testCurrentTemperature();
		this.testPowerLevel();
		this.testInternalControl();
		this.testExternalControl();
	}


	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			
			this.doPortConnection(
					this.refrigerateurUserOutboundPort.getPortURI(),
					this.refrigerateurUserInboundPortURI,
//					RefrigerateurUserConnector.class.getCanonicalName()
					GenereRefrigerateurConnecteur.genereRefrigeratorUserConnector().getCanonicalName()
					);
			this.doPortConnection(
					this.refrigerateurInternalControlOutboundPort.getPortURI(),
					this.refrigerateurInternalControlInboundPortURI,
//					RefrigerateurInternalControlConnector.class.getCanonicalName()
					GenereRefrigerateurConnecteur.genereRefrigeratorInternalControlConnector().getCanonicalName()
					);
			this.doPortConnection(
					this.refrigerateurExternalControlOutboundPort.getPortURI(),
					this.refrigerateurExternalControlInboundPortURI,
//					RefrigerateurExternalControlConnector.class.getCanonicalName()
					GenereRefrigerateurConnecteur.genereRefrigeratorExternalControlConnector().getCanonicalName()
					);
			
			

		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}


	@Override
	public synchronized void	execute() throws Exception
	{
		if (this.isUnitTest) {
			this.runAllTests();
		} else {
			ClocksServerOutboundPort clocksServerOutboundPort =
					new ClocksServerOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			

			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.traceMessage("Refrigerator tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
							CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(
					clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			Instant startInstant = ac.getStartInstant();
			Instant RefrigerateurSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
			Instant RefrigerateurSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
			this.traceMessage("Refrigerator tester waits until start.\n");
			ac.waitUntilStart();
			this.traceMessage("Refrigerator tester schedules switch on and off.\n");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(RefrigerateurSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(RefrigerateurSwitchOff);

			// This is to avoid mixing the 'this' of the task object with the 'this'
			// representing the component object in the code of the next methods run
//			AbstractComponent o = this;

			// schedule the switch on refrigerator
//			this.scheduleTaskOnComponent(
//					new AbstractComponent.AbstractTask() {
//						@Override
//						public void run() {
//							try {
//								o.traceMessage("Refrigerator switches on.\n");
//								refrigerateurUserOutboundPort.switchOn();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}, delayToSwitchOn, TimeUnit.NANOSECONDS);

			// to be completed with a more covering scenario

			// schedule the switch off refrigerator
//			this.scheduleTaskOnComponent(
//					new AbstractComponent.AbstractTask() {
//						@Override
//						public void run() {
//							try {
//								o.traceMessage("Refrigerator switches off.\n");
//								refrigerateurUserOutboundPort.switchOff();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
//			
			
			this.scheduleTask(
					o -> {
						try {
							this.traceMessage("Refrigerator switches on.\n");
						refrigerateurUserOutboundPort.switchOn();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);
			
			this.scheduleTask(
					o -> {
						try {
							this.traceMessage("Refrigerator switches off.\n");
						refrigerateurUserOutboundPort.switchOff();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		}
	}


	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.refrigerateurUserOutboundPort.getPortURI());
		this.doPortDisconnection(this.refrigerateurInternalControlOutboundPort.getPortURI());
		this.doPortDisconnection(this.refrigerateurExternalControlOutboundPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.refrigerateurUserOutboundPort.unpublishPort();
			this.refrigerateurInternalControlOutboundPort.unpublishPort();
			this.refrigerateurExternalControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


}
