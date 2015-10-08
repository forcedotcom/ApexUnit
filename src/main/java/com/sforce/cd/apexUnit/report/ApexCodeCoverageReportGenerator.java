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
 * Class for generating code coverage reportfor a given ApexUnit run
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.fileReader.ApexManifestFileReader;
import com.sforce.cd.apexUnit.client.testEngine.TestStatusPollerAndResultHandler;
import com.sforce.cd.apexUnit.client.utils.ApexClassFetcherUtils;

public class ApexCodeCoverageReportGenerator {

	public static void generateHTMLReport(ApexReportBean[] reportBeans,
			ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans) {
		// Preparing the table:
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		// String styleTagProperties =
		// "table { font-size: 18px; background-color: blue; color: orange;
		// text-align: center; }"
		// +
		// "body { font-family: \"Times New Roman\"; text-align: center;
		// font-size: 20px;}";
		// appendTag(htmlBuilder, "style", styleTagProperties, "");

		// Print a summary of the coverage for the team's apex classes and
		// triggers
		StringBuilder styleBuilder = new StringBuilder();
		// define all styles for the html tags here
		String styleBuilderString = "body {background-color:white;} " + "h1   {color:blue; font-size:300%}"
				+ "summary    {color:black; font-size:125%;}" + "header    {color:blue; font-size:200%;}"
				+ "th   {color:blue; font-size:125%; background-color:lightgrey;}";
		appendTag(styleBuilder, "style", styleBuilderString);
		htmlBuilder.append(styleBuilder);
		StringBuilder summaryHeader = new StringBuilder();

		String summaryHeaderString = "<b>ApexUnit Report</b>\n";
		appendTag(summaryHeader, "h1", "align = 'center'; font-size: 25px; ", summaryHeaderString);
		appendLineSpaces(summaryHeader, 2);
		htmlBuilder.append(summaryHeader);
		
		StringBuilder codeCoverageSummary = new StringBuilder();
		
		appendTag(codeCoverageSummary, "header", "Code Coverage Summary: *");
		appendLineSpaces(codeCoverageSummary, 2);
		String teamCodeCoverageSummaryString = " Team code coverage: "
				+ String.format("%.2f", ApexUnitCodeCoverageResults.teamCodeCoverage) + "%"
				+ "  [The customized team code coverage threshold was: "
				+ CommandLineArguments.getTeamCodeCoverageThreshold() + "%]";
		String orgWideCodeCoverageSummaryString = "<br/> Org wide code coverage: "
				+ String.format("%.2f", ApexUnitCodeCoverageResults.orgWideCodeCoverage) + "%"
				+ "  [The customized org wide code coverage threshold was: "
				+ CommandLineArguments.getOrgWideCodeCoverageThreshold() + "%]";
		if (ApexUnitCodeCoverageResults.teamCodeCoverage < CommandLineArguments.getTeamCodeCoverageThreshold()) {
			appendTag(codeCoverageSummary, "summary", "style=\"color:crimson\"", teamCodeCoverageSummaryString);
		} else {
			appendTag(codeCoverageSummary, "summary", teamCodeCoverageSummaryString);
		}
		if (ApexUnitCodeCoverageResults.orgWideCodeCoverage < CommandLineArguments.getOrgWideCodeCoverageThreshold()) {
			appendTag(codeCoverageSummary, "summary", "style=\"color:crimson\"", orgWideCodeCoverageSummaryString);
		} else {
			appendTag(codeCoverageSummary, "summary", orgWideCodeCoverageSummaryString);
		}
		appendLineSpaces(codeCoverageSummary, 2);

		htmlBuilder.append(codeCoverageSummary);

		StringBuilder apexTestExecutionSummary = new StringBuilder();
		appendTag(apexTestExecutionSummary, "header", "Test Execution Summary: ");
		appendLineSpaces(apexTestExecutionSummary, 2);
		int failureTestMethodsCount = 0;
		if (TestStatusPollerAndResultHandler.testFailures) {
			if (TestStatusPollerAndResultHandler.failedTestMethods != null
					&& !TestStatusPollerAndResultHandler.failedTestMethods.isEmpty())
				failureTestMethodsCount = TestStatusPollerAndResultHandler.failedTestMethods.size();
		}
		StringBuffer apexTestExecutionSummaryString = new StringBuffer(
				" Total test classes executed: " + TestStatusPollerAndResultHandler.totalTestClasses);
		if (TestStatusPollerAndResultHandler.totalTestClassesAborted > 0) {
			apexTestExecutionSummaryString.append("<br/>Total Apex test classes aborted: "
					+ TestStatusPollerAndResultHandler.totalTestClassesAborted);
		}
		apexTestExecutionSummaryString.append(
				"<br/> Total test methods executed: " + TestStatusPollerAndResultHandler.totalTestMethodsExecuted);
		apexTestExecutionSummaryString.append("<br/> Test method pass count: "
				+ (TestStatusPollerAndResultHandler.totalTestMethodsExecuted - failureTestMethodsCount));
		apexTestExecutionSummaryString.append("<br/> Test method fail count: " + failureTestMethodsCount);

		appendTag(apexTestExecutionSummary, "summary", apexTestExecutionSummaryString.toString());
		appendLineSpaces(apexTestExecutionSummary, 1);

		htmlBuilder.append(apexTestExecutionSummary);
		appendLineSpaces(htmlBuilder, 2);
		// provide link to the test report
		appendTag(htmlBuilder, "header", "Apex Test Report: ");
		appendLineSpaces(htmlBuilder, 2);
		String workingDir = System.getProperty("user.dir");
		String apexUnitTestReportPath = "";
		if (!workingDir.contains("jenkins")) {
			apexUnitTestReportPath = workingDir + System.getProperty("file.separator") + "ApexUnitReport.xml";
		} else {
			int lastIndexOfSlash = workingDir.lastIndexOf('/');
			String jobName = workingDir.substring(lastIndexOfSlash + 1);
			apexUnitTestReportPath = "https://jenkins.internal.salesforce.com/job/" + jobName
					+ "/lastCompletedBuild/testReport/";
		}
		appendTag(htmlBuilder, "a", "style=\"font-size:125%\"; href=" + apexUnitTestReportPath, "Detailed Test Report");
		appendLineSpaces(htmlBuilder, 2);

		appendTag(htmlBuilder, "header", "Detailed code coverage report: ");
		appendLineSpaces(htmlBuilder, 2);
		htmlBuilder.append("<body>");

		StringBuilder codeCoverageHTMLContent = new StringBuilder();
		if (apexClassCodeCoverageBeans != null) {
			// populate the header cells for the table
			codeCoverageHTMLContent.append("<header>");
			appendHeaderCell(codeCoverageHTMLContent, "", "Apex Class Name");
			appendHeaderCell(codeCoverageHTMLContent, "", "API Version");
			appendHeaderCell(codeCoverageHTMLContent, "", "Code Coverage %");
			appendHeaderCell(codeCoverageHTMLContent, "", "#Covered Lines");
			appendHeaderCell(codeCoverageHTMLContent, "", "#Uncovered Lines");
			appendHeaderCell(codeCoverageHTMLContent, "", "Covered Lines");
			appendHeaderCell(codeCoverageHTMLContent, "", "Uncovered Lines");
			appendHeaderCell(codeCoverageHTMLContent, "", "Length Without Comments(Bytes)");
			codeCoverageHTMLContent.append("</header>");
			appendTag(codeCoverageHTMLContent, "tr", "");
			codeCoverageHTMLContent.append("<lowcc>");
			// populate the data cells for the table
			for (ApexClassCodeCoverageBean apexClassCodeCoverageBean : apexClassCodeCoverageBeans) {
				String codeCoverageStyle = "";
				if (apexClassCodeCoverageBean.getCoveragePercentage() < CommandLineArguments
						.getTeamCodeCoverageThreshold()) {
					codeCoverageStyle = "align='Center' style=\"color:crimson\"";
				} else {
					codeCoverageStyle = "align='Center' style=\"color:green\"";
				}
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						apexClassCodeCoverageBean.getApexClassName());
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle, apexClassCodeCoverageBean.getApiVersion());
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						String.format("%.2f", apexClassCodeCoverageBean.getCoveragePercentage()) + "%");
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						"" + apexClassCodeCoverageBean.getNumLinesCovered());
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						"" + apexClassCodeCoverageBean.getNumLinesUncovered());
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						populateListInAStringBuffer(apexClassCodeCoverageBean.getCoveredLinesList()));
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						populateListInAStringBuffer(apexClassCodeCoverageBean.getUncoveredLinesList()));
				appendDataCell(codeCoverageHTMLContent, codeCoverageStyle,
						apexClassCodeCoverageBean.getLengthWithoutComments());
				appendTag(codeCoverageHTMLContent, "tr", "");
			}
			codeCoverageHTMLContent.append("</lowcc>");
		}
		htmlBuilder.append("<table border='1'>");
		htmlBuilder.append(codeCoverageHTMLContent);
		htmlBuilder.append("</table>");

		// list out the duplicate entries(if any)
		if (ApexClassFetcherUtils.duplicateApexClassMap != null
				&& ApexClassFetcherUtils.duplicateApexClassMap.size() > 0) {
			StringBuilder duplicateApexClassesHTMLContent = new StringBuilder();
			appendLineSpaces(duplicateApexClassesHTMLContent, 2);
			duplicateApexClassesHTMLContent.append("<table border='1'>");
			appendHeaderCell(duplicateApexClassesHTMLContent, "",
					"Duplicate Apex Class Names Across Manifest Files And Regular Expressions");
			for (String duplicateEntry : ApexClassFetcherUtils.duplicateApexClassMap.values()) {
				appendTag(duplicateApexClassesHTMLContent, "tr", "");
				appendDataCell(duplicateApexClassesHTMLContent, "", duplicateEntry);
			}
			duplicateApexClassesHTMLContent.append("</table>");
			htmlBuilder.append(duplicateApexClassesHTMLContent);
		}

		// list out the non existant class entries(if any)
		if (ApexManifestFileReader.nonExistantApexClassEntries != null
				&& ApexManifestFileReader.nonExistantApexClassEntries.size() > 0) {
			StringBuilder nonExistantApexClassesHTMLContent = new StringBuilder();
			appendLineSpaces(nonExistantApexClassesHTMLContent, 2);
			nonExistantApexClassesHTMLContent.append("<table border='1'>");
			appendHeaderCell(nonExistantApexClassesHTMLContent, "",
					"Invalid/Non-existant Apex Class Names Across Manifest Files And Regular Expressions");
			for (String invalidEntry : ApexManifestFileReader.nonExistantApexClassEntries) {
				appendTag(nonExistantApexClassesHTMLContent, "tr", "");
				appendDataCell(nonExistantApexClassesHTMLContent, "", invalidEntry);
			}
			nonExistantApexClassesHTMLContent.append("</table>");
			htmlBuilder.append(nonExistantApexClassesHTMLContent);
		}
		appendLineSpaces(htmlBuilder, 2);
		appendTag(htmlBuilder, "a",
				"href=" + "http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_code_coverage_best_pract.htm",
				"Apex_Code_Coverage_Best_Practices");
		htmlBuilder.append(
				"<br/> <br/> <i>* Code coverage is calculated by dividing the number of unique Apex code lines executed during your test method execution by the total number of Apex code lines in all of your trigger and classes. <br/>(Note: these numbers do not include lines of code within your testMethods)</i>");
		htmlBuilder.append("</body>");
		htmlBuilder.append("</html>");

		createHTMLReport(htmlBuilder.toString());
	}

	private static void createHTMLReport(String htmlBuffer) {

		File tmpFile = null;
		FileOutputStream tmpOut = null;
		String workingDir = System.getProperty("user.dir") + System.getProperty("file.separator") + "Report";
		File dir = new File(workingDir);
		dir.mkdirs();
		tmpFile = new File(dir, "ApexUnitReport.html");
		byte[] reportAsBytes;
		try {
			tmpOut = new FileOutputStream(tmpFile);
			reportAsBytes = htmlBuffer.getBytes("UTF-8");
			tmpOut.write(reportAsBytes);
			tmpOut.close();
		} catch (UnsupportedEncodingException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "UnsupportedEncodingException encountered while creating the HTML report");
		} catch (FileNotFoundException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "FileNotFoundException encountered while writing the HTML report to ApexUnitReport.html");
		} catch (IOException e) {
			ApexUnitUtils
					.shutDownWithDebugLog(e, "IOException encountered while writing the HTML report to ApexUnitReport.html");
		}
	}

	private static String populateListInAStringBuffer(List<Long> listWithValues) {
		StringBuffer processedListAsStrBuf = new StringBuffer("");
		int i = 0;
		if (listWithValues != null) {
			for (Long value : listWithValues) {
				i++;
				processedListAsStrBuf.append(value);
				processedListAsStrBuf.append(",");
				if (i >= 10) {
					processedListAsStrBuf.append("\n");
					i = 0;
				}
			}
			return processedListAsStrBuf.substring(0, processedListAsStrBuf.length() - 1);
		} else {
			return "-";
		}
	}

	private static void appendTag(StringBuilder sb, String tag, String tagProperties, String contents) {
		sb.append('<').append(tag).append(" " + tagProperties).append('>');
		sb.append(contents);
		sb.append("</").append(tag).append('>');
	}

	private static void appendTag(StringBuilder sb, String tag, String contents) {
		appendTag(sb, tag, "", contents);
	}

	private static void appendDataCell(StringBuilder sb, String tagProperties, String contents) {
		appendTag(sb, "td", tagProperties, contents);
	}

	private static void appendHeaderCell(StringBuilder sb, String tagProperties, String contents) {
		appendTag(sb, "th", tagProperties, contents);
	}

	private static void appendLineSpaces(StringBuilder sb, int numberOfLines) {
		for (int i = 1; i < numberOfLines; i++) {
			appendTag(sb, "br", "");
		}
	}
}
