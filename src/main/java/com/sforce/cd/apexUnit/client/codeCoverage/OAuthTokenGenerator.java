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
 * Class to generate OAuth token for the given org and login credentials
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */
package com.sforce.cd.apexUnit.client.codeCoverage;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.cd.apexUnit.ApexUnitUtils;

/*
 * Generates OAuth Token
 * Usage: the token can be used to invoke web services 
 * and leverage features provided by force.com platform like Tooling APIs 
 */
public class OAuthTokenGenerator {
	private static Logger LOG = LoggerFactory.getLogger(OAuthTokenGenerator.class);
	private static String orgToken = "";

	/*
	 * returns oauth org token
	 */
	public static String getOrgToken() {
		if (orgToken.equals("")) {
			orgToken = doPostAndGetOrgToken();
		}
		return orgToken;
	}

	/*
	 * sends out post request and fetches the org token for the org
	 */
	private static String doPostAndGetOrgToken() {
		String oAuthTokenServiceUrl = "/services/oauth2/token";
		String orgToken = null;
		WebServiceInvoker webServiceInvoker = new WebServiceInvoker();
		// get response map using a post web service call to the service url
		HashMap<String, String> responseMap = webServiceInvoker.doPost(oAuthTokenServiceUrl);
		if (responseMap != null && responseMap.containsKey("access_token")) {
			orgToken = responseMap.get("access_token");
			LOG.debug("Org token : " + orgToken);
		} else {
			ApexUnitUtils.shutDownWithErrMsg(
					"Unable to get access_token for OAuth authentication and hence unable to establish connection with the web services."
							+ "Terminating the process..");
		}
		// TODO setting session as cookie -- need to look into this later so
		// that the session can be handled more efficiently
		// HttpServletResponse httpResponse = (HttpServletResponse)responseBody;
		// Cookie session = new Cookie(ACCESS_TOKEN, result);
		// session.setMaxAge(-1); //cookie not persistent, destroyed on browser
		// exit
		// httpResponse.addCookie(session);

		return orgToken;
	}
}
