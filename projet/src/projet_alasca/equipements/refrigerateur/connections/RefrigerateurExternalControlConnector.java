package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlCI;

public class RefrigerateurExternalControlConnector extends		AbstractConnector implements RefrigerateurExternalControlCI{

	@Override
	public double getTargetTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getTargetTemperature();
	}


	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.offering).getTargetTemperature();
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
	
	
}
