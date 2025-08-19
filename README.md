# The GUCON Obligation Manager 
The GUCON Obligation Manager (Figure 1) is implemented in Java using Apache Jena. The manager expects three primary inputs: a KB and a policy, both provided as Turtle files, and a time instant t, expressed using the W3C XML Schema Definition Language (XSD).
The Obligation Manager comprises five core components: the *Knowledge Base Manager*, the *Rule Manager*, the *Obligation State Manager*, the *Compliance Checker*, and the *Report Generator*. The Knowledge Base Manager loads the KB from a Turtle file into Jena TDB2. The Rule Manager loads and maintains the rules in memory as Java objects, enabling their evaluation against the KB. Additionally, it is responsible for augmenting the rules with optional temporal bindings. 
The Obligation State Manager is responsible for reasoning over the states of obligations, while the Compliance Checker assesses the compliance of the KB. The Report Generator produces a compliance report detailing the state of each obligation and the overall compliance status.

![the gucon obligation manager](https://github.com/Ines-Akaichi/Temporal-GUCON/blob/main/obligation-diagram-component.png)
                  **Figure 1**. The GUCON Obligation Manager
                  
# How to run the GUCON Obligation Manager ?
To run the obligation manaher, download the jar file from (figshare) [https://figshare.com/articles/software/The_GUCON_Obligation_Manager/29941160?file=57285929]. You should have Java installed
The prototpye is implemented using java.runtime.version=17.0.6+10. 
Run the prototpye using the following java command:

`java -jar  [jarpath]  [kb path]  [rule path] [dateTime]`  

# Evaluate the Performance & Scalability of the GUCON Obligation Manager

 To evaluate our prototype, we use the EMRBots dataset (100-patients) [https://figshare.com/articles/dataset/A_100-patient_database/7040039?file=12941135]  with 100 patients. The dataset is converted to an RDF graph using RMLMapper and mapping rules.
 The input graph is then fed to our generator (Figure 2). 
 
![the data generation pipeline]( https://github.com/Ines-Akaichi/Temporal-GUCON/blob/main/data-generation-pipeline.png)
                  **Figure 1**. The Data Generation Pipeline.

To run the generator, download the jar file from (figshare generator) [https://figshare.com/articles/software/Evaluation_of_the_GUCON_Obligation_Manager/29941226]. 
Run the prototpye using the following java command:

`java -jar  [jarpath]  [configPath.yaml] `

The config path expects the scale for the rules, the kb, and the relevant input time, among other things (figshare config)[https://figshare.com/articles/software/Evaluation_of_the_GUCON_Obligation_Manager/29941226]. A link to the data we used for evaluation in our paper can be found here  (figshare data)[https://figshare.com/articles/dataset/RDF_Data_For_the_EMRBots_Dataset/29941235].

We developed a test harness that works with our generated data and is found here (figshare test) [https://figshare.com/articles/software/Evaluation_of_the_GUCON_Obligation_Manager/29941226]. To run the test harness, download the jar file  and run the prototpye using the following java command:

`java -jar  [jarpath]  [kb path] [rule path] [iteartion number]`

The scripts used for evalaution are found here (scripts) [https://github.com/Ines-Akaichi/Temporal-GUCON/tree/main/scripts].
