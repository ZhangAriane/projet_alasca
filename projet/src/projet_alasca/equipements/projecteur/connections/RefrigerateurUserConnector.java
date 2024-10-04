package projet_alasca.equipements.projecteur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserCI;

public class RefrigerateurUserConnector extends		AbstractConnector implements RefrigerateurUserCI {

	@Override
	public double getTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getTargetTemperature();
	}

	@Override
	public double getCongelateurTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getCongelateurTargetTemperature();
	}

	@Override
	public double getCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getCurrentTemperature();
	}

	@Override
	public double getCongelateurCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getCongelateurCurrentTemperature();
	}

	@Override
	public boolean on() throws Exception {

		return ((RefrigerateurUserCI)this.offering).on();
	}

	@Override
	public void switchOn() throws Exception {

		((RefrigerateurUserCI)this.offering).switchOn();
	}

	@Override
	public void switchOff() throws Exception {

		((RefrigerateurUserCI)this.offering).switchOff();
	}

	@Override
	public void setTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.offering).setTargetTemperature(target);
	}

	@Override
	public void setCongelateurTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.offering).setCongelateurTargetTemperature(target);
	}

	@Override
	public double getMaxPowerLevel() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {

		((RefrigerateurUserCI)this.offering).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public double getCurrentPowerLevel() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getCurrentPowerLevel();
	}

}
