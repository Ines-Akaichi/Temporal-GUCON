package gucon.framework.sparqlEngine;

import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;



public class QueryEngine {
	/*public QueryEngine()
	{
	}	*/
		
	public static Model construct (Model model, ParameterizedSparqlString pss)
	{
        Model constructedModel =null;

		Query query = pss.asQuery();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
        constructedModel = qexec.execConstruct();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
		return constructedModel;
	}

	public static Model construct (Model model, String qs)
	{
        Model constructedModel =null;

		Query query = QueryFactory.create(qs);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
        constructedModel = qexec.execConstruct();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
		return constructedModel;
	}
	
	public static Model construct (Dataset dataset, String qs)
	{
        Model constructedModel =null;

		Query query = QueryFactory.create(qs);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
        constructedModel = qexec.execConstruct();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
		return constructedModel;
	}
	
	
	
	public static List<QuerySolution> select(Dataset dataset, String qs) throws Exception
	{	
		List<QuerySolution> list = null;
	  	Query sparql = QueryFactory.create(qs);
//	  	System.out.println(sparql.serialize());
	   
		QueryExecution qe = QueryExecutionFactory.create(sparql, dataset);
		
		//Iterate through the results
		ResultSet results = qe.execSelect();
		
		ResultSetFormatter.out(System.out, results);
		
		if(results.hasNext())
		{
			list = ResultSetFormatter.toList(results);
		}
		return list;
	}
	
	public static Boolean ask(Model dataset, Query sparql) throws Exception
	{	
	  	//Query sparql = QueryFactory.create(qs);
	   
		QueryExecution qe = QueryExecutionFactory.create(sparql, dataset);
		
		// Execute the query
		return qe.execAsk();
		
	}
	public Boolean ask(Dataset dataset, String qs) throws Exception
	{	
	  	Query sparql = QueryFactory.create(qs);
	   
		QueryExecution qe = QueryExecutionFactory.create(sparql, dataset);
		
		// Execute the query
		return qe.execAsk();
		
	}
	

	
}
