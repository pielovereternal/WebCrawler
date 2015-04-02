package assignment;

import java.util.ArrayList;
import java.util.regex.*;

public class Tokenizer {
	private static final Pattern PUNCTUATION = Pattern.compile("[\\Q][{},.;?<>%\\E]");
	private String stream;
	private int streamPos;
	
	public enum CharTokens {
		LeftParen, RightParen, And, Or
	}
	
	/**
	 * Initialize variables. Also pre-process the String for implicit AND.
	 * 
	 * @param stream String to be parsed, typically user query.
	 */
	public Tokenizer(String stream) {
		this.stream = stream;
		streamPos = 0;
		sanitizeQuery();
		processString();
	}
	
	/**
	 * Sanitizes the query input, removing punctuation that does not
	 * conform to the word definition. 
	 */
	private void sanitizeQuery() {
		Matcher m = PUNCTUATION.matcher(stream);
		stream = m.replaceAll("");
	}
	
	/**
	 * Pre-processes the query string before tokenization.
	 * Specifically converts implicit AND to explicit AND
	 * and wraps parentheses where appropriate.
	 * <br> Resets stream when finished.
	 */
	private void processString() {		
		// create string builder to make modification easier
		StringBuffer s = new StringBuffer(stream);
		int[] indices;		

		// pre-process the entire string
		while (hasMoreTokens()) {
			// store location of next word
			indices = findSkipNextWord();	
			
			// check if no words left 
			if (indices == null) {
				stream = s.toString();
				streamPos = 0;
				return;
			}			
			
			// handle conversion to explicit AND
			// check every possible terminating case
			Token token = nextToken();
			
			if (token == null) {
				stream = s.toString();
				streamPos = 0;
				return;
			} else if (token.isRightParen() || token.isOperator()) {
				streamPos++;
			} else {
				// need skip unit to detect left paren case
				if (token.isLeftParen()) {
					streamPos--;
				}
				
				skipUnit();
				
				int leftParenPt = indices[0];
				s.insert(leftParenPt, '(');	
				int andInsertPt = indices[1];
				s.insert(andInsertPt + 1, " &"); // need to compensate for insertion of (		
				int rightParenPt = streamPos;
				s.insert(rightParenPt + 3, ')'); // added " &" and (, need to compensate by 3
			} 
		}
		
		stream = s.toString();
		streamPos = 0;
	}
	
	/**
	 * Find the next word, save the index of the first char as found in the stream,
	 * and skip the word upon encountering whitespace.
	 * A word may be encountered inside parentheses, but not as part of a phrase.
	 * 
	 * @return the indices of where the next word starts and ends,
	 * 		   null if no words left or another error is detected
	 */
	private int[] findSkipNextWord() {
		char current = stream.charAt(streamPos);
		int[] positions = new int[2];

		// find beginning of next word
		while (!Character.isLetterOrDigit(current)) {
			// handle skipping phrases
			if (current == '"')
				nextToken();

			// update current
			streamPos++;
			
			if (!hasMoreTokens()) {
				return null;
			}
			
			current = stream.charAt(streamPos);		
		}	

		// query terminates in phrase
		if (!hasMoreTokens()) {
			return null;
		}
		
		// save index
		positions[0] = streamPos;
		
		// skip word
		while (hasMoreTokens()) {
			if (current == ' ' || current == ')')
				break;
			else if (current == '&' || current == '|') {
				streamPos--;
				break;
			}
			current = stream.charAt(streamPos);
			streamPos++;
		}
		
		// save ending index
		positions[1] = streamPos;
		
		return positions;
	}
	
	/**
	 * Skips the next unit, guaranteed to not be an operator.
	 * The unit may be a word, negation, phrase, or parethesized
	 * query. Advances streamPos to where a right parenthesis should
	 * be inserted for processString().
	 */
	private void skipUnit() {
		if (!hasMoreTokens())
			return;
		
		char current = stream.charAt(streamPos);
		
		// skip unit depending on identity of ch
		if (Character.isLetterOrDigit(current) || current == '!' ||
				current == '"') {
			nextToken();
		} else if (current == '(') {
			// parenthesized query case
			int numLeft = 1;
			int numRight = 0;
			while (numLeft != numRight) {
				streamPos++;
				current = stream.charAt(streamPos);
				numLeft += (current == '(') ? 1 : 0;
				numRight += (current == ')') ? 1 : 0;
			}
		} else {
			System.err.println("Unexpected character: " + current + ".");
		}
	}
	
	/**
     * Given the user entered query, 
     * pass tokens to help construct the parse tree
     * @param stream - User query
     * @return valid token, either operator or word
     */
    public Token nextToken() { 
    	if (!hasMoreTokens())
    		return null;
    	char ch = stream.charAt(streamPos);
    	  	
    	// advance pointer
    	streamPos++;    	
    	
    	// consume spaces
    	while (ch == ' ') {
    		ch = stream.charAt(streamPos);
    		streamPos++;
    	}
    	
    	// perform case conversion
    	ch = Character.toLowerCase(ch);
    	
    	// handle special character token
    	if (ch == '(')
    		return new Token(CharTokens.LeftParen);
    	else if (ch == ')')
			return new Token(CharTokens.RightParen); 
    	else if (ch == '&')
    		return new Token(CharTokens.And); 
    	else if (ch == '|')
    		return new Token(CharTokens.Or); 
    	else if (ch == '"') {
    		// phrase query case
    		ArrayList<String> phrase = new ArrayList<String>();
    		String word = "";
    		while (hasMoreTokens() && 
    				(ch = stream.charAt(streamPos)) != '"') {
    			if (ch == ' ' || ch == '"') {
    				phrase.add(word);
    				word = "";
    			} else {
    				word += Character.toLowerCase(ch);
    			}
    			streamPos++;
    		}
    		
    		// never encountered matching quote
    		if (ch != '"') {
    			System.err.println("No matching quote for tokenizing phrase.");
    			return null;
    		}
    		
    		// add last word to phrase and advance pos past last "
    		if (!word.equals(""))
    			phrase.add(word);
    		streamPos++;

    		return new Token(phrase);
    	} else {
    		// word case
    		String word;
    		
    		// check if this word is negated and construct initial word
    		boolean negated = false;
    		if (ch == '!') {
    			negated = true;
    			word = "";
    		} else if (ch == ' ') {
    			word = "";
    		} else {
    			word = ch + "";
    		}
    		
    		for (; streamPos < stream.length(); streamPos++) {
    			// continue appending chars until space or operator
    			char next = stream.charAt(streamPos);
    			if (next == ' ' || next == ')') {
    				break;    				
    			} else if (next == '&' || next == '|') {
    				streamPos--;
    				break;
    			} else {
    				word += Character.toLowerCase(next);
    			}    			
    		}
    		
    		return new Token(word, negated);
    	}
    }
    
    /**
     * Returns the next token if it is an operator. Otherwise,
     * it resets the stream and returns null.
     * 
     * @return - next token if it is an operator, null otherwise
     */
    public Token getOperator() {
    	int beginPos = streamPos;
    	Token token = nextToken();
   
    	if (token != null && token.isOperator()) {
    		return token;
    	} else {
    		streamPos = beginPos;
    		return null;
    	}
    }
    
    /**
     * Checks if the tokenizer is done parsing the String.
     * @return true if end of String has been reached, false otherwise
     */
    public boolean hasMoreTokens() {
    	return (streamPos >= 0 && streamPos < stream.length());
    }
        
    /**
     * Useful for testing the tokenization parsing
     * 
     * @return a List of all the tokens of the query
     */
    public String toString() {
    	String output = "";
    	while (hasMoreTokens()) {
    		Token token = nextToken();
    		output += token.toString() + " ";
    	}
    	output = output.substring(0, output.length() - 1);
    	return output;
    }
}
