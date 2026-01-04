package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class HomePageTest extends BaseClass {
	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	@Test
	public void verfiyOrangeHRMLogo() {
		ExtentManager.startTest("Home Page verify Test");
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login("Admin", "admin123");
		ExtentManager.logStep("Verifying logo is visible or not");
		Assert.assertTrue(homePage.isOrangeHRMLogoVisible(), "Logo is not visible");
		ExtentManager.logStep("Validation successful");
		ExtentManager.logStep("Logged out successfully!");
	}
}
