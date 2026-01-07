package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {
	protected static Properties prop;
	//protected static WebDriver driver;
	//private static ActionDriver actionDriver;
	
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	@BeforeSuite
	public void loadConfig() throws IOException {
		// load the configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file is loaded");
	
		//Start the Extent Report
		//ExtentManager.getReporter(); --This has been implemented in TestListener
	}

	@BeforeMethod
	public synchronized void setup() throws IOException {
		System.out.println("Setting up webdriver for: " + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);
		logger.info("WebDriver initialized and browser maximized");
		logger.trace("This is a trace message");
		logger.error("This is a error message");
		logger.debug("This is a debug message");
		logger.fatal("This is a fatal message");
		logger.warn("This is a warn message");

		/*// Initialize the ActionDriver instance once
		if (actionDriver == null) {
			actionDriver = new ActionDriver(driver);
			logger.info("Action Driver instance is created");
		}*/
		
		//Initialize the ActionDriver for current thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread: " + Thread.currentThread().getId());
	}

	// Initialize the Webdriver based on browser defined in config.properties
	private synchronized void launchBrowser() {
		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			//driver = new ChromeDriver();
			driver.set(new ChromeDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("Chrome browser instance is created");
		} else if (browser.equalsIgnoreCase("firefox")) {
			//driver = new FirefoxDriver();
			driver.set(new FirefoxDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("Firefox browser instance is created");
		} else if (browser.equalsIgnoreCase("edge")) {
			//driver = new EdgeDriver();
			driver.set(new EdgeDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("Edge browser instance is created");
		} else {
			throw new IllegalArgumentException("Browser not supported: " + browser);
		}
	}

	// Configure browser settings such as implicit wait, maximize the browser and
	// navigate to URL
	private void configureBrowser() {
		// Implicit wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

		// maximize the browser
		getDriver().manage().window().maximize();

		// Navigate to URL
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to navigate to the URL: " + e.getMessage());
		}
	}

	@AfterMethod
	public synchronized void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				System.out.println("Unable to quit the driver: " + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		driver.remove();
		actionDriver.remove();
		//driver = null;
		//actionDriver = null;
		//ExtentManager.endTest(); --This has been implemented in TestListener
	}

	// Driver getter method public WebDriver getDriver() { return driver; }

	// Prop getter method
	public static Properties getProp() {
		return prop;
	}

	// Getter Method for WebDriver
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			System.out.println("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

	// Getter Method for ActionDriver
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			System.out.println("ActionDriver is not initialized");
			throw new IllegalStateException("ActionDriver is not initialized");
		}
		return actionDriver.get();
	}

	// Driver setter method
	public void setDriver(ThreadLocal<WebDriver> driver) {
		this.driver = driver;
	}

	// static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
