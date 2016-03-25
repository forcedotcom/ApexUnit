/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.sforce.cd.apexUnit.client.connection;

import javax.xml.namespace.QName;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.SessionRenewer;

public class SFDCSessionRenewer implements SessionRenewer {
// Thanks to Thys Michels blog for an example implementation. Reference:
// http://thysmichels.com/2014/02/15/salesforce-wsc-partner-connection-session-renew-when-session-timeout/
	public SessionRenewalHeader renewSession(ConnectorConfig config) throws ConnectionException {
		PartnerConnection connection = ConnectionHandler.getConnectionHandlerInstance().getConnection();
		SessionRenewalHeader sessionRenewalHeader = new SessionRenewalHeader();
		sessionRenewalHeader.name = new QName("urn:partner.soap.sforce.com", "SessionHeader");
		sessionRenewalHeader.headerElement = connection.getSessionHeader();
		return sessionRenewalHeader;
	}
}