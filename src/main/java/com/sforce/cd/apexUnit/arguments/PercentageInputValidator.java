/* Copyright (c) 2015, salesforce.com, inc.
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
