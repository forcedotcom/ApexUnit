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
 * Bulk API handler class to deal with the bulk API's used in the bulk batch queries during the test execution
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.testEngine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.BulkConnection;
import com.sforce.async.CSVReader;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.OperationEnum;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.client.QueryConstructor;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/*
 * This class handles the Bulk Query operations
 */
public class AsyncBulkApiHandler {

	private static Logger LOG = LoggerFactory.getLogger(AsyncBulkApiHandler.class);

	/*
	 * This method handles the bulk API flow using BulkConnection: 1. Creates
	 * JobInfo for the ApexTestQueueItem object 2. Creates a list of BatchInfo
	 * using the csv file(bulk file) 3. Closes the JobInfo object 4. Waits for
	 * completion of the batch jobs 5. Fetches the results from the Batch jobs
	 * 6. Returns parentJobId for the batch job results
	 */
	public String handleBulkApiFlow(PartnerConnection conn, BulkConnection bulkConnection,
			String[] testClassesAsArray) {
		String parentJobId = "";
		JobInfo job;
		try {

			// TODO : evaluate using builder pattern?
			// Creates JobInfo for the ApexTestQueueItem object
			job = createJob("ApexTestQueueItem", bulkConnection, OperationEnum.insert);
			// Creates a list of BatchInfo using the test Classes fetched

			List<BatchInfo> batchInfoList = createBatchesForApexClasses(bulkConnection, job, testClassesAsArray);

			// Closes the JobInfo object
			closeJob(bulkConnection, job.getId());
			// Waits for completion of the batch jobs
			awaitCompletion(bulkConnection, job, batchInfoList);
			// Fetches the results from the Batch jobs
			List<SaveResult> batchResults = checkResults(bulkConnection, job, batchInfoList);
			// Returns parentJobId for the batch job results
			if (batchResults != null) {
				parentJobId = getParentJobIdForTestQueueItems(batchResults, conn);
			} else {
				ApexUnitUtils.shutDownWithErrMsg(
						"Problem encountered while trying to fetch results of the batch job upon completion. "
								+ "Null batchResult was returned");
			}
		} catch (AsyncApiException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "Caught AsyncApiException exception while trying to deal with bulk connection: "
							+ e.getMessage());
		} catch (IOException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "Caught IO exception while trying to deal with bulk connection: "
							+ e.getMessage());
		} finally {
			bulkConnection = null;
		}
		if (parentJobId == null) {
			ApexUnitUtils.shutDownWithErrMsg("Parent Job Id returned is null. "
					+ "This typically means the test classes were not submitted correctly to the (Force.com )test execution engine");
		}
		LOG.info(
				"############################# List of Apex test classes successfully submitted to the Force.com test execution engine #############################");
		return parentJobId;
	}

	public List<BatchInfo> createBatchesForApexClasses(BulkConnection bulkConnection, JobInfo jobInfo,
			String[] testClassesAsArray) {

		List<BatchInfo> batchInfos = new ArrayList<BatchInfo>();
		String stringToFeedIntoTheBatch = "ApexClassId\n";
		for (String testClass : testClassesAsArray) {
			stringToFeedIntoTheBatch += testClass;
			stringToFeedIntoTheBatch += "\n";
		}
		InputStream inputStream = new ByteArrayInputStream(stringToFeedIntoTheBatch.getBytes());

		batchInfos = createBatch(inputStream, batchInfos, jobInfo, bulkConnection);

		return batchInfos;
	}

	/**
	 * Create a batchInfo by using the inputStream consisting of classIds.
	 * 
	 * @param inputStream
	 *            The input stream used to create a batchInfo.
	 * @param batchInfos
	 *            The batch info for the newly created batch is added to this
	 *            list.
	 * @param bulkConnection
	 *            The BulkConnection used to create the new batch.
	 * @param jobInfo
	 *            The JobInfo associated with the new batch.
	 * @throws IOException
	 */
	private List<BatchInfo> createBatch(InputStream inputStream, List<BatchInfo> batchInfos, JobInfo jobInfo,
			BulkConnection bulkConnection) {

		try {
			LOG.info("Creating batch for the test classes to execute using bulk connection....");
			BatchInfo batchInfo = bulkConnection.createBatchFromStream(jobInfo, inputStream);
			batchInfos.add(batchInfo);

		} catch (AsyncApiException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "Encountered AsyncApiException Exception while trying to create batchInfo"
							+ " using bulk connection. " + e.getMessage());
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				ApexUnitUtils
						.shutDownWithDebugLog(e, "Encountered IO Exception while trying to close the input stream after "
								+ "creating batchInfo using bulk connection. "
								+ e.getMessage());
			}
		}
		return batchInfos;
	}

	/**
	 * Create a new job using the Bulk API.
	 * 
	 * @param sobjectType
	 *            The object type being loaded, such as "Account"
	 * @param connection
	 *            BulkConnection used to create the new job.
	 * @param operation
	 *            operation to be performed - insert/update/query/upsert
	 * @return The JobInfo for the new job.
	 * @throws AsyncApiException
	 */
	public JobInfo createJob(String sobjectType, BulkConnection bulkConnection, OperationEnum operation)
			throws AsyncApiException {
		JobInfo job = new JobInfo();
		job.setObject(sobjectType);
		job.setOperation(operation);
		job.setContentType(ContentType.CSV);
		job = bulkConnection.createJob(job);

		return job;
	}

	/*
	 * Moves the JobInfo's status to 'closed' status
	 */
	public void closeJob(BulkConnection bulkConnection, String jobId) throws AsyncApiException {
		JobInfo job = new JobInfo();
		job.setId(jobId);
		job.setState(JobStateEnum.Closed);
		bulkConnection.updateJob(job);
	}

	/**
	 * Wait for a job to complete by polling the Bulk API.
	 * 
	 * @param connection
	 *            BulkConnection used to check results.
	 * @param job
	 *            The job awaiting completion.
	 * @param batchInfoList
	 *            List of batches for this job.
	 * @throws AsyncApiException
	 */
	public void awaitCompletion(BulkConnection connection, JobInfo job, List<BatchInfo> batchInfoList)
			throws AsyncApiException {
		long sleepTime = 0L;
		Set<String> incompleteBatchInfos = new HashSet<String>();
		for (BatchInfo bi : batchInfoList) {
			incompleteBatchInfos.add(bi.getId());
		}
		while (!incompleteBatchInfos.isEmpty()) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				ApexUnitUtils
						.shutDownWithDebugLog(e, "InterruptedException encountered while the thread was attempting to sleep");
			}
			LOG.debug("Awaiting results... Batches remaining for processing: " + incompleteBatchInfos.size());
			sleepTime = 10000L;
			BatchInfo[] statusList = connection.getBatchInfoList(job.getId()).getBatchInfo();
			for (BatchInfo batchInfo : statusList) {
				// Retain the BatchInfo's which are in InProgress and Queued
				// status,
				// Remove the rest from the incompleteBatchInfos
				if (batchInfo.getState() == BatchStateEnum.Completed) {
					if (incompleteBatchInfos.remove(batchInfo.getId())) {
						LOG.debug("BATCH STATUS:" + batchInfo.getStateMessage());
					}
				} else if (batchInfo.getState() == BatchStateEnum.NotProcessed) {
					LOG.info("Batch " + batchInfo.getId() + " did not process, terminating it");
					incompleteBatchInfos.remove(batchInfo.getId());
				} else if (batchInfo.getState() == BatchStateEnum.Failed) {
					ApexUnitUtils.shutDownWithErrMsg("BATCH STATUS:" + batchInfo.getStateMessage());
				}
			}
		}
	}

	/**
	 * Gets the results of the operation and checks for errors.
	 */
	public List<SaveResult> checkResults(BulkConnection bulkConnection, JobInfo job, List<BatchInfo> batchInfoList)
			throws AsyncApiException, IOException {
		LOG.debug("Checking Results.... ");
		List<SaveResult> saveResults = new ArrayList<SaveResult>();

		// batchInfoList was populated when batches were created and submitted
		for (BatchInfo batchInfo : batchInfoList) {
			CSVReader csvReaderForBatchResultStream = new CSVReader(
					bulkConnection.getBatchResultStream(job.getId(), batchInfo.getId()));
			List<String> resultHeader = csvReaderForBatchResultStream.nextRecord();
			int resultCols = resultHeader.size();

			List<String> batchResultStream = null;
			while ((batchResultStream = csvReaderForBatchResultStream.nextRecord()) != null) {

				Map<String, String> resultInfo = new HashMap<String, String>();
				for (int i = 0; i < resultCols; i++) {
					resultInfo.put(resultHeader.get(i), batchResultStream.get(i));
				}
				SaveResult sr = new SaveResult();
				sr.setId(resultInfo.get("Id"));
				boolean success = Boolean.valueOf(resultInfo.get("Success"));
				sr.setSuccess(success);

				if (!success) {
					if (resultInfo.get("Error") != null && StringUtils.isNotEmpty(resultInfo.get("Error"))) {
						ApexUnitUtils.shutDownWithErrMsg(
								"Error while fetching results for the batch job" + resultInfo.get("Error"));
					}
				}

				saveResults.add(sr);
			}
		}

		return saveResults;
	}

	/*
	 * Fetches the parentJobId for the bulk results
	 */
	public String getParentJobIdForTestQueueItems(List<SaveResult> bulkResults, PartnerConnection conn){
		String parentJobId = null;

		if (bulkResults != null && bulkResults.size() > 0) {
			SaveResult sr = bulkResults.get(0);
			String testQueueItemId = sr.getId();
			String soql = QueryConstructor.fetchParentJobIdForApexTestQueueItem(testQueueItemId);
			LOG.debug("Query used for fetching parent job ID for bulk results: " + soql);
			QueryResult queryResult = null;
			try {
				queryResult = conn.query(soql);
			} catch (ConnectionException connEx) {
				ApexUnitUtils.shutDownWithDebugLog(connEx, ConnectionHandler.logConnectionException(connEx, conn, soql));
			}
			if (queryResult != null && queryResult.isDone()) {
				// TODO: We need to verify what's the limit of records that the
				// bulk api can insert in one transaction. multiple transactions
				// mean multiple parent job ids
				parentJobId = fetchParentJobId(queryResult);
				LOG.info("Async test parent job Id : " + parentJobId);
			}
		} else {
			ApexUnitUtils.shutDownWithErrMsg("Invalid bulk results. No bulk results returned.");
		}

		return parentJobId;
	}

	private String fetchParentJobId(QueryResult queryResult) {
		String parentJobId = "";
		if (queryResult.getDone()) {
			SObject[] sObjects = queryResult.getRecords();
			if (sObjects != null) {
				for (SObject sobject : sObjects) {
					parentJobId = sobject.getField("ParentJobId").toString();
				}
				return parentJobId;
			}
		}
		return null;
	}
}
