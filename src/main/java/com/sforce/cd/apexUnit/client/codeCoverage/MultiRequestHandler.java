package com.sforce.cd.apexUnit.client.codeCoverage;

import org.json.simple.JSONObject;

public class MultiRequestHandler {

	private JSONObject idJson;
	
	
	public boolean isClassAvailable(String relativeServiceURL, String soql){
		boolean result = false;
		JSONObject json =WebServiceInvoker.doGet(relativeServiceURL, soql, OAuthTokenGenerator.getOrgToken());
		if(  json != null){
			result = true;
			this.idJson = json;
		}
		
		return result;
	}
	
	public String reloadTest(String relativeServiceURL, String soql){
		String result = null;
		JSONObject json =WebServiceInvoker.doGet(relativeServiceURL, soql, OAuthTokenGenerator.getOrgToken());
		if(json != null){
			result ="OK";
		}
		return result;
	}

	public JSONObject getIdJson() {
		return idJson;
	}
}
