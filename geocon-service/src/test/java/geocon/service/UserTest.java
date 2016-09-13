package geocon.service;

import geocon.common.Paths;
import geocon.common.QParams;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static geocon.service.Configuration.PRIMARY_INDEX_NAME;
import static geocon.service.Configuration.USERS_TYPE_NAME;
import static org.junit.Assert.assertEquals;

public class UserTest {

	private HttpServer server;
	private WebTarget target;

	@Before
	public void setUp() throws Exception {
		// start the server
		server = Main.startServer();
		// create the client
		Client c = ClientBuilder.newClient();

		// uncomment the following line if you want to enable
		// support for JSON in the client (you also have to uncomment
		// dependency on jersey-media-json module in pom.xml and
		// Main.startServer())
		// --
		// c.configuration().enable(new
		// org.glassfish.jersey.media.json.JsonJaxbFeature());

		target = c.target(Main.BASE_URI);

		fillSampleDB();
	}

	private void fillSampleDB() {
		// TODO Fill the DB with sample data
	}

	@After
	public void tearDown() throws Exception {
		server.shutdownNow();
		flushDB();
	}

	private void flushDB() {

		DeleteIndexResponse response = ElasticSearch
				.getClient()
				.admin()
				.indices()
				.delete(new DeleteIndexRequest(Configuration.PRIMARY_INDEX_NAME))
				.actionGet();

		// assertTrue(response.isAcknowledged());

	}

	/**
	 * Test the web service users get method
	 */
	@Test
	public void testGet() {
		String responseMsg = target.path(Paths.USERS)
				.queryParam(QParams.ID, "2").request().get(String.class);
		assertEquals("33", responseMsg);
	}
}
