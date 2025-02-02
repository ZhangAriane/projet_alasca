package projet_alasca.equipements.panneauSolaire;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class PanneauSolaireInboundPort
extends		AbstractInboundPort 
implements PanneauSolaireCI
{

	private static final long serialVersionUID = 1L;

	public PanneauSolaireInboundPort( ComponentI owner)
			throws Exception {
		super(PanneauSolaireCI.class, owner);
		
	}

	public				PanneauSolaireInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, PanneauSolaireCI.class, owner);
		
	}
	
	@Override
	public void startProduce() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((PanneauSolaireI)o).startProduce();
				return null;
			 });		
	}

	@Override
	public void stopProduce() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((PanneauSolaireI)o).stopProduce();
				return null;
			 });			
	}

	@Override
	public double getEnergyProduction() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PanneauSolaireI)o).getEnergyProduction());
	}

	@Override
	public void setEnergyProduction(double energy) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((PanneauSolaireI)o).setEnergyProduction(energy);
				return null;
			 });			
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PanneauSolaireI)o).isOn());
	}






}
