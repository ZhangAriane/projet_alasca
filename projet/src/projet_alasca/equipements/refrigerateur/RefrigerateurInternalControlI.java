package projet_alasca.equipements.refrigerateur;

public interface RefrigerateurInternalControlI extends RefrigerateurTemperateurI{
	
	public boolean		heating() throws Exception;

	public void			startHeating() throws Exception;


	public void			stopHeating() throws Exception;

}
