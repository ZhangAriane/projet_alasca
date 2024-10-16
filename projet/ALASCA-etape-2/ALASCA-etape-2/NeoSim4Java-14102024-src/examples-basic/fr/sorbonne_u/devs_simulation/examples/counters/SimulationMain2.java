package fr.sorbonne_u.devs_simulation.examples.counters;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulationMain2</code> illustrates the basic counter example
 * with two atomic models under one coupled model simulated in logical time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-04-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulationMain2
{
	public static void	main(String[] args)
	{
		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			String model1uri = BasicCounterModel.MODEL_URI_PREFIX + "-" + 1;
			String model2uri = BasicCounterModel.MODEL_URI_PREFIX + "-" + 2;
			atomicModelDescriptors.put(
				model1uri,
				AtomicModelDescriptor.create(
						null,
						model1uri,
						TimeUnit.MILLISECONDS,
						// To illustrate the use of model factories
						new BasicCounterModelFactory()));
			atomicModelDescriptors.put(
				model2uri,
				AtomicModelDescriptor.create(
						BasicCounterModel.class,
						model2uri,
						TimeUnit.MILLISECONDS,
						// To illustrate that when the creation protocol is
						// standard, the knowledge of the model class is
						// enough to effectively create the model instance
						null));

			Set<String> submodelURIs = new HashSet<String>();
			submodelURIs.add(model1uri);
			submodelURIs.add(model2uri);

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

			coupledModelDescriptors.put(
				BasicCounterCoupledModel.BASIC_COUPLED_MODEL_URI,
				new CoupledModelDescriptor(
						BasicCounterCoupledModel.class,
						BasicCounterCoupledModel.BASIC_COUPLED_MODEL_URI,
						submodelURIs,
						new HashMap<Class<? extends EventI>,EventSink[]>(),
						new HashMap<Class<? extends EventI>,ReexportedEvent>(),
						new HashMap<EventSource,EventSink[]>(),
						// as the standard protocol is used to create coupled
						// model instances, there is no need for a factory
						null));
						// but alternatively the following can be used instead
						// new BasicCoupledModelFactory()));
			ArchitectureI architecture =
				new Architecture(
						BasicCounterCoupledModel.BASIC_COUPLED_MODEL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.MILLISECONDS
						);

			SimulatorI se = architecture.constructSimulator();

			Map<String,Object> simParams = new HashMap<>();
			simParams.put(
				ModelI.createRunParameterName(
								model1uri,
								BasicCounterModel.START_VALUE_PARAM_NAME),
				0);
			simParams.put(
				ModelI.createRunParameterName(
								model1uri,
								BasicCounterModel.STEP_VALUE_PARAM_NAME),
				750.0);
			simParams.put(
				ModelI.createRunParameterName(
								model2uri,
								BasicCounterModel.START_VALUE_PARAM_NAME),
				100);
			simParams.put(
				ModelI.createRunParameterName(
								model2uri,
								BasicCounterModel.STEP_VALUE_PARAM_NAME),
				1000.0);
			se.setSimulationRunParameters(simParams);

			se.doStandAloneSimulation(0.0, 10100.0);

			System.out.println(se.getFinalReport());
			System.out.println("Simulation ends.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
