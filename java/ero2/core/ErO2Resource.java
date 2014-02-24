package ero2.core;

public class ErO2Resource {

	private String name;
	private String uri;
	private String requestMethod;
	private String queryString;
	
	public ErO2Resource(String name,String uri,String requestMethod, String queryString){
		this.name = name;
		this.uri = uri;
		this.requestMethod = requestMethod;
		if(queryString!=ErO2Service.NULL){
			this.queryString = queryString;
		}
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setURI(String uri){
		this.uri = uri;
	}
	
	public void setMethod(String method){
		if("COAP_GET".equals(method)){
			method = "GET";
		}
		
		if("COAP_POST".equals(method)){
			method="POST";
		}
		this.requestMethod = method;
	}
	
	public void setQueryParameters(String queryParameters){
		this.queryString = queryParameters;
	}

	public String getName(){
		return name;
	}
	
	public String getURI(){
		return uri;
	}
	
	public String getMethod(){
		return requestMethod;
	}
	
	public String getQueryParameters(){
		return queryString;
	}
}
