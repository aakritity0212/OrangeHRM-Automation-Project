package com.orangehrm.test;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass extends BaseClass {
	
	@Test
	public void dummyTest() {
		//ExtentManager.startTest("DummyTest1 Start"); --This has been implemented in TestListener
		String title = getDriver().getTitle();
		assert title.equals("OrangeHRM") : "Test Failed - Title is not Matching";
		ExtentManager.logStep("Verifying the title");
		System.out.println("Test Passed - Title is Matching");
		ExtentManager.logStep("This test case is skipped");
		throw new SkipException("Skipping the test as part of testing");
	}
}
