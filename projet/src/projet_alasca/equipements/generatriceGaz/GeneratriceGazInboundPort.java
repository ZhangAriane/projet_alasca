package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class GeneratriceGazInboundPort extends		AbstractInboundPort implements GeneratriceGazCI{

	private static final long serialVersionUID = 1L;

	public GeneratriceGazInboundPort( ComponentI owner)
			throws Exception {
		super(GeneratriceGazCI.class, owner);
		
	}

	public				GeneratriceGazInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, GeneratriceGazCI.class, owner);
		
	}






}
