/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * @author adarsh.ramakrishna@salesforce.com
 */
package com.sforce.cd.apexUnit.client;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.soap.partner.PartnerConnection;

@Ignore
public class ApexClassFetcherUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(ApexClassFetcherUtilsTest.class);
	ConnectionHandler connHandler = null;
	PartnerConnection conn = null;
	String soql = null;
	HashMap<String, String> localMap = new HashMap<String, String>();

	@BeforeTest
	public void setup() {
		new CommandLineArgumentsTest().setup();
		connHandler = ConnectionHandler.getConnectionHandlerInstance();
		//conn = connHandler.getConnection();
	}

	/*@Test
	public void constructTestClassesArrayTest() {
		String[] testClasses = ApexClassFetcherUtils.constructTestClassesArray(conn);
		if (testClasses != null) {
			Assert.assertTrue(testClasses.length > 0 || ApexClassFetcherUtils.apexClassMap.size() > 0);
		}
	}

	@Test
	public void fetchApexClassesFromManifestFilesTest() {
		String[] testClasses = ApexClassFetcherUtils
				.fetchApexClassesFromManifestFiles(CommandLineArguments.getTestManifestFiles(), true);
		if (testClasses != null && ApexClassFetcherUtils.apexClassMap != null
				&& ApexClassFetcherUtils.apexClassMap.size() != testClasses.length) {
			Assert.assertTrue(testClasses.length > 0 || ApexClassFetcherUtils.apexClassMap.size() > 0);
		}
	}

	@Test
	public void fetchApexClassesBasedOnPrefixTest() {
		String[] testClasses = ApexClassFetcherUtils.fetchApexClassesBasedOnMultipleRegexes(conn, null,
				CommandLineArguments.getTestRegex(), true);
		if (testClasses != null) {
			Assert.assertTrue(testClasses.length > 0 || ApexClassFetcherUtils.apexClassMap.size() > 0);
		}
	}

	@Test(priority = 1)
	public void constructTestClassArrayUsingWSC() {
		soql = QueryConstructor.generateQueryToFetchApexClassesBasedOnRegex(null, CommandLineArguments.getTestRegex());
		String[] testClasses = ApexClassFetcherUtils.constructClassIdArrayUsingWSC(conn, soql);
		logFilteredTestClasses(testClasses);
		if (testClasses != null) {
			Assert.assertTrue(testClasses.length > 0 || ApexClassFetcherUtils.apexClassMap.size() > 0);
		}
	}

	@Test(priority = 2)
	public void fetchApexClassIdFromNameTest() {
		String className = null;
		String expectedTestClassId = null;
		// pass empty string so that random classes gets picked
		soql = QueryConstructor.generateQueryToFetchApexClassesBasedOnRegex(null, "*");
		// limit the result to 1 . Thats all we need to test the method
		// fetchApexClassIdFromName
		soql += " limit 1";
		// the below call populates ApexClassFetcher.apexClassMap
		String[] testClasses = ApexClassFetcherUtils.constructClassIdArrayUsingWSC(conn, soql);
		if (testClasses != null && ApexClassFetcherUtils.apexClassMap != null) {
			for (String testClass : testClasses) {
				className = ApexClassFetcherUtils.apexClassMap.get(testClass);
				expectedTestClassId = testClass;
			}
		}
		soql = QueryConstructor.generateQueryToFetchApexClass(null, className);
		String testClassId = ApexClassFetcherUtils.fetchAndAddToMapApexClassIdBasedOnName(conn, soql);
		Assert.assertEquals(expectedTestClassId, testClassId);
	}

	@Test(priority = 2)
	public void fetchApexClassNameFromIdTest() {
		String classId = null;
		String expectedClassName = null;
		// pass empty string so that random classes gets picked
		soql = QueryConstructor.generateQueryToFetchApexClassesBasedOnRegex(null,"*");
		// limit the result to 1 . Thats all we need to test the method
		// fetchApexClassIdFromName
		soql += " limit 1";
		// the below call populates ApexClassFetcher.apexClassMap
		String[] testClasses = ApexClassFetcherUtils.constructClassIdArrayUsingWSC(conn, soql);
		if (testClasses != null && ApexClassFetcherUtils.apexClassMap != null) {
			for (String testClassId : testClasses) {
				expectedClassName = ApexClassFetcherUtils.apexClassMap.get(testClassId);
				classId = testClassId;
			}
		}
		if (classId != null) {
			HashMap<String, String> apexClassInfoMap = ApexClassFetcherUtils.fetchApexClassInfoFromId(conn, classId);
			String className = apexClassInfoMap.get("Name");
			Assert.assertEquals(expectedClassName, className);
		}
	}

	private void logFilteredTestClasses(String[] testClasses) {
		LOG.debug("Fetched apex classes from the test: ");
		for (int i = 0; i < testClasses.length; i++) {
			LOG.debug(testClasses[i]);
		}
	}*/
}
