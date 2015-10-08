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
 * This class represents the Bean object for test execution results for a given Apex test class
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.report;

/*
 * ApexReportBean stores the results of execution of Apex test classes
 */
public class ApexReportBean {

	private String Outcome;
	private String ApexClassName;
	private String ApexClassId;
	private String MethodName;
	private String Message;
	private String StackTrace;
	private Integer passedTestsCount;
	private Integer failedTestsCount;
	private long timeElapsed;

	public String getOutcome() {
		return Outcome;
	}

	public void setOutcome(String outcome) {
		Outcome = outcome;
	}

	public String getApexClassName() {
		return ApexClassName;
	}

	public void setApexClassName(String apexClassName) {
		ApexClassName = apexClassName;
	}

	public String getApexClassId() {
		return ApexClassId;
	}

	public void setApexClassId(String apexClassId) {
		ApexClassId = apexClassId;
	}

	public String getMethodName() {
		return MethodName;
	}

	public void setMethodName(String methodName) {
		MethodName = methodName;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getStackTrace() {
		return StackTrace;
	}

	public void setStackTrace(String stackTrace) {
		StackTrace = stackTrace;
	}

	public Integer getPassedTestsCount() {
		return passedTestsCount;
	}

	public void setPassedTestsCount(Integer passedTestsCount) {
		this.passedTestsCount = passedTestsCount;
	}

	public Integer getFailedTestsCount() {
		return failedTestsCount;
	}

	public void setFailedTestsCount(Integer failedTestsCount) {
		this.failedTestsCount = failedTestsCount;
	}

	public long getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

}
