package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;



public interface BatterieCI 
extends OfferedCI,
		RequiredCI,
		BatterieI
{
	@Override
	public boolean isFull() throws Exception;

	@Override
	public boolean isEmpty() throws Exception;

	@Override
	public void ConsumeEnergy(double energy) throws Exception;

	@Override
	public void provideEnergy(double energy) throws Exception;
	
	@Override
	public double getEnergyLevel() throws Exception;
	
}
