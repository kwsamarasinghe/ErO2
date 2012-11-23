package ch.unige.tcs.ero2.registry;

public class ErO2Service{
	String ipaddr;
	String name;
	String uri;
	String requestMethod;
	String queryString;
	
	public static String DELIMITER = "|";
	public static String COMMA = ",";
	public static String NULL="(null)";

	public ErO2Service(){}
	
	public ErO2Service(String name,String uri,String requestMethod, String queryString,String ipaddr){
		this.name = name;
		this.uri = uri;
		this.requestMethod = requestMethod;
		this.queryString = queryString;
		this.ipaddr = ipaddr;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setURI(String uri){
		this.uri = uri;
	}
	
	public void setMethod(String method){
		this.requestMethod = method;
	}
	
	public void setIP(String ipaddr){
		this.ipaddr = ipaddr;
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
	
	public String getIP(){
		return ipaddr;
	}
	
	public String setQueryParameters(){
		return queryString;
	}
	
	public String getSerialization(){
		String serviceString;
		serviceString=DELIMITER+name+DELIMITER+uri+DELIMITER+requestMethod+DELIMITER+queryString+DELIMITER;
		return serviceString;
	}
}
