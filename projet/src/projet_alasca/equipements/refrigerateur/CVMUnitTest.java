package projet_alasca.equipements.refrigerateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import projet_alasca.equipements.gestionEnergie.GestionEnergie;

public class CVMUnitTest extends AbstractCVM {
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		RefrigerateurTester.VERBOSE = true;
		RefrigerateurTester.X_RELATIVE_POSITION = 0;
		RefrigerateurTester.Y_RELATIVE_POSITION = 0;
		Refrigerateur.VERBOSE = true;
		Refrigerateur.X_RELATIVE_POSITION = 1;
		Refrigerateur.Y_RELATIVE_POSITION = 0;
	}


	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Refrigerateur.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				RefrigerateurTester.class.getCanonicalName(),
				new Object[]{true});
		
		
		AbstractComponent.createComponent(
				GestionEnergie.class.getCanonicalName(),
				new Object[]{true,false});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
