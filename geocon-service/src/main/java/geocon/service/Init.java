package geocon.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Class Init. It's used for fill the Elasticsearch database.
 */
public class Init {

	/** The Constant COMMON_PASSWORD. It's the password of every User in the db. */
	private static final String COMMON_PASSWORD = "prova";
	
	/** The Elasticsearch client instance. */
	private static Client client = ElasticSearch.getClient();

	/**
	 * Initializes the db .
	 *
	 * @param path - the path of the resource
	 * @param index - the index where the documents are gonna be stored
	 * @param type - the type of document you want to store
	 * @throws FileNotFoundException - if file is not found 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException if there are problems during the JSON parsing
	 */
	@SuppressWarnings("rawtypes")
	private static void init(String path, String index, String type)
			throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(path));
		JSONArray jsonObject = (JSONArray) obj;
		Iterator it = jsonObject.iterator();
		int numElementsInserted = 0;
		while (it.hasNext()) {
			Object next = it.next();
			IndexResponse risposta = client
					.prepareIndex(index, type,
							Integer.toString(numElementsInserted))
					.setSource(next.toString()).execute().actionGet();
			System.out.println("ID: " + risposta.getId());
			numElementsInserted++;
		}
		System.out.println("Added " + numElementsInserted + " " + type);

	}

	/**
	 * The main method.
	 *
	 * @param args - nothing special
	 */
	public static void main(String[] args) {

		System.out.println();
		System.out.println("ElasticSearch db Filler");
		System.out.println();

		Scanner sc = new Scanner(System.in);

		try {

			System.out
					.println("Please insert the USERS text file path: (e.g: examples/utenti)");
			initUsers(sc.nextLine(), Configuration.PRIMARY_INDEX_NAME,
					Configuration.USERS_TYPE_NAME);

			System.out
					.println("Please insert the EVENTS text file path: (e.g: examples/eventi)");
			init(sc.nextLine(), Configuration.PRIMARY_INDEX_NAME,
					Configuration.EVENTS_TYPE_NAME);

			System.out
					.println("Please insert the PLACES text file path: (e.g: examples/posti)");
			init(sc.nextLine(), Configuration.PRIMARY_INDEX_NAME,
					Configuration.PLACES_TYPE_NAME);

			System.out
					.println("Please insert the RESOURCES text file path: (e.g: examples/risorse)");
			init(sc.nextLine(), Configuration.PRIMARY_INDEX_NAME,
					Configuration.RESOURCES_TYPE_NAME);

		} catch (IOException | ParseException ecc) {
			System.out
					.println("File not found or problem during parse the file.");
		} catch (org.elasticsearch.client.transport.NoNodeAvailableException e) {
			System.out
					.println("No node available. Is an Elasticsearch istance running?");
		} finally {
			client.close();
			sc.close();
		}
	}

	/**
	 * Initializes the users.
	 *
	 * @param path - The path where there's stored the user vector in JSON format
	 * @param index - the index where the users have to be stored
	 * @param type - the type name (in std config USER)
	 * @throws FileNotFoundException if the file is not found
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException if there are problems during the JSON parsing
	 */
	private static void initUsers(String path, String index, String type)
			throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(path));
		JSONArray jsonObject = (JSONArray) obj;
		@SuppressWarnings("rawtypes")
		Iterator it = jsonObject.iterator();
		int numElementsInserted = 0;
		while (it.hasNext()) {
			Object next = it.next();
			IndexResponse risposta = client
					.prepareIndex(index, type,
							Integer.toString(numElementsInserted))
					.setSource(next.toString()).execute().actionGet();

			System.out.println("ID: " + risposta.getId());

			IndexResponse credenziali = client
					.prepareIndex(index, Configuration.CREDENTIALS_TYPE_NAME,
							risposta.getId())
					.setSource("password", COMMON_PASSWORD).execute()
					.actionGet();
			System.out.println("Successfully created password for: "
					+ credenziali.getId());

			numElementsInserted++;
		}
		System.out.println("Added " + numElementsInserted + " " + type);

	}
}
