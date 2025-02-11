package projet_alasca.equipements.refrigerateur.mil.events;



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

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.refrigerateur.mil.RefrigerateurElectricityModel;
import projet_alasca.equipements.refrigerateur.mil.RefrigerateurElectricityModel.CompressorState;
import projet_alasca.equipements.refrigerateur.mil.RefrigerateurElectricityModel.State;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetPowerHeater</code> defines the simulation event of the
 * heater power being set to some level (in watts).
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
 * <p>Created on : 2023-10-12</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SetPowerCongelateur
extends		ES_Event
implements	RefrigerateurEventI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PowerValue</code> represent a power value to be passed
	 * as an {@code EventInformationI} when creating a {@code SetPowerHeater}
	 * event.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code power >= 0.0 && power <= HeaterElectricityModel.MAX_HEATING_POWER}
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-10-13</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	PowerValue
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		/* a power in watts.												*/
		protected final double	power;

		/**
		 * create an instance of {@code PowerValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code power >= 0.0 && power <= HeaterElectricityModel.MAX_HEATING_POWER}
		 * post	{@code getPower() == power}
		 * </pre>
		 *
		 * @param power	the power in watts to put in this container.
		 */
		public			PowerValue(double power)
		{
			super();

			assert	power >= 0.0 &&
							power <= RefrigerateurElectricityModel.MAX_FREEZING_POWER :
					new AssertionError(
							"Precondition violation: power >= 0.0 && "
							+ "power <= RefrigerateurElectricityModel.MAX_FREEZING_POWER,"
							+ " but power = " + power);

			this.power = power;
		}

		/**
		 * return the power value in watts.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return >= 0.0 && return <= HeaterElectricityModel.MAX_HEATING_POWER}
		 * </pre>
		 *
		 * @return	the power value in watts.
		 */
		public double	getPower()	{ return this.power; }

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.power);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the power value to be set on the heater when the event will be
	 *  executed.															*/
	protected final PowerValue	powerValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code SetPowerHeater} event which content is a
	 * {@code PowerValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * pre	{@code content != null && content instanceof PowerValue}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the power value to be set on the heater when the event will be executed.
	 */
	public				SetPowerCongelateur(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);

		assert	content != null && content instanceof PowerValue :
				new AssertionError(
						"Precondition violation: event content is null or"
						+ " not a PowerValue " + content);

		this.powerValue = (PowerValue) content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// if many heater events occur at the same time, the SetPowerHeater one
		// will be executed first except for SwitchOnHeater ones.
		if (e instanceof SwitchOffRefrigerateur) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof RefrigerateurElectricityModel :
				new AssertionError(
						"Precondition violation: model instanceof "
						+ "RefrigerateurElectricityModel");

		RefrigerateurElectricityModel refrigerator = (RefrigerateurElectricityModel)model;
		assert	refrigerator.getState() == State.ON :
				new AssertionError(
						"model not in the right state, should be "
						+ "State.ON but is " + refrigerator.getState());
		assert	refrigerator.getCongelatorCompressorState() == CompressorState.ON :
			new AssertionError(
					"model not in the right state, should be "
					+ "CompressorState.ON but is " + refrigerator.getRefrigeratorCompressorState());
		
		refrigerator.setCurrentFreezingPower(this.powerValue.getPower(),
									  this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------
