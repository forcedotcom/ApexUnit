
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
