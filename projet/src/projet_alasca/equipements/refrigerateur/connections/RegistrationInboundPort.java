package projet_alasca.equipements.refrigerateur.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {

	private static final long serialVersionUID = 1L;

	public RegistrationInboundPort( ComponentI owner)
			throws Exception {
		super(RegistrationCI.class, owner);
	}
	
	
	public RegistrationInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RegistrationCI.class, owner);
	}
	

	@Override
	public boolean registered(String uid) throws Exception {
		return this.getOwner().handleRequest(o -> ((RegistrationCI)o).registered(uid));
	}

	@Override
	public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
		return this.getOwner().handleRequest(o -> ((RegistrationCI)o).register(uid,controlPortURI,xmlControlAdapter));
	}

	@Override
	public void unregister(String uid) throws Exception {
		this.getOwner().handleRequest(
			o -> {	((RegistrationCI)o).unregister(uid);;
			return null;
	});
	}
}
