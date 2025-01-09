package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;



@OfferedInterfaces(offered = {BatterieCI.class})
public class Batterie 
extends AbstractComponent 
implements BatterieI {
	
	/*protected State state = State.PRODUCT;*/
	protected BatterieInboundPort batterieInboundPort;
	public static final String batterieInboundPortURI = "batterieInboundPortURI";
	public static int X_RELATIVE_POSITION = 2;
	public static int Y_RELATIVE_POSITION = 1;
	//public static boolean VERBOSE = false;
	/*

	protected Batterie() {
		super(1, 0);
		try {
			this.batterieInboundPort = new BatterieInboundPort(batterieInboundPortURI, this);
			this.batterieInboundPort.publishPort();
			
			if (Batterie.VERBOSE) {
				this.tracer.get().setTitle("Batterie component");
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
	public State getState() throws Exception{
		if(Batterie.VERBOSE)
			this.logMessage("Batterie affiche son état : " + state);
		return state;
	}
	
	
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.batterieInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}*/
	
	

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the Batterie inbound port used in tests.					*/
	public static final String		INBOUND_PORT_URI =
												"Batterie-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static  boolean		VERBOSE = false;

	/** inbound port offering the <code>BatterieCI</code> interface.			*/
	protected BatterieInboundPort	bip;
	/** max stored energy.	*/
	protected double			maxEnergy = 100;
	/** current stored energy.	*/
	protected double		currentEnergy = 50;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------


	protected Batterie() throws Exception {
		super (1,0);
		this.initialise(INBOUND_PORT_URI);
	}

	protected Batterie(
			String BatterieInboundPortURI) throws Exception
	{
		super(1, 0);
		this.initialise(BatterieInboundPortURI);
	}
	
	protected Batterie(
			String reflectionInboundPortURI, 
			String BatterierInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(BatterierInboundPortURI);
	}
	
	/**
	 * create a new Batterie.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code BatterierInboundPortURI != null}
	 * pre	{@code !BatterierInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param BatterierInboundPortURI	URI of the inbound port to call the Batterie component.
	 * @throws Exception			<i>to do </i>.
	 */
	protected void		initialise(String BatterierInboundPortURI) throws Exception
	{
		assert	BatterierInboundPortURI != null;
		assert	!BatterierInboundPortURI.isEmpty();

		this.bip = new BatterieInboundPort(BatterierInboundPortURI, this);
		this.bip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Batterie component");
			this.tracer.get().setRelativePosition(1, 1);
			this.toggleTracing();		
		}
	}
	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	public double getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(double maxEnergy) {
		this.maxEnergy = maxEnergy;
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.bip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public boolean isFull() throws Exception {
		return this.currentEnergy == this.maxEnergy;
	}

	@Override
	public boolean isEmpty() throws Exception {
		return this.currentEnergy == 0;
	}

	/*@Override
	public void ConsumeEnergy(double energy) throws Exception {
		assert !this.isFull();
		double sum = this.currentEnergy + energy;
		if(sum >= this.maxEnergy) {
			this.currentEnergy = this.maxEnergy;
			assert this.isFull();
		} else {
			this.currentEnergy = sum;
		}
	}*/
	@Override
    public void ConsumeEnergy(double energy) throws Exception {
        if (VERBOSE) {
            this.traceMessage("Trying to consume " + energy + " energy units.");
        }

        // Vérifie si la batterie est pleine
        if (this.isEmpty()) {
            throw new Exception("Error: Battery is empty. Cannot consume energy.");
        }

        double newEnergyLevel = this.currentEnergy - energy;
        if (newEnergyLevel < 0) {
            this.currentEnergy = 0;
            if (VERBOSE) {
                this.traceMessage("Battery emptied. Current energy is now 0.");
            }
        } else {
            this.currentEnergy = newEnergyLevel;
            if (VERBOSE) {
                this.traceMessage("Battery consumed " + energy + " energy units. Current energy: " + this.currentEnergy);
            }
        }
    }


	/*@Override
	public void provideEnergy(double energy) throws Exception {
		assert !this.isEmpty();
		double diff = this.currentEnergy - energy;
		if(diff > 0) {
			this.currentEnergy = diff;
		} else {
			this.currentEnergy = 0;
			assert this.isEmpty();
		}
	}*/
	
	@Override
    public void provideEnergy(double energy) throws Exception {
        if (VERBOSE) {
            this.traceMessage("Trying to provide " + energy + " energy units.");
        }

        // Vérifie si la batterie est pleine
        if (this.isFull()) {
            throw new Exception("Error: Battery is full. Cannot provide more energy.");
        }

        double newEnergyLevel = this.currentEnergy + energy;
        if (newEnergyLevel > this.maxEnergy) {
            this.currentEnergy = this.maxEnergy;
            if (VERBOSE) {
                this.traceMessage("Battery fully charged. Current energy is now " + this.maxEnergy);
            }
        } else {
            this.currentEnergy = newEnergyLevel;
            if (VERBOSE) {
                this.traceMessage("Battery provided " + energy + " energy units. Current energy: " + this.currentEnergy);
            }
        }
    }
	
	@Override
	public double getEnergyLevel() throws Exception {
	    return this.currentEnergy;
	}

	

}
