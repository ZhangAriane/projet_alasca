package projet_alasca.equipements.projecteur.connections;

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
	public boolean heating() throws Exception {

		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).heating());
	}

	@Override
	public void startHeating() throws Exception {

		this.getOwner().handleRequest(
				o -> {	((RefrigerateurInternalControlI)o).
					startHeating();
				return null;
				});
	}

	@Override
	public void stopHeating() throws Exception {

		this.getOwner().handleRequest(
				o -> {	((RefrigerateurInternalControlI)o).
					stopHeating();
				return null;
				});
	}

	@Override
	public double getTargetTemperature() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCongelateurTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getCongelateurTargetTemperature());

	}

	@Override
	public double getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getCurrentTemperature());

	}

	@Override
	public double getCongelateurCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigerateurInternalControlI)o).getCongelateurCurrentTemperature());

	}

}
