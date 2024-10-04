package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurUserI extends RefrigerateurExternalControlI{

	public boolean		on() throws Exception;

	 
	public void			switchOn() throws Exception;


	public void			switchOff() throws Exception;


	public void			setTargetTemperature(double target)
	throws Exception;
	
	public void			setCongelateurTargetTemperature(double target)
			throws Exception;
}
