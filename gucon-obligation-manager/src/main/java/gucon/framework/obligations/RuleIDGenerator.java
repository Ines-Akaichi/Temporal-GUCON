package gucon.framework.obligations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class RuleIDGenerator {

	   private final Map<String, Integer> instanceCounters = new HashMap<>();

	    public String generateInstantiatedRuleID(String originalRuleID) {
	        String prefix = extractPrefix(originalRuleID);
	        String ruleNumber = extractRuleNumber(originalRuleID);

	        // Increment counter for this rule
	        int count = instanceCounters.getOrDefault(originalRuleID, 0) + 1;
	        instanceCounters.put(originalRuleID, count);

	        return prefix + ruleNumber + "_instance" + count;
	    }

	    private String extractPrefix(String ruleID) {
	        int hashIndex = ruleID.lastIndexOf('#');
	        int slashIndex = ruleID.lastIndexOf('/');

	        if (hashIndex != -1) {
	            return ruleID.substring(0, hashIndex + 1);
	        } else if (slashIndex != -1) {
	            return ruleID.substring(0, slashIndex + 1);
	        } else {
	            throw new IllegalArgumentException("Invalid rule ID: No '#' or '/' delimiter found");
	        }
	    }

	    private String extractRuleNumber(String ruleID) {
	        int hashIndex = ruleID.lastIndexOf('#');
	        int slashIndex = ruleID.lastIndexOf('/');

	        int start = Math.max(hashIndex, slashIndex);
	        if (start == -1 || start == ruleID.length() - 1) {
	            throw new IllegalArgumentException("Rule ID does not contain a rule number segment");
	        }

	        return ruleID.substring(start + 1);
	    }
}
