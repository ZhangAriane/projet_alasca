package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserCI;

public class RefrigerateurUserConnector extends		AbstractConnector implements RefrigerateurUserCI {

	@Override
	public double getRefrigeratorTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getRefrigeratorTargetTemperature();
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getFreezerTargetTemperature();
	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getRefrigeratorCurrentTemperature();
	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.offering).getFreezerCurrentTemperature();
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
	public void setRefrigeratorTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.offering).setRefrigeratorTargetTemperature(target);
	}

	@Override
	public void setFreezerTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.offering).setFreezerTargetTemperature(target);
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
	
	@Override
	public void switchOnRefrigeratorCompressor() throws Exception {
		((RefrigerateurUserCI)this.offering).switchOnRefrigeratorCompressor();

	}

	@Override
	public void switchOffRefrigeratorCompressor() throws Exception {
		((RefrigerateurUserCI)this.offering).switchOffRefrigeratorCompressor();

	}

	@Override
	public void switchOnFreezerCompressor() throws Exception {
		((RefrigerateurUserCI)this.offering).switchOnFreezerCompressor();

	}

	@Override
	public void switchOffFreezerCompressor() throws Exception {
		((RefrigerateurUserCI)this.offering).switchOnFreezerCompressor();

	}

	@Override
	public boolean onRegrigeratorCompressor() throws Exception {
		return ((RefrigerateurUserCI)this.offering).onRegrigeratorCompressor();
	}

	@Override
	public boolean onFreezerCompressor() throws Exception {
		return ((RefrigerateurUserCI)this.offering).onFreezerCompressor();
	}

}
