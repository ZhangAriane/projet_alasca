package projet_alasca.equipements.panneauSolaire.mil;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

public class PanneauSolaireElectricityModel
extends AtomicHIOA
{
	
	 public static final String URI = PanneauSolaireElectricityModel.class.getSimpleName();
	 
	 // Variables
	    private double production; // Production actuelle d'énergie (en kW)
	    private static final double MAX_PRODUCTION = 1.5; // Production maximale pendant la journée
	    private static final double NIGHT_PRODUCTION = 0.0; // Production pendant la nuit

	    // Durée entre chaque mise à jour de la production
	    private final Duration updateInterval = new Duration(1.0, TimeUnit.HOURS);

	public PanneauSolaireElectricityModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
        this.production = 0.0;
        
	}
	
	@Override
    public Duration timeAdvance() {
        // Le modèle émet un événement toutes les heures pour mettre à jour la production
        return updateInterval;
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);

        // Obtenir l'heure actuelle simulée pour déterminer la production
        Time currentTime = this.getCurrentStateTime();
        double hourOfDay = currentTime.getSimulatedTime() % 24; // Heure de la journée (de 0 à 23)

        // Définir la production en fonction de l'heure
        if (hourOfDay >= 6 && hourOfDay <= 18) {
            production = MAX_PRODUCTION; // Production maximale durant la journée
        } else {
            production = NIGHT_PRODUCTION; // Pas de production pendant la nuit
        }

        
        this.logMessage("PanneauSolaire : Production mise à jour à " + production + " kW à l'heure " + hourOfDay);

        // Planifier la prochaine mise à jour de la production
        this.externalTransition(updateInterval);
    }

    // Méthode pour obtenir la production actuelle
    public double getCurrentProduction() {
        return production;
    }
    
    @Override
    public void endSimulation(Time endTime) {
        super.endSimulation(endTime);
        this.logMessage("Fin de la simulation du Panneau Solaire avec production finale de : " + production + " kW");
    }
	


}
