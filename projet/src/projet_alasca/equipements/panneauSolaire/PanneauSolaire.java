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
	
	
	// Constants et variables : 
	
		public static final String			INBOUND_PORT_URI =
				"PANNEAU-SOLAIRE-INBOUND-PORT-URI";							
	public static boolean	VERBOSE = false;

	public static int	X_RELATIVE_POSITION = 0;
	public static int	Y_RELATIVE_POSITION = 0;

								
	public static final PanneauSolaireState	INITIAL_STATE = PanneauSolaireState.OFF;
								
	protected PanneauSolaireState	currentState;	
	
	protected PanneauSolaireInboundPort	psip;
		
	
	protected PanneauSolaire()  throws Exception{
		super(1,0);
		this.initialise(INBOUND_PORT_URI);
		
	}
	
	private double energy = 0;

	
	protected	PanneauSolaire(
			String panneauSolaireInboundPortURI)
			throws Exception
			{
				super(1, 0);
				this.initialise(panneauSolaireInboundPortURI);
			}
	
	protected			PanneauSolaire(
			String panneauSolaireInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);
			this.initialise(panneauSolaireInboundPortURI);
		}

	
	
	
	protected void	initialise(String panneauSolaireInboundPortURI)
			throws Exception
			{
				assert	panneauSolaireInboundPortURI != null :
							new PreconditionException(
												"panneauSolaireInboundPortURI != null");
				assert	!panneauSolaireInboundPortURI.isEmpty() :
							new PreconditionException(
												"!panneauSolaireInboundPortURI.isEmpty()");

				this.currentState = INITIAL_STATE;
				this.psip = new PanneauSolaireInboundPort(panneauSolaireInboundPortURI, this);
				this.psip.publishPort();

				if (PanneauSolaire.VERBOSE) {
					this.tracer.get().setTitle("Panneau solaire component");
					this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
														  Y_RELATIVE_POSITION);
					this.toggleTracing();
				}
			}
	
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.psip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
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

        this.currentState = PanneauSolaireState.ON;

        assert isOn() : new PostconditionException("isOn()");
    }
    
    @Override
    public void stopProduce() throws Exception {
        assert isOn() : new PreconditionException("isOn()");

        if (VERBOSE) {
            this.traceMessage("Panneau Solaire stops producing.\n");
        }

        this.currentState = PanneauSolaireState.OFF;

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
    	 if (VERBOSE) {
             this.traceMessage("Panneau Solaire is " + this.currentState + ".\n");
         }
        return this.currentState != PanneauSolaireState.OFF;
    }
	

}
