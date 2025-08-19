package gucon.framework.obligations;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import gucon.framework.dbsms.GraphDBStorageProvider;
import gucon.framework.dbsms.TDB2StorageProvider;


public class ObligationStateManager {

	 // Input
	 private KnowledgeBaseManager kbManager;
	 private OffsetDateTime time; 
	 private Model snapshot ;

	 Map<String, EvaluatedRule> evaluatedRules = new HashMap<>();

	 
	 public KnowledgeBaseManager getKbManager() {
		return kbManager;
	}

	public void setKbManager(KnowledgeBaseManager kbManager) {
		this.kbManager = kbManager;
	}

	public OffsetDateTime getTime() {
		return time;
	}

	public void setTime(OffsetDateTime time) {
		this.time = time;
	}

	public void setSnapshot(Model snapshot) {
		this.snapshot = snapshot;
	}
	
	 public Map<String, EvaluatedRule> getEvaluatedRules() {
		return evaluatedRules;
	}

	public void setEvaluatedRules(Map<String, EvaluatedRule> evaluatedRules) {
		this.evaluatedRules = evaluatedRules;
	}

	public ObligationStateManager(KnowledgeBaseManager kbManager, String timeString) {
		 this.kbManager = kbManager;	        
	     this.time = OffsetDateTime.parse(timeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		 this.snapshot= getSnapshot(); // don't create immediately
	 }
		 


   /**
    * Generates a snapshot of the knowledge base at a given time.
    */
   private Model getSnapshot() {
       if (snapshot == null) {
           snapshot = kbManager.generateSnapshot(time.toString());
       }
       return snapshot;
   }
	

 /*  
  public List<MappedRule> getSatisfiedRules(List<Rule> rules) {
	    RuleIDGenerator generator = new RuleIDGenerator();
	    List<MappedRule> satisfiedRules = new ArrayList<>();

	    for (Rule r : rules) {
	        Query conditionQuery = r.getFullConditionQuery();
	       // System.out.println("condition query full "  + conditionQuery);
	        
	        Query actionQuery = r.getActionQuery();

	        String deonticOperator = r.getDeonticOperator().trim();
	        String ruleID = r.getRuleID().trim();
	        String policyID = r.getPolicyID().trim();

	        List<Map<Var, Node>> conditionMappings = RuleMapper.GetMappings(conditionQuery, snapshot);

	

	        for (Map<Var, Node> mapping : conditionMappings) {
	        	//System.out.println("mapping " +mapping);
	            ElementGroup mappedActionGroup = RuleMapper.map(actionQuery.getQueryPattern(), mapping);
	            ElementGroup mappedConditionGroup = RuleMapper.map(conditionQuery.getQueryPattern(), mapping);
                MappedCondition mappedcondition = new MappedCondition (mappedConditionGroup);
	            MappedAction mappedAction = new MappedAction(mappedActionGroup);

	            String instantiatedRuleID = generator.generateInstantiatedRuleID(ruleID);

	            MappedRule instRule = new MappedRule(
	            		mappedcondition,
	                deonticOperator,
	                mappedAction,
	                ruleID,
	                instantiatedRuleID,
	                policyID,
	                time
	            );

	            satisfiedRules.add(instRule);
	        }
	    }

	    return satisfiedRules;
	}
	*/	
  
   public List<EvaluatedRule> getObligationStates(List<Rule> rules) throws Exception {
	    RuleIDGenerator generator = new RuleIDGenerator();
	    List<EvaluatedRule> evaluatedRules = new ArrayList<>();

	    for (Rule r : rules) {
	        Query conditionQuery = r.getFullConditionQuery();
	        Query actionQuery = r.getActionQuery();

	        String deonticOperator = r.getDeonticOperator().trim();
	        String ruleID = r.getRuleID().trim();
	        String policyID = r.getPolicyID().trim();

	        List<Map<Var, Node>> conditionMappings = RuleMapper.GetMappings(conditionQuery, snapshot);

	        for (Map<Var, Node> mapping : conditionMappings) {
	            // Map patterns
	            ElementGroup mappedActionGroup = RuleMapper.map(actionQuery.getQueryPattern(), mapping);
	            ElementGroup mappedConditionGroup = RuleMapper.map(conditionQuery.getQueryPattern(), mapping);

	            MappedCondition mappedCondition = new MappedCondition(mappedConditionGroup);
	            MappedAction mappedAction = new MappedAction(mappedActionGroup);

	            String instantiatedRuleID = generator.generateInstantiatedRuleID(ruleID);

	            // Create MappedRule (needed to parse execution time)
	            
	            MappedRule instRule = new MappedRule(
	                mappedCondition,
	                deonticOperator,
	                mappedAction,
	                ruleID,
	                instantiatedRuleID,
	                policyID,
	                time
	            );

	            // ---- STATE EVALUATION STARTS HERE ----
	            OffsetDateTime startTime = mappedAction.getStartTime();
	            OffsetDateTime deadline = mappedAction.getDeadline();
	            OffsetDateTime execDateTime = instRule.getAtTimeValue();

	            OffsetDateTime validExecTime = null;

	            EvaluatedRule details = new EvaluatedRule(
	                deonticOperator,
	                mappedAction,
	                ruleID,
	                instantiatedRuleID,
	                policyID,
	                time
	            );

	            if (execDateTime != null) {
	                boolean isValid = true;

	                if (startTime != null && execDateTime.isBefore(startTime))
	                    isValid = false;

	                if (deadline != null && execDateTime.isAfter(deadline))
	                    isValid = false;

	                if (isValid && validExecTime == null)
	                    validExecTime = execDateTime;
	            }

	            if (startTime != null && deadline != null) {
	                if (!time.isAfter(deadline) && !time.isBefore(startTime)) {
	                    details.getStates().add(ObligationState.ACTIVE);
	                    if (validExecTime == null)
	                        details.getStates().add(ObligationState.NOTSATISFIED);
	                }
	                if (time.isAfter(deadline)) {
	                    details.getStates().add(ObligationState.EXPIRED);
	                    if (validExecTime == null)
	                        details.getStates().add(ObligationState.VIOLATED);
	                }
	            } else if (startTime != null) {
	                if (!time.isBefore(startTime)) {
	                    details.getStates().add(ObligationState.ACTIVE);
	                    if (validExecTime == null)
	                        details.getStates().add(ObligationState.NOTSATISFIED);
	                }
	            } else if (deadline != null) {
	                if (!time.isAfter(deadline)) {
	                    details.getStates().add(ObligationState.ACTIVE);
	                    if (validExecTime == null)
	                        details.getStates().add(ObligationState.NOTSATISFIED);
	                }
	                if (time.isAfter(deadline)) {
	                    details.getStates().add(ObligationState.EXPIRED);
	                    if (validExecTime == null)
	                        details.getStates().add(ObligationState.VIOLATED);
	                }
	            } else {
	                throw new Exception("Neither startTime nor deadline is defined for rule: " + instantiatedRuleID);
	            }

	            if (validExecTime != null) {
	                details.setExecutionTime(validExecTime);
	                details.getStates().add(ObligationState.FULFILLED);
	            }

	            evaluatedRules.add(details);
	        }
	    }

	    return evaluatedRules;
	}

 

	public ComplianceStatus complianceChecker(List<EvaluatedRule> evaluatedRules) {
	    for (EvaluatedRule rule : evaluatedRules) {
	        boolean isViolated = rule.getStates().contains(ObligationState.VIOLATED);

	        if (isViolated) {
	            //rule.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);
	            return ComplianceStatus.NON_COMPLIANT;
	        }
	    }
	    return ComplianceStatus.COMPLIANT;
	}


	public ComplianceReport generateComplianceReport(List<Rule> rules) throws Exception {
		
		   ComplianceReport report = new ComplianceReport();
		    report.setEvaluationTime(time);

		    // Identify the KB
		    String kbIRI = kbManager.getContextIRI();
		    report.setContextIRI(kbIRI);

		    // Get  evaluated rules
		    //List<MappedRule> satisfiedRules = getSatisfiedRules(rules);
		    List<EvaluatedRule> evaluatedRules = getObligationStates(rules);

		    report.setEvaluatedObligations(evaluatedRules);

		    // Determine compliance
		    ComplianceStatus status = complianceChecker(evaluatedRules);
		    report.setComplianceStatus(status);

		    return report;
	}
	 
   public static void main(String[] args) throws Exception {
    String ttlPath="C:/Users/Administrator/OneDrive - WU Wien/Desktop/GUCON-Obl/generatedData/kb/kb-for-all-rule.ttl";
    String rulePath ="C:/Users/Administrator/OneDrive - WU Wien/Desktop/GUCON-Obl/generatedData/rules/rules-5.ttl";
    String reportFilePath ="C:/Users/Administrator/OneDrive - WU Wien/Desktop/GUCON-Obl/generatedData/reports/report.ttl";
	
	String graphdbDir = "C:/Users/iakaichi/OneDrive - WU Wien/Desktop/PhD/Papers/Paper WU & FORTH/Extension/WWW/Querying Approach/Test/GraphDB";  // Local directory
	String repoId = "repo-defaults";      
	// graph db load different names graphs describing default ontologies
	// kbContext: named graph for my loaded graph
	String kbContextIRIString ="http://example.org/kb";
	String label="My Embedded GraphDB";
       
	//GraphDBStorageProvider graphDBProvider = new GraphDBStorageProvider(graphdbDir,repoId,label);
	//graphDBProvider.loadFromTurtle(ttlPath,kbContextIRIString);
	//graphDBProvider.getModel(kbContextIRIString).listStatements().forEachRemaining(System.out::println);
	
	String tdbDir = "C:/Users/Administrator/OneDrive - WU Wien/Desktop/GUCON-Obl/generatedData/database/TDB2";
	
	TDB2StorageProvider tdb2Provider = new TDB2StorageProvider (tdbDir);
	tdb2Provider.loadFromTurtle(ttlPath,kbContextIRIString);
	
	// 3. Specify a time
	String dateTime ="2010-01-10T10:44:00.000+02:00";


   	KnowledgeBaseManager kbManager = new KnowledgeBaseManager(tdb2Provider,kbContextIRIString);
		
   	PolicyManager ruleManager = new PolicyManager(kbManager, rulePath);
       
       // 4. Load rules
       List<Rule> rules = ruleManager.loadRules();
   	//Model snapshot = kbManager.generateSnapshot(dateTime);
   	
       // 6. Create Obligation Manager for KBManger -- Obligation Manager creates snapshot 
       // Record the start time and memory usage
       long startTime = System.nanoTime();
       Runtime runtime = Runtime.getRuntime();
       runtime.gc(); // Suggest garbage collection to get a more accurate measurement
       long startMemory = runtime.totalMemory() - runtime.freeMemory();
        
       
   	ObligationStateManager obligationManager = new ObligationStateManager(kbManager, dateTime);
   	
   	//List<MappedRule> SatisfiedRules = obligationManager.getSatisfiedRules(rules);
   	/*for (MappedRule r : SatisfiedRules)
   	{
   		
   		System.out.println("rule condition " +r.getMappedCondition().getCondition());
   		System.out.println("rule ID " +r.getRuleID());
   		System.out.println("inst rule ID "+r.getInstantiatedRuleID());
   		System.out.println("execution times "+r.getAtTimeValues());
   		System.out.println("triple action "+r.getMappedAction().getTripleAction());

   		System.out.println("------");

   	}*/
	// List<EvaluatedRule> evaluatedRules= obligationManager.getObligationStates(SatisfiedRules);
	// obligationManager.complianceChecker (evaluatedRules);

	/*	for (EvaluatedRule r : evaluatedRules)
	   	{
			
	   		System.out.println(r.toString());

	   	}*/
	   	
   	ComplianceReport report = obligationManager.generateComplianceReport(rules);
   	
   	//graphDBProvider.close();
   	
   	// Record the end time and memory usage
   	long endTime = System.nanoTime();
   	long endMemory = runtime.totalMemory() - runtime.freeMemory();
   	
   	// Calculate and display results
   	long elapsedTimeInMillis = (endTime - startTime) / 1_000_000;
   	long memoryUsedInKB = (endMemory - startMemory) / 1024;

   	System.out.println("Execution time: " + elapsedTimeInMillis + " ms");
   	System.out.println("Memory used: " + memoryUsedInKB + " KB");
   	
   	//report.printReport();
   	report.saveReport(reportFilePath);
   	
   	//tdb2Provider.close();
   	//report.saveReport();
   	
   }
	 
}