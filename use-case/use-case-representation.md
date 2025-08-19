## Use Case

OncoAid is an application designed to support health monitoring for cancer patients by enabling them to manage their medical and health-related data. 
The app is integrated with a smartwatch that continuously collects and monitors vital signs (eg., heart rate and blood pressure), 
which are stored in the patient's personal KG. In addition to real-time monitoring, OncoAid provides access to key medical data, including electronic medical records, lab results, imaging, medication plans, and diagnosis notes by the doctor. 
This data is maintained in the hospital's KG. Patients using OncoAid can view and manage their personal data through their own KG while also accessing relevant hospital data. 
In contrast, doctors are responsible for managing hospital data and, when necessary, can access the patient’s KG to provide informed medical care.

**Running Example.**
Alice is admitted to CityCare Hospital for treatment. Upon admission, her corresponding doctor, Dr. Smith, performs different laboratory tests.
By the end of the admission date, a diagnosis is concluded, and all diagnosis notes and laboratory results are stored in the hospital KG. Figure 1 depicts the running example.
During Alice's admission period in the hospital, different scenarios are envisioned as follows:

**Scenario 1** During her stay, Dr. Smith orders lab tests, in which the results are stored in the hospital KG. Dr. Smith must share a treatment plan with Alice after her lab results are ready. 

**Scenario 2** As Alice’s discharge date approaches, she must review and electronically sign her discharge forms before the scheduled end of her admission period. This e-signed document is securely stored in the hospital’s KG.

**Scenario 3** After Alice is discharged from CityCare Hospital, her doctor has 12 hours to sign and finalize her diagnosis report. 

The different scenarios highlight a range of obligations related to data usage and standard hospital procedures. 
These obligations may involve, for example, sharing a treatment plan following a specific event, signing a document before a set deadline, or approving a request within a defined timeframe. 
Each obligation is regulated by temporal rules that determine whether an action must occur before, after, or within a specified time window. 
For instance, framing Scenario 3 as a temporal obligation allows it to be evaluated: the obligation is considered fulfilled if the doctor’s request is approved within the permitted period, or violated if no action is taken before the deadline (e.g., within 24 hours).

![the medical use case](https://github.com/Ines-Akaichi/GUCON-Extension/blob/main/use-case/medical-use-case.jpg)
                  **Figure 1**. The Hospital Care Admission Use Case

## A vocabulary for the Hospital Care Admission Use Case

To model our use case, we developed an ontology based on the EMRBots database schema [EMRBots](ttps://github.com/kartoun/emrbots), which offers synthetic electronic medical records for 100 patients. EMRBots simulates the structure and content of real-world medical databases, including information on patient admissions, demographics, socioeconomic status, lab results, and more.
The resulting ontology, named the Hospital Inpatient Care (HIC), is illustrated in Figure 2. We use the prefix `hc: <http://wu.ac.at/domain/hospital-inpatient-care>` to denote concepts from the HIC ontology.

![the hospital care admission ontology](https://github.com/Ines-Akaichi/GUCON-Extension/blob/main/use-case/hospital-care-admission-use-case.png)
                  **Figure 2**. An Ontology for The hospital Care Admission Use Case
                  
                  
## Representing Hospital Care Admission Use Case the HIC ontology.

Using the HIC ontology and the GUCON~t~ policy language, the various scenarios described can be represented as follows:

### Scenario 1
      {
      ?doctor a  hc:Doctor .
      ?treatmentPlan a  hc:TreatmentPlan .
      ?patient  a  hc:Patient .
      ?patient hc:hasResponsibleDoctor ?doctor .
      ?labTest  a  hc:LabTest .
      ?labTest   hc:hasPatient   ?patient .
      ?labTest hc:hasLabResult  ?labResult .
      ?treatmentPlan hc:isBasedOnLabResult   ?labResult .
      ?labResult hc:hasLabResultDatetime ?labResultDateTime .
      BIND (?labResultDateTime AS ?startTime) .
      }
      --> O {<<?doctor gucon:share   ?treatmentPlan>> gucon:startTime ?startTime}

### Scenario 2
      {
      ?patient a hc:Patient .
      ?dischargeForm a hc:DischargeForm .
      ?admission a hc:Admission .
      ?admission hc:hasPatient  ?patient .
      ?admission hc:hasDischargeForm ?dischargeForm .
      ?admission  hc:hasExpectedAdmissionEndDate  ?expectedAdmissionEndDate .
      BIND (?expectedAdmissionEndDate AS ?deadline) .
      }
      --> O {<<?patient gucon:sign  ?dischargeForm>> gucon:deadline ?deadline }


### Scenario 3
    {
    ?doctor a hc:Doctor .
    ?diagnosisReport a hc:DiagnosisReport .
    ?admission a hc:Admission .
    ?admission hc:hasPatient ?patient .
    ?admission hc:hasActualAdmissionEndDate ?actualAdmissionEndDate .
    ?diagnosisReport hc:hasAdmission  ?admission .
    ?patient hc:hasResponsibleDoctor ?doctor .
    BIND (?actualAdmissionEndDate AS ?startTime) .
    BIND(?startTime +"PT12H"^^xsd:duration AS ?deadline)
    }
    --> O {<<?doctor gucon:sign ?diagnosisReport>> gucon:startTime ?startTime; gucon:deadline ?deadline}

