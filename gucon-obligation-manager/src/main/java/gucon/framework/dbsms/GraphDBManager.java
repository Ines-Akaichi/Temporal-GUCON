package gucon.framework.dbsms;

import com.ontotext.trree.config.OWLIMSailSchema;
import com.ontotext.trree.graphdb.GraphDBRepositoryConfig;

import org.eclipse.rdf4j.common.io.FileUtil;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.*;
import org.eclipse.rdf4j.repository.config.*;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.repository.sail.config.SailRepositorySchema;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;

import java.io.*;
import java.util.Map;
public class GraphDBManager implements Closeable{
    private  LocalRepositoryManager repositoryManager;
    

    public GraphDBManager(String baseDir) throws RepositoryException {
        this.repositoryManager = new LocalRepositoryManager(new File(baseDir));
        this.repositoryManager.init();
    }
    
    
    public void createRepository(String repositoryId, String repositoryLabel) throws RepositoryConfigException {
        if (repositoryManager.hasRepositoryConfig(repositoryId)) {
            return; // already exists
        }

        TreeModel graph = new TreeModel();

        try (InputStream config = GraphDBManager.class.getResourceAsStream("/repo-defaults.ttl")) {
            RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
            parser.setRDFHandler(new StatementCollector(graph));
            parser.parse(config, RepositoryConfigSchema.NAMESPACE);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        ValueFactory vf = SimpleValueFactory.getInstance();

        // Find the repository node
        Resource repoNode = Models.subject(graph.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY))
                                    .orElseThrow(() -> new IllegalStateException("No repository node found in config"));

        // Set repository ID and label
        graph.add(repoNode, RepositoryConfigSchema.REPOSITORYID, vf.createLiteral(repositoryId));
        if (repositoryLabel != null) {
            graph.add(repoNode, RDFS.LABEL, vf.createLiteral(repositoryLabel));
        }

        RepositoryConfig config = RepositoryConfig.create(graph, repoNode);
        repositoryManager.addRepositoryConfig(config);
    }
    
    
    public void createRepository(String repositoryId, String repositoryLabel, Map<String, String> overrides)
            throws IOException, RDFParseException, RDFHandlerException, RepositoryConfigException {

        if (repositoryManager.hasRepositoryConfig(repositoryId)) return;

        TreeModel graph = new TreeModel();

        try (InputStream config = GraphDBManager.class.getResourceAsStream("/repo-defaults.ttl")) {
            RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
            parser.setRDFHandler(new StatementCollector(graph));
            parser.parse(config, RepositoryConfigSchema.NAMESPACE);
        }

        ValueFactory vf = SimpleValueFactory.getInstance();
        Resource repoNode = Models.subject(graph.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY)).orElse(null);

        graph.add(repoNode, RepositoryConfigSchema.REPOSITORYID, vf.createLiteral(repositoryId));
        if (repositoryLabel != null) graph.add(repoNode, RDFS.LABEL, vf.createLiteral(repositoryLabel));

        if (overrides != null) {
            Resource configNode = (Resource) Models.object(graph.filter(null, SailRepositorySchema.SAILIMPL, null)).orElse(null);
            for (Map.Entry<String, String> entry : overrides.entrySet()) {
                IRI key = vf.createIRI(OWLIMSailSchema.NAMESPACE + entry.getKey());
                Literal value = vf.createLiteral(entry.getValue());
                graph.remove(configNode, key, null);
                graph.add(configNode, key, value);
            }
        }

        RepositoryConfig config = RepositoryConfig.create(graph, repoNode);
        repositoryManager.addRepositoryConfig(config);
    }

    public Repository getRepository(String repoId) {
        if (isRepositoryLocked(repoId)) {
            throw new IllegalStateException("Repository is currently locked: " + repoId);
        }
        return repositoryManager.getRepository(repoId);
    }
    
    private boolean isRepositoryLocked(String repoId) {
        File lockFile = new File(repositoryManager.getBaseDir(), "repositories/" + repoId + "/.lock");
        return lockFile.exists();
    }

    public boolean repositoryExists(String repoId) {
        return repositoryManager.hasRepositoryConfig(repoId);
    }
    
    @Override
    public void close() {
        repositoryManager.shutDown();
    }


	public void shutDown() {
		// TODO Auto-generated method stub
		repositoryManager.shutDown();
	}
}
