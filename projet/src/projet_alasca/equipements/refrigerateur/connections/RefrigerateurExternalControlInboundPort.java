package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlCI;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlI;
import projet_alasca.equipements.refrigerateur.RefrigerateurTemperatureI;

public class RefrigerateurExternalControlInboundPort extends		AbstractInboundPort implements RefrigerateurExternalControlCI{

	private static final long serialVersionUID = 1L;

	public RefrigerateurExternalControlInboundPort( ComponentI owner)
			throws Exception {
		super(RefrigerateurExternalControlCI.class, owner);
		assert	owner instanceof RefrigerateurExternalControlI :
			new PreconditionException(
					"owner instanceof RefrigerateurExternalControlI");
	}
	
	
	public RefrigerateurExternalControlInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RefrigerateurExternalControlCI.class, owner);
		assert	owner instanceof RefrigerateurExternalControlI :
			new PreconditionException(
					"owner instanceof RefrigerateurExternalControlI");
	}

	@Override
	public double getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurTemperatureI)o).getTargetTemperature());
	
	}


	@Override
	public double getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurTemperatureI)o).getCurrentTemperature());
	
	}

	
	
	@Override
	public double getMaxPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurExternalControlI)o).getMaxPowerLevel());
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {
		this.getOwner().handleRequest(
				o -> {((RefrigerateurExternalControlI)o).setCurrentPowerLevel(powerLevel);
				return null;
				});

	}

	@Override
	public double getCurrentPowerLevel() throws Exception {

		return this.getOwner().handleRequest(
				o -> ((RefrigerateurExternalControlI)o).getCurrentPowerLevel());
	}
	
	
}
