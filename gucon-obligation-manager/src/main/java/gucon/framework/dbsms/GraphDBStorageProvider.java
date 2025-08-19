package gucon.framework.dbsms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.algebra.Lang;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;


public class GraphDBStorageProvider implements RDFStorageProvider {
	
	private final GraphDBManager graphDB ;
    private final Repository repository;
    private final RepositoryConnection conn;
    //private final IRI contextName;
    
    public GraphDBStorageProvider(String directoryPath, String repoId, String label) {


      try {
         this.graphDB = new GraphDBManager(directoryPath);

         this.graphDB.createRepository(repoId, label);
      
    	 this.repository  = this.graphDB.getRepository(repoId);
         this.conn = repository.getConnection();


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize GraphDBStorageProvider", e);
        }

    }
    
     

    @Override
	//contextName is the name of the named graph of the specific KB 
	public void loadFromTurtle(String filePath, String contextName) {
        IRI contextNameIRI = SimpleValueFactory.getInstance().createIRI(contextName);
		conn.begin();
    	conn.clear(contextNameIRI); // Clears all existing triples
        try {
			conn.add(new FileInputStream(filePath), "urn:base",RDFFormat.TURTLE, contextNameIRI);
		} catch (RDFParseException | RepositoryException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        conn.commit();  // explicitly commit just in case
        System.out.println("Turtle data size loaded " + conn.size());
      
	}


	
	

    @Override
    public void close() throws IOException {
        try {
            if (conn != null && conn.isOpen()) {
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }

        try {
            if (graphDB != null) {
                graphDB.shutDown();  // essential to release the lock
            }
        } catch (Exception e) {
            System.err.println("Error shutting down GraphDB: " + e.getMessage());
        }
    }



	@Override

	public Model getModel(String contextName) {
	    IRI contextNameIRI = SimpleValueFactory.getInstance().createIRI(contextName);
	    if (!conn.isOpen()) {
	        System.err.println("Repository connection is closed!");
	    }

	    // Write statements into ByteArrayOutputStream as Turtle* (triples only)
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Rio.write(conn.getStatements(null, null, null, false, contextNameIRI), out, RDFFormat.TURTLESTAR);

	    // Create Jena model and read triples from ByteArrayInputStream
	    Model jenaModel = ModelFactory.createDefaultModel();
	    jenaModel.read(new ByteArrayInputStream(out.toByteArray()), null, "TURTLE");

	    // Now: manually copy prefixes from RDF4J repository to Jena model
	    // RDF4J RepositoryConnection#getNamespaces() returns prefixes as Map<String,String>
	    try {
	        // conn is your RepositoryConnection
	        for (Namespace ns : conn.getNamespaces()) {
	            jenaModel.setNsPrefix(ns.getPrefix(), ns.getName());
	        }
	    } catch (Exception e) {
	        System.err.println("Failed to get prefixes from RDF4J repository: " + e.getMessage());
	    }

	    if (jenaModel.isEmpty()) {
	        System.out.println("⚠️ Warning: The Jena model is empty after reading.");
	    } else {
	        System.out.println("✅ Jena model successfully loaded with " + jenaModel.size() + " triples.");
	    }
	    System.out.println("size of prefixes " + jenaModel.getNsPrefixMap().size());

	    return jenaModel;
	}






   
}
