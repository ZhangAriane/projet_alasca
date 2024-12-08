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
	public double getRefrigeratorTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getRefrigeratorTargetTemperature();
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getFreezerTargetTemperature();
	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getRefrigeratorCurrentTemperature();
	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {

		return ((RefrigerateurUserCI)this.getConnector()).getFreezerCurrentTemperature();
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
	public void setRefrigeratorTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.getConnector()).setRefrigeratorTargetTemperature(target);
	}

	@Override
	public void setFreezerTargetTemperature(double target) throws Exception {

		((RefrigerateurUserCI)this.getConnector()).setFreezerTargetTemperature(target);
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
	
	@Override
	public void switchOnRefrigeratorCompressor() throws Exception {
		((RefrigerateurUserCI)this.getConnector()).switchOnRefrigeratorCompressor();

	}

	@Override
	public void switchOffRefrigeratorCompressor() throws Exception {
		((RefrigerateurUserCI)this.getConnector()).switchOffRefrigeratorCompressor();

	}

	@Override
	public void switchOnFreezerCompressor() throws Exception {
		((RefrigerateurUserCI)this.getConnector()).switchOnFreezerCompressor();

	}

	@Override
	public void switchOffFreezerCompressor() throws Exception {
		((RefrigerateurUserCI)this.getConnector()).switchOffFreezerCompressor();

	}

	@Override
	public boolean onRefrigeratorCompressor() throws Exception {
		return ((RefrigerateurUserCI)this.getConnector()).onRefrigeratorCompressor();
	}

	@Override
	public boolean onFreezerCompressor() throws Exception {
		return ((RefrigerateurUserCI)this.getConnector()).onFreezerCompressor();
	}

}