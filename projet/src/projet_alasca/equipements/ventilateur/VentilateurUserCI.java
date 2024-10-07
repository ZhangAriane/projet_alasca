package projet_alasca.equipements.ventilateur;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface VentilateurUserCI 
extends  OfferedCI,
		RequiredCI,
		VentilateurImplementationI
{

	@Override
	public VentilateurState	getState() throws Exception;
	
	@Override
	public VentilateurMode	getMode() throws Exception;

	@Override
	public void	turnOn() throws Exception;

	@Override
	public void	turnOff() throws Exception;

	@Override
	public void	setHigh() throws Exception;
	
	@Override
	public void	setMedium() throws Exception;
	
	@Override
	public void	setLow() throws Exception;

}
