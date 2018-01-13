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

import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.codeCoverage.WebServiceInvoker;
import org.junit.Assert;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.codeCoverage.CodeCoverageComputer;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.report.ApexClassCodeCoverageBean;
import com.sforce.soap.partner.PartnerConnection;

import java.io.UnsupportedEncodingException;

import static java.net.URLEncoder.encode;

@Ignore
public class ToolingAPIInvokerTest {
	CodeCoverageComputer codeCoverageComputer = null;
	PartnerConnection conn = null;
	private static final Logger LOG = LoggerFactory.getLogger(ToolingAPIInvokerTest.class);

	@BeforeTest
	public void setup() {
		new CommandLineArgumentsTest().setup();
		codeCoverageComputer = new CodeCoverageComputer();
		ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
		//conn = connectionHandler.getConnection();
	}

	/*@Test
	public void calculateAggregatedCodeCoverageUsingToolingAPITest() {
		// TODO Assert on contents of apexClassCodeCoverageBeans over to size
		ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans = codeCoverageComputer
				.calculateAggregatedCodeCoverageUsingToolingAPI();
		Assert.assertTrue(apexClassCodeCoverageBeans != null && apexClassCodeCoverageBeans.length > 0);
	}

	@Test
	public void getOrgWideCodeCoverageTest() {
		int orgWideCodeCoverage = -1;
		orgWideCodeCoverage = codeCoverageComputer.getOrgWideCodeCoverage();
		LOG.info("orgWideCodeCoverage: " + orgWideCodeCoverage);
		Assert.assertTrue(orgWideCodeCoverage >= 0);
	}

	@Test
	public void RequestStringPasswordIsEncodedTest() throws UnsupportedEncodingException {
		WebServiceInvoker wsi = new WebServiceInvoker();
		String requestString = wsi.generateRequestString();
		String passwordIdentifier = "&password=";
		String encodedPassword = encode(CommandLineArguments.getPassword(), "UTF-8");
		int indexOfpasswordIdentifier = requestString.indexOf(passwordIdentifier);
		String passwordInRequestString = requestString.substring(indexOfpasswordIdentifier + passwordIdentifier.length());
		org.testng.Assert.assertEquals(encodedPassword, passwordInRequestString);
	}*/
}
