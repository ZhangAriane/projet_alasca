package projet_alasca.equipements.ventilateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;


public class CVMUnitTest 
extends AbstractCVM 
{

	public CVMUnitTest() throws Exception 
	{
		VentilateurTester.VERBOSE = true;
		VentilateurTester.X_RELATIVE_POSITION = 0;
		VentilateurTester.Y_RELATIVE_POSITION = 0;
		Ventilateur.VERBOSE = true;
		Ventilateur.X_RELATIVE_POSITION = 1;
		Ventilateur.Y_RELATIVE_POSITION = 0;
	}


	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
					Ventilateur.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					VentilateurTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}
	
	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
