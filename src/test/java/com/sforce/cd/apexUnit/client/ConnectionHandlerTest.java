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

import junit.framework.Assert;

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

public class ConnectionHandlerTest {

	ConnectionHandler connHandler = ConnectionHandler.getConnectionHandlerInstance();
	private static Logger LOG = LoggerFactory.getLogger(ConnectionHandlerTest.class);

	@Test(priority = 1)
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

	}

}
