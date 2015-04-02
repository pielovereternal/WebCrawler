package assignment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WebCrawler extends Crawler {
	private WebIndex webIndex;
	private HashSet<String> visitedPages;
	private static final HashSet<String> tagsAvoid;
	private static final HashSet<String> extnsAvoid;
	private boolean printText;
	
	private String pageText;
	private boolean shouldAvoid;
	private URL context;
	private LinkedList<URL> links;
	private int pos;
	
	static {
		tagsAvoid = new HashSet<String>();
		tagsAvoid.add("style");
		tagsAvoid.add("head");
		tagsAvoid.add("script");
		tagsAvoid.add("img");
		tagsAvoid.add("html");
		tagsAvoid.add("option");
		tagsAvoid.add("meta");
		
		extnsAvoid = new HashSet<String>();
		extnsAvoid.add(".jpg");
		extnsAvoid.add(".png");
		extnsAvoid.add(".gif");
	}
	
    public WebCrawler() {
        parser.setContentHandler(this);
        webIndex = new WebIndex();
        visitedPages = new HashSet<String>();
        printText = false;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("No URLs specified.");
            System.exit(0);
        }
        
        List<URL> remaining = new LinkedList<>();
        for (int i = 0; i < args.length; i++) {
            String url = args[i];
            remaining.add(new URL(url));
        }
        
        long beginTime = System.nanoTime();
                
        WebCrawler crawler = new WebCrawler();
        while (!remaining.isEmpty()) {
            try {
                remaining.addAll(crawler.parse(remaining.remove(0)));
            } catch (MalformedURLException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            } catch (IllegalArgumentException e) {
            	System.out.println(e);
            }
        }
        
        double elapsedTime = System.nanoTime() - beginTime;
        elapsedTime /= 1e9;
        
        // print useful information
        System.out.println("\nIndex saved!");
        System.out.printf("Elapsed time: %2d min %2.2f s \n", 
        		(int)(elapsedTime / 60), elapsedTime % 60);
        System.out.println("Number of web pages: " + crawler.numPagesVisited());
        
        crawler.getWebIndex().save("index.db");
    }

    /**
     * Called to indicate an HTML document should be parsed. This method
     * will eventually cause the callback methods below to be called
     * when appropriate.
     *
     * @param url    URL of the HTML document to parse
     * @return    a List of URLs (in String form) found in the document
     */
    public List<URL> parse(URL url) throws IOException, SAXException {
    	// if already visited, return empty list
    	String urlString = url.toString();
    	if (visitedPages.contains(urlString)) {
    		return new LinkedList<URL>();
    	} else {
    		visitedPages.add(url.toString());
    		context = url;
    	}

    	// construct list of all hyperlinks for single page
    	links = new LinkedList<URL>();
    	
    	pageText = "";
    	pos = 0;
    	
    	//System.out.println(numPagesVisited());
    	
    	if (printText)
    		System.out.println("STARTED PARSING " + url + " ...");
    	
    	// begin parsing the page
    	InputSource in = null;
    	in = new InputSource(new InputStreamReader(url.openStream()));
    	parser.parse(in);

    	// parse the entire text and store into index
    	String[] words = pageText.split("\\s+");    	
    	for (String word: words) {
    		if (word.length() > 0) {
    			webIndex.insert(word, urlString, pos);
    			pos++;
    		}
    	}
    	    	
    	if (printText)
    		System.out.println("Text: " + Arrays.toString(words));
    	
    	return links;         
    }

    /**
     * Returns the WebIndex object built by this Crawler.
     *
     * @return a WebIndex object covering all pages crawled by this Crawler.
     */
    public WebIndex getWebIndex() {    	
        return webIndex;
    }

    /**
     * Called when the parser first starts reading a document.
     */
    public void startDocument() {
        if (printText)
        	System.out.println("Start of document");
    }

    /**
     * Called when the parser finishes reading a document.
     */
    public void endDocument() {
        if (printText)
        	System.out.println("End of document");
    }

    /**
     * Called at the start of any tag. You can uncomment the if
     * statement if you want, but it just outputs the Namespace of
     * the element, most of which are in http://www.w3.org/1999/xhtml
     * (for regular HTML).
     */
    public void startElement(String uri, String name, String qName, Attributes atts) {
    	if (tagsAvoid.contains(qName)) {
    		shouldAvoid = true;
    	} else {
    		shouldAvoid = false;
    		
    		if (printText)
    			System.out.println("\nStart element: " + qName);
    		    		
    		// add link if tag is of form <a href=URL>
    		if (qName.equals("a")) {
    			String relative = atts.getValue("href");
    			addLink(relative);
    		}
    	} 
    }
    
    /**
     * Given a String relative path, attempt to add the entire URL to the list of 
     * links to push to the Queue
     * 
     * @param relative - relative path found inside an HTML <a> tag
     */
    private void addLink(String relative) {
    	if (relative == null) {
    		shouldAvoid = true;
    		return;
    	} else {
			// disallow invalid protocol and disallow local links
			if ((relative.indexOf(':') != -1)) {
				shouldAvoid = true;
				return;    				
			}
			
			// disallow certain file extensions
			int dotIndex = 0;
			if ((dotIndex = relative.lastIndexOf('.')) != -1) {
				String ext = relative.substring(dotIndex);
				if (extnsAvoid.contains(ext)) {
					shouldAvoid = true;
					return;
				}
			}		
			
			// remove query (?link=URL) from end of url if present
			int questionMarkIndex = 0;
			if ((questionMarkIndex = relative.lastIndexOf('?')) != -1) {
				relative = relative.substring(0, questionMarkIndex);
			}

			if (printText)
				System.out.println(relative);

			// try to add link
			try {
				URL link = new URL(context, relative);
				links.add(link);
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}    		
    	} 
    }

    /**
     * Called at the end of any tag. You can uncomment the if
     * statement if you want, but it just outputs the Namespace of
     * the element, most of which are in http://www.w3.org/1999/xhtml
     * (for regular HTML).
     */
    public void endElement(String uri, String name, String qName) {
    	//if (!tagsAvoid.contains(qName) && printText)
    	//	System.out.println("End element:   " + qName + "\n");
    }

    /**
     * Called whenever characters are found inside a tag. Note that the parser is not
     * required to return all characters in the tag in a single chunk. Whitespace is
     * also returned as characters.
     */
    public void characters(char ch[], int start, int length) {
    	if (printText)
    		System.out.print("Characters:    \"");
    	
    	// take care of bold, italic, etc. tags separation
    	pageText += " ";
        
        for(int i = start; i < start + length; i++) {
            switch(ch[i]) {
                case '\\':
                    if (printText)
                    	System.out.print("\\\\");
                    break;
                case '"':
                    if (printText)
                    	System.out.print("\\\"");
                    break;
                case '\n':
                    if (printText)
                    	System.out.print("\\n");;
                case '\r':
                    if (printText)
                    	System.out.print("\\r");
                    if (!shouldAvoid)
                    	pageText += " ";
                    break;
                case '\t':
                    if (printText)
                    	System.out.print("\\t");
                    break;      
                default:
                    if (printText)
                    	System.out.print(ch[i]);
                    if (!shouldAvoid) {
                    	if (Character.isLetterOrDigit(ch[i]) || ch[i] == '\'')
                    		pageText += Character.toLowerCase(ch[i]);
                    	else {
                    		pageText += " ";
                    	}
                    }
                    break;
            }
        }

        if (printText)
        	System.out.print("\"\n");        
    }    
    
    /**
     * Return total number of unique web pages crawled.
     * @return number of unique web pages crawled
     */
    public int numPagesVisited() {
    	return visitedPages.size();
    }
}
