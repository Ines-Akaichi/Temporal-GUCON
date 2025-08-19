package gucon.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;

public class NamespaceUtils {

    public static String mergeNamespaceDeclarations(String... declarations) {
        Set<String> uniquePrefixes = new LinkedHashSet<>();
        for (String declaration : declarations) {
            if (declaration != null && !declaration.isBlank()) {
                uniquePrefixes.addAll(Arrays.asList(declaration.split("\n")));
            }
        }
        return String.join("\n", uniquePrefixes);
    }
    
    public static String getNameSpaceDeclaration (Model kb)
    {
            List<String> prefixesList = new ArrayList<>();
    		// Access the default model (default graph)
            //Model defaultModel = kb.getDefaultModel();
            
    	     // Get the prefix map
            Map<String, String> prefixMap = kb.getNsPrefixMap();
            // Print out each prefix and its associated namespace
            for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
              //  System.out.println("Prefix: " + entry.getKey() + " -> Namespace: " + entry.getValue());
                String prefixLine = "prefix " + entry.getKey() + ": <" + entry.getValue() + ">";
                prefixesList.add(prefixLine);
            }
            
    	    String prefixes = String.join("\n", prefixesList);

    return  prefixes;
    
   }
}
