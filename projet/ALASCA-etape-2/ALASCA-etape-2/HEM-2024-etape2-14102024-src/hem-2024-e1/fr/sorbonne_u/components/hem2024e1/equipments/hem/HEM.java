package fr.sorbonne_u.components.hem2024e1.equipments.hem;

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
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.HeaterTester;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a very limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
								ClocksServerCI.class})
public class			HEM
extends		AbstractComponent
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

	/** when true, manage the heater in a customised way, otherwise let
	 *  it register itself as an adjustable appliance.						*/
	protected boolean						isPreFirstStep;
	/** port to connect to the heater when managed in a customised way.		*/
	protected AdjustableOutboundPort		heaterop;

	/** when true, this implementation of the HEM performs the tests
	 *  that are planned in the method execute.								*/
	protected boolean						performTest;
	/** accelerated clock used for the tests.								*/
	protected AcceleratedClock				ac;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the glass-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hem != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hem	instance to be tested.
	 * @return		true if the glass-box invariants are observed, false otherwise.
	 */
	protected static boolean	glassBoxInvariants(HEM hem)
	{
		assert	hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the black-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hem != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hem	instance to be tested.
	 * @return		true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	blackBoxInvariants(HEM hem)
	{
		assert	hem != null : new PreconditionException("hem != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				X_RELATIVE_POSITION >= 0,
				HEM.class, hem,
				"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				Y_RELATIVE_POSITION >= 0,
				HEM.class, hem,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

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
	 */
	protected 			HEM()
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
	protected 			HEM(boolean performTest)
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

		assert	glassBoxInvariants(this) :
				new InvariantException("HEM.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
				new InvariantException("HEM.blackBoxInvariants(this)");
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
				this.heaterop = new AdjustableOutboundPort(this);
				this.heaterop.publishPort();
				this.doPortConnection(
						this.heaterop.getPortURI(),
						Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						HeaterConnector.class.getCanonicalName());
			}
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
				this.testHeater();
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.meterop.getPortURI());
		if (this.isPreFirstStep) {
			this.doPortDisconnection(this.heaterop.getPortURI());
		}
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
			if (this.isPreFirstStep) {
				this.heaterop.unpublishPort();
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
	protected void		testHeater()
	{
		// Test for the heater
		Instant heaterTestStart =
				this.ac.getStartInstant().plusSeconds(
							(HeaterTester.SWITCH_ON_DELAY +
											HeaterTester.SWITCH_OFF_DELAY)/2);
		this.traceMessage("HEM schedules the heater test.\n");
		long delay = this.ac.nanoDelayUntilInstant(heaterTestStart);

		// schedule the switch on heater in one second
		this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
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
												heaterop.setMode(1) + "\n");
							traceMessage("Heater current mode is? " +
												heaterop.currentMode() + "\n");
							traceMessage("Heater is suspended? " +
												heaterop.suspended() + "\n");
							traceMessage("Heater suspends? " +
												heaterop.suspend() + "\n");
							traceMessage("Heater is suspended? " +
												heaterop.suspended() + "\n");
							traceMessage("Heater emergency? " +
												heaterop.emergency() + "\n");
							Thread.sleep(1000);
							traceMessage("Heater emergency? " +
												heaterop.emergency() + "\n");
							traceMessage("Heater resumes? " +
												heaterop.resume() + "\n");
							traceMessage("Heater is suspended? " +
												heaterop.suspended() + "\n");
							traceMessage("Heater current mode is? " +
									heaterop.currentMode() + "\n");
							traceMessage("HEM heater test ends.\n");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);

	}
}
// -----------------------------------------------------------------------------
