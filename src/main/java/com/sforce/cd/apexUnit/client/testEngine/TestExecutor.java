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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.async.BulkConnection;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.QueryConstructor;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.cd.apexUnit.report.ApexReportBean;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class TestExecutor {
	private static Logger LOG = LoggerFactory.getLogger(TestExecutor.class);
	private static final int BATCH_SIZE = 200;

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
		String soql = QueryConstructor.getQueryForApexClassInfo(processClassArrayForQuery(testClassesAsArray));
		QueryResult queryresult = null;
		try {
			queryresult = conn.query(soql);
		} catch (ConnectionException e) {
			
			LOG.debug(e.getMessage());
		}
		SObject []s= queryresult.getRecords();
		SObject[] updateResult = new SObject[s.length];
		int i =0;
		for (SObject sObject : s) {
			SObject obj = new SObject();
			obj.setType("ApexTestQueueItem");
			obj.setId(sObject.getId());
			obj.setField("status", "Aborted");
			updateResult[i++] = obj;
		}
		LOG.info("No of test classes running tests "+queryresult.getSize());
		boolean submitTest = true;
		if(queryresult.getSize() != 0){
			LOG.info("Test Reload "+ CommandLineArguments.isTestReload());
			if(CommandLineArguments.isTestReload()){
				
				try {
					conn.update(updateResult);
				} catch (ConnectionException e) {
					LOG.debug(e.getMessage());
				}
			}
			else{
				submitTest = false;
			}
			
			
		}

		if(!submitTest){
			ApexUnitUtils.shutDownWithErrMsg("Test for these classes already running/enqueue at server...");
		}
		else{

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
		}
		return apexReportBean.toArray(new ApexReportBean[0]);
		
	}
	
	private String processClassArrayForQuery(String[] classesAsArray) {
		String queryString = "";
		for (int i = 0; i < classesAsArray.length; i++) {
			queryString += "'" + classesAsArray[i] + "'";
			queryString += ",";
		}
		if (queryString.length() > 1) {
			queryString = queryString.substring(0, queryString.length() - 1);
		}
		return queryString;
	}

}
