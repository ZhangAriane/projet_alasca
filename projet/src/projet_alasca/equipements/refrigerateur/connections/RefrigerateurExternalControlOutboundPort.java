package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlCI;

public class RefrigerateurExternalControlOutboundPort extends		AbstractOutboundPort implements RefrigerateurExternalControlCI{

	private static final long serialVersionUID = 1L;

	public RefrigerateurExternalControlOutboundPort(ComponentI owner)
			throws Exception {
		super(RefrigerateurExternalControlCI.class, owner);
	}
	
	public RefrigerateurExternalControlOutboundPort(String uri,ComponentI owner)
			throws Exception {
		super(uri,RefrigerateurExternalControlCI.class, owner);
	}
	
	@Override
	public double getTargetTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.getConnector()).
				getTargetTemperature();
	}



	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigerateurExternalControlCI)this.getConnector()).
				getCurrentTemperature();
	}



	@Override
	public double getMaxPowerLevel() throws Exception {
		return ((RefrigerateurExternalControlCI)this.getConnector()).
				getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {
		((RefrigerateurExternalControlCI)this.getConnector()).
		setCurrentPowerLevel(powerLevel);
	}

	@Override
	public double getCurrentPowerLevel() throws Exception {
		return ((RefrigerateurExternalControlCI)this.getConnector()).
				getCurrentPowerLevel();
	}
	

	
}
