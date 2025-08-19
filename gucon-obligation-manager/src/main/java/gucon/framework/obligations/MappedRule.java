package gucon.framework.obligations;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;


public class MappedRule  {

	private MappedCondition mappedCondition;
	private String deonticOperator;
	private MappedAction mappedAction ;
	private String ruleID;
	private String instantiatedRuleID;
	private String policyID;
	private OffsetDateTime evaluationTime;
	 private OffsetDateTime atTimeValue;


	public MappedRule( String deonticOperator, MappedAction mappedAction,
			String ruleID,String instantiatedRuleID,String policyID, OffsetDateTime evaluationTime) {
	
		this.deonticOperator = deonticOperator;
		this.ruleID = ruleID;
		this.instantiatedRuleID = instantiatedRuleID;
	    this.mappedAction = mappedAction;
	    this.policyID=policyID;
	    this.evaluationTime=evaluationTime;

	}
		
	public MappedRule(MappedCondition mappedCondition, String deonticOperator, MappedAction mappedAction,
			String ruleID,String instantiatedRuleID,String policyID, OffsetDateTime evaluationTime) {
	
		this.mappedCondition = mappedCondition;
		this.deonticOperator = deonticOperator;
		this.ruleID = ruleID;
		this.instantiatedRuleID = instantiatedRuleID;
	    this.mappedAction = mappedAction;
	    this.policyID=policyID;
	    this.evaluationTime=evaluationTime;
	    this.atTimeValue=mappedCondition.getAtTime();

	}



	public OffsetDateTime getAtTimeValue() {
		return atTimeValue;
	}

	public void setAtTimeValue(OffsetDateTime atTimeValue) {
		this.atTimeValue = atTimeValue;
	}

	public MappedAction getMappedAction() {
		return mappedAction;
	}



	public void setMappedAction(MappedAction mappedExtendedAction) {
		this.mappedAction = mappedExtendedAction;
	}


	public MappedCondition getMappedCondition() {
		return mappedCondition;
	}


	public void setMappedCondition(MappedCondition mappedCondition) {
		this.mappedCondition = mappedCondition;
	}
	public String getDeonticOperator() {
		return deonticOperator;
	}
	public void setDeonticOperator(String deonticOperator) {
		this.deonticOperator = deonticOperator;
	}

	public String getRuleID() {
		return ruleID;
	}
	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}
	public String getInstantiatedRuleID() {
		return instantiatedRuleID;
	}


	public void setInstantiatedRuleID(String instantiatedRuleID) {
		this.instantiatedRuleID = instantiatedRuleID;
	}
	
	
	
	public String getPolicyID() {
		return policyID;
	}



	public void setPolicyID(String policyID) {
		this.policyID = policyID;
	}


	

	public OffsetDateTime getEvaluationTime() {
		return evaluationTime;
	}



	public void setEvaluationTime(OffsetDateTime evaluationTime) {
		this.evaluationTime = evaluationTime;
	}



	@Override
	public String toString() {
		return "MappedRule [mappedCondition=" + mappedCondition + ", deonticOperator=" + deonticOperator
				+ ", mappedAction=" + mappedAction + ", ruleID=" + ruleID + ", instantiatedRuleID=" + instantiatedRuleID
				+ ", policyID=" + policyID + ", evaluationTime=" + evaluationTime + "]";
	}




	
	
	
}
