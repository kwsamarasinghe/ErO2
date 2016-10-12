package ero2.resource.rest;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.identification.ErO2UserProfile;
import ero2.transport.coap.COAPClient;

public class ErO2RestMediatorResource {

	ErO2Registry ERO2REGISTRY;
	Connection conn;

	public ErO2RestMediatorResource() {
		ERO2REGISTRY = ErO2Registry.getInstance();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/Syndesi?user=root&password=");

		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			System.out.println("class not found");
		}
	}

	@SuppressWarnings("unused")
	@Get()
	public String callService() {
		String serviceLocator = null;
		String resourceName = null;
		String profileID = null;
		ErO2Service service = null;
		String parameterString = null;

		Set<String> parameters = getQuery().getNames();
		for (String parameter : parameters) {
			if (parameter.equals("service")) {
				serviceLocator = getQuery().getValues(parameter);
				service = ERO2REGISTRY.searchService(serviceLocator);
			}

			if (parameter.equals("resource")) {
				resourceName = getQuery().getValues(parameter);
			}

			if (parameter.equals("profileid")) {
				profileID = getQuery().getValues(parameter);
			}
		}

		if (service != null) {
			String qString = getQuery().getQueryString();
			parameterString = qString.substring(qString.indexOf("&") + 1);

			COAPClient client = new COAPClient();
			String uri = getURI(service, resourceName, parameterString);
			String responsePayload = client.doRequest(uri, service
					.getResourceByName(resourceName).getMethod());
			System.out.println(responsePayload);
			return "{response:" + "responsePayload" + "}";
		} else if (profileID != null) {
			activateProfile(profileID);
			return "{response:profile activated}";
		}
		return "ERROR";
		// return serializeUpdate(serviceLocator);
	}

	private String getURI(ErO2Service service, String resourceName,
			String paramterString) {
		System.out.println(resourceName);
		ErO2Resource resource = service.getResourceByName(resourceName);
		String uri = "coap://" + service.getIPAddress() + ":"
				+ COAPClient.COAPPORT + "/" + resource.getName() + "?"
				+ paramterString;
		return uri;
	}

	private void activateProfile(String profileID) {
		// Connect with the db and get sensor_group_id and profile things
		ErO2UserProfile userProfile = getProfile(profileID);
		String group = userProfile.getSensorGroupID();

		// currently activate basic. i.e all
		Vector<ErO2Service> services = ERO2REGISTRY.searchServiceGroup(group);
		activateBasic(services, group);

	}

	private String activateBasic(Vector<ErO2Service> services, String group) {
		if (!services.isEmpty()) {
			// unlock the door
			String localRoot = group.substring(0, 2);
			System.out.println(localRoot);
			ErO2Service rootService = ERO2REGISTRY.searchService(localRoot);
			if (rootService != null) {
				COAPClient root = new COAPClient();
				String rootURI = getURI(rootService, rootService
						.getResourceByName("lock").getName(), "status=1");
				root.doRequest(rootURI, rootService.getResourceByName("lock")
						.getMethod());

				while (!services.isEmpty()) {
					ErO2Service service = services.remove(0);
					ErO2Resource resource = service.getResourceByName("light");
					System.out.println("calling resource: "
							+ resource.getName());
					COAPClient client = new COAPClient();

					String uri = getURI(service, resource.getName(), "status=1");

					String responsePayload = client.doRequest(uri,
							resource.getMethod());
					System.out.println(responsePayload);
				}
				return "good";
			} else {
				return "Unable to unlock the door";
			}
		} else {
			return "Error activating profile";
		}

	}

	private void activateAdvanced(Vector<ErO2Service> services) {

	}

	private ErO2UserProfile getProfile(String profileID) {
		try {
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT sensor_group_id,light_level,temp_level,window_state FROM profile WHERE NFC_id="
							+ profileID);

			if (resultSet.next()) {
				ErO2UserProfile profile = new ErO2UserProfile();
				String sgid = resultSet.getString("sensor_group_id");
				profile.setSensorGroupID(sgid);

				String ll = resultSet.getString("light_level");
				profile.setLight(ll);

				String tl = resultSet.getString("temp_level");
				profile.setTemp(tl);

				String ws = resultSet.getString("window_state");
				profile.setWindow(ws);

				return profile;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
