package geocon.service;

import static geocon.service.Configuration.PRIMARY_INDEX_NAME;
import static geocon.service.Configuration.RESOURCES_TYPE_NAME;

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

/**
 * The Class ResourceService.
 */
@Path(Paths.RESOURCES)
public class ResourceService {

	/** The Constant OK_MESSAGE_RESOURCESERVICE_PUT. */
	private static final String OK_MESSAGE_RESOURCESERVICE_PUT = Messages.OK_MESSAGE_RESOURCESERVICE_PUT;
	
	/** The Elasticsearch client. */
	private Client esClient = ElasticSearch.getClient();

	/**
	 * Returns a Resource 
	 *
	 * @param id of the resource to return
	 * @return the resource as JSON
	 */
	@GET
	@Path("{" + QParams.ID + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam(QParams.ID) String id) {

		try {
			GetResponse response = esClient
					.prepareGet(PRIMARY_INDEX_NAME, RESOURCES_TYPE_NAME, id)
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
	 * Updates the Resource of given id
	 *
	 * @param id of the resource to update
	 * @param metadata - the JSON formatted string containing the updated resource
	 * @return an OK message if everything goes fine.
	 */
	@PUT
	@Path("{" + QParams.ID + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam(QParams.ID) String id, String metadata) {

		get(id);

		// Dumb variable for auth
		boolean isMyself = true;
		if (isMyself) {

			esClient.prepareIndex(PRIMARY_INDEX_NAME, RESOURCES_TYPE_NAME, id)
					.setSource(metadata).execute().actionGet();
		}

		return OK_MESSAGE_RESOURCESERVICE_PUT;
	}

	/**
	 * Registers a new Resource.
	 *
	 * @param metadata - The resource formatted in JSON
	 * @return the id of created Resource
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String register(String metadata) {
		IndexResponse response = esClient
				.prepareIndex(PRIMARY_INDEX_NAME, RESOURCES_TYPE_NAME)
				.setSource(metadata).execute().actionGet();

		return response.getId();
	}

	/**
	 * This method is used for throw a 403 exception in case of POSTing on a resource ID.
	 */
	@POST
	@Path("{" + QParams.ID + "}")
	public void notAllowed() {
		throw new WebApplicationException(403);
	}

	/**
	 * Deletes a Resource.
	 *
	 * @param id - The id of the resource to delete
	 * @return true if deleted, 404 HTTP error if not found
	 */
	@DELETE
	@Path("{" + QParams.ID + "}")
	public String delete(@PathParam("id") String id) {

		get(id);

		// Dumb variable for authentication
		boolean isMyself = true;

		if (isMyself) {
			DeleteResponse response = esClient
					.prepareDelete(PRIMARY_INDEX_NAME, RESOURCES_TYPE_NAME, id)
					.execute().actionGet();

			return Boolean.toString(response.isFound());
		}

		throw new WebApplicationException(401);

	}

}
