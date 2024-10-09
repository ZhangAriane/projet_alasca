package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class PanneauSolaireInboundPort extends		AbstractInboundPort implements PanneauSolaireCI{

	private static final long serialVersionUID = 1L;

	public PanneauSolaireInboundPort( ComponentI owner)
			throws Exception {
		super(PanneauSolaireCI.class, owner);
		
	}

	public				PanneauSolaireInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, PanneauSolaireCI.class, owner);
		
	}






}
