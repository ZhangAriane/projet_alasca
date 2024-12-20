package projet_alasca.equipements.refrigerateur.mil;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import projet_alasca.equipements.refrigerateur.mil.events.Cool;
import projet_alasca.equipements.refrigerateur.mil.events.DoNotCool;
import projet_alasca.equipements.refrigerateur.mil.events.DoNotFreeze;
import projet_alasca.equipements.refrigerateur.mil.events.Freeze;
import projet_alasca.equipements.refrigerateur.mil.events.SetPowerCongelateur;
import projet_alasca.equipements.refrigerateur.mil.events.SetPowerRefrigerateur;
import projet_alasca.equipements.refrigerateur.mil.events.SwitchOffRefrigerateur;
import projet_alasca.equipements.refrigerateur.mil.events.SwitchOnRefrigerateur;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunHeaterUnitarySimulation</code> creates a simulator
 * for the heater and then runs a typical simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class shows how to use simulation model descriptors to create the
 * description of a simulation architecture and then create an instance of this
 * architecture by instantiating and connecting the models. Note how models
 * are described by atomic model descriptors and coupled model descriptors and
 * then the connections between coupled models and their submodels as well as
 * exported events and variables to imported ones are described by different
 * maps. In this example, only connections of events and bindings of variables
 * between models within this architecture are necessary, but when creating
 * coupled models, they can also import and export events and variables
 * consumed and produced by their submodels.
 * </p>
 * <p>
 * The architecture object is the root of this description and it provides
 * the method {@code constructSimulator} that instantiate the models and
 * connect them. This method returns the reference on the simulator attached
 * to the root coupled model in the architecture instance, which is then used
 * to perform simulation runs by calling the method
 * {@code doStandAloneSimulation}.
 * </p>
 * <p>
 * The descriptors and maps can be viewed as kinds of nodes in the abstract
 * syntax tree of an architectural language that does not have a concrete
 * syntax yet.
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
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunRefrigerateurUnitaryMILSimulation
{
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
					new HashMap<>();

			// the heater models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					RefrigerateurElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							RefrigerateurElectricityModel.class,
							RefrigerateurElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					RefrigerateurTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							RefrigerateurTemperatureModel.class,
							RefrigerateurTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null));

			atomicModelDescriptors.put(
					CongelateurTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							CongelateurTemperatureModel.class,
							CongelateurTemperatureModel.URI,
							TimeUnit.HOURS,
							null));


			// the heater unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					RefrigerateurUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							RefrigerateurUnitTesterModel.class,
							RefrigerateurUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
					new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(RefrigerateurElectricityModel.URI);
			submodels.add(RefrigerateurTemperatureModel.URI);
			submodels.add(ExternalTemperatureModel.URI);
			submodels.add(RefrigerateurUnitTesterModel.URI);
			submodels.add(CongelateurTemperatureModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
					new HashMap<EventSource,EventSink[]>();

					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI,
									SetPowerRefrigerateur.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											SetPowerRefrigerateur.class)
							});
					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI,
									SwitchOnRefrigerateur.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											SwitchOnRefrigerateur.class)
							});
					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI,
									SwitchOffRefrigerateur.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											SwitchOffRefrigerateur.class),
									new EventSink(RefrigerateurTemperatureModel.URI,
											SwitchOffRefrigerateur.class)
							});
					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI, Cool.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											Cool.class),
									new EventSink(RefrigerateurTemperatureModel.URI,
											Cool.class)
							});
					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI, DoNotCool.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											DoNotCool.class),
									new EventSink(RefrigerateurTemperatureModel.URI,
											DoNotCool.class)
							});

					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI,
									SetPowerCongelateur.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											SetPowerCongelateur.class)
							});
					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI, Freeze.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											Freeze.class),
									new EventSink(CongelateurTemperatureModel.URI,
											Freeze.class)
							});
					connections.put(
							new EventSource(RefrigerateurUnitTesterModel.URI, DoNotFreeze.class),
							new EventSink[] {
									new EventSink(RefrigerateurElectricityModel.URI,
											DoNotFreeze.class),
									new EventSink(CongelateurTemperatureModel.URI,
											DoNotFreeze.class)
							});

					// variable bindings between exporting and importing models
					Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

							bindings.put(new VariableSource("externalTemperature",
									Double.class,
									ExternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("externalTemperature",
													Double.class,
													RefrigerateurTemperatureModel.URI)
							});
							bindings.put(new VariableSource("currentCoolingPower",
									Double.class,
									RefrigerateurElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentCoolingPower",
													Double.class,
													RefrigerateurTemperatureModel.URI)
							});
							
							bindings.put(new VariableSource("externalTemperature",
									Double.class,
									ExternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("externalTemperature",
													Double.class,
													CongelateurTemperatureModel.URI)
							});
							
							bindings.put(new VariableSource("currentFreezingPower",
									Double.class,
									RefrigerateurElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentFreezingPower",
													Double.class,
													CongelateurTemperatureModel.URI)
							});

							// coupled model descriptor
							coupledModelDescriptors.put(
									RefrigerateurCoupledModel.URI,
									new CoupledHIOA_Descriptor(
											RefrigerateurCoupledModel.class,
											RefrigerateurCoupledModel.URI,
											submodels,
											null,
											null,
											connections,
											null,
											null,
											null,
											bindings));

							// simulation architecture
							ArchitectureI architecture =
									new Architecture(
											RefrigerateurCoupledModel.URI,
											atomicModelDescriptors,
											coupledModelDescriptors,
											TimeUnit.HOURS);

							// create the simulator from the simulation architecture
							SimulatorI se = architecture.constructSimulator();
							// this add additional time at each simulation step in
							// standard simulations (useful when debugging)
							SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
							// run a simulation with the simulation beginning at 0.0 and
							// ending at 24.0
							se.doStandAloneSimulation(0.0, 24.0);
							System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
