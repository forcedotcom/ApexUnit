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
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.client;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.codeCoverage.CodeCoverageComputer;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.report.ApexClassCodeCoverageBean;
import com.sforce.soap.partner.PartnerConnection;

public class ToolingAPIInvokerTest {
	CodeCoverageComputer codeCoverageComputer = null;
	PartnerConnection conn = null;
	private static final Logger LOG = LoggerFactory.getLogger(ToolingAPIInvokerTest.class);

	@BeforeTest
	public void setup() {
		new CommandLineArgumentsTest().setup();
		codeCoverageComputer = new CodeCoverageComputer();
		ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
		conn = connectionHandler.getConnection();
	}

	@Test
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

}
