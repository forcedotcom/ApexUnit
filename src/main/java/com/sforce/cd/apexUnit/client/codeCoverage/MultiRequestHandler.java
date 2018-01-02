package com.sforce.cd.apexUnit.client.codeCoverage;

import org.json.simple.JSONObject;

public class MultiRequestHandler {

	private JSONObject idJson;
	
	
	public boolean isClassAvailable(String relativeServiceURL, String soql){
		boolean result = false;
		String org_tocket = OAuthTokenGenerator.getOrgToken();
		JSONObject json =WebServiceInvoker.doGet(relativeServiceURL, soql, org_tocket);
		if(  json != null){
			result = true;
			this.idJson = json;
		}
		
		return result;
	}
	
	public String reloadTest(String relativeServiceURL, String soql){
		String result = null;
		String org_tocket = OAuthTokenGenerator.getOrgToken();
		JSONObject json =WebServiceInvoker.doGet(relativeServiceURL, soql, org_tocket);
		if(json != null){
			result ="OK";
		}
		return result;
	}

	public JSONObject getIdJson() {
		return idJson;
	}
	
//	public static void main(String []a){
//		System.out.println("org_tocket "+ OAuthTokenGenerator.getOrgToken());
//		System.out.println("");
//		
//	}
}
