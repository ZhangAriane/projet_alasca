package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class BatterieConnector extends		AbstractConnector implements BatterieCI{

	/*@Override
	public State getState() throws Exception{
		return ((BatterieCI)this.offering).getState();
	}*/
	
	@Override
	public boolean isFull() throws Exception {
		return ((BatterieCI)this.offering).isFull();
	}

	@Override
	public boolean isEmpty() throws Exception {
		return ((BatterieCI)this.offering).isEmpty();
	}

	@Override
	public void ConsumeEnergy(double energy) throws Exception {
		((BatterieCI)this.offering).ConsumeEnergy(energy);
	}

	@Override
	public void provideEnergy(double energy) throws Exception {
		((BatterieCI)this.offering).provideEnergy(energy);
	}

	@Override
	public double getEnergyLevel() throws Exception {
		return ((BatterieCI)this.offering).getEnergyLevel();
	}

}
