package fr.sorbonne_u.devs_simulation.examples.counters;

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

import fr.sorbonne_u.devs_simulation.models.AbstractCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;

//-----------------------------------------------------------------------------
/**
 * The class <code>BasicCoupledModelFactory</code> defines a coupled model
 * factory for the basic example, this creating an instance of
 * {@code BasicCoupledModel}.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Very simple example to show the use of factories to create instances of
 * coupled models. Usually, factories are used when the model class requires
 * more constructor parameters to be created than the ones defined by the
 * standard constructor of {@code CoupledModel}. In such a case, the model
 * factory can store the complementary parameters through a "set" method
 * and then use them in the method {@code createCoupledModel}.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-05-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				BasicCoupledModelFactory
extends		AbstractCoupledModelFactory
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#createCoupledModel()
	 */
	@Override
	public CoupledModel		createCoupledModel()
	{
		assert	this.modelParametersSet();
		assert	this.modelURI.equals(
							BasicCounterCoupledModel.BASIC_COUPLED_MODEL_URI);

		return new BasicCounterCoupledModel(
								this.modelURI,
								this.models[0].getSimulatedTimeUnit(),
								this.se,
								this.models,
								this.imported,
								this.reexported,
								this.connections);
	}
}
//-----------------------------------------------------------------------------
