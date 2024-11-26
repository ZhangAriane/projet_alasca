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
	public GeneratriceGazState getState() throws Exception {
		return ((GeneratriceGazCI)this.getConnector()).
				getState();
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

	

}
