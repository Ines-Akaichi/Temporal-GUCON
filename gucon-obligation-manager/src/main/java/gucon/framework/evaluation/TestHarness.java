package gucon.framework.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.jena.query.ARQ;
import org.apache.jena.sys.JenaSubsystemLifecycle;
import org.apache.jena.sys.JenaSystem;

import gucon.framework.dbsms.TDB2StorageProvider;
import gucon.framework.obligations.ComplianceReport;
import gucon.framework.obligations.EvaluatedRule;
import gucon.framework.obligations.KnowledgeBaseManager;
import gucon.framework.obligations.MappedRule;
import gucon.framework.obligations.ObligationStateManager;
import gucon.framework.obligations.Rule;
import gucon.framework.obligations.PolicyManager;

public class TestHarness implements JenaSubsystemLifecycle  {

	public static void main(String[] args) throws Exception {

	    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	    JenaSystem.init();
        // 🔧 Disable query result caching globally
	    
	    if (args.length < 3) {
	        System.err.println("Usage: java -jar myApp.jar <kb> <rules> <iterations>");
	        System.exit(1);
	    }

	    String ttlPath = args[0];
	    String rulePath = args[1];
	    int iterationNb = Integer.parseInt(args[2]);

	    File ttlFile = new File(ttlPath);
	    File ruleFile = new File(rulePath);

	    String kbFileName = ttlFile.getName().replace(".ttl", "");
	    String kbSizeStr = kbFileName.replaceAll("[^0-9]", "");
	    String ruleFileName = ruleFile.getName().replace(".ttl", "");
	    String ruleSizeStr = ruleFileName.replaceAll("[^0-9]", "");

	    File databaseParentDir = new File(ttlFile.getParentFile().getParent(), "database");
	    if (!databaseParentDir.exists()) databaseParentDir.mkdirs();

	    File tdbDirFile = new File(databaseParentDir, kbFileName);
	    String tdbDir = tdbDirFile.getAbsolutePath();

	    String kbContextIRIString = "http://example.org/kb";
	    String dateTime = "2010-01-10T10:44:00.000+02:00";

	    // Initialize TDB2 provider only once
	    TDB2StorageProvider tdb2Provider = new TDB2StorageProvider(tdbDir);
	    tdb2Provider.loadFromTurtle(ttlPath, kbContextIRIString);

	        // Load rules once
	        
		    Runtime runtime = Runtime.getRuntime();
		    
		 // GC to clean up before timing and measuring memory
		    awaitFullGc(); 
		    
		    
		    long startMemory = runtime.totalMemory() - runtime.freeMemory();
		    long startTime = System.nanoTime();
		    
		    
	        KnowledgeBaseManager kbManager = new KnowledgeBaseManager(tdb2Provider, kbContextIRIString);
	        PolicyManager ruleManager = new PolicyManager(kbManager, rulePath);
	        List<Rule> rules = ruleManager.loadRules();

	        // ⚠️ JVM Warm-up: run a few iterations without recording results -> Avoids cold-start bias from JIT
	        /*for (int i = 0; i < 3; i++) {
	            ObligationStateManager warmUpManager = new ObligationStateManager(kbManager, dateTime);
	            warmUpManager.generateComplianceReport(rules);
	        }*/
	        
	     //  GC to minimize memory noise
		     //System.gc(); 
		     //Thread.sleep(100);  // Allow GC to settle


		    ObligationStateManager obligationManager = new ObligationStateManager(kbManager, dateTime);
		    obligationManager.generateComplianceReport(rules);

		 // GC after workload (optional: for post-analysis)
		    
		    awaitFullGc();  // <- Ensure memory is cleaned up consistently
		    
		    long endTime = System.nanoTime();
		    long endMemory = runtime.totalMemory() - runtime.freeMemory();

		    long elapsedTimeInMillis = (endTime - startTime) / 1_000_000;
		    long memoryUsedInKB = Math.abs(endMemory - startMemory) / 1024;
		    
		    //System.out.printf("Iteration %d - Time: %d ms, Memory: %d KB%n", iterationNb, elapsedTimeInMillis, memoryUsedInKB);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Return output as a single CSV-formatted line (no header)
            System.out.printf("%d,%s,%s,%s,%d,%d%n", iterationNb, timestamp, kbSizeStr, ruleSizeStr, elapsedTimeInMillis, memoryUsedInKB);
            
	        //report.saveReport(reportPath);

	    
	    tdb2Provider.close();
	}

	private static void awaitFullGc() {
	    final java.util.concurrent.CountDownLatch finalizerRan = new java.util.concurrent.CountDownLatch(1);
	    Object obj = new Object() {
	        @Override protected void finalize() {
	            finalizerRan.countDown();
	        }
	    };
	    java.lang.ref.WeakReference<Object> ref = new java.lang.ref.WeakReference<>(obj);

	    obj = null; // Clear strong reference

	    System.gc(); // Suggest GC
	    try {
	        finalizerRan.await(); // Wait for finalize() to run
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }

	    // Wait until reference is cleared
	    for (int i = 0; i < 10 && ref.get() != null; i++) {
	        System.gc();
	        try {
	            Thread.sleep(50);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }

	    System.runFinalization(); // Catch stragglers
	}


    
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}


}