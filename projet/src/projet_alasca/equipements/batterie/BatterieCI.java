package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;



public interface BatterieCI 
extends OfferedCI,
RequiredCI,
BatterieI
{
	@Override
	public BatterieState getState() throws Exception;

	@Override
	public void swicthConsume()throws Exception;

	@Override
	public void switchProduct()throws Exception;

}
