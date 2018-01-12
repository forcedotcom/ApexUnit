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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;

import com.sforce.cd.apexUnit.arguments.CommandLineArgumentsTest;
import com.sforce.cd.apexUnit.client.codeCoverage.OAuthTokenGenerator;

@Ignore
public class OAuthTokenGeneratorTest {
	private final static Logger LOG = LoggerFactory.getLogger(OAuthTokenGeneratorTest.class);

	@BeforeTest
	public void setup() {
		new CommandLineArgumentsTest().setup();
	}

	/*@Test
	public void getOrgToken() {
		String orgToken = "";
		// generate/get the orgToken and test if the token has been generated
		orgToken = OAuthTokenGenerator.getOrgToken();
		LOG.debug(orgToken + "  -->  This is the orgToken");
		Assert.assertNotEquals(orgToken, null);
		Assert.assertTrue(StringUtils.isNotEmpty(orgToken));
	}*/
}
