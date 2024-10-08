package projet_alasca.equipements.machineCafe;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMUnitTest extends		AbstractCVM{


	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		MachineCafeTester.VERBOSE = true;
		MachineCafeTester.X_RELATIVE_POSITION = 0;
		MachineCafeTester.Y_RELATIVE_POSITION = 0;
		MachineCafe.VERBOSE = true;
		MachineCafe.X_RELATIVE_POSITION = 1;
		MachineCafe.Y_RELATIVE_POSITION = 0;
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
				MachineCafe.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				MachineCafeTester.class.getCanonicalName(),
				new Object[]{true});

		super.deploy();
	}

	public static void		main(String[] args)
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
