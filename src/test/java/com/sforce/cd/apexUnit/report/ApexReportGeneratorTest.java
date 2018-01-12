/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.report;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.codeCoverage.CodeCoverageComputer;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.testEngine.TestStatusPollerAndResultHandler;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Ignore
public class ApexReportGeneratorTest {

	private static final Logger LOG = LoggerFactory.getLogger(ApexReportGeneratorTest.class);
	PartnerConnection conn = null;
	String parentJobId = null;

	@BeforeTest
	public void setup() {
		/*new CommandLineArgumentsTest().setup();
		cleanUpReports();
		ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
		conn = connectionHandler.getConnection();
		// pick the most recent parentJobId
		String soql = "SELECT AsyncApexJobId,SystemModstamp,TestTimestamp FROM ApexTestResult ORDER BY SystemModstamp DESC LIMIT 1";
		QueryResult queryResult;
		try {
			queryResult = conn.query(soql);

			if (queryResult.getDone()) {
				SObject[] sObjects = queryResult.getRecords();
				if (sObjects != null) {
					for (SObject sobject : sObjects) {
						Object parentJob = sobject.getField("AsyncApexJobId");
						if(parentJob !=null){
						  parentJobId = sobject.getField("AsyncApexJobId").toString();
						}
					}
				}
			}
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, "ConnectionException : ");
		}*/
	}

	/*@Test
	public void generateTestReportTest() {

		TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
		Long justBeforeReportGeneration = System.currentTimeMillis();
		ApexReportBean[] apexReportBeans = queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn);
		CodeCoverageComputer toolingAPIInvoker = new CodeCoverageComputer();
		ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans = toolingAPIInvoker
				.calculateAggregatedCodeCoverageUsingToolingAPI();
		String reportFileName = "ApexUnitReport.xml";

		ApexUnitTestReportGenerator.generateTestReport(apexReportBeans, reportFileName);

		File reportFile = new File(reportFileName);
		LOG.info("justBeforeReportGeneration: " + justBeforeReportGeneration);
		LOG.info("reportFile last modified..: " + reportFile.lastModified());
		LOG.info("Result:" + FileUtils.isFileNewer(reportFile, justBeforeReportGeneration));
		Assert.assertTrue(FileUtils.isFileNewer(reportFile, justBeforeReportGeneration));
	}

	@Test
	public void generateHTMLReportTest() {
		TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
		ApexReportBean[] apexReportBeans = queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn);
		CodeCoverageComputer codeCoverageComputer = new CodeCoverageComputer();
		Long justBeforeReportGeneration = System.currentTimeMillis();
		ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans = codeCoverageComputer
				.calculateAggregatedCodeCoverageUsingToolingAPI();

		if (apexClassCodeCoverageBeans != null) {
			ApexCodeCoverageReportGenerator.generateHTMLReport(apexClassCodeCoverageBeans);
		}
		String reportFilePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "Report"
				+ System.getProperty("file.separator") + "ApexUnitReport.html";
		File reportFile = new File(reportFilePath);

		Assert.assertTrue(FileUtils.isFileNewer(reportFile, justBeforeReportGeneration));

	}
*/
	public void cleanUpReports() {

		String testReportPath = System.getProperty("user.dir") + System.getProperty("file.separator")
				+ "ApexUnitReport.xml";
		File testReport = new File(testReportPath);
		if (testReport.exists() && testReport.delete()) {
			LOG.info("Test report deleted");
		} else {
			LOG.info("Test report not deleted");
		}
		String reportDirPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "Report";
		File reportDir = new File(reportDirPath);
		if (reportDir.exists()) {
			try {
				FileUtils.deleteDirectory(reportDir);
			} catch (IOException e) {
				ApexUnitUtils.shutDownWithDebugLog(e, "IO Exception encountered while deleting the reports");
			}
			LOG.info("Report directory deleted");
		} else {
			LOG.info("Report directory does not exist; hence not deleted");
		}

	}
}
