package projet_alasca.equipements.gestionEnergie;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;

public class RegistrationConnector extends AbstractConnector implements RegistrationCI{

	@Override
	public boolean registered(String uid) throws Exception {
		return ((RegistrationCI)this.offering).registered(uid);
	}

	@Override
	public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
		return ((RegistrationCI)this.offering).register(uid,controlPortURI,xmlControlAdapter);
	}

	@Override
	public void unregister(String uid) throws Exception {
		((RegistrationCI)this.offering).unregister(uid);
		
	}

}
