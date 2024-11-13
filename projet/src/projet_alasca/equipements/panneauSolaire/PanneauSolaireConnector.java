package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class PanneauSolaireConnector 
extends		AbstractConnector
implements PanneauSolaireCI{

	@Override
	public void startProduce() throws Exception {
		((PanneauSolaireCI)this.offering).startProduce();
	}

	@Override
	public void stopProduce() throws Exception {
		((PanneauSolaireCI)this.offering).stopProduce();
	}

	@Override
	public double getEnergyProduction() throws Exception {
		return ((PanneauSolaireCI)this.offering).getEnergyProduction();
	}

	@Override
	public void setEnergyProduction(double energy) throws Exception {
		((PanneauSolaireCI)this.offering).setEnergyProduction(energy);
	}

	@Override
	public boolean isOn() throws Exception {
		return ((PanneauSolaireCI)this.offering).isOn();
	}
	

}
