/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.client;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.OperationEnum;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.testEngine.AsyncBulkApiHandler;
import com.sforce.cd.apexUnit.client.testEngine.TestStatusPollerAndResultHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.cd.apexUnit.report.ApexReportBean;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Ignore
public class AsyncBulkApiHandlerTest {
	private static Logger LOG = LoggerFactory.getLogger(AsyncBulkApiHandlerTest.class);
	BulkConnection bulkConnection = null;
	AsyncBulkApiHandler bulkApiHandler = new AsyncBulkApiHandler();
	JobInfo jobInfo = null;
	String jobId = null;
	List<BatchInfo> batchInfoList = null;
	List<SaveResult> batchResults = null;
	String parentJobId = null;
	String[] apexClasses = new String[200];

	@BeforeTest
	public void setup() {
	/*	new CommandLineArgumentsTest().setup();
		ConnectionHandler connHandler = ConnectionHandler.getConnectionHandlerInstance();
		bulkConnection = connHandler.getBulkConnection();
		PartnerConnection conn = connHandler.getConnection();
		// populate the classfile names
		// String[] apexClasses = new String[200];
		// {"01p300000000KcmAAE","01p300000000KcnAAE", "01pQ00000007axIIAQ",
		// "01pQ00000007axJIAQ", "01pQ00000007axKIAQ",
		// "01pQ00000007axLIAQ", "01pQ00000007axMIAQ","01pQ00000007axNIAQ"};
		apexClasses = ApexClassFetcherUtils.constructTestClassesArray(conn);*/

	}

	/*@Test(priority = 1)
	public void createJob() {
		try {
			ConnectorConfig connConfig = bulkConnection.getConfig();
			LOG.info("Rest  end point:" + connConfig.getRestEndpoint());
			jobInfo = bulkApiHandler.createJob("ApexTestQueueItem", bulkConnection, OperationEnum.insert);
			jobId = jobInfo.getId();
			// making sure JobInfo has been created
			Assert.assertTrue(jobId != null);
		} catch (AsyncApiException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "Caught exception while trying to create job: "
							+ e.getMessage());
		}
	}

	@Test(priority = 2)
	public void createBatchesForApexClasses() {
		batchInfoList = bulkApiHandler.createBatchesForApexClasses(bulkConnection, jobInfo, apexClasses);
		Assert.assertTrue(batchInfoList.size() > 0);
	}

	@Test(priority = 3)
	public void closeJob() {
		try {
			bulkApiHandler.closeJob(bulkConnection, jobId);
			// make sure closeJob closes Job with a given Job Id
			Assert.assertEquals(JobStateEnum.Closed, bulkConnection.getJobStatus(jobId).getState());
		} catch (AsyncApiException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, e.getMessage());
		}
	}

	@Test(priority = 4)
	public void checkResults() {
		try {
			bulkApiHandler.awaitCompletion(bulkConnection, jobInfo, batchInfoList);
			batchResults = bulkApiHandler.checkResults(bulkConnection, jobInfo, batchInfoList);
			Assert.assertTrue(batchResults.size() > 0);
		} catch (AsyncApiException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, "AsyncApiException encountered while fetching batch results using Bulk API. Exception thrown is: "
					+ e);
		} catch (IOException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, "IOException encountered. Unable to get Batch stream info from Bulk connection. Exception thrown is: "
					+ e);
		}
	}

	@Test(priority = 5)
	public void getParentJobId() {
		parentJobId = bulkApiHandler.getParentJobIdForTestQueueItems(batchResults,
				ConnectionHandler.getConnectionHandlerInstance().getConnection());
		LOG.info("ParentJobId in the test: " + parentJobId);
		Assert.assertNotNull(parentJobId);
	}

	@Test(priority = 6)
	public void fetchResultsFromParentJobId() {

		TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
		PartnerConnection conn = ConnectionHandler.getConnectionHandlerInstance().getConnection();
		ApexReportBean[] apexReportBeans = queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn);
		// check status of wait method to make sure
		// fetchResultsFromParentJobId() method is completed
		boolean isTestsExecutionCompleted = queryPollerAndResultHandler.waitForTestsToComplete(parentJobId, conn);
		Assert.assertEquals(true, isTestsExecutionCompleted);
		Assert.assertTrue(apexReportBeans.length > 0);
	}*/
}
