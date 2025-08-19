package gucon.framework.obligations;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import gucon.utils.Namespace;

public class MappedAction {
	
	private OffsetDateTime startTime = null;
	private OffsetDateTime deadline = null;
	private Triple tripleAction;
	
	private ElementGroup mappedActionGroup;
	
	
	public MappedAction(ElementGroup mappedActionGroup) {
		super();
		this.mappedActionGroup = mappedActionGroup;
		extractElements();
	}
	
	
	public ElementGroup getMappedActionGroup() {
		return mappedActionGroup;
	}


	public void setMappedActionGroup(ElementGroup mappedActionGroup) {
		this.mappedActionGroup = mappedActionGroup;
	}


	public OffsetDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
	}
	public OffsetDateTime getDeadline() {
		return deadline;
	}
	public void setDeadline(OffsetDateTime deadline) {
		this.deadline = deadline;
	}
	public Triple getTripleAction() {
		return tripleAction;
	}
	public void setTripleAction(Triple tripleAction) {
		this.tripleAction = tripleAction;
	}
	
	

	private void extractElements() {
	    if (mappedActionGroup == null) {
	        return;
	    }

	    for (Element element : mappedActionGroup.getElements()) {
	        if (element instanceof ElementPathBlock pathBlock) {

	            for (TriplePath triple : pathBlock.getPattern()) {
	                Node subject = triple.getSubject(); 
	                Node predicate = triple.getPredicate();
	                Node object = triple.getObject();

	                // Handle RDF-star triple
	                if (subject.isNodeTriple()) {
	                    Triple embeddedTripleAction = subject.getTriple();
	                    this.setTripleAction(embeddedTripleAction);
	                } else {
	                    Triple tripleAction = triple.asTriple();
	                    this.setTripleAction(tripleAction);
	                }

	                // Handle temporal properties
	                if (predicate.isURI()) {
	                    String predicateURI = predicate.getURI();

	                    if (predicateURI.equals(Namespace.GUCON_URI + "startTime")) {
	                        if (!object.isLiteral()) {
	                            System.err.println("Warning: startTime object is not a literal: " + object);
	                            continue;
	                        }

	                        OffsetDateTime start = OffsetDateTime.parse(
	                                object.getLiteralLexicalForm(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
	                        );
	                        this.setStartTime(start);
	                        continue;
	                    }

	                    if (predicateURI.equals(Namespace.GUCON_URI + "deadline")) {
	                        if (!object.isLiteral()) {
	                            System.err.println("Warning: deadline object is not a literal: " + object);
	                            continue;
	                        }

	                        OffsetDateTime deadline = OffsetDateTime.parse(
	                                object.getLiteralLexicalForm(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
	                        );
	                        this.setDeadline(deadline);
	                        continue;
	                    }
	                }
	            }
	        }
	    }
	}

	
	public String getFormattedTriplePatterns() {
	    if (mappedActionGroup == null) {
	        return "null";
	    }

	    StringBuilder sb = new StringBuilder();
	    String startTimeStr = (startTime != null) 
	        ? startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) 
	        : "null";
	    String deadlineStr = (deadline != null) 
	        ? deadline.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) 
	        : "null";

	    String stringAction = formatTriple(tripleAction);

	    sb.append(stringAction)
	      .append(" startTime: ").append(startTimeStr)
	      .append(" deadline: ").append(deadlineStr);

	    return sb.toString();
	}

	private String formatTriple(Triple triple) {
	    return String.format("(%s, %s, %s)",
	            triple.getSubject().toString(),
	            triple.getPredicate().toString(),
	            triple.getObject().toString());
	}



}
