package projet_alasca.equipements.gestionEnergie;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlCI;

public class RefrigerateurConnector extends		AbstractConnector implements AdjustableCI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------


	// mode 0 : aucun compresseur n'est ouvert
	// mode 1 : le compresseur du réfrigérateur est ouvert
	// mode 2 : le compresseur du congélateur est ouvert
	// mode 3 : le compresseur du réfrigérateur et du congélateur sont ouvert
	protected static final int		MAX_MODE = 3;
//	/** the minimum admissible temperature from which the refrigerator should
//	 *  be resumed in priority after being suspended to save energy.		*/
//	protected static final double	MIN_ADMISSIBLE_TEMP = 7.0;
//	/** the maximal admissible difference between the target and the
//	 *  current temperature from which the refrigerator should be resumed in
//	 *  priority after being suspended to save energy.						*/
//	protected static final double	MAX_ADMISSIBLE_DELTA = 1.0;
//
//	/** the minimum admissible temperature from which the freezer should
//	 *  be resumed in priority after being suspended to save energy.		*/
//	protected static final double	MIN_ADMISSIBLE_TEMP_FREEZER = -15.0;
//	/** the maximal admissible difference between the target and the
//	 *  current temperature from which the freezer should be resumed in
//	 *  priority after being suspended to save energy.						*/
//	protected static final double	MAX_ADMISSIBLE_DELTA_FREEZER = -23.0;

	/** the current mode of the refrigerator.										*/
	protected int		currentMode;
	/** true if the refrigerator has been suspended, false otherwise.				*/
	protected boolean	isSuspended;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of connector.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code !suspended}
	 * post	{@code currentMode() == MAX_MODE}
	 * </pre>
	 *
	 */
	public				RefrigerateurConnector()
	{
		super();
		this.currentMode = MAX_MODE;
		this.isSuspended = false;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------


	@Override
	public int maxMode() throws Exception {
		return MAX_MODE;
	}

	protected void		computeAndSetNewLevel(int newMode) throws Exception
	{
		assert	newMode >= 0 && newMode <= MAX_MODE :
			new PreconditionException("newMode >= 0 && newMode <= MAX_MODE");

		switch (this.currentMode) {
		case 0 : 
			if(((RefrigerateurExternalControlCI)this.offering).onFreezerCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOffFreezerCompressor();
			if(((RefrigerateurExternalControlCI)this.offering).onRegrigeratorCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOffRefrigeratorCompressor();
			break;

		case 1 : 
			if(((RefrigerateurExternalControlCI)this.offering).onFreezerCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOffFreezerCompressor();
			if(!((RefrigerateurExternalControlCI)this.offering).onRegrigeratorCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOnRefrigeratorCompressor();
			break;
		case 2 : 
			if(!((RefrigerateurExternalControlCI)this.offering).onFreezerCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOnFreezerCompressor();
			if(((RefrigerateurExternalControlCI)this.offering).onRegrigeratorCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOffRefrigeratorCompressor();
			break;
		case 3 : 
			if(!((RefrigerateurExternalControlCI)this.offering).onFreezerCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOffFreezerCompressor();
			if(!((RefrigerateurExternalControlCI)this.offering).onRegrigeratorCompressor())
				((RefrigerateurExternalControlCI)this.offering).switchOffRefrigeratorCompressor();
			break;
		}

	}

	@Override
	public boolean upMode() throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() <= MAX_MODE :
			new PreconditionException("currentMode() < MAX_MODE");

		try {

			this.computeAndSetNewLevel(this.currentMode + 1);
			this.currentMode++;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean downMode() throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() > 0 :
			new PreconditionException("currentMode() > 0");

		try {
			this.computeAndSetNewLevel(this.currentMode - 1);
			this.currentMode--;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	modeIndex > 0 && modeIndex <= this.maxMode() :
			new PreconditionException(
					"modeIndex > 0 && modeIndex <= maxMode()");

		try {
			this.computeAndSetNewLevel(modeIndex);
			this.currentMode = modeIndex;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public int currentMode() throws Exception {
		if (this.suspended()) {
			return 0;
		} else {
			return this.currentMode;
		}
	}

	@Override
	public boolean suspended() throws Exception {
		return this.isSuspended;
	}

	@Override
	public boolean suspend() throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");

		try {
			((RefrigerateurExternalControlCI)this.offering).switchOffFreezerCompressor();
			((RefrigerateurExternalControlCI)this.offering).switchOffRefrigeratorCompressor();
			this.isSuspended = true;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean resume() throws Exception {
		assert	this.suspended() : new PreconditionException("suspended()");

		try {
			this.computeAndSetNewLevel(this.currentMode);
			this.isSuspended = false;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public double emergency() throws Exception {
//		assert	this.suspended() : new PreconditionException("suspended()");
//
//		double currentTemperature =
//				((RefrigerateurExternalControlCI)this.offering).
//				getRefrigeratorCurrentTemperature();
//		double targetTemperature =
//				((RefrigerateurExternalControlCI)this.offering).
//				getRefrigeratorTargetTemperature();
//		double delta = Math.abs(targetTemperature - currentTemperature);
//		double ret = -1.0;
//		if (currentTemperature < RefrigerateurConnector.MIN_ADMISSIBLE_TEMP ||
//				delta >= RefrigerateurConnector.MAX_ADMISSIBLE_DELTA) {
//			ret = 1.0;
//		} else {
//			ret = delta/RefrigerateurConnector.MAX_ADMISSIBLE_DELTA;
//		}
//
//		assert	ret >= 0.0 && ret <= 1.0 :
//			new PostconditionException("return >= 0.0 && return <= 1.0");
//
//		return ret;
		return 0;
	}

}