/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * QueryConstructor class for constructing queries that are fired by the ApexUnit tool
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 

package com.sforce.cd.apexUnit.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.partner.sobject.SObject;

/*
 * Constructs and returns queries for the client to invoke web services and fetch the data from the org
 */
public class QueryConstructor {
	private static Logger logger = LoggerFactory.getLogger(QueryConstructor.class);
	/*
	 * construct query that fetches Id and Name of the ApexClass based on regex
	 * provided by the user * is converted to % in the regex. If no * is found
	 * in the regex, the regex is considered as a prefix and a % is added at the
	 * end of regex
	 * 
	 * @param regex - regex for ApexClass as String
	 * 
	 * @return - Query to fetch apex classes as String
	 */
	public static String generateQueryToFetchApexClassesBasedOnRegex(String namespace, String regex) {
		String processedRegex = processRegexForSoqlQueries(regex);
		String soql = "SELECT Id , Name FROM ApexClass WHERE NamespacePrefix ="+ escapeSingleQuote(namespace) +" AND "
				+ "Name like " + escapeSingleQuote(processedRegex);
		return soql;
	}

	/*
	 * construct query that fetches Id and Name of the ApexTrigger based on
	 * regex provided by the user * is converted to % in the regex. If no * is
	 * found in the regex, the regex is considered as a prefix and a % is added
	 * at the end of regex
	 * 
	 * @param regex - regex for ApexTrigger as String
	 * 
	 * @return - Query to fetch apex triggers as String
	 */
	public static String generateQueryToFetchApexTriggersBasedOnRegex(String namespace, String regex) {
		String processedRegex = processRegexForSoqlQueries(regex);
		String soql = "SELECT Id , Name FROM ApexTrigger WHERE NamespacePrefix ="+ escapeSingleQuote(namespace) +" AND "
				+ "Name like " + escapeSingleQuote(processedRegex);
		return soql;
	}

	/*
	 * construct query that fetches Id and Name of the ApexClass for a given
	 * ApexClassId
	 * 
	 * @param apexClassId - apexClassId as String
	 * 
	 * @return - Query to fetch apex class as String
	 */
	public static String generateQueryToFetchApexClassFromId(String apexClassId) {
		String soql = "SELECT Id, Name FROM ApexClass WHERE Id = " + escapeSingleQuote(apexClassId);
		return soql;
	}

	/*
	 * construct query that fetches Id and Name of the ApexTrigger for a given
	 * ApexClassId
	 * 
	 * @param apexTriggerId - ApexTrigger Id as String
	 * 
	 * @return - Query to fetch apex trigger as String
	 */
	public static String generateQueryToFetchApexTriggerFromId(String apexTriggerId) {
		String soql = "";
		if (apexTriggerId != null && !apexTriggerId.equals("")) {
			soql = "SELECT Id, Name FROM ApexTrigger WHERE Id = " + escapeSingleQuote(apexTriggerId);
		}
		return soql;
	}

	/*
	 * construct query that fetches Id and Name of the ApexClass for a given
	 * ApexClassName
	 * 
	 * @param apexClassName - Apex class name as String
	 * 
	 * @return - Query to fetch apex class as String
	 */
	public static String generateQueryToFetchApexClass(String namespace, String apexClassName) {
		String soql = "";
		if (apexClassName != null && !apexClassName.equals("")) {
			soql = "SELECT Id, Name FROM ApexClass WHERE NamespacePrefix ="+ escapeSingleQuote(namespace) +" AND "
					+ "Name = " + escapeSingleQuote(apexClassName);
		}
		return soql;
	}

	/*
	 * construct query that fetches Id and Name of the ApexTrigger for a given
	 * ApexTriggerName
	 * 
	 * @param apexClassName - Apex trigger name as String
	 * 
	 * @return - Query to fetch apex trigger as String
	 */
	public static String generateQueryToFetchApexTrigger(String namespace, String apexTriggerName) {
		String soql = "";
		if (apexTriggerName != null && !apexTriggerName.equals("")) {
			soql = "SELECT Id, Name FROM ApexTrigger WHERE NamespacePrefix ="+ escapeSingleQuote(namespace) +" AND "
					+ "Name = " + escapeSingleQuote(apexTriggerName);
		}
		return soql;
	}

	/*
	 * construct query that fetches parent job Id for a given ApexTestQueueItem
	 * Id
	 * 
	 * @param apexTestQueueItemId - Apex trigger name as String
	 * 
	 * @return - Query to fetch apex trigger as String
	 */
	public static String fetchParentJobIdForApexTestQueueItem(String testQueueItemId) {
		String soql = "";
		if (testQueueItemId != null && !testQueueItemId.equals("")) {
			// we need to limit the number of records we fetch to 1 because
			// multiple records are returned for each ApexTestClass
			soql = "select ParentJobId from ApexTestQueueItem where id  = " + escapeSingleQuote(testQueueItemId)
					+ " limit 1";
		}
		return soql;
	}

	/*
	 * construct query that fetches the test execution status and related info
	 * from ApexTestQueueItem table for a given parentJob Id
	 * 
	 * @param parentJobId - Parent job ID as String
	 * 
	 * @return - Query to fetch test execution status as String
	 */
	public static String getTestExecutionStatus(String parentJobId) {
		String soql = "";
		if (parentJobId != null && !parentJobId.equals("")) {
			soql = "Select Id, ApexClassId, ApexClass.Name, ExtendedStatus, ParentJobId, Status, SystemModstamp, CreatedDate "
					+ "From ApexTestQueueItem Where ParentJobId = "+ escapeSingleQuote(parentJobId);
		}
		return soql;
	}

	/*
	 * construct query that fetches the test result and related info from
	 * ApexTestResult table for a given parentJob Id
	 * 
	 * @param parentJobId - Parent job ID as String
	 * 
	 * @return - Query to fetch test result as String
	 */
	public static String fetchResultFromApexTestQueueItem(String parentJobId) {
		String soql = "";
		if (parentJobId != null && !parentJobId.equals("")) {
			soql = "SELECT ApexClassId,AsyncApexJobId,Id,Message,MethodName,Outcome,QueueItemId,RunTime,StackTrace,SystemModstamp,TestTimestamp "
					+ "FROM ApexTestResult WHERE AsyncApexJobId = "+ escapeSingleQuote(parentJobId);
		}
		return soql;
	}

	/*
	 * construct query that fetches the count of the test result from
	 * ApexTestQueueItem table for a given parentJob Id
	 * 
	 * @param parentJobId - Parent job ID as String
	 * 
	 * @return - Query to fetch count of the test result as String
	 */
	public static String fetchTestClassCountForParentJobId(String parentJobId) {
		String soql = "";
		if (parentJobId != null && !parentJobId.equals("")) {
			soql = "select count() from ApexTestQueueItem where ParentJobId = '" + parentJobId + "'";
		}
		return soql;
	}

	/*
	 * construct query that computes aggregated code coverage metrics for a
	 * given array of classes
	 * 
	 * @param parentJobId - array of class names as String
	 * 
	 * @return - Query to compute aggregated code coverage metrics as String
	 */
	public static String getAggregatedCodeCoverage(String classArrayForQuery) {
		String soql = "";
		if (classArrayForQuery != null && !classArrayForQuery.equals("")) {
			soql = "select ApexClassorTriggerId,NumLinesCovered,NumLinesUncovered,Coverage FROM "
					+ "ApexCodeCoverageAggregate WHERE ApexClassOrTriggerId IN (" + classArrayForQuery + ")";
		}
		return soql;
	}

	/*
	 * construct query that computes code coverage metrics for a given array of
	 * classes at each method level (to be used later. not in use currently)
	 * 
	 * @param parentJobId - array of class names as String
	 * 
	 * @return - Query to compute code coverage metrics as String
	 */

	public static String getClassLevelCodeCoverage(String classArrayForQuery) {
		String soql = "select ApexClassOrTriggerId,NumLinesCovered,ApexTestClassId,NumLinesUncovered,TestMethodName FROM "
				+ "ApexCodeCoverage WHERE ApexClassOrTriggerId IN (" + classArrayForQuery + ")";
		return soql;
	}

	/*
	 * construct query that computes org wide code coverage metrics for all the
	 * classes in the prg
	 * 
	 * @return - Query to compute org wide code coverage metrics as String
	 */
	public static String getOrgWideCoverage() {
		String soql = "select PercentCovered FROM ApexOrgWideCoverage";
		return soql;
	}

	/*
	 * construct query that fetches class info of the ApexClass for a given
	 * ApexClassId
	 * 
	 * @param apexClassId - Apex class id as String
	 * 
	 * @return - Query to fetch info of apex class as String
	 */
	public static String getApexClassInfo(String apexClassId) {
		String soql = "";
		if (apexClassId != null && !apexClassId.equals("")) {
			soql = "SELECT Id,Name,ApiVersion,LengthWithoutComments FROM ApexClass where Id = "
					+ escapeSingleQuote(apexClassId);
		}
		return soql;
	}

	/*
	 * construct query that fetches info of the ApexTrigger for a given Apex
	 * class(trigger) Id
	 * 
	 * @param apexClassId - Apex trigger id as String
	 * 
	 * @return - Query to fetch info on apex trigger as String
	 */
	public static String getApexTriggerInfo(String apexClassId) {
		String soql = "";
		if (apexClassId != null && !apexClassId.equals("")) {
			soql = "SELECT Id,Name,ApiVersion,LengthWithoutComments FROM ApexTrigger where Id = "
					+ escapeSingleQuote(apexClassId);
		}
		return soql;
	}

	/*
	 * Process regex provided by the user so that the regex can be consumed by
	 * soql queries * is converted to % in the regex. If no * is found in the
	 * regex, the regex is considered as a prefix and a % is added at the end of
	 * regex
	 * 
	 * @param apexClassNameRegex - regex for the apexclass name (provided by the
	 * user)
	 * 
	 * @return processed regex as a String
	 */
	private static String processRegexForSoqlQueries(String apexClassNameRegex) {
		if (apexClassNameRegex != null) {
			String processedRegexForSoqlQueries = apexClassNameRegex.replace('*', '%');
			// if there are no '*'s in the given regex, convert it to prefix by
			// default
			if (processedRegexForSoqlQueries.equals(apexClassNameRegex)) {
				processedRegexForSoqlQueries += "%";
			}
			return processedRegexForSoqlQueries;
		}
		return null;
	}

	/*
	 * Escape single quotes in the user input during qyery execution
	 * 
	 * @param : userInput: String
	 * 
	 * @return : singleQuotrEscapedStr : String
	 */
	private static String escapeSingleQuote(String userInput) {
		String singleQuoteEscapedStr=null;
		if (userInput != null) {
			singleQuoteEscapedStr = userInput.replaceAll("'", "\'");
			singleQuoteEscapedStr = "'"+singleQuoteEscapedStr+"'";
		}
		return singleQuoteEscapedStr;
	}
	/*
	 * Query to check class exists in Apex Test queue 
	 * @param : userInput: String
	 * 
	 * @return : singleQuotrEscapedStr : String
	 *
	 */
	
	public static String getQueryForApexClassInfo(String apexClassess){
		String sql ="";
		if(apexClassess !=null){
			sql = "SELECT Id FROM ApexTestQueueItem WHERE  ApexClassId IN "+ "("+apexClassess+") AND status IN ('Queued', 'Processing')";
		}
		return sql;
	}
	
	
	
}
