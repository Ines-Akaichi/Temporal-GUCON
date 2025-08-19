package gucon.framework.dbsms;

import java.io.IOException;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class main {

	public static void main(String[] args) throws IOException {
		
		
		  // TODO Auto-generated method stub
	      String graphdbDir = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/GraphDB";  // Local directory
	      String tdbDir = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/TDB2";
	      String repoId = "repo-defaults";      
	      String ttlPath = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/kb.ttl";  // Turtle file to load
	      // kbContext: named graph for my loaded graph
	      // graph db load different names graphs describing default ontologies
	      IRI  kbContextIRI = SimpleValueFactory.getInstance().createIRI("http://example.org/kb");
	      String kbContextIRIString ="http://example.org/kb";

	      String label="My Embedded GraphDB";
	      
	      TDB2StorageProvider tdb2Provider = new TDB2StorageProvider (tdbDir);
	      tdb2Provider.loadFromTurtle(ttlPath,kbContextIRIString);
	      tdb2Provider.getModel(kbContextIRIString).listStatements().forEachRemaining(System.out::println);
	      tdb2Provider.close();
	      
	      GraphDBStorageProvider graphDBProvider = new GraphDBStorageProvider(graphdbDir,repoId,label);
	      graphDBProvider.loadFromTurtle(ttlPath,kbContextIRIString);
	      graphDBProvider.getModel(kbContextIRIString).listStatements().forEachRemaining(System.out::println);
	      graphDBProvider.close();

	}

}
