/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
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
