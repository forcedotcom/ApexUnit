/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class to poll and fetch the results for the ApexUnit test executions
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.testEngine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.QueryConstructor;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.cd.apexUnit.report.ApexReportBean;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class TestStatusPollerAndResultHandler {

	public static boolean testFailures = false;
	public static int totalTestMethodsExecuted = 0;
	public static int totalTestClasses = 0;
	public static int totalTestClassesAborted = 0;
	public static List<String> failedTestMethods = new ArrayList<String>();

	private static Logger LOG = LoggerFactory.getLogger(TestStatusPollerAndResultHandler.class);

	public ApexReportBean[] fetchResultsFromParentJobId(String parentJobId, PartnerConnection conn) {
		waitForTestsToComplete(parentJobId, conn);
		LOG.info("All tests have now completed executing!!");
		// Each test method execution is represented by a single ApexTestResult
		// record.
		// For example, if an Apex test class contains six test methods,
		// six ApexTestResult records are created.
		// These records are in addition to the ApexTestQueueItem record that
		// represents the Apex class.
		String soql = QueryConstructor.fetchResultFromApexTestQueueItem(parentJobId);

		LOG.debug(soql);
		ApexReportBean[] apexReportBeans = null;
		QueryResult queryResult = null;
		try {
			 queryResult = conn.query(soql);
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, ConnectionHandler
					.logConnectionException(e, conn, soql));
		}
		if (queryResult.getDone()) {
			int index = 0;
			SObject[] sObjects = queryResult.getRecords();
			if (sObjects != null) {
				totalTestMethodsExecuted = sObjects.length;
				LOG.info("Total test methods executed: " + TestStatusPollerAndResultHandler.totalTestMethodsExecuted);
				apexReportBeans = new ApexReportBean[sObjects.length];
				for (SObject sobject : sObjects) {
					ApexReportBean apexReportBean = populateReportBean(conn, sobject);
					apexReportBeans[index++] = apexReportBean;
				}
			}
		}
		return apexReportBeans;
	}

	private ApexReportBean populateReportBean(PartnerConnection conn, SObject sobject) {

		String apexClassId = sobject.getField("ApexClassId").toString();
		ApexReportBean apexReportBean = null;
		if (apexClassId != null) {
			apexReportBean = new ApexReportBean();
			apexReportBean.setApexClassId(sobject.getField("ApexClassId").toString());
			if (ApexClassFetcherUtils.apexClassMap.get(apexReportBean.getApexClassId()) != null) {
				apexReportBean
						.setApexClassName(ApexClassFetcherUtils.apexClassMap.get(apexReportBean.getApexClassId()));
			} else {
				apexReportBean.setApexClassName(
						ApexClassFetcherUtils.fetchApexTestClassNameFromId(conn, apexReportBean.getApexClassId()));
			}
			if (sobject.getField("MethodName") != null) {
				apexReportBean.setMethodName(sobject.getField("MethodName").toString());
			}
			if (sobject.getField("Message") != null) {
				apexReportBean.setMessage(sobject.getField("Message").toString());
			}
			if (sobject.getField("Outcome") != null) {
				String outcome = sobject.getField("Outcome").toString();
				apexReportBean.setOutcome(outcome);
				if (outcome.equalsIgnoreCase("fail") || outcome.equalsIgnoreCase("compilefail")) {
					testFailures = true;
					failedTestMethods.add(apexReportBean.getApexClassName() + "." + apexReportBean.getMethodName());
				}
			}
			if (sobject.getField("RunTime") != null) {
				apexReportBean.setTimeElapsed(Long.parseLong(sobject.getField("RunTime").toString()));
			}
			if (sobject.getField("StackTrace") != null) {
				apexReportBean.setStackTrace(sobject.getField("StackTrace").toString());
			}
			// LOG.info("SystemModstamp,TestTimestamp"+sobject.getField("SystemModstamp")+
			// sobject.getField("TestTimestamp"));
			// LOG.info("ApexLog.DurationMilliseconds,ApexLog.Operation,ApexLog.Request,ApexLog.Status,ApexClass.Name"+
			// sobject.getField("ApexLog.DurationMilliseconds")+","+sobject.getField("ApexLog.DurationMilliseconds")
			// +","+sobject.getField("ApexLog.Request")+","+sobject.getField("ApexLog.Status")+","+sobject.getField("ApexClass.Name")+"...");
		}
		return apexReportBean;
	}

	public boolean waitForTestsToComplete(String parentJobId, PartnerConnection conn) {
		String soql = QueryConstructor.getTestExecutionStatus(parentJobId);
		// String soql =
		// QueryConstructor.getTestExecutionStatusAndTransactionTime(parentJobId);

		QueryResult queryResult;
		boolean testsCompleted = false;

		try {
			LOG.debug(soql);
			int index = 0;
			queryResult = conn.query(soql);

			if (queryResult.getDone()) {
				SObject[] sObjects = queryResult.getRecords();

				if (sObjects != null) {
					String status = "";
					int totalTests = sObjects.length;
					totalTestClasses = totalTests;
					int remainingTests = totalTests;
					LOG.info("Total test classes to execute: " + totalTestClasses);
					String testId = "";
					String testName = "";
					String id = "";
					StopWatch stopWatch = new StopWatch();
					long startTime = 0;
					long endTime = 0;
					for (SObject sobject : sObjects) {
						sobject.setType("ApexTestQueueItem");
						status = sobject.getField("Status").toString();
						testId = sobject.getField("ApexClassId").toString();
						id = sobject.getField("Id").toString();
						LOG.debug("ID for ApexTestQueueItem: " + id);
						testName = ApexClassFetcherUtils.apexClassMap.get(testId);
						LOG.info("Now executing the test class: " + testName + " (" + CommandLineArguments.getOrgUrl()
								+ "/" + testId + " ) " + "Status : " + status);
						stopWatch.reset();
						stopWatch.start();
						startTime = stopWatch.getTime();
						LOG.debug("Start time: " + startTime);

						while (status.equals("Processing") || status.equals("Queued") || status.equals("Preparing")
								|| !status.equals("Completed")) {

							// break out of the loop if the test failed
							if (status.equals("Failed")) {
								LOG.info("Test class failure for : " + testName + " ("
										+ CommandLineArguments.getOrgUrl() + "/" + testId + " ) ");
								break;
							} else if (status.equals("Aborted")) {
								LOG.info("Test : " + testName + " (" + CommandLineArguments.getOrgUrl() + "/" + testId
										+ " ) has been aborted.");
								totalTestClassesAborted++;
								break;
							}
							// Abort the long running tests based on user
							// input(default: 10 minutes)
							// stopWatch.getTime() will be in milliseconds,
							// hence divide by 1000 to convert to seconds
							// maxTestExecTimeThreshold will be in minutes,
							// hence multiply by 60 to convert to seconds

							if (CommandLineArguments.getMaxTestExecTimeThreshold() != null && stopWatch.getTime()
									/ 1000.0 > CommandLineArguments.getMaxTestExecTimeThreshold() * 60
									&& status.equals("Processing")) {
								LOG.info("Oops! This test is a long running test. "
										+ CommandLineArguments.getMaxTestExecTimeThreshold()
										+ " minutes elapsed; aborting the test: " + testName);

								// create new sobject for updating the record
								SObject newSObject = new SObject();
								newSObject.setType("ApexTestQueueItem");
								newSObject.setField("Id", id);
								// abort the test using DML, set status to
								// "Aborted"
								newSObject.setField("Status", "Aborted");
								totalTestClassesAborted++;
								// logging the status and id fields to compare
								// them for pre and post update call

								try {
									// TODO : up to 10 records can be updated at
									// a time by update() call.
									// add the logic to leverage this feature.
									// Currently only one record is being
									// updated(aborted)

									// Challenge: By the time we wait for 10
									// records that needs to be aborted, the
									// 'to-be-aborted' test might continue to
									// run and might get completed

									// update() call- analogous to UPDATE
									// Statement in SQL
									SaveResult[] saveResults = conn.update(new SObject[] { newSObject });

									LOG.debug("Stop time: " + stopWatch.getTime());
									stopWatch.stop();

									for (int i = 0; i < saveResults.length; i++) {
										if (saveResults[i].isSuccess()) {
											LOG.debug("The record " + saveResults[i].getId()
													+ " was updated successfully");
											LOG.info("Aborted test case: " + testName
													+ " since the test took more time than the threshold execution time of "
													+ CommandLineArguments.getMaxTestExecTimeThreshold() + " mins");
										} else {
											// There were errors during the
											// update call, so loop through and
											// print them out

											StringBuffer errorMsg = new StringBuffer();
											errorMsg.append("Record " + saveResults[i].getId() + " failed to save");
											for (int j = 0; j < saveResults[i].getErrors().length; j++) {
												com.sforce.soap.partner.Error err = saveResults[i].getErrors()[j];
												errorMsg.append("error code: " + err.getStatusCode().toString());
												errorMsg.append("error message: " + err.getMessage());
											}
											ApexUnitUtils.shutDownWithErrMsg(errorMsg.toString());
										}
									}
									LOG.debug("After update--" + newSObject.getField("Status").toString());
									break;
								} catch (ConnectionException e) {
									ApexUnitUtils
											.shutDownWithDebugLog(e, ConnectionHandler.logConnectionException(e, conn, soql));
								}

							}

							LOG.debug("Status of the test class: " + testName + " (" + CommandLineArguments.getOrgUrl()
									+ "/" + testId + " ) " + " is : " + status);

							while (stopWatch.getTime() % 1000 != 0) {
								// wait, till 1 second elapses
							}
							LOG.debug("Firing polling query at " + stopWatch.getTime());
							queryResult = conn.query(soql);
							sObjects = queryResult.getRecords();
							status = sObjects[index].getField("Status").toString();
						}
						endTime = stopWatch.getTime();
						// get and log extended status for the test
						if (sObjects[index] != null && sObjects[index].getField("ExtendedStatus") != null) {
							String extendedStatus = sObjects[index].getField("ExtendedStatus").toString();
							LOG.info("Test status for " + testName + ":" + extendedStatus);
						}
						LOG.info("Completed executing the test class: " + testName + ". Time taken by the test: "
								+ endTime / 1000 / 60 + " minutes," + (endTime / 1000) % 60 + " seconds");
						index++;
						remainingTests = totalTests - index;
						LOG.info("Total tests executed " + index + " , Remaining tests " + remainingTests);
						if (remainingTests == 0) {
							testsCompleted = true;
						}
					}
				}
			}
		} catch (ConnectionException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, ConnectionHandler.logConnectionException(e, conn, soql));
		}
		return testsCompleted;

	}

}
