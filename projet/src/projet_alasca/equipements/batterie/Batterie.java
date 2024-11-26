package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.machineCafe.MachineCafe;
import projet_alasca.equipements.machineCafe.MachineCafeInboundPort;
import projet_alasca.equipements.ventilateur.Ventilateur;
import projet_alasca.equipements.ventilateur.VentilateurInboundPort;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurMode;
import projet_alasca.equipements.ventilateur.VentilateurImplementationI.VentilateurState;

public class Batterie 
extends AbstractComponent 
implements BatterieI {


	// Constants et variables : 

	public static final String			INBOUND_PORT_URI =
			"BATTERIE-INBOUND-PORT-URI";							
	public static boolean	VERBOSE = false;

	public static int	X_RELATIVE_POSITION = 0;
	public static int	Y_RELATIVE_POSITION = 0;

	public static final BatterieState	INITIAL_STATE  = BatterieState.PRODUCT;


	protected BatterieState	currentState;


	protected BatterieInboundPort	batterieInboundPort;


	//---------------------------------------------------------
	protected Batterie()  throws Exception{
		super(1,0);
		this.initialise(INBOUND_PORT_URI);

	}


	protected	Batterie(
			String BatterieInboundPortURI)
					throws Exception
	{
		super(1, 0);
		this.initialise(BatterieInboundPortURI);
	}

	protected			Batterie(
			String BatterieInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(BatterieInboundPortURI);
	}




	protected void	initialise(String BatterieInboundPortURI)
			throws Exception
	{
		assert	BatterieInboundPortURI != null :
			new PreconditionException(
					"BatterieInboundPortURI != null");
		assert	!BatterieInboundPortURI.isEmpty() :
			new PreconditionException(
					"!BatterieInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.batterieInboundPort = new BatterieInboundPort(BatterieInboundPortURI, this);
		this.batterieInboundPort.publishPort();

		if (Batterie.VERBOSE) {
			this.tracer.get().setTitle("Batterie component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
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
	}
	

	@Override
	public BatterieState getState() throws Exception{
		if(Batterie.VERBOSE)
			this.logMessage("Batterie returns its state : " + currentState);
		return currentState;
	}

	@Override
	public void swicthConsume() throws Exception {
		if (Batterie.VERBOSE) {
			this.traceMessage("Batterie is turned consume.\n");
		}

		assert	this.getState() == BatterieState.PRODUCT :
				new PreconditionException("getState() == BatterieState.PRODUCT");

		this.currentState = BatterieState.CONSUME;
	}

	@Override
	public void switchProduct() throws Exception {
		if (Batterie.VERBOSE) {
			this.traceMessage("Batterie is turned product.\n");
		}

		assert	this.getState() == BatterieState.CONSUME :
				new PreconditionException("getState() == BatterieState.CONSUME");

		this.currentState = BatterieState.CONSUME;
	}

}
