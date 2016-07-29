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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.beust.jcommander.ParameterException;

public class PercentageInputValidatorTest {

	private static Logger LOG = LoggerFactory.getLogger(PercentageInputValidatorTest.class);

	// TODO rewrite test cases since we are halting the program in case of
	// ParameterException
	// @Test
	public void validateStringNegativeTest() {
		PercentageInputValidator percentInputValidator = new PercentageInputValidator();
		String name = "codeCoverageThreshold";
		String value = "abc";
		String referenceResult = "valid argument";
		String result = referenceResult;
		try {
			percentInputValidator.validate(name, value);
		} catch (ParameterException e) {
			result = e.getMessage();
		}
		LOG.debug("result:" + result);
		Assert.assertTrue(!result.equals(referenceResult));
	}

	// @Test
	public void validateIntUnderLimitNegativeTest() {
		PercentageInputValidator percentInputValidator = new PercentageInputValidator();
		String name = "codeCoverageThreshold";
		String value = "0.01";
		String referenceResult = "valid argument";
		String result = referenceResult;
		try {
			percentInputValidator.validate(name, value);
		} catch (ParameterException e) {
			result = e.getMessage();
		}
		LOG.debug("result:" + result);
		Assert.assertTrue(!result.equals(referenceResult));
	}

	// @Test
	public void validateIntOverLimitNegativeTest() {
		PercentageInputValidator percentInputValidator = new PercentageInputValidator();
		String name = "codeCoverageThreshold";
		String value = "1001";
		String referenceResult = "valid argument";
		String result = referenceResult;
		try {
			percentInputValidator.validate(name, value);
		} catch (ParameterException e) {
			result = e.getMessage();
		}
		LOG.debug("result:" + result);
		Assert.assertTrue(!result.equals(referenceResult));
	}

	@Test
	public void validatePositiveTest() {
		PercentageInputValidator percentInputValidator = new PercentageInputValidator();
		String name = "codeCoverageThreshold";
		String value = "80";
		String referenceResult = "valid argument";
		String result = referenceResult;
		try {
			percentInputValidator.validate(name, value);
		} catch (ParameterException e) {
			result = e.getMessage();
		}
		LOG.debug("result:" + result);
		Assert.assertTrue(result.equals(referenceResult));
	}
}
