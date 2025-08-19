package gucon.framework.dbsms;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;

public class TDB {
	 // Step 1: Load RDF data from a Turtle file
    public static void loadTurtleIntoTDB(String tdbDir, String turtleFilePath) {
         Dataset dataset = TDB2Factory.connectDataset(tdbDir);

        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            RDFDataMgr.read(model, turtleFilePath); // loads TTL into default graph
            dataset.commit();
            System.out.println("Turtle file loaded into TDB.");
        } catch (Exception e) {
            dataset.abort();
            e.printStackTrace();
        } finally {
            dataset.end();
        }
    }

    // Step 2: Query the TDB store using SPARQL
    public static void queryTDB(String tdbDir) {
        Dataset dataset = TDB2Factory.connectDataset(tdbDir);

        dataset.begin(ReadWrite.READ);
        try {
            String sparql = "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 10";
            try (QueryExecution qExec = QueryExecutionFactory.create(sparql, dataset)) {
                ResultSet results = qExec.execSelect();
                ResultSetFormatter.out(System.out, results);
            }
        } finally {
            dataset.end();
        }
    }
	
	 public static void main(String[] args) {
		 
		   // Path to the TDB2 directory (created if it doesn't exist)
	        String tdbDir = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/TDB2";

	        // Load Turtle data into TDB
	        loadTurtleIntoTDB(tdbDir, "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/kb.ttl");
		 

	        // Query data from TDB
	        queryTDB(tdbDir);
	  
	 }
}
