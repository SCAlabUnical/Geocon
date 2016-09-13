package geocon.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The Class Welcome.
 */
@Path("/")
public class Welcome {

	/** The Constant WELCOME. */
	private static final String WELCOME = "Welcome to "
			+ Configuration.SERVICE_NAME
			+ "."
			+ " For further information, please refer to the README document included.";

	/**
	 * Welcome message.
	 *
	 * @return the string
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String welcomeMessage() {
		return WELCOME;
	}

}
