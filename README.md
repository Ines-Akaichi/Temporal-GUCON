# The GUCON Obligation Manager 

The **GUCON Obligation Manager** (see Figure 1) is implemented in Java using **Apache Jena**.  
It requires three primary inputs:  

1. **Knowledge Base (KB)** – provided as a Turtle file  
2. **Policy** – provided as a Turtle file  
3. **Time instant `t`** – expressed using the W3C XML Schema Definition Language (XSD)
   
It returns a compliance report detailing the state of each obligation and the overall compliance status.  

## System Components  

The Obligation Manager is composed of **five core components**:  

- **Knowledge Base Manager**  
  - Loads the KB from a Turtle file into **Jena TDB2**.  
- **Rule Manager**  
  - Loads and maintains rules in memory as Java objects.  
  - Evaluates rules against the KB.  
  - Augments rules with optional temporal bindings.  
- **Obligation State Manager**  
  - Performs reasoning over the states of obligations.  
- **Compliance Checker**  
  - Assesses the compliance of the KB with respect to obligations.  
- **Report Generator**  
  - Produces a compliance report.  

![the gucon obligation manager](https://github.com/Ines-Akaichi/Temporal-GUCON/blob/main/obligation-diagram-component.png) 

**Figure 1**.The GUCON Obligation Manager 

## Running the GUCON Obligation Manager  

1. Download the JAR file from Figshare: [The GUCON Obligation Manager JAR](https://figshare.com/articles/software/The_GUCON_Obligation_Manager/29941160?file=57285929)  
2. Ensure that **Java 17** is installed (`java.runtime.version=17.0.6+10`).  
3. Run the prototype with: `java -jar [jarpath] [kb path] [rule path] [dateTime]`

# Evaluate the Performance & Scalability of the GUCON Obligation Manager 

To evaluate our prototype, we use the EMRBots dataset (100-patients) [https://figshare.com/articles/dataset/A_100-patient_database/7040039?file=12941135] with 100 patients. The dataset was converted to an RDF graph using RMLMapper and mapping rules. The input graph is then fed to our generator (see Figure 2). 

![the data generation pipeline]( https://github.com/Ines-Akaichi/Temporal-GUCON/blob/main/data-generation-pipeline.png) 

**Figure 2**. The Data Generation Pipeline. 

## Running the Data Generator   
1.  Download the jar file from Figshare: [figshare generator](https://figshare.com/articles/software/Evaluation_of_the_GUCON_Obligation_Manager/29941226).
2.  Run the prototpye with: `java -jar [jarpath] [configPath.yaml]`

The config path specifies the scale for the rules, the kb, and the relevant input time, among other things [figshare config](https://figshare.com/articles/software/Evaluation_of_the_GUCON_Obligation_Manager/29941226).

## Running the Test Harness   
A link to the data we used for evaluation in our paper can be found here: [figshare data](https://figshare.com/articles/dataset/RDF_Data_For_the_EMRBots_Dataset/29941235).
We developed a test harness that works with our generated data. To run the test harness:

1. Download the jar file [figshare test](https://figshare.com/articles/software/Evaluation_of_the_GUCON_Obligation_Manager/29941226)
2.  run the prototpye using the following java command:  `java -jar [jarpath] [kb path] [rule path] [iteartion number]`

The scripts used for evaluation can be found here [scripts](https://github.com/Ines-Akaichi/Temporal-GUCON/tree/main/scripts).
