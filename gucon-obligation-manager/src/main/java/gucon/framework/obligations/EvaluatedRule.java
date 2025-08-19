package gucon.framework.obligations;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.sparql.syntax.ElementGroup;



public class EvaluatedRule extends MappedRule {
	
	// This class inherits from the class MappedRule 
	
	 private OffsetDateTime executionTime;
	 Set<ObligationState> states = new HashSet<>();

	public Set<ObligationState> getStates() {
		return states;
	}
	public void setStates(Set<ObligationState> states) {
		this.states = states;
	}

	 	
    public EvaluatedRule(
            String deonticOperator,
            MappedAction mappedAction,
            String ruleID,
            String instantiatedRuleID,
            String policyID,
            OffsetDateTime evaluationTime
        ) {
            super( deonticOperator, mappedAction, ruleID, instantiatedRuleID, policyID,evaluationTime);
        }
	


	public OffsetDateTime getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(OffsetDateTime executionTime) {
		this.executionTime = executionTime;
	}
	

    @Override
    public String toString() {
    	
        return "EvaluatedRule [mappedAction=" + getMappedAction().getFormattedTriplePatterns()
                + ", executionTime=" + executionTime
                + ", evaluationTime=" + getEvaluationTime()
                + ", instantiatedRuleID=" + getInstantiatedRuleID()
                + ", ruleID=" + getRuleID()
                + ", policyID=" + getPolicyID()
                + ", states=" + states +"]";
    }

	
	
	 
	
}
