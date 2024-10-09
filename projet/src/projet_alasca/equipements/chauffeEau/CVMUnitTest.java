package projet_alasca.equipements.chauffeEau;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMUnitTest extends AbstractCVM {

	public				CVMUnitTest() throws Exception
	{
		ChauffeEauTester.VERBOSE = true;
		ChauffeEauTester.X_RELATIVE_POSITION = 0;
		ChauffeEauTester.Y_RELATIVE_POSITION = 0;
		ChauffeEau.VERBOSE = true;
		ChauffeEau.X_RELATIVE_POSITION = 1;
		ChauffeEau.Y_RELATIVE_POSITION = 0;
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
				ChauffeEau.class.getCanonicalName(),
				new Object[]{true});

		AbstractComponent.createComponent(
				ChauffeEauTester.class.getCanonicalName(),
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
