package projet_alasca.equipements.batterie;


public interface BatterieI{

	/*public enum State{
		CONSUME,
		PRODUCT
	}

	public State getState() throws Exception;*/
	
	public boolean isFull() throws Exception;
	public boolean isEmpty() throws Exception;
	
	// doit etre !isFull() pour qu'il puisse produire de l'energie
	public void ConsumeEnergy(double energy) throws Exception;
	
	// doit etre !isEmty() pour qu'il puisse produire de l'energie
	public void provideEnergy(double energy) throws Exception;
	
	public double getEnergyLevel() throws Exception;

}
