/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Validator class to validate if the given value is a positive integer , greater than 1.
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 
package com.sforce.cd.apexUnit.arguments;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.sforce.cd.apexUnit.ApexUnitUtils;

public class PositiveIntegerValidator implements IParameterValidator {
	/*
	 * Validates if the given value is a positive integer , greater than 1. Used
	 * to validate the input value for the parameters that expects positive
	 * integers (non-Javadoc)
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
			if(value == null || value.isEmpty()){
				ApexUnitUtils.shutDownWithDebugLog(new NumberFormatException() , "Null/No value specified for "+ name );
			}
			int n = Integer.parseInt(value);
			if (n <= 0) {
				ApexUnitUtils.shutDownWithErrMsg("ParameterException: Input value for the Parameter " + name
						+ " should be positive integer (found " + value + ")");
			}
		} catch (NumberFormatException e) {
			ApexUnitUtils.shutDownWithDebugLog(e,"NumberFormatException:Input value for the Parameter " + name
					+ " should be positive integer (found " + value + ")");
		}
	}

}
