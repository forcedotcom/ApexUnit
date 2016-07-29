/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Validator class to validate if a given input is within the percentage range of 0 to 100%
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.sforce.cd.apexUnit.ApexUnitUtils;

public class PercentageInputValidator implements IParameterValidator {
	private static Logger LOG = LoggerFactory.getLogger(PercentageInputValidator.class);

	/*
	 * Validates if the given value lies in the range of 0 to 100. Used to
	 * validate the input value for parameters that expects percentage
	 * (non-Javadoc)
	 * 
	 * @see com.beust.jcommander.IParameterValidator#validate(java.lang.String,
	 * java.lang.String)
	 * 
	 * @param name - name of the parameter
	 * 
	 * @param value - value passed to the parameter
	 */
	public void validate(String name, String value) throws ParameterException {
		try {
			int n = Integer.parseInt(value);
			if (n < 0) {
				ApexUnitUtils.shutDownWithErrMsg("ParameterException: Input value for the Parameter " + name
						+ " should be positive integer (found " + value + ")");
			}
			if (n > 100) {
				ApexUnitUtils.shutDownWithErrMsg("ParameterException:Input value for the Parameter " + name
						+ " should be in the range of 0  to 100 (found " + value + ")");
			}
		} catch (NumberFormatException e) {
			LOG.debug("Value provided for the parameter " + name + " is out of range. "
					+ "The value must be in the range of 0-100");
			ApexUnitUtils.shutDownWithDebugLog(e,"NumberFormatException:Input value for the Parameter " + name
					+ " should be positive integer (found " + value + ")");
		}
	}

}
