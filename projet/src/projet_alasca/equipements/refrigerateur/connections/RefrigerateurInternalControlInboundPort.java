package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlCI;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlI;

public class RefrigerateurInternalControlInboundPort extends		AbstractInboundPort implements RefrigerateurInternalControlCI{

	private static final long serialVersionUID = 1L;

	public RefrigerateurInternalControlInboundPort( ComponentI owner)
			throws Exception {
		super(RefrigerateurInternalControlCI.class, owner);

		assert	owner instanceof RefrigerateurInternalControlI :
			new PreconditionException(
					"owner instanceof RefrigerateurInternalControlI");
	}

	public RefrigerateurInternalControlInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RefrigerateurInternalControlCI.class, owner);

		assert	owner instanceof RefrigerateurInternalControlI :
			new PreconditionException(
					"owner instanceof RefrigerateurInternalControlI");
	}

	@Override
	public double getRefrigeratorTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getRefrigeratorTargetTemperature());
	}

	@Override
	public double getFreezerTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getFreezerTargetTemperature());

	}

	@Override
	public double getRefrigeratorCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getRefrigeratorCurrentTemperature());

	}

	@Override
	public double getFreezerCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getFreezerCurrentTemperature());

	}

	@Override
	public boolean cooling() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).cooling());
	}

	@Override
	public void startCooling() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurInternalControlI)o).
					startCooling();
				return null;
				});
		
	}

	@Override
	public void stopCooling() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurInternalControlI)o).
					stopCooling();
				return null;
				});
		
	}

	@Override
	public boolean freezing() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).freezing());
	}

	@Override
	public void startFreezing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurInternalControlI)o).
					startFreezing();
				return null;
				});
		
	}

	@Override
	public void stopFreezing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigerateurInternalControlI)o).
					stopFreezing();
				return null;
				});
		
	}

}
