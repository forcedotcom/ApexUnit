/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * ApexUnitRunner serves as entry point to the ApexUnit 2.x tool
 * ApexUnit 2.x is test framework for Force.com platform and has two major functinalities:
 * Test execution: Test names are provided by the user. The tests are enqueued and submitted to the test execution engine.
 * The test execution results are fetched and results are published as JUnit xml report
 * Code coverage: Source class names are provided by the user. 
 * The tool uses Tooling APIs to compute code coverage for the source class(es)
 * A consolidated report is generated with covered/uncovered line numbers in the code.
 * User can also provide regex(es) to select the source and test class names
 * Thresholds for code coverage can be customized(default: 75%) by the user. 
 * The tool errors out if code coverage threshold metrics are not met
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.codeCoverage.CodeCoverageComputer;
import com.sforce.cd.apexUnit.client.testEngine.TestExecutor;
import com.sforce.cd.apexUnit.client.testEngine.TestStatusPollerAndResultHandler;
import com.sforce.cd.apexUnit.report.ApexClassCodeCoverageBean;
import com.sforce.cd.apexUnit.report.ApexCodeCoverageReportGenerator;
import com.sforce.cd.apexUnit.report.ApexReportBean;
import com.sforce.cd.apexUnit.report.ApexUnitTestReportGenerator;
import com.sforce.cd.apexUnit.report.ApexUnitCodeCoverageResults;

public class ApexUnitRunner {
	private static Logger LOG = LoggerFactory.getLogger(ApexUnitRunner.class);

	/*
	 * main method: entry point to the ApexUnit 2.x
	 * 
	 * @param: arguments as a String array
	 */
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		// Read input arguments using JCommander
		CommandLineArguments cmdLineArgs = new CommandLineArguments();
		JCommander jcommander = new JCommander(cmdLineArgs, args);
		boolean skipCodeCoverageComputation = CommandLineArguments.getTeamCodeCoverageThreshold() == 0
				&& CommandLineArguments.getOrgWideCodeCoverageThreshold() == 0;
		if (CommandLineArguments.isHelp()) {
			logHelp(jcommander);
		}
		if ((CommandLineArguments.getTestManifestFiles() == null && CommandLineArguments.getTestRegex() == null)) {
			ApexUnitUtils.shutDownWithErrMsg("Either of the test manifest file or test regex should be provided");
		}
		if (CommandLineArguments.getClassManifestFiles() == null && CommandLineArguments.getSourceRegex() == null
				&& !skipCodeCoverageComputation) {
			ApexUnitUtils.shutDownWithErrMsg("Either of the source class manifest file or source class regex should be provided");
		}
		// Invoke the FlowController.logicalFlow() that handles the entire
		// logical flow of ApexUnit tool.
		TestExecutor testExecutor = new TestExecutor();
		LOG.info("####################################   "
				 + "Processing the Apex test classes specified by the user"
				 + "  #################################### ");
		ApexReportBean[] apexReportBeans = testExecutor.testExecutionFlow();

		// constructing report file with test results and code coverage results
		String runTimeExceptionMessage = "";
		ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans = null;

		// skip code coverage computation if team code coverage and org wide
		// code coverage thresholds are set to 0
		if (!skipCodeCoverageComputation) {
			LOG.info("####################################    "
					+ "Computing code coverage for the team based on the Apex Class names(source class names) provided"
					+ "   #################################### ");

			CodeCoverageComputer toolingAPIInvoker = new CodeCoverageComputer();
			apexClassCodeCoverageBeans = toolingAPIInvoker.calculateAggregatedCodeCoverageUsingToolingAPI();
			// sort the codeCoverageBeans before generating report so that
			// records
			// are ordered in ascending order of code coverage percentage
			Arrays.sort(apexClassCodeCoverageBeans);
			// computes org wide code coverage
			toolingAPIInvoker.getOrgWideCodeCoverage();
			// generate the reports for publishing
		} else {
			LOG.info("####################################   Skipping code coverage computation. "
					 + "Please update the threshold parameters for team code coverage and "
					 + "org wide code coverage to activate the code coverage computation feature"
					 + "   #################################### ");
		}
		Long end = System.currentTimeMillis();
		LOG.debug("Total Time taken by ApexUnit tool in secs: " + (end - start) / 1000);
		if (apexReportBeans != null && apexReportBeans.length > 0) {
			LOG.info("Total test methods executed: " + apexReportBeans.length);
			String reportFile = "ApexUnitReport.xml";
			ApexUnitTestReportGenerator.generateTestReport(apexReportBeans, reportFile);
		} else {
			ApexUnitUtils.shutDownWithErrMsg("Unable to generate test report. "
											 + "Did not find any test results for the job id");
		}
		if (!skipCodeCoverageComputation) {
			ApexCodeCoverageReportGenerator.generateHTMLReport(apexClassCodeCoverageBeans);

			// validating the code coverage metrics against the thresholds
			// provided by the user
			boolean teamCodeCoverageThresholdError = false;
			boolean orgWideCodeCoverageThresholdError = false;
			if (ApexUnitCodeCoverageResults.teamCodeCoverage < CommandLineArguments.getTeamCodeCoverageThreshold()) {
				if (ApexUnitCodeCoverageResults.teamCodeCoverage == -1) {
					LOG.warn("No source class names provided. Team Code coverage not computed ");
				} else {
					teamCodeCoverageThresholdError = true;
				}
			}
			if (ApexUnitCodeCoverageResults.orgWideCodeCoverage < CommandLineArguments
					.getOrgWideCodeCoverageThreshold()) {
				orgWideCodeCoverageThresholdError = true;
			}

			if (teamCodeCoverageThresholdError) {
				runTimeExceptionMessage += "Failed to meet the Team code coverage threshold : "
						+ CommandLineArguments.getTeamCodeCoverageThreshold()
						+ " The team code coverage for the given classes is: "
						+ ApexUnitCodeCoverageResults.teamCodeCoverage + "%\n"
						+ "Calibrate your threshold values if you are happy with the current code coverage\n";
			}
			if (orgWideCodeCoverageThresholdError) {
				runTimeExceptionMessage += "Failed to meet the Org code coverage threshold : "
						+ CommandLineArguments.getOrgWideCodeCoverageThreshold()
						+ " The org code coverage for the org is: " + ApexUnitCodeCoverageResults.orgWideCodeCoverage
						+ "%" + "Calibrate your threshold values if you are happy with the current code coverage\n";
			}
		}

		// if there are test failures, concatenate error messages
		if (TestStatusPollerAndResultHandler.testFailures) {
			runTimeExceptionMessage += "Test failures amongst the Apex tests executed. ";
			if (TestStatusPollerAndResultHandler.failedTestMethods != null
					&& TestStatusPollerAndResultHandler.failedTestMethods.size() > 0) {
				int failedTestsCount = TestStatusPollerAndResultHandler.failedTestMethods.size();
				runTimeExceptionMessage += "Failed test methods count: " + failedTestsCount + " Failed test methods: "
						+ TestStatusPollerAndResultHandler.failedTestMethods.toString();
			}
		}
		// if there are any runtime exceptions, throw the error messages and
		// shut down ApexUnit
		if (!runTimeExceptionMessage.equals("")) {
			ApexUnitUtils.shutDownWithErrMsg(runTimeExceptionMessage);
		} else {
			LOG.info("Success!! No test failures and all code coverage thresholds are met!! Exiting ApexUnit.. Good bye..");
		}

	}

	/*
	 * Log command line options for -help option
	 * @param jcommander - JCommander instance 
	 * @return nothing. Display the description for each CLI parameter
	 */
	private static void logHelp(JCommander jcommander) {
		List<ParameterDescription> parameters = jcommander.getParameters();
		for (ParameterDescription parameter : parameters) {
			LOG.info(parameter.getLongestName() + " : " + parameter.getDescription());
		}
		System.exit(0);
	}
}
