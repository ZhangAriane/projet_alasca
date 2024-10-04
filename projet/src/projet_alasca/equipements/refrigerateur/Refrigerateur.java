package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.hem2024.bases.RegistrationCI;

@RequiredInterfaces(required = {RegistrationCI.class})
@OfferedInterfaces(offered={RefrigerateurUserCI.class, RefrigerateurInternalControlCI.class, RefrigerateurExternalControlCI.class})
public class Refrigerateur extends AbstractComponent{

	protected Refrigerateur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

}
