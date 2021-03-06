package com.github.jorge2m.testmaker.service.webdriver.maker;

import com.github.jorge2m.testmaker.domain.InputParamsTM;
import com.github.jorge2m.testmaker.domain.suitetree.TestRunTM;

public class FactoryWebdriverMaker {

	public enum EmbeddedDriver { 
		firefox(false), 
		firefoxhless(true), 
		chrome(false), 
		chromehless(true),
		edge(false),
		browserstack(false);
		
		boolean headless;
		private EmbeddedDriver(boolean headless) {
			this.headless = headless;
		}
		
		public boolean isHeadless() {
			return headless;
		}
	}	

	public static DriverMaker make(TestRunTM testRun) {
		InputParamsTM inputParams = testRun.getSuiteParent().getInputParams();
		String driver = inputParams.getDriver();
		DriverMaker driverMakerUser = testRun.getDriverMakerUser();
		if (driverMakerUser!=null) {
			return driverMakerUser;
		}
		return getEmbebdedDriverMaker(driver, testRun);
	}
	
	private static DriverMaker getEmbebdedDriverMaker(String browser, TestRunTM testRun) {
		EmbeddedDriver driverType = EmbeddedDriver.valueOf(browser);
		switch (driverType) {
		case firefox:
		case firefoxhless:
			return (new FirefoxdriverMaker(driverType.isHeadless()));
		case browserstack:
			return (new BrowserStackDriverMaker(testRun));
		case edge:
			return (new EdgedriverMaker());
		case chrome:
		case chromehless:
		default:
			return (new ChromedriverMaker(driverType.isHeadless()));
		}
	}
}
