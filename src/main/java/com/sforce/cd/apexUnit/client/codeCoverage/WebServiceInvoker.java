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
 * Class to invoke web services calls: get and post methods for the REST APIs using OAUTH
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */

package com.sforce.cd.apexUnit.client.codeCoverage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;

/*
 * WebServiceInvoker provides interfaces for get and post methods for the REST APIs using OAUTH
 */
public class WebServiceInvoker {
	private static Logger LOG = LoggerFactory.getLogger(WebServiceInvoker.class);

	/*
	 * Utility to perform HTTP post operation on the orgUrl with the specific
	 * sub-url as mentioned in the relativeServiceURL
	 * 
	 * @param relativeServiceURL - relative service url w.r.t org url for firing
	 * post request
	 * 
	 * @return : hashmap with key-value pairs of response from the post query
	 */
	public HashMap<String, String> doPost(String relativeServiceURL) {

		PostMethod post = null;
		HttpClient httpclient = new HttpClient();
		String requestString = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();

		try {
			// the client id and secret is applicable across all dev orgs
			requestString = "grant_type=password&client_id=" + CommandLineArguments.getClientId() + "&client_secret="
					+ CommandLineArguments.getClientSecret() + "&username=" + CommandLineArguments.getUsername()
					+ "&password=" + CommandLineArguments.getPassword();
			String authorizationServerURL = CommandLineArguments.getOrgUrl() + relativeServiceURL;

			httpclient.getParams().setSoTimeout(0);
			post = new PostMethod(authorizationServerURL);
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addRequestHeader("X-PrettyPrint", "1");
			post.setRequestEntity(new StringRequestEntity(requestString, "application/x-www-form-urlencoded", "UTF-8"));
			httpclient.executeMethod(post);

			Gson json = new Gson();
			// obtain the result map from the response body and get the access
			// token
			responseMap = json.fromJson(post.getResponseBodyAsString(), new TypeToken<HashMap<String, String>>() {
			}.getType());

		} catch (Exception ex) {

			ApexUnitUtils
			.shutDownWithDebugLog(ex, "Exception during post method: " + ex);
			if(LOG.isDebugEnabled()) {
				ex.printStackTrace();
			}
		} finally {
			post.releaseConnection();
		}

		return responseMap;

	}

	public static JSONObject doGet(String relativeServiceURL, String soql, String accessToken) {
		if (soql != null && !soql.equals("")) {
			try {
				relativeServiceURL += "/query/?q=" + URLEncoder.encode(soql, "UTF-8");
			} catch (UnsupportedEncodingException e) {

				ApexUnitUtils
				.shutDownWithDebugLog(e, "Error encountered while trying to encode the query string using UTF-8 format. The error says: "+ e.getMessage());
			}
		}
		return doGet(relativeServiceURL, accessToken);
	}

	/*
	 * method to perform get operation using the access token for the org and
	 * return the json response
	 * 
	 * @param relativeServiceURL - relative service url w.r.t org url for firing
	 * post request
	 * 
	 * @param accessToken : access token for the org(generated in the post
	 * method)
	 * 
	 * @return : json response from the get request
	 */
	public static JSONObject doGet(String relativeServiceURL, String accessToken) {

		LOG.debug("relativeServiceURL in doGet method:" + relativeServiceURL);
		HttpClient httpclient = new HttpClient();
		GetMethod get = null;

		String authorizationServerURL = CommandLineArguments.getOrgUrl() + relativeServiceURL;
		get = new GetMethod(authorizationServerURL);
		get.addRequestHeader("Content-Type", "application/json");
		get.setRequestHeader("Authorization", "Bearer " + accessToken);
		LOG.debug("Start GET operation for the url..." + authorizationServerURL);
		InputStream instream = null;
		try {
			instream = executeHTTPMethod(httpclient, get, authorizationServerURL);
			LOG.debug("done with get operation");

			JSONObject json = (JSONObject) JSONValue.parse(new InputStreamReader(instream));
			LOG.debug("is json null? :" + json == null ? "true" : "false");
			if (json != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("ToolingApi.get response: " + json.toString());
					Set<String> keys = castSet(String.class, json.keySet());
					Iterator<String> jsonKeyIter = keys.iterator();
					LOG.debug("Response for the GET method: ");
					while (jsonKeyIter.hasNext()) {
						String key = jsonKeyIter.next();
						LOG.debug("key : " + key + ". Value :  " + json.get(key) + "\n");
						// TODO if query results are too large, only 1st batch
						// of results
						// are returned. Need to use the identifier in an
						// additional query
						// to retrieve rest of the next batch of results

						if (key.equals("nextRecordsUrl")) {
							// fire query to the value for this key
							// doGet((String) json.get(key), accessToken);
							try {
								authorizationServerURL = CommandLineArguments.getOrgUrl() + (String) json.get(key);
								get.setURI(new URI(authorizationServerURL, false));
								instream = executeHTTPMethod(httpclient, get, authorizationServerURL);
								JSONObject newJson = (JSONObject) JSONValue.parse(new InputStreamReader(instream));
								if (newJson != null) {
									Set<String> newKeys = castSet(String.class, json.keySet());
									Iterator<String> newJsonKeyIter = newKeys.iterator();
									while (newJsonKeyIter.hasNext()) {
										String newKey = newJsonKeyIter.next();
										json.put(newKey, newJson.get(newKey));
										LOG.debug("newkey : " + newKey + ". NewValue :  " +  newJson.get(newKey) + "\n");
									}
								}
							
								} catch (URIException e) {
									ApexUnitUtils.shutDownWithDebugLog(e, "URI exception while fetching subsequent batch of result");
								} 
							}
							
						}
					}
				}
			return json;
		} finally {
			get.releaseConnection();
			try {
				if (instream != null) {
					instream.close();

				} 
			} catch (IOException e) {
				ApexUnitUtils
				.shutDownWithDebugLog(e, "Encountered IO exception when closing the stream after reading response from the get method. The error says: "+ e.getMessage());
			}
		}
	}

	/**
	 * 
	 * execute the HTTP Get method and return response as Input stream
	 * 
	 * @param httpclient
	 *            HTTPClient
	 * @param get
	 *            GetMethod
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */

	private static InputStream executeHTTPMethod(HttpClient httpclient, GetMethod get,
			String authorizationServerURL) {
		try {
			httpclient.executeMethod(get);
		} catch (HttpException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, "Encountered HTTP exception when executing get method using OAuth authentication for the url "+ authorizationServerURL 
					+". The error says: "+ e.getMessage());
		} catch (IOException e) {
			ApexUnitUtils
			.shutDownWithDebugLog(e, "Encountered IO exception when executing get method using OAuth authentication for the url "+ authorizationServerURL 
					+". The error says: "+ e.getMessage());
		}
		LOG.info("Status code : "
				+ get.getStatusCode() + "   Status message from the get request:" + get.getStatusText() + " Reason phrase: "+get.getStatusLine().getReasonPhrase());
		
		InputStream instream = null;
		try {
			// don't delete the below line --i.e. getting response body as
			// string. Getting response as stream fails upon deleting the below
			// line! strange!
			String respStr;
			respStr = get.getResponseBodyAsString();
			instream = get.getResponseBodyAsStream();
		} catch (IOException e) {

			ApexUnitUtils
			.shutDownWithDebugLog(e, "Encountered IO exception when obtaining response body for the get method. The error says: "+ e.getMessage());
		}
		return instream;
	}

	/*
	 * Method to cast a collection of objects to a set of given type
	 * 
	 * @param clazz - class type of the collection
	 * 
	 * @param c - collection set
	 * 
	 * @return set - A set returned that is of the class 'clazz'
	 */
	public static <T> Set<T> castSet(Class<? extends T> clazz, Collection<?> c) {
		Set<T> set = new HashSet<T>();
		for (Object o : c)
			set.add(clazz.cast(o));
		return set;
	}
}
