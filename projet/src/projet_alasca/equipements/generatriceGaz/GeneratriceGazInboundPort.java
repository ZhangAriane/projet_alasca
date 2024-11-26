package projet_alasca.equipements.generatriceGaz;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import projet_alasca.equipements.chauffeEau.ChauffeEauExternalControlI;

public class GeneratriceGazInboundPort extends		AbstractInboundPort implements GeneratriceGazCI{

	private static final long serialVersionUID = 1L;

	public GeneratriceGazInboundPort( ComponentI owner)
			throws Exception {
		super(GeneratriceGazCI.class, owner);
		
	}

	public				GeneratriceGazInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, GeneratriceGazCI.class, owner);
		
	}

	@Override
	public GeneratriceGazState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((GeneratriceGazI)o).
				getState());
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GeneratriceGazI)o).
					turnOn();
						return null;
				});
		
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GeneratriceGazI)o).
					turnOff();
						return null;
				});
		
	}







}
