package projet_alasca.equipements.projecteur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlI;
import projet_alasca.equipements.refrigerateur.RefrigerateurTemperateurI;
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
	public double getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperateurI)o).getTargetTemperature());
	}

	@Override
	public double getCongelateurTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperateurI)o).getCongelateurTargetTemperature());
	}

	@Override
	public double getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperateurI)o).getCurrentTemperature());
	}

	@Override
	public double getCongelateurCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurTemperateurI)o).getCongelateurCurrentTemperature());
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
	public void setTargetTemperature(double target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurUserI)o).setTargetTemperature(target);;
						return null;
				});
}

	@Override
	public void setCongelateurTargetTemperature(double target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurUserI)o).setCongelateurTargetTemperature(target);;
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
