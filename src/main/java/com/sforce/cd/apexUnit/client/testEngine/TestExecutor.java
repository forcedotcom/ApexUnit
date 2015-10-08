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
 * Class for controlling the test execution flow in APexUnit
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.testEngine;

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

	public ApexReportBean[] testExecutionFlow() {
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
				ApexReportBean[] apexReportBeans = queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId,
						conn);

				return apexReportBeans;
			}
		}
		return null;
	}

}
