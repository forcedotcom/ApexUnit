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
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.report;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
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

public class ApexReportGeneratorTest {

	private static final Logger LOG = LoggerFactory.getLogger(ApexReportGeneratorTest.class);
	PartnerConnection conn = null;
	String parentJobId = null;

	@BeforeTest
	public void setup() {
		new CommandLineArgumentsTest().setup();
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
						parentJobId = sobject.getField("AsyncApexJobId").toString();
					}
				}
			}
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, "ConnectionException : ");
		}
	}

	@Test
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
