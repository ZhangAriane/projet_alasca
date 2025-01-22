package projet_alasca.etape3;

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
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import projet_alasca.equipements.chauffeEau.ChauffeEau;
import projet_alasca.equipements.chauffeEau.ChauffeEauUser;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauCoupledModel;
import projet_alasca.equipements.chauffeEau.mil.ChauffeEauUnitTesterModel;
import projet_alasca.equipements.chauffeEau.mil.events.SetPowerChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOffChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.SwitchOnChauffeEau;
import projet_alasca.equipements.chauffeEau.mil.events.Heat;
import projet_alasca.equipements.chauffeEau.mil.events.DoNotHeat;
import projet_alasca.equipements.machineCafe.MachineCafe;
import projet_alasca.equipements.machineCafe.MachineCafeUser;
import projet_alasca.equipements.machineCafe.mil.MachineCafeCoupledModel;
import projet_alasca.equipements.machineCafe.mil.MachineCafeStateModel;
import projet_alasca.equipements.machineCafe.mil.MachineCafeUserModel;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOffMachineCafe;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOnMachineCafe;
import projet_alasca.equipements.ventilateur.Ventilateur;
import projet_alasca.equipements.ventilateur.VentilateurUser;
import projet_alasca.equipements.ventilateur.mil.VentilateurStateModel;
import projet_alasca.equipements.ventilateur.mil.VentilateurUserModel;
import projet_alasca.equipements.ventilateur.mil.events.SetHighVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetLowVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SetMediumVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOffVentilateur;
import projet_alasca.equipements.ventilateur.mil.events.SwitchOnVentilateur;
import projet_alasca.etape3.equipments.hairdryer.HairDryer;
import projet_alasca.etape3.equipments.hairdryer.HairDryerUser;
import projet_alasca.etape3.equipments.hairdryer.mil.HairDryerStateModel;
import projet_alasca.etape3.equipments.hairdryer.mil.HairDryerUserModel;
import projet_alasca.etape3.equipments.hairdryer.mil.events.SetHighHairDryer;
import projet_alasca.etape3.equipments.hairdryer.mil.events.SetLowHairDryer;
import projet_alasca.etape3.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import projet_alasca.etape3.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import projet_alasca.etape3.equipments.heater.Heater;
import projet_alasca.etape3.equipments.heater.HeaterUser;
import projet_alasca.etape3.equipments.heater.mil.HeaterCoupledModel;
import projet_alasca.etape3.equipments.heater.mil.HeaterUnitTesterModel;
import projet_alasca.etape3.equipments.heater.mil.events.SetPowerHeater;
import projet_alasca.etape3.equipments.heater.mil.events.SwitchOffHeater;
import projet_alasca.etape3.equipments.heater.mil.events.SwitchOnHeater;
import projet_alasca.etape3.equipments.meter.ElectricMeter;
import projet_alasca.etape3.equipments.meter.sim.ElectricMeterCoupledModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentSimulationArchitectures</code> defines the global
 * component simulation architectures for the whole HEM application.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	ComponentSimulationArchitectures
{
	/**
	 * create the global MIL component simulation architecture for the HEM
	 * application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI of the component model architecture to be created.
	 * @param simulatedTimeUnit	simulated time unit used in the architecture.
	 * @return					the global MIL simulation  architecture for the HEM application.
	 * @throws Exception		<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture
	createMILComponentSimulationArchitectures(
			String architectureURI, 
			TimeUnit simulatedTimeUnit
			) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		atomicModelDescriptors.put(
				HairDryerStateModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						HairDryerStateModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,
							SwitchOffHairDryer.class,
							SetLowHairDryer.class,
							SetHighHairDryer.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnHairDryer.class,
								SwitchOffHairDryer.class,
								SetLowHairDryer.class,
								SetHighHairDryer.class},
						simulatedTimeUnit,
						HairDryer.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				HairDryerUserModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						HairDryerUserModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,
							SwitchOffHairDryer.class,
							SetLowHairDryer.class,
							SetHighHairDryer.class},
						simulatedTimeUnit,
						HairDryerUser.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				HeaterCoupledModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						HeaterCoupledModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerHeater.class,
							SwitchOnHeater.class,
							SwitchOffHeater.class,
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerHeater.class,
								SwitchOnHeater.class,
								SwitchOffHeater.class,
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						simulatedTimeUnit,
						Heater.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				HeaterUnitTesterModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						HeaterUnitTesterModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHeater.class,
							SwitchOffHeater.class,
							SetPowerHeater.class,
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						simulatedTimeUnit,
						HeaterUser.REFLECTION_INBOUND_PORT_URI));


		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,
							SwitchOffHairDryer.class,
							SetLowHairDryer.class,
							SetHighHairDryer.class,
							SetPowerHeater.class,
							SwitchOnHeater.class,
							SwitchOffHeater.class,
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class,
							
							SwitchOnMachineCafe.class,
							SwitchOffMachineCafe.class,
							
							SwitchOnVentilateur.class,
							SwitchOffVentilateur.class,
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							
							SetPowerChauffeEau.class,
							SwitchOnChauffeEau.class,
							SwitchOffChauffeEau.class,
							Heat.class,
							DoNotHeat.class
							
							},
						(Class<? extends EventI>[]) new Class<?>[]{},
						simulatedTimeUnit,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		//machine cafe
		atomicModelDescriptors.put(
				MachineCafeStateModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						MachineCafeStateModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,
							SwitchOffMachineCafe.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnMachineCafe.class,
								SwitchOffMachineCafe.class},
						simulatedTimeUnit,
						MachineCafe.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				MachineCafeUserModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						MachineCafeUserModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,
							SwitchOffMachineCafe.class},
						simulatedTimeUnit,
						MachineCafeUser.REFLECTION_INBOUND_PORT_URI));

		//ventilateur
		atomicModelDescriptors.put(
				VentilateurStateModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						VentilateurStateModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnVentilateur.class,
							SwitchOffVentilateur.class,
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							SetMediumVentilateur.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnVentilateur.class,
								SwitchOffVentilateur.class,
								SetLowVentilateur.class,
								SetHighVentilateur.class,
								SetMediumVentilateur.class},
						simulatedTimeUnit,
						Ventilateur.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				VentilateurUserModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						VentilateurUserModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnVentilateur.class,
							SwitchOffVentilateur.class,
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							SetMediumVentilateur.class},
						simulatedTimeUnit,
						VentilateurUser.REFLECTION_INBOUND_PORT_URI));
		//ChauffeEau 
		
		atomicModelDescriptors.put(
				ChauffeEauCoupledModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						ChauffeEauCoupledModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerChauffeEau.class,
							SwitchOnChauffeEau.class,
							SwitchOffChauffeEau.class,
							Heat.class,
							DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerChauffeEau.class,
								SwitchOnChauffeEau.class,
								SwitchOffChauffeEau.class,
								Heat.class,
								DoNotHeat.class},
						simulatedTimeUnit,
						ChauffeEau.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				ChauffeEauUnitTesterModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						ChauffeEauUnitTesterModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnChauffeEau.class,
							SwitchOffChauffeEau.class,
							SetPowerChauffeEau.class,
							Heat.class,
							DoNotHeat.class},
						simulatedTimeUnit,
						ChauffeEauUser.REFLECTION_INBOUND_PORT_URI));
		



		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(HairDryerStateModel.MIL_URI);
		submodels.add(HairDryerUserModel.MIL_URI);
		submodels.add(HeaterCoupledModel.MIL_URI);
		submodels.add(HeaterUnitTesterModel.MIL_URI);
		submodels.add(ElectricMeterCoupledModel.MIL_URI);
		//machine cafe
		submodels.add(MachineCafeStateModel.MIL_URI);
		submodels.add(MachineCafeUserModel.MIL_URI);
		//ventilateur
		submodels.add(VentilateurStateModel.MIL_URI);
		submodels.add(VentilateurUserModel.MIL_URI);
		
		//ChauffeEau
		submodels.add(ChauffeEauCoupledModel.MIL_URI);
		submodels.add(ChauffeEauUnitTesterModel.MIL_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>();
				connections.put(
						new EventSource(HairDryerUserModel.MIL_URI,
								SwitchOnHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_URI,
										SwitchOnHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerUserModel.MIL_URI,
								SwitchOffHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_URI,
										SwitchOffHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerUserModel.MIL_URI,
								SetLowHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_URI,
										SetLowHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerUserModel.MIL_URI,
								SetHighHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_URI,
										SetHighHairDryer.class)
						});

				connections.put(
						new EventSource(HairDryerStateModel.MIL_URI,
								SwitchOnHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOnHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.MIL_URI,
								SwitchOffHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOffHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.MIL_URI,
								SetLowHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetLowHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.MIL_URI,
								SetHighHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetHighHairDryer.class)
						});

				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_URI,
								SetPowerHeater.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										SetPowerHeater.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_URI,
								SwitchOnHeater.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										SwitchOnHeater.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_URI,
								SwitchOffHeater.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										SwitchOffHeater.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_URI,
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										projet_alasca.etape3.equipments.heater.mil.events.Heat.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_URI,
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class)
						});

				connections.put(
						new EventSource(HeaterCoupledModel.MIL_URI,
								SetPowerHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetPowerHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_URI,
								SwitchOnHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOnHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_URI,
								SwitchOffHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOffHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_URI,
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										projet_alasca.etape3.equipments.heater.mil.events.Heat.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_URI,
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class)
						}); 

				//machine cafe
				connections.put(
						new EventSource(MachineCafeUserModel.MIL_URI,
								SwitchOnMachineCafe.class),
						new EventSink[] {
								new EventSink(MachineCafeStateModel.MIL_URI,
										SwitchOnMachineCafe.class)
						});
				connections.put(
						new EventSource(MachineCafeUserModel.MIL_URI,
								SwitchOffMachineCafe.class),
						new EventSink[] {
								new EventSink(MachineCafeStateModel.MIL_URI,
										SwitchOffMachineCafe.class)
						});

				connections.put(
						new EventSource(MachineCafeStateModel.MIL_URI,
								SwitchOnMachineCafe.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOnMachineCafe.class)
						});
				connections.put(
						new EventSource(MachineCafeStateModel.MIL_URI,
								SwitchOffMachineCafe.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOffMachineCafe.class)
						});
				
				//ventilateur
				connections.put(
						new EventSource(VentilateurUserModel.MIL_URI,
								SwitchOnVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_URI,
										SwitchOnVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_URI,
								SwitchOffVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_URI,
										SwitchOffVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_URI,
								SetLowVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_URI,
										SetLowVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_URI,
								SetHighVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_URI,
										SetHighVentilateur.class)
						});
				
				connections.put(
						new EventSource(VentilateurUserModel.MIL_URI,
								SetMediumVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_URI,
										SetMediumVentilateur.class)
						});

				connections.put(
						new EventSource(VentilateurStateModel.MIL_URI,
								SwitchOnVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOnVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.MIL_URI,
								SwitchOffVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOffVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.MIL_URI,
								SetLowVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetLowVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.MIL_URI,
								SetHighVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetHighVentilateur.class)
						});
				
				connections.put(
						new EventSource(VentilateurStateModel.MIL_URI,
								SetMediumVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetMediumVentilateur.class)
						});
				
				//ChauffeEau 
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_URI,
								SetPowerChauffeEau.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_URI,
										SetPowerChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_URI,
								SwitchOnChauffeEau.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_URI,
										SwitchOnChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_URI,
								SwitchOffChauffeEau.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_URI,
										SwitchOffChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_URI,
								Heat.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_URI,
										DoNotHeat.class)
						});

				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_URI,
								SetPowerChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SetPowerChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_URI,
								SwitchOnChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOnChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_URI,
								SwitchOffChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOffChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_URI,
								Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										DoNotHeat.class)
						});



				// coupled model descriptor
				coupledModelDescriptors.put(
						GlobalCoupledModel.MIL_URI,
						ComponentCoupledModelDescriptor.create(
								GlobalCoupledModel.class,
								GlobalCoupledModel.MIL_URI,
								submodels,
								null,
								null,
								connections,
								null,
								CoordinatorComponent.REFLECTION_INBOUND_PORT_URI,
								CoordinatorPlugin.class,
								null));

				ComponentModelArchitecture architecture =
						new ComponentModelArchitecture(
								GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI,
								GlobalCoupledModel.MIL_URI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								simulatedTimeUnit);

				return architecture;
	}

	/**
	 * create the global MIL real time component simulation architecture for the
	 * HEM application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI of the component model architecture to be created.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor for this run.
	 * @return						the global MIL real time simulation  architecture for the HEM application.
	 * @throws Exception			<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture
	createMILRTComponentSimulationArchitectures(
			String architectureURI, 
			TimeUnit simulatedTimeUnit,
			double accelerationFactor
			) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		atomicModelDescriptors.put(
				HairDryerStateModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						HairDryerStateModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,	// notice that the
							SwitchOffHairDryer.class,	// imported events of
							SetLowHairDryer.class,		// the atomic model
							SetHighHairDryer.class},	// appear here
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnHairDryer.class,	// notice that the
								SwitchOffHairDryer.class,	// exported events of
								SetLowHairDryer.class,		// the atomic model
								SetHighHairDryer.class},	// appear here
						simulatedTimeUnit,
						HairDryer.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				HairDryerUserModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						HairDryerUserModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,	// notice that the
							SwitchOffHairDryer.class,	// exported events of
							SetLowHairDryer.class,		// the atomic model
							SetHighHairDryer.class},	// appear here
						simulatedTimeUnit,
						HairDryerUser.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				HeaterCoupledModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						HeaterCoupledModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerHeater.class,		// notice that the
							SwitchOnHeater.class,		// imported events of
							SwitchOffHeater.class,		// the coupled model
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,					// appear here
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerHeater.class,		// notice that the
								SwitchOnHeater.class,		// reexported events of
								SwitchOffHeater.class,		// the coupled model
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class,					// appear here
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						simulatedTimeUnit,
						Heater.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				HeaterUnitTesterModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						HeaterUnitTesterModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHeater.class,
							SwitchOffHeater.class,
							SetPowerHeater.class,
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						simulatedTimeUnit,
						HeaterUser.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,
							SwitchOffHairDryer.class,
							SetLowHairDryer.class,
							SetHighHairDryer.class,
							SwitchOnHeater.class,
							SwitchOffHeater.class,
							SetPowerHeater.class,
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class,
							
							SwitchOnMachineCafe.class,
							SwitchOffMachineCafe.class,

							
							SwitchOnVentilateur.class,
							SwitchOffVentilateur.class,
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							
							SetPowerChauffeEau.class,
							SwitchOnChauffeEau.class,
							SwitchOffChauffeEau.class,
							Heat.class,
							DoNotHeat.class
							
							
							},
						(Class<? extends EventI>[]) new Class<?>[]{},
						simulatedTimeUnit,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		//machine cafe
		atomicModelDescriptors.put(
				MachineCafeStateModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						MachineCafeStateModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,	// notice that the
							SwitchOffMachineCafe.class	// imported events of
						},	// appear here
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,	// notice that the
							SwitchOffMachineCafe.class	// exported events of
						},	// appear here
						simulatedTimeUnit,
						MachineCafe.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				MachineCafeUserModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						MachineCafeUserModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,	// notice that the
							SwitchOffMachineCafe.class	// exported events of
						},	// appear here
						simulatedTimeUnit,
						MachineCafeUser.REFLECTION_INBOUND_PORT_URI));

		
		//ventilateur
		atomicModelDescriptors.put(
				VentilateurStateModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						VentilateurStateModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnVentilateur.class,	// notice that the
							SwitchOffVentilateur.class,	// imported events of
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							SetMediumVentilateur.class
						},	// appear here
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnVentilateur.class,	// notice that the
							SwitchOffVentilateur.class,	// imported events of
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							SetMediumVentilateur.class
						},	// appear here
						simulatedTimeUnit,
						Ventilateur.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				VentilateurUserModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						VentilateurUserModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnVentilateur.class,	// notice that the
							SwitchOffVentilateur.class,	// imported events of
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							SetMediumVentilateur.class
						},	// appear here
						simulatedTimeUnit,
						VentilateurUser.REFLECTION_INBOUND_PORT_URI));
		
		//ChauffeEau
		
		atomicModelDescriptors.put(
				ChauffeEauCoupledModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						ChauffeEauCoupledModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerChauffeEau.class,		// notice that the
							SwitchOnChauffeEau.class,		// imported events of
							SwitchOffChauffeEau.class,		// the coupled model
							Heat.class,					// appear here
							DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerChauffeEau.class,		// notice that the
								SwitchOnChauffeEau.class,		// reexported events of
								SwitchOffChauffeEau.class,		// the coupled model
								Heat.class,					// appear here
								DoNotHeat.class},
						simulatedTimeUnit,
						ChauffeEau.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				ChauffeEauUnitTesterModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						ChauffeEauUnitTesterModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnChauffeEau.class,
							SwitchOffChauffeEau.class,
							SetPowerChauffeEau.class,
							Heat.class,
							DoNotHeat.class},
						simulatedTimeUnit,
						ChauffeEauUser.REFLECTION_INBOUND_PORT_URI));


		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(HairDryerStateModel.MIL_RT_URI);
		submodels.add(HairDryerUserModel.MIL_RT_URI);
		submodels.add(HeaterCoupledModel.MIL_RT_URI);
		submodels.add(HeaterUnitTesterModel.MIL_RT_URI);
		submodels.add(ElectricMeterCoupledModel.MIL_RT_URI);
		//		machine cafe
		submodels.add(MachineCafeStateModel.MIL_RT_URI);
		submodels.add(MachineCafeUserModel.MIL_RT_URI);
		//ventilateur
		submodels.add(VentilateurStateModel.MIL_RT_URI);
		submodels.add(VentilateurUserModel.MIL_RT_URI);
		//ChauffeEau
		submodels.add(ChauffeEauCoupledModel.MIL_RT_URI);
		submodels.add(ChauffeEauUnitTesterModel.MIL_RT_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>();
				connections.put(
						new EventSource(HairDryerUserModel.MIL_RT_URI,
								SwitchOnHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_RT_URI,
										SwitchOnHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerUserModel.MIL_RT_URI,
								SwitchOffHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_RT_URI,
										SwitchOffHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerUserModel.MIL_RT_URI,
								SetLowHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_RT_URI,
										SetLowHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerUserModel.MIL_RT_URI,
								SetHighHairDryer.class),
						new EventSink[] {
								new EventSink(HairDryerStateModel.MIL_RT_URI,
										SetHighHairDryer.class)
						});

				connections.put(
						new EventSource(HairDryerStateModel.MIL_RT_URI,
								SwitchOnHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOnHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.MIL_RT_URI,
								SwitchOffHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOffHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.MIL_RT_URI,
								SetLowHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetLowHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.MIL_RT_URI,
								SetHighHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetHighHairDryer.class)
						});

				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								SetPowerHeater.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										SetPowerHeater.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								SwitchOnHeater.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										SwitchOnHeater.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								SwitchOffHeater.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										SwitchOffHeater.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										projet_alasca.etape3.equipments.heater.mil.events.Heat.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class)
						});

				connections.put(
						new EventSource(HeaterCoupledModel.MIL_RT_URI,
								SetPowerHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetPowerHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_RT_URI,
								SwitchOnHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOnHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_RT_URI,
								SwitchOffHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOffHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_RT_URI,
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										projet_alasca.etape3.equipments.heater.mil.events.Heat.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_RT_URI,
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class)
						});

				//machine cafe
				connections.put(
						new EventSource(MachineCafeUserModel.MIL_RT_URI,
								SwitchOnMachineCafe.class),
						new EventSink[] {
								new EventSink(MachineCafeStateModel.MIL_RT_URI,
										SwitchOnMachineCafe.class)
						});
				connections.put(
						new EventSource(MachineCafeUserModel.MIL_RT_URI,
								SwitchOffMachineCafe.class),
						new EventSink[] {
								new EventSink(MachineCafeStateModel.MIL_RT_URI,
										SwitchOffMachineCafe.class)
						});

				connections.put(
						new EventSource(MachineCafeStateModel.MIL_RT_URI,
								SwitchOnMachineCafe.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOnMachineCafe.class)
						});
				connections.put(
						new EventSource(MachineCafeStateModel.MIL_RT_URI,
								SwitchOffMachineCafe.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOffMachineCafe.class)
						});
				
				//ventilateur
				connections.put(
						new EventSource(VentilateurUserModel.MIL_RT_URI,
								SwitchOnVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_RT_URI,
										SwitchOnVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_RT_URI,
								SwitchOffVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_RT_URI,
										SwitchOffVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_RT_URI,
								SetLowVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_RT_URI,
										SetLowVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_RT_URI,
								SetHighVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_RT_URI,
										SetHighVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurUserModel.MIL_RT_URI,
								SetMediumVentilateur.class),
						new EventSink[] {
								new EventSink(VentilateurStateModel.MIL_RT_URI,
										SetMediumVentilateur.class)
						});

				connections.put(
						new EventSource(VentilateurStateModel.MIL_RT_URI,
								SwitchOnVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOnVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.MIL_RT_URI,
								SwitchOffVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOffVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.MIL_RT_URI,
								SetLowVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetLowVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.MIL_RT_URI,
								SetHighVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetHighVentilateur.class)
						});
				
				connections.put(
						new EventSource(VentilateurStateModel.MIL_RT_URI,
								SetMediumVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetMediumVentilateur.class)
						});
				
				//ChauffeEau 
				
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_RT_URI,
								SetPowerChauffeEau.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_RT_URI,
										SetPowerChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_RT_URI,
								SwitchOnChauffeEau.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_RT_URI,
										SwitchOnChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_RT_URI,
								SwitchOffChauffeEau.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_RT_URI,
										SwitchOffChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_RT_URI,
								Heat.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_RT_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(ChauffeEauUnitTesterModel.MIL_RT_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ChauffeEauCoupledModel.MIL_RT_URI,
										DoNotHeat.class)
						});

				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_RT_URI,
								SetPowerChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SetPowerChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_RT_URI,
								SwitchOnChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOnChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_RT_URI,
								SwitchOffChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOffChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_RT_URI,
								Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.MIL_RT_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										DoNotHeat.class)
						});



				// coupled model descriptor
				coupledModelDescriptors.put(
						GlobalCoupledModel.MIL_RT_URI,
						RTComponentCoupledModelDescriptor.create(
								GlobalCoupledModel.class,
								GlobalCoupledModel.MIL_RT_URI,
								submodels,
								null,
								null,
								connections,
								null,
								CoordinatorComponent.REFLECTION_INBOUND_PORT_URI,
								CoordinatorPlugin.class,
								null,
								accelerationFactor));

				ComponentModelArchitecture architecture =
						new ComponentModelArchitecture(
								GlobalSupervisor.MIL_RT_SIM_ARCHITECTURE_URI,
								GlobalCoupledModel.MIL_RT_URI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								TimeUnit.HOURS);

				return architecture;
	}

	/**
	 * create the global SIL real time component simulation architecture for the
	 * HEM application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI of the component model architecture to be created.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor for this run.
	 * @return						the global MIL real time simulation  architecture for the HEM application.
	 * @throws Exception			<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture
	createSILComponentSimulationArchitectures(
			String architectureURI, 
			TimeUnit simulatedTimeUnit,
			double accelerationFactor
			) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// Currently, the HEM application has only two appliances: a hair dryer
		// and a heater.
		atomicModelDescriptors.put(
				HairDryerStateModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						HairDryerStateModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,	// notice that the
							SwitchOffHairDryer.class,	// imported events of
							SetLowHairDryer.class,		// the atomic model
							SetHighHairDryer.class},	// appear here
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnHairDryer.class,	// notice that the
								SwitchOffHairDryer.class,	// exported events of
								SetLowHairDryer.class,		// the atomic model
								SetHighHairDryer.class},	// appear here
						simulatedTimeUnit,
						HairDryer.REFLECTION_INBOUND_PORT_URI
						));

		atomicModelDescriptors.put(
				HeaterCoupledModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						HeaterCoupledModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerHeater.class,		// notice that the
							SwitchOnHeater.class,		// imported events of
							SwitchOffHeater.class,		// the coupled model
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,					// appear here
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerHeater.class,		// notice that the
								SwitchOnHeater.class,		// reexported events of
								SwitchOffHeater.class,		// the coupled model
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class,					// appear here
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class},
						simulatedTimeUnit,
						Heater.REFLECTION_INBOUND_PORT_URI));

		// The electric meter also has a SIL simulation model
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnHairDryer.class,	// notice that the
							SwitchOffHairDryer.class,	// imported events of
							SetLowHairDryer.class,		// the coupled model
							SetHighHairDryer.class,		// appear here
							SetPowerHeater.class,
							SwitchOnHeater.class,
							SwitchOffHeater.class,
							projet_alasca.etape3.equipments.heater.mil.events.Heat.class,
							projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class ,
							
							SwitchOnMachineCafe.class,
							SwitchOffMachineCafe.class,
							
							SwitchOnVentilateur.class,
							SwitchOffVentilateur.class,
							SetLowVentilateur.class,
							SetHighVentilateur.class,
							
							SetPowerChauffeEau.class,
							SwitchOnChauffeEau.class,
							SwitchOffChauffeEau.class,
							Heat.class,
							DoNotHeat.class
							},
						(Class<? extends EventI>[]) new Class<?>[]{},
						simulatedTimeUnit,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		//machine cafe
		atomicModelDescriptors.put(
				MachineCafeStateModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						MachineCafeStateModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,	// notice that the
							SwitchOffMachineCafe.class	// imported events of
						},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnMachineCafe.class,	// notice that the
							SwitchOffMachineCafe.class	// exported events of
						},	// appear here
						simulatedTimeUnit,
						MachineCafe.REFLECTION_INBOUND_PORT_URI
						));
		
		//ventilateur
				atomicModelDescriptors.put(
						VentilateurStateModel.SIL_URI,
						RTComponentAtomicModelDescriptor.create(
								VentilateurStateModel.SIL_URI,
								(Class<? extends EventI>[]) new Class<?>[]{
									SwitchOnMachineCafe.class,	// notice that the
									SwitchOffMachineCafe.class,	// imported events of
									SetLowVentilateur.class,
									SetHighVentilateur.class,
									SetMediumVentilateur.class
								},
								(Class<? extends EventI>[]) new Class<?>[]{
									SwitchOnMachineCafe.class,	// notice that the
									SwitchOffMachineCafe.class,	// imported events of
									SetLowVentilateur.class,
									SetHighVentilateur.class,
									SetMediumVentilateur.class
								},	// appear here
								simulatedTimeUnit,
								Ventilateur.REFLECTION_INBOUND_PORT_URI
								));
		
		//ChauffeEau
				atomicModelDescriptors.put(
						ChauffeEauCoupledModel.SIL_URI,
						RTComponentAtomicModelDescriptor.create(
								ChauffeEauCoupledModel.SIL_URI,
								(Class<? extends EventI>[]) new Class<?>[]{
									SetPowerChauffeEau.class,		// notice that the
									SwitchOnChauffeEau.class,		// imported events of
									SwitchOffChauffeEau.class,		// the coupled model
									Heat.class,					// appear here
									DoNotHeat.class
								},
								(Class<? extends EventI>[]) new Class<?>[]{
										SetPowerChauffeEau.class,		// notice that the
										SwitchOnChauffeEau.class,		// reexported events of
										SwitchOffChauffeEau.class,		// the coupled model
										Heat.class,					// appear here
										DoNotHeat.class
								},
								simulatedTimeUnit,
								ChauffeEau.REFLECTION_INBOUND_PORT_URI
								));



		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(HairDryerStateModel.SIL_URI);
		submodels.add(HeaterCoupledModel.SIL_URI);
		submodels.add(ElectricMeterCoupledModel.SIL_URI);
		//machine cafe
		submodels.add(MachineCafeStateModel.SIL_URI);
		//ventilateur
		submodels.add(VentilateurStateModel.SIL_URI);
		//ChauffeEau
		submodels.add(ChauffeEauCoupledModel.SIL_URI);
		

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>();

				// first, the events going from the hair dryer to the electric meter
				connections.put(
						new EventSource(HairDryerStateModel.SIL_URI,
								SwitchOnHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOnHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.SIL_URI,
								SwitchOffHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOffHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.SIL_URI,
								SetLowHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetLowHairDryer.class)
						});
				connections.put(
						new EventSource(HairDryerStateModel.SIL_URI,
								SetHighHairDryer.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetHighHairDryer.class)
						});

				// second, the events going from the heater to the electric meter
				connections.put(
						new EventSource(HeaterCoupledModel.SIL_URI,
								SetPowerHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetPowerHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.SIL_URI,
								SwitchOnHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOnHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.SIL_URI,
								SwitchOffHeater.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOffHeater.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.SIL_URI,
								projet_alasca.etape3.equipments.heater.mil.events.Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										projet_alasca.etape3.equipments.heater.mil.events.Heat.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.SIL_URI,
								projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										projet_alasca.etape3.equipments.heater.mil.events.DoNotHeat.class)
						});

				//machine cafe.
				connections.put(
						new EventSource(MachineCafeStateModel.SIL_URI,
								SwitchOnMachineCafe.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOnMachineCafe.class)
						});
				connections.put(
						new EventSource(MachineCafeStateModel.SIL_URI,
								SwitchOffMachineCafe.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOffMachineCafe.class)
						});
				
				//ventilateur
				connections.put(
						new EventSource(VentilateurStateModel.SIL_URI,
								SwitchOnVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOnVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.SIL_URI,
								SwitchOffVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOffVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.SIL_URI,
								SetLowVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetLowVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.SIL_URI,
								SetHighVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetHighVentilateur.class)
						});
				connections.put(
						new EventSource(VentilateurStateModel.SIL_URI,
								SetMediumVentilateur.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetMediumVentilateur.class)
						});
				
				//ChauffeEau
				
				connections.put(
						new EventSource(ChauffeEauCoupledModel.SIL_URI,
								SetPowerChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SetPowerChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.SIL_URI,
								SwitchOnChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOnChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.SIL_URI,
								SwitchOffChauffeEau.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOffChauffeEau.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.SIL_URI,
								Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(ChauffeEauCoupledModel.SIL_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										DoNotHeat.class)
						});


				// coupled model descriptor
				coupledModelDescriptors.put(
						GlobalCoupledModel.SIL_URI,
						RTComponentCoupledModelDescriptor.create(
								GlobalCoupledModel.class,
								GlobalCoupledModel.SIL_URI,
								submodels,
								null,
								null,
								connections,
								null,
								CoordinatorComponent.REFLECTION_INBOUND_PORT_URI,
								CoordinatorPlugin.class,
								null,
								accelerationFactor));

				ComponentModelArchitecture architecture =
						new ComponentModelArchitecture(
								GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI,
								GlobalCoupledModel.SIL_URI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								simulatedTimeUnit);

				return architecture;
	}
}
// -----------------------------------------------------------------------------
