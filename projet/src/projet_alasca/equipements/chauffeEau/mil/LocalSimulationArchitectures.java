package projet_alasca.equipements.chauffeEau.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.exceptions.PreconditionException;
import projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat;
import projet_alasca.equipements.chauffeEau.mil.events.Heat;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOffChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOnChauffeEau;
import projet_alasca.etape3.utils.SimulationType;

// -----------------------------------------------------------------------------
/**
 * The class <code>LocalSimulationArchitectures</code> defines the local
 * MIL simulation architectures pertaining to the ChauffeEau appliance.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class provides static methods that create the local MIL, MIL real time
 * and SIL simulation architectures for the {@code ChauffeEau} and the
 * {@code ChauffeEauUser} components. The overall simulation architecture
 * for the ChauffeEau appliance can be seen as follows:
 * </p>
 * <p><img src="../../../../../../../../images/hem-2024-e3/ChauffeEauMILModel.png"/></p> 
 * <p>
 * But, to fit this architecture onto separate components, it has to be split
 * into component local simulation architectures and then composed into a global
 * simulation architecture by the simulation supervisor component.
 * </p>
 * <p>
 * The simulation architectures created in this class are local to components
 * in the sense that they define the simulators that are created and run by
 * each component. These are integrated in more global component simulation
 * architectures where they are seen as atomic models that are composed into
 * coupled models that will reside in coordinator components.
 * </p>
 * <p>
 * As there is nothing to change to the simulation architectures of the
 * {@code ChauffeEau} component hair to go from MIL real time to SIL simulations,
 * the MIL real time architectures can be used to execute SIL simulations. For
 * the {@code ChauffeEauUser}, there is no need for a simulator in SIL
 * simulations as its entire behaviour lies in its component code.
 * </p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	LocalSimulationArchitectures
{
	/**
	 * create the local MIL simulation architecture for the {@code ChauffeEau}
	 * component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><img src="../../../../../../../../images/hem-2024-e3/ChauffeEauUnitTestLocalArchitecture.png"/></p> 
	 * <p>
	 * In this simulation architecture, the ChauffeEau simulator consists of five
	 * atomic models:
	 * </p>
	 * <ol>
	 * <li>The {@code ChauffeEauStateModel} keeps track of the state (switched on,
	 *   switched off, etc.) of the ChauffeEau and its current power level. The
	 *   state changes are triggered by the reception of external events through
	 *   {@code ChauffeEauCoupledModel} that imports and transmit them; whenever a
	 *   state change occurs, the triggering event is reemitted towards the
	 *   {@code ChauffeEauElectricityModel} and the {@code ChauffeEauTemperatureModel}
	 *   (except for {@code SwitchOnChauffeEau} that does not influence the
	 *   temperature model).</li>
	 * <li>The {@code ChauffeEauElectricityModel} keeps track of the electric power
	 *   consumed by the ChauffeEau in a variable <code>currentIntensity</code>,
	 *   which is exported but not used in this simulation of the ChauffeEau in
	 *   isolation.</li>
	 * <li>The {@code ExternalTemperatureModel} simulates the temperature
	 *   outside the room, a part of the environment. The simulated temperature
	 *   is put in an exported variable {@code externalTemperature} that is
	 *   imported with the same name by the {@code ChauffeEauTemperatureModel}.</li>
	 * <li>The {@code ChauffeEauTemperatureModel} simulates the temperature inside
	 *   the heated room, using the external temperature provided by the
	 *   {@code ExternalTemperatureModel} and the current power of the ChauffeEau,
	 *   which it keeps track of through the {@code SetPowerChauffeEau} and
	 *   {@code SwitchOffChauffeEau} events. The evolution of the inside temperature
	 *   also obviously depends upon the fact that the ChauffeEau actually is
	 *   heating or not, a state which is kept track of through the events
	 *   {@code Heat} and {@code DoNotHeat}.</li>
	 * <li>The {@code ChauffeEauUnitTesterModel} simulates a ChauffeEau user and a
	 *   ChauffeEau controller by emitting state changing events towards the
	 *   {@code ChauffeEauStateModel}.</li>
	 * </ol>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit	simulated time unit used in the architecture.
	 * @return					the local MIL simulation architecture for the {@code ChauffeEau} component.
	 * @throws Exception		<i>to do</i>.
	 */
	public static Architecture	createChauffeEauMILLocalArchitecture4UnitTest(
		String architectureURI,
		TimeUnit simulatedTimeUnit
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				ChauffeEauStateModel.MIL_URI,
				AtomicModelDescriptor.create(
						ChauffeEauStateModel.class,
						ChauffeEauStateModel.MIL_URI,
						simulatedTimeUnit,
						null));
		// the ChauffeEau models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				ExternalTemperatureModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ExternalTemperatureModel.class,
						ExternalTemperatureModel.MIL_URI,
						simulatedTimeUnit,
						null));
		atomicModelDescriptors.put(
				ChauffeEauTemperatureModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ChauffeEauTemperatureModel.class,
						ChauffeEauTemperatureModel.MIL_URI,
						simulatedTimeUnit,
						null));
		atomicModelDescriptors.put(
				ChauffeEauElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ChauffeEauElectricityModel.class,
						ChauffeEauElectricityModel.MIL_URI,
						simulatedTimeUnit,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ChauffeEauStateModel.MIL_URI);
		submodels.add(ExternalTemperatureModel.MIL_URI);
		submodels.add(ChauffeEauTemperatureModel.MIL_URI);
		submodels.add(ChauffeEauElectricityModel.MIL_URI);

		// events received by the coupled model transmitted to its submodels
		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<>();

		imported.put(
				SwitchOnChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI,
								  SwitchOnChauffeEau.class)
				});
		imported.put(
				SetPowerChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI,
								  SetPowerChauffeEau.class)
				});
		imported.put(
				SwitchOffChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI,
								  SwitchOffChauffeEau.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI, Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI, DoNotHeat.class)
				});

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI,
								SwitchOnChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauElectricityModel.MIL_URI,
									  SwitchOnChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI,
								SetPowerChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  SetPowerChauffeEau.class),
						new EventSink(ChauffeEauElectricityModel.MIL_URI,
									  SetPowerChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI,
								SwitchOffChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  SwitchOffChauffeEau.class),
						new EventSink(ChauffeEauElectricityModel.MIL_URI,
									  SwitchOffChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI, Heat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  Heat.class),
						new EventSink(ChauffeEauElectricityModel.MIL_URI,
									  Heat.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  DoNotHeat.class),
						new EventSink(ChauffeEauElectricityModel.MIL_URI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										ExternalTemperatureModel.MIL_URI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  ChauffeEauTemperatureModel.MIL_URI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				ChauffeEauCoupledModel.MIL_URI,
				new CoupledHIOA_Descriptor(
						ChauffeEauCoupledModel.class,
						ChauffeEauCoupledModel.MIL_URI,
						submodels,
						imported,
						null,
						connections,
						null,
						null,
						null,
						bindings));

		// simulation architecture
		Architecture architecture =
				new Architecture(
						architectureURI,
						ChauffeEauCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit);

		return architecture;
	}

	/**
	 * create the local MIL real time or SIL simulation architecture for the
	 * {@code ChauffeEau} component when doing real time unit tests with simulation.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The simulation architecture created for {@code ChauffeEau} real time
	 * unit tests is the same as for non real time unit tests except that the
	 * simulation engines has to be real time, hence using the corresponding
	 * model and architecture descriptors. See
	 * {@code createChauffeEauMIL_ArchitectureUnitTest} for more detailed
	 * explanations about the architecture itself.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentSimulationType.isMILRTSimulation() || currentSimulationType.isSILSimulation()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentSimulationType	simulation type for the next run.
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor used to execute in a logical time speeding up the real time.
	 * @return						the local MIL real time simulation architecture for the unit tests of the {@code ChauffeEau} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createChauffeEau_RT_LocalArchitecture4UnitTest(
		SimulationType currentSimulationType,
		String architectureURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	currentSimulationType.isMILRTSimulation() ||
									currentSimulationType.isSILSimulation() :
				new PreconditionException(
						"currentSimulationType.isMILRTSimulation() || "
						+ "currentSimulationType.isSILSimulation()");

		String ChauffeEauStateModelURI = null;
		String ChauffeEauTemperatureModelURI = null;
		String externalTemperatureModelURI = null;
		String ChauffeEauElectricityModelURI = null;
		String ChauffeEauCoupledModelURI = null;
		switch (currentSimulationType) {
		case MIL_RT_SIMULATION:
			ChauffeEauStateModelURI = ChauffeEauStateModel.MIL_RT_URI;
			ChauffeEauTemperatureModelURI = ChauffeEauTemperatureModel.MIL_RT_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.MIL_RT_URI;
			ChauffeEauElectricityModelURI = ChauffeEauElectricityModel.MIL_RT_URI;
			ChauffeEauCoupledModelURI = ChauffeEauCoupledModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			ChauffeEauStateModelURI = ChauffeEauStateModel.SIL_URI;
			ChauffeEauTemperatureModelURI = ChauffeEauTemperatureModel.SIL_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.SIL_URI;
			ChauffeEauElectricityModelURI = ChauffeEauElectricityModel.SIL_URI;
			ChauffeEauCoupledModelURI = ChauffeEauCoupledModel.SIL_URI;
			break;
		default:
		}

		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				ChauffeEauStateModelURI,
				RTAtomicModelDescriptor.create(
						ChauffeEauStateModel.class,
						ChauffeEauStateModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		// the ChauffeEau models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				externalTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						ExternalTemperatureModel.class,
						externalTemperatureModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				ChauffeEauTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						ChauffeEauTemperatureModel.class,
						ChauffeEauTemperatureModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				ChauffeEauElectricityModelURI,
				RTAtomicHIOA_Descriptor.create(
						ChauffeEauElectricityModel.class,
						ChauffeEauElectricityModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ChauffeEauStateModelURI);
		submodels.add(externalTemperatureModelURI);
		submodels.add(ChauffeEauTemperatureModelURI);
		submodels.add(ChauffeEauElectricityModelURI);

		// events received by the coupled model transmitted to its submodels
		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<>();

		imported.put(
				SwitchOnChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI,
								  SwitchOnChauffeEau.class)
				});
		imported.put(
				SetPowerChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI,
								  SetPowerChauffeEau.class)
				});
		imported.put(
				SwitchOffChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI,
								  SwitchOffChauffeEau.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI, Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI, DoNotHeat.class)
				});

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
				new EventSource(ChauffeEauStateModelURI,
								SwitchOnChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauElectricityModelURI,
									  SwitchOnChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI,
								SetPowerChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  SetPowerChauffeEau.class),
						new EventSink(ChauffeEauElectricityModelURI,
									  SetPowerChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI,
								SwitchOffChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  SwitchOffChauffeEau.class),
						new EventSink(ChauffeEauElectricityModelURI,
									  SwitchOffChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI, Heat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  Heat.class),
						new EventSink(ChauffeEauElectricityModelURI,
									  Heat.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  DoNotHeat.class),
						new EventSink(ChauffeEauElectricityModelURI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										externalTemperatureModelURI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  ChauffeEauTemperatureModelURI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				ChauffeEauCoupledModelURI,
				new RTCoupledHIOA_Descriptor(
						ChauffeEauCoupledModel.class,
						ChauffeEauCoupledModelURI,
						submodels,
						imported,
						null,
						connections,
						null,
						null,
						null,
						bindings,
						accelerationFactor));

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						architectureURI,
						ChauffeEauCoupledModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}

	/**
	 * create the local MIL simulation architecture for the tests of the
	 * {@code ChauffeEauUser} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @return						the local MIL simulation architecture for the unit tests of the {@code ChauffeEauUser} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createChauffeEauUserMILLocalArchitecture(
		String architectureURI,
		TimeUnit simulatedTimeUnit
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();
		// the ChauffeEau unit tester model only exchanges event, an
		// atomic model hence we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				ChauffeEauUnitTesterModel.MIL_URI,
				AtomicModelDescriptor.create(
						ChauffeEauUnitTesterModel.class,
						ChauffeEauUnitTesterModel.MIL_URI,
						simulatedTimeUnit,
						null));
		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();


		// simulation architecture
		Architecture architecture =
				new Architecture(
						architectureURI,
						ChauffeEauUnitTesterModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit);

		return architecture;
	}

	/**
	 * create the local MIL real time simulation architecture for the tests
	 * of the {@code ChauffeEauUser} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor used to execute in a logical time speeding up the real time.
	 * @return						the local MIL real time simulation architecture for the unit tests of the {@code ChauffeEauUser} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createChauffeEauUserMILRT_LocalArchitecture(
		String architectureURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();
		// the ChauffeEau unit tester model only exchanges event, an
		// atomic model hence we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				ChauffeEauUnitTesterModel.MIL_RT_URI,
				RTAtomicModelDescriptor.create(
						ChauffeEauUnitTesterModel.class,
						ChauffeEauUnitTesterModel.MIL_RT_URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();


		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						architectureURI,
						ChauffeEauUnitTesterModel.MIL_RT_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}

	/**
	 * create the local MIL simulation architecture for the {@code ChauffeEau}
	 * component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><img src="../../../../../../../../images/hem-2024-e3/ChauffeEauUnitTestLocalArchitecture.png"/></p> 
	 * <p>
	 * In this simulation architecture, the ChauffeEau simulator consists of five
	 * atomic models:
	 * </p>
	 * <ol>
	 * <li>The {@code ChauffeEauStateModel} keeps track of the state (switched on,
	 *   switched off, etc.) of the ChauffeEau and its current power level. The
	 *   state changes are triggered by the reception of external events through
	 *   {@code ChauffeEauCoupledModel} that imports and transmit them; whenever a
	 *   state change occurs, the triggering event is reemitted towards the
	 *   {@code ChauffeEauElectricityModel} and the {@code ChauffeEauTemperatureModel}
	 *   (except for {@code SwitchOnChauffeEau} that does not influence the
	 *   temperature model).</li>
	 * <li>The {@code ExternalTemperatureModel} simulates the temperature
	 *   outside the room, a part of the environment. The simulated temperature
	 *   is put in an exported variable {@code externalTemperature} that is
	 *   imported with the same name by the {@code ChauffeEauTemperatureModel}.</li>
	 * <li>The {@code ChauffeEauTemperatureModel} simulates the temperature inside
	 *   the heated room, using the external temperature provided by the
	 *   {@code ExternalTemperatureModel} and the current power of the ChauffeEau,
	 *   which it keeps track of through the {@code SetPowerChauffeEau} and
	 *   {@code SwitchOffChauffeEau} events. The evolution of the inside temperature
	 *   also obviously depends upon the fact that the ChauffeEau actually is
	 *   heating or not, a state which is kept track of through the events
	 *   {@code Heat} and {@code DoNotHeat}.</li>
	 * <li>The {@code ChauffeEauUnitTesterModel} simulates a ChauffeEau user and a
	 *   ChauffeEau controller by emitting state changing events towards the
	 *   {@code HairDryerStateModel}.</li>
	 * </ol>
	 * 
	 * <p>
	 * The {@code ChauffeEauElectricityModel} keeps track of the electric power
	 * consumed by the ChauffeEau in a variable <code>currentIntensity</code>,
	 * which is exported. As this variable is imported by the
	 * {@code ElectricMeterElectricityModel} in integration tests, the
	 * {@code ChauffeEauElectricityModel} is created in the {@code ElectricMeter}
	 * component simulator and the events emitted by the {@code ChauffeEauStateModel}
	 * are reexported by the {@code ChauffeEauCoupledModel} towards the
	 * {@code ElectricMeterCoupledModel} model.
	 * </li>
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit	simulated time unit used in the architecture.
	 * @return					the local MIL simulation architecture for the {@code ChauffeEau} component.
	 * @throws Exception		<i>to do</i>.
	 */
	public static Architecture	createChauffeEauMILLocalArchitecture4IntegrationTest(
		String architectureURI,
		TimeUnit simulatedTimeUnit
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				ChauffeEauStateModel.MIL_URI,
				AtomicModelDescriptor.create(
						ChauffeEauStateModel.class,
						ChauffeEauStateModel.MIL_URI,
						simulatedTimeUnit,
						null));
		// the ChauffeEau models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				ExternalTemperatureModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ExternalTemperatureModel.class,
						ExternalTemperatureModel.MIL_URI,
						simulatedTimeUnit,
						null));
		atomicModelDescriptors.put(
				ChauffeEauTemperatureModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ChauffeEauTemperatureModel.class,
						ChauffeEauTemperatureModel.MIL_URI,
						simulatedTimeUnit,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ChauffeEauStateModel.MIL_URI);
		submodels.add(ExternalTemperatureModel.MIL_URI);
		submodels.add(ChauffeEauTemperatureModel.MIL_URI);

		// events received by the coupled model transmitted to its submodels
		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<>();

		imported.put(
				SwitchOnChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI,
								  SwitchOnChauffeEau.class)
				});
		imported.put(
				SetPowerChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI,
								  SetPowerChauffeEau.class)
				});
		imported.put(
				SwitchOffChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI,
								  SwitchOffChauffeEau.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI, Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModel.MIL_URI, DoNotHeat.class)
				});

		// events emitted by submodels that are reexported towards other models
		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>();

		reexported.put(
				SwitchOnChauffeEau.class,
				new ReexportedEvent(ChauffeEauStateModel.MIL_URI,
									SwitchOnChauffeEau.class));
		reexported.put(
				SetPowerChauffeEau.class,
				new ReexportedEvent(ChauffeEauStateModel.MIL_URI,
									SetPowerChauffeEau.class));
		reexported.put(
				SwitchOffChauffeEau.class,
				new ReexportedEvent(ChauffeEauStateModel.MIL_URI,
									SwitchOffChauffeEau.class));
		reexported.put(
				Heat.class,
				new ReexportedEvent(ChauffeEauStateModel.MIL_URI,
									Heat.class));
		reexported.put(
				DoNotHeat.class,
				new ReexportedEvent(ChauffeEauStateModel.MIL_URI,
									DoNotHeat.class));

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI,
								SetPowerChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  SetPowerChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI,
								SwitchOffChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  SwitchOffChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI, Heat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  Heat.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModel.MIL_URI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModel.MIL_URI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										ExternalTemperatureModel.MIL_URI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  ChauffeEauTemperatureModel.MIL_URI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				ChauffeEauCoupledModel.MIL_URI,
				new CoupledHIOA_Descriptor(
						ChauffeEauCoupledModel.class,
						ChauffeEauCoupledModel.MIL_URI,
						submodels,
						imported,
						reexported,
						connections,
						null,
						null,
						null,
						bindings));

		// simulation architecture
		Architecture architecture =
				new Architecture(
						architectureURI,
						ChauffeEauCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit);

		return architecture;
	}

	/**
	 * create the local MIL real time or SIL simulation architecture for the
	 * {@code ChauffeEau} component when doing real time integration tests.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The simulation architecture created for {@code ChauffeEau} real time
	 * unit tests is the same as for non real time unit tests except that the
	 * simulation engines has to be real time, hence using the corresponding
	 * model and architecture descriptors. See
	 * {@code createChauffeEauMIL_ArchitectureUnitTest} for more detailed
	 * explanations about the architecture itself.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentSimulationType.isMILRTSimulation() || currentSimulationType.isSILSimulation()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentSimulationType	simulation type for the next run.
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor used to execute in a logical time speeding up the real time.
	 * @return						the local MIL real time simulation architecture for the unit tests of the {@code ChauffeEau} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createChauffeEau_RT_LocalArchitecture4IntegrationTest(
		SimulationType currentSimulationType,
		String architectureURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	currentSimulationType.isMILRTSimulation() ||
									currentSimulationType.isSILSimulation() :
				new PreconditionException(
						"currentSimulationType.isMILRTSimulation() || "
						+ "currentSimulationType.isSILSimulation()");

		String ChauffeEauStateModelURI = null;
		String ChauffeEauTemperatureModelURI = null;
		String externalTemperatureModelURI = null;
		String ChauffeEauCoupledModelURI = null;
		switch (currentSimulationType) {
		case MIL_RT_SIMULATION:
			ChauffeEauStateModelURI = ChauffeEauStateModel.MIL_RT_URI;
			ChauffeEauTemperatureModelURI = ChauffeEauTemperatureModel.MIL_RT_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.MIL_RT_URI;
			ChauffeEauCoupledModelURI = ChauffeEauCoupledModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			ChauffeEauStateModelURI = ChauffeEauStateModel.SIL_URI;
			ChauffeEauTemperatureModelURI = ChauffeEauTemperatureModel.SIL_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.SIL_URI;
			ChauffeEauCoupledModelURI = ChauffeEauCoupledModel.SIL_URI;
			break;
		default:
		}

		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				ChauffeEauStateModelURI,
				RTAtomicModelDescriptor.create(
						ChauffeEauStateModel.class,
						ChauffeEauStateModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		// the ChauffeEau models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				externalTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						ExternalTemperatureModel.class,
						externalTemperatureModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				ChauffeEauTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						ChauffeEauTemperatureModel.class,
						ChauffeEauTemperatureModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ChauffeEauStateModelURI);
		submodels.add(externalTemperatureModelURI);
		submodels.add(ChauffeEauTemperatureModelURI);

		// events received by the coupled model transmitted to its submodels
		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<>();

		imported.put(
				SwitchOnChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI,
								  SwitchOnChauffeEau.class)
				});
		imported.put(
				SetPowerChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI,
								  SetPowerChauffeEau.class)
				});
		imported.put(
				SwitchOffChauffeEau.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI,
								  SwitchOffChauffeEau.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI, Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
					new EventSink(ChauffeEauStateModelURI, DoNotHeat.class)
				});

		// events emitted by submodels that are reexported towards other models
		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>();

		reexported.put(
				SwitchOnChauffeEau.class,
				new ReexportedEvent(ChauffeEauStateModelURI,
									SwitchOnChauffeEau.class));
		reexported.put(
				SetPowerChauffeEau.class,
				new ReexportedEvent(ChauffeEauStateModelURI,
									SetPowerChauffeEau.class));
		reexported.put(
				SwitchOffChauffeEau.class,
				new ReexportedEvent(ChauffeEauStateModelURI,
									SwitchOffChauffeEau.class));
		reexported.put(
				Heat.class,
				new ReexportedEvent(ChauffeEauStateModelURI,
									Heat.class));
		reexported.put(
				DoNotHeat.class,
				new ReexportedEvent(ChauffeEauStateModelURI,
									DoNotHeat.class));

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
				new EventSource(ChauffeEauStateModelURI,
								SetPowerChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  SetPowerChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI,
								SwitchOffChauffeEau.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  SwitchOffChauffeEau.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI, Heat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  Heat.class)
				});
		connections.put(
				new EventSource(ChauffeEauStateModelURI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(ChauffeEauTemperatureModelURI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										externalTemperatureModelURI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  ChauffeEauTemperatureModelURI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				ChauffeEauCoupledModelURI,
				new RTCoupledHIOA_Descriptor(
						ChauffeEauCoupledModel.class,
						ChauffeEauCoupledModelURI,
						submodels,
						imported,
						reexported,
						connections,
						null,
						null,
						null,
						bindings,
						accelerationFactor));

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						architectureURI,
						ChauffeEauCoupledModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
