package com.github.jorge2m.testmaker.boundary.remotetest;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.SerializationUtils;

import com.github.jorge2m.testmaker.domain.InputParamsTM;
import com.github.jorge2m.testmaker.domain.ServerSubscribers.ServerSubscriber;
import com.github.jorge2m.testmaker.domain.suitetree.ChecksTM;
import com.github.jorge2m.testmaker.domain.suitetree.StepTM;
import com.github.jorge2m.testmaker.domain.suitetree.SuiteBean;
import com.github.jorge2m.testmaker.domain.suitetree.TestCaseBean;
import com.github.jorge2m.testmaker.domain.suitetree.TestCaseTM;

public class RemoteTest extends JaxRsClient {
	
	private final ServerSubscriber server;
	
	public RemoteTest(ServerSubscriber server) {
		this.server = server;
	}
	
	public SuiteBean execute(TestCaseTM testCase, Object testObject) 
	throws Exception {
		InputParamsTM inputParams = testCase.getInputParamsSuite();
		if (testCase.getSuiteParent().isTestFromFactory(testObject)) {
			return executeTestFromFactory(testCase, inputParams, (Serializable)testObject);
		}
		return executeTestStandar(testCase, inputParams);
	}
	
	private SuiteBean executeTestFromFactory(TestCaseTM testCase, InputParamsTM inputParams, Serializable testObject) 
	throws Exception {
		byte[] testSerialized = SerializationUtils.serialize(testObject);
		String testSerializedStrB64 = Base64.getEncoder().encodeToString(testSerialized);
		SuiteBean suiteRemote = suiteRun(
				inputParams, 
				Arrays.asList(testCase.getName()), 
				testSerializedStrB64);
		return processTestCaseRemote(testCase, suiteRemote); 
	}
	
	private SuiteBean executeTestStandar(TestCaseTM testCase, InputParamsTM inputParams) throws Exception {
		SuiteBean suiteRemote = suiteRun(
				inputParams, 
				Arrays.asList(testCase.getName()), 
				null);
		return processTestCaseRemote(testCase, suiteRemote);
	}
	
	private SuiteBean processTestCaseRemote(TestCaseTM testCase, SuiteBean suiteRemoteExecuted) {
		//Get TestCase Remote
		TestCaseBean testCaseRemote = getTestCaseRemote(suiteRemoteExecuted);
		
		//Coser TestCase
		String throwableStrB64 = testCaseRemote.getThrowable();
		Throwable throwable = (Throwable)fromStringB64(throwableStrB64);
		testCase.getResult().setThrowable(throwable);
		testCase.getResult().setStatus(testCaseRemote.getStatusTng());
		testCase.addSufixToName(testCaseRemote.getSpecificInputData());
		
		List<StepTM> listStepsRemote = testCaseRemote.getListStep();
		for (StepTM stepRemote : listStepsRemote) {
			testCase.addStep(stepRemote);
			stepRemote.setParents(testCase);
			stepRemote.getEvidencesWarehouse().setStep(stepRemote);
			stepRemote.moveContentEvidencesToFile();
			for (ChecksTM checks : stepRemote.getListChecksTM()) {
				checks.setParents(stepRemote);
			}
		}

		return suiteRemoteExecuted;
	}
	
	private TestCaseBean getTestCaseRemote(SuiteBean suiteRemote) {
		List<TestCaseBean> listTestCaseRemote = suiteRemote
				.getListTestRun().get(0)
				.getListTestCase();
		for (TestCaseBean testCaseRemote : listTestCaseRemote) {
			if (testCaseRemote.getListStep().size() > 0) {
				return testCaseRemote;
			}
		}
		return listTestCaseRemote.get(0);
	}
	
	public SuiteBean suiteRun(InputParamsTM inputParams, List<String> testCases, String testObjectSerialized) 
	throws Exception {
		Form formParams = getFormParams(inputParams.getAllParamsValues());
		MultivaluedMap<String, String> mapParams = formParams.asMap();
		if (testCases!=null) {
			mapParams.putSingle(InputParamsTM.TCaseNameParam, String.join(",", testCases));
		}
		if (testObjectSerialized!=null) {
			mapParams.putSingle(InputParamsTM.TestObjectParam, testObjectSerialized);
		}
		mapParams.putSingle(InputParamsTM.AsyncExecParam, "false");
		mapParams.putSingle(InputParamsTM.RemoteParam, "true");

		Client client = getClientIgnoreCertificates();
		SuiteBean suiteData = 
			client
				.target(server.getUrl() + "/suiterun")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.form(formParams), SuiteBean.class);

		return suiteData;
	}
	
	private Form getFormParams(Map<String,String> params) {
		Form formParams = new Form();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			formParams.param(entry.getKey(), entry.getValue());
		}
		return formParams;
	}
	
	/** Read the object from Base64 string. */
	private static Object fromStringB64(String s) {
		try {
			byte [] data = Base64.getDecoder().decode( s );
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o  = ois.readObject();
			ois.close();
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}
}
