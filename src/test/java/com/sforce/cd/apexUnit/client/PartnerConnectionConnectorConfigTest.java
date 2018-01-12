/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.sforce.cd.apexUnit.client;

import junit.framework.Assert;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.connection.PartnerConnectionConnectorConfig;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Ignore
public class PartnerConnectionConnectorConfigTest {
	private static Logger LOG = LoggerFactory.getLogger(PartnerConnectionConnectorConfigTest.class);

	@BeforeTest
	public void setup() {
		new CommandLineArgumentsTest().setup();
	}

	/*@Test
	public void testConfigForSessionRenewParam() throws ConnectionException {
		PartnerConnectionConnectorConfig pcConnectorConfig = new PartnerConnectionConnectorConfig();
		// Instantiate Connection Handler so that it sets max time out value
		ConnectionHandler connHandler = ConnectionHandler.getConnectionHandlerInstance();
		PartnerConnection connection = connHandler.getConnection();
		ConnectorConfig config = pcConnectorConfig.createConfig();
		LOG.debug("testConfigForSessionRenewParam() test .. "
				+ config.getSessionRenewer().renewSession(config).headerElement);
		// assert and check if timeout has been set accurately
		Assert.assertEquals(config.getConnectionTimeout(), ConnectionHandler.MAX_TIME_OUT_IN_MS_INT);
		// assert and check if the session info for connector config info for
		// session renewer component matches connection's session info
		Assert.assertEquals(config.getSessionRenewer().renewSession(config).headerElement,
				connection.getSessionHeader());
	}*/

}
