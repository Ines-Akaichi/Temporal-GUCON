package gucon.framework.obligations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import gucon.framework.dbsms.GraphDBStorageProvider;
import gucon.framework.dbsms.RDFStorageProvider;
import gucon.framework.dbsms.TDB2StorageProvider;
import gucon.utils.Namespace;
import gucon.utils.NamespaceUtils;

public class KnowledgeBaseManager {
	  private final RDFStorageProvider backend;
	  private  Model model;
	  private String contextIRI;
	  private String nameSpaceDeclaration;
	    /**
	     * Loads the KB model from a file (e.g., TTL, RDF/XML).
	     *
	     * @param filepath Path to the RDF file.
	     */
	    public KnowledgeBaseManager(RDFStorageProvider backend,String contextIRI) {
	    	
	    	this.backend=backend;
	    	this.model=backend.getModel(contextIRI);	
	    	this.contextIRI=contextIRI;
	    	this.nameSpaceDeclaration=getNameSpaceDeclaration();
	        // Add specific namespace prefixes here
	    	Namespace.registerAll(this.model);

	    }

	    /**
	     * Gets the full KB model.
	     */
	    public Model getModel(String contextIRI) {
	    	return this.model;
 
	   }

	    /**
	     * Generates a snapshot (Model) at a given time T.
	     *
	     * @param timeT The snapshot time as Instant.
	     * @return Model representing the snapshot.
	     */
	    public Model generateSnapshot(String timeString) {
	        Model snapshot = ModelFactory.createDefaultModel();

	        // Build the SPARQL query using Namespace constants
	        String query =
	            "PREFIX " + Namespace.XSD_PREFIX + ": <" + Namespace.XSD_URI + ">\n" +
	            "PREFIX " + Namespace.GUCON_PREFIX + ": <" + Namespace.GUCON_URI + ">\n" +
	            "CONSTRUCT {\n" +
	            "  ?s ?p ?o .\n" +
	            "  <<?e ?a ?r>> " + Namespace.GUCON_PREFIX + ":executionTime ?t .\n" +
	            "}\n" +
	            "WHERE {\n" +
	            "  { ?s ?p ?o }\n" +
	            "  UNION {\n" +
	            "    <<?e ?a ?r>> " + Namespace.GUCON_PREFIX + ":executionTime ?t .\n" +
	            "    FILTER (?t <= \"" + timeString + "\"^^" + Namespace.XSD_PREFIX + ":long)\n" +
	            "  }\n" +
	            "}";

	        try (QueryExecution qexec = QueryExecutionFactory.create(QueryFactory.create(query), model)) {
	            snapshot.add(qexec.execConstruct());
	        }

	        return snapshot;
	    }

	    



	    public  String getNameSpaceDeclaration() {
			return NamespaceUtils.getNameSpaceDeclaration (this.model);
		}
	    
	   

		public void setModel(Model model) {
			this.model = model;
		}
		


		public String getContextIRI() {
			return contextIRI;
		}

		public void setContextIRI(String contextIRI) {
			this.contextIRI = contextIRI;
		}

	public static void main(String[] args) throws Exception {
	  String graphdbDir = "C:/dbsm/graphdb";  // Local directory
      String tdbDir = "C:/dbsm/tdb";
      String repoId = "repo-defaults";      
      String ttlPath = "C:/dbsm/data/kb.ttl";  // Turtle file to load
      // kbContext: named graph for my loaded graph
      // graph db load different names graphs describing default ontologies
      IRI  kbContextIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/kb");
      String kbContextIRIString ="http://example.org/kb";
      String label="My Embedded GraphDB";
      
      TDB2StorageProvider tdb2Provider = new TDB2StorageProvider (tdbDir);
      tdb2Provider.loadFromTurtle(ttlPath,kbContextIRIString);

      
      GraphDBStorageProvider graphDBProvider = new GraphDBStorageProvider(graphdbDir,repoId,label);
      graphDBProvider.loadFromTurtle(ttlPath,kbContextIRIString);
 
		KnowledgeBaseManager kbManager = new KnowledgeBaseManager(graphDBProvider,kbContextIRIString);
		
		Model test= kbManager.getModel(kbContextIRIString);  
		
		test.write(System.out,"TURTLE");

		String dateTime ="2025-08-03T10:30:00.000+02:00";
		Model snapshot = kbManager.generateSnapshot(dateTime);
		snapshot.write(System.out,"TURTLE");
		//System.out.println(kbManager.getDefaultNamespace());
		
	
	    tdb2Provider.close();
	    graphDBProvider.close();
	    
	    }
}
