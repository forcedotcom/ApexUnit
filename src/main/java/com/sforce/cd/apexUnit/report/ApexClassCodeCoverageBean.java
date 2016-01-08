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
 * This class represents the Bean object for code coverage computation for source classes
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.report;

import java.util.List;

public class ApexClassCodeCoverageBean implements Comparable<ApexClassCodeCoverageBean> {

	private String apexTestClassID;
	private String apexClassorTriggerId;
	private String apexClassName;
	private int numLinesCovered = 0;
	private int numLinesUncovered = 0;
	private ApexMethodCodeCoverageBean[] testMethodNames;
	private String apiVersion;
	private String lengthWithoutComments;
	private List<Long> coveredLinesList;
	private List<Long> uncoveredLinesList;

	public List<Long> getCoveredLinesList() {
		return coveredLinesList;
	}

	public void setCoveredLinesList(List<Long> coveredLinesList) {
		this.coveredLinesList = coveredLinesList;
	}

	public List<Long> getUncoveredLinesList() {
		return uncoveredLinesList;
	}

	public void setUncoveredLinesList(List<Long> uncoveredLinesList) {
		this.uncoveredLinesList = uncoveredLinesList;
	}

	public String getApexTestClassID() {
		return apexTestClassID;
	}

	public void setApexTestClassID(String apexTestClassID) {
		this.apexTestClassID = apexTestClassID;
	}

	public String getApexClassorTriggerId() {
		return apexClassorTriggerId;
	}

	public void setApexClassorTriggerId(String apexClassorTriggerId) {
		this.apexClassorTriggerId = apexClassorTriggerId;
	}

	public String getApexClassName() {
		return apexClassName;
	}

	public void setApexClassName(String apexClassName) {
		this.apexClassName = apexClassName;
	}

	public int getNumLinesCovered() {
		return numLinesCovered;
	}

	public void setNumLinesCovered(int numLinesCovered) {
		this.numLinesCovered = numLinesCovered;
	}

	public int getNumLinesUncovered() {
		return numLinesUncovered;
	}

	public void setNumLinesUncovered(int numLinesUncovered) {
		this.numLinesUncovered = numLinesUncovered;
	}

	public ApexMethodCodeCoverageBean[] getTestMethodNames() {
		return testMethodNames;
	}

	public void setTestMethodNames(ApexMethodCodeCoverageBean[] testMethodNames) {
		this.testMethodNames = testMethodNames;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getLengthWithoutComments() {
		return lengthWithoutComments;
	}

	public void setLengthWithoutComments(String lengthWithoutComments) {
		this.lengthWithoutComments = lengthWithoutComments;
	}

	public double getCoveragePercentage() {
		double totalLines = numLinesCovered + numLinesUncovered;
		if (totalLines > 0) {
			return (numLinesCovered / (totalLines)) * 100.0;
		} else {
			return 100.0;
		}
	}

	/*
	 * over riding compareTo method of Comparable imterface
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ApexClassCodeCoverageBean codeCoverageBean) {
		if (this.getCoveragePercentage() < codeCoverageBean.getCoveragePercentage()) return -1;
		if (this.getCoveragePercentage() > codeCoverageBean.getCoveragePercentage()) return 1;
		return 0;
	}
}
