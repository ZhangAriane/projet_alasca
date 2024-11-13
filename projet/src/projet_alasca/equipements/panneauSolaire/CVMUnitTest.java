package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMUnitTest extends AbstractCVM {
	
	 public CVMUnitTest() throws Exception {
	        
	    }
	 
	 @Override
	    public void deploy() throws Exception {
	        // Deploy the SolarPanel component
	        AbstractComponent.createComponent(
	               PanneauSolaire.class.getCanonicalName(),
	                new Object[]{}); // Add any constructor arguments if needed

	        // Deploy the SolarPanelTester component, which tests the SolarPanel
	        AbstractComponent.createComponent(
	        		PanneauSolaireTester.class.getCanonicalName(),
	                new Object[]{true}); // Add any constructor arguments if needed

	        super.deploy();
	    }
	 
	 public static void main(String[] args) {
	        try {
	            CVMUnitTest cvm = new CVMUnitTest();
	            // Start the standard lifecycle with a specified duration (e.g., 100 seconds)
	            cvm.startStandardLifeCycle(100000L);
	            // Allow time to observe results before shutdown
	            Thread.sleep(10000L);
	            // Exit the application
	            System.exit(0);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }




}
