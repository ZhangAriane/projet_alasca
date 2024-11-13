package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

public class GeneratriceGaz extends AbstractComponent implements GeneratriceGazCI {



	protected GeneratriceGazInboundPort generatriceGazInboundPort;
	public static final String generatriceGazInboundPortURI = "generatriceGazInboundPortURI";
	public static int X_RELATIVE_POSITION = 2;
	public static int Y_RELATIVE_POSITION = 1;
	public static boolean VERBOSE = false;
	
	protected State state = State.OFF;
	protected Mode mode = Mode.LOW;


	protected GeneratriceGaz() {
		super(1, 0);
		try {
			this.generatriceGazInboundPort = new GeneratriceGazInboundPort(generatriceGazInboundPortURI, this);
			this.generatriceGazInboundPort.publishPort();

			if (GeneratriceGaz.VERBOSE) {
				this.tracer.get().setTitle("GeneratriceGaz component");
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
			this.generatriceGazInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	@Override
	public State getState() throws Exception {
		return this.state;
	}
	@Override
	public Mode getMode() throws Exception {
		return this.mode;
	}
	@Override
	public void turnOn() throws Exception {
		this.state = State.ON;
		
	}
	@Override
	public void turnOff() throws Exception {
		this.state = State.OFF;
		
	}
	@Override
	public void setHigh() throws Exception {
		this.mode = Mode.HIGH;
		
	}
	@Override
	public void setLow() throws Exception {
		this.mode = Mode.LOW;
		
	}
	@Override
	public void setMeddium() throws Exception {
		this.mode = Mode.MEDDIUM;
		
	}


}
