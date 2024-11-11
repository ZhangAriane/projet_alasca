package projet_alasca.equipements.ventilateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered = {VentilateurUserCI.class})
public class Ventilateur
extends	   AbstractComponent  
implements VentilateurImplementationI
{

	// Constants et variables : 
	
	public static final String			INBOUND_PORT_URI =
			"VENTILATEUR-INBOUND-PORT-URI";							
public static boolean	VERBOSE = false;

public static int	X_RELATIVE_POSITION = 0;
public static int	Y_RELATIVE_POSITION = 0;

							
public static final VentilateurState	INITIAL_STATE = VentilateurState.OFF;
									
public static final VentilateurMode	INITIAL_MODE = VentilateurMode.LOW;


protected VentilateurState	currentState;	
protected VentilateurMode	currentMode;

protected VentilateurInboundPort	vip;
	
	
	
	
	
	
	//---------------------------------------------------------
	protected Ventilateur()  throws Exception{
		super(1,0);
		this.initialise(INBOUND_PORT_URI);
		
	}

	
	protected	Ventilateur(
			String VentilateurInboundPortURI)
			throws Exception
			{
				super(1, 0);
				this.initialise(VentilateurInboundPortURI);
			}
	
	protected			Ventilateur(
			String VentilateurInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);
			this.initialise(VentilateurInboundPortURI);
		}

	
	
	
	protected void	initialise(String VentilateurInboundPortURI)
			throws Exception
			{
				assert	VentilateurInboundPortURI != null :
							new PreconditionException(
												"VentilateurInboundPortURI != null");
				assert	!VentilateurInboundPortURI.isEmpty() :
							new PreconditionException(
												"!VentilateurInboundPortURI.isEmpty()");

				this.currentState = INITIAL_STATE;
				this.currentMode = INITIAL_MODE;
				this.vip = new VentilateurInboundPort(VentilateurInboundPortURI, this);
				this.vip.publishPort();

				if (Ventilateur.VERBOSE) {
					this.tracer.get().setTitle("Ventilateur component");
					this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
														  Y_RELATIVE_POSITION);
					this.toggleTracing();
				}
			}
	
	
	
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.vip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


	@Override
	public VentilateurState getState() throws Exception {
		
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}


	@Override
	public VentilateurMode getMode() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}


	@Override
	public void turnOn() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is turned on.\n");
		}

		assert	this.getState() == VentilateurState.OFF :
				new PreconditionException("getState() == VentilateurState.OFF");

		this.currentState = VentilateurState.ON;
		this.currentMode = VentilateurMode.LOW;
	}


	@Override
	public void turnOff() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is turned off.\n");
		}

		assert	this.getState() == VentilateurState.ON :
				new PreconditionException("getState() == VentilateurState.ON");

		this.currentState = VentilateurState.OFF;
		
	}


	@Override
	public void setHigh() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is set high.\n");
		}

		assert	this.getState() == VentilateurState.ON :
				new PreconditionException("getState() == VentilateurState.ON");
		assert	this.getMode() == VentilateurMode.MEDIUM || this.getMode() == VentilateurMode.LOW :
				new PreconditionException("getMode() == VentilateurMode.MEDIUM or getMode() == VentilateurMode.LOW ");

		this.currentMode = VentilateurMode.HIGH;
		
	}


	@Override
	public void setMedium() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is set medium.\n");
		}
		assert	this.getState() == VentilateurState.ON :
			new PreconditionException("getState() == VentilateurState.ON");
		assert	this.getMode() == VentilateurMode.HIGH  || this.getMode() == VentilateurMode.LOW:
			new PreconditionException("getMode() == VentilateurMode.HIGH or getMode() == VentilateurMode.LOW");
		
		this.currentMode = VentilateurMode.MEDIUM;
		
	}


	@Override
	public void setLow() throws Exception {
		if (Ventilateur.VERBOSE) {
			this.traceMessage("Ventilateur is set low.\n");
		}

		assert	this.getState() == VentilateurState.ON :
				new PreconditionException("getState() == VentilateurState.ON");
		assert	this.getMode() == VentilateurMode.HIGH  || this.getMode() == VentilateurMode.MEDIUM:
				new PreconditionException("getMode() == VentilateurMode.HIGH or getMode() == VentilateurMode.MEDIUM");

		this.currentMode =VentilateurMode.LOW;
		
	}
	
	
	
	
	

	

}
