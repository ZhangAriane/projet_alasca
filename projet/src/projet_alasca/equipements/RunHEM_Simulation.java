package projet_alasca.equipements;

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


import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.HairDryerElectricityModel;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.HairDryerUserModel;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SetHighHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SetLowHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.ExternalTemperatureModel;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.HeaterElectricityModel;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.HeaterTemperatureModel;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.HeaterUnitTesterModel;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.events.SetPowerHeater;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2024e2.equipments.heater.mil.events.SwitchOnHeater;
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
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import projet_alasca.equipements.HEM_CoupledModel.HEM_Report;
import projet_alasca.equipements.machineCafe.mil.MachineCafeElectricityModel;
import projet_alasca.equipements.machineCafe.mil.MachineCafeUserModel;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOffMachineCafe;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOnMachineCafe;
import projet_alasca.equipements.meter.mil.ElectricMeterElectricityModel;
import projet_alasca.equipements.ventilateur.mil.VentilateurElectricityModel;
import projet_alasca.equipements.ventilateur.mil.VentilateurUserModel;
import projet_alasca.equipements.ventilateur.mil.events.SetHighVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetLowVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetMediumVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOffVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOnVentilateur;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunHEM_Simulation</code> creates the simulator for the
 * household energy management example and then runs a typical simulation.
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
 * {@code doStandAloneSimulation}. Notice the use of the method
 * {@code setSimulationRunParameters} to initialise some parameters of
 * the simulation defined in the different models. This method is implemented
 * to traverse all of the models, hence each one can get its own parameters by
 * carefully defining unique names for them. Also, it shows how to get the
 * simulation reports from the models after the simulation run.
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
 * <p>Created on : 2023-10-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunHEM_Simulation
{
	public static void	main(String[] args)
	{
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
					new HashMap<>();

			// atomic HIOA models require AtomicHIOA_Descriptor while
			// atomic models require AtomicModelDescriptor

			// hair dryer models
			atomicModelDescriptors.put(
					HairDryerElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							HairDryerElectricityModel.class,
							HairDryerElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					HairDryerUserModel.URI,
					AtomicModelDescriptor.create(
							HairDryerUserModel.class,
							HairDryerUserModel.URI,
							TimeUnit.HOURS,
							null));

			// the heater models
			atomicModelDescriptors.put(
					HeaterElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							HeaterElectricityModel.class,
							HeaterElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					HeaterTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							HeaterTemperatureModel.class,
							HeaterTemperatureModel.URI,
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
					HeaterUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							HeaterUnitTesterModel.class,
							HeaterUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));

			// the electric meter model
			atomicModelDescriptors.put(
					ElectricMeterElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							ElectricMeterElectricityModel.class,
							ElectricMeterElectricityModel.URI,
							TimeUnit.HOURS,
							null));

			// ventilateur models
			atomicModelDescriptors.put(
					VentilateurElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							VentilateurElectricityModel.class,
							VentilateurElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					VentilateurUserModel.URI,
					AtomicModelDescriptor.create(
							VentilateurUserModel.class,
							VentilateurUserModel.URI,
							TimeUnit.HOURS,
							null));

			// machine cafe models
			atomicModelDescriptors.put(
					MachineCafeElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							MachineCafeElectricityModel.class,
							MachineCafeElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					MachineCafeUserModel.URI,
					AtomicModelDescriptor.create(
							MachineCafeUserModel.class,
							MachineCafeUserModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
					new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(HairDryerElectricityModel.URI);
			submodels.add(HairDryerUserModel.URI);
			submodels.add(HeaterElectricityModel.URI);
			submodels.add(HeaterTemperatureModel.URI);
			submodels.add(ExternalTemperatureModel.URI);
			submodels.add(HeaterUnitTesterModel.URI);
			submodels.add(ElectricMeterElectricityModel.URI);
			
			//ventilateur
			submodels.add(VentilateurElectricityModel.URI);
			submodels.add(VentilateurUserModel.URI);
			//machine cafe
			submodels.add(MachineCafeElectricityModel.URI);
			submodels.add(MachineCafeUserModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
					new HashMap<EventSource,EventSink[]>();

					connections.put(
							new EventSource(HairDryerUserModel.URI, SwitchOnHairDryer.class),
							new EventSink[] {
									new EventSink(HairDryerElectricityModel.URI,
											SwitchOnHairDryer.class)
							});
					connections.put(
							new EventSource(HairDryerUserModel.URI, SwitchOffHairDryer.class),
							new EventSink[] {
									new EventSink(HairDryerElectricityModel.URI,
											SwitchOffHairDryer.class)
							});
					connections.put(
							new EventSource(HairDryerUserModel.URI, SetHighHairDryer.class),
							new EventSink[] {
									new EventSink(HairDryerElectricityModel.URI,
											SetHighHairDryer.class)
							});
					connections.put(
							new EventSource(HairDryerUserModel.URI, SetLowHairDryer.class),
							new EventSink[] {
									new EventSink(HairDryerElectricityModel.URI,
											SetLowHairDryer.class)
							});

					connections.put(
							new EventSource(HeaterUnitTesterModel.URI,
									SetPowerHeater.class),
							new EventSink[] {
									new EventSink(HeaterElectricityModel.URI,
											SetPowerHeater.class)
							});
					connections.put(
							new EventSource(HeaterUnitTesterModel.URI,
									SwitchOnHeater.class),
							new EventSink[] {
									new EventSink(HeaterElectricityModel.URI,
											SwitchOnHeater.class)
							});
					connections.put(
							new EventSource(HeaterUnitTesterModel.URI,
									SwitchOffHeater.class),
							new EventSink[] {
									new EventSink(HeaterElectricityModel.URI,
											SwitchOffHeater.class),
									new EventSink(HeaterTemperatureModel.URI,
											SwitchOffHeater.class)
							});
					connections.put(
							new EventSource(HeaterUnitTesterModel.URI, Heat.class),
							new EventSink[] {
									new EventSink(HeaterElectricityModel.URI,
											Heat.class),
									new EventSink(HeaterTemperatureModel.URI,
											Heat.class)
							});
					connections.put(
							new EventSource(HeaterUnitTesterModel.URI, DoNotHeat.class),
							new EventSink[] {
									new EventSink(HeaterElectricityModel.URI,
											DoNotHeat.class),
									new EventSink(HeaterTemperatureModel.URI,
											DoNotHeat.class)
							});
					
					//ventilateur
					connections.put(
							new EventSource(VentilateurUserModel.URI, SwitchOnVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.URI,
											SwitchOnVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.URI, SwitchOffVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.URI,
											SwitchOffVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.URI, SetHighVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.URI,
											SetHighVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.URI, SetMediumVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.URI,
											SetMediumVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.URI, SetLowVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.URI,
											SetLowVentilateur.class)
							});
					//machine cafe
					connections.put(
							new EventSource(MachineCafeUserModel.URI, SwitchOnMachineCafe.class),
							new EventSink[] {
									new EventSink(MachineCafeElectricityModel.URI,
											SwitchOnMachineCafe.class)
							});
					connections.put(
							new EventSource(MachineCafeUserModel.URI, SwitchOffMachineCafe.class),
							new EventSink[] {
									new EventSink(MachineCafeElectricityModel.URI,
											SwitchOffMachineCafe.class)
							});
					

					// variable bindings between exporting and importing models
					Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

							// bindings among heater models
							bindings.put(
									new VariableSource("externalTemperature",
											Double.class,
											ExternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("externalTemperature",
													Double.class,
													HeaterTemperatureModel.URI)
									});
							bindings.put(
									new VariableSource("currentHeatingPower",
											Double.class,
											HeaterElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentHeatingPower",
													Double.class,
													HeaterTemperatureModel.URI)
									});

							// bindings between hair dryer and heater models to the electric
							// meter model
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											HairDryerElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentHairDryerIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											HeaterElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentHeaterIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							
							// bindings between ventilateur and machine cafe models to the electric
							// meter model
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											VentilateurElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentVentilateurIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											MachineCafeElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentMachineCafeIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});


							// coupled model descriptor: an HIOA requires a
							// CoupledHIOA_Descriptor
							coupledModelDescriptors.put(
									HEM_CoupledModel.URI,
									new CoupledHIOA_Descriptor(
											HEM_CoupledModel.class,
											HEM_CoupledModel.URI,
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
											HEM_CoupledModel.URI,
											atomicModelDescriptors,
											coupledModelDescriptors,
											TimeUnit.HOURS);

							// create the simulator from the simulation architecture
							SimulatorI se = architecture.constructSimulator();

							// Optional: how to use simulation run parameters to modify
							// the behaviour of the models from runs to runs
							Map<String,Object> simParams = new HashMap<>();
							simParams.put(
									ModelI.createRunParameterName(
											HairDryerElectricityModel.URI,
											HairDryerElectricityModel.LOW_MODE_CONSUMPTION_RPNAME),
									1320.0);
							simParams.put(
									ModelI.createRunParameterName(
											HairDryerElectricityModel.URI,
											HairDryerElectricityModel.HIGH_MODE_CONSUMPTION_RPNAME),
									2200.0);
							simParams.put(ModelI.createRunParameterName(
									HairDryerUserModel.URI,
									HairDryerUserModel.MEAN_STEP_RPNAME),
									0.05);
							simParams.put(ModelI.createRunParameterName(
									HairDryerUserModel.URI,
									HairDryerUserModel.MEAN_DELAY_RPNAME),
									2.0);
							simParams.put(
									ModelI.createRunParameterName(
											HeaterElectricityModel.URI,
											HeaterElectricityModel.NOT_HEATING_POWER_RUNPNAME),
									0.0);
							simParams.put(
									ModelI.createRunParameterName(
											HeaterElectricityModel.URI,
											HeaterElectricityModel.MAX_HEATING_POWER_RUNPNAME),
									4400.0);
							
							//ventilateur
							simParams.put(
									ModelI.createRunParameterName(
											VentilateurElectricityModel.URI,
											VentilateurElectricityModel.LOW_MODE_CONSUMPTION_RPNAME),
									1320.0);
							simParams.put(
									ModelI.createRunParameterName(
											VentilateurElectricityModel.URI,
											VentilateurElectricityModel.MEDIUM_MODE_CONSUMPTION_RPNAME),
									2200.0);
							simParams.put(
									ModelI.createRunParameterName(
											VentilateurElectricityModel.URI,
											VentilateurElectricityModel.HIGH_MODE_CONSUMPTION_RPNAME),
									2200.0);
							simParams.put(ModelI.createRunParameterName(
									VentilateurUserModel.URI,
									VentilateurUserModel.MEAN_STEP_RPNAME),
									0.05);
							simParams.put(ModelI.createRunParameterName(
									VentilateurUserModel.URI,
									VentilateurUserModel.MEAN_DELAY_RPNAME),
									2.0);
							
							//machine cafe
							simParams.put(
									ModelI.createRunParameterName(
											MachineCafeElectricityModel.URI,
											MachineCafeElectricityModel.CONSUMPTION_RPNAME),
									1320.0);
							simParams.put(ModelI.createRunParameterName(
									MachineCafeUserModel.URI,
									MachineCafeUserModel.MEAN_STEP_RPNAME),
									0.05);
							simParams.put(ModelI.createRunParameterName(
									MachineCafeUserModel.URI,
									MachineCafeUserModel.MEAN_DELAY_RPNAME),
									2.0);
							
							
							se.setSimulationRunParameters(simParams);

							// this add additional time at each simulation step in
							// standard simulations (useful for debugging)
							SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
							// run a simulation with the simulation beginning at 0.0 and
							// ending at 24.0 hours
							se.doStandAloneSimulation(0.0, 24.0);

							// Optional: simulation report
//							HEM_Report r = (HEM_Report) se.getFinalReport();
//							System.out.println(r.printout(""));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
