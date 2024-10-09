package projet_alasca.equipements.batterie;


public interface BatterieI{

	public enum State{
		CONSUME,
		PRODUCT
	}

	public State getState() throws Exception;

}
