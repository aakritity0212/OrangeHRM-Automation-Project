package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.core.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {
	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = (Logger) BaseClass.logger;
	
	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		logger.info("WebDriver instance is created");
	}
	
	//Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("Clicked an element:" + elementDescription);
			logger.info("Clicked an element " + elementDescription);
		} catch (Exception e) {
			System.out.println("Unable to click element: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element: ", elementDescription + "_unable to click");
			logger.error("Unable to click an element");
		}
	}
	
	//Method to enter text into an input field -- Avoid code duplication 
	public void enterText(By by, String value) {
		try {
			waitForElementToBeVisible(by);
			//driver.findElement(by).clear();
			//driver.findElement(by).sendKeys(value);
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text on: " + getElementDescription(by) + "-->" + value);
		} catch (Exception e) {
			logger.error("Unable to enter the text in the input field: " + e.getMessage());
		}
	}
	
	//Method to get text from an input field
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			return driver.findElement(by).getText();
		} catch (Exception e) {
			logger.error("Unable to get the text: " + e.getMessage());
			return "";
		}
	}
	
	//Method to compare two text -- Change the return type
	public boolean compareText(By by, String expectedText) {
		try {
			waitForElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if(expectedText.equals(actualText)) {
				logger.info("Both text are matching: " + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Compare Text", "Text Verified successfully!" + actualText + " equals " + expectedText);
				return true;
			}
			else {
				logger.error("Both text are not matching: " + actualText + " not equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Compare Text", "Text comparison failed: " + actualText + " not equals " + expectedText);
				return false;
			}
		} catch (Exception e) {
			logger.error("Unable to compare text " + e.getMessage());
		}
		return false;
	}
	
	//Method to check if an element is displayed
//	public boolean isDisplayed(By by) {
//		try {
//			waitForElementToBeVisible(by);
//			boolean isDisplayed = driver.findElement(by).isDisplayed();
//			if(isDisplayed) {
//				System.out.println("Element is visible");
//				return isDisplayed;
//			}
//			else {
//				return isDisplayed;
//			}
//		} catch (Exception e) {
//			System.out.println("Element is not displayed: " + e.getMessage());
//			return false;
//		}
//	}
	
	//Method to check if an element is displayed -- Remove redundant code
	public boolean isDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			logger.info("Element is displayed: " + getElementDescription(by));
			ExtentManager.logStep("Element is displayed: " + getElementDescription(by));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed: ", "Element is displayed: " + getElementDescription(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			logger.error("Element is not displayed: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Element is not displayed: ", "Element is not displayed: " + getElementDescription(by));
			return false;
		}
	}
	
	//Method to scroll to an element
	public void scrollToElement(By by) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0], scrollIntoView(true);", element);
		} catch (Exception e) {
			logger.error("Unable to locate element: " + e.getMessage());
		}
	}
	
	//Wait for the page to load
	public void waitForPage(int timeOutInSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)).executeScript("return document.readyState").equals("complete");
			logger.info("Page loaded successfully");
		} catch (Exception e) {
			logger.error("Page did not load within " + timeOutInSec + " seconds.Exception " + e.getMessage());
		}
	}
	
	//Wait for Element to be clickable
	private void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element is not clickable: " + e.getMessage());
		}
	}
	
	//Wait for Element to be visible
	private void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element not visible: " + e.getMessage());
		}
	}
	
	//Method to get element description using By locator 
	public String getElementDescription(By locator) {
		//Check for null driver or locator for Null pointer exception
		if(driver == null)
			return "driver is null";
		if(locator == null)
			return "locator is null";
		
		try {
			//find the element using the locator
			WebElement element = driver.findElement(locator);
			
			//Get ELement attributes
			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String text = element.getText();
			String className = element.getDomAttribute("class");
			String placeholder = element.getDomAttribute("placeholder");
			
			//Return the element based on attributes
			if(isNotEmpty(name)) {
				return "Element with name: " + name;
			}
			else if(isNotEmpty(id)) {
				return "Element with id: " + id;
			}
			else if(isNotEmpty(text)) {
				return "Element with text: " + truncate(text, 50);
			}
			else if(isNotEmpty(className)) {
				return "Element with class name: " + className;
			}
			else if(isNotEmpty(placeholder)) {
				return "Element with placeholder: " + placeholder;
			}
		} catch (Exception e) {
			logger.error("Unable to describe the element: " + e.getMessage());
		}
		return "Unable to describe the element";
	}
	
	//Utility method to check a String is not NULL or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}
	
	//Utility method to truncate long text
	private String truncate(String value, int maxLength) {
		if(value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength) + "...";
	}
}
