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
 * validator class to validate if the input to the url field matches the url regex pattern.
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.arguments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.sforce.cd.apexUnit.ApexUnitUtils;

public class URLValidator implements IParameterValidator {
	private static Logger LOG = LoggerFactory.getLogger(URLValidator.class);

	/*
	 * overridden method for implementing IParameterValidator (non-Javadoc)
	 * 
	 * @see com.beust.jcommander.IParameterValidator#validate(java.lang.String,
	 * java.lang.String) validates URL pattern
	 * 
	 * @param name - name of the parameter
	 * 
	 * @param value - value passed to the parameter
	 */
	public void validate(String name, String value) throws ParameterException {
		boolean validUrl = false;
		// regex to be matched for a url to be considered valid
		// supports both http and https regex patterns
		String regex = "\\b(https?)://[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]+.[-a-zA-Z]+";
		Pattern urlPattern = Pattern.compile(regex);
		Matcher matcher = urlPattern.matcher(value);
		validUrl = matcher.matches();
		if (validUrl == false) {
			LOG.debug("Invalid url provided for the parameter : " + name + ". Please follow the below regex format: "
					+ regex);
			ApexUnitUtils.shutDownWithErrMsg(
					"ParameterException:Parameter " + name + " should be a valid URL (found " + value + ")");
		}
	}

	/*
	 * Takes in the name and value of the url string and returns a String which
	 * can be used by Assert statements of corresponding test method
	 * 
	 * TODO: Have better mechanism in validateURL than using property file value
	 * for test assertion
	 * 
	 * @param name - name of the parameter
	 * 
	 * @param value - value passed to the parameter
	 */
	public String validateUrl(String name, String value) {
		try {
			validate(name, value);
		} catch (ParameterException paramEx) {
			ApexUnitUtils.shutDownWithDebugLog(paramEx, paramEx.getMessage());
		}
		return "validUrl";
	}

}
