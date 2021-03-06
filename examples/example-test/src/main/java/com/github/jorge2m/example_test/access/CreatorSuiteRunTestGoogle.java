package com.github.jorge2m.example_test.access;

import java.util.Arrays;

import com.github.jorge2m.example_test.access.datatmaker.Suites;
import com.github.jorge2m.example_test.test.suite.SmokeTestSuite;
import com.github.jorge2m.testmaker.domain.CreatorSuiteRun;
import com.github.jorge2m.testmaker.domain.InputParamsBasic;
import com.github.jorge2m.testmaker.domain.SuiteMaker;

public class CreatorSuiteRunTestGoogle extends CreatorSuiteRun {

	private final String ChromeDriverVersionDefault = "83.0.4103.39";
	private final String GeckoDriverVersionDefault = "0.26.0";
	
	private CreatorSuiteRunTestGoogle() throws Exception {
		super();
	}
	private CreatorSuiteRunTestGoogle(InputParamsBasic inputParams) throws Exception {
		super(inputParams);
	}
	public static CreatorSuiteRunTestGoogle getNew() throws Exception {
		return new CreatorSuiteRunTestGoogle();
	}
	public static CreatorSuiteRunTestGoogle getNew(InputParamsBasic inputParams) throws Exception {
		return new CreatorSuiteRunTestGoogle(inputParams);
	}
	
	@Override
	public SuiteMaker getSuiteMaker() throws Exception {
		setWebDriverVersion();
		try {
			switch ((Suites)inputParams.getSuite()) {
			case SmokeTest:
				return (new SmokeTestSuite(inputParams));
			case OtherSuite:  
			default:
			}
		}
		catch (IllegalArgumentException e) {
			System.out.println("Suite Name not valid. Posible values: " + Arrays.asList(Suites.values()));
		}

		return null;
	}
	
	private void setWebDriverVersion() {
		if (inputParams.getDriverVersion()==null) {
			switch (inputParams.getDriver()) {
			case "firefox":
				inputParams.setDriverVersion(GeckoDriverVersionDefault);
				break;
			case "chrome":
				inputParams.setDriverVersion(ChromeDriverVersionDefault);
				break;
			}
		}
	}
}
