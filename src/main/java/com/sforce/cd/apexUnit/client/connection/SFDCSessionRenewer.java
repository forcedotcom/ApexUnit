package com.sforce.cd.apexUnit.client.connection;

import javax.xml.namespace.QName;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.SessionRenewer;

public class SFDCSessionRenewer implements SessionRenewer {

	public SessionRenewalHeader renewSession(ConnectorConfig config) throws ConnectionException {
		PartnerConnection connection = ConnectionHandler.getConnectionHandlerInstance().getConnection();
		SessionRenewalHeader sessionRenewalHeader = new SessionRenewalHeader();
		sessionRenewalHeader.name = new QName("urn:partner.soap.sforce.com", "SessionHeader");
		sessionRenewalHeader.headerElement = connection.getSessionHeader();
		return sessionRenewalHeader;
	}
}