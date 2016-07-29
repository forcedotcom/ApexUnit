
 /* 
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *

 */

/*
 * ApexUnitUtils class consists of utility methods used by the ApexUnit tool like the shutdown logic
 *  
 * @author adarsh.ramakrishna@salesforce.com
 */ 
package com.sforce.cd.apexUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApexUnitUtils {


	private static Logger LOG = LoggerFactory
			.getLogger(ApexUnitUtils.class);
	
	
	/**
	 * Shutdown framework with an error message and print stack trace
	 * @param ex Exception thrown by the application
	 */
	public static void shutDownWithDebugLog(Exception ex, String errorMsg){
		if(LOG.isDebugEnabled()) {
			ex.printStackTrace();
		}
		shutDownWithErrMsg(errorMsg);
	}
	
	/**
	 * Shutdown framework with an error message
	 * 
	 * @param errorMsg
	 *            error message to log
	 */

	public static void shutDownWithErrMsg(String errorMsg){
		if(errorMsg != null) {
			LOG.error(errorMsg);
		}
		LOG.info("Shutting down ApexUnit");
		Thread.dumpStack();
		System.exit(-1);
	}

}
