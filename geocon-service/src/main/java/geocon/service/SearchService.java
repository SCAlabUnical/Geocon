package geocon.service;

import geocon.common.Paths;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

/**
 * The Class SearchService. It's used for interfacing the framework with the Elasticsearch
 * search engine.
 */
@Path(Paths.SEARCH)
public class SearchService {

	/** The Constant NO_TYPE. */
	private static final String NO_TYPE = "no_type";
	
	/** The Constant DEFAULT_SIZE. It indicates how many documents the
	 * query result has return*/
	private static final String DEFAULT_SIZE = "10";
	
	/** The Elasticsearch client instance. */
	private Client esClient = ElasticSearch.getClient();

	/** The Constant DEBUG. Put this variable @ true for enabling echo strings */
	private static final boolean DEBUG = false;

	/**
	 * The search method. It's used for forwarding queries to Elasticsearch cluster.
	 *
	 * @param type - If you don't want to search in the whole index, you may specify
	 * which type of document you're interested in.
	 * @param query - the query specified in Elasticsearch DSL
	 * @param size - the number of documents that the query should return (maximum)
	 * @return the result of the query as JSON document
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String search(
			@DefaultValue(NO_TYPE) @QueryParam("type") String type,
			@QueryParam("q") String query,
			@DefaultValue(DEFAULT_SIZE) @QueryParam("size") String size) {

		if (DEBUG) {
			System.out.println(type);
			System.out.println(query);
			System.out.println(size);
		}

		if (type.equals(Configuration.USERS_TYPE_NAME))
			return searchInUsers(query, size).toString();
		if (type.equals(Configuration.RESOURCES_TYPE_NAME))
			return searchInResources(query, size).toString();
		if (type.equals(Configuration.PLACES_TYPE_NAME))
			return searchInPlaces(query, size).toString();
		if (type.equals(Configuration.EVENTS_TYPE_NAME))
			return searchInEvents(query, size).toString();
		if (type.equals(NO_TYPE))
			return searchInAllTypes(query, size).toString();

		throw new WebApplicationException(404);

	}

	/**
	 * Search in all types.
	 *
	 * @param query the query
	 * @param size the size
	 * @return the search response
	 */
	private SearchResponse searchInAllTypes(String query, String size) {

		return esClient.prepareSearch(Configuration.PRIMARY_INDEX_NAME)
				.setQuery(query).setSize(Integer.parseInt(size)).execute()
				.actionGet();
	}

	/**
	 * Search in users.
	 *
	 * @param query the query
	 * @param size the size
	 * @return the search response
	 */
	private SearchResponse searchInUsers(String query, String size) {
		return esClient.prepareSearch(Configuration.PRIMARY_INDEX_NAME)
				.setTypes(Configuration.USERS_TYPE_NAME).setQuery(query)
				.setSize(Integer.parseInt(size)).execute().actionGet();
	}

	/**
	 * Search in events.
	 *
	 * @param query the query
	 * @param size the size
	 * @return the search response
	 */
	private SearchResponse searchInEvents(String query, String size) {
		return esClient.prepareSearch(Configuration.PRIMARY_INDEX_NAME)
				.setTypes(Configuration.EVENTS_TYPE_NAME).setQuery(query)
				.setSize(Integer.parseInt(size)).execute().actionGet();
	}

	/**
	 * Search in resources.
	 *
	 * @param query the query
	 * @param size the size
	 * @return the search response
	 */
	private SearchResponse searchInResources(String query, String size) {
		return esClient.prepareSearch(Configuration.PRIMARY_INDEX_NAME)
				.setTypes(Configuration.RESOURCES_TYPE_NAME).setQuery(query)
				.setSize(Integer.parseInt(size)).execute().actionGet();
	}

	/**
	 * Search in places.
	 *
	 * @param query the query
	 * @param size the size
	 * @return the search response
	 */
	private SearchResponse searchInPlaces(String query, String size) {
		return esClient.prepareSearch(Configuration.PRIMARY_INDEX_NAME)
				.setTypes(Configuration.PLACES_TYPE_NAME).setQuery(query)
				.setSize(Integer.parseInt(size)).execute().actionGet();
	}

}
