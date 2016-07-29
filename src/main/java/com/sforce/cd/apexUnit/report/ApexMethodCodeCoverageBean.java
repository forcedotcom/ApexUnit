/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class for computing code coverage metrics for each method in a given source class
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.report;

public class ApexMethodCodeCoverageBean {
	private int coveredLines = 0;
	private int unCoveredLines = 0;
	private String apexTestClassID;
	private String apexClassorTriggerId;

	public int getCoveredLines() {
		return coveredLines;
	}

	public void setCoveredLines(int coveredLines) {
		this.coveredLines = coveredLines;
	}

	public int getUnCoveredLines() {
		return unCoveredLines;
	}

	public void setUnCoveredLines(int unCoveredLines) {
		this.unCoveredLines = unCoveredLines;
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

}
