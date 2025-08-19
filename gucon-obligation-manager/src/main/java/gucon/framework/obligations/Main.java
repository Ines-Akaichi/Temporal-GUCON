package gucon.framework.obligations;

import java.io.File;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.jena.sys.JenaSystem;

import gucon.framework.dbsms.GraphDBStorageProvider;
import gucon.framework.dbsms.TDB2StorageProvider;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		JenaSystem.init();

	      if (args.length < 2) {
	            System.err.println("Usage: java -jar myApp.jar <kb> <rules> [dateTime]");
	            System.exit(1);
	        }

	        String ttlPath = args[0];
	        String rulePath = args[1];

	        // Determine dateTime string
	        String inputDateTimeStr;
	        if (args.length == 3) {
	            inputDateTimeStr = args[2];
	        } else {
	            inputDateTimeStr = OffsetDateTime.now().toString();
	        }


	        // Build report filename using the dateTime now
	        String dateTimeNowStr = OffsetDateTime.now().toString();
	        OffsetDateTime dateTimeNow = OffsetDateTime.parse(dateTimeNowStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	        String dateTimeForFile = dateTimeNow.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
	        String reportName = "report_" + dateTimeForFile + ".ttl";

	        // --- main ---
	        File ttlFile = new File(ttlPath);
	        String kbContextIRIString = "http://example.org/kb";
	        String kbFileName = ttlFile.getName().replace(".ttl", "");

	        // create directory for database
	        File databaseParentDir = new File(ttlFile.getParentFile().getParent(), "database");
	        if (!databaseParentDir.exists()) databaseParentDir.mkdirs();
	        File tdbDirFile = new File(databaseParentDir, kbFileName);
	        String tdbDir = tdbDirFile.getAbsolutePath();
	        
	        // create directory for reports
	        File reportParentDir = new File(ttlFile.getParentFile().getParent(), "reports");
	        if (!reportParentDir.exists()) reportParentDir.mkdirs();
	        File reportFile = new File(reportParentDir, reportName);
	        String reportFilePath = reportFile.getAbsolutePath();
	        

	        // Initialize TDB2
	        TDB2StorageProvider tdb2Provider = new TDB2StorageProvider(tdbDir);
	        tdb2Provider.loadFromTurtle(ttlPath, kbContextIRIString);

	        System.out.println("work in progres ... ");

	        
	        // Call the different GUCON State Manager
	        KnowledgeBaseManager kbManager = new KnowledgeBaseManager(tdb2Provider, kbContextIRIString);
	        PolicyManager ruleManager = new PolicyManager(kbManager, rulePath);
	        List<Rule> rules = ruleManager.loadRules();
	        ObligationStateManager obligationManager = new ObligationStateManager(kbManager, inputDateTimeStr);
	        ComplianceReport report = obligationManager.generateComplianceReport(rules);
	        report.saveReport(reportFilePath);

	        tdb2Provider.close();

	        //System.out.println("Report saved at: " + reportFilePath);
	   
}

}