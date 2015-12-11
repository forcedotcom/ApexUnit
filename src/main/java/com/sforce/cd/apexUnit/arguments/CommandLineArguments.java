/* 
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * CommandLineArguments class used JCommander tool for accepting, validating and assigning to the 
 * command line arguments for the ApexUnit tool
 * The class exposes getter methods for global access of the command line arguments in the tool
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 
package com.sforce.cd.apexUnit.arguments;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
	/*
	 * Static variables that define the command line options
	 */
	public static final String ORG_LOGIN_URL = "-org.login.url";
	public static final String ORG_USERNAME = "-org.username";
	public static final String ORG_PASSWORD = "-org.password";
	public static final String MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE = "-manifest.files.with.test.class.names.to.execute";
	public static final String MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION = "-manifest.files.with.source.class.names.for.code.coverage.computation";
	public static final String REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE = "-regex.for.selecting.test.classes.to.execute";
	public static final String REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION = "-regex.for.selecting.source.classes.for.code.coverage.computation";
	public static final String ORG_WIDE_CODE_COVERAGE_THRESHOLD = "-org.wide.code.coverage.threshold";
	public static final String TEAM_CODE_COVERAGE_THRESHOLD = "-team.code.coverage.threshold";
	public static final String MAX_TEST_EXECUTION_TIME_THRESHOLD = "-max.test.execution.time.threshold";
	public static final String ORG_CLIENT_ID = "-org.client.id";
	public static final String ORG_CLIENT_SECRET = "-org.client.secret";
	public static final String TEST_NAMESPACE_PREFIX = "-test.namespace.prefix";
	public static final String HELP = "-help";

	public static final String DEFAULT_NAMESPACE_TOKEN = "_DEFAULT_";

	/*
	 * Define Parameters using JCommander framework
	 */
	@Parameter(names = ORG_LOGIN_URL, description = "Login URL for the org", required = true, validateWith = URLValidator.class)
	static private String orgUrl = System.getProperty("SERVER_URL_PARAMETER");
	@Parameter(names = ORG_USERNAME, description = "Username for the org", required = true)
	static private String username = System.getProperty("SERVER_USERNAME_PARAMETER");
	@Parameter(names = ORG_PASSWORD, description = "Password corresponding to the username for the org", required = true)
	static private String password = System.getProperty("SERVER_PASSWORD_PARAMETER");
	@Parameter(names = MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE, description = "Manifest files containing the list of test classes to be executed", variableArity = true)
	static private String testManifestFiles = null;
	@Parameter(names = MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION, description = "Manifest files containing the list of Apex classes for which code coverage"
			+ " is to be computed", variableArity = true)
	static private String classManifestFiles = null;
	@Parameter(names = REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE, description = "The test regex used by the team for the apex test classes. "
			+ "All tests beginning with this parameter in the org will be selected to run", variableArity = true)
	static private String testRegex;
	@Parameter(names = REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION, description = "The source regex used by the team for the apex source classes. "
			+ "All classes beginning with this parameter in the org will be used to compute team code coverage", variableArity = true)
	static private String sourceRegex;
	@Parameter(names = ORG_WIDE_CODE_COVERAGE_THRESHOLD, description = "Org wide minimum code coverage required to meet the code coverage standards", validateWith = PercentageInputValidator.class, variableArity = true)
	static private Integer orgWideCodeCoverageThreshold = 75;
	@Parameter(names = TEAM_CODE_COVERAGE_THRESHOLD, description = "Team wide minimum code coverage required to meet the code coverage standards", validateWith = PercentageInputValidator.class, variableArity = true)
	static private Integer teamCodeCoverageThreshold = 75;
	@Parameter(names = MAX_TEST_EXECUTION_TIME_THRESHOLD, description = "Maximum execution time(in minutes) for a test before it gets aborted", validateWith = PositiveIntegerValidator.class, variableArity = true)
	static private Integer maxTestExecTimeThreshold;
	@Parameter(names = ORG_CLIENT_ID, description = "Client ID associated with the org. "
			+ "Steps to confirgure/retrieve client ID/Secret: "
			+ "https://www.salesforce.com/us/developer/docs/api_rest/Content/quickstart_oauth.htm", required = true)
	static private String clientId;
	@Parameter(names = ORG_CLIENT_SECRET, description = "Client Secret associated with the org.", required = true)
	static private String clientSecret;
	@Parameter(names = TEST_NAMESPACE_PREFIX, description = "Namespace prefix of the test classes that will be executed. Defaults to '' (blank) if not specified. " + 
			"To explicitly specify the default namespace, use the token \"_DEFAULT_\" for this parameter value.")
	static private String testNamespacePrefix = "";
	@Parameter(names = HELP, help = true, description = "Displays options available for running this application")
	static private boolean help;

	/*
	 * Static getter methods for each of the CLI parameter
	 */
	public static String getOrgUrl() {
		return orgUrl;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getTestManifestFiles() {
		return testManifestFiles;
	}

	public static String getClassManifestFiles() {
		return classManifestFiles;
	}

	public static String getTestRegex() {
		return testRegex;
	}

	public static String getSourceRegex() {
		return sourceRegex;
	}

	public static Integer getOrgWideCodeCoverageThreshold() {
		return orgWideCodeCoverageThreshold;
	}

	public static Integer getTeamCodeCoverageThreshold() {
		return teamCodeCoverageThreshold;
	}

	public static Integer getMaxTestExecTimeThreshold() {
		return maxTestExecTimeThreshold;
	}

	public static String getClientId() {
		return clientId;
	}

	public static void setClientId(String clientId) {
		CommandLineArguments.clientId = clientId;
	}

	public static String getClientSecret() {
		return clientSecret;
	}

	public static void setClientSecret(String clientSecret) {
		CommandLineArguments.clientSecret = clientSecret;
	}

	public static String getTestNamespacePrefix() {
		// This allows you to explicitly specify the default namespace by passing "_DEFAULT_" as the value for the -test.namespace.prefix argument
		if (DEFAULT_NAMESPACE_TOKEN.equals(testNamespacePrefix)) {
			return "";
		} else {
			return testNamespacePrefix;
		}
	}
	
	public static void setTestNamespacePrefix(String testNamespacePrefix) {
		CommandLineArguments.testNamespacePrefix = testNamespacePrefix;
	}
	
	public static boolean isHelp() {
		return help;
	}
}
