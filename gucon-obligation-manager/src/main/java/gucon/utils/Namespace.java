package gucon.utils;

import org.apache.jena.rdf.model.Model;

public class Namespace {
    private Namespace() {} // Prevent instantiation
    
    // Prefixes
    public static final String UCP_PREFIX   = "ucp";
    public static final String XSD_PREFIX   = "xsd";
    public static final String EX_PREFIX    = "ex";
    public static final String AFN_PREFIX   = "afn";
    public static final String GUCON_PREFIX = "gucon";
    public static final String EMR_PREFIX   = "emr";
    public static final String RDF_PREFIX   = "rdf";
    public static final String GC_PREFIX = "gc";

    // URIs
    public static final String UCP_URI     = "http://www.wu.ac.at/2024/ucp#";
    public static final String XSD_URI     = "http://www.w3.org/2001/XMLSchema#";
    public static final String EX_URI      = "http://example.com/ns#";
    public static final String AFN_URI     = "http://jena.apache.org/ARQ/function#";
    public static final String GUCON_URI   = "http://www.wu.ac.at/2024/gucon#";
    public static final String EMR_URI     = "http://wu.ac.at/domain/emr#";
    public static final String RDF_URI     = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String GC_URI ="http://www.wu.ac.at/2025/gucon-compliance#";
    
    // Optional: register all namespaces to a Jena Model at once
    public static void registerAll(Model model) {
        model.setNsPrefix(UCP_PREFIX, UCP_URI);
        model.setNsPrefix(XSD_PREFIX, XSD_URI);
        model.setNsPrefix(EX_PREFIX, EX_URI);
        model.setNsPrefix(AFN_PREFIX, AFN_URI);
        model.setNsPrefix(GUCON_PREFIX, GUCON_URI);
        model.setNsPrefix(EMR_PREFIX, EMR_URI);
        model.setNsPrefix(RDF_PREFIX, RDF_URI);
        model.setNsPrefix(GC_PREFIX, GC_URI);  
    }
    

	
}
