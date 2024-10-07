package projet_alasca.equipements.chauffeEau;

public interface ChauffeEauExternalControlI
extends ChauffeEauTemperatureI 
{
	public double		getMaxPowerLevel() throws Exception;
	public void			setCurrentPowerLevel(double powerLevel) throws Exception;
	public double		getCurrentPowerLevel() throws Exception;
}
