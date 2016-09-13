package geocon.service;

import geocon.common.Messages;
import geocon.common.Paths;
import geocon.common.QParams;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import static geocon.service.Configuration.PRIMARY_INDEX_NAME;
import static geocon.service.Configuration.PLACES_TYPE_NAME;

/**
 * Root resource (exposed at "places" path).
 */
@Path(Paths.PLACES)
public class PlaceService {

	/** The Constant OK_MESSAGE_PLACESERVICE_PUT. */
	private static final String OK_MESSAGE_PLACESERVICE_PUT = Messages.OK_MESSAGE_PLACESERVICE_PUT;

	/** The Elasticsearch client. */
	private Client esClient = ElasticSearch.getClient();

	/**
	 * Returns a Place
	 *
	 * @param id of the Place to return
	 * @return the Place as JSON
	 */
	@GET
	@Path("{" + QParams.ID + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam(QParams.ID) String id) {

		GetResponse response = esClient
				.prepareGet(PRIMARY_INDEX_NAME, PLACES_TYPE_NAME, id).execute()
				.actionGet();
		if (!response.isExists())
			throw new WebApplicationException(404);
		return response.getSourceAsString();
	}

	/**
	 * Updates the Place of given id.
	 *
	 * @param id of the Place to update
	 * @param metadata - the JSON formatted string containing the updated Place
	 * @return an OK message if everything goes fine.
	 */
	@PUT
	@Path("{" + QParams.ID + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam(QParams.ID) String id, String metadata) {

		get(id);

		esClient.prepareIndex(PRIMARY_INDEX_NAME, PLACES_TYPE_NAME, id)
				.setSource(metadata).execute().actionGet();

		return OK_MESSAGE_PLACESERVICE_PUT;
	}

	/**
	 * Registers a new Place.
	 *
	 * @param metadata - The Place formatted in JSON
	 * @return the id of the created Place.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String register(String metadata) {

		IndexResponse response = esClient
				.prepareIndex(PRIMARY_INDEX_NAME, PLACES_TYPE_NAME)
				.setSource(metadata).execute().actionGet();

		return response.getId();
	}

	/**
	 * This method is used for throw a 403 exception in case of POSTing on a Place ID.
	 */
	@POST
	@Path("{" + QParams.ID + "}")
	public void notAllowed() {
		throw new WebApplicationException(405);
	}

	/**
	 * Deletes a Place.
	 *
	 * @param id - the id of the Place to delete
	 * @return true if deleted - 404 HTTP error if not found.
	 */
	@DELETE
	public String delete(@PathParam("id") String id) {

		DeleteResponse response = esClient
				.prepareDelete(PRIMARY_INDEX_NAME, PLACES_TYPE_NAME, id)
				.execute().actionGet();

		return Boolean.toString(response.isFound());
	}

}
