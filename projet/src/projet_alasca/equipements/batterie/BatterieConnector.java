package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class BatterieConnector extends		AbstractConnector implements BatterieCI{

	@Override
	public BatterieState getState() throws Exception{
		return ((BatterieCI)this.offering).getState();
	}

	@Override
	public void swicthConsume() throws Exception {
		((BatterieCI)this.offering).swicthConsume();
		
	}

	@Override
	public void switchProduct() throws Exception {
		((BatterieCI)this.offering).switchProduct();
		
	}


}
