package geocon.service;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * The Class ElasticSearch. It provides the object who represent 
 * the elasticsearch cluster in the framework.
 */
public class ElasticSearch {

	/** The instance of the client. */
	private static Client instance;

	/** The settings object, needed for client configuration */
	private static Settings settings = ImmutableSettings.settingsBuilder()
			.put("cluster.name", Configuration.CLUSTER_NAME).build();

	/**
	 * Private constructor for avoiding forbidden instantiations
	 */
	private ElasticSearch() {
	}

	/**
	 * Gets the single instance of ElasticSearch.
	 *
	 * @return single instance of ElasticSearch
	 */
	@SuppressWarnings("resource")
	public static synchronized Client getInstance() {
		if (instance == null) {
			instance = new TransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							Configuration.CLUSTER_ADDRESS, Integer
									.parseInt(Configuration.CLUSTER_PORT)));
		}
		return instance;
	}

	/**
	 * Start.
	 */
	public static synchronized void start() {
		getInstance();
	}

	/**
	 * Stop.
	 */
	public static synchronized void stop() {
		instance.close();
	}

	/**
	 * Gets the client.
	 *
	 * @return the client
	 * @throws NoNodeAvailableException when no node are available
	 */
	public static Client getClient() throws NoNodeAvailableException {
		return getInstance();
	}
}
