/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * @author adarsh.ramakrishna@salesforce.com
 */
package com.sforce.cd.apexUnit.testEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.testEngine.TestExecutor;
import com.sforce.soap.partner.PartnerConnection;

public class TestExecutorTest {

	private static final Logger LOG = LoggerFactory.getLogger(TestExecutorTest.class);
	PartnerConnection conn = null;

	@BeforeTest
	public void setup() {
		ConnectionHandler connectionHandler = ConnectionHandler.getConnectionHandlerInstance();
		//conn = connectionHandler.getConnection();
	}

	// @Test
	public void testLogicalFlow() {
		TestExecutor flowController = new TestExecutor();
		// flowController.logicalFlow();
	}

}
