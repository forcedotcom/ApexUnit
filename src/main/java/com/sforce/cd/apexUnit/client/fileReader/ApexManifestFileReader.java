/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class to read manifest files containing Apex class names
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client.fileReader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.client.QueryConstructor;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;

public class ApexManifestFileReader {
	private static Logger LOG = LoggerFactory.getLogger(ApexManifestFileReader.class);
	public static ArrayList<String> nonExistantApexClassEntries = new ArrayList<String>();
	private boolean includeTriggers;

	public ApexManifestFileReader(boolean includeTriggers) {
		this.includeTriggers = includeTriggers;
	}

	public String[] fetchClassNamesFromManifestFiles(String files) {
		String[] apexClassesStrArr = null;
		String[] apexClassesStrArrForManifest = null;
		LOG.info("Reading from Manifest files: " + files);
		String[] manifestFiles = files.split(",");

		for (String file : manifestFiles) {
			LOG.info("Reading Manifest file from location : " + file);
			InputStream inStr;
			try {
				inStr = this.getClass().getClassLoader().getResourceAsStream(file);
				if (inStr != null) {
					apexClassesStrArrForManifest = readInputStreamAndConstructClassArray(inStr);
				} else {
					ApexUnitUtils.shutDownWithErrMsg(
							"Unable to find the file " + file + " in the src->main->resources folder");
				}
			} catch (IOException e) {
				ApexUnitUtils.shutDownWithDebugLog(e, "IOException while trying to read the manifest file " + file);
			}
			if (apexClassesStrArrForManifest != null) {
				apexClassesStrArr = (String[]) ArrayUtils.addAll(apexClassesStrArr, apexClassesStrArrForManifest);
			} else {
				LOG.warn("Given manifest file " + file
						+ " contains invalid/no test classes. 0 Apex test class id's returned");
			}
		}
		Set<String> uniqueSetOfClasses = new HashSet<String>();
		if (apexClassesStrArr != null && apexClassesStrArr.length > 0) {
			for (String apexClass : apexClassesStrArr) {
				if (!uniqueSetOfClasses.add(apexClass)) {
					LOG.warn("Duplicate entry found across manifest files for : "
							+ ApexClassFetcherUtils.apexClassMap.get(apexClass)
							+ " . Skipping multiple execution/code coverage computation of this test class/source class");
				}
			}
		}
		String[] uniqueClassesAsArray = uniqueSetOfClasses.toArray(new String[uniqueSetOfClasses.size()]);
		if (LOG.isDebugEnabled()) {
			ApexClassFetcherUtils.logTheFetchedApexClasses(apexClassesStrArr);
		}
		return uniqueClassesAsArray;
	}

	// Split up the method for testability
	private String[] readInputStreamAndConstructClassArray(InputStream inStr) throws IOException {
		String[] testClassesAsArray = null;
		ArrayList<String> testClassList = new ArrayList<String>();

		LOG.debug("Input stream: " + inStr);
		DataInputStream dataIS = new DataInputStream(inStr);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataIS));
		String strLine = null;
		String newline = System.getProperty("line.separator");
		
		while ((strLine = bufferedReader.readLine()) != null) {
			if (!newline.equals(strLine) && !strLine.equals("") && strLine.length() > 0) {
				LOG.debug("The line says .... -  " + strLine);
				insertIntoTestClassesArray(strLine, testClassList);
			}
		}
		dataIS.close();

		Object[] apexClassesObjArr = testClassList.toArray();
		testClassesAsArray = (Arrays.copyOf(apexClassesObjArr, apexClassesObjArr.length, String[].class));
		if (LOG.isDebugEnabled()) {
			ApexClassFetcherUtils.logTheFetchedApexClasses(testClassesAsArray);
		}
		return testClassesAsArray;
	}
	
	/*
	 * inserts the classname from manifest file to the test class array
	 * 
	 * @param strLine - String type - classname provided on manifest file
	 * @param testClassList - ArrayList of String - test classes saved so far
	 */
	private void insertIntoTestClassesArray(String strLine, ArrayList<String> testClassList){
		String tempTestClassId = null;
		String[] splitByPeriod = strLine.split("\\.", 2);
		String namespace = null;
		String classname = strLine;

		if (splitByPeriod.length == 2) {
			namespace = splitByPeriod[0].trim();
			classname = splitByPeriod[1].trim();
		}
		
		String soql = QueryConstructor.generateQueryToFetchApexClass(namespace, classname);
		// query using WSC
		tempTestClassId = ApexClassFetcherUtils.fetchAndAddToMapApexClassIdBasedOnName(
				ConnectionHandler.getConnectionHandlerInstance().getConnection(), soql);
		LOG.debug("tempTestClassId: " + tempTestClassId);
		
		//triggers are included only for code coverage and not for tests to avoid exception by the platform
		if (tempTestClassId == null && includeTriggers) {
			// look if the given class name is a trigger if its not ApexClass
			String soqlForTrigger = QueryConstructor.generateQueryToFetchApexTrigger(namespace, classname);
			// query using WSC
			tempTestClassId = ApexClassFetcherUtils.fetchAndAddToMapApexClassIdBasedOnName(
					ConnectionHandler.getConnectionHandlerInstance().getConnection(), soqlForTrigger);
			LOG.debug("tempTestClassId(TriggerId: " + tempTestClassId);
		}
		if (tempTestClassId != null) {
			if (!testClassList.contains(tempTestClassId)) {
				testClassList.add(tempTestClassId);
				ApexClassFetcherUtils.apexClassMap.put(tempTestClassId, strLine);
			} else {
				LOG.warn("Duplicate entry found in manifest file for : " + strLine
						+ " . Skipping multiple execution/code coverage computation of this test class/source class");
				ApexClassFetcherUtils.duplicateApexClassMap.put(tempTestClassId, strLine);
			}

		} else {
			LOG.warn("The class " + strLine + " does not exist in the org.");
			if (!nonExistantApexClassEntries.contains(strLine)) {
				nonExistantApexClassEntries.add(strLine);
			}
		}
	}
}
