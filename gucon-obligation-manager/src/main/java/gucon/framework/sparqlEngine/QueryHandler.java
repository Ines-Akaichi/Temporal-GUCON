package gucon.framework.sparqlEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

public class QueryHandler {
	
	


	   
	   
	   public static String createConstructSparql (String template, String WhereClause)
		{
		   // List<String> prefixesList= extractPrefixes (model);
		    //String prefixes = String.join("\n", prefixesList);
			String constructQuery = "CONSTRUCT {"+ template + "} where { " + WhereClause + "}";
				return constructQuery;
		}
	   
	   
	   public static String createConstructSparql (String prefix,String template, String WhereClause)
		{
		   // List<String> prefixesList= extractPrefixes (model);
		    //String prefixes = String.join("\n", prefixesList);
			String constructQuery = prefix+ "CONSTRUCT {"+ template + "} where { " + WhereClause + "}";
				return constructQuery;
		}
	   
	   public static String createSelectCountQuery(String namespace, String whereClause) {
		    return namespace + "\n" +
		           "SELECT (COUNT(*) AS ?count)\n" +
		           "WHERE {\n" + whereClause + "\n}";
		}
	 	   
}
