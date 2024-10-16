package fr.sorbonne_u.devs_simulation.hioa.models.vars.histories;

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

// -----------------------------------------------------------------------------
/**
 * The class <code>Polynomial2</code> implements a polynomial function of
 * order 2 (quadratic).
 *
 * <p><strong>Description</strong></p>
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
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2022-07-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Polynomial2
implements	PolynomialI
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/** order 0 coefficient.												*/
	double		c0;
	/** order 1 coefficient.												*/
	double		c1;
	/** order 2 coefficient.												*/
	double		c2;

	// -------------------------------------------------------------------------
	//	Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an order 2 polynomial.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c0	order 0 coefficient.
	 * @param c1	order 1 coefficient.
	 * @param c2	order 2 coefficient.
	 */
	public				Polynomial2(double c0, double c1, double c2)
	{
		super();
		this.c0 = c0;
		this.c1 = c1;
		this.c2 = c2;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.PolynomialI#valueOf(double)
	 */
	@Override
	public double		valueOf(double t)
	{
		return t*(t*c2 + c1) + c0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.PolynomialI#firstDerivative(double)
	 */
	@Override
	public double		firstDerivative(double t)
	{
		return 2.0*c2*t + c1;
	}
}
// -----------------------------------------------------------------------------
