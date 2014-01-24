package eu.ldbc.semanticpublishing.agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ldbc.semanticpublishing.TestDriver;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection.QueryType;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryConnection;
import eu.ldbc.semanticpublishing.endpoint.SparqlQueryExecuteManager;
import eu.ldbc.semanticpublishing.properties.Definitions;
import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.resultanalyzers.Query24Analyzer;
import eu.ldbc.semanticpublishing.resultanalyzers.Query25Analyzer;
import eu.ldbc.semanticpublishing.statistics.Statistics;
import eu.ldbc.semanticpublishing.templates.MustacheTemplate;
import eu.ldbc.semanticpublishing.templates.aggregation.*;
import eu.ldbc.semanticpublishing.util.RandomUtil;

/**
 * A class that represents an aggregation agent. It executes aggregation queries 
 * in a loop, updates query execution statistics.
 */
public class AggregationAgent extends AbstractAsynchronousAgent {
	private final SparqlQueryExecuteManager queryExecuteManager;
	private final RandomUtil ru;
	private final AtomicBoolean benchmarkingState;
	private final HashMap<String, String> queryTemplates;
	private SparqlQueryConnection connection;	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AggregationAgent.class.getName());
	private final static Logger BRIEF_LOGGER = LoggerFactory.getLogger(TestDriver.class.getName());
	private final static int MAX_DRILL_DOWN_ITERATIONS = 5;
	
	public AggregationAgent(AtomicBoolean benchmarkingState, SparqlQueryExecuteManager queryExecuteManager, RandomUtil ru, AtomicBoolean runFlag, HashMap<String, String> queryTamplates) {
		super(runFlag);
		this.queryExecuteManager = queryExecuteManager;
		this.ru = ru;
		this.benchmarkingState = benchmarkingState;
		this.queryTemplates = queryTamplates;
		this.connection = new SparqlQueryConnection(queryExecuteManager.getEndpointUrl(), queryExecuteManager.getEndpointUpdateUrl(), queryExecuteManager.getTimeoutMilliseconds(), true);
	}
	
	@Override
	public boolean executeLoop() {
		//retrieve next query to be executed from the aggregation query mix
		int aggregateQueryIndex = Definitions.aggregationOperationsAllocation.getAllocation();
		
		long queryId = 0;
		MustacheTemplate aggregateQuery = null;
		String queryString = "";
		String queryResult = "";
		
		boolean startedDuringBenchmarkPhase = false;

		try {
			boolean drillDownQuery = false;
			
			//important : queryDistribution is zero-based, while QueryNTemplate is not!
			queryId = Statistics.aggregateQueriesArray[aggregateQueryIndex].getNewQueryId();
			
			switch (aggregateQueryIndex) {
				case 0 :
					aggregateQuery = new Query1Template(ru, queryTemplates);
					break;
				case 1 :
					aggregateQuery = new Query2Template(ru, queryTemplates);
					break;
				case 2 : 
					aggregateQuery = new Query3Template(ru, queryTemplates);
					break;
				case 3 :
					aggregateQuery = new Query4Template(ru, queryTemplates);
					break;
				case 4 :
					aggregateQuery = new Query5Template(ru, queryTemplates);
					break;
				case 5 : 
					aggregateQuery = new Query6Template(ru, queryTemplates);
					break;
				case 6 :
					aggregateQuery = new Query7Template(queryTemplates);
					break;
				case 7 : 
					aggregateQuery = new Query8Template(ru, queryTemplates);
					break;
				case 8 :
					aggregateQuery = new Query9Template(queryTemplates);
					break;
				case 9 :
					aggregateQuery = new Query10Template(queryTemplates);
					break;
				case 10 :
					aggregateQuery = new Query11Template(ru, queryTemplates);
					break;
				case 11 : 
					aggregateQuery = new Query12Template(ru, queryTemplates);
					break;
				case 12 : 
					aggregateQuery = new Query13Template(ru, queryTemplates);
					break;
				case 13 : 
					aggregateQuery = new Query14Template(ru, queryTemplates);
					break;
				case 14 : 
					aggregateQuery = new Query15Template(queryTemplates);
					break;
				case 15 : 
					aggregateQuery = new Query16Template(queryTemplates);
					break;
				case 16 : 
					aggregateQuery = new Query17Template(queryTemplates);
					break;
				case 17 : 
					aggregateQuery = new Query18Template(queryTemplates);
					break;
				case 18 : 
					aggregateQuery = new Query19Template(queryTemplates);
					break;
				case 19 : 
					aggregateQuery = new Query20Template(queryTemplates);
					break;
				case 20 : 
					aggregateQuery = new Query21Template(queryTemplates);
					break;
				case 21 : 
					aggregateQuery = new Query22Template(queryTemplates);
					break;
				case 22 : 
					aggregateQuery = new Query23Template(queryTemplates);
					break;
				case 23 :
					//Drill-Down query with constraints on Geo-locations
					drillDownQuery = true;
					aggregateQuery = new Query24Template(ru, queryTemplates);
					break;
				case 24 :
					//Drill-Down query with constraints on Date intervals
					drillDownQuery = true;
					aggregateQuery = new Query25Template(ru, queryTemplates);
					break;
				case 25 :
					//FTS Query
					aggregateQuery = new Query26Template(ru, queryTemplates);
					break;
			}
			
			queryString = aggregateQuery.compileMustacheTemplate();
			
			//remember if query was executed before benchmark phase start to skip it later when updating query statistics. No need to do that for Editorial Agents.
			startedDuringBenchmarkPhase = benchmarkingState.get();
			
			long executionTimeMs = System.currentTimeMillis();
			
			queryResult = queryExecuteManager.executeQuery(connection, aggregateQuery.getTemplateFileName(), queryString, aggregateQuery.getTemplateQueryType(), true, false);			
			
			updateQueryStatistics(true, startedDuringBenchmarkPhase, aggregateQuery.getTemplateQueryType(), aggregateQuery.getTemplateFileName(), queryString, queryResult, queryId, System.currentTimeMillis() - executionTimeMs);
			
			if (drillDownQuery) {
				//further loop the drill-down query using results from previous run
				executeDrillDown(aggregateQuery, aggregateQueryIndex, queryString, queryResult);
			}

		} catch (IOException ioe) {
			String msg = "Warning : AggregationAgent : IOException caught : " + ioe.getMessage() + ", attempting a new connection" + "\n" + "\tfor query : \n" + queryString;
			
			System.out.println(msg);
			
			LOGGER.warn(msg);
			
			updateQueryStatistics(false, startedDuringBenchmarkPhase, aggregateQuery.getTemplateQueryType(), aggregateQuery.getTemplateFileName(), queryString, queryResult, queryId, 0);
			
			connection = new SparqlQueryConnection(queryExecuteManager.getEndpointUrl(), queryExecuteManager.getEndpointUpdateUrl(), queryExecuteManager.getTimeoutMilliseconds(), true);
		}
		return true;
	}
	
	private void executeDrillDown(MustacheTemplate aggregateQuery, int queryDistribution, String queryString, String queryResult) throws IOException {
		String qString = queryString;
		String qResult = queryResult;
		ArrayList<Entity> entitiesList;
		
		for (int i = 0; i < MAX_DRILL_DOWN_ITERATIONS ; i++) {
			
			switch (queryDistribution) {
				case 23 :
					Query24Analyzer query24Analyzer = new Query24Analyzer();
					entitiesList = query24Analyzer.collectEntitiesList(qResult);
					if (entitiesList.size() > 0) {
						int randomEntity = ru.nextInt(entitiesList.size());
						Entity entity = entitiesList.get(randomEntity);

						double latitude = Double.parseDouble(entity.getObjectFromTriple("geo:lat"));
						double longtitude = Double.parseDouble(entity.getObjectFromTriple("geo:long"));

						((Query24Template)aggregateQuery).initialize(latitude, longtitude, ru.nextDouble(0.01, 0.08));

						qString = aggregateQuery.compileMustacheTemplate();
						qResult = queryExecuteManager.executeQuery(connection, aggregateQuery.getTemplateFileName(), qString, aggregateQuery.getTemplateQueryType(), true, false);

						//If uncommented each execution of the query will be logged in result
						//updateQueryStatistics(true, aggregateQuery.getTemplateQueryType(), aggregateQuery.getTemplateFileName(), qString, queryResult);
					} else {
					return;
				}
				
				break;
				
			case 24 :
				Query25Analyzer query25Analyzer = new Query25Analyzer();
				entitiesList = query25Analyzer.collectEntitiesList(qResult);
				if (entitiesList.size() > 0) {
					int randomEntity = ru.nextInt(entitiesList.size());
					Entity entity = entitiesList.get(randomEntity);
					
					((Query25Template)aggregateQuery).initialize(entity.getObjectFromTriple("cwork:dateModified"), ru.nextInt(1, 3));

					qString = aggregateQuery.compileMustacheTemplate();
					qResult = queryExecuteManager.executeQuery(connection, aggregateQuery.getTemplateFileName(), qString, aggregateQuery.getTemplateQueryType(), true, false);

					//If uncommented each execution of the query will be logged in result
					//updateQueryStatistics(true, aggregateQuery.getTemplateQueryType(), aggregateQuery.getTemplateFileName(), qString, queryResult);
				} else {
					return;
				}
				break;	
			}
		}
	}
	
	@Override
	public void executeFinalize() {				
		connection.disconnect();
	}
	
	private void updateQueryStatistics(boolean reportSuccess, boolean startedDuringBenchmarkPhase, QueryType queryType, String queryName, String queryString, String queryResult, long id, long queryExecutionTimeMs) {
		//skip update of statistics for conformance queries
		if (queryName.startsWith("#")) {
			return;
		}
		
		int queryNumber = getQueryNumber(queryName);
		String queryNameId = constructQueryNameId(queryName, id);
		
		if (queryResult.length() >= 0 && benchmarkingState.get()) {
			if (startedDuringBenchmarkPhase) {
				if (reportSuccess) {
					Statistics.aggregateQueriesArray[queryNumber - 1].reportSuccess(queryExecutionTimeMs);
					Statistics.totalAggregateQueryStatistics.reportSuccess(queryExecutionTimeMs);
					logBrief(queryNameId, queryType, queryResult, "", queryExecutionTimeMs);
				} else {				
					Statistics.aggregateQueriesArray[queryNumber - 1].reportFailure();
					Statistics.totalAggregateQueryStatistics.reportFailure();
					logBrief(queryNameId, queryType, queryResult, ", query has timed out!", queryExecutionTimeMs);
				}
			} else {
				if (queryExecutionTimeMs > 0) {
					LOGGER.info("\tQuery : " + queryName + ", time : " + queryExecutionTimeMs + " ms, queryResult.length : " + queryResult.length() + ", has been started during the warmup phase, it will be ignored in the benchmark result!");
					logBrief(queryNameId, queryType, queryResult, ", has been started during the warmup phase, it will be ignored in the benchmark result!", queryExecutionTimeMs);
				} else {
					LOGGER.warn("\tQuery : " + queryName + ", time : " + queryExecutionTimeMs + " ms, queryResult.length : " + queryResult.length() + ", has failed to execute... possibly query timeout has been reached!");					
					logBrief(queryNameId, queryType, queryResult, ", has failed to execute... possibly query timeout has been reached!", queryExecutionTimeMs);
				}
			}
		}
		
		LOGGER.info("\n*** Query [" + queryNameId + "], execution time : " + queryExecutionTimeMs + " ms\n" + queryString + "\n---------------------------------------------\n*** Result for query [" + queryNameId + "]" + " : \n" + "Length : " + queryResult.length() + "\n" + queryResult + "\n\n");
	}
	
	private void logBrief(String queryId, QueryType queryType, String queryResult, String appendString, long queryExecutionTimeMs) {
		StringBuilder reportSb = new StringBuilder();
		reportSb.append(String.format("\t[%s] Query executed, execution time : %d ms %s", queryId, queryExecutionTimeMs, appendString));
		if (queryType == QueryType.SELECT || queryType == QueryType.CONSTRUCT || queryType == QueryType.DESCRIBE) {
			reportSb.append(", characters returned : " + queryResult.length());
		}
		
		BRIEF_LOGGER.info(reportSb.toString());		
	}
	
	private int getQueryNumber(String queryName) {
		return Integer.parseInt(queryName.substring(queryName.indexOf(Statistics.AGGREGATE_QUERY_NAME) + Statistics.AGGREGATE_QUERY_NAME.length(), queryName.indexOf(".")));
	}
	
	private String constructQueryNameId(String queryName, long id) {
		StringBuilder queryId = new StringBuilder();
		queryId.append(queryName);
		queryId.append(", id:");
		queryId.append("" + id);
		return queryId.toString();
	}
}