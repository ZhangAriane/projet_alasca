package projet_alasca.equipements.ventilateur;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class VentilateurConnector
extends    AbstractConnector
implements VentilateurUserCI 

{


	@Override
	public VentilateurState getState() throws Exception {
		return ((VentilateurUserCI)this.offering).getState();
	}

	@Override
	public VentilateurMode getMode() throws Exception {
		return ((VentilateurUserCI)this.offering).getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((VentilateurUserCI)this.offering).turnOn();
		
	}

	@Override
	public void turnOff() throws Exception {
		((VentilateurUserCI)this.offering).turnOff();
		
	}

	@Override
	public void setHigh() throws Exception {
		((VentilateurUserCI)this.offering).setHigh();
		
	}

	@Override
	public void setMedium() throws Exception {
		((VentilateurUserCI)this.offering).setMedium();
	}

	@Override
	public void setLow() throws Exception {
		((VentilateurUserCI)this.offering).setLow();
		
	}

}
