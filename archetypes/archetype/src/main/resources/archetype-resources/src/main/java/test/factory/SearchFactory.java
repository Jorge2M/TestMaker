package ${package}.test.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Factory;

import ${package}.test.testcase.script.BuscarWithoutRefactor;

public class SearchFactory {

	private final List<String> searchValues;
	
	public SearchFactory() {
		searchValues = Arrays.asList( 
			"Zelda Breath of The Wild",
			"Enter the gungeon",
			"Xenoblade Chronicles",
			"Undertale",
			"Salamander Game",
			"XCOM Enemy Unknown",
			"Super Mario 64",
			"Zelda ocarina of time",
			"Baldur's Gate 2",
			"Head over Hells Game");
	}
	
	@Factory
	public Object[] FAC001_createTests() {
		List<Object> listTests = new ArrayList<>();
		for (String searchValue : searchValues) {
			listTests.add(new BuscarWithoutRefactor(searchValue));
		}
		return listTests.toArray(new Object[listTests.size()]);
	}
}
