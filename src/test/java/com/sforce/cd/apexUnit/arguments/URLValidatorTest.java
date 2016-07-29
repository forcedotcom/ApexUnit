/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.arguments;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class URLValidatorTest {
	// TODO rewrite test cases since we are halting the program in case of
	// ParameterException
	// @Test
	public void validateUrlNegativeTest() {
		URLValidator urlValidator = new URLValidator();
		String name = "testInvalidName";
		String value = "www.Invalid url";
		String result = urlValidator.validateUrl(name, value);
		Assert.assertEquals("Parameter " + name + " should be a valid URL (found " + value + ")", result);
	}

	// @Test
	public void validateUrlEndingWithDotTest() {
		URLValidator urlValidator = new URLValidator();
		String name = "testDotEndingInvalidName";
		String value = "https://www.salesforce.";
		String result = urlValidator.validateUrl(name, value);
		Assert.assertEquals("Parameter " + name + " should be a valid URL (found " + value + ")", result);
	}

	@Test
	public void validateUrlPositiveTest() {
		URLValidator urlValidator = new URLValidator();
		String name = "testName_HTTPS";
		String value = "https://www.salesforce.com";
		String result = urlValidator.validateUrl(name, value);
		Assert.assertEquals("validUrl", result);
	}

	@Test
	public void validateHTTPUrlPositiveTest() {
		URLValidator urlValidator = new URLValidator();
		String name = "testName_HTTP";
		String value = "http://login.salesforce.com";
		String result = urlValidator.validateUrl(name, value);
		Assert.assertEquals("validUrl", result);
	}
}
