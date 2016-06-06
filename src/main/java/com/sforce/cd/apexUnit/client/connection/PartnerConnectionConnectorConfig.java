/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.sforce.cd.apexUnit.client.connection;

import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.ws.ConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartnerConnectionConnectorConfig implements ConnectorConfigInterface {
	private static Logger LOG = LoggerFactory.getLogger(PartnerConnectionConnectorConfig.class);

	public ConnectorConfig createConfig() {
		CommonConnectorConfig commonConnConfig = new CommonConnectorConfig();
		ConnectorConfig config = commonConnConfig.createConfig();
		config.setAuthEndpoint(
				CommandLineArguments.getOrgUrl() + "/services/Soap/u/" + ConnectionHandler.SUPPORTED_VERSION);
		config.setSessionRenewer(new SFDCSessionRenewer());
		LOG.info("Default connection time out value is: " + config.getConnectionTimeout());
		config.setConnectionTimeout(ConnectionHandler.MAX_TIME_OUT_IN_MS_INT);
		LOG.info("Updated connection time out value(from config.properties file): " + config.getConnectionTimeout());
		return config;
	}

}
