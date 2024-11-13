package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import projet_alasca.equipements.machineCafe.MachineCafeUserCI;

public class PanneauSolaireOutboundPort 
extends		AbstractOutboundPort 
implements PanneauSolaireCI{

	private static final long serialVersionUID = 1L;

	public PanneauSolaireOutboundPort(ComponentI owner)
			throws Exception {
		super(PanneauSolaireCI.class, owner);
	}

	public				PanneauSolaireOutboundPort(String uri, ComponentI owner)
			throws Exception
	{
		super(uri, PanneauSolaireCI.class, owner);
	}

	
	@Override
	public void startProduce() throws Exception {
		((PanneauSolaireCI)this.getConnector()).startProduce();	
	}

	@Override
	public void stopProduce() throws Exception {
		((PanneauSolaireCI)this.getConnector()).stopProduce();	
	}

	@Override
	public double getEnergyProduction() throws Exception {
		return ((PanneauSolaireCI)this.getConnector()).getEnergyProduction();	
	}

	@Override
	public void setEnergyProduction(double energy) throws Exception {
		((PanneauSolaireCI)this.getConnector()).setEnergyProduction(energy);	
	}

	@Override
	public boolean isOn() throws Exception {
		return ((PanneauSolaireCI)this.getConnector()).isOn();
	}

	

}
