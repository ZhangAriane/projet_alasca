package projet_alasca.equipements.chauffeEau.mil;

import fr.sorbonne_u.devs_simulation.models.time.Time;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauStateModel.State;

// -----------------------------------------------------------------------------
/**
 * The class <code>ChauffeEauOperationI</code> defines the common operation used
 * by events to execute on the ChauffeEau models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ChauffeEauOperationI
{
	/**
	 * return the state of the ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public State		getState();

	/**
	 * set the state of the ChauffeEau.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void			setState(State s);

	/**
	 * set the current heating power of the ChauffeEau to {@code newPower}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_HEATING_POWER}
	 * post	{@code getCurrentHeatingPower() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the ChauffeEau.
	 * @param t			time at which the new power is set.
	 */
	public void			setCurrentHeatingPower(double newPower, Time t);
}
// -----------------------------------------------------------------------------
