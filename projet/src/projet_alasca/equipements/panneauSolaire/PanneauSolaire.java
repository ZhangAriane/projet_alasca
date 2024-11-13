package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.PostconditionException;


@OfferedInterfaces(offered= {PanneauSolaireCI.class})
public class PanneauSolaire
extends AbstractComponent 
implements PanneauSolaireI {



	
	protected PanneauSolaireInboundPort panneauSolaireInboundPort;
	public static final String panneauSolaireInboundPortURI = "panneauSolaireInboundPortURI";
	public static int X_RELATIVE_POSITION = 2;
	public static int Y_RELATIVE_POSITION = 1;
	public static boolean VERBOSE = false;


	protected PanneauSolaire() {
		super(1, 0);
		try {
			this.panneauSolaireInboundPort = new PanneauSolaireInboundPort(panneauSolaireInboundPortURI, this);
			this.panneauSolaireInboundPort.publishPort();

			if (PanneauSolaire.VERBOSE) {
				this.tracer.get().setTitle("PanneauSolaire component");
				this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
						Y_RELATIVE_POSITION);
				this.toggleTracing();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.panneauSolaireInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	@Override
	public boolean isOn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void startProduce() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stopProduce() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double getEnergyProduction() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setEnergyProduction(double energy) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	/*public static enum State {
		OFF,
		ON,
	}
	
	public static final String	REFLECTION_INBOUND_PORT_URI = "SP-RIP-URI";
	public static final String	INBOUND_PORT_URI_PREFIX = "panneau-solaire-ibp";
	
	public static final boolean		VERBOSE = true;
	
	protected PanneauSolaireInboundPort	psip;
	
	protected State		currentState;
	
	protected double	energy = 0;
	
	protected PanneauSolaire ()  throws Exception {
		super(REFLECTION_INBOUND_PORT_URI, 1, 1);

        // Initialisation de l'état du panneau
        this.currentState = State.OFF;
        
        // Création et publication du port entrant
        this.psip = new  PanneauSolaireInboundPort(PanneauSolaire.INBOUND_PORT_URI_PREFIX, this);
        this.psip.publishPort();

        if (VERBOSE) {
            this.tracer.get().setTitle("Panneau Solaire component");
            this.tracer.get().setRelativePosition(2, 3);
            this.toggleTracing();        
        }

	}
	
	// Méthodes du cycle de vie du composant
    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();
        if (VERBOSE) {
            this.traceMessage("Panneau Solaire component started.\n");
        }
    }
    
    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.sip.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }
    
 // Méthodes pour gérer la production d'énergie
    @Override
    public void startProduce() throws Exception {
        assert !isOn() : new PreconditionException("!isOn()");

        if (VERBOSE) {
            this.traceMessage("Panneau solaire starts producing.\n");
        }

        this.currentState = State.ON;

        assert isOn() : new PostconditionException("isOn()");
    }
    
    @Override
    public void stopProduce() throws Exception {
        assert isOn() : new PreconditionException("isOn()");

        if (VERBOSE) {
            this.traceMessage("Panneau Solaire stops producing.\n");
        }

        this.currentState = State.OFF;

        assert !isOn() : new PostconditionException("!isOn()");
    }

    @Override
    public double getEnergyProduction() throws Exception {
        return this.energy;
    }
    
    @Override
    public void setEnergyProduction(double energy) throws Exception {
        this.energy = energy;
    }

    @Override
    public boolean isOn() throws Exception {
        return this.currentState != State.OFF;
    }
	*/

}
