/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class for controlling the test execution flow in APexUnit
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.testEngine;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.async.BulkConnection;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.cd.apexUnit.report.ApexReportBean;
import com.sforce.soap.partner.PartnerConnection;

public class TestExecutor {
	private static Logger LOG = LoggerFactory.getLogger(TestExecutor.class);
	private static final int BATCH_SIZE = 200;

	/*public ApexReportBean[] testExecutionFlow() {
		ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
		PartnerConnection conn = connectionHandler.getConnection();
		if (conn == null) {
			ApexUnitUtils.shutDownWithErrMsg("Unable to establish Connection with the org. Suspending the run..");
		}
		String[] testClassesAsArray = ApexClassFetcherUtils.constructTestClassesArray(conn);
		if (LOG.isDebugEnabled()) {
			ApexClassFetcherUtils.logTheFetchedApexClasses(testClassesAsArray);
		}
		if (testClassesAsArray != null && testClassesAsArray.length > 0) {
			BulkConnection bulkConnection = connectionHandler.getBulkConnection();
			AsyncBulkApiHandler bulkApiHandler = new AsyncBulkApiHandler();
			String parentJobId = bulkApiHandler.handleBulkApiFlow(conn, bulkConnection, testClassesAsArray);

			if (parentJobId != null) {
				LOG.info("Parent job ID for the submission of the test classes to the Force.com platform is: "
						+ parentJobId);
				TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
				LOG.info("############################# Now executing - Apex tests.. #############################");
				return queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn);
			}
		}
		return null;
	}*/
	
	public ApexReportBean[] testExecutionFlow() {

		ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
		PartnerConnection conn = connectionHandler.getConnection();
		BulkConnection bulkConnection = null;
		AsyncBulkApiHandler bulkApiHandler = null;
		String[] testClassesInBatch = null;
		String parentJobId;
		ArrayList<ApexReportBean> apexReportBean = null;
		ApexReportBean[] apexReportBeanArray = null;
		

		if (conn == null) {
			ApexUnitUtils.shutDownWithErrMsg("Unable to establish Connection with the org. Suspending the run..");
		}

		String[] testClassesAsArray = ApexClassFetcherUtils.constructTestClassesArray(conn);

		if (LOG.isDebugEnabled()) {
			ApexClassFetcherUtils.logTheFetchedApexClasses(testClassesAsArray);
		}

		if (testClassesAsArray != null && testClassesAsArray.length > 0) {

			int numOfBatches = 0;
			int fromIndex = 0;
			int toIndex = 0;
			apexReportBean = new ArrayList<ApexReportBean>();
			bulkConnection = connectionHandler.getBulkConnection();
			bulkApiHandler = new AsyncBulkApiHandler();

			int lastSetOfClasses = testClassesAsArray.length % BATCH_SIZE;
			if (lastSetOfClasses == 0) {
				numOfBatches = testClassesAsArray.length / BATCH_SIZE;
			} else {
				numOfBatches = testClassesAsArray.length / BATCH_SIZE + 1;
			}

			for (int count = 0; count < numOfBatches; count++) {

				fromIndex = count * BATCH_SIZE;
				toIndex = (lastSetOfClasses != 0 && count == numOfBatches - 1) ? (toIndex + lastSetOfClasses)
						: (fromIndex + BATCH_SIZE);

				testClassesInBatch = Arrays.copyOfRange(testClassesAsArray, fromIndex, toIndex);
				parentJobId = bulkApiHandler.handleBulkApiFlow(conn, bulkConnection, testClassesInBatch);
                
				LOG.info("#####Parent JOB ID  #####"+parentJobId);
				if (parentJobId != null) {
					LOG.info("Parent job ID for the submission of the test classes to the Force.com platform is: "
							+ parentJobId);
					TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
					LOG.info(
							"############################# Now executing - Apex tests.. #############################");
					apexReportBeanArray = queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn);
					if(apexReportBeanArray != null){
						apexReportBean.addAll(Arrays.asList(apexReportBeanArray));
					}

				}

			}
			
		}
		return apexReportBean.toArray(new ApexReportBean[0]);
	}

}
