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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.async.BulkConnection;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.cd.apexUnit.report.ApexReportBean;
import com.sforce.soap.partner.PartnerConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.testng.collections.Lists;

public class TestExecutor {

    private static Logger LOG = LoggerFactory.getLogger(TestExecutor.class);

    public ApexReportBean[] testExecutionFlow() {
        ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
        PartnerConnection conn = connectionHandler.getConnection();
        final int BATCH_SIZE = 200;
        String parentJobId;
        List<ApexReportBean> reportBean = new ArrayList();

        if (conn == null) {
            ApexUnitUtils.shutDownWithErrMsg("Unable to establish Connection with the org. Suspending the run..");
        }
        String[] testClassesAsArray = ApexClassFetcherUtils.constructTestClassesArray(conn);
        if (LOG.isDebugEnabled()) {
            ApexClassFetcherUtils.logTheFetchedApexClasses(testClassesAsArray);
        }
        if (testClassesAsArray != null && testClassesAsArray.length > 0) {
            LOG.info("Total number of classes: " + testClassesAsArray.length);
            
            if (testClassesAsArray.length > 200) {
                int numOfBatches = 1;
                int fromIndex = 0;
                int toIndex = BATCH_SIZE;

                if (testClassesAsArray.length % BATCH_SIZE == 0) {
                    numOfBatches = testClassesAsArray.length / BATCH_SIZE;
                } else {
                    numOfBatches = testClassesAsArray.length / BATCH_SIZE + 1;
                }

                for (int count = 0; count < numOfBatches; count++) {
                    String[] testClassesInBatch = Arrays.copyOfRange(testClassesAsArray, fromIndex, toIndex);
                    BulkConnection bulkConnection = connectionHandler.getBulkConnection();
                    AsyncBulkApiHandler bulkApiHandler = new AsyncBulkApiHandler();
                    parentJobId = bulkApiHandler.handleBulkApiFlow(conn, bulkConnection, testClassesInBatch);
                    LOG.info("Running Batch of " + testClassesInBatch.length + " classes");
                    if (parentJobId != null) {
                        LOG.info("Parent job ID for the submission of the test classes to the Force.com platform is: "
                                + parentJobId);
                        TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
                        LOG.info("############################# Now executing - Apex tests.. #############################");           
                        reportBean.addAll(Arrays.asList(queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn)));
                    }

                    if (toIndex == testClassesAsArray.length) {
                        break;
                    } else {
                        fromIndex = fromIndex + BATCH_SIZE;
                        if ((toIndex + BATCH_SIZE) < (testClassesAsArray.length)) {
                            toIndex = toIndex + BATCH_SIZE;
                        } else {
                            toIndex = testClassesAsArray.length;
                        }
                    }
                }
                return reportBean.toArray(new ApexReportBean[reportBean.size()]);
            } else {
                BulkConnection bulkConnection = connectionHandler.getBulkConnection();
                AsyncBulkApiHandler bulkApiHandler = new AsyncBulkApiHandler();
                parentJobId = bulkApiHandler.handleBulkApiFlow(conn, bulkConnection, testClassesAsArray);

                if (parentJobId != null) {
                    LOG.info("Parent job ID for the submission of the test classes to the Force.com platform is: "
                            + parentJobId);
                    TestStatusPollerAndResultHandler queryPollerAndResultHandler = new TestStatusPollerAndResultHandler();
                    LOG.info("############################# Now executing - Apex tests.. #############################");
                    return queryPollerAndResultHandler.fetchResultsFromParentJobId(parentJobId, conn);
                }
            }

        }
        return null;
    }

}
