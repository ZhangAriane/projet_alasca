package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurInternalControlI extends RefrigerateurTemperatureI{
	
	public boolean		cooling()throws Exception;
	
	public void			startCooling() throws Exception;

	public void			stopCooling() throws Exception;
	
	public boolean		freezing() throws Exception;

	public void			startFreezing() throws Exception;

	public void			stopFreezing() throws Exception;

}