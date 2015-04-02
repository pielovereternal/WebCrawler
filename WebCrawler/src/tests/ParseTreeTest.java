package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import assignment.Index;
import assignment.PTreeNode;
import assignment.ParseTree;
import assignment.Tokenizer;
import assignment.WebIndex;

public class ParseTreeTest {
	private WebIndex webIndex;

	// initialize the index for use for the tests
	private void loadIndex() throws ClassNotFoundException, IOException {
		if (webIndex == null) {
			URL baseURL = new File(System.getProperty("user.dir")).toURI().toURL();
			URL indexURL = new URL(baseURL, "index.db");
			webIndex = (WebIndex)Index.load(indexURL);
		}
	}

	/* Should throw an exception upon encountering second &
	@Test
	public void testConsecutiveOperators() {
		String query = "(word & & word)";
		String expected = "word\n";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		String message = "Expected: " + expected.toString() + "\n" +
				 "Actual:   " + actual.toString();

		assertEquals(message, expected, actual);
	} */

	@Test
	public void testOneQuote() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "\"";
		String expected = "";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testComplexQuery() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "((Little !Mary & had) | \"a little lamb\")";
		String expected = "|\n"
				+ "\t&\n"
				+ "\t\t&\n"
				+ "\t\t\tlittle\n"
				+ "\t\t\t!mary\n"
				+ "\t\thad\n" 
				+ "\ta little lamb\n";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testNegationQuery() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "!walk";
		String expect = "!walk\n";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expect, actual);
	}

	@Test
	public void testImplicitANDQuery() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "bob john";
		String expect = "&\n"
				+ "\tbob\n"
				+ "\tjohn\n";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expect, actual);
	}

	@Test
	public void testPhraseQuery() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "\"September 11 2011 was not my fault\"";
		String expect = "september 11 2011 was not my fault\n";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expect, actual);
	}

	@Test
	public void testAndOrQuery() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "((Calvin & Lin) | (Josh & Keenan))";
		String expect = "|\n"
				+ "\t&\n"
				+ "\t\tcalvin\n" 
				+ "\t\tlin\n" 
				+ "\t&\n"
				+ "\t\tjosh\n"
				+ "\t\tkeenan\n";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expect, actual);
	}

	@Test
	public void testEmptyString() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "";
		String expected = "";

		ParseTree pTree = new ParseTree(query, webIndex);
		String actual = pTree.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testIterator() {
		try {
			loadIndex();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String query = "((Little !Mary & had) | \"a little lamb\")";
		String expected = "little & !mary & had | a little lamb";

		ParseTree pTree = new ParseTree(query, webIndex);
		StringBuilder tempActual = new StringBuilder();

		Iterator<PTreeNode> iter = pTree.iterator();
		while (iter.hasNext()) {
			tempActual.append(iter.next() + " ");
		}
		tempActual.deleteCharAt(tempActual.length() - 1);

		String actual = tempActual.toString();

		assertEquals(expected, actual);
	}
}
