package projet_alasca.equipements.projecteur.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlCI;

public class RefrigerateurInternalControlConnector extends		AbstractConnector implements RefrigerateurInternalControlCI{

	@Override
	public boolean heating() throws Exception {

		return ((RefrigerateurInternalControlCI)this.offering).heating();
	}

	@Override
	public void startHeating() throws Exception {
		((RefrigerateurInternalControlCI)this.offering).startHeating();

	}

	@Override
	public void stopHeating() throws Exception {
		((RefrigerateurInternalControlCI)this.offering).stopHeating();

		
	}

	@Override
	public double getTargetTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getTargetTemperature();

	}

	@Override
	public double getCongelateurTargetTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getCongelateurTargetTemperature();

	}

	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getCurrentTemperature();

	}

	@Override
	public double getCongelateurCurrentTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.offering).getCongelateurCurrentTemperature();

	}

}
