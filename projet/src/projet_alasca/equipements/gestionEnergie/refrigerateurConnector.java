package projet_alasca.equipements.gestionEnergie;

import fr.sorbonne_u.components.hem2024.bases.AdjustableCI;

public class refrigerateurConnector implements AdjustableCI {

	@Override
	public int maxMode() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean upMode() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean downMode() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int currentMode() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean suspended() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean suspend() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resume() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double emergency() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
