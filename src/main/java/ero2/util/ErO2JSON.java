package ero2.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.core.ErO2ServiceStatus;

public class ErO2JSON {

	@SuppressWarnings("unchecked")
	public static String getServicesJSONString(
			Hashtable<String, ErO2Service> serviceRegistry) {

		Enumeration<String> serviceKeys = serviceRegistry.keys();

		JSONObject finalJSON = new JSONObject();
		JSONArray servicesJSON = new JSONArray();
		while (serviceKeys.hasMoreElements()) {
			String serviceLocator = serviceKeys.nextElement();
			System.out.println(serviceLocator);
			ErO2Service service = serviceRegistry.get(serviceLocator);
			// []
			Vector<ErO2Resource> resources = service.getResources();
			JSONArray resourcesJSON = new JSONArray();

			JSONObject resourceJSON;
			for (ErO2Resource ero2Resource : resources) {
				if (ero2Resource.getName() != null
						&& ero2Resource.getMethod() != null) {
					resourceJSON = new JSONObject();
					resourceJSON.put("name", ero2Resource.getName());
					resourceJSON.put("method", ero2Resource.getMethod());
					resourceJSON.put("uri", ero2Resource.getURI());
					resourceJSON.put("params",
							ero2Resource.getQueryParameters());
					resourcesJSON.add(resourceJSON);
				}
			}

			// {service
			JSONObject serviceJSON = new JSONObject();
			serviceJSON.put("serviceID", serviceLocator);
			serviceJSON.put("resources", resourcesJSON);

			servicesJSON.add(serviceJSON);
		}
		finalJSON.put("services", servicesJSON);

		StringWriter out = new StringWriter();
		try {
			finalJSON.writeJSONString(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jsonText = out.toString();
		System.out.println(jsonText);
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	public static String getUpdatesJSONString(String serviceLocator,
			ErO2ServiceStatus status) {
		JSONObject readings = new JSONObject();
		readings.put("service", serviceLocator);
		Enumeration<String> readingsKeys = status.getReadings().keys();
		while (readingsKeys.hasMoreElements()) {
			String key = readingsKeys.nextElement();
			readings.put(key, status.getReadings().get(key));
		}

		StringWriter out = new StringWriter();
		try {
			readings.writeJSONString(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jsonText = out.toString();
		return jsonText;
	}
}
