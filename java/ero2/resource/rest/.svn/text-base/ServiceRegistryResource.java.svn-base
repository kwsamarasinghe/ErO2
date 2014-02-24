package ero2.resource.rest;

import java.util.StringTokenizer;

import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ero2.core.*;
import ero2.transport.coap.ErO2Utils;

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
		ERO2REGISTRY = ErO2Registry.getInstance();
	}
	
	public ServiceRegistryResource() {
		this("register", "Register Service", "RegisterServiceDisplayer");
	}

	public void performPOST(POSTRequest request) {
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		
		//Should check the IP too
		String sqkey=request.sequenceKey();
		String ipaddr=request.sequenceKey().substring(0, sqkey.lastIndexOf(':'));
		if(parseMessage(ipaddr,request.getPayloadString())){
			response.setPayload(ErO2Utils.REGISTRATION_SUCCESSFUL);
		}else{
			response.setPayload(ErO2Utils.REGISTRATION_FAILED);
		}
		
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
		request.respond(response);
	}
	
	private boolean parseMessage(String ipaddr,String payloadString){

		String serviceLocator=payloadString.substring(0,payloadString.indexOf(ErO2Service.DELIMITER));
		String serviceDescriptor=payloadString.substring(payloadString.indexOf(ErO2Service.DELIMITER)+1,payloadString.lastIndexOf(ErO2Service.DELIMITER));
		StringTokenizer serviceTokenizer=new StringTokenizer(serviceDescriptor, ErO2Service.DELIMITER);
		
		ErO2Service service;
		ErO2Resource resource=null;
		while(serviceTokenizer.hasMoreTokens()){
			service=new ErO2Service(ipaddr);
			String name=null,uri=null,method=null,queryString=null;
			
			String attributeDescriptor = serviceTokenizer.nextToken();
			StringTokenizer attributeTokenizer=new StringTokenizer(attributeDescriptor, ErO2Service.COMMA);
			
			int attributeno=0;
			String attribute=null;
			while(attributeTokenizer.hasMoreTokens()){
				attribute=attributeTokenizer.nextToken();
				switch(attributeno){
					case 0:
						name=attribute;
						resource=new ErO2Resource(name, uri, method, queryString);
						break;
					case 1:
						uri=attribute;
						resource.setURI(uri);
						break;
					case 2:
						method=attribute;
						resource.setMethod(method);
						break;
					case 3:
						queryString=attribute;
						resource.setQueryParameters(queryString);
						break;
				}
				attributeno++;
			}

			if(resource!=null){
				service.addResource(resource);
				System.out.println("Registering service "+service.getSerialization());
				ERO2REGISTRY.registerService(serviceLocator, service);
			}
		}	
		
		if(resource!=null){
			return true;
		}else{
			return false;
		}
	}
}
