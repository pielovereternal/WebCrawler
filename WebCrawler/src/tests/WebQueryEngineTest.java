package tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import assignment.Index;
import assignment.WebIndex;
import assignment.WebQueryEngine;

public class WebQueryEngineTest {
	private WebIndex webIndex;
	
	// initialize the index for use for the tests
	private void loadIndex() throws ClassNotFoundException, IOException {
        URL baseURL = new File(System.getProperty("user.dir")).toURI().toURL();
		URL indexURL = new URL(baseURL, "index.db");
		webIndex = (WebIndex)Index.load(indexURL);
	}
	
	@Test
	public void testWebQueryEngine() {
		try {
			loadIndex();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		WebQueryEngine engine = new WebQueryEngine();
		engine.useWebIndex(webIndex);
		
		String first = "\"collection of\"";
		System.out.println(engine.query(first).size());
		
		String second = "((jokes & fun) | death bomb)";
		System.out.println(engine.query(second).size());
	}
}
