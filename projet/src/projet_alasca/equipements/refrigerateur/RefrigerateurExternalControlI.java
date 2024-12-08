package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurExternalControlI extends RefrigerateurTemperatureI{

	public double		getMaxPowerLevel() throws Exception;

	public void			setCurrentPowerLevel(double powerLevel) throws Exception;

	public double		getCurrentPowerLevel() throws Exception;
	
	public void switchOnRefrigeratorCompressor() throws Exception;

	public void switchOffRefrigeratorCompressor() throws Exception;

	public void switchOnFreezerCompressor() throws Exception;

	public void switchOffFreezerCompressor() throws Exception;
	
	
	public boolean onRefrigeratorCompressor() throws Exception;

	public boolean onFreezerCompressor() throws Exception;
	
}