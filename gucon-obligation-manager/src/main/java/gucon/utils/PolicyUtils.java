package gucon.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;

import gucon.framework.obligations.Rule;


public class PolicyUtils {
	
    // he extracted namespace string
    private static String namespaceDeclaration;
    
    

    public static String getNamespaceDeclaration() {
        return namespaceDeclaration;
    }
    
	
	
    public static List<Rule> readRulesFromPolicy(String filepath) throws IOException {    
        List<Rule> rulesList = new ArrayList<>();
        Model model = ModelFactory.createDefaultModel();

        try (InputStream in = new FileInputStream(filepath)) {
            model.read(in, null, "TTL");
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filepath);
            throw e;
        }

        namespaceDeclaration = NamespaceUtils.getNameSpaceDeclaration(model);

        // Iterate over all resources of type ucp:ObligationRule
        Resource ruleClass = model.createResource(Namespace.UCP_URI + "ObligationRule");
        StmtIterator stmtIter = model.listStatements(null, RDF.type, ruleClass);

        Property isPartOfPolicy = model.createProperty(Namespace.UCP_URI + "isPartOfPolicy");

        while (stmtIter.hasNext()) {
            Statement stmt = stmtIter.nextStatement();
            Resource ruleResource = stmt.getSubject();

            String ruleID = ruleResource.getURI();
            String conditionPattern = null;
            String deonticOperator = null;
            String actionPattern = null;
            String policyID = null;

            // Get associated properties using predefined prefixes
            Property hasCondition = model.createProperty(Namespace.UCP_URI + "hasConditionPattern");
            Property hasDeonticOperator = model.createProperty(Namespace.UCP_URI + "hasDeonticOperator");
            Property hasActionPattern = model.createProperty(Namespace.UCP_URI + "hasActionPattern");

            if (ruleResource.hasProperty(hasCondition)) {
                conditionPattern = ruleResource.getProperty(hasCondition).getObject().toString();
                
            }

            if (ruleResource.hasProperty(hasDeonticOperator)) {
                deonticOperator = ruleResource.getProperty(hasDeonticOperator).getObject().toString();
            }

            if (ruleResource.hasProperty(hasActionPattern)) {
                actionPattern = ruleResource.getProperty(hasActionPattern).getObject().toString();
            }

            // Extract policy ID
            if (ruleResource.hasProperty(isPartOfPolicy)) {
                RDFNode policyNode = ruleResource.getProperty(isPartOfPolicy).getObject();
                if (policyNode.isResource()) {
                    policyID = policyNode.asResource().getURI();  // or .getLocalName() if you prefer
                }
            }

            Rule rule = new Rule(conditionPattern, deonticOperator, actionPattern, ruleID, policyID);
            rulesList.add(rule);
        }

        return rulesList;
    }

	
 

}
