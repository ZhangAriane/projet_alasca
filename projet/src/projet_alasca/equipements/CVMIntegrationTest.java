package projet_alasca.equipements;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2024e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.exceptions.ContractException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.equipements.batterie.Batterie;
import projet_alasca.equipements.chauffeEau.ChauffeEau;
import projet_alasca.equipements.chauffeEau.ChauffeEauTester;
import projet_alasca.equipements.generatriceGaz.GeneratriceGaz;
import projet_alasca.equipements.gestionEnergie.GestionEnergie;
import projet_alasca.equipements.machineCafe.MachineCafe;
import projet_alasca.equipements.machineCafe.MachineCafeTester;
import projet_alasca.equipements.panneauSolaire.PanneauSolaire;
import projet_alasca.equipements.refrigerateur.Refrigerateur;
import projet_alasca.equipements.refrigerateur.RefrigerateurTester;
import projet_alasca.equipements.ventilateur.Ventilateur;
import projet_alasca.equipements.ventilateur.VentilateurTester;


public class CVMIntegrationTest extends		AbstractCVM{

	/** for integration tests, a {@code Clock} is used to get a
	 *  time-triggered synchronisation of the actions of the components
	 *  in the test scenarios.												*/
	public static final String	CLOCK_URI = "test-clock";
	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.					*/
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static final double	ACCELERATION_FACTOR = 1.0;
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static final Instant	START_INSTANT =
									Instant.parse("2024-09-18T14:00:00.00Z");

	public				CVMIntegrationTest() throws Exception
	{
		// Trace and trace window positions
		ContractException.VERBOSE = true;
		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;
		GestionEnergie.VERBOSE = true;
		GestionEnergie.X_RELATIVE_POSITION = 0;
		GestionEnergie.Y_RELATIVE_POSITION = 1;
		ElectricMeter.VERBOSE = true;
		ElectricMeter.X_RELATIVE_POSITION = 1;
		ElectricMeter.Y_RELATIVE_POSITION = 1;
		
		MachineCafeTester.VERBOSE = true;
		MachineCafeTester.X_RELATIVE_POSITION = 0;
		MachineCafeTester.Y_RELATIVE_POSITION = 2;
		MachineCafe.VERBOSE = true;
		MachineCafe.X_RELATIVE_POSITION = 1;
		MachineCafe.Y_RELATIVE_POSITION = 2;
		
		RefrigerateurTester.VERBOSE = true;
		RefrigerateurTester.X_RELATIVE_POSITION = 0;
		RefrigerateurTester.Y_RELATIVE_POSITION = 3;
		Refrigerateur.VERBOSE = true;
		Refrigerateur.X_RELATIVE_POSITION = 1;
		Refrigerateur.Y_RELATIVE_POSITION = 3;
		
		VentilateurTester.VERBOSE = true;
		VentilateurTester.X_RELATIVE_POSITION = 2;
		VentilateurTester.Y_RELATIVE_POSITION = 2;
		Ventilateur.VERBOSE = true;
		Ventilateur.X_RELATIVE_POSITION = 3;
		Ventilateur.Y_RELATIVE_POSITION = 2;
		
		ChauffeEauTester.VERBOSE = true;
		ChauffeEauTester.X_RELATIVE_POSITION = 2;
		ChauffeEauTester.Y_RELATIVE_POSITION = 3;
		ChauffeEau.VERBOSE = true;
		ChauffeEau.X_RELATIVE_POSITION = 3;
		ChauffeEau.Y_RELATIVE_POSITION = 3;
		
		GeneratriceGaz.VERBOSE = true;
		GeneratriceGaz.X_RELATIVE_POSITION = 3;
		GeneratriceGaz.Y_RELATIVE_POSITION = 1;
		
		PanneauSolaire.VERBOSE = true;
		PanneauSolaire.X_RELATIVE_POSITION = 2;
		PanneauSolaire.Y_RELATIVE_POSITION = 1;
		
		Batterie.VERBOSE = true;
		Batterie.X_RELATIVE_POSITION = 3;
		Batterie.Y_RELATIVE_POSITION = 0;

	}
	
	@Override
	public void			deploy() throws Exception
	{
		// start time in Unix epoch time in nanoseconds.
		long unixEpochStartTimeInMillis = 
				System.currentTimeMillis() + DELAY_TO_START_IN_MILLIS;

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
										 		unixEpochStartTimeInMillis),
						START_INSTANT,
						ACCELERATION_FACTOR});

		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				MachineCafe.class.getCanonicalName(),
				new Object[]{});
		// At this stage, the tester for the hair dryer is added only
		// to show the hair dryer functioning; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				MachineCafeTester.class.getCanonicalName(),
				new Object[]{false});

		AbstractComponent.createComponent(
				Refrigerateur.class.getCanonicalName(),
				new Object[]{});

		// At this stage, the tester for the heater is added only
		// to switch on and off the heater; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				RefrigerateurTester.class.getCanonicalName(),
				new Object[]{false});
		
		
		// *********** Ventilateur ****************
		AbstractComponent.createComponent(
				Ventilateur.class.getCanonicalName(),
				new Object[]{});
		AbstractComponent.createComponent(
				VentilateurTester.class.getCanonicalName(),
				new Object[]{false});

		// *********** ChauffeEau ****************
		
		AbstractComponent.createComponent(
				ChauffeEau.class.getCanonicalName(),
				new Object[]{});
		AbstractComponent.createComponent(
				ChauffeEauTester.class.getCanonicalName(),
				new Object[]{false});
		
		
		
		AbstractComponent.createComponent(
				GestionEnergie.class.getCanonicalName(),
				new Object[]{});
		
		
		AbstractComponent.createComponent(
				Batterie.class.getCanonicalName(),
				new Object[]{});
		
		AbstractComponent.createComponent(
				PanneauSolaire.class.getCanonicalName(),
				new Object[]{});
		
		AbstractComponent.createComponent(
				GeneratriceGaz.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
			cvm.startStandardLifeCycle(240000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
