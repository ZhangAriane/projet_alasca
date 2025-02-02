package projet_alasca.etape3.equipments.hem;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.utils.aclocks.AcceleratedAndSimulationClock;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationConnector;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationOutboundPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2024e1.equipments.hem.AdjustableOutboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.hem.HeaterConnector;
//import fr.sorbonne_u.components.hem2024e1.equipments.hem.ChauffeEauConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.equipements.chauffeEau.ChauffeEau;
import projet_alasca.equipements.gestionEnergie.ChauffeEauConnector;
import projet_alasca.equipements.refrigerateur.connections.RegistrationInboundPort;
import projet_alasca.etape3.CVMIntegrationTest;
import projet_alasca.etape3.equipments.heater.Heater;
//import projet_alasca.etape3.equipments.ChauffeEau.ChauffeEau;
import projet_alasca.etape3.equipments.meter.ElectricMeter;
import projet_alasca.etape3.utils.SimulationType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
								ClocksServerWithSimulationCI.class})
public class			HEM
extends		AbstractComponent implements RegistrationCI
{
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
	/** port to connect to the Heater.										*/
	protected AdjustableOutboundPort		heaterop;
	
	/** port to connect to the ChauffeEau.										*/
	protected AdjustableOutboundPort  chauffeEauop;
	
	protected RegistrationInboundPort registrationInboundPort;

	private ArrayList<String> equipementEnregistrer;
	public static final String registrationInboundPortURI = "registrationInboundPorURI";


	/** period of the HEM control loop.										*/
	protected final long					PERIOD_IN_SECONDS = 60L;

	// Execution/Simulation

	/** current type of simulation.											*/
	protected final SimulationType			currentSimulationType;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

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
	 * @param currentSimulationType	current execution type for the next run.
	 */
	protected 			HEM(SimulationType currentSimulationType)
	{
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.currentSimulationType = currentSimulationType;

		if (VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
		try {
			this.registrationInboundPort = new RegistrationInboundPort(this.registrationInboundPortURI,this);
			this.registrationInboundPort.publishPort();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		this.equipementEnregistrer = new ArrayList<>();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * perform once the control and then schedule another task to continue,
	 * unless the end instant has been reached; following this approach, the
	 * decisions to be made by the HEM could be introduced in this method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * pre	{@code end != null}
	 * pre	{@code ac != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	the instant at which the current execution of the control must be scheduled.
	 * @param end		the instant at which the control loop must stop.
	 * @param ac		the accelerated clock used as time reference to interpret the instants.
	 */
	protected void		loop(Instant current, Instant end, AcceleratedClock ac)
	{
		// For each action, compute the waiting time for this action
		// using the above instant and the clock, and then schedule the
		// task that will perform the action at the appropriate time.
		long delayInNanos = ac.nanoDelayUntilInstant(current);
		Instant next = current.plusSeconds(PERIOD_IN_SECONDS);
		if (next.compareTo(end) < 0) {
			this.scheduleTask(
				o -> {
					try	{
						o.traceMessage(
								"Electric meter current consumption: " +
								meterop.getCurrentConsumption() + "\n");
						o.traceMessage(
								"Electric meter current production: " +
								meterop.getCurrentProduction() + "\n");
						loop(next, end, ac);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}, delayInNanos, TimeUnit.NANOSECONDS);
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

			this.heaterop = new AdjustableOutboundPort(this);
			this.heaterop.publishPort();
			this.doPortConnection(
					this.heaterop.getPortURI(),
					Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
					HeaterConnector.class.getCanonicalName());
			
			this.chauffeEauop = new AdjustableOutboundPort(this);
			this.chauffeEauop.publishPort();
			this.doPortConnection(
					this.chauffeEauop.getPortURI(),
					ChauffeEau.EXTERNAL_CONTROL_INBOUND_PORT_URI,
					ChauffeEauConnector.class.getCanonicalName());
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
		// First, get the clock and wait until the start time that it specifies.
		AcceleratedAndSimulationClock ac = null;
		ClocksServerWithSimulationOutboundPort clocksServerOutboundPort =
							new ClocksServerWithSimulationOutboundPort(this);
		clocksServerOutboundPort.publishPort();
		this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerWithSimulationConnector.class.getCanonicalName());
		this.logMessage("HEM gets the clock.");
		ac = clocksServerOutboundPort.getClockWithSimulation(
												CVMIntegrationTest.CLOCK_URI);
		this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
		clocksServerOutboundPort.unpublishPort();
		clocksServerOutboundPort.destroyPort();
		this.logMessage("HEM waits until start time.");
		ac.waitUntilStart();
		this.logMessage("HEM starts.");

		if (this.currentSimulationType.isMilSimulation() ||
							this.currentSimulationType.isMILRTSimulation()) {
			this.logMessage("HEM has no MIL or MIL RT simulator yet.");
		} else if (this.currentSimulationType.isSILSimulation()) {
			// For SIL simulation, execute the control loop until the end of
			// simulation time.
			// delay until the first call to the electric meter
			Instant first = ac.getSimulationStartInstant().plusSeconds(600L);
			Instant end = ac.getSimulationEndInstant().minusSeconds(600L);
			this.logMessage("HEM schedules the SIL integration test.");
			this.loop(first, end, ac);
		} else {
			// Integration test for the meter and the ChauffeEau
			Instant meterTest = ac.getStartInstant().plusSeconds(60L);
			long delay = ac.nanoDelayUntilInstant(meterTest);
			this.logMessage("HEM schedules the meter integration test in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			// For the electric meter, simply perform two calls to test the
			// sensor methods.
			this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							traceMessage(
									"Electric meter current consumption: " +
									meterop.getCurrentConsumption() + "\n");
							traceMessage(
									"Electric meter current production: " +
									meterop.getCurrentProduction() + "\n");
							traceMessage("HEM meter test ends.\n");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);

			// For the ChauffeEau, perform a series of call that will also test the
			// adjustable interface.
			Instant ChauffeEau1 = ac.getStartInstant().plusSeconds(30L);
			delay = ac.nanoDelayUntilInstant(ChauffeEau1);
			this.logMessage("HEM schedules the ChauffeEau first call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								traceMessage("HEM ChauffeEau first call begins.\n");
								traceMessage("ChauffeEau maxMode index? " +
											   chauffeEauop.maxMode() + "\n");
								traceMessage("ChauffeEau current mode index? " +
											   chauffeEauop.currentMode() + "\n");
								traceMessage("ChauffeEau going down one mode? " +
											   chauffeEauop.downMode() + "\n");
								traceMessage("ChauffeEau current mode is? " +
											   chauffeEauop.currentMode() + "\n");
								traceMessage("ChauffeEau going up one mode? " +
											   chauffeEauop.upMode() + "\n");
								traceMessage("ChauffeEau current mode is? " +
											   chauffeEauop.currentMode() + "\n");
								traceMessage("ChauffeEau setting current mode? " +
											   chauffeEauop.setMode(2) + "\n");
								traceMessage("ChauffeEau current mode is? " +
											   chauffeEauop.currentMode() + "\n");
								traceMessage("ChauffeEau is suspended? " +
											   chauffeEauop.suspended() + "\n");
								traceMessage("HEM ChauffeEau first call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);

			Instant ChauffeEau2 = ac.getStartInstant().plusSeconds(120L);
			delay = ac.nanoDelayUntilInstant(ChauffeEau2);
			this.logMessage("HEM schedules the ChauffeEau second call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								traceMessage("HEM ChauffeEau second call begins.\n");
								traceMessage("ChauffeEau suspends? " +
											   chauffeEauop.suspend() + "\n");
								traceMessage("ChauffeEau is suspended? " +
											   chauffeEauop.suspended() + "\n");
								traceMessage("ChauffeEau emergency? " +
											   chauffeEauop.emergency() + "\n");
								traceMessage("HEM ChauffeEau second call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);

			Instant ChauffeEau3 = ac.getStartInstant().plusSeconds(240L);
			delay = ac.nanoDelayUntilInstant(ChauffeEau3);
			this.logMessage("HEM schedules the ChauffeEau third call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								traceMessage("HEM ChauffeEau third call begins.\n");
								traceMessage("ChauffeEau emergency? " +
											   chauffeEauop.emergency() + "\n");
								traceMessage("ChauffeEau resumes? " +
											   chauffeEauop.resume() + "\n");
								traceMessage("ChauffeEau is suspended? " +
											   chauffeEauop.suspended() + "\n");
								traceMessage("ChauffeEau current mode is? " +
											   chauffeEauop.currentMode() + "\n");
								traceMessage("HEM ChauffeEau third call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);
			
			
			
			// Heater : 
			
			// For the Heater, perform a series of call that will also test the
						// adjustable interface.
						Instant Heater1 = ac.getStartInstant().plusSeconds(280L);
						delay = ac.nanoDelayUntilInstant(Heater1);
						this.logMessage("HEM schedules the Heater first call in "
													+ delay + " " + TimeUnit.NANOSECONDS);
						this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										try {
											traceMessage("HEM Heater first call begins.\n");
											traceMessage("Heater maxMode index? " +
														   heaterop.maxMode() + "\n");
											traceMessage("Heater current mode index? " +
														   heaterop.currentMode() + "\n");
											traceMessage("Heater going down one mode? " +
														   heaterop.downMode() + "\n");
											traceMessage("Heater current mode is? " +
															heaterop.currentMode() + "\n");
											traceMessage("Heater going up one mode? " +
													heaterop.upMode() + "\n");
											traceMessage("Heater current mode is? " +
													heaterop.currentMode() + "\n");
											traceMessage("Heater setting current mode? " +
													heaterop.setMode(2) + "\n");
											traceMessage("Heater current mode is? " +
													heaterop.currentMode() + "\n");
											traceMessage("Heater is suspended? " +
													heaterop.suspended() + "\n");
											traceMessage("HEM Heater first call ends.\n");
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, delay, TimeUnit.NANOSECONDS);

						Instant Heater2 = ac.getStartInstant().plusSeconds(3200L);
						delay = ac.nanoDelayUntilInstant(Heater2);
						this.logMessage("HEM schedules the Heater second call in "
													+ delay + " " + TimeUnit.NANOSECONDS);
						this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										try {
											traceMessage("HEM Heater second call begins.\n");
											traceMessage("Heater suspends? " +
													heaterop.suspend() + "\n");
											traceMessage("Heater is suspended? " +
													heaterop.suspended() + "\n");
											traceMessage("Heater emergency? " +
													heaterop.emergency() + "\n");
											traceMessage("HEM Heater second call ends.\n");
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, delay, TimeUnit.NANOSECONDS);

						Instant Heater3 = ac.getStartInstant().plusSeconds(360L);
						delay = ac.nanoDelayUntilInstant(Heater3);
						this.logMessage("HEM schedules the Heater third call in "
													+ delay + " " + TimeUnit.NANOSECONDS);
						this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										try {
											traceMessage("HEM Heater third call begins.\n");
											traceMessage("Heater emergency? " +
													heaterop.emergency() + "\n");
											traceMessage("Heater resumes? " +
													heaterop.resume() + "\n");
											traceMessage("Heater is suspended? " +
													heaterop.suspended() + "\n");
											traceMessage("Heater current mode is? " +
													heaterop.currentMode() + "\n");
											traceMessage("HEM Heater third call ends.\n");
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, delay, TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.logMessage("HEM ends.");
		this.doPortDisconnection(this.meterop.getPortURI());
		this.doPortDisconnection(this.heaterop.getPortURI());
		this.doPortDisconnection(this.chauffeEauop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.meterop.unpublishPort();
			this.heaterop.unpublishPort();
			this.chauffeEauop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	// -------------------------------------------------------------------------
		// Service methods
		// -------------------------------------------------------------------------


		@Override
		public boolean registered(String uid) throws Exception {
			boolean res = this.equipementEnregistrer.contains(uid);
			this.logMessage("Connection ? " + res );
			return res;
		}

		@Override
		public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
			this.logMessage("Register : " + uid);

			this.equipementEnregistrer.add(uid);
			return this.equipementEnregistrer.contains(uid);
		}

		@Override
		public void unregister(String uid) throws Exception {
			this.logMessage("Unregister : " + uid);
			this.equipementEnregistrer.remove(uid);

		}
}
// -----------------------------------------------------------------------------
