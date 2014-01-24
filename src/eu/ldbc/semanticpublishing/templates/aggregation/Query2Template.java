package eu.ldbc.semanticpublishing.templates.aggregation;

import java.util.HashMap;

import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.refdataset.DataManager;
import eu.ldbc.semanticpublishing.templates.MustacheTemplate;
import eu.ldbc.semanticpublishing.util.RandomUtil;

/**
 * A class extending the MustacheTemplate, used to generate a query string
 * corresponding to file Configuration.QUERIES_PATH/aggregation/query2.txt
 */
public class Query2Template extends MustacheTemplate {

	//must match with corresponding file name of the mustache template file
	private static final String templateFileName = "query2.txt"; 
	
	private final RandomUtil ru;	
	
	public Query2Template(RandomUtil ru, HashMap<String, String> queryTemplates) {
		super(queryTemplates);
		this.ru = ru;
	}
	
	/**
	 * A method for replacing mustache template : {{{cwUri}}}
	 */	
	public String cwUri() {
		long cwNextId = ru.nextInt((int)DataManager.creativeWorksNexId.get());
		return ru.numberURI("things", cwNextId, true, true);		
	}

	@Override
	public String getTemplateFileName() {
		return templateFileName;
	}
	
	@Override
	public QueryType getTemplateQueryType() {
		return QueryType.CONSTRUCT;
	}
}