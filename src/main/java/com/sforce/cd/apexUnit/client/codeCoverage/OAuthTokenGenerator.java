/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
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
		LOG.info("*****Response Map "+ responseMap);
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
