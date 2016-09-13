package geocon.service;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
/**
 * Main class. Where everything begins.
 *
 */
public class Main {
	/** Base URI the Grizzly HTTP server will listen on. */
	public static final String BASE_URI = "http://" + Configuration.ADDRESS
			+ ":" + Configuration.PORT + "/" + Configuration.SERVICE_NAME + "/";

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and
		// providers in geocon.service package
		final ResourceConfig rc = new ResourceConfig()
				.packages("geocon.service");

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI),
				rc);
	}

	/**
	 * Main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		final String FILL_COMMAND = "fill";

		if (args.length == 1) {
			if (args[0].equals(FILL_COMMAND)) {
				Init.main(null);
				return;
			} else
				throw new RuntimeException(
						"Bad syntax. Please read the manual for correct parameters");
		}
		final HttpServer server = startServer();
		ElasticSearch.getInstance();
		System.out.println(String.format(
				"Jersey app started with WADL available at "
						+ "%sapplication.wadl\nHit enter to stop it...",
				BASE_URI));
		System.in.read();
		server.shutdownNow();
		ElasticSearch.stop();
	}
}
