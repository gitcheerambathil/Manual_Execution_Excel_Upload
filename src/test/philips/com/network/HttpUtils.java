package test.philips.com.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import test.philips.com.utils.Init;
import test.philips.com.utils.ReusableMethods;

public class HttpUtils extends Init {

	public static Object createStepUpdateJson(Object testPointId,Object testRunId,ArrayList<String> actualResult,ArrayList<String> status,ArrayList<String> stepName, int stepCount)throws Exception{
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		String testCaseStatus = "Passed";
		
		String[] actualResultArray = new String[actualResult.size()];
		actualResultArray = actualResult.toArray(actualResultArray);
		
		String[] statusArray = new String[status.size()];
		statusArray = status.toArray(statusArray);
		
		String[] stepnameArray = new String[stepName.size()];
		stepnameArray = stepName.toArray(stepnameArray);
		
		for (int i=0;i<stepCount;i++) {
			JSONObject object1 = new JSONObject();
			object1.put("actualResponse", actualResultArray[i]);
			object1.put("stepNumber", stepnameArray[i]);
			object1.put("StepStatus", statusArray[i]);
			array.put(object1);
			if(statusArray[i].equalsIgnoreCase("Failed"))
				testCaseStatus="Failed";
		}
		System.out.println("TestCaseID: "+ReusableMethods.testCaseId);
		object.put("testSteps", array);
		object.put("testRunId", testRunId);
		object.put("testCaseId", ReusableMethods.testCaseId);
		object.put("testPointId", testPointId);
		object.put("testCaseStatus", testCaseStatus);
		//object.put("comments", actualResult + "\r\n" + "Test Data" + "\r\n" + ReusableMethods.TEST_DATA);
		
		System.out.println("Json New"+object);
		return object;
	}
	/**
	 * This function will create the Json structure for updating the test results in test case level of TFS.
	 * @param testCaseId : Test case id for which the test case has to be updated.
	 * @param actualResult : Actual result of the test case
	 * @param status : Status of test case whether it is pass/fail
	 * @return : It will return the Json array to the HttpURLConnection
	 * @throws Exception
	 */
	public static Object createTestRunJson(Object testPointId)throws Exception{
		String testPlanId = config.getProperty("testPlanId");
		String testCase = ReusableMethods.testCaseId+"_"+ReusableMethods.TEST_DATA.get("TestCase_Name")+"_New Run";
		
		JSONObject object = new JSONObject();
		JSONObject object1 = new JSONObject();
		object1.put("id", testPlanId);
		JSONArray array = new JSONArray();
		array.put(testPointId);
		object.put("name", testCase);
		object.put("pointIds", array);
		object.put("plan", object1);
		
		System.out.println("Json"+object);
		return object;
	}
	/**
	 * This function will create the api URL to be called by adding the test run id.
	 * @param runId : Test Run id of TFS
	 * @return : It will return the final URL
	 * @throws Exception
	 */
	public static String getAbsoluteUrl(String urlType) throws Exception{
		int testCaseId = ReusableMethods.testCaseId;
		String testStepId = ReusableMethods.TEST_DATA.get("Step_No");
		String tfsLink = config.getProperty("tfsUrl");
		String apiStructure = config.getProperty("apiStructure");
		String testPlanId = config.getProperty("testPlanId");
		String testSuiteId = config.getProperty("testSuiteId");
		String updateStepUrl = config.getProperty("updateStepUrl");
		String uploadFileUrl = config.getProperty("uploadFileUrl");
		
		if(urlType.equalsIgnoreCase("getTestPointUrl"))
			return tfsLink+apiStructure+"/plans/"+testPlanId+"/suites/"+testSuiteId+"/points?testcaseid="+testCaseId+"&includePointDetails=true";
		else if(urlType.equalsIgnoreCase("startTestRun"))
			return tfsLink+apiStructure+"/runs?api-version=3.0-preview";
		else if(urlType.equalsIgnoreCase("updateTestStepResult"))
			return updateStepUrl;
		else if(urlType.equalsIgnoreCase("uploadFileUrl"))
			return uploadFileUrl+ReusableMethods.testRunId+"/"+testCaseId+"/"+ReusableMethods.testPointId+"/"+testStepId+"?fileName=";
		else
			return null;
		
	}
	
	
	public static Object createJsonForPost(int testCaseId, String actualResult,String status)throws Exception{

		return null;
		
	}
	
	

}
