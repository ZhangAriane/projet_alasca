package projet_alasca.equipements.batterie;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import projet_alasca.equipements.machineCafe.MachineCafe;
import projet_alasca.equipements.machineCafe.MachineCafeInboundPort;

public class Batterie 
extends AbstractComponent 
implements BatterieI {
	
	protected State state = State.PRODUCT;
	protected BatterieInboundPort batterieInboundPort;
	public static final String batterieInboundPortURI = "batterieInboundPortURI";
	public static int X_RELATIVE_POSITION = 2;
	public static int Y_RELATIVE_POSITION = 1;
	public static boolean VERBOSE = false;
	

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
	}
	

}
