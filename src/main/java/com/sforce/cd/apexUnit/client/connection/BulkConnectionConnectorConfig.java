/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.sforce.cd.apexUnit.client.connection;

import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.ws.ConnectorConfig;

public class BulkConnectionConnectorConfig implements ConnectorConfigInterface {

	public ConnectorConfig createConfig() {
		// When PartnerConnection is instantiated, a login is implicitly
		// executed and, if successful,
		// a valid session is stored in the ConnectorConfig instance.
		// Use this key to initialize a BulkConnection:
		CommonConnectorConfig commonConnConfig = new CommonConnectorConfig();
		ConnectorConfig config = commonConnConfig.createConfig();
		String sessionId = ConnectionHandler.getConnectionHandlerInstance().getSessionIdFromConnectorConfig();
		config.setSessionId(sessionId);
		String restEndPoint = CommandLineArguments.getOrgUrl() + "/services/async/"
				+ ConnectionHandler.SUPPORTED_VERSION;
		config.setRestEndpoint(restEndPoint);
		config.setTraceMessage(false);
		return config;
	}

}
