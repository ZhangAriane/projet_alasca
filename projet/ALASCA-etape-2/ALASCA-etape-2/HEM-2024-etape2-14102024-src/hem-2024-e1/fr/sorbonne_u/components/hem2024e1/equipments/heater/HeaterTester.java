package fr.sorbonne_u.components.hem2024e1.equipments.heater;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
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
import fr.sorbonne_u.components.hem2024e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterExternalControlConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterInternalControlConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterUserConnector;
import fr.sorbonne_u.components.hem2024e1.equipments.heater.connections.HeaterUserOutboundPort;
import fr.sorbonne_u.exceptions.InvariantChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterTester</code> implements a component performing 
 * tests for the class <code>Heater</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
 * invariant	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
 * invariant	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required={HeaterUserCI.class,
							  HeaterInternalControlCI.class,
							  HeaterExternalControlCI.class,
							  ClocksServerCI.class})
public class			HeaterTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/**	in clock-driven scenario, the delay from the start instant at which
	 *  the heater is switched on.											*/
	public static final int		SWITCH_ON_DELAY = 2;
	/**	in clock-driven scenario, the delay from the start instant at which
	 *  the heater is switched off.											*/
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
	protected String			heaterUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			heaterInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			heaterExternalControlInboundPortURI;

	/** user component interface inbound port.								*/
	protected HeaterUserOutboundPort			hop;
	/** internal control component interface inbound port.					*/
	protected HeaterInternalControlOutboundPort	hicop;
	/** external control component interface inbound port.					*/
	protected HeaterExternalControlOutboundPort	hecop;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the glass-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ht != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ht	instance to be tested.
	 * @return		true if the glass-box invariants are observed, false otherwise.
	 */
	protected static boolean	glassBoxInvariants(HeaterTester ht)
	{
		assert	ht != null : new PreconditionException("ht != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
				ht.heaterUserInboundPortURI != null &&
									!ht.heaterUserInboundPortURI.isEmpty(),
				HeaterTester.class, ht,
				"ht.heaterUserInboundPortURI != null && "
							+ "!ht.heaterUserInboundPortURI.isEmpty()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				ht.heaterInternalControlInboundPortURI != null &&
							!ht.heaterInternalControlInboundPortURI.isEmpty(),
				HeaterTester.class, ht,
				"ht.heaterInternalControlInboundPortURI != null && "
						+ "!ht.heaterInternalControlInboundPortURI.isEmpty()");
		ret &= InvariantChecking.checkGlassBoxInvariant(
				ht.heaterExternalControlInboundPortURI != null &&
							!ht.heaterExternalControlInboundPortURI.isEmpty(),
				HeaterTester.class, ht,
				"ht.heaterExternalControlInboundPortURI != null &&"
						+ "!ht.heaterExternalControlInboundPortURI.isEmpty()");
		return ret;
	}

	/**
	 * return true if the black-box invariants is observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ht != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ht	instance to be tested.
	 * @return		true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	blackBoxInvariants(HeaterTester ht)
	{
		assert	ht != null : new PreconditionException("ht != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				X_RELATIVE_POSITION >= 0,
				HeaterTester.class, ht,
				"X_RELATIVE_POSITION >= 0");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				Y_RELATIVE_POSITION >= 0,
				HeaterTester.class, ht,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest	true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @throws Exception	<i>to do</i>.
	 */
	protected			HeaterTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 Heater.USER_INBOUND_PORT_URI,
			 Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a heater test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param heaterUserInboundPortURI				URI of the user component interface inbound port.
	 * @param heaterInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param heaterExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			HeaterTester(
		boolean isUnitTest,
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(heaterUserInboundPortURI,
						heaterInternalControlInboundPortURI,
						heaterExternalControlInboundPortURI);
	}

	/**
	 * create a heater test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param heaterUserInboundPortURI				URI of the user component interface inbound port.
	 * @param heaterInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param heaterExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			HeaterTester(
		boolean isUnitTest,
		String reflectionInboundPortURI,
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(heaterUserInboundPortURI,
						heaterInternalControlInboundPortURI,
						heaterExternalControlInboundPortURI);
	}

	/**
	 * initialise a heater test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param heaterUserInboundPortURI				URI of the user component interface inbound port.
	 * @param heaterInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param heaterExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI
		) throws Exception
	{
		this.heaterUserInboundPortURI = heaterUserInboundPortURI;
		this.hop = new HeaterUserOutboundPort(this);
		this.hop.publishPort();
		this.heaterInternalControlInboundPortURI =
									heaterInternalControlInboundPortURI;
		this.hicop = new HeaterInternalControlOutboundPort(this);
		this.hicop.publishPort();
		this.heaterExternalControlInboundPortURI =
									heaterExternalControlInboundPortURI;
		this.hecop = new HeaterExternalControlOutboundPort(this);
		this.hecop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Heater tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	glassBoxInvariants(this) :
				new InvariantException("HeaterTester.glassBoxInvariants(this)");
		assert	blackBoxInvariants(this) :
				new InvariantException("HeaterTester.blackBoxInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void		testSwitchOnSwitchOff()
	{
		this.traceMessage("testSwitchOnSwitchOff...\n");
		try {
			this.hop.switchOn();
		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		try {
			this.hop.switchOff();
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
			assertEquals(false, this.hop.on());
		} catch (Exception e) {
			this.traceMessage("...KO. " + e + "\n");
			assertTrue(false);
		}
		try {
			this.hop.switchOn();
			assertEquals(true, this.hop.on());
			this.hop.switchOff();
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
			this.hop.setTargetTemperature(10.0);
			assertEquals(10.0, this.hop.getTargetTemperature());
			this.hop.setTargetTemperature(Heater.STANDARD_TARGET_TEMPERATURE);
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
			this.hop.switchOn();
			assertEquals(Heater.FAKE_CURRENT_TEMPERATURE,
						 this.hop.getCurrentTemperature());
			this.hop.switchOff();
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
			assertEquals(Heater.MAX_POWER_LEVEL,
						 this.hop.getMaxPowerLevel());
			this.hop.switchOn();
			this.hop.setCurrentPowerLevel(Heater.MAX_POWER_LEVEL/2.0);
			assertEquals(Heater.MAX_POWER_LEVEL/2.0,
						 this.hop.getCurrentPowerLevel());
			this.hop.switchOff();
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
			assertEquals(Heater.STANDARD_TARGET_TEMPERATURE,
						 this.hicop.getTargetTemperature());
			this.hop.switchOn();
			assertEquals(true, this.hop.on());
			assertEquals(Heater.FAKE_CURRENT_TEMPERATURE,
						 this.hicop.getCurrentTemperature());
			this.hicop.startHeating();
			assertTrue(this.hicop.heating());
			this.hicop.stopHeating();
			assertTrue(!this.hicop.heating());
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
			assertEquals(Heater.MAX_POWER_LEVEL,
						 this.hecop.getMaxPowerLevel());
			this.hecop.setCurrentPowerLevel(Heater.MAX_POWER_LEVEL/2.0);
			assertEquals(Heater.MAX_POWER_LEVEL/2.0,
						 this.hecop.getCurrentPowerLevel());
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
					this.hop.getPortURI(),
					this.heaterUserInboundPortURI,
					HeaterUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.hicop.getPortURI(),
					heaterInternalControlInboundPortURI,
					HeaterInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.hecop.getPortURI(),
					heaterExternalControlInboundPortURI,
					HeaterExternalControlConnector.class.getCanonicalName());
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
			this.traceMessage("Heater tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
										CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(
						clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			Instant startInstant = ac.getStartInstant();
			Instant heaterSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
			Instant heaterSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
			this.traceMessage("Heater tester waits until start.\n");
			ac.waitUntilStart();
			this.traceMessage("Heater tester schedules switch on and off.\n");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(heaterSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(heaterSwitchOff);

			// This is to avoid mixing the 'this' of the task object with the 'this'
			// representing the component object in the code of the next methods run
			AbstractComponent o = this;

			// schedule the switch on heater
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Heater switches on.\n");
								hop.switchOn();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);

			// to be completed with a more covering scenario

			// schedule the switch off heater
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Heater switches off.\n");
								hop.switchOff();
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
		this.doPortDisconnection(this.hop.getPortURI());
		this.doPortDisconnection(this.hicop.getPortURI());
		this.doPortDisconnection(this.hecop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hop.unpublishPort();
			this.hicop.unpublishPort();
			this.hecop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
