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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.exceptions.InvariantChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>ChauffeEauCoupledModel</code> defines a simple coupled
 * model used to assemble the models defined for the ChauffeEau in order to
 * execute unit tests on the ChauffeEau simulator.
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
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ChauffeEauCoupledModel
extends		CoupledModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a MIL model; works when only one instance is created.		*/
	public static final String	MIL_URI = ChauffeEauCoupledModel.class.
			getSimpleName() + "-MIL";
	/** URI for a MIL real time model; works when only one instance is
	 *  created.															*/
	public static final String	MIL_RT_URI = ChauffeEauCoupledModel.class.
			getSimpleName() + "-MIL-RT";
	/** URI for a SIL model; works when only one instance is created.		*/
	public static final String	SIL_URI = ChauffeEauCoupledModel.class.
			getSimpleName() + "-SIL";


	protected static boolean	glassBoxInvariants(
			ChauffeEauCoupledModel instance
			)
	{
		assert	instance != null :
			new NeoSim4JavaException("Precondition violation: "
					+ "instance != null");

		boolean ret = true;
		return ret;
	}

	protected static boolean	blackBoxInvariants(
			ChauffeEauCoupledModel instance
			)
	{
		// TODO Auto-generated method stub
		assert	instance != null :
			new NeoSim4JavaException("Precondition violation: "
					+ "instance != null");

		boolean ret = true;
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MIL_URI != null && !MIL_URI.isEmpty(),
				ChauffeEauCoupledModel.class,
				instance,
				"MIL_URI != null && !MIL_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				MIL_RT_URI != null && !MIL_RT_URI.isEmpty(),
				ChauffeEauCoupledModel.class,
				instance,
				"MIL_RT_URI != null && !MIL_RT_URI.isEmpty()");
		ret &= InvariantChecking.checkBlackBoxInvariant(
				SIL_URI != null && !SIL_URI.isEmpty(),
				ChauffeEauCoupledModel.class,
				instance,
				"URI != null && !URI.isEmpty()");
		return ret;
	}
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating the coupled model with event exchanges only.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code submodels != null && submodels.length > 1}
	 * pre	{@code Stream.of(submodels).allMatch(s -> s!= null)}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ChauffeEauCoupledModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			CoordinatorI simulationEngine,
			ModelI[] submodels,
			Map<Class<? extends EventI>,EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported,
			Map<EventSource, EventSink[]> connections
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
				imported, reexported, connections);
		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException("White-box invariants violation!");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException("Black-box invariants violation!");
	}

	/**
	 * creating the coupled model with event and variable exchanges.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete...
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code submodels != null && submodels.length > 1}
	 * pre	{@code Stream.of(submodels).allMatch(s -> s!= null)}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ChauffeEauCoupledModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			CoordinatorI simulationEngine,
			ModelI[] submodels,
			Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported,
			Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars,
			Map<VariableSource, VariableSink[]> bindings
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
				imported, reexported, connections,
				importedVars, reexportedVars, bindings);
		assert	glassBoxInvariants(this) :
			new NeoSim4JavaException("White-box invariants violation!");
		assert	blackBoxInvariants(this) :
			new NeoSim4JavaException("Black-box invariants violation!");
	}
}
// -----------------------------------------------------------------------------
