package projet_alasca.equipements.chauffeEau.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOnChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOffChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau.PowerValue;
import projet_alasca.equipements.chauffeEau.mil.events.Heat;
import projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterUnitTesterModel</code> defines a model that is used
 * to test the models defining the heater simulator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 *   {@code SwitchOnHeater},
 *   {@code SwitchOffHeater},
 *   {@code SetPowerHeater},
 *   {@code Heat},
 *   {@code DoNotHeat}</li>
 * </ul>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code step >= 0}
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(exported = {SwitchOnChauffeEau.class,
								 SwitchOffChauffeEau.class,
								 Heat.class,
								 DoNotHeat.class,
								 SetPowerChauffeEau.class})
// -----------------------------------------------------------------------------
public class			ChauffeEauUnitTesterModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = ChauffeEauUnitTesterModel.class.
															getSimpleName();

	/** current step in the test scenario.									*/
	protected int	step;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>HeaterUnitTesterModel</code> instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ChauffeEauUnitTesterModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.step = 1;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// Simple way to implement a test scenario. Here each step generates
		// an event sent to the other models in the standard order.
		if (this.step > 0 && this.step < 10) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			switch (this.step) {
			case 1:
				ret.add(new SwitchOnChauffeEau(this.getTimeOfNextEvent()));
				break;
			case 2:
				ret.add(new Heat(this.getTimeOfNextEvent()));
				break;
			case 3:
				ret.add(new DoNotHeat(this.getTimeOfNextEvent()));
				break;
			case 4:
				ret.add(new Heat(this.getTimeOfNextEvent()));
				break;
			case 5:
				ret.add(new SetPowerChauffeEau(this.getTimeOfNextEvent(),
										   new PowerValue(1600.0)));
				break;
			case 6:
				ret.add(new SetPowerChauffeEau(this.getTimeOfNextEvent(),
										   new PowerValue(1200.0)));
				break;
			case 7:
				ret.add(new SetPowerChauffeEau(this.getTimeOfNextEvent(),
										   new PowerValue(800.0)));
				break;
			case 8:
				ret.add(new SetPowerChauffeEau(this.getTimeOfNextEvent(),
										   new PowerValue(400.0)));
				break;
			case 9:
				ret.add(new SwitchOffChauffeEau(this.getTimeOfNextEvent()));
				break;
			}
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// As long as events have to be created and sent, the next internal
		// transition is set at one second later, otherwise, no more internal
		// transitions are triggered (delay = infinity).
		if (this.step < 10) {
			return new Duration(1.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// advance to the next step in the scenario
		this.step++;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------