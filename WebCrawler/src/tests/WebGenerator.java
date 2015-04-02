package tests;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Given an adjacency list representing the pages and their links,
 * builds a small "<i>web</i>" in the <i>tests</i> folder. Temporary files
 * and directories are used, and these should be deleted after testing is done.
 * 
 * @author Aaron
 *
 */
public class WebGenerator {
	public static Path directoryPath;
	private boolean isBuilt;
	private URL baseURL;
	private HashMap<String, String[]> adjList;
	
	/**
	 * Initializes the WebGenerator with a Map representing
	 * each URL and its neighbors. 
	 * <br> Each URL key should not contain an extension.
	 * <br> Note that each element in the 
	 * adjacency list should be a relative link.
	 * 
	 * @param adjList HashMap that maps a URL to all links that should
	 * 				  be found on that page.
	 */
	public WebGenerator(HashMap<String, String[]> adjList) {
		try {
			baseURL = new File(System.getProperty("user.dir")).toURI().toURL();					
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		directoryPath = Paths.get(baseURL.getPath().substring(1) + "testweb");
		System.out.println(directoryPath.toString());
		this.isBuilt = false;
		this.adjList = adjList;		
	}
	
	/**
	 * When called, creates the web that this WebGenerator represents.
	 * Converts the adjacency list into a set of pages and their links,
	 * then creates the files.
	 * 
	 * @throws IOException
	 * @return returns the Path corresponding to the generated index page
	 */
	public String generateWeb() throws IOException {
		if (isBuilt)
			return "";
		else if (baseURL == null || adjList == null) {
			System.err.println("Error with directory or no list supplied.");
			return "";
		} else {
			String index = "";
			
			// create directory for test
			Path dirPath = Paths.get(directoryPath.toUri());
			File tempDir = new File(dirPath.toString());
			if (!tempDir.exists()) 
				tempDir.mkdir();		
			
			// create each page as a new HTML file
			Iterator<String> iter = adjList.keySet().iterator();
			while (iter.hasNext()) {
				// grab the URL for the current page
				String URL = iter.next();
				String[] neighbors = adjList.get(URL);
				
				// create text that emulates an HTML page
				String page = createPage(neighbors);
				byte[] pageBytes = page.getBytes(Charset.forName("UTF-8"));
				
				// create actual file from the text
				Path filePath = Paths.get(tempDir.getPath(), URL + ".html");
			    Path tempFile = Files.createFile(filePath);
			    Files.write(tempFile, pageBytes, StandardOpenOption.WRITE);
			    
			    // save index
			    if (URL.equals("index")) {
			    	index = tempFile.toString();
			    }			    			
			}		
			
			return index;
		}
	}
	
	/**
	 * Deletes the temporary files and folder generated.
	 * 
	 * @throws IOException
	 */
	public void deleteWeb(String dir) throws IOException {
		// find temporary directory created
		File directory = new File(dir);
		
		// find all files within and delete them
		File[] files = directory.listFiles();
		for (File f: files) {
			f.delete();
		}
		
		directory.delete();
	}
	
	/**
	 * Attempts to create a String representation of an HTML page.
	 * @return String representing an HTML page
	 */
	private String createPage(String[] neighbors) {
		// build page
		String page = "<!DOCTYPE html>\n"
			        + "<html>\n"
			        + "<body>\n";
		
		// embed links
		for (String neighbor: neighbors) {
			page += "<a href=" + neighbor + ".html>\n";
		}
		
		// finish page
		page += "</body>\n";
		page += "</html>";
		
		return page;
	}
}
