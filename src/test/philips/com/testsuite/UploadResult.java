package test.philips.com.testsuite;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import test.philips.com.network.HttpUtils;
import test.philips.com.utils.ReusableMethods;
/**
 * This class contains all the test scenarios to be tested related to to login.
 * @author Maneesh Cheerambathil
 *
 */
public class UploadResult extends ReusableMethods{
	int numberOfTestScenarios=0;
	String excelTabName = "1797 - Mandatory_Boundry";
	int row = 0;
	int nextRow=0;
    String reportPath=null;
    String logPath=null;
	public static Logger logger;
   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	
	@BeforeTest
	private void beforeTest()throws Exception{
		numberOfTestScenarios=getNumberOfTestScenarios(excelTabName); 
		reportPath=config.getProperty("reportPath")+dateFormat.format(new Date());
		File file = new File(reportPath); 
        if (!file.exists())
        	file.mkdirs();
		//Start of Log
        String logPath = file+"\\"+"TFS Upload Exception Log_"+dateFormat.format(new Date())+".log";
      	Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh1;  
        fh1 = new FileHandler(logPath);  
        logger.addHandler(fh1);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh1.setFormatter(formatter); 
	}
	
	@Test
	private void loginTest(){
		for(row=1;row<numberOfTestScenarios;){
			 try{
				 String StepNo = null;
				 int stepCount = 0;
				 ArrayList<String> actualresult = new ArrayList<String>();
				 ArrayList<String> status = new ArrayList<String>();
				 ArrayList<String> stepName = new ArrayList<String>();
				 getTestData(excelTabName,row);
				 nextRow =row+1;
				 if(!TEST_DATA.get("TestCase_ID").isEmpty()&&
						 !TEST_DATA.get("TestCase_ID").equals(null)&&
						 !TEST_DATA.get("TestCase_ID").equals(""))
					 ReusableMethods.testCaseId=Integer.parseInt(TEST_DATA.get("TestCase_ID"));	
				 do{
					 if(nextRow<numberOfTestScenarios){
						 getTestData(excelTabName,nextRow);
						 StepNo =TEST_DATA.get("Step_No");
						 getTestData(excelTabName,row);
						 actualresult.add(TEST_DATA.get("Actual_Result"));
						 status.add(TEST_DATA.get("Status"));
						 stepName.add(TEST_DATA.get("Step_No"));
						 stepCount=stepCount+1;
						 nextRow=nextRow+1;
					 }
					 else{
						 getTestData(excelTabName,row);
						 actualresult.add(TEST_DATA.get("Actual_Result"));
						 status.add(TEST_DATA.get("Status"));
						 stepName.add(TEST_DATA.get("Step_No"));
						 stepCount=stepCount+1;
						 nextRow=nextRow+1;
						 StepNo ="1";
					 }
					 row=row+1;
				 }while(!StepNo.equalsIgnoreCase("1"));
				 uploadResultsToTfs(actualresult,status,stepName,stepCount);
			 }catch(Exception e){
				 String message;
				try {
					 message = "TFS Upload Failed for "+ TEST_DATA.get("TestCase_ID")+"_"+testRunId+". Exception : " + getExceptionString(e);
					 //logger.info(message);  
					 e.printStackTrace();	
				} catch (Exception e1) {
					 e1.printStackTrace();
				}

			 }	
		}
	 }	

	@AfterTest
	private void afterTest()throws Exception{
		System.out.println("Test Completed");
	}
}


