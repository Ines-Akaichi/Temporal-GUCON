package gucon.framework.dbsms;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.config.RepositoryConfigUtil;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.ontotext.trree.graphdb.GraphDBRepositoryConfig;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;


import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;


public class GraphDB {
	public static void main(String[] args) throws Exception {
        String baseDir = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/GraphDB";  // Local directory
        String repoId = "repo-defaults";      
        String ttlPath = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/kb.ttl";  // Turtle file to load
        // kbContext: named graph for my loaded graph
        // graph db load different names graphs describing default ontologies
        IRI  kbContextIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/kb");


    try (GraphDBManager graphDB = new GraphDBManager(baseDir)) {

        graphDB.createRepository(repoId, "My Embedded GraphDB");

        
        // Load data
        Repository repo = graphDB.getRepository(repoId);
        
        System.out.println("Repository ID: " + repo.getDataDir());
        try (RepositoryConnection conn = repo.getConnection()) {
        	conn.begin();
        	conn.clear(); // Clears all existing triples
          
            conn.add(new FileInputStream(ttlPath), "urn:base",RDFFormat.TURTLE,kbContextIRI);
            conn.commit();  // explicitly commit just in case
            System.out.println("Turtle data size " + conn.size());

            System.out.println("Turtle data loaded.");
            
            // print loaded result
            //false: Don't include inferred triples.
            // show only loaded context 
           conn.getStatements(null, null, null, false, kbContextIRI).forEach(statement -> {
                System.out.println(statement.getSubject() + " " +
                                  statement.getPredicate() + " " +
                                  statement.getObject());
          });  }

        // Query data
        try (RepositoryConnection conn = repo.getConnection()) {
        	String kbContextIRIString = kbContextIRI.toString();
			String queryString = String.format("""
			    SELECT * 
			    FROM <%s>
			    WHERE { ?s ?p ?o }
			    LIMIT 10
			""", kbContextIRIString);
			
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    System.out.println("Result:");
                    bindingSet.getBindingNames().forEach(var -> {
                        System.out.println("  " + var + " = " + bindingSet.getValue(var));
                    });
                    System.out.println();
                }
            }
            
                		
                		
        
        }
        
        //System.out.println("Repository ID: " + repo.getDataDir());
    }
        
	}
}
