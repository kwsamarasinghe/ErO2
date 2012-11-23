package ch.unige.tcs.ero2.server;

import java.net.SocketException;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.util.Log;
import ch.unige.tcs.ero2.registry.ErO2Registry;
import ch.unige.tcs.ero2.resource.*;

public class ErO2Server extends LocalEndpoint {

	public static final int ERR_INIT_FAILED = 1;
	
	private ErO2Registry ERO2REGISTRY;
	
	public ErO2Server() throws SocketException {
		
		//Initializes the registry
		ERO2REGISTRY=new ErO2Registry();
		
		//Initializes ErO2 services
		addResource(new ServiceRegistryResource());
	}

	public void handleRequest(Request request) {
		request.prettyPrint();
		super.handleRequest(request);
	}

	
	public static void main(String[] args) {
		Log.init();
		try {
			Endpoint server = new ErO2Server();
			System.out.printf("ErO2Server started and listening on port %d.\n", server.port());
		} catch (SocketException e) {
			System.err.printf("ErO2 server failed to start: %s\n", e.getMessage());
			System.exit(ERR_INIT_FAILED);
		}
	}

}
