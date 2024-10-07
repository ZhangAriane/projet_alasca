package projet_alasca.equipements.chauffeEau;

public interface ChauffeEauUserI 
extends ChauffeEauExternalControlI
{
	public boolean	on() throws Exception;

	public void	 switchOn() throws Exception;

	public void	switchOff() throws Exception;

	public void	setTargetTemperature(double target)
	throws Exception;
	
}
