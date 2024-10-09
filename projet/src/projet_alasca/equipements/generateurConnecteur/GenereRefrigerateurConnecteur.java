package projet_alasca.equipements.generateurConnecteur;

import java.util.HashMap;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import projet_alasca.equipements.refrigerateur.RefrigerateurExternalControlCI;
import projet_alasca.equipements.refrigerateur.RefrigerateurInternalControlCI;
import projet_alasca.equipements.refrigerateur.RefrigerateurUserCI;

public class GenereRefrigerateurConnecteur {
	
	public static Class<?> genereRefrigeratorUserConnector() throws Exception{
		HashMap<String, String> methodNamesMap = new HashMap<String, String>() ;
		methodNamesMap.put("getRefrigeratorTargetTemperature","getRefrigeratorTargetTemperature") ;
		methodNamesMap.put("getFreezerTargetTemperature","getFreezerTargetTemperature") ;
		methodNamesMap.put("getRefrigeratorCurrentTemperature","getRefrigeratorCurrentTemperature") ;
		methodNamesMap.put("getFreezerCurrentTemperature","getFreezerCurrentTemperature") ;
		methodNamesMap.put("on","on") ;
		methodNamesMap.put("switchOn","switchOn") ;
		methodNamesMap.put("switchOff","switchOff") ;
		methodNamesMap.put("setRefrigeratorTargetTemperature","setRefrigeratorTargetTemperature") ;
		
		methodNamesMap.put("setFreezerTargetTemperature","setFreezerTargetTemperature") ;
		methodNamesMap.put("getMaxPowerLevel","getMaxPowerLevel") ;
		methodNamesMap.put("setCurrentPowerLevel","setCurrentPowerLevel") ;
		methodNamesMap.put("getCurrentPowerLevel","getCurrentPowerLevel") ;
		
		
		
		Class<?> connectorClass =
				GenerateurConnecteur.makeConnectorClassJavassist(
						"projet_alasca.equipements.refrigerateur.connections.RefrigerateurUserConnector",
						AbstractConnector.class,
						RefrigerateurUserCI.class,
						RefrigerateurUserCI.class,
						methodNamesMap) ;
		return connectorClass;
	}
	
	public static Class<?> genereRefrigeratorInternalControlConnector() throws Exception{
		HashMap<String, String> methodNamesMap = new HashMap<String, String>() ;
		methodNamesMap.put("getRefrigeratorTargetTemperature","getRefrigeratorTargetTemperature") ;
		methodNamesMap.put("getFreezerTargetTemperature","getFreezerTargetTemperature") ;
		methodNamesMap.put("getRefrigeratorCurrentTemperature","getRefrigeratorCurrentTemperature") ;
		methodNamesMap.put("getFreezerCurrentTemperature","getFreezerCurrentTemperature") ;
		
		methodNamesMap.put("cooling","cooling") ;
		methodNamesMap.put("startCooling","startCooling") ;
		methodNamesMap.put("stopCooling","stopCooling") ;
		
		methodNamesMap.put("freezing","freezing") ;
		methodNamesMap.put("startFreezing","startFreezing") ;
		methodNamesMap.put("stopFreezing","stopFreezing") ;
		
		Class<?> connectorClass =
				GenerateurConnecteur.makeConnectorClassJavassist(
						"projet_alasca.equipements.refrigerateur.connections.RefrigerateurInternalControlConnector",
						AbstractConnector.class,
						RefrigerateurInternalControlCI.class,
						RefrigerateurInternalControlCI.class,
						methodNamesMap) ;
		return connectorClass;
	}
	
	public static Class<?> genereRefrigeratorExternalControlConnector() throws Exception{
		HashMap<String, String> methodNamesMap = new HashMap<String, String>() ;
		methodNamesMap.put("getRefrigeratorTargetTemperature","getRefrigeratorTargetTemperature") ;
		methodNamesMap.put("getFreezerTargetTemperature","getFreezerTargetTemperature") ;
		methodNamesMap.put("getRefrigeratorCurrentTemperature","getRefrigeratorCurrentTemperature") ;
		methodNamesMap.put("getFreezerCurrentTemperature","getFreezerCurrentTemperature") ;
		methodNamesMap.put("getMaxPowerLevel","getMaxPowerLevel") ;
		methodNamesMap.put("setCurrentPowerLevel","setCurrentPowerLevel") ;
		methodNamesMap.put("getCurrentPowerLevel","getCurrentPowerLevel") ;
		
		Class<?> connectorClass =
				GenerateurConnecteur.makeConnectorClassJavassist(
						"projet_alasca.equipements.refrigerateur.connections.RefrigerateurExternalControlConnector",
						AbstractConnector.class,
						RefrigerateurExternalControlCI.class,
						RefrigerateurExternalControlCI.class,
						methodNamesMap) ;
		return connectorClass;
	}
	
	

}
