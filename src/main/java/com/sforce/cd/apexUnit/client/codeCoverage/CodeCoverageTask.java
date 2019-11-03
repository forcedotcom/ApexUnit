package com.sforce.cd.apexUnit.client.codeCoverage;

import java.util.concurrent.Callable;

import org.json.simple.JSONObject;
public class CodeCoverageTask implements Callable<JSONObject>{
	
	private final String relativeUrl;
	private final String soqlcc;
	private final String oauthTocken;
	
	public CodeCoverageTask(String relativeUrl, String soqlcc, String oauthTocken){
		this.relativeUrl = relativeUrl;
		this.soqlcc = soqlcc;
		this.oauthTocken = oauthTocken;
	}
	

	public JSONObject call() throws Exception {

		return WebServiceInvoker.doGet(relativeUrl, soqlcc, oauthTocken);
				
	}

}
