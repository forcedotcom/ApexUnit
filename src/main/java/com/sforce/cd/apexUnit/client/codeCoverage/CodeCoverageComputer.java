/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class to compute code coverage for the given class names and org wide code coverage
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.codeCoverage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.QueryConstructor;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.cd.apexUnit.report.ApexClassCodeCoverageBean;
import com.sforce.cd.apexUnit.report.ApexUnitCodeCoverageResults;
import com.sforce.soap.partner.PartnerConnection;

public class CodeCoverageComputer {
	private static Logger LOG = LoggerFactory.getLogger(CodeCoverageComputer.class);
	Properties prop = new Properties();
	String propFileName = "config.properties";
	private String SUPPORTED_VERSION = System.getProperty("API_VERSION");

	/*
	 * Constructor for CodeCoverageComputer Initialize SUPPORTED_VERSION
	 * variable from property file TODO fetch SUPPORTED_VERSION from the org(by
	 * querying?)
	 */
	public CodeCoverageComputer() {
		// execute below code Only when System.getProperty() call doesn't
		// function
		if (SUPPORTED_VERSION == null) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				try {
					prop.load(inputStream);
				} catch (IOException e) {
					ApexUnitUtils
							.shutDownWithErrMsg("IO exception encountered while reading from the file:" + propFileName);
				}
			}
			SUPPORTED_VERSION = prop.getProperty("API_VERSION");
		}
	}

	/**
	 * Calculate Aggregated code coverage results for the Apex classes using
	 * Tooling API's
	 * 
	 * @return code coverage result(beans) as array
	 */
	public ApexClassCodeCoverageBean[] calculateAggregatedCodeCoverageUsingToolingAPI() {
		PartnerConnection connection = ConnectionHandler.getConnectionHandlerInstance().getConnection();

		ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans = null;
		String[] classesAsArray = null;

		/*
		 * Builder design pattern construct the test class array by building the
		 * final array using simple objects(arrays) viz. array from Manifest
		 * file and array from regex prefix
		 */
		// read class names from manifest files
		
		if (CommandLineArguments.getClassManifestFiles() != null) {
			LOG.debug(" Fetching apex classes from location : " + CommandLineArguments.getClassManifestFiles());
			classesAsArray = ApexClassFetcherUtils
					.fetchApexClassesFromManifestFiles(CommandLineArguments.getClassManifestFiles(), true);
		}
		// fetch matching class names based on regex
		if (CommandLineArguments.getSourceRegex() != null) {
			LOG.debug(" Fetching apex classes with regex : " + CommandLineArguments.getSourceRegex());
			classesAsArray = ApexClassFetcherUtils.fetchApexClassesBasedOnMultipleRegexes(connection, classesAsArray,
					CommandLineArguments.getSourceRegex(), true);
		}
		// Do not proceed if no class names are returned from both manifest
		// files and/or regexes
		if (classesAsArray != null && classesAsArray.length > 0) {
			String classArrayAsStringForQuery = processClassArrayForQuery(classesAsArray);
			String relativeServiceURL = "/services/data/v" + SUPPORTED_VERSION + "/tooling";
			// compute aggregated code coverage
			String soqlcc = QueryConstructor.getAggregatedCodeCoverage(classArrayAsStringForQuery);
			
			JSONObject responseJsonObject = null;
			responseJsonObject = WebServiceInvoker.doGet(relativeServiceURL, soqlcc, OAuthTokenGenerator.getOrgToken());
			LOG.debug("responseJsonObject says " + responseJsonObject + "\n relativeServiceURL is " + relativeServiceURL
					+ "\n soqlcc is " + soqlcc);
			if (responseJsonObject != null) {
				apexClassCodeCoverageBeans = processJSONResponseAndConstructCodeCoverageBeans(connection,
						responseJsonObject);
			}
			if (apexClassCodeCoverageBeans == null) {
				ApexUnitUtils.shutDownWithErrMsg(
						"Code coverage metrics not computed. Null object returned while processing the JSON response from the Tooling API");
			}
		} else {
			ApexUnitUtils.shutDownWithErrMsg("No/Invalid Apex source classes mentioned in manifest file and/or "
					+ "regex pattern for ApexSourceClassPrefix didn't return any Apex source class names from the org");
		}
		return apexClassCodeCoverageBeans;
	}

	/*
	 * convert string array into csv string to facilitate the querying
	 * 
	 * @param: class names as string array
	 * 
	 * @return csv class names as string
	 */
	private String processClassArrayForQuery(String[] classesAsArray) {
		String queryString = "";
		for (int i = 0; i < classesAsArray.length; i++) {
			queryString += "'" + classesAsArray[i] + "'";
			queryString += ",";
		}
		if (queryString.length() > 1) {
			queryString = queryString.substring(0, queryString.length() - 1);
		}
		return queryString;
	}

	/*
	 * Derive an array of code coverage beans from processing the JSON response
	 * of Tooling API call
	 * 
	 * @param connection = partner connection
	 * 
	 * @param responseJsonObject - json response object that needs to be
	 * processed
	 * 
	 * @return code coverage result(beans) as array
	 */
	private ApexClassCodeCoverageBean[] processJSONResponseAndConstructCodeCoverageBeans(PartnerConnection connection,
			JSONObject responseJsonObject) {
		int classCounter = 0;
		int coveredLinesForTheTeam = 0;
		int unCoveredLinesForTheTeam = 0;
		String responseStr = responseJsonObject.toJSONString();
		LOG.debug(responseStr);
		JSONArray recordObject = (JSONArray) responseJsonObject.get("records");
		if (recordObject != null && recordObject.size() > 0) {
			ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans = new ApexClassCodeCoverageBean[recordObject.size()];
			for (int i = 0; i < recordObject.size(); ++i) {

				ApexClassCodeCoverageBean apexClassCodeCoverageBean = new ApexClassCodeCoverageBean();
				// The object below is one record from the ApexCodeCoverage
				// object
				JSONObject rec = (JSONObject) recordObject.get(i);

				// ApexClassOrTriggerId - The ID of the class or trigger under
				// test.
				String apexClassOrTriggerId = (String) rec.get("ApexClassOrTriggerId").toString();
				if (apexClassOrTriggerId != null) {
					int coveredLines = 0;
					if (rec.get("NumLinesCovered") != null) {
						coveredLines = Integer.valueOf((String) rec.get("NumLinesCovered").toString());
						coveredLinesForTheTeam += coveredLines;
					} else {
						LOG.debug(apexClassOrTriggerId + " has NumLinesCovered as NULL !!!!!!!!!");
					}
					int unCoveredLines = 0;
					if (rec.get("NumLinesUncovered") != null) {
						unCoveredLines = Integer.valueOf((String) rec.get("NumLinesUncovered").toString());
						unCoveredLinesForTheTeam += unCoveredLines;
					} else {
						LOG.debug(apexClassOrTriggerId + " has NumLinesUncovered as NULL !!!!!!!!!");
					}
					if (rec.get("Coverage") != null) {
						JSONObject codeCoverageLineLists = (JSONObject) rec.get("Coverage");
						JSONArray coveredLinesJsonArray = (JSONArray) codeCoverageLineLists.get("coveredLines");
						JSONArray uncoveredLinesJsonArray = (JSONArray) codeCoverageLineLists.get("uncoveredLines");
						List<Long> coveredLinesList = new ArrayList<Long>();
						for (int j = 0; j < coveredLinesJsonArray.size(); j++) {
							coveredLinesList.add((Long) coveredLinesJsonArray.get(j));
							LOG.debug("covered " + (Long) coveredLinesJsonArray.get(j));
						}
						if (coveredLinesList.size() > 0) {
							apexClassCodeCoverageBean.setCoveredLinesList(coveredLinesList);
						}

						List<Long> uncoveredLinesList = new ArrayList<Long>();
						for (int k = 0; k < uncoveredLinesJsonArray.size(); k++) {
							uncoveredLinesList.add((Long) uncoveredLinesJsonArray.get(k));
							LOG.debug("uncovered " + (Long) uncoveredLinesJsonArray.get(k));
						}
						if (uncoveredLinesList.size() > 0) {
							apexClassCodeCoverageBean.setUncoveredLinesList(uncoveredLinesList);
						}
					}

					apexClassCodeCoverageBean.setNumLinesCovered(coveredLines);
					apexClassCodeCoverageBean.setNumLinesUncovered(unCoveredLines);
					apexClassCodeCoverageBean.setApexClassorTriggerId(apexClassOrTriggerId);
					HashMap<String, String> apexClassInfoMap = ApexClassFetcherUtils
							.fetchApexClassInfoFromId(connection, apexClassOrTriggerId);
					String apexClassName = apexClassInfoMap.get("Name");
					String apiVersion = apexClassInfoMap.get("ApiVersion");
					String lengthWithoutComments = apexClassInfoMap.get("LengthWithoutComments");
					apexClassCodeCoverageBean.setApexClassName(apexClassName);
					apexClassCodeCoverageBean.setApiVersion(apiVersion);
					apexClassCodeCoverageBean.setLengthWithoutComments(lengthWithoutComments);
					apexClassCodeCoverageBeans[classCounter++] = apexClassCodeCoverageBean;

					LOG.info("Record number # " + classCounter + " : coveredLines : " + coveredLines
							+ " : unCoveredLines : " + unCoveredLines + " : code coverage % : "
							+ apexClassCodeCoverageBean.getCoveragePercentage() + " : apexClassOrTriggerId : "
							+ apexClassOrTriggerId + " : apexClassName : " + apexClassName + " : apiVersion : "
							+ apiVersion + " : lengthWithoutComments : " + lengthWithoutComments);
				}
			}
			double totalLines = coveredLinesForTheTeam + unCoveredLinesForTheTeam;
			if (totalLines > 0.0) {
				ApexUnitCodeCoverageResults.teamCodeCoverage = (coveredLinesForTheTeam / (totalLines)) * 100.0;
			} else {
				ApexUnitCodeCoverageResults.teamCodeCoverage = 100.0;
			}
			LOG.info(
					"####################################   Summary of code coverage computation for the team..  #################################### ");
			LOG.info("Total Covered lines : " + coveredLinesForTheTeam + "\n Total Uncovered lines : "
					+ unCoveredLinesForTheTeam);

			LOG.info("Team code coverage is : " + ApexUnitCodeCoverageResults.teamCodeCoverage + "%");
			return apexClassCodeCoverageBeans;
		} else {
			// no code coverage record object found in the response. return null
			return null;
		}
	}

	/**
	 * This method is not used currently Calculate code coverage results for the
	 * Apex classes using Tooling API's This method is intended to provide code
	 * coverage at method level for each class . This indicates which exact
	 * method needs more coverage
	 * 
	 * @return
	 */
	public void calculateCodeCoverageUsingToolingAPI(String classArrayAsStringForQuery) {
		int classCounter = 0;
		String relativeServiceURL = "/services/data/v" + SUPPORTED_VERSION + "/tooling";
		String soqlcc = QueryConstructor.getClassLevelCodeCoverage(classArrayAsStringForQuery);
		LOG.debug("OAuthTokenGenerator.getOrgToken() : " + OAuthTokenGenerator.getOrgToken());
		JSONObject responseJsonObject = null;
		responseJsonObject = WebServiceInvoker.doGet(relativeServiceURL, soqlcc, OAuthTokenGenerator.getOrgToken());

		if (responseJsonObject != null) {
			String responseStr = responseJsonObject.toJSONString();
			LOG.debug(responseStr);
			JSONArray recordObject = (JSONArray) responseJsonObject.get("records");
			for (int i = 0; i < recordObject.size(); ++i) {
				classCounter++;
				// The object below is one record from the ApexCodeCoverage
				// object
				JSONObject rec = (JSONObject) recordObject.get(i);

				int coveredLines = Integer.valueOf((String) rec.get("NumLinesCovered").toString());
				int unCoveredLines = Integer.valueOf((String) rec.get("NumLinesUncovered").toString());
				// ApexTestClassId - The ID of the test class.
				String apexTestClassID = (String) rec.get("ApexTestClassId").toString();
				// ApexClassOrTriggerId - The ID of the class or trigger under
				// test.
				String apexClassorTriggerId = (String) rec.get("ApexClassOrTriggerId").toString();
				String testMethodName = (String) rec.get("TestMethodName").toString();
				LOG.info("Record number # " + classCounter + " : coveredLines : " + coveredLines
						+ " : unCoveredLines : " + unCoveredLines + " : apexTestClassID : " + apexTestClassID
						+ " : apexClassorTriggerId : " + apexClassorTriggerId + " : testMethodName : "
						+ testMethodName);

			}

		}
	}

	/*
	 * Calculate org wide code coverage results for all the Apex classes in the
	 * org using Tooling API's
	 * 
	 * @return org wide code coverage result as integer
	 */
	public int getOrgWideCodeCoverage() {
		String relativeServiceURL = "/services/data/v" + SUPPORTED_VERSION + "/tooling";
		String soql = QueryConstructor.getOrgWideCoverage();
		int coverage = 0;
		JSONObject responseJsonObject = null;
		responseJsonObject = WebServiceInvoker.doGet(relativeServiceURL, soql, OAuthTokenGenerator.getOrgToken());

		if (responseJsonObject != null) {
			String responseStr = responseJsonObject.toJSONString();
			LOG.debug("responseStr during org wide code coverage" + responseStr);
			JSONArray recordObject = (JSONArray) responseJsonObject.get("records");
			for (int i = 0; i < recordObject.size(); ++i) {

				JSONObject rec = (JSONObject) recordObject.get(i);

				coverage = Integer.valueOf((String) rec.get("PercentCovered").toString());
				LOG.info(
						"####################################   Org wide code coverage result  #################################### ");
				LOG.info("Org wide code coverage : " + coverage + "%");
			}
		} else {
			ApexUnitUtils.shutDownWithErrMsg("Org wide code coverage not computed");
		}
		ApexUnitCodeCoverageResults.orgWideCodeCoverage = coverage;
		return coverage;
	}
}
