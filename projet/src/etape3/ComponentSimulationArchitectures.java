package etape3;

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
import etape3.equipments.hairdryer.HairDryer;
import etape3.equipments.hairdryer.HairDryerUser;
import etape3.equipments.hairdryer.mil.HairDryerStateModel;
import etape3.equipments.hairdryer.mil.HairDryerUserModel;
import etape3.equipments.hairdryer.mil.events.SetHighHairDryer;
import etape3.equipments.hairdryer.mil.events.SetLowHairDryer;
import etape3.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import etape3.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import etape3.equipments.heater.Heater;
import etape3.equipments.heater.HeaterUser;
import etape3.equipments.heater.mil.HeaterCoupledModel;
import etape3.equipments.heater.mil.HeaterUnitTesterModel;
import etape3.equipments.heater.mil.events.DoNotHeat;
import etape3.equipments.heater.mil.events.Heat;
import etape3.equipments.heater.mil.events.SetPowerHeater;
import etape3.equipments.heater.mil.events.SwitchOffHeater;
import etape3.equipments.heater.mil.events.SwitchOnHeater;
import etape3.equipments.meter.ElectricMeter;
import etape3.equipments.meter.sim.ElectricMeterCoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import projet_alasca.equipements.machineCafe.MachineCafe;
import projet_alasca.equipements.machineCafe.MachineCafeUser;
import projet_alasca.equipements.machineCafe.mil.MachineCafeStateModel;
import projet_alasca.equipements.machineCafe.mil.MachineCafeUserModel;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOffMachineCafe;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOnMachineCafe;

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
							Heat.class,
							DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerHeater.class,
								SwitchOnHeater.class,
								SwitchOffHeater.class,
								Heat.class,
								DoNotHeat.class},
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
							Heat.class,
							DoNotHeat.class},
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
							Heat.class,
							DoNotHeat.class},
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
								Heat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_URI,
										DoNotHeat.class)
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
								Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										DoNotHeat.class)
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
							Heat.class,					// appear here
							DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerHeater.class,		// notice that the
								SwitchOnHeater.class,		// reexported events of
								SwitchOffHeater.class,		// the coupled model
								Heat.class,					// appear here
								DoNotHeat.class},
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
							Heat.class,
							DoNotHeat.class},
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
							Heat.class,
							DoNotHeat.class},
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
		//machine cafe
		//		submodels.add(MachineCafeStateModel.MIL_RT_URI);
		//		submodels.add(MachineCafeUserModel.MIL_RT_URI);

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
								Heat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(HeaterCoupledModel.MIL_RT_URI,
										DoNotHeat.class)
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
								Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.MIL_RT_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										DoNotHeat.class)
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
							Heat.class,					// appear here
							DoNotHeat.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SetPowerHeater.class,		// notice that the
								SwitchOnHeater.class,		// reexported events of
								SwitchOffHeater.class,		// the coupled model
								Heat.class,					// appear here
								DoNotHeat.class},
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
							Heat.class,
							DoNotHeat.class},
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
								Heat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										Heat.class)
						});
				connections.put(
						new EventSource(HeaterCoupledModel.SIL_URI,
								DoNotHeat.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										DoNotHeat.class)
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
