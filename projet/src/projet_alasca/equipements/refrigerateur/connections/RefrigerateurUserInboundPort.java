package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlI;
import projet_alasca.equipements.refrigerateur.RefrigerateurTemperatureI;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserCI;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserI;

public class RefrigerateurUserInboundPort extends		AbstractInboundPort implements RefrigerateurUserCI{

	private static final long serialVersionUID = 1L;

	public RefrigerateurUserInboundPort( ComponentI owner)
			throws Exception {
		super(RefrigerateurUserCI.class, owner);
		assert	owner instanceof RefrigerateurUserI :
			new PreconditionException("owner instanceof RefrigerateurUserI");
		}
	
	public RefrigerateurUserInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RefrigerateurUserCI.class, owner);
		assert	owner instanceof RefrigerateurUserI :
			new PreconditionException("owner instanceof RefrigerateurUserI");
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
	
	@Override
	public void switchOnRefrigeratorCompressor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurExternalControlI)o).switchOnRefrigeratorCompressor();;
						return null;
				});
		
	}

	@Override
	public void switchOffRefrigeratorCompressor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurExternalControlI)o).switchOffRefrigeratorCompressor();;
						return null;
				});
		
	}

	@Override
	public void switchOnFreezerCompressor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurExternalControlI)o).switchOnFreezerCompressor();;
						return null;
				});
		
	}

	@Override
	public void switchOffFreezerCompressor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurExternalControlI)o).switchOffFreezerCompressor();;
						return null;
				});
		
	}

	@Override
	public boolean onRefrigeratorCompressor() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurExternalControlI)o).onRefrigeratorCompressor());
	}

	@Override
	public boolean onFreezerCompressor() throws Exception {
		return this.getOwner().handleRequest(o -> ((RefrigerateurExternalControlI)o).onFreezerCompressor());
	}

}