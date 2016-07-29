/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class that creates and handles the connection with the org using salesforce wsc api's
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.client.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.arguments.PositiveIntegerValidator;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class ConnectionHandler {
	private static ConnectionHandler connectionHandler = null;
	private static Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);
	Properties prop = new Properties();
	String propFileName = "config.properties";
	public static int MAX_TIME_OUT_IN_MS_INT;
	public static String SUPPORTED_VERSION = System.getProperty("API_VERSION");
	private String MAX_TIME_OUT_IN_MS = System.getProperty("MAX_TIME_OUT_IN_MS");

	private String sessionIdFromConnectorConfig = null;
	PartnerConnection connection = null;
	BulkConnection bulkConnection = null;
	/*
	 * Constructor for ConnectionHandler
	 * Initialize SUPPORTED_VERSION variable from property file
	 * TODO fetch SUPPORTED_VERSION from the org(by querying?)
	 */
	private ConnectionHandler() {
		// execute below code Only when System.getProperty() call doesn't
		// function
		if (SUPPORTED_VERSION == null || MAX_TIME_OUT_IN_MS == null) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				try {
					prop.load(inputStream);
				} catch (IOException e) {
					ApexUnitUtils.shutDownWithDebugLog(e, "Error while trying to load the property file "
									+ propFileName
									+ " Unable to establish Connection with the org. Suspending the run..");
				}
			}
			SUPPORTED_VERSION = prop.getProperty("API_VERSION");
			MAX_TIME_OUT_IN_MS = prop.getProperty("MAX_TIME_OUT_IN_MS");
		}
		PositiveIntegerValidator positiveIntegerValidator = new PositiveIntegerValidator();
		positiveIntegerValidator.validate("MAX_TIME_OUT_IN_MS", MAX_TIME_OUT_IN_MS);
		try {
			MAX_TIME_OUT_IN_MS_INT = Integer.parseInt(MAX_TIME_OUT_IN_MS);
		} catch (NumberFormatException nfe) {
			ApexUnitUtils.shutDownWithErrMsg(
					"Invalid value for MAX_TIME_OUT_IN_MS in the property file. Only positive integers are allowed");
		}		
	}

	// singleton pattern.. Ensures the class has only one common instance of
	// connectionHandler and provides global access point
	// TODO Comments by Vamshi:
	// "In that case this is perfect use case of builder design pattern.
	// Following such standards will eliminate these questions for any new
	// person to
	// understand the intent of your flow"
	public static ConnectionHandler getConnectionHandlerInstance() {
		if (connectionHandler == null) {
			connectionHandler = new ConnectionHandler();
		}
		return connectionHandler;

	}

	/*
	 * create partner connection using the wsc connector
	 * 
	 * @return connection - instance of PartnerConnection
	 */
			
	private PartnerConnection createConnection() {
		if (connection == null) {
			PartnerConnectionConnectorConfig pcConnectorConfig = new PartnerConnectionConnectorConfig();
			ConnectorConfig config = pcConnectorConfig.createConfig();
			
			LOG.debug("creating connection for : " + CommandLineArguments.getUsername() + " "
					+ CommandLineArguments.getOrgUrl() + " "
					+ config.getUsername() + " " + config.getAuthEndpoint());
			try {
				connection = Connector.newConnection(config);
				setSessionIdFromConnectorConfig(config);
				LOG.debug("Partner Connection established with the org!! \n SESSION  ID IN createPartnerConn: "
						+ sessionIdFromConnectorConfig);
			} catch (ConnectionException connEx) {
				ApexUnitUtils.shutDownWithDebugLog(connEx, ConnectionHandler
						.logConnectionException(connEx, connection));
			}
		}
		return connection;
	}
	
	/*
	 * method to retrieve sessionId from connector config
	 * Used for initializing bulk connection
	 * 
	 * @return sessionIdFromConnectorConfig = string representing session ID
	 */

	public String getSessionIdFromConnectorConfig() {
		// if sessionId is null, in all probability connection is null(not
		// created)
		if (sessionIdFromConnectorConfig == null && connection == null) {
			// createConnection() call creates connection and sets the
			// sessionId
			connection = createConnection();
		}
		return sessionIdFromConnectorConfig;
	}

	/*
	 * set session id from connection config
	 */
	public void setSessionIdFromConnectorConfig(ConnectorConfig config) {
		sessionIdFromConnectorConfig = config.getSessionId();
	}

	// BulkConnection instance is the base for using the Bulk API.
	// The instance can be reused for the rest of the application life span.
	private BulkConnection createBulkConnection() {
		
		BulkConnectionConnectorConfig bcConnectorConfig = new BulkConnectionConnectorConfig();
		ConnectorConfig config = bcConnectorConfig.createConfig();
		try {
			bulkConnection = new BulkConnection(config);
			LOG.info("Bulk connection established.");
		} catch (AsyncApiException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "Caught AsyncApiException exception while trying to deal with bulk connection: "
							+ e.getMessage());
		}
		return bulkConnection;
	}

	public PartnerConnection getConnection() {
		if (connection == null) {
			connection = createConnection();
		}
		return connection;
	}

	public void setConnection(PartnerConnection connection) {
		this.connection = connection;
	}

	public BulkConnection getBulkConnection() {
		if (bulkConnection == null) {
			createBulkConnection();
		}
		return bulkConnection;
	}

	public void setBulkConnection(BulkConnection bulkConnection) {
		this.bulkConnection = bulkConnection;
	}

	public static String logConnectionException(ConnectionException connEx, PartnerConnection conn) {
		return logConnectionException(connEx, conn, null);
	}

	public static String logConnectionException(ConnectionException connEx, PartnerConnection conn, String soql) {
		StringBuffer returnString = new StringBuffer("Connection Exception encountered ");
		if (null != soql) {
			returnString.append("when trying to query : " + soql);
		}
		returnString.append(" The connection exception description says : " + connEx.getMessage());
		returnString.append(" Object dump for the Connection object: " + ReflectionToStringBuilder.toString(conn));

		return returnString.toString();

	}

}

