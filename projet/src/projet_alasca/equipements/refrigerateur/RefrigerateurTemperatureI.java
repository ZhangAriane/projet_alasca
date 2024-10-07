package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurTemperatureI {
	
	public double		getRefrigeratorTargetTemperature() throws Exception
	;
	public double		getFreezerTargetTemperature() throws Exception;
	
	public double		getRefrigeratorCurrentTemperature() throws Exception;
	
	public double		getFreezerCurrentTemperature() throws Exception;
	
	
	
}
