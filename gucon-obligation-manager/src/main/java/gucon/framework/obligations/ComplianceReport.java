package gucon.framework.obligations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.syntax.ElementGroup;

import gucon.utils.Namespace;

public class ComplianceReport {
    		// Namespaces

	    private String contextIRI;
	    private ComplianceStatus complianceStatus;
	    private OffsetDateTime evaluationTime;
	    private List<EvaluatedRule> evaluatedObligations = new ArrayList<>();
	    
	    public ComplianceReport() {
	    }
	    
		public OffsetDateTime getEvaluationTime() {
			
			return evaluationTime;
		}
		public void setEvaluationTime(OffsetDateTime evaluationTime) {
			this.evaluationTime = evaluationTime;
		}
			
		public  List<EvaluatedRule> getEvaluatedObligations() {
		    return evaluatedObligations;
		}

		public void setEvaluatedObligations(List<EvaluatedRule>obligations) {
		    this.evaluatedObligations = obligations;
		}
		public String getContextIRI() {
		    return contextIRI;
		}

		public void setContextIRI(String kbIRI) {
		    this.contextIRI = kbIRI;
		}

		public ComplianceStatus getComplianceStatus() {
		    return complianceStatus;
		}

		public void setComplianceStatus(ComplianceStatus complianceStatus) {
		    this.complianceStatus = complianceStatus;
		}
		

	    //  method to print nicely
		public void printReport() {
		     System.out.println("=== Compliance Report ===");
		        System.out.println("Generated for: " + evaluationTime);

		        for (ObligationState state : ObligationState.values()) {
		            System.out.println("\n" + state.name() + " Obligations:");
		            for (EvaluatedRule rule : evaluatedObligations) {
		                for (ObligationState actualState : rule.getStates()) {
		                    if (actualState == state) {
		                        System.out.println(rule.getInstantiatedRuleID() + " : " + rule);
		                        break;
		                    }
		                }
		            }
		        }
		}


	
	    //  method to save report using Turtle
	
		public void saveReport(String filePath) {
		    Model model = ModelFactory.createDefaultModel();

		    // Namespaces
		
		    Namespace.registerAll(model);

		    // Properties
		    Property type = model.createProperty(Namespace.RDF_URI+ "type");
		    Property hasReportTime = model.createProperty(Namespace.GC_URI + "hasReportTime");
		    Property hasEvaluationTime = model.createProperty(Namespace.GC_URI+ "hasEvaluationTime");
		    Property isGeneratedFrom = model.createProperty(Namespace.GC_URI + "isGeneratedFrom");
		    Property hasComplianceStatus = model.createProperty(Namespace.GC_URI + "hasComplianceStatus");

		    ZonedDateTime now = ZonedDateTime.now();
		    String nowStr = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		    // Report resource
		    Resource report = model.createResource(Namespace.GC_URI+ "report-" + nowStr)
		        .addProperty(type, model.createResource(Namespace.GC_URI + "Report"))
		        .addProperty(hasReportTime, model.createTypedLiteral(nowStr, Namespace.XSD_URI + "dateTime"))
		        .addProperty(hasEvaluationTime, model.createTypedLiteral(evaluationTime, Namespace.XSD_URI+ "dateTime"));

		    // KB resource
		    String kbIRI = contextIRI;
		    Resource kbRes = model.createResource(kbIRI)
		        .addProperty(type, model.createResource(Namespace.GC_URI + "KnowledgeBase"));

		    // Compliance status for the KB
		    ComplianceStatus status = complianceStatus;
		    kbRes.addProperty(hasComplianceStatus, model.createResource(Namespace.GC_URI + status.name()));

		    
		    // Link report to KB
		    report.addProperty(isGeneratedFrom, kbRes);


		    // Add evaluated obligations
		    for (EvaluatedRule rule : evaluatedObligations) {
		        serializeObligation(model, rule, report);
		    }

		    //System.out.println("Model size: " + model.size());

		    try (FileOutputStream out = new FileOutputStream(filePath)) {
		        model.write(out, "TURTLE");
		        System.out.println("Report saved to: " + filePath);
		    } catch (IOException e) {
		        System.err.println("Error writing report: " + e.getMessage());
		    }
		}


	    
		private void serializeObligation(Model model, EvaluatedRule rule, Resource report) {
	
		    // Properties
		    Property hasExtendedAction = model.createProperty(Namespace.GC_URI + "hasExtendedAction");
		    
		    Property startTimeProp = model.createProperty(Namespace.GUCON_URI + "startTime");
		    Property deadlineProp = model.createProperty(Namespace.GUCON_URI + "deadline");
		    Property executionTimeProp = model.createProperty(Namespace.GUCON_URI + "executionTime");
		    Property entityProp = model.createProperty(Namespace.GUCON_URI+ "entity");
		    Property actionProp = model.createProperty(Namespace.GUCON_URI + "action");
		    Property resourceProp = model.createProperty(Namespace.GUCON_URI+ "resource");
		    
		    
		    Property isDerivedFrom = model.createProperty(Namespace.GC_URI+ "isDerivedFrom");
		    Property includes = model.createProperty(Namespace.GC_URI + "includes");
		    Property hasObligationState = model.createProperty(Namespace.GC_URI + "hasObligationState");

		    Property type = model.createProperty(Namespace.RDF_URI + "type");

		    // Report -> includes -> MappedObligationRule
		    Resource mappedRuleRes = model.createResource(rule.getInstantiatedRuleID())
		        .addProperty(type, model.createResource(Namespace.GC_URI + "MappedObligationRule"))
		        .addProperty(isDerivedFrom, model.createResource(rule.getRuleID()));

		    // Obligation states
		    for (ObligationState state : rule.getStates()) {
		        mappedRuleRes.addProperty(hasObligationState, model.createResource(Namespace.GC_URI  + state.name()));
		    }

		    // Extended Action
		    MappedAction action = rule.getMappedAction();
		    if (action != null) {
		        Resource actionRes = model.createResource()
		            .addProperty(type, model.createResource(Namespace.GUCON_URI + "ExtendedAction"));

		        Triple actionTriple  =action.getTripleAction();
		        //entity
		        actionRes.addProperty(entityProp, model.createResource(actionTriple.getSubject().toString()));
		        
		        //action
	            actionRes.addProperty(actionProp, model.createResource(actionTriple.getPredicate().toString()));

		        //resource
		    
	            actionRes.addProperty(resourceProp, model.createResource(actionTriple.getObject().toString()));
	            
	            // Times
		        if (action.getStartTime() != null) {
		            actionRes.addLiteral(startTimeProp, model.createTypedLiteral(action.getStartTime(), Namespace.XSD_URI + "dateTime"));
		        }
		        if (action.getDeadline() != null) {
		            actionRes.addLiteral(deadlineProp, model.createTypedLiteral(action.getDeadline(), Namespace.XSD_URI  + "dateTime"));
		        }
		        if (rule.getExecutionTime() != null) {
		            actionRes.addLiteral(executionTimeProp, model.createTypedLiteral(rule.getExecutionTime(), Namespace.XSD_URI  + "dateTime"));
		        }
	            
	            mappedRuleRes.addProperty(hasExtendedAction, actionRes);
		    }

		    // Link to report
		    report.addProperty(includes, mappedRuleRes);
		}

	    
}
