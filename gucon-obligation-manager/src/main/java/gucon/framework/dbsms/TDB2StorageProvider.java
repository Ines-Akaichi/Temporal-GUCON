package gucon.framework.dbsms;

import java.io.File;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.eclipse.rdf4j.model.IRI;

public class TDB2StorageProvider implements RDFStorageProvider {
    private final Dataset dataset;

    public TDB2StorageProvider(String directoryPath) {
    	
        deleteDirectory(new File(directoryPath));
        this.dataset = TDB2Factory.connectDataset(directoryPath); // persistent storage
        //System.out.println("Creating TDB2StorageProvider for: " + directoryPath);
    }
    
    
    
	@Override
	public void loadFromTurtle(String filePath, String graphIRI) {
		// TODO Auto-generated method stub
		dataset.begin(ReadWrite.WRITE);
		try {
			Model namedModel = dataset.getNamedModel(graphIRI);
        RDFDataMgr.read(namedModel, filePath,Lang.TURTLE);
        
        dataset.commit(); // persist the changes
        //System.out.println("the turtle KB is loaded");
		 } 
		catch (Exception e) {
		        dataset.abort(); // rollback on error
		        throw e; // or handle/log the exception
		 }
		finally {
		    dataset.end();
		}
	}

	@Override
	public Model getModel(String graphIRI) {
	    Model copy = ModelFactory.createDefaultModel();

	    dataset.begin(ReadWrite.READ);
	    try {
	        Model tdbModel = dataset.getNamedModel(graphIRI);
	        copy.add(tdbModel);  // copy content into an in-memory model
	    } finally {
	        dataset.end();  // safe to end transaction
	    }

	    return copy;  // in-memory, safe to use after transaction ends
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
        dataset.close();
	}


	   private static void deleteDirectory(File directory) {
	        if (directory.exists()) {
	            File[] files = directory.listFiles();
	            if (files != null) {
	                for (File file : files) {
	                    if (file.isDirectory()) {
	                        deleteDirectory(file);
	                    } else {
	                        file.delete();
	                    }
	                }
	            }
	            directory.delete();
	        }
	    }



}
