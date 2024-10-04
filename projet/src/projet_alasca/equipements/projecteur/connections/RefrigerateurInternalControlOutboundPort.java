package projet_alasca.equipements.projecteur.connections;

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
	public boolean heating() throws Exception {

		return ((RefrigerateurInternalControlCI)this.getConnector()).heating();
	}

	@Override
	public void startHeating() throws Exception {

		((RefrigerateurInternalControlCI)this.getConnector()).startHeating();
	}

	@Override
	public void stopHeating() throws Exception {

		((RefrigerateurInternalControlCI)this.getConnector()).stopHeating();
	}

	@Override
	public double getTargetTemperature() throws Exception {

		return ((RefrigerateurInternalControlCI)this.getConnector()).getTargetTemperature();
	}

	@Override
	public double getCongelateurTargetTemperature() throws Exception {

		return ((RefrigerateurInternalControlCI)this.getConnector()).getCongelateurTargetTemperature();
	}

	@Override
	public double getCurrentTemperature() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCongelateurCurrentTemperature() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
