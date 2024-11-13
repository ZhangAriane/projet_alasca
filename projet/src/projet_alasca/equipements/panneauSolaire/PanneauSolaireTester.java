package projet_alasca.equipements.panneauSolaire;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PreconditionException;

public class PanneauSolaireTester extends AbstractComponent {
	
	 protected static boolean VERBOSE = true;
	 protected final String inboundPortURI;
	  protected PanneauSolaireOutboundPort outboundPort;
	  
	  
	  protected PanneauSolaireTester(String inboundPortURI) {
	        super(1, 1);

	        assert inboundPortURI != null && !inboundPortURI.isEmpty() :
	                new PreconditionException("inboundPortURI must not be null or empty");

	        this.inboundPortURI = inboundPortURI;

	        if (VERBOSE) {
	            this.tracer.get().setTitle("Panneau Solaire MIL tester component");
	            this.tracer.get().setRelativePosition(3, 3);
	            this.toggleTracing();
	        }
	    }

	  
	    @Override
	    public synchronized void start() throws ComponentStartException {
	        super.start();

	        try {
	            // Set up the outbound port to connect to the PanneauSolaire component
	            this.outboundPort = new PanneauSolaireOutboundPort(this);
	            this.outboundPort.publishPort();
	            this.doPortConnection(
	                    this.outboundPort.getPortURI(),
	                    inboundPortURI,
	                    PanneauSolaireConnector.class.getCanonicalName());
	        } catch (Exception e) {
	            throw new ComponentStartException(e);
	        }
	        this.traceMessage("PanneauSolaireTester started.\n");
	    }

	    @Override
	    public void execute() throws Exception {
	        // Simulate a test scenario without accelerated time control
	        final PanneauSolaireOutboundPort sp = this.outboundPort;

	        this.traceMessage("Starting MIL test scenario...\n");

	        // Schedule turning on the solar panel
	        this.scheduleTask(
	            o -> {
	                try {
	                    sp.startProduce();
	                    traceMessage("startProduce() called.\n");
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            },
	            5,  // delay in seconds
	            TimeUnit.SECONDS);

	        // Schedule turning off the solar panel
	        this.scheduleTask(
	            o -> {
	                try {
	                    sp.stopProduce();
	                    traceMessage("stopProduce() called.\n");
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            },
	            10, // delay in seconds
	            TimeUnit.SECONDS);
	    }

	    @Override
	    public synchronized void finalise() throws Exception {
	        this.doPortDisconnection(this.outboundPort.getPortURI());
	        super.finalise();
	    }

	    @Override
	    public synchronized void shutdown() throws ComponentShutdownException {
	        this.traceMessage("PanneauSolaireTester shutdown.\n");
	        try {
	            this.outboundPort.unpublishPort();
	        } catch (Exception e) {
	            throw new ComponentShutdownException(e);
	        }
	        super.shutdown();
	    }
}
