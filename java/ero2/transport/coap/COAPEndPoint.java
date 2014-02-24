package ero2.transport.coap;

import java.net.SocketException;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;
import ero2.resource.rest.ServiceMonitorResource;
import ero2.resource.rest.ServiceRegistryResource;

public class COAPEndPoint extends ServerEndpoint {

	public COAPEndPoint() throws SocketException {
		// Initializes ErO2 services
		ServiceRegistryResource serviceRegistry = new ServiceRegistryResource();
		addResource(serviceRegistry);

		// Add monitor service
		ServiceMonitorResource monitorRegistry = new ServiceMonitorResource();
		addResource(monitorRegistry);
	}

	public void handleRequest(Request request) {
		request.prettyPrint();
		super.handleRequest(request);
	}

}
