package ero2.resource.rest;

import java.util.StringTokenizer;

import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ero2.core.ErO2Registry;
import ero2.core.ErO2Service;
import ero2.core.ErO2ServiceStatus;
import ero2.transport.coap.ErO2Utils;

public class ServiceMonitorResource extends LocalResource {

	private ErO2Registry ERO2REGISTRY;

	public ServiceMonitorResource(String custom, String title, String rt) {
		super(custom);
		setTitle(title);
		setResourceType(rt);
		ERO2REGISTRY = ErO2Registry.getInstance();
	}

	public ServiceMonitorResource() {
		this("monitor", "Monitoring Service", "MonitorServiceDisplayer");
	}

	public void performPOST(POSTRequest request) {
		Response response = new Response(CodeRegistry.RESP_CONTENT);

		String sqkey = request.sequenceKey();
		String ipaddr = request.sequenceKey().substring(0,
				sqkey.lastIndexOf(':'));
		String payloadString = request.getPayloadString();
		String serviceLocator = payloadString.substring(0,
				payloadString.indexOf(ErO2Service.DELIMITER));

		// Parse sensor readings hack for now :)
		String sensorReadings = payloadString.substring(
				payloadString.indexOf(ErO2Service.DELIMITER) + 1,
				payloadString.lastIndexOf(ErO2Service.DELIMITER));
		System.out.println(sensorReadings);

		System.out.println("Hearbeat receving from " + serviceLocator
				+ " with light" + " temp");

		// Prepare service status
		ErO2ServiceStatus status = new ErO2ServiceStatus();
		status.setIPAddr(ipaddr);

		int i = 1;
		String lum = "", temp = "";

		// updates the service monitor registry
		StringTokenizer tokenizer = new StringTokenizer(sensorReadings,	ErO2Service.DELIMITER);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextElement().toString();
			switch (i) {
			case 1:
				status.setReading("luminocity", token);
				System.out.println("lum"+token);
				lum = token;
				break;
			case 2:
				status.setReading("temp", token);
				temp = token;
				System.out.println("temp"+token);
				break;
			}
			i++;
		}

		ERO2REGISTRY.updateStatus(serviceLocator, status);
		System.out.println("Ero2 service updated " + serviceLocator + " "
				+ ipaddr + " " + lum + " " + temp);

		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
		request.respond(response);
	}
}
