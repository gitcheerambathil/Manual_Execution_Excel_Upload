package test.philips.com.utils;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import test.philips.com.network.HttpUtils;
import test.philips.com.testsuite.UploadResult;

import com.relevantcodes.extentreports.LogStatus;

/**
 * This class contains all the methods that can be reused in all the test case classes. 
 * @author Maneesh Cheerambathil
 *
 */
public class ReusableMethods extends Init {
	
	public static Map<String,String> TEST_DATA=new HashMap<String,String>();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    public static String screenshotFileName = null;
    public static String videoFileName = null;
    public static String videoFilePath = null;
    public static String screenshotFilePath = null;
	public static Object testRunId = null;
	public static Object testPointId = null;
	public static int testCaseId;
		
  	/**
  	 * This function will find the number of test scenarios from the test data excel file. Based on the excel file sheet/tab name passed as parameter, it will open the file and find number of test scearios in that.
  	 * @param excelSheetTabName  - Sheet/Tab name inside the test data excel file where the corresponding test case is written.
  	 * @return
  	 * @throws Exception
  	 */
  	public static int getNumberOfTestScenarios(String excelSheetTabName) throws Exception 
  	{
  			int rowCount=0;
  			File src=new File(config.getProperty("testDataPath"));
  		    Workbook workbook=Workbook.getWorkbook(src);
  		    Sheet worksheet=workbook.getSheet(excelSheetTabName);
  		    rowCount=worksheet.getRows();
  		    return rowCount;
  	}
  	
  	/**
  	 * This function will fetch all the test data in test scenario under the a particular row of excel tab name and this will write the data into a hash map. 
  	   Data will be written in hash map under the title of  column headers in the excel. For  eg: If we call Test_data.get("ID"), it will fin the id under title ID.
  	 * @param excelSheetTabName -  Excel sheet/ tab name under which the test scenario resides. 
  	 * @param excelSheetRowId - Row number of particular test scenario in the excel
  	 * @throws Exception
  	 */
  	public static void getTestData(String excelSheetTabName,int excelSheetRowId) throws Exception{
  			Map<String,String> dataSource=new HashMap<String,String>();
  		    String col,row = null;
  			File src=new File(config.getProperty("testDataPath"));
  		    Workbook workbook=Workbook.getWorkbook(src);
  		    Sheet worksheet=workbook.getSheet(excelSheetTabName);
  				  for (int j=0;j<worksheet.getColumns();j++){
  						jxl.Cell cell1=worksheet.getCell(j,0);
  						jxl.Cell cell2=worksheet.getCell(j,excelSheetRowId);	        
  				        row= cell1.getContents();
  				        col= cell2.getContents();
  				        dataSource.put(row, col);
  				        //System.out.println(TEST_DATA);
  				        TEST_DATA=dataSource; 
  				  }        
  	  	}
	/**
	 * This function will convert the exception caught to a string and return the exception as string.
	 * @param exception - Actual exception caught from the main class
	 * @return - It will return the exception as string value
	 * @throws Exception
	 */
	public static String getExceptionString(Exception exception) throws Exception{
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return(errors.toString());
	}
	
	/**
	 * This function will internally create the json structure for uploading the results into TFS and upload the actual and status.
	   This function will create a http connection and and call the Rest API of Tfs to update the result.
	 * @param actualResult - Array of steps Actual result of the test
	 * @param status - Array of steps Status of the test (Use enums "Passed, Failed, Blocked" only
	 * @param stepName - Array of step names
	 * @param stepCount - Number of Steps
	 * @throws Exception
	 */
	public static void uploadResultsToTfs(ArrayList<String> actualResult, ArrayList<String> status, ArrayList<String> stepName, int stepCount){
		Object jsonBody1 =null;	
		Object jsonBody2 =null;	
		try{
			URL url = new URL(HttpUtils.getAbsoluteUrl("getTestPointUrl"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));	
			JSONObject response1Json=new JSONObject(br.readLine());
			testPointId=response1Json.getJSONArray("value").getJSONObject(0).get("id");
			conn.disconnect();
			
			URL url1 = new URL(HttpUtils.getAbsoluteUrl("startTestRun"));
			HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
			conn1.setDoOutput(true);
			conn1.setRequestMethod("POST");
			conn1.setRequestProperty("Content-Type", "application/json");
			jsonBody1 = HttpUtils.createTestRunJson(testPointId);
			
			DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());
			wr.write(jsonBody1.toString().getBytes("UTF-8"));
			BufferedReader br1 = new BufferedReader(new InputStreamReader((conn1.getInputStream())));
			JSONObject responseJson=new JSONObject(br1.readLine());
			testRunId=responseJson.get("id");
			conn1.disconnect();
			
			URL url2 = new URL(HttpUtils.getAbsoluteUrl("updateTestStepResult"));
			HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
			conn2.setDoOutput(true);
			conn2.setRequestMethod("POST");
			conn2.setRequestProperty("Content-Type", "text/plain");
			jsonBody2 = HttpUtils.createStepUpdateJson(testPointId,testRunId,actualResult,status,stepName,stepCount);
			
			DataOutputStream wr1 = new DataOutputStream(conn2.getOutputStream());
			wr1.write(jsonBody2.toString().getBytes("UTF-8"));
			System.out.println("Status: "+conn2.getResponseCode()+conn2.getResponseMessage());
			conn2.disconnect();
			
			}catch(Exception e){
				try {
					String message = "TFS Upload Failed for "+ TEST_DATA.get("TestCase_ID")+"_"+testRunId+". Exception : " + getExceptionString(e);
					UploadResult.logger.info(message);  
					e.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	}

	/**
	 * This function will upload the screenshot or video taken for this test case to Tfs based on the type of file
	 * @param type : Type of file whether it is image or video. For screenshot use  keyword "image" and for video use keyword "video"
	 * @throws Exception
	 */
	public static void uploadAttachmentToTfs(String type) throws Exception{	
		try{
			Thread.sleep(1000);
			String charset = "UTF-8";
			File file=null;
			URL url = null;
			if(type.equalsIgnoreCase("image")){
				file = new File(screenshotFilePath);
				url = new URL(HttpUtils.getAbsoluteUrl("uploadFileUrl")+screenshotFileName);
			}
			else if(type.equalsIgnoreCase("video")){
				file = new File(videoFilePath);
				url = new URL(HttpUtils.getAbsoluteUrl("uploadFileUrl")+videoFileName);
			}			
			
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			
			System.out.println(url);
			
			URLConnection connection = new URL(url.toString()).openConnection();
			connection.setDoOutput(true);
		    
			OutputStream output = connection.getOutputStream();
		    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
		    writer.append("Content-Transfer-Encoding: binary");
		    
		    InputStream is = new FileInputStream(file);
		    IOUtils.copy(is, output);
		    
		    output.flush(); 
		    writer.close();

			int responseCode = ((HttpURLConnection) connection).getResponseCode();
			System.out.println(responseCode); 

		}catch(Exception e){
			try {
				String message = "TFS Image/video Upload Failed for "+ TEST_DATA.get("TestCase_ID")+"_"+testRunId+". Exception : " + getExceptionString(e);
				UploadResult.logger.info(message);  
				e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	
	}
	
}