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
		String cvsSplitBy = ",";
		String[] manifestFiles = files.split(cvsSplitBy);

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
				ApexUnitUtils
						.shutDownWithDebugLog(e, "IOException while trying to read the manifest file "
								+ file);
			}
			if (apexClassesStrArrForManifest != null) {
				apexClassesStrArr = (String[]) ArrayUtils.addAll(apexClassesStrArr, apexClassesStrArrForManifest);
			} else {
				LOG.warn("Given manifest file " + file
						+ " contains invalid/no test classes. 0 Apex test class id's returned");
			}
		}
		Set<String> uniqueSetOfClasses = new HashSet<String>();
		ArrayList<String> duplicateList = new ArrayList<String>();
		if (apexClassesStrArr != null && apexClassesStrArr.length > 0) {
			for (int i = 0; i < apexClassesStrArr.length; i++) {
				if (!uniqueSetOfClasses.add(apexClassesStrArr[i])) {
					LOG.warn("Duplicate entry found across manifest files for : "
							+ ApexClassFetcherUtils.apexClassMap.get(apexClassesStrArr[i])
							+ " . Skipping multiple execution/code coverage computation of this test class/source class");
					duplicateList.add(apexClassesStrArr[i]);
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
	
	private void insertIntoTestClassesArray(String strLine, ArrayList<String> testClassList){
		String tempTestClassId = null;
		Map<String, String> namespaceAndName = new HashMap<String, String>();
		namespaceAndName.put("name",strLine);				
		
		String soql = QueryConstructor.generateQueryToFetchApexClass(namespaceAndName.get("namespace"), 
				namespaceAndName.get("name"));
		// query using WSC
		tempTestClassId = ApexClassFetcherUtils.fetchAndAddToMapApexClassIdBasedOnName(
				ConnectionHandler.getConnectionHandlerInstance().getConnection(), soql);
		LOG.debug("tempTestClassId: " + tempTestClassId);
		
		if (tempTestClassId == null && includeTriggers) {
			// look if the given class name is a trigger if its not ApexClass
			String soqlForTrigger = QueryConstructor.generateQueryToFetchApexTrigger(namespaceAndName.get("namespace"), 
					namespaceAndName.get("name"));
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
