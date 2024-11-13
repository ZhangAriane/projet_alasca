package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class GeneratriceGazConnector 
extends		AbstractConnector
implements GeneratriceGazCI{

	@Override
	public State getState() throws Exception {
		return ((GeneratriceGazCI)this.offering).getState();
	}

	@Override
	public Mode getMode() throws Exception {
		return ((GeneratriceGazCI)this.offering).getMode();
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

	@Override
	public void setHigh() throws Exception {
		((GeneratriceGazCI)this.offering).
		setHigh();
		
	}

	@Override
	public void setLow() throws Exception {
		((GeneratriceGazCI)this.offering).
		setLow();
		
	}

	@Override
	public void setMeddium() throws Exception {
		((GeneratriceGazCI)this.offering).
		setMeddium();
	}

	

}
