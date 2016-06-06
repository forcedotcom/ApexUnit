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

public class CommonConnectorConfig implements ConnectorConfigInterface {
	private static Logger LOG = LoggerFactory.getLogger(CommonConnectorConfig.class);

	public ConnectorConfig createConfig() {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(CommandLineArguments.getUsername());
		config.setPassword(CommandLineArguments.getPassword());
		config.setCompression(true);
		if (CommandLineArguments.getProxyHost() != null && CommandLineArguments.getProxyPort() != null) {
			LOG.debug("Setting proxy configuraiton to " + CommandLineArguments.getProxyHost() + " on port "
					+ CommandLineArguments.getProxyPort());
			config.setProxy(CommandLineArguments.getProxyHost(), CommandLineArguments.getProxyPort());
		}
		return config;
	}

}
