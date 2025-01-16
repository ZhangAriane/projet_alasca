package projet_alasca.equipements.chauffeEau.connections;

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

import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.hem2024e1.utils.Measure;
import projet_alasca.equipements.chauffeEau.ChauffeEauSensorDataCI;
import projet_alasca.equipements.chauffeEau.measures.ChauffeEauSensorData;

import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>ChauffeEauSensorDataConnector</code> implements the connector for
 * the {@code ChauffeEauSensorDataCI} component data interface, and as such must
 * implement the {@code ChauffeEauSensorDataCI.ChauffeEauSensorRequiredPullCI} pull
 * interface.
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
 * <p>Created on : 2023-11-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ChauffeEauSensorDataConnector
extends		DataConnector
implements	ChauffeEauSensorDataCI.ChauffeEauSensorRequiredPullCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.ChauffeEauSensorDataCI.equipments.ChauffeEau.ChauffeEauSensorDataCI.ChauffeEauSensorCI#heatingPullSensor()
	 */
	@Override
	public ChauffeEauSensorData<Measure<Boolean>>	heatingPullSensor()
	throws Exception
	{
		return ((ChauffeEauSensorDataCI.ChauffeEauSensorOfferedPullCI)this.offering).heatingPullSensor();
	}

	/**
	 * @see fr.sorbonne_u.components.ChauffeEauSensorDataCI.equipments.ChauffeEau.ChauffeEauSensorDataCI.ChauffeEauSensorCI#targetTemperaturePullSensor()
	 */
	@Override
	public ChauffeEauSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return ((ChauffeEauSensorDataCI.ChauffeEauSensorOfferedPullCI)this.offering).targetTemperaturePullSensor();
	}

	/**
	 * @see fr.sorbonne_u.components.ChauffeEauSensorDataCI.equipments.ChauffeEau.ChauffeEauSensorDataCI.ChauffeEauSensorCI#currentTemperaturePullSensor()
	 */
	@Override
	public ChauffeEauSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return ((ChauffeEauSensorDataCI.ChauffeEauSensorOfferedPullCI)this.offering).currentTemperaturePullSensor();
	}

	/**
	 * @see fr.sorbonne_u.components.ChauffeEauSensorDataCI.equipments.ChauffeEau.ChauffeEauSensorDataCI.ChauffeEauSensorCI#startTemperaturesPushSensor(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		((ChauffeEauSensorDataCI.ChauffeEauSensorOfferedPullCI)this.offering).
								startTemperaturesPushSensor(controlPeriod, tu);
	}
}
// -----------------------------------------------------------------------------
