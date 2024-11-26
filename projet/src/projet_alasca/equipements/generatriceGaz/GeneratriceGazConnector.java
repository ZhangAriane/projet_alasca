package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class GeneratriceGazConnector 
extends		AbstractConnector
implements GeneratriceGazCI{

	@Override
	public GeneratriceGazState getState() throws Exception {
		return ((GeneratriceGazCI)this.offering).getState();
	}


	@Override
	public void turnOn() throws Exception {
		((GeneratriceGazCI)this.offering).
		turnOn();
		
	}

	@Override
	public void turnOff() throws Exception {
		((GeneratriceGazCI)this.offering).
		turnOff();
	}


	

}
