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

import junit.framework.Assert;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.sforce.async.BulkConnection;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Ignore
public class ConnectionHandlerTest {

	ConnectionHandler connHandler = ConnectionHandler.getConnectionHandlerInstance();
	private static Logger LOG = LoggerFactory.getLogger(ConnectionHandlerTest.class);

	/*@Test(priority = 1)
	public void createConnection() throws ConnectionException {
		PartnerConnection partnerConn = connHandler.getConnection();
		LoginResult loginRes = null;
		if (partnerConn != null) {
			loginRes = partnerConn.login(CommandLineArguments.getUsername(), CommandLineArguments.getPassword());
		}
		if (loginRes != null) {
			Assert.assertEquals(false, loginRes.getPasswordExpired());
		} else {
			ApexUnitUtils.shutDownWithErrMsg("Unable to create Connection.. Failed test!");
		}
	}

	@Test(priority = 2)
	public void getBulkConnection() {
		String sessionId = connHandler.getSessionIdFromConnectorConfig();
		BulkConnection bulkConn = connHandler.getBulkConnection();
		ConnectorConfig connConfig = bulkConn.getConfig();
		LOG.info("Rest  end point:" + connConfig.getRestEndpoint());
		Assert.assertEquals(sessionId, connConfig.getSessionId());

	}*/

}
