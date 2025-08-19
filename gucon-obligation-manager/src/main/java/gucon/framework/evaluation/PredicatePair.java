package gucon.framework.evaluation;


import java.util.Objects;

import org.apache.jena.rdf.model.Property;

public class PredicatePair {
	 private final Property p1;
	    private final Property p2;

	    public PredicatePair(Property a, Property b) {
	        // Sort the URIs to ensure consistency
	        if (a.getURI().compareTo(b.getURI()) <= 0) {
	            this.p1 = a;
	            this.p2 = b;
	        } else {
	            this.p1 = b;
	            this.p2 = a;
	        }
	    }

	    public Property getFirst() { return p1; }
	    public Property getSecond() { return p2; }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof PredicatePair)) return false;
	        PredicatePair other = (PredicatePair) o;
	        return p1.equals(other.p1) && p2.equals(other.p2);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(p1, p2);
	    }

	    @Override
	    public String toString() {
	        return "(" + p1.getLocalName() + ", " + p2.getLocalName() + ")";
	    }
}
