package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import assignment.Tokenizer;

public class TokenizerTest {

	@Test
	public void testSanitize() {
		String query  = "<>{}[]hello???<%";
		Tokenizer tokenizer = new Tokenizer(query);
		assertEquals("hello", tokenizer.toString());
	}
	
	@Test
	public void testTokenizer() {
		String query = "((Little !Mary & had) | \"a little lamb\")";
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("(");
		expected.add("(");
		expected.add("little");
		expected.add("&");
		expected.add("!mary");
		expected.add(")");
		expected.add("&");
		expected.add("had");
		expected.add(")");
		expected.add("|");
		expected.add("a little lamb");
		expected.add(")");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}

		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual:   " + actual.toString();
		
		System.out.println(message);
		
		assertTrue(message, expected.equals(actual));
	}

	@Test
	public void testImplicitAndParenthesized() {
		String query = "mary (had & lamb)";

		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("mary");
		expected.add("&");
		expected.add("(");
		expected.add("had");
		expected.add("&");
		expected.add("lamb");
		expected.add(")");
		expected.add(")");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}

		String message = "Expected: " + expected.toString() + "\n" +
				"Actual: " + actual.toString();

		assertTrue(message, expected.equals(actual));
	}

	@Test
	public void testAnotherImplicitAnd() {
		String query = "mary \"had a little lamb\"";
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("mary");
		expected.add("&");
		expected.add("had a little lamb");
		expected.add(")");
		
		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
				 "Actual: " + actual.toString();
		
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testNegationImplicitAnd() {
		String query = "Mary !had";

		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("mary");
		expected.add("&");
		expected.add("!had");
		expected.add(")");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testNegation() {
		String query = "!Mary";

		ArrayList<String> expected = new ArrayList<String>();
		expected.add("!mary");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testPhrase() {
		String query = "\"September 11 2011\"";
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("september 11 2011");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testAndOrImplicitAnd() {
		String query = "(john smith | (smith & bob))";
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("(");
		expected.add("john");
		expected.add("&");
		expected.add("smith");
		expected.add(")");
		expected.add("|");
		expected.add("(");
		expected.add("smith");
		expected.add("&");
		expected.add("bob");
		expected.add(")");
		expected.add(")");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testImplicitAnd() {
		String query = "john smith";
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("john");
		expected.add("&");
		expected.add("smith");
		expected.add(")");

		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}

	@Test
	public void testAndTokenization() {
		String query = "(rosencrantz & guildenstern)";
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("rosencrantz");
		expected.add("&");
		expected.add("guildenstern");
		expected.add(")");
		
		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testOrTokenization() {
		String query = "(rosencrantz | guildenstern)";
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("rosencrantz");
		expected.add("|");
		expected.add("guildenstern");
		expected.add(")");
		
		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testAndOrTokenization() {
		String query = "((wealth & fame) | happiness)";
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("(");
		expected.add("(");
		expected.add("wealth");
		expected.add("&");
		expected.add("fame");
		expected.add(")");
		expected.add("|");
		expected.add("happiness");
		expected.add(")");
		
		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	
	
	@Test
	public void testEmptyString() {
		String query = "";
		ArrayList<String> expected = new ArrayList<String>();
		
		Tokenizer tokenizer = new Tokenizer(query);
		ArrayList<String> actual = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			actual.add(tokenizer.nextToken().toString());
		}
		
		String message = "Expected: " + expected.toString() + "\n" +
						 "Actual: " + actual.toString();
				
		assertTrue(message, expected.equals(actual));
	}
	
	@Test
	public void testHasMoreTokens() {
		String query = "((wealth & fame) | happiness)";
		int expected = 9;
		int actual = 0;
		
		Tokenizer tokenizer = new Tokenizer(query);
		while (tokenizer.hasMoreTokens()) {
			tokenizer.nextToken();
			actual++;
		}
		
		if (tokenizer.hasMoreTokens()) {
			fail("There should be no more tokens remaining.");
		}
		
		assertEquals(expected, actual);
	}
}
