package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserCI;

public class RefrigerateurUserOutboundPort extends		AbstractOutboundPort implements RefrigerateurUserCI{

	private static final long serialVersionUID = 1L;
	
	public RefrigerateurUserOutboundPort(ComponentI owner)
			throws Exception {
		super(RefrigerateurUserCI.class, owner);
	}

	public RefrigerateurUserOutboundPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, RefrigerateurUserCI.class, owner);
	}

	@Override
	public double getTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getTargetTemperature();
	}


	@Override
	public double getCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getCurrentTemperature();
	}

	@Override
	public boolean on() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).on();
	}

	@Override
	public void switchOn() throws Exception {

		 ((RefrigerateurUserCI)this.getConnector()).switchOn();
	}

	@Override
	public void switchOff() throws Exception {

		((RefrigerateurUserCI)this.getConnector()).switchOff();
	}

	@Override
	public void setTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.getConnector()).setTargetTemperature(target);
	}


	@Override
	public double getMaxPowerLevel() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {

		((RefrigerateurUserCI)this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public double getCurrentPowerLevel() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getCurrentPowerLevel();
	}
	

}
