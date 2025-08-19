package gucon.framework.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.yaml.snakeyaml.Yaml;

import gucon.framework.obligations.PolicyManager;
import gucon.framework.obligations.Rule;
import gucon.framework.sparqlEngine.QueryHandler;
import gucon.utils.Namespace;
import gucon.utils.NamespaceUtils;
import gucon.utils.PolicyUtils;

public class Generator {

    static String policyName;
	private  static Integer  ruleIDO =0;
	private Model modelRules = ModelFactory.createDefaultModel();
	private Model modelKB = ModelFactory.createDefaultModel();
	private Model inModel =  ModelFactory.createDefaultModel();
	 // cache for rule matches 
    private Map<String, Set<Statement>> ruleToMatchesCache = new HashMap<>();

   public Generator(String dataFilePath) {
			super();
			RDFDataMgr.read(this.inModel , dataFilePath, Lang.TURTLE);
			modelKB.add(inModel);
        	System.out.println("size of input model " +modelKB.size());
   }
   
   
	   
  public Model getModelRules() {
		return modelRules;
	}


	public void setModelRules(Model modelRules) {
		this.modelRules = modelRules;
	}
	
	
	
	public Model getModelKB() {
		return modelKB;
	}
	
	
	
	public void setModelKB(Model modelKB) {
		this.modelKB = modelKB;
	}

	
	
	// <--------- query handling --------->
	
	
    private Map<String, Set<Statement>> computeRuleMatches(List<Rule> rules) {
        if (!ruleToMatchesCache.isEmpty()) return ruleToMatchesCache;
        for (Rule rule : rules) {
            Query query = rule.getFullConditionQuery();
            Model result = executeConstructQuery(modelKB, query);
            Set<Statement> matches = result.listStatements().toSet();
            ruleToMatchesCache.put(rule.getRuleID().trim(), matches);
        }
        return ruleToMatchesCache;
    }



	public Resource createRuleResource(String ruleID, String conditionPattern, String deonticOperator, String actionPattern) {
	    // Create the rule resource
	Resource rule = modelRules.createResource(Namespace.EX_URI + ruleID)
	        .addProperty(modelRules.createProperty(Namespace.UCP_URI + "isPartOfPolicy"),
	                     modelRules.createResource(Namespace.EX_URI + policyName))
	        .addProperty(RDF.type, modelRules.createResource(Namespace.UCP_URI + "ObligationRule"))
	        .addLiteral(modelRules.createProperty(Namespace.UCP_URI + "hasConditionPattern"),
	                    modelRules.createLiteral(conditionPattern))
	        .addProperty(modelRules.createProperty(Namespace.UCP_URI + "hasDeonticOperator"),
	                     modelRules.createResource(Namespace.UCP_URI + deonticOperator))
	        .addLiteral(modelRules.createProperty(Namespace.UCP_URI + "hasActionPattern"),
	                        modelRules.createLiteral(actionPattern));
	
	    return rule;
	}


	/** Extract CONSTRUCT queries from rule model
	 */
	public static String extractConditionWithoutBinds(String condition) {
		  // Remove all BIND(...) statements (full lines or inline)
	    String noBinds = condition.replaceAll("(?m)^\\s*BIND\\s*\\(.*?\\)\\s*\\.?", "");
	    
	    // Remove extra blank lines
	    return noBinds.replaceAll("(?m)^\\s*$\\n?", "").trim();
	}
	
	public static String removeOptionalClause(String input) {
	    // This regex removes OPTIONAL { ... } blocks (including newlines inside)
	    return input.replaceAll("(?s)OPTIONAL\\s*\\{.*?\\}", "").trim();
	}

   public String createOptionalAtTimeCondition( String conditionPattern,String AtTimeCondition) {
      
        String optionalAtTime = String.format("OPTIONAL { %s  }", AtTimeCondition);
        
        return  conditionPattern + " " + optionalAtTime ;
    }
	    
    public String createAtTimeCondition(String quotedTriple) {
    	  // Hash the triple string to create a unique variable name
        int hash = quotedTriple.hashCode();
        // store atTime var
        String atTimeVar = "?atTime_" + Integer.toHexString(hash);
        //rule.setAtTimeVarName(atTimeVar.substring(1)); // store without "?" for Var.alloc later
        String AtTimeCondition = String.format(" %s <%sexecutionTime> %s . ", quotedTriple, Namespace.GUCON_URI, atTimeVar);
        return AtTimeCondition;
    }

    
	/** Execute a CONSTRUCT query on a model and return the resulting model */
	private Model executeConstructQuery(Model dataModel, Query query) {
	    Model resultModel = ModelFactory.createDefaultModel();

	    try (QueryExecution qexec = QueryExecutionFactory.create(query, dataModel)) {
	        resultModel = qexec.execConstruct();
	    }
	    return resultModel;
	}
	
	
	
	private List<Rule> extractSparqlQueriesFromRules(String ruleFilePath) throws IOException {
	    List<Rule> ruleQueries = new ArrayList<>();
	    PolicyManager manager = new PolicyManager (ruleFilePath);
	    List <Rule> rules = manager.getRules();

	    for (Rule rule : rules) {
            String conditionPattern = rule.getConditionPattern();
            String actionPattern = rule.getActionPattern();
            // Extract quoted triple from action
            String quotedTriple = PolicyManager.extractQuotedTriple(actionPattern);
            String ruleID = rule.getRuleID();
            // Extract condition without bind
            
            String conditionWOBinds =  extractConditionWithoutBinds(conditionPattern);
            String atTimeCondition = createAtTimeCondition (quotedTriple);
            // Create full condition pattern by adding OPTIONAL triple
            String whereClause = createOptionalAtTimeCondition(conditionWOBinds,atTimeCondition);
           // System.out.println("whereClause "+whereClause);
            String template = conditionWOBinds + " " +atTimeCondition;
           // System.out.println("template "+template);

            try {
                Query fullConditionPatternQuery = QueryFactory.create(manager.buildConstructSparql(template,whereClause));
                if (fullConditionPatternQuery.isConstructType()) 
                {
    	            ruleQueries.add(new Rule(ruleID, fullConditionPatternQuery));	        	

                }     
            }
         catch (QueryParseException e) {
            System.err.println("Failed to parse query from rule condition: " + e.getMessage());
        }
        }

	    return ruleQueries;
	}

	
	public static String formatDateTime(OffsetDateTime dt) {
	    return dt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	
	// <-------- generate rules -------------->
	
	
	List<Resource>  generateDataDrivenRules(List<String> actions,  OffsetDateTime t) {
	    Random random = new Random();
	    List<Statement> statements = inModel.listStatements().toList();
	    List<Resource> orderedRules = new ArrayList<>();
	    
	 // Step 1: Count predicate frequency 
	 Map<Property, Long> predicateCounts = statements.stream()
	     .collect(Collectors.groupingBy(Statement::getPredicate, Collectors.counting()));

	 // Step 2: Index statements by subject
	 Map<Resource, List<Statement>> subjectToStatements = statements.stream()
	     .collect(Collectors.groupingBy(Statement::getSubject));

	 // Step 3: Build valid predicate pairs with subject matches
	 Map<PredicatePair, Set<Resource>> validPairsWithSubjects = new HashMap<>();

	 for (Map.Entry<Resource, List<Statement>> entry : subjectToStatements.entrySet()) {
	     Resource subject = entry.getKey();
	     List<Property> predicates = entry.getValue().stream()
	         .map(Statement::getPredicate)
	         .distinct()
	         .collect(Collectors.toList());

	     for (int i = 0; i < predicates.size(); i++) {
	         for (int j = i + 1; j < predicates.size(); j++) {
	             PredicatePair pair = new PredicatePair(predicates.get(i), predicates.get(j));
	             validPairsWithSubjects
	                 .computeIfAbsent(pair, k -> new HashSet<>())
	                 .add(subject);  // ✅ Add subject that supports both predicates
	         }
	     }
	 }
	 // ✅ Step 4: Keep only pairs that have at least one subject supporting both predicates
	 List<PredicatePair> predicatePairs = validPairsWithSubjects.entrySet().stream()
			    .filter(entry -> !entry.getValue().isEmpty())
			    .map(Map.Entry::getKey)
			    .sorted(Comparator.comparingInt(pair -> validPairsWithSubjects.get(pair).size()))
			    .collect(Collectors.toList());

	 
	/* System.out.println("Top 5 predicate pairs with fewest subject matches:");
	 predicatePairs.stream()
	     .limit(12)
	     .forEach(pair -> {
	         int count = validPairsWithSubjects.get(pair).size();
	         System.out.println(pair + " → " + count + " subjects");
	     });
   */
	 // Optional logging
	 System.out.println("Total valid predicate pairs with shared subjects (i.e., potential rules): " + predicatePairs.size());


	    int actualRuleCount =  predicatePairs.size();

	    int rulesCreated = 0;
	    
	    for (int i = 0; i < actualRuleCount; i++) {
		    PredicatePair pair = predicatePairs.get(i);
		    Property p1 = pair.getFirst();
		    Property p2 = pair.getSecond();
		    
		  // System.out.print("p1 + p2 " + p1 );
		 //  System.out.println(p2);

	       // List<Resource> possibleSubjects = validPairsWithSubjects.get(pair);
	        //Resource subject = possibleSubjects.get(random.nextInt(possibleSubjects.size()));

	        // Get matching objects
	       // RDFNode o1 = inModel.getProperty(subject, p1).getObject();
	       // RDFNode o2 = inModel.getProperty(subject, p2).getObject();

	        ruleIDO += 1;

	        String sVar = "?s"  + rulesCreated;
	        String oVar1 = "?o1_" + rulesCreated;
	        String oVar2 = "?o2_" + rulesCreated;

	        String triplePattern1 = sVar + " <" + p1.getURI() + "> " + oVar1 + " .";
	        String triplePattern2 = sVar + " <" + p2.getURI() + "> " + oVar2 + " .";

	        String action = actions.get(random.nextInt(actions.size()));
	        String bracketedAction = "<" + action + ">";
	        String actionTriple = String.format("<< %s %s %s >>", sVar, bracketedAction, oVar1);
	        

	        // Temporal case
	        int caseIndex = random.nextInt(3); // Generates 0, 1, or 2 randomly
	        //int caseIndex = 0;
	        OffsetDateTime startTime, deadline, executionTime = null;

	        switch (caseIndex) {
	            case 0:  // active, not satisfied
	                startTime = t.minusHours(12);
	                executionTime = startTime.plusHours(60);
	                break;
	            case 1:  // expired, violated
	                startTime = t.minusHours(30);
	                executionTime = startTime.plusHours(60);
	                break;
	            case 2:  // expired, fulfilled
	                startTime = t.minusHours(30);
	                executionTime = startTime.plusHours(12);
	                break;
	            default:
	                startTime = t;
	        }

	        deadline = startTime.plusHours(24);

	        String startBind = String.format("BIND(\"%s\"^^xsd:dateTime AS ?startTime)", formatDateTime(startTime));
	        String deadlineBind = String.format("BIND(\"%s\"^^xsd:dateTime AS ?deadline)", formatDateTime(deadline));

	        String mainActionPattern = String.format(
	        	    "%s <%sstartTime> ?startTime ; <%sdeadline> ?deadline .",
	        	    actionTriple, Namespace.GUCON_URI, Namespace.GUCON_URI
	        	);

	        StringBuilder fullPattern = new StringBuilder();
	        fullPattern.append(triplePattern1).append("\n")
	                   .append(triplePattern2).append("\n")
	                   .append(startBind).append("\n")
	                   .append(deadlineBind).append("\n");

	        String deonticOperator = "Obligation";

	        Resource newRule= createRuleResource(ruleIDO.toString(), fullPattern.toString().trim(), deonticOperator, mainActionPattern);
	        orderedRules.add(newRule);
	        // Add RDF-star execution time triple to modelKB
	        // Add RDF-star triple for executionTime if available
	        int atTtimeTriples=0;
	        if (executionTime != null) {

	            // Pick subject that support both p1 and p2
	        	int expectedTriples = 0;
	            Set<Resource> subjects = validPairsWithSubjects.get(pair);
	            for (Resource subject : subjects) {
	                if (inModel.contains(subject, p1) && inModel.contains(subject,p2)) {
	                    expectedTriples++;
	                }
	            }
	            System.out.println("Rule " + i + ": Expected RDF-star triples for p1 = " + p1.getLocalName() + " = " + expectedTriples);

                //System.out.println("pair " +pair);
                //System.out.println("subjects list size " +subjects.size());
	            for (Resource subject : subjects) {
	                Statement s1 = inModel.getProperty(subject, p1);
	                Statement s2 = inModel.getProperty(subject, p2);

	                if (s1 == null && s2 == null) 
	                {
	                	System.out.println("yes");
	                	continue;
	                }
	                RDFNode object = s1.getObject();
	                //System.out.println("object " +object);

	                Triple quotedTriple = Triple.create(
	                    subject.asNode(),
	                    NodeFactory.createURI(action),
	                    object.asNode()
	                );

	                Triple rdfStarTriple = Triple.create(
	                    NodeFactory.createTripleNode(quotedTriple),
	                    NodeFactory.createURI(Namespace.GUCON_URI + "executionTime"),
	                    NodeFactory.createLiteral(formatDateTime(executionTime), XSDDatatype.XSDdateTime)
	                );


	                //System.out.println("rdfTriple" + rdfStarTriple);
	    	        if (!modelKB.getGraph().contains(rdfStarTriple)) {
		    	        atTtimeTriples++;
	    	        	//System.out.println("add in graph " +rdfStarTriple +"number:"+added);
	    		       //System.out.println("Rule " + i + ": Actual RDF-star triples created and added = " + atTtimeTriples);

	    	            modelKB.getGraph().add(rdfStarTriple);
	    	        }

	            }
	        }

	        
	        rulesCreated++;
	    }

	    System.out.println("Rules created with guaranteed matches: " + rulesCreated);
	    return orderedRules;
	}


	public  void generateRules(List<Integer> ruleScales,String outRuleDir, String outKbDir, OffsetDateTime t) throws Exception
	{
		modelRules = ModelFactory.createDefaultModel();
    	Namespace.registerAll(modelRules);

	        List<String> actions = Arrays.asList("gucon:approve", "gucon:access", "gucon:annotate", "gucon:delete", "gucon:log", "gucon:share");
	        ruleIDO = 0; // Reset rule counter
	        // 🔹 Generate a unique policy ID (UUID or random string)
	        String policyID = "policy-" + UUID.randomUUID(); // e.g., policy-3f8a3c2e-...
	        policyName = policyID;

	        // Step 1: Generate all rules
	        generateDataDrivenRules(actions, t);
       
	        // write all rules
	        String outFullRulefilename = outRuleDir + "rules-all.ttl";

            try (FileWriter writer = new FileWriter(outFullRulefilename)) {
            	modelRules.write(writer, "TURTLE");
            }
            System.out.println(" all Rules written to: " + outFullRulefilename);
            
	        // Write KB
	        String outKbFilePath = outKbDir + "kb-for-all-rule.ttl";
	        try (FileWriter writer = new FileWriter(outKbFilePath)) {
	            modelKB.write(writer, "TURTLE");
	            System.out.println("size of output model " + modelKB.size());
	        }
	        System.out.println("KB written to: " + outKbFilePath);
	        
	       // Extract rules from  rules-all.ttl
	        List<Rule> allRules = extractSparqlQueriesFromRules(outFullRulefilename);
	        // Bucket them by selectivity
	        Map<Selectivity, List<Rule>> buckets = bucketRules(allRules);
	        // Generate files from the HIGH selectivity bucket, using scales from config
	        createRuleFilesFromBucket(ruleScales, Selectivity.HIGH, buckets, modelRules, outRuleDir);	        
	        
	}
	
	
	  private Map<Selectivity, List<Rule>> bucketRules(List<Rule> rules) {
	        Map<String, Set<Statement>> matches = computeRuleMatches(rules);

	        Map<Selectivity, List<Rule>> buckets = new HashMap<>();
	        buckets.put(Selectivity.LOW, new ArrayList<>());
	        buckets.put(Selectivity.MEDIUM, new ArrayList<>());
	        buckets.put(Selectivity.HIGH, new ArrayList<>());

	        for (Rule rule : rules) {
	            Selectivity sel = classifyRule(matches.get(rule.getRuleID()));
	            buckets.get(sel).add(rule);
	        }
	        return buckets;
	    }
	// 
	/*private Selectivity classifyRule(Rule ruleWithQuery, Model modelKB) {
	    try {
	        Query query = ruleWithQuery.getFullConditionQuery();

	        // Run the CONSTRUCT query against the KB
	        Model result = executeConstructQuery(modelKB, query);
	        int matchCount = result.listStatements().toList().size();

	        if (matchCount < 400) {
	            return Selectivity.LOW;
	        } else if (matchCount < 1500) {
	            return Selectivity.MEDIUM;
	        } else if (matchCount < 600000) {
	            return Selectivity.HIGH;
	        } else {
	            // fallback if match count explodes
	            return Selectivity.HIGH;
	        }
	    } catch (Exception e) {
	        System.err.println("Error classifying rule " + ruleWithQuery.getRuleID() + ": " + e.getMessage());
	        return Selectivity.LOW; // safe fallback
	    }
	}*/
	

    private Selectivity classifyRule(Set<Statement> matches) {
        int matchCount = matches.size();
        if (matchCount <= 400) return Selectivity.LOW;
        if (matchCount <= 1500) return Selectivity.MEDIUM;
        return Selectivity.HIGH;
    }


	
	
	public void createRuleFilesFromBucket(
	        List<Integer> scales,
	        Selectivity targetBucket,
	        Map<Selectivity, List<Rule>> buckets,
	        Model modelRules,
	        String outputDir
	) throws IOException {

	    List<Rule> rules = buckets.get(targetBucket);

	    // Ensure stable order (so incremental makes sense)
	    rules.sort(Comparator.comparing(Rule::getRuleID));

	    int prevIndex = 0;
	    for (int scale : scales) {
	        int toIndex = Math.min(scale, rules.size());
	        List<Rule> subset = rules.subList(0, toIndex);

	        // Create a new model for this subset
	        Model subsetModel = ModelFactory.createDefaultModel();
	        subsetModel.setNsPrefixes(modelRules.getNsPrefixMap());

	        // Copy *all* rules from 0 → toIndex (incremental)
	        for (Rule rule : subset) {
	            Resource ruleRes = modelRules.getResource(rule.getRuleID());
	            if (ruleRes != null) {
	                subsetModel.add(ruleRes.listProperties());
	            }
	        }

	        // Write file
	        String outFile = outputDir + "rules-"+ scale + ".ttl";
	        try (FileWriter writer = new FileWriter(outFile)) {
	            subsetModel.write(writer, "TURTLE");
	        }
	        System.out.println("Wrote " + subset.size() + " rules (" + targetBucket + ") to " + outFile);

	        prevIndex = toIndex; // track progress
	    }
	}


    // <------------ generate subsets -------------->

	public void createSubsets(
	        List<Integer> subsetSizes,
	        String rulesBaseDir,
	        String outputKBDir
	) throws IOException {

	    List<Statement> akbStatements = modelKB.listStatements().toList();
	    List<Rule> ruleQueryList  = extractSparqlQueriesFromRules(rulesBaseDir + "/rules-all.ttl");
	    
	    // Step 2: Execute queries and collect matches per rule
	    //Map<String, Set<Statement>> ruleToMatches = new HashMap<>();
	    Map<String, Set<Statement>> ruleToMatches = this.ruleToMatchesCache;
	    Map<Statement, Set<String>> stmtToRules = new HashMap<>();

	    for (Map.Entry<String, Set<Statement>> entry : ruleToMatches.entrySet()) {
	    	// this can be reused somewhere else!
	        //String ruleID = ruleWithQuery.getRuleID().trim();
	        //Query query = ruleWithQuery.getFullConditionQuery();
	        //Model result = executeConstructQuery(modelKB, query);
	        String ruleID = entry.getKey();
	    	Set<Statement> matches = entry.getValue();
	        //Set<Statement> matches = new HashSet<>(result.listStatements().toList());
	        System.out.println("rule " +ruleID+"  final result size "+ matches.size());
	        
	        //ruleToMatches.put(ruleID, matches);
	        for (Statement stmt : matches) {
	            stmtToRules.computeIfAbsent(stmt, k -> new HashSet<>()).add(ruleID);
	        }
	    }

	    // Step 3: Build subsets for each requested size
	    for (int subsetSize : subsetSizes) {
	        System.out.println("\n Generating subset of size: " + subsetSize);

	        // Compute total match pool size
	        int totalMatchPoolSize = ruleToMatches.values().stream()
	                .mapToInt(Set::size).sum();

	        // Compute per-rule quotas proportionally, with optional cap
	        Map<String, Integer> ruleToStatementQuota = new HashMap<>();
	        int maxCap = (int) (subsetSize * 0.4);  // Max 40% per rule

	        for (Map.Entry<String, Set<Statement>> entry : ruleToMatches.entrySet()) {
	            String ruleID = entry.getKey();
	            int matchCount = entry.getValue().size();

	            int ruleQuota = (int) Math.round(((double) matchCount / totalMatchPoolSize) * subsetSize);
	            ruleQuota = Math.min(ruleQuota, maxCap); // Enforce max cap
	            ruleToStatementQuota.put(ruleID, ruleQuota);
	        }

	        // Step 4: Select statements from each rule
	        Model subsetModel = ModelFactory.createDefaultModel();
	        subsetModel.setNsPrefixes(modelKB.getNsPrefixMap());
	        Set<Statement> usedStatements = new HashSet<>();

	        for (Map.Entry<String, Integer> entry : ruleToStatementQuota.entrySet()) {
	        	
	        	
	            String ruleID = entry.getKey();
	            int quota = entry.getValue();

	            List<Statement> candidates = new ArrayList<>(ruleToMatches.get(ruleID));
	            //Collections.shuffle(candidates);

	            int added = 0;
	            for (Statement stmt : candidates) {
	                if (usedStatements.add(stmt)) { // Add only if not already used
	                    subsetModel.add(stmt);
	                    //if (ruleID.equals("http://example.com/ns#47"))
	                    //{
	                    	//System.out.println(stmt);	                    }
	                    added++;
	                    if (added >= quota) break;
	                }
	            }

	            System.out.println(" Rule " + ruleID + ": added " + added + "/" + quota + " matches");
	        }

	        // Step 5: Fill remaining space with background knowledge (AKB)
	        if (subsetModel.size() < subsetSize) {
	            System.out.println("Filling with AKB background triples...");
	            List<Statement> remaining = new ArrayList<>(akbStatements);
	            remaining.removeAll(usedStatements);
	            Collections.shuffle(remaining);

	            int toAdd = subsetSize - (int) subsetModel.size();
	            for (int i = 0; i < Math.min(toAdd, remaining.size()); i++) {
	                Statement stmt = remaining.get(i);
	                if (usedStatements.add(stmt)) {
	                    subsetModel.add(stmt);
	                }
	            }
	        }

	        // Step 6: Write final subset to file
	        String outPath = outputKBDir + "kb-" + subsetSize + ".ttl";
	        try (FileWriter writer = new FileWriter(outPath)) {
	            subsetModel.write(writer, "TURTLE");
	        }

	        System.out.println("Subset of " + subsetSize + " triples written to: " + outPath);
	    }
	}
	
		
	public static void main(String[] args) throws Exception {
	    if (args.length < 1) {
	        System.err.println("Usage: java YourClassName <path-to-config.yaml>");
	        System.exit(1);
	    }

	    // Load config from the file path provided in args[0]
	    String configPath = args[0];
	    Yaml yaml = new Yaml();
	    Map<String, Object> config = yaml.load(new FileInputStream(configPath));
	    
	    
    String outputRuleDirectory = (String) config.get("outputRuleDirectory");
    String outputKBDirectory = (String) config.get("outputKBDirectory");
    String infilename = (String) config.get("inputDataDirectory");
    Generator generator = new Generator (infilename);
    OffsetDateTime t;
    Object inputTime = config.get("inputTime"); // could be Date or String
    if (inputTime instanceof Date) {
        Date date = (Date) inputTime;
        t = date.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(date.toInstant()));
    } else if (inputTime instanceof String) {
        String dateTimeStr = ((String) inputTime).trim();
        t = OffsetDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } else {
        throw new IllegalArgumentException("Unsupported inputTime type: " + inputTime.getClass());
    }
    
    List<Integer> ruleScales = (List<Integer>) config.get("ruleScale");	

    generator.generateRules(ruleScales,outputRuleDirectory,outputKBDirectory,t);
    
    List<Integer> kbScales = (List<Integer>) config.get("KBScale");	
    generator.createSubsets(kbScales,outputRuleDirectory,outputKBDirectory);
    
    
	}
}
