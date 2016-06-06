/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.sforce.cd.apexUnit.client.connection;

import com.sforce.ws.ConnectorConfig;

public interface ConnectorConfigInterface {
	ConnectorConfig createConfig();
}
