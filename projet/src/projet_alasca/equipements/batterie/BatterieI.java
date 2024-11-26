package projet_alasca.equipements.batterie;


public interface BatterieI{

	public enum BatterieState{
		CONSUME,
		PRODUCT
	}

	public BatterieState getState() throws Exception;
	
	public void swicthConsume()throws Exception;
	
	public void switchProduct()throws Exception;

}
