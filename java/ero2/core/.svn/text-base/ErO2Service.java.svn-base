package ero2.core;

import java.util.Vector;

public class ErO2Service {
	String ipaddr;
	Vector<ErO2Resource> resources = new Vector<ErO2Resource>();

	public static String DELIMITER = "|";
	public static String COMMA = ",";
	public static String NULL = "(null)";

	public ErO2Service() {
	}

	public ErO2Service(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public void addResource(ErO2Resource resource) {
		resources.add(resource);
	}

	public void removeResource(ErO2Resource resource) {
		resources.removeElement(resource);
	}

	public Vector<ErO2Resource> getResources() {
		return resources;
	}

	public ErO2Resource getResourceByName(String resourceName) {
		if (resourceName != null) {
			for (ErO2Resource resource : resources) {
				if (resourceName.equals(resource.getName())) {
					return resource;
				}
			}
		}
		return null;
	}

	public String getIPAddress() {
		return ipaddr;
	}

	public String getSerialization() {
		String serviceString = ipaddr;
		for (ErO2Resource resource : resources) {
			serviceString = serviceString + DELIMITER + resource.getName()
					+ DELIMITER + resource.getURI() + DELIMITER
					+ resource.getMethod() + DELIMITER + resource.getQueryParameters()
					+ DELIMITER;
		}
		return serviceString;
	}
}
