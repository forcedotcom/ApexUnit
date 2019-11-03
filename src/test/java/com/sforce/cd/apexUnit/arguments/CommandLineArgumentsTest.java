/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.sforce.cd.apexUnit.arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.beust.jcommander.JCommander;

public class CommandLineArgumentsTest {
	private static Logger logs = LoggerFactory.getLogger(CommandLineArgumentsTest.class);
	CommandLineArguments cmdLineArgs = new CommandLineArguments();
	// initialize test parameter values.
	// Change the below values to execute various positive/negative test cases

	private final String SERVER_ORG_LOGIN_URL_PARAMETER = System.getProperty(CommandLineArguments.ORG_LOGIN_URL);
	private final String ORG_USERNAME_PARAMETER = System.getProperty(CommandLineArguments.ORG_USERNAME);
	private final String ORG_PASSWORD_PARAMETER = System.getProperty(CommandLineArguments.ORG_PASSWORD);
	private final String CLIENT_ID = System.getProperty(CommandLineArguments.ORG_CLIENT_ID);
	private final String CLIENT_SECRET = System.getProperty(CommandLineArguments.ORG_CLIENT_SECRET);
	private final String MANIFEST_FILE_PARAMETER = System
			.getProperty(CommandLineArguments.MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE);
	private final String CLASS_MANIFEST_FILE_PARAMETER = System
			.getProperty(CommandLineArguments.MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION);
	private final String TEST_PREFIX_PARAMETER = System
			.getProperty(CommandLineArguments.REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE);
	private final String ORG_WIDE_CC_THRESHOLD_PARAMETER = System
			.getProperty(CommandLineArguments.ORG_WIDE_CODE_COVERAGE_THRESHOLD);
	private final String TEAM_CC_THRESHOLD_PARAMETER = System
			.getProperty(CommandLineArguments.TEAM_CODE_COVERAGE_THRESHOLD);
	private final String CLASS_PREFIX_PARAMETER = System
			.getProperty(CommandLineArguments.REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION);
	private final String MAX_TEST_EXEC_TIME_THRESHOLD = System
			.getProperty(CommandLineArguments.MAX_TEST_EXECUTION_TIME_THRESHOLD);

	@BeforeTest
	public void setup() {
		StringBuffer arguments = new StringBuffer();

		arguments.append(CommandLineArguments.ORG_LOGIN_URL);
		arguments.append(appendSpaces(SERVER_ORG_LOGIN_URL_PARAMETER));
		arguments.append(CommandLineArguments.ORG_USERNAME);
		arguments.append(appendSpaces(ORG_USERNAME_PARAMETER));
		arguments.append(CommandLineArguments.ORG_PASSWORD);
		arguments.append(appendSpaces(ORG_PASSWORD_PARAMETER));
		arguments.append(CommandLineArguments.MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE);
		arguments.append(appendSpaces(MANIFEST_FILE_PARAMETER));
		arguments.append(CommandLineArguments.MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION);
		arguments.append(appendSpaces(CLASS_MANIFEST_FILE_PARAMETER));
		arguments.append(CommandLineArguments.REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE);
		arguments.append(appendSpaces(TEST_PREFIX_PARAMETER));
		arguments.append(CommandLineArguments.ORG_WIDE_CODE_COVERAGE_THRESHOLD);
		arguments.append(appendSpaces(ORG_WIDE_CC_THRESHOLD_PARAMETER));
		arguments.append(CommandLineArguments.TEAM_CODE_COVERAGE_THRESHOLD);
		arguments.append(appendSpaces(TEAM_CC_THRESHOLD_PARAMETER));
		arguments.append(CommandLineArguments.REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION);
		arguments.append(appendSpaces(CLASS_PREFIX_PARAMETER));
		arguments.append(CommandLineArguments.MAX_TEST_EXECUTION_TIME_THRESHOLD);
		arguments.append(appendSpaces(MAX_TEST_EXEC_TIME_THRESHOLD));
		arguments.append(CommandLineArguments.ORG_CLIENT_ID);
		arguments.append(appendSpaces(CLIENT_ID));
		arguments.append(CommandLineArguments.ORG_CLIENT_SECRET);
		arguments.append(appendSpaces(CLIENT_SECRET));
		String[] args = arguments.toString().split(" ");

		//JCommander jcommander = new JCommander(cmdLineArgs, args);
	}

/*	@Test
	public void getOrgWideCodeCoverageThreshold() {
		Assert.assertEquals(CommandLineArguments.getOrgWideCodeCoverageThreshold().intValue(),
				Integer.parseInt(ORG_WIDE_CC_THRESHOLD_PARAMETER));
	}

	@Test
	public void getManifestFileLoc() {
		Assert.assertEquals(CommandLineArguments.getTestManifestFiles(), MANIFEST_FILE_PARAMETER);
	}

	@Test
	public void getClassManifestFileLoc() {
		Assert.assertEquals(CommandLineArguments.getClassManifestFiles(), CLASS_MANIFEST_FILE_PARAMETER);
	}

	public void getPassword() {
		Assert.assertEquals(CommandLineArguments.getPassword(), ORG_PASSWORD_PARAMETER);

	}

	@Test
	public void getTeamCodeCoverageThreshold() {
		Assert.assertEquals(CommandLineArguments.getTeamCodeCoverageThreshold().intValue(),
				Integer.parseInt(TEAM_CC_THRESHOLD_PARAMETER));
	}

	@Test
	public void getTestPrefix() {
		Assert.assertEquals(CommandLineArguments.getTestRegex(), TEST_PREFIX_PARAMETER);
	}

	@Test
	public void getUrl() {
		Assert.assertEquals(CommandLineArguments.getOrgUrl(), SERVER_ORG_LOGIN_URL_PARAMETER);
	}

	@Test
	public void getUsername() {
		Assert.assertEquals(CommandLineArguments.getUsername(), ORG_USERNAME_PARAMETER);
	}

	@Test
	public void getClassPrefix() {
		Assert.assertEquals(CommandLineArguments.getSourceRegex(), CLASS_PREFIX_PARAMETER);
	}

	@Test
	public void getClientId() {
		Assert.assertEquals(CommandLineArguments.getClientId(), CLIENT_ID);
	}

	@Test
	public void getClientSecret() {
		Assert.assertEquals(CommandLineArguments.getClientSecret(), CLIENT_SECRET);
	}
*/
	private String appendSpaces(String input) {
		if (input != null && !input.isEmpty()) {
			return " " + input.toString() + " ";
		}
		return " ";
	}
	
	@Test
	public void tempTest1(){
		Assert.assertNotNull("temp");
	}

}
