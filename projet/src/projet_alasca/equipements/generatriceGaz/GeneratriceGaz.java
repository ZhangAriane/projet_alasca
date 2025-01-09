package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.batterie.Batterie;
import projet_alasca.equipements.batterie.BatterieInboundPort;

public class GeneratriceGaz extends AbstractComponent implements GeneratriceGazCI {


	// Constants et variables : 

	public static final String			INBOUND_PORT_URI =
			"GENERATRICE-GAZ-INBOUND-PORT-URI";							
	public static boolean	VERBOSE = false;

	public static int	X_RELATIVE_POSITION = 0;
	public static int	Y_RELATIVE_POSITION = 0;

	public static final GeneratriceGazState	INITIAL_STATE  = GeneratriceGazState.OFF;

	protected GeneratriceGazState	currentState;


	protected GeneratriceGazInboundPort	generatriceGazInboundPort;




	//---------------------------------------------------------
	protected GeneratriceGaz()  throws Exception{
		super(1,0);
		this.initialise(INBOUND_PORT_URI);

	}


	protected	GeneratriceGaz(
			String GeneratriceGazInboundPortURI)
					throws Exception
	{
		super(1, 0);
		this.initialise(GeneratriceGazInboundPortURI);
	}

	protected			GeneratriceGaz(
			String GeneratriceGazInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(GeneratriceGazInboundPortURI);
	}




	protected void	initialise(String generatriceGazInboundPortURI)
			throws Exception
	{
		assert	generatriceGazInboundPortURI != null :
			new PreconditionException(
					"generatriceGazInboundPortURI != null");
		assert	!generatriceGazInboundPortURI.isEmpty() :
			new PreconditionException(
					"!generatriceGazInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.generatriceGazInboundPort = new GeneratriceGazInboundPort(generatriceGazInboundPortURI, this);
		this.generatriceGazInboundPort.publishPort();

		if (GeneratriceGaz.VERBOSE) {
			this.tracer.get().setTitle("Generatrice gaz component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}



	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.generatriceGazInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	@Override
	public GeneratriceGazState getState() throws Exception {
		if(GeneratriceGaz.VERBOSE)
			this.logMessage("Generatrice Gaz returns its state : " + currentState);
		return currentState;
	}

	@Override
	public void turnOn() throws Exception {
		if (GeneratriceGaz.VERBOSE) {
			this.traceMessage("Generatrice Gaz is turned on.\n");
		}

		assert	this.getState() == GeneratriceGazState.OFF:
			new PreconditionException("getState() == GeneratriceGazState.OFF");

		this.currentState = GeneratriceGazState.ON;

	}
	@Override
	public void turnOff() throws Exception {
		if (GeneratriceGaz.VERBOSE) {
			this.traceMessage("Generatrice Gaz is turned off.\n");
		}

		assert	this.getState() == GeneratriceGazState.ON:
			new PreconditionException("getState() == GeneratriceGazState.ON");

		this.currentState = GeneratriceGazState.OFF;

	}



}
