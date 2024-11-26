package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;


public class CVMUnitTest 
extends AbstractCVM 
{

	public CVMUnitTest() throws Exception 
	{
		GeneratriceGazTester.VERBOSE = true;
		GeneratriceGazTester.X_RELATIVE_POSITION = 0;
		GeneratriceGazTester.Y_RELATIVE_POSITION = 0;
		GeneratriceGaz.VERBOSE = true;
		GeneratriceGaz.X_RELATIVE_POSITION = 1;
		GeneratriceGaz.Y_RELATIVE_POSITION = 0;
	}


	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				GeneratriceGaz.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					GeneratriceGazTester.class.getCanonicalName(),
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
