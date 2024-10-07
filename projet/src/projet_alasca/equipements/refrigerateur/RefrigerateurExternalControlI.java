package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurExternalControlI extends RefrigerateurTemperatureI{

	public double		getMaxPowerLevel() throws Exception;

	public void			setCurrentPowerLevel(double powerLevel) throws Exception;

	public double		getCurrentPowerLevel() throws Exception;
	
	
}
