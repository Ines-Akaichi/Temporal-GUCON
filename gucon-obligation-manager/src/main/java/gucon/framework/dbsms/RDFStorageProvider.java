package gucon.framework.dbsms;


import org.eclipse.rdf4j.model.IRI;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;


public interface RDFStorageProvider {
	
    void loadFromTurtle(String filePath, String contextIRI);
    void close() throws IOException; // To clean up connections
	Model getModel(String contextIRI);
}
