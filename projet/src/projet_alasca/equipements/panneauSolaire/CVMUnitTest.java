package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;


public class CVMUnitTest 
extends AbstractCVM 
{

	public CVMUnitTest() throws Exception 
	{
		PanneauSolaireTester.VERBOSE = true;
		PanneauSolaireTester.X_RELATIVE_POSITION = 0;
		PanneauSolaireTester.Y_RELATIVE_POSITION = 0;
		PanneauSolaire.VERBOSE = true;
		PanneauSolaire.X_RELATIVE_POSITION = 1;
		PanneauSolaire.Y_RELATIVE_POSITION = 0;
	}


	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
					PanneauSolaire.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
				PanneauSolaireTester.class.getCanonicalName(),
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
