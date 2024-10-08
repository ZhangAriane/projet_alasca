package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurUserI extends RefrigerateurExternalControlI{

	public boolean		on() throws Exception;

	 
	public void			switchOn() throws Exception;


	public void			switchOff() throws Exception;


	public void			setRefrigeratorTargetTemperature(double target)
	throws Exception;
	
	public void			setFreezerTargetTemperature(double target)
			throws Exception;
}
