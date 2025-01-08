package etape3.equipments.meter.sim;

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
import etape3.equipments.hairdryer.mil.HairDryerElectricityModel;
import etape3.equipments.hairdryer.mil.events.SetHighHairDryer;
import etape3.equipments.hairdryer.mil.events.SetLowHairDryer;
import etape3.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import etape3.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import etape3.equipments.heater.mil.HeaterElectricityModel;
import etape3.equipments.heater.mil.events.DoNotHeat;
import etape3.equipments.heater.mil.events.Heat;
import etape3.equipments.heater.mil.events.SetPowerHeater;
import etape3.equipments.heater.mil.events.SwitchOffHeater;
import etape3.equipments.heater.mil.events.SwitchOnHeater;
import etape3.utils.SimulationType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.HIOA_Composer;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import projet_alasca.equipements.machineCafe.mil.MachineCafeElectricityModel;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOffMachineCafe;
import projet_alasca.equipements.machineCafe.mil.events.SwitchOnMachineCafe;

// -----------------------------------------------------------------------------
/**
 * The class <code>MILSimulationArchitectures</code>  defines the local MIL
 * simulation architecture pertaining to the electric meter component.
 *
 * <p><strong>Description</strong></p>
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
 * <p>Created on : 2023-11-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	LocalSimulationArchitectures
{
	/**
	 * create the local MIL simulation architecture for the {@code ElectricMeter}
	 * component.
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
	 * @return					the local MIL simulation architecture for the {@code ElectricMeter} component.
	 * @throws Exception		<i>to do</i>.
	 */
	public static Architecture	createElectricMeterMILArchitecture4IntegrationTests(
			String architectureURI, 
			TimeUnit simulatedTimeUnit
			) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// the electric meter electricity model accumulates the electric
		// power consumption, an atomic HIOA model hence we use an
		// RTAtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				ElectricMeterElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ElectricMeterElectricityModel.class,
						ElectricMeterElectricityModel.MIL_URI,
						simulatedTimeUnit,
						null));
		// The electricity models of all appliances will need to be put within
		// the ElectricMeter simulator to be able to share the variables
		// containing their power consumptions.
		atomicModelDescriptors.put(
				HairDryerElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						HairDryerElectricityModel.class,
						HairDryerElectricityModel.MIL_URI,
						simulatedTimeUnit,
						null));
		atomicModelDescriptors.put(
				HeaterElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						HeaterElectricityModel.class,
						HeaterElectricityModel.MIL_URI,
						simulatedTimeUnit,
						null));

		//machine cafe
		atomicModelDescriptors.put(
				MachineCafeElectricityModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						MachineCafeElectricityModel.class,
						MachineCafeElectricityModel.MIL_URI,
						simulatedTimeUnit,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ElectricMeterElectricityModel.MIL_URI);
		submodels.add(HairDryerElectricityModel.MIL_URI);
		submodels.add(HeaterElectricityModel.MIL_URI);
		//machine cafe
		submodels.add(MachineCafeElectricityModel.MIL_URI);

		// events imported from the HairDryer component model architecture
		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		imported.put(
				SwitchOnHairDryer.class,
				new EventSink[] {
						new EventSink(HairDryerElectricityModel.MIL_URI,
								SwitchOnHairDryer.class)
				});
		imported.put(
				SwitchOffHairDryer.class,
				new EventSink[] {
						new EventSink(HairDryerElectricityModel.MIL_URI,
								SwitchOffHairDryer.class)
				});
		imported.put(
				SetLowHairDryer.class,
				new EventSink[] {
						new EventSink(HairDryerElectricityModel.MIL_URI,
								SetLowHairDryer.class)
				});
		imported.put(
				SetHighHairDryer.class,
				new EventSink[] {
						new EventSink(HairDryerElectricityModel.MIL_URI,
								SetHighHairDryer.class)
				});

		imported.put(
				SetPowerHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
								SetPowerHeater.class)
				});
		imported.put(
				SwitchOnHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
								SwitchOnHeater.class)
				});
		imported.put(
				SwitchOffHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
								SwitchOffHeater.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
								Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.MIL_URI,
								DoNotHeat.class)
				});

		//machine cafe
		imported.put(
				SwitchOnMachineCafe.class,
				new EventSink[] {
						new EventSink(MachineCafeElectricityModel.MIL_URI,
								SwitchOnMachineCafe.class)
				});
		imported.put(
				SwitchOffMachineCafe.class,
				new EventSink[] {
						new EventSink(MachineCafeElectricityModel.MIL_URI,
								SwitchOffMachineCafe.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
				new HashMap<VariableSource,VariableSink[]>();
				bindings.put(
						new VariableSource("currentIntensity",
								Double.class,
								HairDryerElectricityModel.MIL_URI),
						new VariableSink[] {
								new VariableSink("currentHairDryerIntensity",
										Double.class,
										ElectricMeterElectricityModel.MIL_URI)
						});
				bindings.put(
						new VariableSource("currentIntensity",
								Double.class,
								HeaterElectricityModel.MIL_URI),
						new VariableSink[] {
								new VariableSink("currentHeaterIntensity",
										Double.class,
										ElectricMeterElectricityModel.MIL_URI)
						});

				//machine cafe
				bindings.put(
						new VariableSource("currentIntensity",
								Double.class,
								MachineCafeElectricityModel.MIL_URI),
						new VariableSink[] {
								new VariableSink("currentMachineCafeIntensity",
										Double.class,
										ElectricMeterElectricityModel.MIL_URI)
						});

				coupledModelDescriptors.put(
						ElectricMeterCoupledModel.MIL_URI,
						new CoupledHIOA_Descriptor(
								ElectricMeterCoupledModel.class,
								ElectricMeterCoupledModel.MIL_URI,
								submodels,
								imported,
								null,
								null,
								null,
								null,
								null,
								bindings,
								new HIOA_Composer()));

				Architecture architecture =
						new Architecture(
								architectureURI,
								ElectricMeterCoupledModel.MIL_URI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								simulatedTimeUnit);

				return architecture;
	}

	/**
	 * create the local MIL real time simulation architecture for the
	 * {@code ElectricMeter} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentSimulationType	simulation type for the next run.
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor used to execute in a logical time speeding up the real time.
	 * @return						the local MIL real time simulation architecture for the {@code ElectricMeter} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createElectricMeter_RT_Architecture4IntegrationTests(
			SimulationType currentSimulationType,
			String architectureURI, 
			TimeUnit simulatedTimeUnit,
			double accelerationFactor
			) throws Exception
	{
		String electricMeterElectricityModelURI = null;
		Class<? extends AtomicHIOA> electricMeterElectricityModelClass = null;
		String hairDryerElectricityModelURI = null;
		String heaterElectricityModelURI = null;
		String electricMeterCoupledModelURI = null;
		//machine cafe
		String machineCafeElectricityModelURI = null;
		switch (currentSimulationType) {
		case MIL_RT_SIMULATION:
			electricMeterElectricityModelURI = ElectricMeterElectricityModel.MIL_RT_URI;
			electricMeterElectricityModelClass = ElectricMeterElectricityModel.class;
			hairDryerElectricityModelURI = HairDryerElectricityModel.MIL_RT_URI;
			heaterElectricityModelURI = HeaterElectricityModel.MIL_RT_URI;
			electricMeterCoupledModelURI = ElectricMeterCoupledModel.MIL_RT_URI;
			//machine cafe
			machineCafeElectricityModelURI = MachineCafeElectricityModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			electricMeterElectricityModelURI = ElectricMeterElectricitySILModel.SIL_URI;
			electricMeterElectricityModelClass = ElectricMeterElectricitySILModel.class;
			hairDryerElectricityModelURI = HairDryerElectricityModel.SIL_URI;
			heaterElectricityModelURI = HeaterElectricityModel.SIL_URI;
			electricMeterCoupledModelURI = ElectricMeterCoupledModel.SIL_URI;
			//machine cafe
			machineCafeElectricityModelURI = MachineCafeElectricityModel.SIL_URI;
			break;
		default:
		}

		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// the electric meter electricity model accumulates the electric
		// power consumption, an atomic HIOA model hence we use an
		// RTAtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				electricMeterElectricityModelURI,
				RTAtomicHIOA_Descriptor.create(
						electricMeterElectricityModelClass,
						electricMeterElectricityModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		// The electricity models of all appliances will need to be put within
		// the ElectricMeter simulator to be able to share the variables
		// containing their power consumptions.
		atomicModelDescriptors.put(
				hairDryerElectricityModelURI,
				RTAtomicHIOA_Descriptor.create(
						HairDryerElectricityModel.class,
						hairDryerElectricityModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				heaterElectricityModelURI,
				RTAtomicHIOA_Descriptor.create(
						HeaterElectricityModel.class,
						heaterElectricityModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		
		//machine cafe
		atomicModelDescriptors.put(
				machineCafeElectricityModelURI,
				RTAtomicHIOA_Descriptor.create(
						MachineCafeElectricityModel.class,
						machineCafeElectricityModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(electricMeterElectricityModelURI);
		submodels.add(hairDryerElectricityModelURI);
		submodels.add(heaterElectricityModelURI);
		//machine cafe
		submodels.add(machineCafeElectricityModelURI);

		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		imported.put(
				SwitchOnHairDryer.class,
				new EventSink[] {
						new EventSink(hairDryerElectricityModelURI,
								SwitchOnHairDryer.class)
				});
		imported.put(
				SwitchOffHairDryer.class,
				new EventSink[] {
						new EventSink(hairDryerElectricityModelURI,
								SwitchOffHairDryer.class)
				});
		imported.put(
				SetLowHairDryer.class,
				new EventSink[] {
						new EventSink(hairDryerElectricityModelURI,
								SetLowHairDryer.class)
				});
		imported.put(
				SetHighHairDryer.class,
				new EventSink[] {
						new EventSink(hairDryerElectricityModelURI,
								SetHighHairDryer.class)
				});

		imported.put(
				SetPowerHeater.class,
				new EventSink[] {
						new EventSink(heaterElectricityModelURI,
								SetPowerHeater.class)
				});
		imported.put(
				SwitchOnHeater.class,
				new EventSink[] {
						new EventSink(heaterElectricityModelURI,
								SwitchOnHeater.class)
				});
		imported.put(
				SwitchOffHeater.class,
				new EventSink[] {
						new EventSink(heaterElectricityModelURI,
								SwitchOffHeater.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
						new EventSink(heaterElectricityModelURI,
								Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
						new EventSink(heaterElectricityModelURI,
								DoNotHeat.class)
				});
		
		//machine cafe
		imported.put(
				SwitchOnMachineCafe.class,
				new EventSink[] {
						new EventSink(machineCafeElectricityModelURI,
								SwitchOnMachineCafe.class)
				});
		imported.put(
				SwitchOffMachineCafe.class,
				new EventSink[] {
						new EventSink(machineCafeElectricityModelURI,
								SwitchOffMachineCafe.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
				new HashMap<VariableSource,VariableSink[]>();
				bindings.put(
						new VariableSource("currentIntensity",
								Double.class,
								hairDryerElectricityModelURI),
						new VariableSink[] {
								new VariableSink("currentHairDryerIntensity",
										Double.class,
										electricMeterElectricityModelURI)
						});
				bindings.put(
						new VariableSource("currentIntensity",
								Double.class,
								heaterElectricityModelURI),
						new VariableSink[] {
								new VariableSink("currentHeaterIntensity",
										Double.class,
										electricMeterElectricityModelURI)
						});
				
				//machine cafe
				bindings.put(
						new VariableSource("currentIntensity",
								Double.class,
								machineCafeElectricityModelURI),
						new VariableSink[] {
								new VariableSink("currentMachineCafeIntensity",
										Double.class,
										electricMeterElectricityModelURI)
						});

				coupledModelDescriptors.put(
						electricMeterCoupledModelURI,
						new RTCoupledHIOA_Descriptor(
								ElectricMeterCoupledModel.class,
								electricMeterCoupledModelURI,
								submodels,
								imported,
								null,
								null,
								null,
								null,
								null,
								bindings,
								new HIOA_Composer(),
								accelerationFactor));

				Architecture architecture =
						new RTArchitecture(
								architectureURI,
								electricMeterCoupledModelURI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								simulatedTimeUnit,
								accelerationFactor);

				return architecture;
	}
}
// -----------------------------------------------------------------------------
