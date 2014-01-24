package eu.ldbc.semanticpublishing.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A holder for all the benchmark configuration parameters.
 * 
 * A client is expected to instantiate this class, which will provide values
 * (defaults or blank) for all configuration parameters, and then to save this
 * to a file (to create a template configuration file) or to load it from a file
 * (which is the usual case).
 */
public class Configuration {
	
	public static final String ENDPOINT_URL = "endpointURL";
	public static final String ENDPOINT_UPDATE_URL = "endpointUpdateURL";
	public static final String DATASET_SIZE_TRIPLES = "datasetSize";
	public static final String AGGREGATION_AGENTS_COUNT = "aggregationAgents";
	public static final String EDITORIAL_AGENTS_COUNT = "editorialAgents";
	public static final String QUERY_TIMEOUT_SECONDS = "queryTimeoutSeconds";
	public static final String SYSTEM_QUERY_TIMEOUT_SECONDS = "systemQueryTimeoutSeconds";
	public static final String WARMUP_PERIOD_SECONDS = "warmupPeriodSeconds";
	public static final String BENCHMARK_RUN_PERIOD_SECONDS = "benchmarkRunPeriodSeconds";
	public static final String GENERATED_TRIPLES_PER_FILE = "generatedTriplesPerFile";
	public static final String DATA_GENERATOR_WORKERS = "dataGeneratorWorkers";
	public static final String VERBOSE = "verbose";
	
	public static final String LOAD_ONTOLOGIES = "loadOntologies";
	public static final String ONTOLOGIES_PATH = "ontologiesPath";
	public static final String LOAD_REFERENCE_DATASETS = "loadReferenceDatasets";
	public static final String REFERENCE_DATASETS_PATH = "referenceDatasetsPath";
	public static final String GENERATE_CREATIVE_WORKS = "generateCreativeWorks";
	public static final String GENERATE_CREATIVE_WORKS_FORMAT = "generateCreativeWorksFormat";
	public static final String CREATIVE_WORKS_PATH = "creativeWorksPath";
	public static final String LOAD_CREATIVE_WORKS = "loadCreativeWorks";
	public static final String WARM_UP = "warmUp";
	public static final String RUN_BENCHMARK = "runBenchmark";
	public static final String CLEAR_DATABASE = "clearDatabase";
	public static final String CHECK_CONFORMANCE = "checkConformance";
	public static final String QUERIES_PATH = "queriesPath";
	public static final String DEFINITIONS_PATH = "definitionsPath";
	
	/**
	 * Initialise and set default values for parameters that make sense.
	 */
	public Configuration() {
		properties.setProperty(ENDPOINT_URL, "" );
		properties.setProperty(ENDPOINT_UPDATE_URL, "" );
		properties.setProperty(DATASET_SIZE_TRIPLES, "" );
		properties.setProperty(AGGREGATION_AGENTS_COUNT, "16" );
		properties.setProperty(EDITORIAL_AGENTS_COUNT, "2" );
		properties.setProperty(QUERY_TIMEOUT_SECONDS, "90" );
		properties.setProperty(SYSTEM_QUERY_TIMEOUT_SECONDS, "3600");
		properties.setProperty(WARMUP_PERIOD_SECONDS, "30" );
		properties.setProperty(BENCHMARK_RUN_PERIOD_SECONDS, "60" );
		properties.setProperty(GENERATED_TRIPLES_PER_FILE, "100000");
		properties.setProperty(DATA_GENERATOR_WORKERS, "1");
		properties.setProperty(VERBOSE, "false" );
		
		properties.setProperty(LOAD_ONTOLOGIES, "true");
		properties.setProperty(ONTOLOGIES_PATH, "./data/ontologies");
		properties.setProperty(LOAD_REFERENCE_DATASETS, "true");
		properties.setProperty(REFERENCE_DATASETS_PATH, "./data/datasets");
		properties.setProperty(GENERATE_CREATIVE_WORKS, "true");
		properties.setProperty(CREATIVE_WORKS_PATH, "");
		properties.setProperty(WARM_UP, "true");
		properties.setProperty(RUN_BENCHMARK, "true");
		properties.setProperty(CLEAR_DATABASE, "false");
		properties.setProperty(CHECK_CONFORMANCE, "false");
		properties.setProperty(QUERIES_PATH, "./data/sparql");
		properties.setProperty(DEFINITIONS_PATH, "./definitions.properties");
	}
	
	/**
	 * Load the configuration from the given file (java properties format).
	 * @param filename A readable file on the file system.
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws IOException {
		
		InputStream input = new FileInputStream(filename);
		try {
			properties.load(input);
		}
		finally {
			input.close();
		}
	}
	
	/**
	 * Save the configuration to a text file (java properties format).
	 * @param filename
	 * @throws IOException
	 */
	public void saveToFile(String filename) throws IOException {
		OutputStream output = new FileOutputStream(filename);
		try {
			properties.store(output, "");
		}
		finally {
			output.close();
		}
	}
	
	/**
	 * Read a configuration parameter's value as a string
	 * @param key
	 * @return
	 */
	public String getString( String key) {
		String value = properties.getProperty(key);
		
		if(value == null) {
			throw new IllegalStateException( "Missing configuration parameter: " + key);
		}
		return value;
	}

	/**
	 * Read a configuration parameter's value as a boolean
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		String value = getString(key);
		
		if(value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("y") ) {
			return true;
		}
		if(value.equalsIgnoreCase("false") || value.equals("0") || value.equalsIgnoreCase("n") ) {
			return false;
		}
		throw new IllegalStateException( "Illegal value for boolean configuration parameter: " + key);
	}

	/**
	 * Read a configuration parameter's value as an int
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		String value = getString(key);
		
		try {
			return Integer.parseInt(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for integer configuration parameter: " + key);
		}
	}

	/**
	 * Read a configuration parameter's value as a long
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		String value = getString(key);
		
		try {
			return Long.parseLong(value);
		}
		catch( NumberFormatException e ) {
			throw new IllegalStateException( "Illegal value for long integer configuration parameter: " + key);
		}
	}

	private final Properties properties = new Properties();
	
	public static void main(String[] args) throws IOException  {
		Configuration c = new Configuration();
		c.saveToFile("default_config.properties");
	}
}