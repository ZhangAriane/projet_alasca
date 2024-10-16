package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlCI;

public class RefrigerateurExternalControlConnector extends		AbstractConnector implements RefrigerateurExternalControlCI{

	@Override
	public double getRefrigeratorTargetTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getRefrigeratorTargetTemperature();
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getFreezerTargetTemperature();
	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getRefrigeratorTargetTemperature();
	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getFreezerCurrentTemperature();
	}

	@Override
	public double getMaxPowerLevel() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {
		((RefrigerateurExternalControlCI)this.offering).setCurrentPowerLevel(powerLevel);
		
	}

	@Override
	public double getCurrentPowerLevel() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getCurrentPowerLevel();
	}
	
	@Override
	public void switchOnRefrigeratorCompressor() throws Exception {
		((RefrigerateurExternalControlCI)this.offering).switchOnRefrigeratorCompressor();
	}

	@Override
	public void switchOffRefrigeratorCompressor() throws Exception {
		((RefrigerateurExternalControlCI)this.offering).switchOffRefrigeratorCompressor();

	}

	@Override
	public void switchOnFreezerCompressor() throws Exception {
		((RefrigerateurExternalControlCI)this.offering).switchOnFreezerCompressor();

	}

	@Override
	public void switchOffFreezerCompressor() throws Exception {
		((RefrigerateurExternalControlCI)this.offering).switchOffFreezerCompressor();

	}

	@Override
	public boolean onRegrigeratorCompressor() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).onRegrigeratorCompressor();
	}

	@Override
	public boolean onFreezerCompressor() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).onFreezerCompressor();
	}

	
}
