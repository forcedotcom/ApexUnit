/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
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
	 * over riding compareTo method of Comparable interface
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ApexClassCodeCoverageBean codeCoverageBean) {
		if (this.getCoveragePercentage() < codeCoverageBean.getCoveragePercentage()) {
			return -1;
		} else if (this.getCoveragePercentage() > codeCoverageBean.getCoveragePercentage()) {
			return 1;
		} else {
			return 0;
		}
	}
}
