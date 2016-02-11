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
 * @author adarsh.ramakrishna@salesforce.com
 */
package com.sforce.cd.apexUnit.client;

import java.util.HashMap;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;
import com.sforce.soap.partner.PartnerConnection;

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
		conn = connHandler.getConnection();
	}

	@Test
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
	}
}
