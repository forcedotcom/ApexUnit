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
 * Class to generate test report for ApexUnit run in JUnit test report format
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.soap.partner.PartnerConnection;

public class ApexUnitTestReportGenerator {
	private static Logger LOG = LoggerFactory.getLogger(ApexUnitTestReportGenerator.class);

	/**
	 * Generates a JUnit/Jenkins compliant test report in XML for the given job.
	 * 
	 * @param apexClassCodeCoverageBeans
	 * @param Id
	 *            of the job whose test report is to be generated
	 * 
	 */
	public static void generateTestReport(ApexReportBean[] reportBeans,
			ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans, String reportFile) {
		if (reportBeans != null && reportBeans.length > 0) {
			// Create root element
			Document document = new Document();
			Element rootElement = new Element("testsuite");
			document.setRootElement(rootElement);

			int failureCount = 0;
			int testCount = 0;

			for (ApexReportBean reportBean : reportBeans) {
				testCount++;

				// Create testcase element retrieving values from the result
				Element testcase = new Element("testcase");
				String apexClassName = "";
				// set apex ClassName
				if (reportBean.getApexClassName() == null || reportBean.getApexClassName().equals("")) {

					if (reportBean.getApexClassId() != null
							&& ApexClassFetcherUtils.apexClassMap.get(reportBean.getApexClassId()) != null) {
						apexClassName = ApexClassFetcherUtils.apexClassMap.get(reportBean.getApexClassId());
					} else if (reportBean.getApexClassId() != null) {
						PartnerConnection conn = ConnectionHandler.getConnectionHandlerInstance().getConnection();
						HashMap<String, String> apexClassInfoMap = ApexClassFetcherUtils.fetchApexClassInfoFromId(conn,
								reportBean.getApexClassId());
						apexClassName = apexClassInfoMap.get("Name");
					} else {
						LOG.debug(
								"Report bean not constructed properly. 'null' class ID associated with the report bean");
					}
					// By now apexClassName will be populated
					reportBean.setApexClassName(apexClassName);
				} else {
					apexClassName = reportBean.getApexClassName();
				}
				testcase.setAttribute("classname", apexClassName);
				// set class test method
				testcase.setAttribute("name", reportBean.getMethodName());

				// Increment pass/fail counters
				if (reportBean.getOutcome().equalsIgnoreCase("pass")) {
					Element success = new Element("Success");
					success.setAttribute("message", "Passed");
					testcase.addContent(success);
				} else if (reportBean.getOutcome().equalsIgnoreCase("fail")
						| reportBean.getOutcome().equalsIgnoreCase("compilefail")) {
					failureCount++;
					Element failure = new Element("failure");
					failure.setAttribute("message", reportBean.getMessage());
					failure.setText(reportBean.getStackTrace());
					testcase.addContent(failure);
				}

				rootElement.addContent(testcase);
			}

			// Add the pass, fail counters as attributes to root element
			rootElement.setAttribute("failures", Integer.toString(failureCount));
			rootElement.setAttribute("tests", Integer.toString(testCount));

			// Write the DOM to xml file
			try {
				XMLOutputter outputter = new XMLOutputter();
				outputter.output(document, new FileOutputStream(reportFile));
			} catch (IOException e) {
				ApexUnitUtils
						.shutDownWithDebugLog(e, "IOException encountered while trying to write the ApexUnit test report to the file "
								+ reportFile);
			}
		} else {
			ApexUnitUtils.shutDownWithErrMsg(
					"Unable to generate test report. " + "Did not find any test results for the job id");
		}
	}

}
