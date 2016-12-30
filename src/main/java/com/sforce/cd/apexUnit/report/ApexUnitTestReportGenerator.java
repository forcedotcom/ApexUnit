/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
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
	 * @param reportBeans
	 * @param reportFile
	 *            of the job whose test report is to be generated
	 * 
	 */
	public static void generateTestReport(ApexReportBean[] reportBeans, String reportFile) {
		if (reportBeans != null && reportBeans.length > 0) {
			// Create root element
			Document document = new Document();
			Element rootElement = new Element("testsuite");
			document.setRootElement(rootElement);

			int failureCount = 0;
			int testCount = 0;
			Long totalTimeInMillis = 0L;

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

				// set time and accumulate for class
				Long timeInMillis = new Long(reportBean.getTimeElapsed());
				totalTimeInMillis += timeInMillis;
				testcase.setAttribute("time", Double.toString(timeInMillis.doubleValue() / 1000));

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
			rootElement.setAttribute("time", Double.toString(totalTimeInMillis.doubleValue() / 1000));

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
