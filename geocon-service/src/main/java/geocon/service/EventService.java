package geocon.service;

import static geocon.service.Configuration.PRIMARY_INDEX_NAME;
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

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import static geocon.service.Configuration.EVENTS_TYPE_NAME;
/**
 * Root resource (exposed at "Event" path).
 */
@Path(Paths.EVENTS)
public class EventService {

	/** The Constant OK_MESSAGE_EVENTSERVICE_PUT. It's the message returned 
	 * when a PUT goes fine. */
	private static final String OK_MESSAGE_EVENTSERVICE_PUT = Messages.OK_MESSAGE_EVENTSERVICE_PUT;

	/** The Elasticsearch client. */
	private Client esClient = ElasticSearch.getClient();

	/**
	 * Returns an Event
	 *
	 * @param id of the Event to return
	 * @return the Event as JSON
	 */
	@GET
	@Path("{" + QParams.ID + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam(QParams.ID) String id) {

		try {
			GetResponse response = esClient
					.prepareGet(PRIMARY_INDEX_NAME, EVENTS_TYPE_NAME, id)
					.execute().actionGet();
			if (response.isExists()) {
				return response.getSourceAsString();
			}
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			return e.getMessage();
		}

		throw new WebApplicationException(404);
	}

	/**
	 * Updates the Event of given id.
	 *
	 * @param id of the Event to update
	 * @param metadata - the JSON formatted string containing the updated Event
	 * @return an OK message if everything goes fine.
	 */
	@PUT
	@Path("{" + QParams.ID + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam(QParams.ID) String id, String metadata) {
		get(id);
		// Dumb variable for auth.
		boolean isMyself = true;
		if (isMyself) {
			esClient.prepareIndex(PRIMARY_INDEX_NAME, EVENTS_TYPE_NAME, id)
					.setSource(metadata).execute().actionGet();
		}

		return OK_MESSAGE_EVENTSERVICE_PUT;
	}

	/**
	 * Registers a new Event.
	 *
	 * @param metadata - The Event formatted in JSON
	 * @return the id of the created Event.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String register(String metadata) {

		IndexResponse response = esClient
				.prepareIndex(PRIMARY_INDEX_NAME, EVENTS_TYPE_NAME)
				.setSource(metadata).execute().actionGet();
		System.out.println(response.getId());
		return response.getId();
	}

	/**
	 * This method is used for throw a 403 exception in case of POSTing on a Event ID.
	 */
	@POST
	@Path("{" + QParams.ID + "}")
	public void notAllowed() {
		throw new WebApplicationException(405);
	}

	/**
	 * Deletes an Event.
	 *
	 * @param id - the id of the Event to delete
	 * @return true if deleted - 404 HTTP error if not found.
	 */
	@DELETE
	@Path("{" + QParams.ID + "}")
	public String delete(@PathParam("id") String id) {

		get(id);
		
		// Dumb variable for auth.
		boolean isMyself = true;
		boolean deleted = false;

		if (isMyself) {
			DeleteResponse response = esClient
					.prepareDelete(PRIMARY_INDEX_NAME, EVENTS_TYPE_NAME, id)
					.execute().actionGet();

			deleted = response.isFound();
		}

		return Boolean.toString(deleted);
	}
}
