package geocon.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Class Configuration. It's used as a layer between the config.properties file and the 
 * framework
 */
public class Configuration {

	/** The Constant PRIMARY_INDEX_NAME. */
	public static final String PRIMARY_INDEX_NAME;
	
	/** The Constant CREDENTIALS_TYPE_NAME. */
	public static final String CREDENTIALS_TYPE_NAME;
	
	/** The Constant USERS_TYPE_NAME. */
	public static final String USERS_TYPE_NAME;
	
	/** The Constant EVENTS_TYPE_NAME. */
	public static final String EVENTS_TYPE_NAME;
	
	/** The Constant RESOURCES_TYPE_NAME. */
	public static final String RESOURCES_TYPE_NAME;
	
	/** The Constant PLACES_TYPE_NAME. */
	public static final String PLACES_TYPE_NAME;
	
	/** The Constant ADDRESS. */
	public static final String ADDRESS;
	
	/** The Constant PORT. */
	public static final String PORT;
	
	/** The Constant SERVICE_NAME. */
	public static final String SERVICE_NAME;
	
	/** The Constant CLUSTER_NAME. */
	public static final String CLUSTER_NAME;
	
	/** The Constant CLUSTER_PORT. */
	public static final String CLUSTER_PORT;
	
	/** The Constant CLUSTER_ADDRESS. */
	public static final String CLUSTER_ADDRESS;

	// Private constructor to prevent initialization.
	/**
	 * Instantiates a new configuration.
	 */
	private Configuration() {
	}

	// reading from config.properites
	static {
		// dumb init
		Properties prop = null;
		try {
			FileInputStream input = new FileInputStream("config.properties");
			prop = new Properties();
			prop.load(input);
		} catch (IOException e) {
			System.out
					.println("Problem encountered while reading the .properties file. Is the path correct?");
			e.printStackTrace();
		}
		CLUSTER_PORT = prop.getProperty("cluster_port");
		CLUSTER_ADDRESS = prop.getProperty("cluster_address");
		CLUSTER_NAME = prop.getProperty("cluster_name");
		PRIMARY_INDEX_NAME = prop.getProperty("primary_index_name");
		CREDENTIALS_TYPE_NAME = prop.getProperty("credentials_type_name");
		USERS_TYPE_NAME = prop.getProperty("users_type_name");
		RESOURCES_TYPE_NAME = prop.getProperty("resources_type_name");
		EVENTS_TYPE_NAME = prop.getProperty("events_type_name");
		PLACES_TYPE_NAME = prop.getProperty("places_type_name");
		ADDRESS = prop.getProperty("address");
		PORT = prop.getProperty("port");
		SERVICE_NAME = prop.getProperty("service_name");
	}
}
