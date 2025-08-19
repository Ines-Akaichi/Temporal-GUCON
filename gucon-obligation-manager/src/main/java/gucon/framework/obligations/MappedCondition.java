package gucon.framework.obligations;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;

import gucon.utils.Namespace;

public class MappedCondition {

	private ElementGroup mappedconditionGroup;
	private OffsetDateTime atTime =null; 

	public OffsetDateTime getAtTime() {
		return atTime;
	}

	public void setAtTime(OffsetDateTime atTime) {
		this.atTime = atTime;
	}

	public MappedCondition(ElementGroup mappedconditionGroup) {
		super();
		this.mappedconditionGroup = mappedconditionGroup;
		extractElements();

	}

	public ElementGroup getCondition() {
		return mappedconditionGroup;
	}

	public void setCondition(ElementGroup mappedconditionGroup) {
		this.mappedconditionGroup = mappedconditionGroup;
	}

	@Override
	public String toString() {
		return "Condition [condition=" + mappedconditionGroup + "]";
	}
	

	private void extractElements() {
	    if (mappedconditionGroup == null) return;

	    for (Element outerEl : mappedconditionGroup.getElements()) {
	        // Check if this is a nested group
	        if (outerEl instanceof ElementGroup) {
	            ElementGroup nestedGroup = (ElementGroup) outerEl;

	            // Get the nested optional
	            for (Element innerEl : nestedGroup.getElements()) {

	                if (innerEl instanceof ElementPathBlock pathBlock) {

	                    for (TriplePath t : pathBlock.getPattern()) {
	                        Node predicate = t.getPredicate();

	                        if (predicate.isURI() && predicate.getURI().equals(Namespace.GUCON_URI + "executionTime")) {
	                            Node object = t.getObject();
	                            if (!object.isLiteral()) {
	                                //System.err.println("Warning: atTime object is not a literal: " + object);
	                                continue;
	                            }

	                            try {
	                                OffsetDateTime atTime = OffsetDateTime.parse(
	                                    object.getLiteralLexicalForm(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
	                                );
	                                this.setAtTime(atTime);
	                            } catch (Exception e) {
	                                System.err.println("Could not parse atTime value: " + object + " → " + e.getMessage());
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	}

		

	
}
