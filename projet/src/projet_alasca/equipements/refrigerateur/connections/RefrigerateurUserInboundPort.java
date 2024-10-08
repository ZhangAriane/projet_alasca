package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlI;
import projet_alasca.equipements.refrigerateur.RefrigerateurTemperatureI;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserCI;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserI;

public class RefrigerateurUserInboundPort extends		AbstractInboundPort implements RefrigerateurUserCI{

	private static final long serialVersionUID = 1L;

	public RefrigerateurUserInboundPort( ComponentI owner)
			throws Exception {
		super(RefrigerateurUserCI.class, owner);
		}
	
	public RefrigerateurUserInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RefrigerateurUserCI.class, owner);
		}

	@Override
	public double getRefrigeratorTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperatureI)o).getRefrigeratorTargetTemperature());
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperatureI)o).getFreezerTargetTemperature());
	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperatureI)o).getRefrigeratorCurrentTemperature());
	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperatureI)o).getFreezerCurrentTemperature());
	}

	@Override
	public boolean on() throws Exception {

		return this.getOwner().handleRequest(o -> ((RefrigerateurUserI)o).on());
	}

	@Override
	public void switchOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurUserI)o).switchOn();;
						return null;
				});
	}

	@Override
	public void switchOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurUserI)o).switchOff();;
						return null;
				});
}

	@Override
	public void setRefrigeratorTargetTemperature(double target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurUserI)o).setRefrigeratorTargetTemperature(target);;
						return null;
				});
}

	@Override
	public void setFreezerTargetTemperature(double target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurUserI)o).setFreezerTargetTemperature(target);;
						return null;
				});
}

	@Override
	public double getMaxPowerLevel() throws Exception {

		return this.getOwner().handleRequest(o -> ((RefrigerateurExternalControlI)o).getMaxPowerLevel());
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurExternalControlI)o).setCurrentPowerLevel(powerLevel);;
						return null;
				});
	}

	@Override
	public double getCurrentPowerLevel() throws Exception {

		return this.getOwner().handleRequest(o -> ((RefrigerateurExternalControlI)o).getCurrentPowerLevel());
	}

}
