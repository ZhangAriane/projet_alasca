package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

public class GeneratriceGaz extends AbstractComponent implements GeneratriceGazCI {



	protected GeneratriceGazInboundPort generatriceGazInboundPort;
	public static final String generatriceGazInboundPortURI = "generatriceGazInboundPortURI";
	public static int X_RELATIVE_POSITION = 2;
	public static int Y_RELATIVE_POSITION = 1;
	public static boolean VERBOSE = false;


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


}
