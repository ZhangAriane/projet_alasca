package projet_alasca.equipements.generatriceGaz;


public interface GeneratriceGazI{

	public static enum GeneratriceGazState {
		ON,
		OFF
	}
	
	
	public GeneratriceGazState getState() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	

}
