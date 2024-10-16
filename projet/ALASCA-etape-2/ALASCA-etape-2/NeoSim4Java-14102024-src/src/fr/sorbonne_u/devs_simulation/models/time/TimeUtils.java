package fr.sorbonne_u.devs_simulation.models.time;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>TimeUtils</code> defines utilities to manipulate
 * {@code Time} and {@code Duration} instances.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022-10-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	TimeUtils
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected static boolean	VERBOSE = false;

	protected static final long	MICROS2NANOS = 1000L;
	protected static final long	MILLIS2NANOS = 1000L * MICROS2NANOS;
	protected static final long	SECONDS2NANOS = 1000L * MILLIS2NANOS;
	protected static final long	MINUTES2NANOS = 60L * SECONDS2NANOS;
	protected static final long	HOURS2NANOS = 60L * MINUTES2NANOS;
	protected static final long	DAYS2NANOS = 24L * HOURS2NANOS;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * convert a simulated duration in nanoseconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d >= 0}
	 * pre	{@code u != null}
	 * post	{@code ret > 0}
	 * </pre>
	 *
	 * @param d	a duration.
	 * @param u	the time unit interpreting {@code d}.
	 * @return	the duration in nanoseconds as a {@code long}.
	 */
	public static long	toNanos(double d, TimeUnit u)
	{
		assert	d >= 0 : new AssertionError("Precondition violation: d >= 0");
		assert	u != null :
				new AssertionError("Precondition violation: u != null");

		switch (u) {
		case NANOSECONDS : return (long) d;
		case MICROSECONDS : return (long) d * MICROS2NANOS;
		case MILLISECONDS : return (long) d * MILLIS2NANOS;
		case SECONDS : return (long) d * SECONDS2NANOS;
		case MINUTES : return (long) d * MINUTES2NANOS;
		case HOURS : return (long) d * HOURS2NANOS;
		case DAYS : return (long) d * DAYS2NANOS;
		default : return 0L;
		}
	}

	/**
	 * unit tests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param args	command line arguments.
	 */
	public static void	main(String[] args)
	{
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.NANOSECONDS),
				toNanos(1.0, TimeUnit.NANOSECONDS));
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.MICROSECONDS),
				toNanos(1.0, TimeUnit.MICROSECONDS));
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.MILLISECONDS),
				toNanos(1.0, TimeUnit.MILLISECONDS));
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.SECONDS),
				toNanos(1.0, TimeUnit.SECONDS));
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.MINUTES),
				toNanos(1.0, TimeUnit.MINUTES));
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.HOURS),
				toNanos(1.0, TimeUnit.HOURS));
		Assertions.assertEquals(
				TimeUnit.NANOSECONDS.convert(1L, TimeUnit.DAYS),
				toNanos(1.0, TimeUnit.DAYS));

		if (VERBOSE) {
			System.out.println("1 nanosecond is " +
					toNanos(1.0, TimeUnit.NANOSECONDS) +
					" nanoseconds.");
			System.out.println("1 microsecond is " +
					toNanos(1.0, TimeUnit.MICROSECONDS) +
					" nanoseconds.");
			System.out.println("1 millisecond is " +
					toNanos(1.0, TimeUnit.MILLISECONDS) +
					" nanoseconds.");
			System.out.println("1 second is " +
					toNanos(1.0, TimeUnit.SECONDS) +
					" nanoseconds.");
			System.out.println("1 minute is " +
					toNanos(1.0, TimeUnit.MINUTES) +
					" nanoseconds.");
			System.out.println("1 hour is " +
					toNanos(1.0, TimeUnit.HOURS) +
					" nanoseconds.");
			System.out.println("1 day is " +
					toNanos(1.0, TimeUnit.DAYS) +
					" nanoseconds.");
		}
	}
}
// -----------------------------------------------------------------------------
