package eu.ldbc.semanticpublishing.templates.aggregation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.substitutionparameters.SubstitutionParametersGenerator;
import eu.ldbc.semanticpublishing.properties.Definitions;
import eu.ldbc.semanticpublishing.templates.MustacheTemplate;
import eu.ldbc.semanticpublishing.util.RandomUtil;

/**
 * A class extending the MustacheTemplate, used to generate a query string
 * corresponding to file Configuration.QUERIES_PATH/aggregation/query21.txt
 * A time faceted search query template
 */
public class Query21Template extends MustacheTemplate implements SubstitutionParametersGenerator {
	//must match with corresponding file name of the mustache template file
	private static final String templateFileName = "query21.txt";
	
	protected final RandomUtil ru;
	
	protected static final String PROJECTION_STRING_ITERATION_0 = "?title ?description ?category ?tag ?audience ?liveCoverage ?primaryFormat ?year ?month";
	protected static final String PROJECTION_STRING_ITERATION_1 = "?year ?month ((COUNT(*)) AS ?count)";
	protected static final String PROJECTION_STRING_ITERATION_2 = "?year ?month ?tag ((COUNT(*)) as ?count)";
	protected static final String PROJECTION_STRING_ITERATION_3 = "?year ?month ?primaryFormat ((COUNT(*)) as ?count)";
	protected static final String PROJECTION_STRING_ITERATION_4 = "?title ?dateCreated ((COUNT(*)) AS ?count)";	
	
	protected static final String FILTER_CONTAINS_STRING = "FILTER (CONTAINS(?title, \"%s\") || CONTAINS(?description, \"%s\")) .";
	protected static final String FILTER_CATEGORY_STRING = "FILTER (?category = %s) .";
	protected static final String FILTER_DATE_YMD_STRING = "FILTER (?year = %d && ?month = %d && ?day = %d) .";
	
	protected static final String GROUP_BY_STRING_ITERATION_1 = "GROUP BY ?year ?month";
	protected static final String GROUP_BY_STRING_ITERATION_2 = "GROUP BY ?year ?month ?tag";
	protected static final String GROUP_BY_STRING_ITERATION_3 = "GROUP BY ?year ?month ?primaryFormat";
	protected static final String GROUP_BY_STRING_ITERATION_4 = "GROUP BY ?dateCreated ?title";
	
	protected static final String ORDER_BY_STRING_ITERATION_0 = "ORDER BY ?year ?month";
	protected static final String ORDER_BY_STRING_ITERATION_1 = "ORDER BY ?year ?month";
	protected static final String ORDER_BY_STRING_ITERATION_2 = "ORDER BY ?year ?month ?count";
	protected static final String ORDER_BY_STRING_ITERATION_3 = "ORDER BY ?year ?month ?count";
	protected static final String ORDER_BY_STRING_ITERATION_4 = "ORDER BY ?dateCreated ?title";
	
	protected int iteration;
	
	protected static final String[] categoryTypes = {"<http://www.bbc.co.uk/category/PoliticsPersonsReference>", 
													"<http://www.bbc.co.uk/category/PoliticsPersons>", 
													"<http://www.bbc.co.uk/category/PoliticsPersonsAdditional>", 
													"<http://www.bbc.co.uk/category/SportsTeams>", 
													"<http://www.bbc.co.uk/category/SportsCompetitions>" };	
	
	protected int year;
	protected int month;
	protected int day;
	
	protected Calendar calendar;
	
	protected String containsExpression1;
	protected String containsExpression2;
	protected String category;
	
	public Query21Template(RandomUtil ru, HashMap<String, String> queryTemplates, Definitions definitions, String[] substitutionParameters) {
		super(queryTemplates, substitutionParameters);
		this.ru = ru;		
		this.calendar = Calendar.getInstance();
		preInitialize();
	}
	
	protected void preInitialize() {
		this.iteration = 0;
		this.year = 0;
		this.month = 0;
		this.day = 0;
		this.containsExpression1 = ru.randomWordFromDictionary(false, false);
		this.containsExpression2 = ru.randomWordFromDictionary(false, false);
		this.category = String.format(FILTER_CATEGORY_STRING, categoryTypes[ru.nextInt(categoryTypes.length)]);
		this.parameterIndex = 0;
	}
	
	/**
	 * @param dateString - expected date string format : 2010-10-02T17:41:36.229+03:00
	 */
	public void initialize(int iteration, String dateString, String[] substitutionParameters) {
		this.iteration = iteration;
		this.substitutionParameters = substitutionParameters;
		
		try {
			if (!dateString.isEmpty()) {			
				String tokens[] = dateString.split("-");
				if (tokens.length == 3) {
					this.year = Integer.parseInt(tokens[0]);
					this.month = Integer.parseInt(tokens[1]);
					calendar.set(year, month - 1, 1);
					this.day = ru.nextInt(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
				}
			}
		} catch (NumberFormatException nfe) {
			//sink the exception
		}
	}	
	
	/**
	 * A method for replacing mustache template : {{{projection}}}
	 */
	public String projection() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}		
		
		switch (iteration) {
		case 0 :
			return PROJECTION_STRING_ITERATION_0;
		case 1 :
			return PROJECTION_STRING_ITERATION_1;
		case 2 :
			return PROJECTION_STRING_ITERATION_2;
		case 3 :
			return PROJECTION_STRING_ITERATION_3;
		case 4 :
			return PROJECTION_STRING_ITERATION_4;
		default:
			return "SELECT *";
		}
	}	
	
	/**
	 * A method for replacing mustache template : {{{filter1}}}
	 */
	public String filter1() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}
		
		return String.format(FILTER_CONTAINS_STRING, containsExpression1, containsExpression2);
	}

	/**
	 * A method for replacing mustache template : {{{filter2}}}
	 */
	public String filter2() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}
		
		return category;
	}
	
	/**
	 * A method for replacing mustache template : {{{filter3}}}
	 */
	public String filter3() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}
		
		if (iteration == 4) {
			return String.format(FILTER_DATE_YMD_STRING, year, month, day);
		} else {
			return " ";
		}
	}
	
	/**
	 * A method for replacing mustache template : {{{groupBy}}}
	 */
	public String groupBy() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}
		
		switch (iteration) {
		case 0 :
			return " ";
		case 1 :
			return GROUP_BY_STRING_ITERATION_1;
		case 2 :
			return GROUP_BY_STRING_ITERATION_2;
		case 3 :
			return GROUP_BY_STRING_ITERATION_3;
		case 4 :
			return GROUP_BY_STRING_ITERATION_4;
		default:
			return " ";
		}
	}	

	/**
	 * A method for replacing mustache template : {{{orderBy}}}
	 */
	public String orderBy() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}	
		
		switch (iteration) {
		case 0 :
			return ORDER_BY_STRING_ITERATION_0;
		case 1 :
			return ORDER_BY_STRING_ITERATION_1;
		case 2 :
			return ORDER_BY_STRING_ITERATION_2;
		case 3 :
			return ORDER_BY_STRING_ITERATION_3;
		case 4 :
			return ORDER_BY_STRING_ITERATION_4;
		default:
			return " ";
		}
	}	
	
	/**
	 * A method for replacing mustache template : {{{randomLimit}}}
	 */	
	public String randomLimit() {
		if (substitutionParameters != null) {
			return substitutionParameters[parameterIndex++];
		}		
		
		return "500";
	}
	
	@Override
	public String generateSubstitutionParameters(BufferedWriter bw, int amount) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			preInitialize();
			sb.setLength(0);
			sb.append(projection());
			sb.append(SubstitutionParametersGenerator.PARAMS_DELIMITER);
			sb.append(filter1());
			sb.append(SubstitutionParametersGenerator.PARAMS_DELIMITER);
			sb.append(filter2());
			sb.append(SubstitutionParametersGenerator.PARAMS_DELIMITER);
			sb.append(filter3());
			sb.append(SubstitutionParametersGenerator.PARAMS_DELIMITER);
			sb.append(groupBy());
			sb.append(SubstitutionParametersGenerator.PARAMS_DELIMITER);
			sb.append(orderBy());
			sb.append(SubstitutionParametersGenerator.PARAMS_DELIMITER);
			sb.append(randomLimit());
			sb.append("\n");
			bw.write(sb.toString());	
		}
		return null;
	}
	
	@Override
	public String getTemplateFileName() {
		return templateFileName;
	}
	
	@Override
	public QueryType getTemplateQueryType() {
		return QueryType.SELECT;
	}		
}
