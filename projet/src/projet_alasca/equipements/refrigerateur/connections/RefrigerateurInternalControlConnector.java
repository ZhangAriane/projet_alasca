package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlCI;

public class RefrigerateurInternalControlConnector extends		AbstractConnector implements RefrigerateurInternalControlCI{


	@Override
	public double getRefrigeratorTargetTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getRefrigeratorTargetTemperature();
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getFreezerTargetTemperature();
	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getRefrigeratorCurrentTemperature();
	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getFreezerCurrentTemperature();
	}
	
	@Override
	public boolean cooling() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).cooling();
	}

	@Override
	public void startCooling() throws Exception {
		((RefrigerateurInternalControlCI)this.offering).startCooling();
	}

	@Override
	public void stopCooling() throws Exception {
		((RefrigerateurInternalControlCI)this.offering).stopCooling();
	}

	@Override
	public boolean freezing() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).freezing();
	}

	@Override
	public void startFreezing() throws Exception {
		((RefrigerateurInternalControlCI)this.offering).startFreezing();
	}

	@Override
	public void stopFreezing() throws Exception {
		((RefrigerateurInternalControlCI)this.offering).stopFreezing();
	}



}
