/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * @author adarsh.ramakrishna@salesforce.com
 */
package com.sforce.cd.apexUnit.fileReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.fileReader.ApexManifestFileReader;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class ApexManifestFileReaderTest {
	private final static Logger LOG = LoggerFactory.getLogger(ApexManifestFileReaderTest.class);

	private String workingDir = System.getProperty("user.dir") + System.getProperty("file.separator") + "MockTestFiles";
	String fileName = "ManifestFile.txt";
	String manifestFiles = "ManifestFile1.txt,ManifestFile2.txt,ManifestFile3.txt";
	ApexClassFetcherUtils apexClassFetcher = new ApexClassFetcherUtils();
	int limit = 10;
	String[] apexClasses = new String[limit];
	ApexManifestFileReader apexManifestFileReader = new ApexManifestFileReader(true);
	String testClassesAsString = null;

	@BeforeTest
	public void setup() throws IOException, URISyntaxException {
		String soql = "";
		// writing to a file in the current working directory
		/**LOG.info("Working directory : " + workingDir);
		PartnerConnection connection = ConnectionHandler.getConnectionHandlerInstance().getConnection();
		File dir = new File(workingDir);
		dir.mkdirs();
		File file = new File(dir, fileName);
		soql = "SELECT Id , Name FROM ApexClass LIMIT 10";
		QueryResult queryResult = null;
		try {
			queryResult = connection.query(soql);
			LOG.info("soql: " + soql);
		} catch (ConnectionException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, "Connection Exception encountered when trying to query : "+ soql + " \n The connection exception description says : " + e.getMessage());
		}
		if (queryResult != null) {
			testClassesAsString = fetchApexClassesAsString(queryResult);
			LOG.info("testClassesAsString: " + testClassesAsString);
		}

		try {

			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(testClassesAsString);
			bw.close();
			String cvsSplitBy = ",";
			String[] manifestFilesAsArray = manifestFiles.split(cvsSplitBy);
			for (String manifestFile : manifestFilesAsArray) {
				LOG.info("Creating Manifest file : " + manifestFile);
				File tmpFile = new File(dir, manifestFile);
				FileWriter fileWriter = new FileWriter(tmpFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(testClassesAsString);
				bufferedWriter.close();
			}
		} catch (IOException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, "IO Exception caught \n");
		}*/

	}

	private String fetchApexClassesAsString(QueryResult queryResult) {
		String apexClassesAsString = null;
		int arrayLength = 0;
		String apexClassId = "";
		String apexClassName = "";

		if (queryResult.getDone()) {
			SObject[] sObjects = queryResult.getRecords();
			if (sObjects != null) {
				LOG.debug("Fetched the classes:");
				for (SObject sobject : sObjects) {
					apexClassName = sobject.getField("Name").toString();
					apexClassesAsString += apexClassName;
					apexClassesAsString += "\n";
					apexClassId = sobject.getField("Id").toString();
					apexClasses[arrayLength++] = apexClassId;
					ApexClassFetcherUtils.apexClassMap.put(apexClassId, apexClassName);
				}
				return apexClassesAsString;
			}
		}
		return null;
	}

	// @Test
	/*
	 * public void readInputStream() throws FileNotFoundException { String
	 * filePath = workingDir +"/" + fileName; File file = new File(filePath);
	 * InputStream inStr = new FileInputStream(file); String[] testClasses =
	 * null; try { testClasses = apexManifestFileReader.readInputStream(inStr);
	 * } catch (IOException e) { LOG.error(
	 * "IOException while trying to read the manifest file " + file); if
	 * (LOG.isDebugEnabled()) { e.printStackTrace(); } } StringBuffer
	 * testClassesStrBuffer = new StringBuffer(); for(String testClass :
	 * testClasses){ testClassesStrBuffer.append(testClass);
	 * testClassesStrBuffer.append("\n"); }
	 * ApexClassFetcherUtils.logTheFetchedApexClasses(testClasses);
	 * Assert.assertTrue(ApexClassFetcherUtils.apexClassMap.containsKey(
	 * apexClasses[0]));
	 * 
	 * }
	 * 
	 * // @Test public void getTestClasses() { LOG.info(
	 * "testing getTestClasses() " + manifestFiles); String[] apexClassesStrArr
	 * = apexManifestFileReader.getTestClasses(manifestFiles);
	 * if(apexClassesStrArr != null && apexClassesStrArr.length > 0) { LOG.info(
	 * "apexClassesStrArr.length: " + apexClassesStrArr.length);
	 * Assert.assertTrue(apexClassesStrArr.length > 0); } }
	 */

	@AfterTest
	public void cleanUpTestFiles() {
		/*String testFilesDirPath = System.getProperty("user.dir") + System.getProperty("file.separator")
				+ "MockTestFiles";
		File testFilesDir = new File(testFilesDirPath);
		if (testFilesDir.exists()) {
			try {
				FileUtils.deleteDirectory(testFilesDir);
			} catch (IOException e) {
				ApexUnitUtils.shutDownWithDebugLog(e, "IO Exception encountered while deleting the test files directory");
			}
			LOG.info("Test files directory deleted");
		} else {
			LOG.info("Test files directory does not exist; hence not deleted");
		}*/
	}
}
