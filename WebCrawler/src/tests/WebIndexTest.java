package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Test;

import assignment.WebIndex;

public class WebIndexTest {
		
	@Test
	public void testInsertOne() {
		String word = "chicken";
		String url = "http://www.chicken.com";
		int pos = (int)(Math.random() * Integer.MAX_VALUE);
		
		// insert word in
		WebIndex webIndex = new WebIndex();
		webIndex.insert(word, url, pos);
		
		// extract the entry for this url
		HashMap<String, HashMap<Integer, HashSet<Integer>>> index = webIndex.getIndex();
		HashMap<Integer, HashSet<Integer>> urlWordPosns = index.get(word);
		
		// check value for first HashMap was created
		if (urlWordPosns == null) {
			fail("Keyword entry does not exist.");
		}

		// check that the list of urls is created
		ArrayList<String> urlList = webIndex.getURLList();
		
		// check that HashSet of positions was created
		if (urlList == null || urlList.size() == 0) {
			fail("List does not reflect added URL.");
		}
		
		// check that correct position was added for this word
		HashSet<Integer> expected = new HashSet<Integer>();
		expected.add(pos);
		HashSet<Integer> actual = urlWordPosns.get(urlList.indexOf(url));
		
		String message = "Expected: " + expected.toString() + 
						 "\nActual: " + actual.toString(); 
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testInsertDuplicates() {
		// create two duplicate words found in different places
		String[] words = {"chicken", "chicken"};
		String url = "http://www.chicken.com";
		int[] posns = {(int)(Math.random() * Integer.MAX_VALUE),
				(int)(Math.random() * Integer.MAX_VALUE)};
				
		// insert words in
		WebIndex webIndex = new WebIndex();
		for (int i = 0; i < words.length; i++)
			webIndex.insert(words[i], url, posns[i]);
		
		// extract the entry for this url
		HashMap<String, HashMap<Integer, HashSet<Integer>>> index = webIndex.getIndex();
		HashMap<Integer, HashSet<Integer>> urlWordPosns = index.get(words[0]);
		
		// check value for first HashMap was created
		if (urlWordPosns == null) {
			fail("Keyword entry does not exist.");
		}

		// check that the list of urls is created
		ArrayList<String> urlList = webIndex.getURLList();
		
		if (urlList == null || urlList.size() == 0) {
			fail("List does not reflect added URL.");
		}
		
		// check that correct position was added for this word
		// if positions are also duplicates, set takes care of that
		HashSet<Integer> expected = new HashSet<Integer>();
		for (int pos: posns) {
			expected.add(pos);
		}
		
		HashSet<Integer> actual = urlWordPosns.get(urlList.indexOf(url));
		
		String message = "Expected: " + expected.toString() + 
						 "\nActual: " + actual.toString(); 
		
		assertTrue(message, expected.equals(actual));
	}
	/*
	@Test
	public void testStressInsert() {
		int numWords = 10000;
		int numURLs = numWords / 10;
		
		// initialize list of words
		HashSet<String> words = new HashSet<String>();
		String[] tempWords = generateRandomWords(numWords);
		for (String word: tempWords) {
			words.add(word);
		}
		
		// initialize list of urls
		String[] urls = generateRandomWords(numURLs);
		
		// initialize positions
		int[] posns = new int[numWords];
		for (int i = 0; i < posns.length; i++) {
			posns[i] = (int)(Math.random() * numURLs);
		}

		// insert words in
		WebIndex webIndex = new WebIndex();
		for (int i = 0; i < words.length; i++)
			webIndex.insert(words[i], urls[i], posns[i]);
		

		// check that the list of urls is created
		ArrayList<String> urlList = WebIndex.urlList;
		
		if (urlList == null || urlList.size() == 0) {
			fail("List does not reflect added URL.");
		}

		// extract the entry for each url
		HashMap<String, HashMap<Integer, HashSet<Integer>>> index = webIndex.getIndex();

		Iterator<String> iter = index.keySet().iterator();
		
		while (iter.hasNext()) {
			String keyword = iter.next();
			
			HashMap<Integer, HashSet<Integer>> urlWordPosns = index.get(keyword);

			// check value for first HashMap was created
			if (urlWordPosns == null) {
				fail("Keyword entry does not exist.");
			}

			// check that correct position was added for this word
			// if positions are also duplicates, set takes care of that
			HashSet<Integer> expected = new HashSet<Integer>();
			for (int pos: posns) {
				expected.add(pos);
			}

			HashSet<Integer> actual = urlWordPosns.get(urlList.indexOf(urls));

			String message = "Expected: " + expected.toString() + 
					"\nActual: " + actual.toString(); 

			assertTrue(message, expected.equals(actual));
		}		
	}
	*/
	@Test
	public void testRuntime() {
		int numWords = 100000;
		
		// initialize list of words
		String[] words = generateRandomWords(numWords);
				
		// initialize list of urls
		String[] urls = generateRandomWords(numWords);
				
		// initialize positions
		int[] posns = new int[numWords];
		for (int i = 0; i < posns.length; i++) {
			posns[i] = (int)(Math.random() * numWords / 10);
		}
		
		long startTime = System.nanoTime();
		WebIndex webIndex = new WebIndex();
		
		// insert words in
		for (int i = 0; i < words.length; i++)
			webIndex.insert(words[i], urls[i], posns[i]);
		
		long elapsedTime = System.nanoTime() - startTime;
		elapsedTime /= 1e9;
		
		System.out.println("Input size: " + numWords + 
				"\nElapsed time: " + elapsedTime + " (s)");
	}
	
	// creates arbitrary number of random "words"
	private static String[] generateRandomWords(int size) {
		double duplicateFactor = 0.8;
		
		String[] words = new String[size];
		for (int i = 1; i < size; i++) {
			// allow for duplicates
			double isDuplicate = Math.random();
			
			if (isDuplicate > duplicateFactor) {
				words[i] = words[i - 1];
				continue;
			}			
			
			String word = "";
			int length = (int)(Math.random() * 30);
			
			for (int j = 0; j < length; j++) {
				word += (char)((int)(Math.random() * 26) + 'a');
			}
			
			words[i] = word;
		}
		
		return words;
	}
}
