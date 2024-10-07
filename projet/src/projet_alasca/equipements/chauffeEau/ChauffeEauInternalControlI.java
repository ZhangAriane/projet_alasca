package projet_alasca.equipements.chauffeEau;

public interface ChauffeEauInternalControlI 
extends ChauffeEauTemperatureI 
{

	public boolean		heating() throws Exception;

	public void			startHeating() throws Exception;

	public void			stopHeating() throws Exception;
}
