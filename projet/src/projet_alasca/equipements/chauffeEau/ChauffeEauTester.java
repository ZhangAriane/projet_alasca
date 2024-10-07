package projet_alasca.equipements.chauffeEau;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import projet_alasca.equipements.chauffeEau.connections.*;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;


@RequiredInterfaces(required={ChauffeEauUserCI.class,
		ChauffeEauInternalControlCI.class,
		ChauffeEauExternalControlCI.class,
		ClocksServerCI.class})
public class ChauffeEauTester
extends AbstractComponent {
	
	
	public static final int		SWITCH_ON_DELAY = 2;
	public static final int		SWITCH_OFF_DELAY = 8;

	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

									
	protected final boolean		isUnitTest;
	protected String			ChauffeEauUserInboundPortURI;
	protected String			ChauffeEauInternalControlInboundPortURI;
	protected String			ChauffeEauExternalControlInboundPortURI;

									
	protected ChauffeEauUserOutboundPort			chop;					
	protected ChauffeEauInternalControlOutboundPort	chicop;
						
	protected ChauffeEauExternalControlOutboundPort	checop;

	
	
	protected			ChauffeEauTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 ChauffeEau.USER_INBOUND_PORT_URI,
			 ChauffeEau.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 ChauffeEau.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}
	
	
	protected			ChauffeEauTester(
			boolean isUnitTest,
			String ChauffeEauUserInboundPortURI,
			String ChauffeEauInternalControlInboundPortURI,
			String ChauffeEauExternalControlInboundPortURI
			) throws Exception
		{
			super(1, 1);
			this.isUnitTest = isUnitTest;
			this.initialise(ChauffeEauUserInboundPortURI,
					ChauffeEauInternalControlInboundPortURI,
					ChauffeEauExternalControlInboundPortURI);
		}

		
		protected			ChauffeEauTester(
			boolean isUnitTest,
			String reflectionInboundPortURI,
			String ChauffeEauUserInboundPortURI,
			String ChauffeEauInternalControlInboundPortURI,
			String ChauffeEauExternalControlInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 1);
			this.isUnitTest = isUnitTest;
			this.initialise(ChauffeEauUserInboundPortURI,
							ChauffeEauInternalControlInboundPortURI,
							ChauffeEauExternalControlInboundPortURI);
		}

		
		
		protected void		initialise(
			String ChauffeEauUserInboundPortURI,
			String ChauffeEauInternalControlInboundPortURI,
			String ChauffeEauExternalControlInboundPortURI
			) throws Exception
		{
			this.ChauffeEauUserInboundPortURI = ChauffeEauUserInboundPortURI;
			this.chop = new ChauffeEauUserOutboundPort(this);
			this.chop.publishPort();
			this.ChauffeEauInternalControlInboundPortURI =
										ChauffeEauInternalControlInboundPortURI;
			this.chicop = new ChauffeEauInternalControlOutboundPort(this);
			this.chicop.publishPort();
			this.ChauffeEauExternalControlInboundPortURI =
										ChauffeEauExternalControlInboundPortURI;
			this.checop = new ChauffeEauExternalControlOutboundPort(this);
			this.checop.publishPort();

			if (VERBOSE) {
				this.tracer.get().setTitle("ChauffeEau tester component");
				this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
													  Y_RELATIVE_POSITION);
				this.toggleTracing();
			}
		}

		// -------------------------------------------------------------------------
		// Component services implementation
		// -------------------------------------------------------------------------

		protected void		testSwitchOnSwitchOff()
		{
			this.traceMessage("testSwitchOnSwitchOff...\n");
			try {
				this.chop.switchOn();
			} catch (Exception e) {
				this.traceMessage("...KO. " + e + "\n");
				assertTrue(false);
			}
			try {
				this.chop.switchOff();
			} catch (Exception e) {
				this.traceMessage("...KO. " + e + "\n");
				assertTrue(false);
			}
			this.traceMessage("...testSwitchOnSwitchOff() done.\n");
		}

		protected void		testOn()
		{
			this.traceMessage("testOn()...\n");
			try {
				assertEquals(false, this.chop.on());
			} catch (Exception e) {
				this.traceMessage("...KO. " + e + "\n");
				assertTrue(false);
			}
			try {
				this.chop.switchOn();
				assertEquals(true, this.chop.on());
				this.chop.switchOff();
			} catch (Exception e) {
				this.traceMessage("...KO. " + e + "\n");
				assertTrue(false);
			}
			this.traceMessage("...testOn() done.\n");
		}

		protected void		testTargetTemperature()
		{
			this.traceMessage("testTargetTemperature()...\n");
			try {
				this.chop.setTargetTemperature(10.0);
				assertEquals(10.0, this.chop.getTargetTemperature());
				this.chop.setTargetTemperature(ChauffeEau.STANDARD_TARGET_TEMPERATURE);
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
				this.chop.switchOn();
				assertEquals(ChauffeEau.FAKE_CURRENT_TEMPERATURE,
							 this.chop.getCurrentTemperature());
				this.chop.switchOff();
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
				assertEquals(ChauffeEau.MAX_POWER_LEVEL,
							 this.chop.getMaxPowerLevel());
				this.chop.switchOn();
				this.chop.setCurrentPowerLevel(ChauffeEau.MAX_POWER_LEVEL/2.0);
				assertEquals(ChauffeEau.MAX_POWER_LEVEL/2.0,
							 this.chop.getCurrentPowerLevel());
				this.chop.switchOff();
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
				assertEquals(ChauffeEau.STANDARD_TARGET_TEMPERATURE,
							 this.chicop.getTargetTemperature());
				this.chop.switchOn();
				assertEquals(true, this.chop.on());
				assertEquals(ChauffeEau.FAKE_CURRENT_TEMPERATURE,
							 this.chicop.getCurrentTemperature());
				this.chicop.startHeating();
				assertTrue(this.chicop.heating());
				this.chicop.stopHeating();
				assertTrue(!this.chicop.heating());
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
				assertEquals(ChauffeEau.MAX_POWER_LEVEL,
							 this.checop.getMaxPowerLevel());
				this.checop.setCurrentPowerLevel(ChauffeEau.MAX_POWER_LEVEL/2.0);
				assertEquals(ChauffeEau.MAX_POWER_LEVEL/2.0,
							 this.checop.getCurrentPowerLevel());
			} catch (Exception e) {
				this.traceMessage("...KO. " + e + "\n");
				assertTrue(false);
			}
			this.traceMessage("...testExternalControl() done.\n");
		}

		protected void		runAllTests()
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
						this.chop.getPortURI(),
						this.ChauffeEauUserInboundPortURI,
						ChauffeEauUserConnector.class.getCanonicalName());
				this.doPortConnection(
						this.chicop.getPortURI(),
						ChauffeEauInternalControlInboundPortURI,
						ChauffeEauInternalControlConnector.class.getCanonicalName());
				this.doPortConnection(
						this.checop.getPortURI(),
						ChauffeEauExternalControlInboundPortURI,
						ChauffeEauExternalControlConnector.class.getCanonicalName());
			} catch (Exception e) {
				throw new ComponentStartException(e) ;
			}
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractComponent#execute()
		 */
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
				this.traceMessage("ChauffeEau tester gets the clock.\n");
				AcceleratedClock ac =
						clocksServerOutboundPort.getClock(
											CVMIntegrationTest.CLOCK_URI);
				this.doPortDisconnection(
							clocksServerOutboundPort.getPortURI());
				clocksServerOutboundPort.unpublishPort();
				clocksServerOutboundPort = null;

				Instant startInstant = ac.getStartInstant();
				Instant ChauffeEauSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
				Instant ChauffeEauSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
				this.traceMessage("ChauffeEau tester waits until start.\n");
				ac.waitUntilStart();
				this.traceMessage("ChauffeEau tester schedules switch on and off.\n");
				long delayToSwitchOn = ac.nanoDelayUntilInstant(ChauffeEauSwitchOn);
				long delayToSwitchOff = ac.nanoDelayUntilInstant(ChauffeEauSwitchOff);

				// This is to avoid mixing the 'this' of the task object with the 'this'
				// representing the component object in the code of the next methods run
				AbstractComponent o = this;

				// schedule the switch on ChauffeEau
				this.scheduleTaskOnComponent(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									o.traceMessage("ChauffeEau switches on.\n");
									chop.switchOn();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, delayToSwitchOn, TimeUnit.NANOSECONDS);

				// to be completed with a more covering scenario

				// schedule the switch off ChauffeEau
				this.scheduleTaskOnComponent(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									o.traceMessage("ChauffeEau switches off.\n");
									chop.switchOff();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, delayToSwitchOff, TimeUnit.NANOSECONDS);
			}
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
		 */
		@Override
		public synchronized void	finalise() throws Exception
		{
			this.doPortDisconnection(this.chop.getPortURI());
			this.doPortDisconnection(this.chicop.getPortURI());
			this.doPortDisconnection(this.checop.getPortURI());
			super.finalise();
		}

		/**
		 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
		 */
		@Override
		public synchronized void	shutdown() throws ComponentShutdownException
		{
			try {
				this.chop.unpublishPort();
				this.chicop.unpublishPort();
				this.checop.unpublishPort();
			} catch (Exception e) {
				throw new ComponentShutdownException(e) ;
			}
			super.shutdown();
		}
	
 
	


}
