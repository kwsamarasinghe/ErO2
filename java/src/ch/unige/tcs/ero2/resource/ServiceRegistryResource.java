package ch.unige.tcs.ero2.resource;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.ws.spi.ServiceDelegate;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;
import ch.ethz.inf.vs.californium.util.DatagramReader;
import ch.ethz.inf.vs.californium.util.DatagramWriter;
import ch.unige.tcs.ero2.registry.*;
import ch.unige.tcs.ero2.server.ErO2Server;

/**
 * ErO2 auxiliary resource for sensor nodes to register 
 * 
 * @author Kasun Samarasinghe
 */
public class ServiceRegistryResource extends LocalResource {

	private ErO2Registry ERO2REGISTRY;
	
	public ServiceRegistryResource(String custom, String title, String rt) {
		super(custom);
		setTitle(title);
		setResourceType(rt);
	}
	
	public ServiceRegistryResource() {
		this("register", "Register Service", "RegisterServiceDisplayer");
	}

	public void performPOST(POSTRequest request) {
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		//Should do something
		ErO2Service service=parseMessage(request.getPayloadString());
		//Should check the IP too
		System.out.println(request.sequenceKey());
		
		response.setPayload("Register process on the way");
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
		request.respond(response);
	}
	
	public void setRegistry(ErO2Registry registry){
		ERO2REGISTRY = registry;
	}
	
	private ErO2Service parseMessage(String payloadString){
		//System.out.println(payloadString);
		String serviceLocator=payloadString.substring(0,payloadString.indexOf(ErO2Service.DELIMITER));
		String serviceDescriptor=payloadString.substring(payloadString.indexOf(ErO2Service.DELIMITER)+1,payloadString.lastIndexOf(ErO2Service.DELIMITER));
		//serviceDescriptor = serviceDescriptor.substring(0,serviceDescriptor.indexOf(ErO2Service.DELIMITER)); //hack
		StringTokenizer tokenizer=new StringTokenizer(serviceDescriptor, ErO2Service.COMMA);
		
		Vector<String> serviceAttributes=new Vector<String>();
		while(tokenizer.hasMoreTokens()){
			serviceAttributes.add(tokenizer.nextToken());
		}
		
		String name=serviceAttributes.elementAt(0);
		String uri=serviceAttributes.elementAt(1);
		String method=serviceAttributes.elementAt(2);
		String queryString=serviceAttributes.elementAt(3);
		
		ErO2Service service=null; 
		if(name!=ErO2Service.NULL && uri!=ErO2Service.NULL && method!=ErO2Service.NULL){
			service = new ErO2Service();
			service.setName(name);
			service.setName(uri);
			service.setName(method);
			if(queryString!=ErO2Service.NULL){
				service.setName(queryString);
			}
		}
				
		return service;
	}
}
