package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import assignment.WebCrawler;

public class WebCrawlerTest {
	private WebGenerator wg;
	
	/**
	 * Create the test web used for these unit tests.
	 */
	private void createWebGenerator() {
		// create the test web
		HashMap<String, String[]> web = new HashMap<String, String[]>();
		
		// initialize the URLs
		ArrayList<String> urls = new ArrayList<String>();
		urls.add("index");
		urls.add("a");
		urls.add("b");
		urls.add("c");
		urls.add("d");
		urls.add("e");

		// initialize the list of neighbors
		List<String[]> neighborList = new ArrayList<String[]>();
		neighborList.add(new String[] {"a", "b", "c", "d", "e"});
		neighborList.add(new String[] {"b", "index"});
		neighborList.add(new String[] {"c", "index"});
		neighborList.add(new String[] {"d", "index"});
		neighborList.add(new String[] {"e", "index"});
		neighborList.add(new String[] {"a", "index"});		
		
		// add to the map
		Iterator<String> urlIter = urls.iterator();
		Iterator<String[]> linksIter = neighborList.iterator();
		
		while (urlIter.hasNext() && linksIter.hasNext()) {
			web.put(urlIter.next(), linksIter.next());
		}

		wg = new WebGenerator(web);
	}

	@Test
	public void testWebCrawler() throws IOException {	
		createWebGenerator();
		wg.generateWeb();
		
		String index = wg.generateWeb();
		System.out.println(index);
		try {
			WebCrawler.main(new String[] {index});
		} catch (Exception e) {
			e.printStackTrace();
		}		
				
	}
	
	@Test
	public void testRegex() {
		String link = "external.html?link=http://www.templetons.com/brad/ad.html";
		System.out.println(link.split("\\?\\w+")[0]);
		System.out.println(link.substring(0, link.lastIndexOf('?')));
		
		link = "news:news.announce.newusers";
		assertTrue(link.matches("^\\w*[\\:]\\w+"));
		
		link = "dummy#bob";
		assertTrue(link.matches(".*#\\w+$"));
	}

	@Test
	public void testCharacters() {
		fail("Not yet implemented");
	}

	@Test
	public void testParse() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartElement() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndElement() {
		fail("Not yet implemented");
	}

}
