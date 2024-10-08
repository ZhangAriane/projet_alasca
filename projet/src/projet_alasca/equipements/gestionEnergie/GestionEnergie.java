package projet_alasca.equipements.gestionEnergie;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2024e1.equipments.hem.AdjustableOutboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import projet_alasca.equipements.refrigerateur.Refrigerateur;
import projet_alasca.equipements.refrigerateur.RefrigerateurTester;
import projet_alasca.equipements.refrigerateur.connections.RegistrationInboundPort;

@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
		ClocksServerCI.class})
@OfferedInterfaces(offered={RegistrationCI.class})
public class GestionEnergie extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean					VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int						X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int						Y_RELATIVE_POSITION = 0;

	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort		meterop;

	/** when true, manage the refrigerator in a customised way, otherwise let
	 *  it register itself as an adjustable appliance.						*/
	protected boolean						isPreFirstStep;
	/** port to connect to the refrigerator when managed in a customised way.		*/
	protected AdjustableOutboundPort		refrigerateurExternalControlOutboundPort;

	/** when true, this implementation of the HEM performs the tests
	 *  that are planned in the method execute.								*/
	protected boolean						performTest;
	/** accelerated clock used for the tests.								*/
	protected AcceleratedClock				ac;

	protected RegistrationInboundPort registrationInboundPort;
	


	protected 			GestionEnergie()
	{
		// by default, perform the tests planned in the method execute.
		this(true);
	}

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param performTest	if {@code true}, the HEM performs the planned tests, otherwise not.
	 */
	protected 			GestionEnergie(boolean performTest)
	{
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.performTest = performTest;

		// by default, consider this execution as one in the pre-first step
		// and manage the heater in a customised way.
		this.isPreFirstStep = true;

		if (VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
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
			this.meterop = new ElectricMeterOutboundPort(this);
			this.meterop.publishPort();
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());

			if (this.isPreFirstStep) {
				// in this case, connect using the statically customised
				// heater connector and keep a specific outbound port to
				// call the heater.
				this.refrigerateurExternalControlOutboundPort = new AdjustableOutboundPort(this);
				this.refrigerateurExternalControlOutboundPort.publishPort();
				this.doPortConnection(
						this.refrigerateurExternalControlOutboundPort.getPortURI(),
						Refrigerateur.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						RefrigerateurConnector.class.getCanonicalName());
			}
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}



	@Override
	public synchronized void	execute() throws Exception
	{
		// First, get the clock and wait until the start time that it specifies.
		this.ac = null;
		ClocksServerOutboundPort clocksServerOutboundPort =
				new ClocksServerOutboundPort(this);
		clocksServerOutboundPort.publishPort();
		this.doPortConnection(
				clocksServerOutboundPort.getPortURI(),
				ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
		this.traceMessage("HEM gets the clock.\n");
		this.ac = clocksServerOutboundPort.getClock(CVMIntegrationTest.CLOCK_URI);
		this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
		clocksServerOutboundPort.unpublishPort();
		this.traceMessage("HEM waits until start time.\n");
		this.ac.waitUntilStart();
		this.traceMessage("HEM starts.\n");

		if (this.performTest) {
			this.testMeter();
			if (this.isPreFirstStep) {
				this.testRefrigerateur();
			}
		}
	}


	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.meterop.getPortURI());
		if (this.isPreFirstStep) {
			this.doPortDisconnection(this.refrigerateurExternalControlOutboundPort.getPortURI());
		}
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.meterop.unpublishPort();
			if (this.isPreFirstStep) {
				this.refrigerateurExternalControlOutboundPort.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * test the {@code EldctricMeter} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		testMeter() throws Exception
	{
		// simplified integration testing for meter, no time scenario.
		this.traceMessage("Electric meter current consumption? " +
				this.meterop.getCurrentConsumption() + "\n");
		this.traceMessage("Electric meter current production? " +
				this.meterop.getCurrentProduction() + "\n");
	}

	/**
	 * test the {@code Heater} component, in cooperation with the
	 * {@code HeaterTester} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		testRefrigerateur()
	{
		// Test for the heater
		Instant heaterTestStart =
				this.ac.getStartInstant().plusSeconds(
						(RefrigerateurTester.SWITCH_ON_DELAY +
								RefrigerateurTester.SWITCH_OFF_DELAY)/2);
		this.traceMessage("HEM schedules the heater test.\n");
		long delay = this.ac.nanoDelayUntilInstant(heaterTestStart);

		// schedule the switch on heater in one second
		this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							traceMessage("Refrigerator maxMode index? " +
									refrigerateurExternalControlOutboundPort.maxMode() + "\n");
							traceMessage("Refrigerator current mode index? " +
									refrigerateurExternalControlOutboundPort.currentMode() + "\n");
							traceMessage("Refrigerator going down one mode? " +
									refrigerateurExternalControlOutboundPort.downMode() + "\n");
							traceMessage("Refrigerator current mode is? " +
									refrigerateurExternalControlOutboundPort.currentMode() + "\n");
							traceMessage("Refrigerator going up one mode? " +
									refrigerateurExternalControlOutboundPort.upMode() + "\n");
							traceMessage("Refrigerator current mode is? " +
									refrigerateurExternalControlOutboundPort.currentMode() + "\n");
							traceMessage("Refrigerator setting current mode? " +
									refrigerateurExternalControlOutboundPort.setMode(1) + "\n");
							traceMessage("Refrigerator current mode is? " +
									refrigerateurExternalControlOutboundPort.currentMode() + "\n");
							traceMessage("Refrigerator is suspended? " +
									refrigerateurExternalControlOutboundPort.suspended() + "\n");
							traceMessage("Refrigerator suspends? " +
									refrigerateurExternalControlOutboundPort.suspend() + "\n");
							traceMessage("Refrigerator is suspended? " +
									refrigerateurExternalControlOutboundPort.suspended() + "\n");
							traceMessage("Refrigerator emergency? " +
									refrigerateurExternalControlOutboundPort.emergency() + "\n");
							Thread.sleep(1000);
							traceMessage("Refrigerator emergency? " +
									refrigerateurExternalControlOutboundPort.emergency() + "\n");
							traceMessage("HeRefrigeratorater resumes? " +
									refrigerateurExternalControlOutboundPort.resume() + "\n");
							traceMessage("Refrigerator is suspended? " +
									refrigerateurExternalControlOutboundPort.suspended() + "\n");
							traceMessage("Refrigerator current mode is? " +
									refrigerateurExternalControlOutboundPort.currentMode() + "\n");
							traceMessage("HEM heater refrigerator ends.\n");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);

	}

}