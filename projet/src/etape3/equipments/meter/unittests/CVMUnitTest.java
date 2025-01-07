package etape3.equipments.meter.unittests;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
import fr.sorbonne_u.components.cvm.AbstractCVM;
import etape3.equipments.meter.ElectricMeter;
import etape3.utils.ExecutionType;
import etape3.utils.SimulationType;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMUnitTest</code> performs unit tests on the electric
 * meter component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code DELAY_TO_START > 0}
 * invariant	{@code CLOCK_URI != null && !CLOCK_URI.isEmpty()}
 * invariant	{@code START_INSTANT != null && !START_INSTANT.isEmpty()}
 * </pre>
 * 
 * TODO: add that START_INSTANT is well-formatted.
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.				*/
	public static final long			DELAY_TO_START = 3000L;
	/** for unit tests and SIL simulation tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static final String			CLOCK_URI = "hem-clock";
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static final String			START_INSTANT =
													"2023-11-22T00:00:00.00Z";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;
		ElectricMeter.VERBOSE = true;
		ElectricMeter.X_RELATIVE_POSITION = 0;
		ElectricMeter.Y_RELATIVE_POSITION = 1;
		ElectricMeterUnitTester.VERBOSE = true;
		ElectricMeterUnitTester.X_RELATIVE_POSITION = 1;
		ElectricMeterUnitTester.Y_RELATIVE_POSITION = 1;
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{ElectricMeter.REFLECTION_INBOUND_PORT_URI,
							 ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
							 ExecutionType.UNIT_TEST,
							 SimulationType.NO_SIMULATION,
							 "",
							 "",
							 TimeUnit.DAYS,
							 0.0,
							 CLOCK_URI});

		AbstractComponent.createComponent(
				ElectricMeterUnitTester.class.getCanonicalName(),
				new Object[]{CLOCK_URI});

		long unixEpochStartTimeInMillis =
				System.currentTimeMillis() + DELAY_TO_START;

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
											unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						Instant.parse(START_INSTANT),
						1.0});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(DELAY_TO_START + 1000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
