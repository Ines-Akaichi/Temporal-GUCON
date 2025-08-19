package gucon.framework.obligations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import gucon.framework.dbsms.TDB2StorageProvider;
import gucon.utils.Namespace;
import gucon.utils.NamespaceUtils;
import gucon.utils.PolicyUtils;

public class PolicyManager {
	
	 private List <Rule> rules;
	 private  String namespaceDeclaration;

	    public PolicyManager(String rulesFilePath) throws IOException {
	    this.rules = PolicyUtils.readRulesFromPolicy(rulesFilePath);
	    this.namespaceDeclaration = PolicyUtils.getNamespaceDeclaration();
	}

		public PolicyManager(KnowledgeBaseManager kbManager, String rulesFilePath) throws IOException {
	        this.rules = PolicyUtils.readRulesFromPolicy(rulesFilePath);
	        String RulenameSpaceDeclaration = PolicyUtils.getNamespaceDeclaration(); // get from rulesPath
	        String kbnameSpaceDeclaration = kbManager.getNameSpaceDeclaration(); // get from KB
	        // Combine and remove duplicates 
	        this.namespaceDeclaration = NamespaceUtils.mergeNamespaceDeclarations(kbnameSpaceDeclaration, RulenameSpaceDeclaration);
	       // System.out.println("declaration  "+ nameSpaceDeclaration);
	    }
	    
	
	    public PolicyManager() {
			super();
		}

		public List<Rule> getRules() {
			return rules;
		}

		public void setRules(List<Rule> rules) {
			this.rules = rules;
		}

		public List<Rule> loadRules(){
	        for (Rule rule : rules) {
	            String conditionPattern = rule.getConditionPattern();
	            String actionPattern = rule.getActionPattern();

	            // Extract quoted triple from action
	            String quotedTriple = extractQuotedTriple(actionPattern);

	            // Create full condition pattern by adding OPTIONAL triple
	            String fullConditionPattern = addOptionalAtTimeToCondition(rule,conditionPattern, quotedTriple);

	            // Build and set both queries separately
	            rule.setConditionQuery(QueryFactory.create(buildSPARQLQuery(conditionPattern)));  // original
	            rule.setActionQuery(QueryFactory.create(buildSPARQLQuery(actionPattern)));        // original
	            rule.setFullConditionPattern(fullConditionPattern);                               // enriched
	            rule.setFullConditionQuery(QueryFactory.create(buildSPARQLQuery(fullConditionPattern)));
	        }
	    	return rules;
	    }
	    
	    public String getNameSpaceDeclaration() {
	        return namespaceDeclaration;
	    }
	    
	    
	    
	    public   String buildSPARQLQuery(String pattern) {
	        return namespaceDeclaration + "\nSELECT * WHERE { " + pattern + " }";
	    }
	    
	
		public  String buildConstructSparql (String template, String WhereClause)
	    {
			   // List<String> prefixesList= extractPrefixes (model);
			    //String prefixes = String.join("\n", prefixesList);
				String constructQuery = namespaceDeclaration+ "\n CONSTRUCT {"+ template + "} where { " + WhereClause + "}";
			return constructQuery;
	    }
		   
		public String addOptionalAtTimeToCondition(Rule rule, String conditionPattern, String quotedTriple) {
		    if (quotedTriple == null) return conditionPattern;

		    // Hash the triple string to create a unique variable name
		    int hash = quotedTriple.hashCode();

		    // Store atTime var
		    String atTimeVar = "?atTime_" + Integer.toHexString(hash);
		    rule.setAtTimeVarName(atTimeVar.substring(1)); // store without "?" for Var.alloc later

		    // Add optional gucon:executionTime triple using predefined namespace
		    String optionalAtTime = String.format("OPTIONAL { %s <%sexecutionTime> %s . }",
		            quotedTriple, Namespace.GUCON_URI, atTimeVar);

		    return conditionPattern + " " + optionalAtTime;
		}

		public String createAtTimeToCondition(Rule rule, String quotedTriple) {
		    // Use the namespace constant for gucon:executionTime
		    return String.format(" %s <%sexecutionTime> %s . ",
		            quotedTriple, Namespace.GUCON_URI, rule.getAtTimeVarName());
		}


	    public static String extractQuotedTriple(String actionPattern) {
	        // Match quoted triple, e.g., << ?e gucon:share ?d >>
	        String regex = "<<\\s*(\\?\\w+)\\s+(\\S+)\\s+(\\?\\w+)\\s*>>";
	        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
	        java.util.regex.Matcher matcher = pattern.matcher(actionPattern);
	        
	        if (matcher.find()) {
	            return String.format("<< %s %s %s >>", matcher.group(1), matcher.group(2), matcher.group(3));
	        }
	        return null;
	    }

	    
	    public static void main(String[] args) throws IOException {
	      String tdbDir = "C:/dbsm/tdb";
	      String ttlPath = "C:/dbsm/data/kb.ttl";  // Turtle file to load
	      String kbContextIRIString ="http://example.org/kb";

    	  TDB2StorageProvider tdb2Provider = new TDB2StorageProvider (tdbDir);
          tdb2Provider.loadFromTurtle(ttlPath,kbContextIRIString);
	          
	          
	      String kbPath = "C:/Users/Administrator/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/kb.ttl";
	      String rulesPath = "C:/Users/Administrator/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/rule.ttl";
	        
	           
		    KnowledgeBaseManager kbManager = new KnowledgeBaseManager(tdb2Provider,kbContextIRIString);
		    //kbManager.getModel(kbContextIRIString);
		    PolicyManager ruleManager = new PolicyManager(kbManager, rulesPath);

	        // Load rules
	        List<Rule> rules = ruleManager.loadRules();

	        for (Rule rule : rules) {
	            System.out.println("Loaded Rule: " + rule.getRuleID());
	            System.out.println("Condition SPARQL Query:");
	            System.out.println(rule.getConditionQuery());
	            System.out.println("Action SPARQL Query:");
	            System.out.println(rule.getActionQuery());
	            System.out.println("Full COndition SPARQL Query:");
	            System.out.println(rule.getFullConditionQuery());
	            System.out.println("----------");
	        }
	    	
	    
	    }
}
