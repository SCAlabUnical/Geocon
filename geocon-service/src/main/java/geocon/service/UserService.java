package geocon.service;

import static geocon.service.Configuration.PRIMARY_INDEX_NAME;
import static geocon.service.Configuration.USERS_TYPE_NAME;
import static geocon.service.Configuration.CREDENTIALS_TYPE_NAME;

import java.util.Map;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

/**
 * Root resource (exposed at "users" path).
 */
@Path(Paths.USERS)
public class UserService {

	/** The Constant OK_MESSAGE_USERSERVICE_PUT. */
	private static final String OK_MESSAGE_USERSERVICE_PUT = Messages.OK_MESSAGE_USERSERVICE_PUT;
	
	/** The Elasticsearch client. */
	private Client esClient = ElasticSearch.getClient();

	/**
	 * Returns an User
	 *
	 * @param id of the User to return
	 * @return the User as JSON
	 */
	@GET
	@Path("{" + QParams.ID + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam(QParams.ID) String id) {
		try {

			GetResponse response = esClient
					.prepareGet(PRIMARY_INDEX_NAME, USERS_TYPE_NAME, id)
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
	 * Update an User.
	 *
	 * @param id - the id of the user you're intrested in.
	 * @param password - the password set for the User
	 * @param metadata - the updated JSON with User information
	 * @return an OK message if everything goes fine.
	 */
	@PUT
	@Path("{" + QParams.ID + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String update(@PathParam(QParams.ID) String id,
			@QueryParam(QParams.PASSWORD) String password, String metadata) {

		boolean isMyself = auth(id, password);
		get(id);

		if (isMyself) {
			esClient.prepareIndex(PRIMARY_INDEX_NAME, USERS_TYPE_NAME, id)
					.setSource(metadata).execute().actionGet();
			return OK_MESSAGE_USERSERVICE_PUT;
		}

		throw new WebApplicationException(401);
	}

	/**
	 * Registers a new User.
	 *
	 * @param metadata - the User in JSON format
	 * @param password - the password to use for that user
	 * @return the id of the new User.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String register(String metadata,
			@QueryParam(QParams.PASSWORD) String password) {

		if (password == null)
			throw new WebApplicationException(400);

		IndexResponse response = esClient
				.prepareIndex(PRIMARY_INDEX_NAME, USERS_TYPE_NAME)
				.setSource(metadata).execute().actionGet();

		// Creates the entry in the credential database
		esClient.prepareIndex(PRIMARY_INDEX_NAME, CREDENTIALS_TYPE_NAME,
				response.getId()).setSource("password", password).execute()
				.actionGet();

		return response.getId();
	}

	/**
	 * Not allowed. You should not call a POST method on a User id!
	 */
	@POST
	@Path("{" + QParams.ID + "}")
	public void notAllowed() {
		throw new WebApplicationException(405);
	}

	/**
	 * Deletes a User. The password is required.
	 *
	 * @param id of the user you want to delete
	 * @param password - the password associated to the user you want to delete
	 * @return true if the user has been deleted
	 */
	@DELETE
	@Path("{" + QParams.ID + "}")
	public String delete(@PathParam(QParams.ID) String id,
			@QueryParam(QParams.PASSWORD) String password) {
		get(id);

		if (password == null)
			throw new WebApplicationException(403);
		boolean isMyself = auth(id, password);

		if (isMyself) {
			// Deleting the User
			DeleteResponse response = esClient
					.prepareDelete(PRIMARY_INDEX_NAME, USERS_TYPE_NAME, id)
					.execute().actionGet();
			
			// Deleting the Credential
			esClient.prepareDelete(PRIMARY_INDEX_NAME, CREDENTIALS_TYPE_NAME, id)
			.execute().actionGet();
			
			return Boolean.toString(response.isFound());

		}

		throw new WebApplicationException(401);
	}

	/**
	 * Authorizes an action.
	 *
	 * @param id - the id of the User who's trying to do the action
	 * @param passwordToCheck - the password given.
	 * @return true, if successful
	 */
	private boolean auth(String id, String passwordToCheck) {

		Map<String, Object> json = esClient
				.prepareGet(PRIMARY_INDEX_NAME, CREDENTIALS_TYPE_NAME, id)
				.execute().actionGet().getSource();

		return ((String) json.get("password")).equals(passwordToCheck);
	}

}
