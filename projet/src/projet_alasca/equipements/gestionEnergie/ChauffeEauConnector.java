package projet_alasca.equipements.gestionEnergie;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;

public class ChauffeEauConnector extends		AbstractConnector implements AdjustableCI{

	// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		/** modes will be defined by five power levels, including a power
		 *  level of 0.0 watts; note that modes go from 1 (0.0 watts) to
		 *  6 (800.0 watts).													*/
		protected static final int		MAX_MODE = 8;
		/** the minimum admissible temperature from which the refrigerator should
		 *  be resumed in priority after being suspended to save energy.		*/
		protected static final double	MIN_ADMISSIBLE_TEMP = 7.0;
		/** the maximal admissible difference between the target and the
		 *  current temperature from which the refrigerator should be resumed in
		 *  priority after being suspended to save energy.						*/
		protected static final double	MAX_ADMISSIBLE_DELTA = 1.0;

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
		public				ChauffeEauConnector()
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

		protected void		computeAndSetNewPowerLevel(int newMode) throws Exception
		{
			assert	newMode >= 0 && newMode <= MAX_MODE :
				new PreconditionException("newMode >= 0 && newMode <= MAX_MODE");

			double maxPowerLevel =
					((ChauffeEauExternalControlCI)this.offering).getMaxPowerLevel();
			double newPowerLevel =
					(newMode - 1) * maxPowerLevel/(MAX_MODE - 1);
			((ChauffeEauExternalControlCI)this.offering).
			setCurrentPowerLevel(newPowerLevel);

		}

		@Override
		public boolean upMode() throws Exception {
			assert	!this.suspended() : new PreconditionException("!suspended()");
			assert	this.currentMode() <= MAX_MODE :
				new PreconditionException("currentMode() < MAX_MODE");

			try {
				this.computeAndSetNewPowerLevel(this.currentMode + 1);
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
				this.computeAndSetNewPowerLevel(this.currentMode - 1);
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
				this.computeAndSetNewPowerLevel(modeIndex);
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
				((ChauffeEauExternalControlCI)this.offering).setCurrentPowerLevel(0.0);
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
				this.computeAndSetNewPowerLevel(this.currentMode);
				this.isSuspended = false;
			} catch(Exception e) {
				return false;
			}
			return true;
		}

		@Override
		public double emergency() throws Exception {
			assert	this.suspended() : new PreconditionException("suspended()");

			double currentTemperature =
					((ChauffeEauExternalControlCI)this.offering).
					getCurrentTemperature();
			double targetTemperature =
					((ChauffeEauExternalControlCI)this.offering).
					getTargetTemperature();
			double delta = Math.abs(targetTemperature - currentTemperature);
			double ret = -1.0;
			if (currentTemperature < ChauffeEauConnector.MIN_ADMISSIBLE_TEMP ||
					delta >= ChauffeEauConnector.MAX_ADMISSIBLE_DELTA) {
				ret = 1.0;
			} else {
				ret = delta/ChauffeEauConnector.MAX_ADMISSIBLE_DELTA;
			}

			assert	ret >= 0.0 && ret <= 1.0 :
				new PostconditionException("return >= 0.0 && return <= 1.0");

			return ret;
		}
}
