package projet_alasca.equipements.ventilateur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class VentilateurOutboundPort 
extends AbstractOutboundPort
implements VentilateurUserCI
{
	
	private static final long serialVersionUID = 1L;

	
	// Constructeurs 
	
	
	public	VentilateurOutboundPort(ComponentI owner)
			throws Exception
			{
				super(VentilateurUserCI.class, owner);
			}
	public VentilateurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, VentilateurUserCI.class, owner);
	}

	
	
	
	@Override
	public VentilateurState getState() throws Exception {
		return ((VentilateurUserCI)this.getConnector()).getState();
	}

	@Override
	public VentilateurMode getMode() throws Exception {
		return ((VentilateurUserCI)this.getConnector()).getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((VentilateurUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((VentilateurUserCI)this.getConnector()).turnOff();
		
	}

	@Override
	public void setHigh() throws Exception {
		((VentilateurUserCI)this.getConnector()).setHigh();
		
	}

	@Override
	public void setMedium() throws Exception {
		((VentilateurUserCI)this.getConnector()).setMedium();
	}

	@Override
	public void setLow() throws Exception {
		((VentilateurUserCI)this.getConnector()).setLow();
		
	}

}
