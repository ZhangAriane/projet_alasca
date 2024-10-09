package projet_alasca.equipements.ventilateur;

public interface VentilateurImplementationI 
{
	
	
	public static enum VentilateurState{
		//la ventilateur a deux états : allumée ou éteinte 
		ON,
		OFF
	}
	
	public static enum VentilateurMode{
		// la ventilateur a 3 modes : low , medium et high 
		LOW, 
		MEDIUM,
		HIGH
	}

	
	public VentilateurState getState()throws Exception;
	
	public VentilateurMode getMode() throws Exception;
	
	public void	turnOn() throws Exception;
	public void	turnOff() throws Exception;
	
	
	public void	setHigh() throws Exception;
	public void	setMedium() throws Exception;
	public void	setLow() throws Exception;


}
