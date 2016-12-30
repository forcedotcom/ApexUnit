[![Build Status](https://travis-ci.org/forcedotcom/ApexUnit.svg?branch=master)](https://travis-ci.org/forcedotcom/ApexUnit)
ApexUnit
========

## What is ApexUnit?
ApexUnit is a continuous integration tool for the Force.com platform that executes Apex tests and fetches code coverage results.

ApexUnit comprises of two major components:
- An xUnit based testing framework for the Force.com platform 
- Extensive code coverage metrics with actionable detail for specified Apex classes

## Key Features of ApexUnit
Please refer https://github.com/forcedotcom/ApexUnit/wiki to learn more about the key features of ApexUnit and its usage

## Pre-Requisites
- Java 1.6 or later 
  - http://www.oracle.com/technetwork/java/javase/downloads/index.html 
- Maven 3.0.3 or later (latest version is recommended)
  - Download link : https://maven.apache.org/download.cgi 
  - Installation instructions : https://maven.apache.org/install.html
  - Configuring maven : https://maven.apache.org/configure.html
- OAuth setup for the org to retrieve the Client ID and Client Secret using a Connected App
  - http://salesforce.stackexchange.com/questions/40346/where-do-i-find-the-client-id-and-client-secret-of-an-existing-connected-app
  - http://stackoverflow.com/questions/18464598/where-to-get-client-id-and-client-secret-of-salesforce-api-for-rails-3-2-11 
  - http://www.calvinfroedge.com/salesforce-how-to-generate-api-credentials/
  - Please verify the oauth setup for the org by executing the following command: 
```shell
curl -v <Salesforce_Org_URL>/services/oauth2/token -d "grant_type=password" -d "client_id=*CLIENT_ID_GOES_HERE*" -d "client_secret= *CLIENT_SECRET_GOES_HERE*" -d "username=*yourusername@yourdomain.com*" -d "password= *your_password_goes_here+*"
```
*The above command should provide you the access_token in a JSON formatted response. If you are running this command from an IP address that is outside of the trusted IP range specified by your connected app, you must append a [security token](https://help.salesforce.com/apex/HTViewHelpDoc?id=user_security_token.htm&language=en) to your password.
+ If your password contains special characters, you must pass in a URL encoded version of your password. Note that this only needs to be done for the curl command and not the maven command
  
## How to build and execute ApexUnit
- Clone the project onto your local system using the command:
```shell
 git clone https://github.com/forcedotcom/ApexUnit.git 
``` 
This would create a local copy of the project for you.
- (Optional) Open the project in an IDE (Eclipse, IntelliJ, etc.) 
-  There are two ways you can select test classes to execute and select classes you wish to examine the code coverage of
  - regex - identify and provide regex for the test class names that you want to execute in the "-regex.for.selecting.test.classes.to.execute" parameter. Example: if you want to execute the tests: My_Apex_controller_Test, My_Apex_builder_Test and My_Apex_validator_Test, identify the regex as "My_Apex_\*_Test". Pass the parameter "-regex.for.selecting.test.classes.to.execute My_Apex_\*_Test" in the mvn command.
    - Similarly, you can provide regex for the classes for which you want to examine the code coverage of by using "-regex.for.selecting.source.classes.for.code.coverage.computation My_Apex_\*_Class" in the mvn command.
  - Manifest files - Lists of tests can be read from Manifest files. Create a manifest file such as ManifestFile_Unit_Tests.txt in the "src/main/resources" directory of your project. Add test class names to execute in the manifest file. Specify this manifest file in the mvn command like "-manifest.files.with.test.class.names.to.execute ManifestFile_Unit_Tests.txt". 
    - Similarly, add the class names for which you want to exercise code coverage in a manifest file such as ClasssManifestFile.txt in "src/main/resources" directory of your project and specify this manifest file in the mvn command like "-manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt". 
  - Note that multiple regexes and manifest files can be specified using comma seperation(without spaces). Example: "-regex.for.selecting.test.classes.to.execute This_Is_Regex1\*,\*Few_Test,Another_\*_regex -manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt,MoreClassesManifestFile.txt"
- Go to your project directory (the directory containing pom.xml) in your commandline and execute the following command:
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
- -org.login.url : Login URL for the org (for example, https://na14.salesforce.com)
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

Note: You must provide either of the (-regex.for.selecting.source.classes.for.code.coverage.computation OR -manifest.files.with.source.class.names.for.code.coverage.computation) AND either of  -(regex.for.selecting.test.classes.to.execute OR -manifest.files.with.test.class.names.to.execute)

Sample command: 
```java
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner" -Dexec.args=" -org.login.url https://na14.salesforce.com -org.username yourusername@salesforce.com -org.password yourpassword-org.wide.code.coverage.threshold 75  -team.code.coverage.threshold 80 -org.client.id CLIENT_ID_FROM_CONNECTED_APP -org.client.secret CLIENT_SECRET_FROM_CONNECTED_APP -regex.for.selecting.test.classes.to.execute your_regular_exp1_for_test_classes,your_regular_exp2_for_test_classes -regex.for.selecting.source.classes.for.code.coverage.computation your_regular_exp1_for_source_classes,your_regular_exp2_for_source_classes -manifest.files.with.test.class.names.to.execute ManifestFile.txt -manifest.files.with.source.class.names.for.code.coverage.computation ClassManifestFile.txt -max.test.execution.time.threshold 10 -proxy.host your.proxy-if-required.net -proxy.port 8080"
```
Note: Multiple comma separated manifest files and regexes can be provided. Please do not include spaces while providing multiple regex or manifest files.

On successful completion - the command should indicate that your build succeeded and should generate two report files - **ApexUnitReport.xml** (This is the test report in JUnit format) and **Report/ApexUnitReport.html** (This is the code coverage report in html format)

# Using Manifest files and Regexes

You can populate class names in the Manifest file and/or provide regular expressions(regexes) 
Please refer https://github.com/forcedotcom/ApexUnit/wiki/Manifest-file-vs-regex for the usecases where manifest file(s) and regex(es) option can be used

#Addional options

Use src/main/resources/config.properties to set the below parameters.

1. API_VERSION(Default value: 37.0) : The Partner API version in use for the org. 

2. MAX_TIME_OUT_IN_MS(Default value : 1200000 ==> 20 minutes) : Time out setting for the session, Once timeout occurs, session renewer module is invoked which renews the session. Helpful when you face a connection exception during query executions. 

## Integrating with CI pipeline
CI engines like Jenkins(https://jenkins-ci.org/) can be used to seamlessly integrate ApexUnit with CI pipelines.
Please find the details here: https://github.com/forcedotcom/ApexUnit/wiki/Integrating-with-CI-pipeline

## How to contribute or track Bug/Issue for ApexUnit?
https://github.com/forcedotcom/ApexUnit/wiki/Contribution-and-Bug-Issue-tracking

## Questions/Feedback?
https://github.com/forcedotcom/ApexUnit/wiki/Contact-info