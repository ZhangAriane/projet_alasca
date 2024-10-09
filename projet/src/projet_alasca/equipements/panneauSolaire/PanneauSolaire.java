package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

public class PanneauSolaire extends AbstractComponent implements PanneauSolaireCI {



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


}
