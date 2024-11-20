package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlCI;

public class RefrigerateurInternalControlOutboundPort extends		AbstractOutboundPort implements RefrigerateurInternalControlCI {

	private static final long serialVersionUID = 1L;

	public RefrigerateurInternalControlOutboundPort( ComponentI owner)
			throws Exception {
		super(RefrigerateurInternalControlCI.class, owner);
	}
	
	public RefrigerateurInternalControlOutboundPort( String uri, ComponentI owner)
			throws Exception {
		super(uri, RefrigerateurInternalControlCI.class, owner);
	}


	@Override
	public double getTargetTemperature() throws Exception {

		return ((RefrigerateurInternalControlCI)this.getConnector()).getTargetTemperature();
	}


	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigerateurInternalControlCI)this.getConnector()).getCurrentTemperature();
	}


	@Override
	public boolean cooling() throws Exception {
		return ((RefrigerateurInternalControlCI)this.getConnector()).cooling();
	}

	@Override
	public void startCooling() throws Exception {
		((RefrigerateurInternalControlCI)this.getConnector()).startCooling();
	}

	@Override
	public void stopCooling() throws Exception {
		((RefrigerateurInternalControlCI)this.getConnector()).stopCooling();
		
	}



}
