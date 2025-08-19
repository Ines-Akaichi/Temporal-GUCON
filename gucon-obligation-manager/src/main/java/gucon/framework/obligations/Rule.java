package gucon.framework.obligations;

import org.apache.jena.query.Query;

public class Rule {

	private String conditionPattern;
	private String deonticOperator;
	private String actionPattern ;
	private String ruleID;
	private Query conditionQuery;
	private Query actionQuery;
	private String fullConditionPattern;
	private Query fullConditionQuery;
    private String atTimeVarName;  // store the variable name here

    public void setAtTimeVarName(String varName) {
        this.atTimeVarName = varName;
    }

    public String getAtTimeVarName() {
        return this.atTimeVarName;
    }
	public Rule(String ruleID, Query fullConditionQuery) {
		super();
		this.ruleID = ruleID;
		this.fullConditionQuery = fullConditionQuery;
	}
	private String policyID;
	
	
	
	public void setFullConditionPattern(String pattern) {
	    this.fullConditionPattern = pattern;
	}

	public String getFullConditionPattern() {
	    return fullConditionPattern;
	}

	public void setFullConditionQuery(Query query) {
	    this.fullConditionQuery = query;
	}

	public Query getFullConditionQuery() {
	    return fullConditionQuery;
	}


	public Rule(String conditionPattern, String deonticOperator, String actionPattern, String ruleID, String policyID) {
		super();
		this.conditionPattern = conditionPattern;
		this.deonticOperator = deonticOperator;
		this.actionPattern = actionPattern;
		this.ruleID = ruleID;
		this.policyID=policyID;
	}
	

	public String getPolicyID() {
		return policyID;
	}


	public void setPolicyID(String policyID) {
		this.policyID = policyID;
	}

	public Query getConditionQuery() {
		return conditionQuery;
	}
	public void setConditionQuery(Query conditionQuery) {
		this.conditionQuery = conditionQuery;
	}
	public Query getActionQuery() {
		return actionQuery;
	}
	public void setActionQuery(Query actionQuery) {
		this.actionQuery = actionQuery;
	}

	public String getConditionPattern() {
		return conditionPattern;
	}
	public void setConditionPattern(String conditionPattern) {
		this.conditionPattern = conditionPattern;
	}
	public String getDeonticOperator() {
		return deonticOperator;
	}
	public void setDeonticOperator(String deonticOperator) {
		this.deonticOperator = deonticOperator;
	}
	public String getActionPattern() {
		return actionPattern;
	}
	public void setActionPattern(String actionPattern) {
		this.actionPattern = actionPattern;
	}
	public String getRuleID() {
		return ruleID;
	}
	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}
	
	
	
	
	
	
}
