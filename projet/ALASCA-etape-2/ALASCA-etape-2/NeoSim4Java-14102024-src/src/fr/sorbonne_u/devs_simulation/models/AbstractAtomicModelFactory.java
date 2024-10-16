package fr.sorbonne_u.devs_simulation.models;

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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.InvariantChecking;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractAtomicModelFactory</code> provides the
 * basic implementation for atomic model factories.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code modelURI == null || !modelURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-25</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractAtomicModelFactory
implements	AtomicModelFactoryI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI of the model to be created.										*/
	protected String			modelURI;
	/** time unit used for the simulation clock.							*/
	protected TimeUnit			simulatedTimeUnit;
	/** simulation engine to be associated with the atomic model or
	 *  null if none.														*/
	protected AtomicSimulatorI	simulationEngine;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the glass-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the glass-box invariants are observed, false otherwise.
	 */
	protected static boolean	glassBoxInvariants(
		AbstractAtomicModelFactory instance
		)
	{
		assert	instance != null :
				new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkGlassBoxInvariant(
					instance.modelURI == null || !instance.modelURI.isEmpty(),
					AbstractAtomicModelFactory.class,
					instance,
					"modelURI == null || !modelURI.isEmpty()");
		return ret;
	}

	/**
	 * return true if the black-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	blackBoxInvariants(
		AbstractAtomicModelFactory instance
		)
	{
		assert	instance != null :
				new AssertionError("Precondition violation: instance != null");

		boolean ret = true;
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI#setAtomicModelCreationParameters(java.lang.String, java.util.concurrent.TimeUnit, fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI)
	 */
	@Override
	public AtomicModelFactoryI	setAtomicModelCreationParameters(
		String modelURI,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		)
	{
		assert	!this.modelParametersSet() :
				new AssertionError("Precondition violation: "
												+ "!modelParametersSet()");
		assert	modelURI != null && !modelURI.isEmpty() :
				new AssertionError("Precondition violation:"
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new AssertionError("Precondition violation:"
												+ "simulatedTimeUnit != null");

		this.modelURI = modelURI;
		this.simulatedTimeUnit = simulatedTimeUnit;
		this.simulationEngine = simulationEngine;

		assert	glassBoxInvariants(this) :
				new AssertionError("Glass-box invariants violation!");
		assert	glassBoxInvariants(this) :
				new AssertionError("Black-box invariants violation!");

		return this;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI#modelParametersSet()
	 */
	@Override
	public boolean		modelParametersSet()
	{
		return this.modelURI != null && this.simulatedTimeUnit != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI#createAtomicEngine()
	 */
	@Override
	public AtomicSimulatorI	createAtomicEngine()
	{
		return new AtomicEngine();
	}
}
// -----------------------------------------------------------------------------
