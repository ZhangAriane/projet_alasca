package projet_alasca.equipements.panneauSolaire;


public interface PanneauSolaireI{
	
	public static enum	PanneauSolaireState
	{
		ON,
		OFF
	}
	
	public boolean		isOn() throws Exception;
	
	public void startProduce() throws Exception;
	
	public void stopProduce() throws Exception;
	
	public double getEnergyProduction() throws Exception;
	
	public void setEnergyProduction(double energy) throws Exception;

}
