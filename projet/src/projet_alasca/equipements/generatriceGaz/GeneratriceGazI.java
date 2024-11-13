package projet_alasca.equipements.generatriceGaz;


public interface GeneratriceGazI{

	public static enum State {
		ON,
		OFF
	}
	
	public static enum Mode {
		LOW,			
		MEDDIUM,
		HIGH
	}
	
	public State getState() throws Exception;
	
	public Mode getMode() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public void	setHigh() throws Exception;

	public void	setLow() throws Exception;
	
	public void	setMeddium() throws Exception;

}
