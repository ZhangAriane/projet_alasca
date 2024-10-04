package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurTemperateurI {
	public double		getTargetTemperature() throws Exception;
	public double		getCongelateurTargetTemperature() throws Exception;
	
	public double		getCurrentTemperature() throws Exception;
	public double		getCongelateurCurrentTemperature() throws Exception;
	
	
	
}
