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
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import projet_alasca.equipements.HEM_CoupledModel.HEM_Report;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauElectricityModel;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauTemperatureModel;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauUnitTesterModel;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOffChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOnChauffeEau;
import projet_alasca.equipements.machineCafe.mil.MachineCafeElectricityModel;
import projet_alasca.equipements.machineCafe.mil.MachineCafeUserModel;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOffMachineCafe;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOnMachineCafe;
import projet_alasca.equipements.meter.mil.ElectricMeterElectricityModel;
import projet_alasca.equipements.refrigerateur.Refrigerateur;
import projet_alasca.equipements.refrigerateur.mil.CongelateurTemperatureModel;
import projet_alasca.equipements.refrigerateur.mil.RefrigerateurElectricityModel;
import projet_alasca.equipements.refrigerateur.mil.RefrigerateurTemperatureModel;
import projet_alasca.equipements.refrigerateur.mil.RefrigerateurUnitTesterModel;
import projet_alasca.equipements.refrigerateur.mil.events.Cool;
import projet_alasca.equipements.refrigerateur.mil.events.DoNotCool;
import projet_alasca.equipements.refrigerateur.mil.events.DoNotFreeze;
import projet_alasca.equipements.refrigerateur.mil.events.Freeze;
import projet_alasca.equipements.refrigerateur.mil.events.SetPowerCongelateur;
import projet_alasca.equipements.refrigerateur.mil.events.SetPowerRefrigerateur;
import projet_alasca.equipements.refrigerateur.mil.events.SwitchOffRefrigerateur;
import projet_alasca.equipements.refrigerateur.mil.events.SwitchOnRefrigerateur;
import projet_alasca.equipements.ventilateur.mil.VentilateurElectricityModel;
import projet_alasca.equipements.ventilateur.mil.VentilateurUserModel;
import projet_alasca.equipements.ventilateur.mil.events.SetHighVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetLowVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetMediumVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOffVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOnVentilateur;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunHEM_RT_Simulation</code> creates the real time simulator
 * for the household energy management example and then runs a typical
 * simulation in real time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class shows how to describe, construct and then run a real time
 * simulation. By comparison with {@code RunHEM_Simulation}, differences
 * help understanding the passage from a synthetic simulation time run
 * to a real time one. Recall that real time simulations force the simulation
 * time to follow the real time, hence in a standard real time run, the
 * simulation time advance at the rhythm of the real time. However, such
 * simulation runs can become either very lengthy, for examples like the
 * household energy management where simulation runs could last several days,
 * or very short, for examples like simulating microprocessors where events
 * can occur at the nanosecond time scale. So it is also possible to keep the
 * same time structure but to accelerate or decelerate the real time by some
 * factor, here defined as {@code ACCELERATION_FACTOR}. A value greater than
 * one will accelerate the simulation while a value strictly between 0 and 1
 * will decelerate it.
 * </p>
 * <p>
 * So, notice the use of real time equivalent to the model descriptors and
 * the simulation engine attached to models, as well as the acceleration
 * factor passed as parameter through the descriptors. The same acceleration
 * factor must be imposed to all models to get time coherent simulations.
 * </p>
 * <p>
 * The interest of real time simulations will become clear when simulation
 * models will be used in SIL simulations with the actual component software
 * executing in parallel to the simulations. Time coherent exchanges will then
 * become possible between the code and the simulations as the execution
 * of code instructions will occur on the same time frame as the simulations.
 * </p>
 * 
 * <p><strong>Glass-box Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code ACCELERATION_FACTOR > 0.0}
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
public class			RunHEM_RT_Simulation
{
	/** acceleration factor for the real time simulation; with a factor 2.0,
	 *  the simulation runs two times faster than real time i.e., a run that
	 *  is supposed to take 10 seconds in real time will take 5 seconds to
	 *  execute.															*/
	protected static final double ACCELERATION_FACTOR = 600.0;

	public static void	main(String[] args)
	{
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
					new HashMap<>();

			// atomic HIOA models require RTAtomicHIOA_Descriptor while
			// atomic models require RTAtomicModelDescriptor
			// the same acceleration factor must be used for all models

			// hair dryer models
			atomicModelDescriptors.put(
					HairDryerElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HairDryerElectricityModel.class,
							HairDryerElectricityModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					HairDryerUserModel.URI,
					RTAtomicModelDescriptor.create(
							HairDryerUserModel.class,
							HairDryerUserModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// the heater models
			atomicModelDescriptors.put(
					HeaterElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HeaterElectricityModel.class,
							HeaterElectricityModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					HeaterTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HeaterTemperatureModel.class,
							HeaterTemperatureModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					HeaterUnitTesterModel.URI,
					RTAtomicModelDescriptor.create(
							HeaterUnitTesterModel.class,
							HeaterUnitTesterModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// the electric meter model
			atomicModelDescriptors.put(
					ElectricMeterElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ElectricMeterElectricityModel.class,
							ElectricMeterElectricityModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// ventilateur models
			atomicModelDescriptors.put(
					VentilateurElectricityModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							VentilateurElectricityModel.class,
							VentilateurElectricityModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					VentilateurUserModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							VentilateurUserModel.class,
							VentilateurUserModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// machine cafe models
			atomicModelDescriptors.put(
					MachineCafeElectricityModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							MachineCafeElectricityModel.class,
							MachineCafeElectricityModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					MachineCafeUserModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							MachineCafeUserModel.class,
							MachineCafeUserModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// the refrigerateur models
			atomicModelDescriptors.put(
					RefrigerateurElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							RefrigerateurElectricityModel.class,
							RefrigerateurElectricityModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					RefrigerateurTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							RefrigerateurTemperatureModel.class,
							RefrigerateurTemperatureModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
//			atomicModelDescriptors.put(
//					CongelateurTemperatureModel.URI,
//					RTAtomicHIOA_Descriptor.create(
//							CongelateurTemperatureModel.class,
//							CongelateurTemperatureModel.URI,
//							TimeUnit.HOURS,
//							null,
//							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					projet_alasca.equipements.refrigerateur.mil.ExternalTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							projet_alasca.equipements.refrigerateur.mil.ExternalTemperatureModel.class,
							projet_alasca.equipements.refrigerateur.mil.ExternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					RefrigerateurUnitTesterModel.URI,
					RTAtomicModelDescriptor.create(
							RefrigerateurUnitTesterModel.class,
							RefrigerateurUnitTesterModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// the chauffe eau models
			atomicModelDescriptors.put(
					ChauffeEauElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ChauffeEauElectricityModel.class,
							ChauffeEauElectricityModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					ChauffeEauTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ChauffeEauTemperatureModel.class,
							ChauffeEauTemperatureModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					projet_alasca.equipements.chauffeEau.mil.ExternalTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							projet_alasca.equipements.chauffeEau.mil.ExternalTemperatureModel.class,
							projet_alasca.equipements.chauffeEau.mil.ExternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					ChauffeEauUnitTesterModel.URI,
					RTAtomicModelDescriptor.create(
							ChauffeEauUnitTesterModel.class,
							ChauffeEauUnitTesterModel.URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

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
			submodels.add(VentilateurElectricityModel.MIL_RT_URI);
			submodels.add(VentilateurUserModel.MIL_RT_URI);
			//machine cafe
			submodels.add(MachineCafeElectricityModel.MIL_RT_URI);
			submodels.add(MachineCafeUserModel.MIL_RT_URI);

			//refrigerateur
			submodels.add(RefrigerateurElectricityModel.URI);
			submodels.add(RefrigerateurTemperatureModel.URI);
			submodels.add(projet_alasca.equipements.refrigerateur.mil.ExternalTemperatureModel.URI);
			submodels.add(RefrigerateurUnitTesterModel.URI);
//			submodels.add(CongelateurTemperatureModel.URI);

			//chauffe eau
			submodels.add(ChauffeEauElectricityModel.URI);
			submodels.add(ChauffeEauTemperatureModel.URI);
			submodels.add(projet_alasca.equipements.chauffeEau.mil.ExternalTemperatureModel.URI);
			submodels.add(ChauffeEauUnitTesterModel.URI);

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
							new EventSource(VentilateurUserModel.MIL_RT_URI, SwitchOnVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.MIL_RT_URI,
											SwitchOnVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.MIL_RT_URI, SwitchOffVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.MIL_RT_URI,
											SwitchOffVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.MIL_RT_URI, SetHighVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.MIL_RT_URI,
											SetHighVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.MIL_RT_URI, SetMediumVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.MIL_RT_URI,
											SetMediumVentilateur.class)
							});
					connections.put(
							new EventSource(VentilateurUserModel.MIL_RT_URI, SetLowVentilateur.class),
							new EventSink[] {
									new EventSink(VentilateurElectricityModel.MIL_RT_URI,
											SetLowVentilateur.class)
							});
					//machine cafe
					connections.put(
							new EventSource(MachineCafeUserModel.MIL_RT_URI, SwitchOnMachineCafe.class),
							new EventSink[] {
									new EventSink(MachineCafeElectricityModel.MIL_RT_URI,
											SwitchOnMachineCafe.class)
							});
					connections.put(
							new EventSource(MachineCafeUserModel.MIL_RT_URI, SwitchOffMachineCafe.class),
							new EventSink[] {
									new EventSink(MachineCafeElectricityModel.MIL_RT_URI,
											SwitchOffMachineCafe.class)
							});

					//refrigerateur
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
					
//					connections.put(
//							new EventSource(RefrigerateurUnitTesterModel.URI, Freeze.class),
//							new EventSink[] {
//									new EventSink(RefrigerateurElectricityModel.URI,
//											Freeze.class),
//									new EventSink(CongelateurTemperatureModel.URI,
//											Freeze.class)
//							});
//					connections.put(
//							new EventSource(RefrigerateurUnitTesterModel.URI, DoNotFreeze.class),
//							new EventSink[] {
//									new EventSink(RefrigerateurElectricityModel.URI,
//											DoNotFreeze.class),
//									new EventSink(CongelateurTemperatureModel.URI,
//											DoNotFreeze.class)
//							});
//					connections.put(
//							new EventSource(RefrigerateurUnitTesterModel.URI,
//									SetPowerRefrigerateur.class),
//							new EventSink[] {
//									new EventSink(RefrigerateurElectricityModel.URI,
//											SetPowerRefrigerateur.class)
//							});
//					connections.put(
//							new EventSource(RefrigerateurUnitTesterModel.URI,
//									SetPowerCongelateur.class),
//							new EventSink[] {
//									new EventSink(RefrigerateurElectricityModel.URI,
//											SetPowerCongelateur.class)
//							});
					
					//chaffe eau
					connections.put(
							new EventSource(ChauffeEauUnitTesterModel.URI,
									SetPowerChauffeEau.class),
							new EventSink[] {
									new EventSink(ChauffeEauElectricityModel.URI,
											SetPowerChauffeEau.class)
							});
					connections.put(
							new EventSource(ChauffeEauUnitTesterModel.URI,
									SwitchOnChauffeEau.class),
							new EventSink[] {
									new EventSink(ChauffeEauElectricityModel.URI,
											SwitchOnChauffeEau.class)
							});
					connections.put(
							new EventSource(ChauffeEauUnitTesterModel.URI,
									SwitchOffChauffeEau.class),
							new EventSink[] {
									new EventSink(ChauffeEauElectricityModel.URI,
											SwitchOffChauffeEau.class),
									new EventSink(ChauffeEauTemperatureModel.URI,
											SwitchOffChauffeEau.class)
							});
					connections.put(
							new EventSource(ChauffeEauUnitTesterModel.URI, projet_alasca.equipements.chauffeEau.mil.events.Heat.class),
							new EventSink[] {
									new EventSink(ChauffeEauElectricityModel.URI,
											projet_alasca.equipements.chauffeEau.mil.events.Heat.class),
									new EventSink(ChauffeEauTemperatureModel.URI,
											projet_alasca.equipements.chauffeEau.mil.events.Heat.class)
							});
					connections.put(
							new EventSource(ChauffeEauUnitTesterModel.URI, projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat.class),
							new EventSink[] {
									new EventSink(ChauffeEauElectricityModel.URI,
											projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat.class),
									new EventSink(ChauffeEauTemperatureModel.URI,
											projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat.class)
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

							// bindings among refrigerateur models
							bindings.put(
									new VariableSource("externalTemperature",
											Double.class,
											projet_alasca.equipements.refrigerateur.mil.ExternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("externalTemperature",
													Double.class,
													RefrigerateurTemperatureModel.URI)
									});
							bindings.put(
									new VariableSource("currentCoolingPower",
											Double.class,
											RefrigerateurElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentCoolingPower",
													Double.class,
													RefrigerateurTemperatureModel.URI)
									});
							
//							bindings.put(
//									new VariableSource("currentFreezingPower",
//											Double.class,
//											RefrigerateurElectricityModel.URI),
//									new VariableSink[] {
//											new VariableSink("currentFreezingPower",
//													Double.class,
//													CongelateurTemperatureModel.URI)
//									});

							// bindings among chauffe eau models
							bindings.put(
									new VariableSource("externalTemperature",
											Double.class,
											projet_alasca.equipements.chauffeEau.mil.ExternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("externalTemperature",
													Double.class,
													ChauffeEauTemperatureModel.URI)
									});
							bindings.put(
									new VariableSource("currentHeatingPower",
											Double.class,
											ChauffeEauElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentHeatingPower",
													Double.class,
													ChauffeEauTemperatureModel.URI)
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
							//ventilateur
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											VentilateurElectricityModel.MIL_RT_URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentVentilateurIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							//machine cafe
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											MachineCafeElectricityModel.MIL_RT_URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentMachineCafeIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});

							//refrigerateur
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											RefrigerateurElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentRefrigerateurIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							//chauffe eau
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											ChauffeEauElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentIntensity",
													Double.class,
													"currentChauffeEauIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});

							// coupled model descriptor: an HIOA requires a
							// RTCoupledHIOA_Descriptor
							coupledModelDescriptors.put(
									HEM_CoupledModel.URI,
									new RTCoupledHIOA_Descriptor(
											HEM_CoupledModel.class,
											HEM_CoupledModel.URI,
											submodels,
											null,
											null,
											connections,
											null,
											null,
											null,
											bindings,
											ACCELERATION_FACTOR));

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
											VentilateurElectricityModel.MIL_RT_URI,
											VentilateurElectricityModel.LOW_MODE_CONSUMPTION_RPNAME),
									1320.0);
							simParams.put(
									ModelI.createRunParameterName(
											VentilateurElectricityModel.MIL_RT_URI,
											VentilateurElectricityModel.MEDIUM_MODE_CONSUMPTION_RPNAME),
									1320.0);
							simParams.put(
									ModelI.createRunParameterName(
											VentilateurElectricityModel.MIL_RT_URI,
											VentilateurElectricityModel.HIGH_MODE_CONSUMPTION_RPNAME),
									2200.0);
							simParams.put(ModelI.createRunParameterName(
									VentilateurUserModel.MIL_RT_URI,
									VentilateurUserModel.MEAN_STEP_RPNAME),
									0.05);
							simParams.put(ModelI.createRunParameterName(
									VentilateurUserModel.MIL_RT_URI,
									VentilateurUserModel.MEAN_DELAY_RPNAME),
									2.0);

							//Machine cafe
							simParams.put(
									ModelI.createRunParameterName(
											MachineCafeElectricityModel.MIL_RT_URI,
											MachineCafeElectricityModel.CONSUMPTION_RPNAME),
									1320.0);
							simParams.put(ModelI.createRunParameterName(
									VentilateurUserModel.MIL_RT_URI,
									VentilateurUserModel.MEAN_STEP_RPNAME),
									0.05);
							simParams.put(ModelI.createRunParameterName(
									MachineCafeUserModel.MIL_RT_URI,
									MachineCafeUserModel.MEAN_DELAY_RPNAME),
									2.0);

							//refrigerateur
							simParams.put(
									ModelI.createRunParameterName(
											RefrigerateurElectricityModel.URI,
											RefrigerateurElectricityModel.NOT_COOLING_POWER_RUNPNAME),
									0.0);
							simParams.put(
									ModelI.createRunParameterName(
											RefrigerateurElectricityModel.URI,
											RefrigerateurElectricityModel.MAX_COOLING_POWER_RUNPNAME),
									4400.0);
							
//							simParams.put(
//									ModelI.createRunParameterName(
//											RefrigerateurElectricityModel.URI,
//											RefrigerateurElectricityModel.NOT_FREEZING_POWER_RUNPNAME),
//									0.0);
//							simParams.put(
//									ModelI.createRunParameterName(
//											RefrigerateurElectricityModel.URI,
//											RefrigerateurElectricityModel.MAX_FREEZING_POWER_RUNPNAME),
//									4400.0);

							//chauffe eau
							simParams.put(
									ModelI.createRunParameterName(
											ChauffeEauElectricityModel.URI,
											ChauffeEauElectricityModel.NOT_HEATING_POWER_RUNPNAME),
									0.0);
							simParams.put(
									ModelI.createRunParameterName(
											ChauffeEauElectricityModel.URI,
											ChauffeEauElectricityModel.MAX_HEATING_POWER_RUNPNAME),
									4400.0);


							se.setSimulationRunParameters(simParams);

							// run a simulation with the simulation beginning at 0.0 and
							// ending at 24.0

							// duration of the simulation in hours, the simulation time unit
							double simDuration = 24.0;
							// the real time of start of the simulation plus a 1s delay to give
							// the time to initialise all models in the architecture.
							long start = System.currentTimeMillis() + 1000L;

							se.startRTSimulation(start, 0.0, simDuration);

							// wait until the simulation ends i.e., the start delay  plus the
							// duration of the simulation in milliseconds plus another 2s delay
							// to make sure...
							Thread.sleep(1000L
									+ ((long)((simDuration*3600*1000.0)/ACCELERATION_FACTOR))
									+ 3000L);
							// Optional: simulation report
							HEM_Report r = (HEM_Report) se.getFinalReport();
							System.out.println(r.printout(""));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
