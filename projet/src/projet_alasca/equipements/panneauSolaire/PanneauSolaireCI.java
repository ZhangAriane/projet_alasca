package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;



public interface PanneauSolaireCI 
extends OfferedCI,
		RequiredCI,
		 PanneauSolaireI
{

	@Override
	public void startProduce() throws Exception; 

	@Override
	public void stopProduce() throws Exception;

	@Override
	public double getEnergyProduction() throws Exception;

	@Override
	public void setEnergyProduction(double energy) throws Exception;

	@Override
	public boolean isOn() throws Exception;
	
}
