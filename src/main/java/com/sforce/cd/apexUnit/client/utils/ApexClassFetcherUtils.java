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
 * ApexClassFetcherUtils class consists of utility methods used for fetching details on Apex Classes 
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.QueryConstructor;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.fileReader.ApexManifestFileReader;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/*
 * ApexClassFetcher : This class helps to fetch Apex Classes (names/id's) based on various criteria .
 * The criteria is defined by the way query is constructed. 
 * Web services Connector is used to fetch the apex classes from a given org 
 * Manifest file(s) can also be used to populate the test classes
 */
public class ApexClassFetcherUtils {
	private static Logger LOG = LoggerFactory.getLogger(ApexClassFetcherUtils.class);
	public static Map<String, String> apexClassMap = new HashMap<String, String>();
	public static Map<String, String> duplicateApexClassMap = new HashMap<String, String>();

	/*
	 * This method returns a string array of apex test class id's. Either a
	 * manifest file or a regex pattern must be given as a command line input
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services returns apexClassIds as a string array
	 */
	// TODO : Use better Data structure as return type over to string arrays
	// TODO: parameterize and rename this method so that it can be
	// invoked from ToolingAPIInvoker to fetch Apex classes
	public static String[] constructTestClassesArray(PartnerConnection connection) {
		String[] testClassesAsArray = null;
		String[] consolidatedTestClassesAsArray = null;
		/*
		 * Builder design pattern construct the test class array by building the
		 * final array using simple objects(arrays) viz. array from Manifest
		 * file and array from regex prefix
		 */

		if (CommandLineArguments.getTestManifestFiles() != null) {
			LOG.debug(" Fetching apex test classes from location : " + CommandLineArguments.getTestManifestFiles());
			testClassesAsArray = fetchApexClassesFromManifestFiles(CommandLineArguments.getTestManifestFiles(), false);
		}
		if (CommandLineArguments.getTestRegex() != null) {
			LOG.debug(" Fetching apex test classes with prefix : " + CommandLineArguments.getTestRegex());
			consolidatedTestClassesAsArray = fetchApexClassesBasedOnMultipleRegexes(connection, testClassesAsArray,
					CommandLineArguments.getTestRegex(), false);
		} else {
			consolidatedTestClassesAsArray = testClassesAsArray;
		}
		// if null, no apex test classes fetched to execute; throw warning
		if (consolidatedTestClassesAsArray == null
				|| (consolidatedTestClassesAsArray != null && consolidatedTestClassesAsArray.length == 0)) {
			ApexUnitUtils.shutDownWithErrMsg("No/Invalid test classes mentioned in manifest file and/or "
					+ "regex pattern for ApexTestPrefix didn't return any test class names from the org");
		} else {
			LOG.debug("List of all the Fetched Apex test classes to execute:");
			if (LOG.isDebugEnabled()) {
				logTheFetchedApexClasses(consolidatedTestClassesAsArray);
			}
		}

		return consolidatedTestClassesAsArray;
	}

	/*
	 * This method returns a string array of apex class/test class id's fetched
	 * from manifest file(s) whose path is provided as a command line input The
	 * method expects the manifest files are placed in /src/main/resources
	 * folder of the maven package
	 * 
	 * @param manifestFiles - manifest file(s) that will be read to fetch the
	 * apex class names
	 */
	public static String[] fetchApexClassesFromManifestFiles(String manifestFiles, boolean includeTriggers) {
		String[] classIdsAsArray = null;
		if (manifestFiles != null) {
			ApexManifestFileReader apexManifestFileReader = new ApexManifestFileReader(includeTriggers);
			// fetch test class id's based on the test classes mentioned in the
			// manifest file(s)
			classIdsAsArray = apexManifestFileReader.fetchClassNamesFromManifestFiles(manifestFiles);
			if (classIdsAsArray == null || classIdsAsArray.length == 0) {
				LOG.warn("Given manifest file(s) contains invalid/no Apex classes. 0 Apex class id's returned");
			} else {
				LOG.debug("Fetched Apex test classes from the manifest file(s):" + manifestFiles);
				// log the test classes fetched
				if (LOG.isDebugEnabled()) {
					logTheFetchedApexClasses(classIdsAsArray);
				}
			}
		}
		return classIdsAsArray;
	}

	/*
	 * Utility method. This method returns a string array of apex test class
	 * id's fetched based on regex patterns(prefixes) given as a command line
	 * input
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services
	 * 
	 * @param classesAsArray - Apex Class Ids passed by the calling method. The
	 * returned string array of classes includes classes from this array as well
	 * 
	 * @param regexes - comma separated regexes that are used to fetch class
	 * names from the org
	 * 
	 * @param includeTriggers - boolean value whether or not to include triggers
	 *
	 * TODO: rename the method name so that it reflects that class Arrays are
	 * concatenated
	 */
	public static String[] fetchApexClassesBasedOnMultipleRegexes(PartnerConnection connection, String[] classesAsArray,
			String regexes, Boolean includeTriggers) {

		LOG.info("Using regex(es): " + regexes + " to fetch apex classes");
		String cvsSplitBy = ",";
		String[] cvsRegexes = regexes.split(cvsSplitBy);

		for (String regex : cvsRegexes) {
			// if both manifest file and testClass regex expression is provided
			// as command line option, combine the results
			// Also combine the results obtained for each regex
			classesAsArray = fetchApexClassesBasedOnRegex(connection, classesAsArray, regex, includeTriggers);
		}
		return classesAsArray;
	}

	/*
	 * Utility method This method returns a string array of apex test class id's
	 * fetched based on regex pattern given as a command line input
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services
	 * 
	 * @param classesAsArray - Apex Class Ids passed by the calling method. The
	 * returned string array of classes includes classes from this array as well
	 * 
	 * @param regexes - regex that is used to fetch classes from the org
	 */
	private static String[] fetchApexClassesBasedOnRegex(PartnerConnection connection, String[] classesAsArray,
			String regex, Boolean includeTriggers) {
		if (regex != null && !regex.equals(" ")) {
			LOG.info("Using regex: \"" + regex + "\" to fetch apex classes");
			// construct the query
			String namespace = null;
			String soql = QueryConstructor.generateQueryToFetchApexClassesBasedOnRegex(namespace, regex);
			// fire the query using WSC and fetch the results
			String[] classesAsArrayUsingWSC = constructClassIdArrayUsingWSC(connection, soql);
			// if both manifest file and testClass regex expression is provided
			// as command line option, combine the results

			Set<String> uniqueSetOfClasses = new HashSet<String>();
			ArrayList<String> duplicateList = new ArrayList<String>();
			// eliminate duplicates from the given class Ids
			// (just in case duplicates still exist in the class array passed
			// from the calling method)
			if (classesAsArray != null && classesAsArray.length > 0) {
				for (int i = 0; i < classesAsArray.length; i++) {
					if (!uniqueSetOfClasses.add(classesAsArray[i])) {
						duplicateList.add(classesAsArray[i]);
					}
				}
			}
			// eliminate duplicates from the classes fetched using the prefix
			if (classesAsArrayUsingWSC != null && classesAsArrayUsingWSC.length > 0) {
				for (int i = 0; i < classesAsArrayUsingWSC.length; i++) {
					if (!uniqueSetOfClasses.add(classesAsArrayUsingWSC[i])) {
						duplicateList.add(classesAsArrayUsingWSC[i]);
					}
				}
			}

			//if include triggers, add triggers to duplicate list as Triggers cannot be tested on force.com
			if(includeTriggers){
				String soqlForTrigger = QueryConstructor.generateQueryToFetchApexTriggersBasedOnRegex(namespace, regex);
				String[] triggersAsArrayUsingWSC = constructClassIdArrayUsingWSC(connection, soqlForTrigger);

				if (triggersAsArrayUsingWSC != null && triggersAsArrayUsingWSC.length > 0) {
					for (int i = 0; i < triggersAsArrayUsingWSC.length; i++) {
						if (!uniqueSetOfClasses.add(triggersAsArrayUsingWSC[i])) {
							duplicateList.add(triggersAsArrayUsingWSC[i]);
						}
					}
				}
			}

			// eliminate duplicates from the triggers fetched using the prefix

			String[] uniqueClassesAsArray = uniqueSetOfClasses.toArray(new String[uniqueSetOfClasses.size()]);

			// log the duplicate classes/triggers found by querying the org with
			// the given regex
			if (duplicateList != null && !duplicateList.isEmpty()) {
				String logDuplicates = "Found duplicates from the classes fetched from the regex: " + regex
						+ ". Skipping multiple execution/code coverage computation of these test class/source class(es) :";
				for (int i = 0; i < duplicateList.size(); i++) {
					duplicateApexClassMap.put(duplicateList.get(i), apexClassMap.get(duplicateList.get(i)));
					logDuplicates += " " + apexClassMap.get(duplicateList.get(i)) + ",";
				}
				LOG.info(logDuplicates);
			}

			return uniqueClassesAsArray;
		}
		return classesAsArray;
	}

	/*
	 * constructs test class array using web service connector api's
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services
	 * 
	 * @param soql - the query string
	 */
	public static String[] constructClassIdArrayUsingWSC(PartnerConnection connection, String soql) {

		String[] classIdsAsArray = null;
		QueryResult queryResult = null;
		try {
			queryResult = connection.query(soql);
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, ConnectionHandler
					.logConnectionException(e, connection, soql));
		}
		if (queryResult != null) {
			classIdsAsArray = fetchApexClassesAsArray(queryResult);
		} else {
			ApexUnitUtils.shutDownWithErrMsg("Faulty query performed using the soql Query " + soql
					+ " returned incorrect result. Halting the application");
		}
		return classIdsAsArray;
	}

	/*
	 * constructs string array of apex class id's from the query result
	 * 
	 * @param queryResult - QueryResult : the object obtained after executing a
	 * soql query
	 */
	private static String[] fetchApexClassesAsArray(QueryResult queryResult) {
		Object[] apexClassesObjArr = null;
		if (queryResult.getDone()) {
			SObject[] sObjects = queryResult.getRecords();
			if (sObjects != null) {
				ArrayList<String> apexClasses = new ArrayList<String>();
				LOG.debug("Fetched Apex classes:");
				for (SObject sobject : sObjects) {
					apexClasses.add(sobject.getField("Id").toString());
					// crucial step. Populate apexCLassMap each time a class is
					// fetched. Will be used for lot of computations
					apexClassMap.put(sobject.getField("Id").toString(), sobject.getField("Name").toString());
					LOG.debug("ApexClassId : " + sobject.getField("Id").toString() + "  ApexClassName : "
							+ sobject.getField("Name").toString());
				}
				apexClassesObjArr = apexClasses.toArray();
				return (Arrays.copyOf(apexClassesObjArr, apexClassesObjArr.length, String[].class));
			}
		}
		return null;
	}

	/*
	 * Returns Apex class id based on class name. Helps in validating for
	 * duplicates and making sure that only deployed class names are used in
	 * manifest file
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services
	 * 
	 * @param soqlBasedOnName - the query string in which where clause is based
	 * on name of the apex class
	 */
	public static String fetchAndAddToMapApexClassIdBasedOnName(PartnerConnection connection, String soqlBasedOnName) {
		String apexClassId = null;
		QueryResult queryResult = null;
		try {
			queryResult = connection.query(soqlBasedOnName);
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, ConnectionHandler
					.logConnectionException(e, connection, soqlBasedOnName));
		}
		if (queryResult != null && queryResult.getDone()) {
			String[] classIds = fetchApexClassesAsArray(queryResult);
			if(classIds!=null && classIds.length>0){
				apexClassId = classIds[0];
			}
		}

		return apexClassId;
	}

	/*
	 * Returns a string map with ApeX class Id as the key and corresponding Apex
	 * class name as the value
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services
	 * 
	 * @param apexClassId Apex Class Id for which Apex Class name is to be
	 * derived
	 */
	public static HashMap<String, String> fetchApexClassInfoFromId(PartnerConnection connection, String apexClassId) {
		String apexClassName = "";
		HashMap<String, String> apexClassInfoMap = new HashMap<String, String>();
		String soql = QueryConstructor.getApexClassInfo(apexClassId);
		QueryResult queryResult = null;
		try {
			queryResult = connection.query(soql);
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, ConnectionHandler
					.logConnectionException(e, connection, soql));
		}
		if (queryResult == null
				|| (queryResult != null && queryResult.getRecords() != null && queryResult.getRecords().length <= 0)) {
			// if query result is null, try if the apexClassId is associated
			// with a ApexTrigger table
			String soqlForTrigger = QueryConstructor.getApexTriggerInfo(apexClassId);
			try {
				queryResult = connection.query(soqlForTrigger);
			} catch (ConnectionException e) {
				ApexUnitUtils.shutDownWithDebugLog(e, ConnectionHandler
						.logConnectionException(e, connection, soqlForTrigger));
			}
		}
		if (queryResult != null && queryResult.getDone()) {
			SObject[] sObjects = queryResult.getRecords();
			for (SObject sobject : sObjects) {
				if (sobject != null) {
					apexClassName = sobject.getField("Name").toString();
					LOG.debug("Fetched the class for Id: " + apexClassId + " : " + apexClassName);
					apexClassInfoMap.put("Name", apexClassName);
					String apiVersion = sobject.getField("ApiVersion").toString();
					apexClassInfoMap.put("ApiVersion", apiVersion);
					String lengthWithoutComments = sobject.getField("LengthWithoutComments").toString();
					apexClassInfoMap.put("LengthWithoutComments", lengthWithoutComments);
				}
			}
		}

		return apexClassInfoMap;
	}

	/*
	 * Returns a string map with Apex class Id as the key and corresponding Apex
	 * class name as the value
	 * 
	 * @param connection - partnerConnection for the app to connect to the org
	 * using web services
	 * 
	 * @param apexClassId Apex Class Id for which Apex Class name is to be
	 * derived
	 */
	public static String fetchApexTestClassNameFromId(PartnerConnection connection, String apexClassId) {
		String apexClassName = "";
		String soql = QueryConstructor.generateQueryToFetchApexClassFromId(apexClassId);
		QueryResult queryResult = null;
		try {
			queryResult = connection.query(soql);
		} catch (ConnectionException e) {
			ApexUnitUtils.shutDownWithDebugLog(e, ConnectionHandler
					.logConnectionException(e, connection, soql));
		}
		if (queryResult != null && queryResult.getDone()) {
			SObject[] sObjects = queryResult.getRecords();
			for (SObject sobject : sObjects) {
				if (sobject != null) {
					apexClassName = sobject.getField("Name").toString();
					LOG.info("Fetched the Apex test class for Id: " + apexClassId + " : " + apexClassName);
					if (!apexClassMap.containsKey(apexClassId)) {
						apexClassMap.put(apexClassId, apexClassName);
					}
				}
			}
		}
		return apexClassName;
	}

	/*
	 * Logs Apex Classes fetched from the org
	 * 
	 * @param classIds - Array of apex class Ids
	 */
	public static void logTheFetchedApexClasses(String[] classIds) {
		if (classIds != null && classIds.length > 0) {
			LOG.debug("Fetched apex classes: ");
			for (int i = 0; i < classIds.length; i++) {
				if (apexClassMap.containsKey(classIds[i])) {
					LOG.debug(apexClassMap.get(classIds[i]));
				} else {
					LOG.debug(classIds[i] + "(Unable to find class name for this id)");
				}
			}
		}
	}

}
