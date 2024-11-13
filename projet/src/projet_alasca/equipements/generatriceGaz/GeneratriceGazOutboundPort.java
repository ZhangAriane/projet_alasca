package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlCI;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class GeneratriceGazOutboundPort extends		AbstractOutboundPort implements GeneratriceGazCI{

	private static final long serialVersionUID = 1L;

	public GeneratriceGazOutboundPort(ComponentI owner)
			throws Exception {
		super(GeneratriceGazCI.class, owner);
	}

	public				GeneratriceGazOutboundPort(String uri, ComponentI owner)
			throws Exception
	{
		super(uri, GeneratriceGazCI.class, owner);
	}

	@Override
	public State getState() throws Exception {
		return ((GeneratriceGazCI)this.getConnector()).
				getState();
	}

	@Override
	public Mode getMode() throws Exception {
		return ((GeneratriceGazCI)this.getConnector()).
				getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((GeneratriceGazCI)this.getConnector()).
		turnOn();
		
	}

	@Override
	public void turnOff() throws Exception {
		((GeneratriceGazCI)this.getConnector()).
		turnOff();
		
	}

	@Override
	public void setHigh() throws Exception {
		((GeneratriceGazCI)this.getConnector()).
		setHigh();
		
	}

	@Override
	public void setLow() throws Exception {
		((GeneratriceGazCI)this.getConnector()).
		setLow();
		
	}

	@Override
	public void setMeddium() throws Exception {
		((GeneratriceGazCI)this.getConnector()).
		setMeddium();
		
	}

	

	

}
