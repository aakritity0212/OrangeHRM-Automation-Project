package com.orangehrm.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class TestListeners implements ITestListener{

	//Triggered when a test starts
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		//Start logging into Extent reports
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test started: " + testName);
	}

	//Triggered when a test succeeds
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test Passed suucessfully!", "Test End: " + testName + " - Test Passed");
	}

	//Triggered when a test fails
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(failureMessage);
		ExtentManager.logFailure(BaseClass.getDriver(), "Test Failed", "Test End: " + testName + " - Test Failed");
	}

	//Triggered when a test skips
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test Skipped: " + testName);
	}

	//Triggered when a suite starts
	@Override
	public void onStart(ITestContext context) {
		//Initialize the Extent report
		ExtentManager.getReporter();
	}

	//Triggered when a suite ends
	@Override
	public void onFinish(ITestContext context) {
		//Flush the Extent Reports
		ExtentManager.endTest();
	}

}
