package com.github.jorge2m.example_test.test.testcase.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.github.jorge2m.testmaker.service.webdriver.pageobject.PageObjTM;

public class GoogleMainPage extends PageObject {

	private final static String XPathInputBuscador = "//input[@role='combobox']";
	private final static String XPathBuscarConGoogleButton = "//input[@value='Buscar con Google']";
	
	private GoogleMainPage(WebDriver driver) {
		super(driver);
	}
	public static GoogleMainPage getNew(WebDriver driver) {
		return new GoogleMainPage(driver);
	}
	
	public void inputText(String textToInput) {
		By byInput = By.xpath(XPathInputBuscador);
		driver.findElement(byInput).sendKeys(textToInput);
	}
	
	public ResultsGooglePage clickBuscarConGoogleButton() {
		click(By.xpath(XPathBuscarConGoogleButton)).exec();
		return ResultsGooglePage.getNew(driver);
	}
}