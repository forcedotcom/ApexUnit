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
