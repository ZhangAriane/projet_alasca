package projet_alasca.equipements.batterie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;

@RequiredInterfaces(required = {BatterieCI.class , ClocksServerCI.class})
public class BatterieTester
extends AbstractComponent 
{

	 public static boolean VERBOSE = false;
	    public static int X_RELATIVE_POSITION = 0;
	    public static int Y_RELATIVE_POSITION = 0;

	    protected final boolean isUnitTest;
	    protected BatterieOutboundPort bop;
	    protected String BatterieInboundPortURI;

	    public BatterieTester(boolean isUnitTest) throws Exception {
	        this(isUnitTest, Batterie.INBOUND_PORT_URI);
	    }

	    public BatterieTester(boolean isUnitTest, String BatterieInboundPortURI) throws Exception {
	        super(1, 0);
	        assert BatterieInboundPortURI != null && !BatterieInboundPortURI.isEmpty() :
	                new PreconditionException("BatterieInboundPortURI must not be null or empty");

	        this.isUnitTest = isUnitTest;
	        this.initialise(BatterieInboundPortURI);
	    }

	    protected void initialise(String batterieInboundPortURI) throws Exception {
	        this.BatterieInboundPortURI = batterieInboundPortURI;
	        this.bop = new BatterieOutboundPort(this);
	        this.bop.publishPort();

	        if (VERBOSE) {
	            this.tracer.get().setTitle("Batterie tester component");
	            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
	            this.toggleTracing();
	        }
	    }

	    // -------------------------------------------------------------------------
	    // Méthodes de test
	    // -------------------------------------------------------------------------

	    public void testGetBatteryLevel()  {
			this.logMessage("testGetPowerLevel() ...");	
			try {
				assertEquals(this.bop.getEnergyLevel(), this.bop.getEnergyLevel());
			} catch(Exception e) {
				assertTrue(false);
			}	
			this.logMessage("done... \n");
		}
	    
	    public void testIsFull() {
	        this.logMessage("testIsFull()...");
	        try {
	            boolean full = this.bop.isFull();
	            assertEquals(full, this.bop.isFull());
	        } catch (Exception e) {
	            this.logMessage("...KO.");
	            assertTrue(false);
	        }
	        this.logMessage("...done.");
	    }

	    public void testIsEmpty() {
	        this.logMessage("testIsEmpty()...");
	        try {
	            boolean empty = this.bop.isEmpty();
	            assertEquals(empty, this.bop.isEmpty());
	        } catch (Exception e) {
	            assertTrue(false);
	        }
	        this.logMessage("...done.");
	    }

	    public void testConsumeEnergy() throws Exception {
	        this.logMessage("testConsumeEnergy()...");
	        if (!this.bop.isFull()) {
	            this.bop.ConsumeEnergy(20.0);
	            assertTrue(!this.bop.isEmpty());
	        }
	        this.logMessage("...done.");
	    }

	    public void testProvideEnergy() throws Exception {
	        this.logMessage("testProvideEnergy()...");
	        if (!this.bop.isEmpty()) {
	            this.bop.provideEnergy(10.0);
	            assertTrue(!this.bop.isFull());
	        }
	        this.logMessage("...done.");
	    }

	    protected void runAllTests() throws Exception {
	    	this.testGetBatteryLevel();
	        this.testIsFull();
	        this.testIsEmpty();
	        this.testConsumeEnergy();
	        this.testGetBatteryLevel();
	        this.testProvideEnergy();
	        this.testGetBatteryLevel();
	    }

	    // -------------------------------------------------------------------------
	    // Cycle de vie du composant
	    // -------------------------------------------------------------------------

	    @Override
	    public synchronized void start() throws ComponentStartException {
	        super.start();
	        try {
	            this.doPortConnection(
	                    this.bop.getPortURI(),
	                    BatterieInboundPortURI,
	                    BatterieConnector.class.getCanonicalName());
	        } catch (Exception e) {
	            throw new ComponentStartException(e);
	        }
	    }

	    @Override
	    public synchronized void execute() throws Exception {
	        this.traceMessage("Batterie Tester starts the tests.\n");
	        this.runAllTests();
	        this.traceMessage("Batterie Tester ends.\n");
	    }

	    @Override
	    public synchronized void finalise() throws Exception {
	        this.doPortDisconnection(this.bop.getPortURI());
	        super.finalise();
	    }

	    @Override
	    public synchronized void shutdown() throws ComponentShutdownException {
	        try {
	            this.bop.unpublishPort();
	        } catch (Exception e) {
	            throw new ComponentShutdownException(e);
	        }
	        super.shutdown();
	    }
	


}
