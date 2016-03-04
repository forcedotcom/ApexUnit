[![Build Status](https://travis-ci.org/forcedotcom/ApexUnit.svg?branch=master)](https://travis-ci.org/forcedotcom/ApexUnit)
ApexUnit
========

## What is ApexUnit?
ApexUnit is a powerful continuous integration tool for Force.com platform. ApexUnit is a Java application and it leverages the features exposed by the Force.com platform to queue and submit Apex tests for execution. Force.com tooling API's are used for fetching code coverage results. ApexUnit is intended to exercise integrated Force.com objects functionality through APIs beneath the Force.com UI layer.
ApexUnit comprises of two major components:
- A xUnit based testing framework for Force.com platform; 
- Extensive code coverage metrics with actionable detail for Apex source code. The code coverage results can be a component of the CI/CD pipeline.

## Key Features of ApexUnit
Please refer https://github.com/forcedotcom/ApexUnit/wiki to know about the key fearures of ApexUnit and its usage

## Pre-Requisites
- Java 1.6 or later 
  - http://www.oracle.com/technetwork/java/javase/downloads/index.html 
- Maven 3.0.3 or later (latest version is recommended)
  - Download link : https://maven.apache.org/download.cgi 
  - Installation instructions : https://maven.apache.org/install.html
  - Configuring maven : https://maven.apache.org/configure.html
- Eclipse 3.6 or later
- OAuth setup for the org to get Client ID and Client Secret
  - http://salesforce.stackexchange.com/questions/40346/where-do-i-find-the-client-id-and-client-secret-of-an-existing-connected-app
  - http://stackoverflow.com/questions/18464598/where-to-get-client-id-and-client-secret-of-salesforce-api-for-rails-3-2-11 
  - http://www.calvinfroedge.com/salesforce-how-to-generate-api-credentials/
  - Please verify the oauth setup for the org by executing the following command : 
```shell
curl -v <Salesforce_Org_URL>/services/oauth2/token -d "grant_type=password" -d "client_id=****************" -d "client_secret= **************" -d "username=***********" -d "password= *******"
```
*The above command should provide you the access_token in the response.*
  
## How to build and execute?
- Clone the project onto your local system using the command:
```shell
 git clone https://github.com/forcedotcom/ApexUnit.git 
``` 
This would create a local copy of the project for you.
- (Optional) Open the project in an IDE(eclipse, intelliJ etc.) 
- Modify/Add manifest files for test classes (for execution) and for Apex(source) classes (for code coverage computation) in the following location in your project setup: src/main/resources
- Go to your project directory(to the directory having pom.xml) in command prompt and execute the following command:
```java
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner"
-Dexec.args="-org.login.url $Salesforce_org_url 
                     -org.username $username -org.password $password
                     -org.client.id $client_id 
                     -org.client.secret $client_secret
                     -org.wide.code.coverage.threshold $Org_Wide_Code_Coverage_Percentage_Threshold 
                     -team.code.coverage.threshold $team_Code_Coverage_Percentage_Threshold 
                     -regex.for.selecting.source.classes.for.code.coverage.computation 
                           $regex_For_Apex_Classes_To_Compute_Code_Coverage 
                     -regex.for.selecting.test.classes.to.execute $regex_For_Apex_Test_Classes_To_Execute 
                     -manifest.files.with.test.class.names.to.execute   
                           $manifest_Files_For_Apex_Test_Classes_To_Execute 
                     -manifest.files.with.source.class.names.for.code.coverage.computation 
                           $manifest_Files_For_Apex_Source_Classes_to_compute_code_coverage
                     -max.test.execution.time.threshold 
                           $max_time_threshold_for_test_execution_to_abort"
                     -proxy.host
                           $prox_host
                     -proxy.port
                           $proxy_port

``` 
*Please replace all $xyz with the values specific to your environment/project*

Required parameters: 
- -org.login.url : Login URL for the org
- -org.username : Username for the org
- -org.password  : Password corresponding to the username for the org
- -org.client.id : Client ID associated with the org. 
- -org.client.secret : Client Secret associated with the org.

Optional Parameters: 
- -org.wide.code.coverage.threshold (default value: 75) : Org wide minimum code coverage required to meet the code coverage standards
- -team.code.coverage.threshold (default value: 75) : Team wide minimum code coverage required to meet the code coverage standards
- -regex.for.selecting.source.classes.for.code.coverage.computation : The source regex used by the team for the apex source classes. All classes beginning with this parameter in the org will be used to compute team code coverage
- -regex.for.selecting.test.classes.to.execute  : The test regex used by the team for the apex test classes. All tests beginning with this parameter in the org will be selected to run
- -manifest.files.with.test.class.names.to.execute : Manifest files containing the list of test classes to be executed
- -manifest.files.with.source.class.names.for.code.coverage.computation : Manifest files containing the list of Apex classes for which code coverage is to be computed
- -max.test.execution.time.threshold : Maximum execution time(in minutes) for a test before it gets aborted
- -proxy.host : Proxy host for external access
- -proxy.port : Proxy port for external access
- -help : Displays options available for running this application

Note: User must provide either of the (-regex.for.selecting.source.classes.for.code.coverage.computation OR -manifest.files.with.source.class.names.for.code.coverage.computation) AND either of  -(regex.for.selecting.test.classes.to.execute OR -manifest.files.with.test.class.names.to.execute)

Sample command: 
```java
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner" -Dexec.args=" -org.login.url https://na14.salesforce.com -org.username test****@salesforce.com -org.password ****** -org.wide.code.coverage.threshold 75  -team.code.coverage.threshold 80 -org.client.id ******* -org.client.secret ***** -regex.for.selecting.test.classes.to.execute Sample*Test,Sample*test -regex.for.selecting.source.classes.for.code.coverage.computation Sample,Mobile,Wrapper -manifest.files.with.test.class.names.to.execute ManifestFile.txt -manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt -max.test.execution.time.threshold 10 -proxy.host your.proxy-if-required.net -proxy.port 8080"
```
Note: Multiple comma separated manifest files and regexes can be provided.Please do not include spaces while providing multiple regex or manifest files.

On successful completion - the command should give you build success and it should generate two report files - **ApexUnitReport.xml** (This is the test report in JUnit format) and **Report/ApexUnitReport.html** (This is the code coverage report in html format)

# Using Manifest files and Regexes

Users can populate class names in the Manifest file and/or provide regular expressions(regexes) 
Please refer https://github.com/forcedotcom/ApexUnit/wiki/Manifest-file-vs-regex for the usecases where manifest file(s) and regex(es) option can be used

## Integrating with CI pipeline
CI engines like Jenkins(https://jenkins-ci.org/) can be used to seamlessly integrate ApexUnit with CI pipelines.
Please find the details here: https://github.com/forcedotcom/ApexUnit/wiki/Integrating-with-CI-pipeline

## How to contribute or track Bug/Issue for ApexUnit?
https://github.com/forcedotcom/ApexUnit/wiki/Contribution-and-Bug-Issue-tracking

## Questions/Feedback?
https://github.com/forcedotcom/ApexUnit/wiki/Contact-info
