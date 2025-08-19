package gucon.framework.obligations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingBuilder;
import org.apache.jena.sparql.expr.E_Exists;
import org.apache.jena.sparql.expr.E_NotExists;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementUnion;

public class RuleMapper {

	public static boolean areTriplesEqual(Triple value1, Triple value2) {

	    // Compare two triples
	    return value1.equals(value2); // Assuming Triple implements a proper equals method
	}
	
	public static Triple extractSingleTriple(ElementGroup elementGroup) {
	    // Extract the single triple from the ElementGroup
	    // This assumes that the group contains exactly one triple block
		//valid for action triples
		//ElementGroup has only one element which is a triple
	        Element element = elementGroup.get(0);
	        if (element instanceof ElementPathBlock) {
	         	ElementPathBlock block = (ElementPathBlock) element;
	            if (block.patternElts().hasNext()) {
	                return block.getPattern().get(0).asTriple();
	            }
	        }
	    
	    throw new IllegalArgumentException("ElementGroup does not contain a valid triple");
	}
	
	public static List<Map<Var, Node>> GetMappings(Query sparqlQuery, Model kb) {
		
	    List<Map<Var, Node>> omegaMappings = new ArrayList<>();   // a list of mappings --> Omega
		  // Map<Var, Node> variableMappings =  new HashMap<>() ;
	    QueryExecution qe = QueryExecutionFactory.create(sparqlQuery,kb);
	    ResultSet results = qe.execSelect();

	     // Collect all the mappings (QuerySolutions) and return them

	     while (results.hasNext()) {
	    	    QuerySolution qs = results.nextSolution();
	    	    Map<Var, Node> variableMappings = new HashMap<>();

	    	    Iterator<String> varNames = qs.varNames();
	    	    while (varNames.hasNext()) {
	    	        String varName = varNames.next();
	    	        RDFNode rdfNodeValue = qs.get(varName);
	    	        if (rdfNodeValue != null) {
	    	            variableMappings.put(Var.alloc(varName), rdfNodeValue.asNode());
	    	        }
	    	    }
	    	    omegaMappings.add(variableMappings);
	    	}

	     	 
	     qe.close();
	     return omegaMappings;
	 }
	 
//mapGraphPattern
	public static ElementGroup map (Element element,  Map<Var, Node> variableMappings) {
		 
	    ElementGroup mappedElement = new ElementGroup();
	       if (element instanceof ElementGroup) {

	           ElementGroup group = (ElementGroup) element;
	           // Iterate through all elements in the group
	           for (Element elem: group.getElements()) {

	            // process BGP  // could be extended to handle property paths
	            if (elem instanceof ElementPathBlock) {      
				    	//System.out.println("***");
				    	ElementPathBlock pathBlock = (ElementPathBlock) elem;
				        ElementPathBlock newPathBlock = new ElementPathBlock();

				    	 for (TriplePath triplePath : pathBlock.getPattern()) {
		                        // Check if it's a simple triple and add to the BasicPattern
		                        if (triplePath.isTriple()) {
		                       
		                            Triple triple = triplePath.asTriple();
	                           	//System.out.println("triple "+triple.toString() );
		                            newPathBlock= processTriple(triple, variableMappings);
	                           	//System.out.println("newPathBlock "+newPathBlock.toString() );

	      				    	 mappedElement.addElement(newPathBlock);;

		                        }
		       }
				    	 
	            }
	            else if (elem instanceof ElementUnion) {
	           	  ElementUnion union = (ElementUnion) elem;
	                 ElementUnion newUnion = new ElementUnion();
	                 for (Element unionPart : union.getElements()) {
	                     Element mappedUnionPart = map(unionPart, variableMappings);
	                     newUnion.addElement(mappedUnionPart);
	                     //System.out.println("Union "+newUnion.toString() );

	                 }
	                     mappedElement.addElement(newUnion);

	            }
	            
	            else if  (elem instanceof ElementMinus) {
	           	
	           	 ElementMinus minus=(ElementMinus) elem;
	           	 Element mappedMinusPart = map (minus.getMinusElement(), variableMappings);
	           	 mappedElement.addElement(mappedMinusPart);	 
	           	 
	           	 
	            }
	            else if  (elem instanceof ElementOptional) {
	            //System.out.println("Optional element" +elem);
	           	ElementOptional optional = (ElementOptional) elem;
	           	Element mappedOptionalPart = map (optional.getOptionalElement(),variableMappings);
	           	mappedElement.addElement(mappedOptionalPart);
	           	 
	            }
	  
	            else if (elem instanceof ElementFilter) {
	           	  ElementFilter filter = (ElementFilter) elem;
	                 Expr expr = filter.getExpr();
	                 // Check if the filter contains a NOT EXISTS expression
	                   if (expr instanceof E_NotExists) {
	                       E_NotExists notExistsExpr = (E_NotExists) expr;
	                       Element notExistsElement = notExistsExpr.getElement();
	                       // Map the inner element of the NOT EXISTS
	                       Element mappedNotExistsElement = map(notExistsElement, variableMappings);
	                       // Recreate the NOT EXISTS expression with the mapped element
	                       Expr mappedExpr = new E_NotExists(mappedNotExistsElement);
	                       mappedElement.addElement(new ElementFilter(mappedExpr));
	                   } 
		                  // Check if the filter contains an  EXISTS expression

	                   else if 
	                   (expr instanceof E_Exists) {
	                   	E_Exists existsExpr = (E_Exists) expr;
	                       Element existsElement = existsExpr.getElement();
	                       // Map the inner element of the NOT EXISTS
	                       Element mappedExistsElement = map(existsElement, variableMappings);
	                       // Recreate the NOT EXISTS expression with the mapped element
	                       Expr mappedExpr = new E_Exists(mappedExistsElement);
	                       mappedElement.addElement(new ElementFilter(mappedExpr));
	                   } 
	                   
	                   else {
	                       // Standard FILTER handling with variable substitution
	                       Binding binding = createBinding(variableMappings);
	                       Expr mappedExpr = expr.copySubstitute(binding);
	                       mappedElement.addElement(new ElementFilter(mappedExpr));
	                   }
	                
	            }
		 
	            else if (elem instanceof ElementBind) {
	           	  // Handle BIND expressions
	                ElementBind bind = (ElementBind) elem;
	                Var var = bind.getVar();
	               // System.out.println(" Bind Expression: " + var + " = " +bind.getExpr());

	                // Create a Binding from the variable mappings
	                Binding binding = createBinding(variableMappings);

	                // Replace variables in the bind expression
	                Expr mappedExpr = bind.getExpr().copySubstitute(binding);
	               // System.out.println("Mapped Bind Expression: " + var + " = " + mappedExpr);
	                mappedElement.addElement(new ElementBind(var, mappedExpr));	                
	            }

	           }
	           
	      }
			return mappedElement;
	   }

	public static ElementPathBlock processTriple(Triple triple, Map<Var, Node> variableMappings) {
	    ElementPathBlock newPathBlock = new ElementPathBlock();

	    Node s = triple.getSubject();
	    Node p = triple.getPredicate();
	    Node o = triple.getObject();

	    if (s.isNodeTriple()) {
	        Triple inner = s.getTriple();
	        Triple replacedInner = Triple.create(
	                replaceNode(inner.getSubject(), variableMappings),
	                replaceNode(inner.getPredicate(), variableMappings),
	                replaceNode(inner.getObject(), variableMappings)
	        );
	        Node quotedSubject = NodeFactory.createTripleNode(replacedInner);
	        Triple newTriple = Triple.create(quotedSubject, replaceNode(p, variableMappings), replaceNode(o, variableMappings));
	        newPathBlock.addTriplePath(new TriplePath(newTriple));
	    } else if (o.isNodeTriple()) {
	        Triple inner = o.getTriple();
	        Triple replacedInner = Triple.create(
	                replaceNode(inner.getSubject(), variableMappings),
	                replaceNode(inner.getPredicate(), variableMappings),
	                replaceNode(inner.getObject(), variableMappings)
	        );
	        Node quotedObject = NodeFactory.createTripleNode(replacedInner);
	        Triple newTriple = Triple.create(replaceNode(s, variableMappings), replaceNode(p, variableMappings), quotedObject);
	        newPathBlock.addTriplePath(new TriplePath(newTriple));
	    } else {
	        Triple newTriple = Triple.create(
	                replaceNode(s, variableMappings),
	                replaceNode(p, variableMappings),
	                replaceNode(o, variableMappings)
	        );
	        newPathBlock.addTriplePath(new TriplePath(newTriple));
	    }

	    return newPathBlock;
	}

		
	private static boolean isReifiedTriple(Triple triple) {
		
		Boolean reified=false;
	 // Check if the subject is a quoted triple
	 if (triple.getSubject().isNodeTriple()) {
	     //System.out.println("Subject is a quoted triple: " + triple.getSubject().getTriple());
	     reified=true;
	 }

	 // Check if the object is a quoted triple
	 if (triple.getObject().isNodeTriple()) {
	     //System.out.println("Object is a quoted triple: " + triple.getObject().getTriple());
	     reified=true;

	 }
	  
	    return reified;
	}
	private static Node replaceNode(Node node, Map<Var, Node> variableMappings) {
	    if (node.isVariable()) {
	        Var var = Var.alloc(node);
	        if (variableMappings.containsKey(var)) {
	            return variableMappings.get(var);
	        }
	    }
	    return node;  // Return the original node if no mapping is found.
	}

	private static Binding createBinding(Map<Var, Node> variableMappings) {
	    BindingBuilder bindingBuilder = BindingBuilder.create();
	    for (Map.Entry<Var, Node> entry : variableMappings.entrySet()) {
	        bindingBuilder.add(entry.getKey(), entry.getValue());
	    }
	    return bindingBuilder.build();
	}
	
	/*public static List<ElementGroup> executeGraphPattern (Query sparqlQuery, Dataset kb)
	 * {
		List<ElementGroup> listMappedElements = new ArrayList <>();
	    // Parse the SPARQL query
	    //Query query = QueryFactory.create(sparqlQuery);


	    // return mappings omega

	    List<Map<Var, Node>> omegaMappings = GetMappings (sparqlQuery, kb);
	    
	    // Extract the query pattern (the WHERE clause of the query)
	    Element queryPattern = sparqlQuery.getQueryPattern();
	    
	    //apply mappings 
	    for (Map<Var, Node> m: omegaMappings)
	    {
	    	Map<Var, Node> variableMappings =m;
	    	ElementGroup group = mapGraphPattern (queryPattern,variableMappings);
	    	listMappedElements.add(group);
	    }
	    
	   return listMappedElements;
	}
	 * 
	 * 
	 * 
	 */
}
