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
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.utils.aclocks.AcceleratedAndSimulationClock;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationConnector;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationOutboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import projet_alasca.etape3.utils.SimulationType;

// -----------------------------------------------------------------------------
/**
 * The class <code>GlobalSupervisor</code> implements the supervisor component
 * for simulated runs of the HEM project.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In BCM-CyPhy-Components, simulated runs execute both the components and their
 * DEVS simulators. In this case, the supervisor component is responsible for
 * the creation, initialisation and execution of the global component simulation
 * architecture using models disseminated into the different application
 * components.
 * </p>
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
 * <p>Created on : 2023-11-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {ClocksServerWithSimulationCI.class})
public class			GlobalSupervisor
extends		AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int			X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** URI of the simulation architecture when a MIL simulation is
	 *  executed.															*/
	public static final String	MIL_SIM_ARCHITECTURE_URI = "hem-mil-simulator";
	/** URI of the simulation architecture when a MIL real time
	 *  simulation is executed.												*/
	public static final String	MIL_RT_SIM_ARCHITECTURE_URI =
														"hem-mil-rt-simulator";
	/** URI of the simulation architecture when a SIL simulation is
	 *  executed.															*/
	public static final String	SIL_SIM_ARCHITECTURE_URI = "hem-sil-simulator";

	// Execution/Simulation

	/** current type of simulation.											*/
	protected final SimulationType	currentSimulationType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String			simArchitectureURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentSimulationType			simulation type for the next run.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 */
	protected			GlobalSupervisor(
		SimulationType currentSimulationType,
		String simArchitectureURI
		)
	{
		// one thread for execute and one for report reception
		super(2, 0);
		
		assert	currentSimulationType != null :
				new PreconditionException("currentExecutionType != null");
		assert	currentSimulationType.isSimulated() || 
										(simArchitectureURI != null &&
												!simArchitectureURI.isEmpty()) :
				new PreconditionException(
					"currentExecutionType.isSimulated() ||  "
					+ "(simArchitectureURI != null && "
					+ "!simArchitectureURI.isEmpty())");

		this.currentSimulationType = currentSimulationType;
		this.simArchitectureURI = simArchitectureURI;

		this.tracer.get().setTitle("Global supervisor");
		this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
											  Y_RELATIVE_POSITION);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		// First, get the clock and wait until the start time that it specifies.
		AcceleratedAndSimulationClock ac = null;
		ClocksServerWithSimulationOutboundPort clocksServerOutboundPort =
							new ClocksServerWithSimulationOutboundPort(this);
		clocksServerOutboundPort.publishPort();
		this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerWithSimulationConnector.class.getCanonicalName());
		this.logMessage("HEM gets the clock.");
		ac = clocksServerOutboundPort.
						getClockWithSimulation(CVMIntegrationTest.CLOCK_URI);
		this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
		clocksServerOutboundPort.unpublishPort();
		clocksServerOutboundPort.destroyPort();
		this.logMessage("HEM waits until start time.");
		ac.waitUntilStart();
		this.logMessage("HEM starts.");

		switch (this.currentSimulationType) {
		case MIL_SIMULATION:
			ComponentModelArchitecture cma =
					ComponentSimulationArchitectures.
						createMILComponentSimulationArchitectures(
													this.simArchitectureURI,
													ac.getSimulatedTimeUnit());
			SupervisorPlugin sp = new SupervisorPlugin(cma);
			sp.setPluginURI(GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI);
			this.installPlugin(sp);
			this.logMessage("plug-in installed.");
			sp.constructSimulator();
			this.logMessage("simulator constructed, simulation begins.");
			sp.setSimulationRunParameters(new HashMap<>());
			sp.doStandAloneSimulation(0.0, ac.getSimulatedDuration());
			this.logMessage("simulation ends.");
			break;
		case MIL_RT_SIMULATION:
			cma = ComponentSimulationArchitectures.
							createMILRTComponentSimulationArchitectures(
													this.simArchitectureURI,
													ac.getSimulatedTimeUnit(),
													ac.getAccelerationFactor());
			sp = new SupervisorPlugin(cma);
			sp.setPluginURI(GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI);
			this.installPlugin(sp);
			this.logMessage("plug-in installed.");
			sp.constructSimulator();
			this.logMessage("simulator constructed, simulation begins.");
			sp.setSimulationRunParameters(new HashMap<>());
			sp.startRTSimulation(TimeUnit.NANOSECONDS.toMillis(
											ac.getSimulationStartEpochNanos()),
								 ac.getSimulatedStartTime(),
								 ac.getSimulatedDuration());
			// wait for the end of the simulation
			ac.waitUntilSimulationEnd();
			// leave some time for the simulators end of simulation catering
			// tasks
			Thread.sleep(250L);
			this.logMessage(sp.getFinalReport().toString());
			break;
		case SIL_SIMULATION:
			cma = ComponentSimulationArchitectures.
							createSILComponentSimulationArchitectures(
													this.simArchitectureURI,
													ac.getSimulatedTimeUnit(),
													ac.getAccelerationFactor());
			sp = new SupervisorPlugin(cma);
			sp.setPluginURI(GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI);
			this.installPlugin(sp);
			this.logMessage("plug-in installed.");
			sp.constructSimulator();
			this.logMessage("simulator constructed, simulation begins.");
			sp.setSimulationRunParameters(new HashMap<>());
			sp.startRTSimulation(TimeUnit.NANOSECONDS.toMillis(
											ac.getSimulationStartEpochNanos()),
								 ac.getSimulatedStartTime(),
								 ac.getSimulatedDuration());
			// wait for the end of the simulation
			ac.waitUntilSimulationEnd();
			// leave some time for the simulators end of simulation catering
			// tasks
			Thread.sleep(250L);
			this.logMessage(sp.getFinalReport().toString());
			break;
		default:
		}		
	}
}
// -----------------------------------------------------------------------------
