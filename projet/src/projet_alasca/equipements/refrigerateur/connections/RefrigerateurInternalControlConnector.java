package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlCI;

public class RefrigerateurInternalControlConnector extends		AbstractConnector implements RefrigerateurInternalControlCI{


	@Override
	public double getTargetTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getCurrentTemperature();
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




}
