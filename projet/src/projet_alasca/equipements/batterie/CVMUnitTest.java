package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;


public class CVMUnitTest 
extends AbstractCVM 
{

	public CVMUnitTest() throws Exception 
	{
		BatterieTester.VERBOSE = true;
		BatterieTester.X_RELATIVE_POSITION = 0;
		BatterieTester.Y_RELATIVE_POSITION = 0;
		Batterie.VERBOSE = true;
		Batterie.X_RELATIVE_POSITION = 1;
		Batterie.Y_RELATIVE_POSITION = 0;
	}


	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Batterie.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
				BatterieTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}
	
	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(100000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
