package assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * WebIndex stores the web pages accessed by the crawler.
 * The index is then used to handle the search queries made by the client.
 * It is implemented as a inverted index, where keywords are mapped 
 * to their relative positions in each document. 
 * 
 * @author Aaron Zou
 *
 */
public class WebIndex extends Index {
    private static final long serialVersionUID = 1L;
       
    private HashMap<String, HashMap<Integer, HashSet<Integer>>> index;
    private ArrayList<String> urlList;
    
    /**
     * Instantiate list of URLs and the index itself.
     */
    public WebIndex() {
    	urlList = new ArrayList<String>();
    	index = new HashMap<String, HashMap<Integer, HashSet<Integer>>>();
    }
    
    /*
     * Return reference to the index, used for testing.
     */
    public HashMap<String, HashMap<Integer, HashSet<Integer>>> getIndex() {
    	return index;
    }
    
    /*
     * Return reference to the url list, used for testing
     */
    public ArrayList<String> getURLList() {
    	return urlList;
    }
       
    /**
     * For a word found in a page by the crawler, insert it into
     * the index. Updates the HashMap, mapping the word to
     * a URL and int position within the page's text. 
     * 
     * @param word - word found by WebCrawler
     * @param URL - URL of page being crawled
     * @param pos - relative position of the word
     */
    public void insert(String word, String URL, int pos) {
    	// add URL to list if not already visited
    	if (!urlList.contains(URL)) 
    		urlList.add(URL);
    	
    	int urlPos = urlList.indexOf(URL);
    	HashMap<Integer, HashSet<Integer>> urlWordPosns;
    	
    	// add new entry for keyword if not already there
    	if (index.get(word) == null) {
    		urlWordPosns = new HashMap<Integer, HashSet<Integer>>();
        	index.put(word, urlWordPosns); 
      	} else {
      		urlWordPosns = index.get(word);
      	}
    	
    	Set<Integer> existingURLs = urlWordPosns.keySet();
    	HashSet<Integer> wordPosns;
    	
    	// check if this word has already been found in this web page
    	if (existingURLs == null || !existingURLs.contains(urlPos)) {
    		wordPosns = new HashSet<Integer>(); 
    		urlWordPosns.put(urlPos, wordPosns); 
    	} else {
    		wordPosns = urlWordPosns.get(urlPos);
    	}
    	
    	wordPosns.add(pos);
    }
    
    /**
     * Returns a HashMap for a given word mapping each URL it is found at to 
     * the relative locations of the word within the page.
     * 
     * @param word - The word being searched for.
     * @return a HashMap<K, V> where K is the URL's index and V is all the relative
     * positions of the word in that page.
     */
    public HashMap<Integer, HashSet<Integer>> getWordPositions(String word) {
    	return index.get(word);
    }
    
    /**
     * Returns a String representation of the index. 
     * Format is: keyword: [[URL posns], [URL posns]]
     */
    public String toString() {
    	String output = "";
    	Iterator<String> keys = index.keySet().iterator();
    	
    	while (keys.hasNext()) {
    		String keyword = keys.next();
    		output += keyword + ": " + index.get(keyword).toString() + "\n";
    	}
    	
    	return output;
    }
    
    
}
