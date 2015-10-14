ApexUnit
========
This repo is the core of ApexUnit 2.x. ApexUnit 2.x is a powerful continuous delivery tool for Force.com platform. 

## Overview
ApexUnit 2.x is a Java application and it leverages the features exposed by the Force.com platform to queue and submit Apex tests for execution. Force.com tooling API's is used for fetching code coverage. 
The tool comprises of two major components:
1. A xUnit based testing framework for Force.com platform; 
2. Extensive code coverage metrics with actionable detail for Apex source code. The code coverage results can be a component of the CI/CD pipeline.
ApexUnit 2.x is intended to exercise integrated Force.com objects functionality through APIs beneath the Force.com UI layer.

## Key Features of ApexUnit 2.x

- Queue and schedule Apex test runs asynchronously
- Filter and group tests(xUnit based)
  - Multi-Manifest file and Multi-Regex support for filtering and grouping the tests and sources classes
- Summary of code coverage metrics and test execution with pass/fail rate
  - Detailed test report in standard JUnit xml format with drill down feature for failure analysis
  - Info on covered and uncovered code lines including line numbers
  - Code coverage metrics for individual classes; sorted in ascending order of code coverage%
  - Color schemes(red-green) to highlight classes with low code coverage 
- Automatic health checks 
  - Customizable code coverage thresholds at team level and org-wide level
  - Halts the tool when code coverage thresholds are not met and/or when there are test failures
  - Self-abort long running tests using customizable timeout threshold
- Seamless integration with Jenkins and CD pipeline

# Additional features
- Live test status logging
  - Completed test execution count and remaining tests count
  - Periodic test status logging
  - Test execution time for each test
- Handling and reporting the duplicate/invalid test class name entries in manifest files/regexes
- Man page with details on available command line parameter options (-help)

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
  
## How to build?
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
- -help : Displays options available for running this application

Note: User must provide either of the (-regex.for.selecting.source.classes.for.code.coverage.computation OR -manifest.files.with.source.class.names.for.code.coverage.computation) AND either of  -(regex.for.selecting.test.classes.to.execute OR -manifest.files.with.test.class.names.to.execute)

Sample command: 
```java
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner" -Dexec.args=" -org.login.url https://na14.salesforce.com -org.username rama_krishna@salesforce.com -org.password ****** -org.wide.code.coverage.threshold 75  -team.code.coverage.threshold 80 -org.client.id ******* -org.client.secret ***** -regex.for.selecting.test.classes.to.execute Sample*Test,Sample*test -regex.for.selecting.source.classes.for.code.coverage.computation Sample,Mobile,Wrapper -manifest.files.with.test.class.names.to.execute ManifestFile.txt -manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt -max.test.execution.time.threshold 10"
```
Note: Multiple comma separated manifest files and regexes can be provided.Please do not include spaces while providing multiple regex or manifest files.

On successful completion - the command should give you build success and it should generate two report files - **ApexUnitReport.xml** (This is the test report in JUnit format) and **Report/ApexUnitReport.html** (This is the code coverage report in html format)

# Using Manifest files and Regexes

Users can populate class names in the Manifest file and/or provide regular expressions(regexes) to filter the Apex classes
Use cases

**Manifest files:** 
- The user can populate class names in manifest files. Multiple Manifest files can be created for unit tests, functional tests, integration tests etc. 
- If your team has legacy test/source classes, for which no naming conventions have been followed, you can populate class names in the manifest file. Another use case for using Manifest file is if your team is transitioning from ApexUnit 1.0 and have the setup for manifest files, the team can continue to use the setup already in place
- Multiple-manifest file support is provided; user can specify multiple comma separated manifest files.

**Regexes:** 
- Worried about maintaining manifest files each time a new Apex class is created or each time a class is renamed? Regular expressions(regexes) solves the overhead of maintaining the manifest files. 
- In order to use regex, you must make sure that your classes follow a specific naming conventions like TEAM_PROJECT_MODULE_UNITTEST_MY-TEST-NAME.. In such a case you can specify the regex- "TEAM_PROJECT_MODULE_UNITTEST_*"; this would fetch all the classes from the org whose name matches the regex. 
- If you do not wish to rename your existing apex classes, you could use manifest files to filter the classes. However, you could start leveraging the regex-support feature by making sure that the new classes follow a naming convention.
- Multiple-regex support is provided; user can specify multiple comma separated regexes. 

## Integrating with CI pipeline
ApexUnit can be seamlessly integrated with CI pipelines. CI engines like Jenkins(https://jenkins-ci.org/) can be used to achieve the same. 
Prerequisites : 
 - Checkin your maven project to a Source Control Management(SCM) system
 - If you are using Jenkins, you would need the following plugins
   - Plugin specific for your scm
   - Mask passwords plugin : https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin. This helps in masking passwords and client id/secret if you parameterize your build for the cli options
   - Performance plugin - https://wiki.jenkins-ci.org/display/JENKINS/Performance+Plugin. This plugin allows you to capture reports from JUnit. ApexUnit generates test report in standard JUnit format
   - HTML Publisher plugin : https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin. This plugin helps in publishing the consolidated HTML report generated by the tool
 - Generic instructions on building a project using Jenkins : https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project 

Steps for creating a jenkins job for ApexUnit: 
  - Create a "Free style project" using jenkins
  - (Optional)Select the "This build is parameterized" checkbox and parameterize the build by adding relevant (String/password) parameters for all of the cli options
  - 

## Questions?
Questions or share feedback? email it-tools@salesforce.com


