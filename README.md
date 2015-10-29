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
- OAuth setup for the org to get Client ID and Client Secret using connected app
  - http://salesforce.stackexchange.com/questions/40346/where-do-i-find-the-client-id-and-client-secret-of-an-existing-connected-app
  - http://stackoverflow.com/questions/18464598/where-to-get-client-id-and-client-secret-of-salesforce-api-for-rails-3-2-11 
  - http://www.calvinfroedge.com/salesforce-how-to-generate-api-credentials/
  - Please verify the oauth setup for the org by executing the following command : 
```shell
curl -v <Salesforce_Org_URL>/services/oauth2/token -d "grant_type=password" -d "client_id=*CLIENT_ID_GOES_HERE*" -d "client_secret= *CLIENT_SECRET_GOES_HERE*" -d "username=*yourusername@yourdomain.com*" -d "password= *your_password_goes_here*"
```
*The above command should provide you the access_token in the response. If you have special characters in your password you will have to pass URL-encoded password plus the security token*
  
## How to build and execute?
- Clone the project onto your local system using the command:
```shell
 git clone https://github.com/forcedotcom/ApexUnit.git 
``` 
This would create a local copy of the project for you.
- (Optional) Open the project in an IDE(eclipse, intelliJ etc.) 
- There are two ways in which user can select the list of tests to execute and the lsit of classes to examine the code coverage
  - regex - identify and provide regex for the test class names that you want to execute for the "-regex.for.selecting.test.classes.to.execute" parameter. Example: if you want to execute the tests: My_Apex_controller_Test, My_Apex_builder_Test and My_Apex_validator_Test, identify the regex as "My_Apex_\*_Test". Pass the parameter "-regex.for.selecting.test.classes.to.execute My_Apex_\*_Test" while executing the mvn command.
    - Similarly, you can provide regex for the classes for which you want to examine the code coverage by using "-regex.for.selecting.source.classes.for.code.coverage.computation My_Apex_\*_Class" while executing the mvn command.
  - Manifest files - List of tests can be read from Manifest files. Create a manifest file say ManifestFile_Unit_Tests.txt in "src/main/resources" location of your project. Add test names to execute in the manifest file. Specify this manifest file as "-manifest.files.with.test.class.names.to.execute ManifestFile_Unit_Tests.txt" while executing the mvn command. 
    - Similarly, add the class names for which you want to exercise code coverage in a manifest file say ClasssManifestFile.txt in "src/main/resources" location of your project and Specify this manifest file as "-manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt" while executing the mvn command. 
  - Note that multiple regexes and manifest files can be specified using comma seperation(without spaces). Example: "-regex.for.selecting.test.classes.to.execute This_Is_Regex1\*,\*Few_Test,Another_\*_regex -manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt,MoreClassesManifestFile.txt"
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
**Please replace all $xyz with the values specific to your environment/project**

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
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner" -Dexec.args=" -org.login.url https://na14.salesforce.com -org.username yourusername@salesforce.com -org.password yourpassword-org.wide.code.coverage.threshold 75  -team.code.coverage.threshold 80 -org.client.id CLIENT_ID_FROM_CONNECTED_APP -org.client.secret CLIENT_SECRET_FROM_CONNECTED_APP -regex.for.selecting.test.classes.to.execute Sample*Test,Sample*test -regex.for.selecting.source.classes.for.code.coverage.computation Sample*,Mobile*Controller,Wrapper -manifest.files.with.test.class.names.to.execute ManifestFile.txt -manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt -max.test.execution.time.threshold 10"
```
Note: Multiple comma separated manifest files and regexes can be provided.Please do not include spaces while providing multiple regex or manifest files.

On successful completion - the command should give you build success and it should generate two report files - **ApexUnitReport.xml** (This is the test report in JUnit format) and **Report/ApexUnitReport.html** (This is the code coverage report in html format)

# Using Manifest files and Regexes

Users can populate class names in the Manifest file and/or provide regular expressions(regexes) 
Please refer https://github.com/forcedotcom/ApexUnit/wiki/Manifest-file-vs-regex for the usecases where manifest file(s) and regex(es) option can be used

## Integrating with CI pipeline
CI engines like Jenkins(https://jenkins-ci.org/) can be used to seamlessly integrate ApexUnit with CI pipelines.

Prerequisites : 
 - Checkin your ApexUnit(maven) project to a Source Control Management(SCM) system
 - If you are using Jenkins, you would need the following plugins
   - Plugin specific for your SCM
     - Mask passwords plugin : https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin. This helps in masking passwords and client id/secret if you parameterize your build for the cli options
     - Performance plugin - https://wiki.jenkins-ci.org/display/JENKINS/Performance+Plugin. This plugin allows you to capture reports from JUnit. ApexUnit generates test report in standard JUnit format
     - HTML Publisher plugin : https://wiki.jenkins-ci.org/display/JENKINS/HTML+Publisher+Plugin. This plugin helps in publishing the consolidated HTML report generated by the tool
 - Generic instructions on building a project using Jenkins : https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project 

Steps for creating a jenkins job for ApexUnit: 
  - Create a "Free style project" using jenkins
  - (Optional)Select the "This build is parameterized" checkbox and parameterize the build by adding relevant (String/password) parameters for all of the cli options
  - Select the SCM where you checked in your code and update the location of source code to your code on SCM
  - Add the build step "Invoke Top level maven targets" with the command mentioned above in the "How to build" section. Use the parameters if you parameterized the build, otherwise provide the entire command with hard-coded values
  - In the post-build Actions step:
    - Add "Publish HTML reports" step and populate 'HTML directory to archive' field with 'Report' and 'Index page[s]' field with 'ApexUnitReport.html' and choose your 'Report title'
    - Add "Publish JUnit test result report" step and populate 'ApexUnitReport.xml' for the 'Test Report XMLs' field
    - If you would like to get email notifications on the build status for the job, add 'E-mail Notification' step and populate the 'Recipients' field with your and possible your teams email id(s)

## How to contribute or track Bug/Issue for ApexUnit?
- We encourage users to contribute to the code base. 
- Got new ideas/suggestions to improve ApexUnit? Feel free to send out pull requests with detailed description.
- Found issues with the ApexUnit? Please log and track bug/issue using https://github.com/forcedotcom/ApexUnit/issues
We resolve P0 issues ASAP. Lower priority bugs will be addressed based on prioritization and availability of the contributors. Please feel free to suggest resolutions to the open issues.

## Questions?
Questions or share feedback? We would love to talk to you. Please feel free to contact the Salesforce IT-Continuous Delivery team via email: it-tools@salesforce.com
