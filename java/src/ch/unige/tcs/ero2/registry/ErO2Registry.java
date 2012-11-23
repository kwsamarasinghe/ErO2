package ch.unige.tcs.ero2.registry;

import java.util.Hashtable;

public class ErO2Registry {

	private Hashtable<String, ErO2Service> registry;
	
	public ErO2Registry(){
		registry = new Hashtable<String, ErO2Service>();
	}
	
	public void registerService(ErO2Service service, String servicePointer){
		registry.put(servicePointer, service);
		System.out.println("ErO2: Service registerd: "+service.getSerialization());
	}
	
	public ErO2Service searchService(String servicePointer){
		return registry.get(servicePointer);
	}
	
}


