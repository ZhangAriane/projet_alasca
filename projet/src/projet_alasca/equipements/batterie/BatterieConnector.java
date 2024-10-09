package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class BatterieConnector extends		AbstractConnector implements BatterieCI{

	@Override
	public State getState() throws Exception{
		return ((BatterieCI)this.offering).getState();
	}


}
