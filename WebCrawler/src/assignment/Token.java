package assignment;

import java.util.ArrayList;

import assignment.Tokenizer.CharTokens;

/**
 * Internal representation of a token.
 * Can be exactly one character, word, negated word, or phrase.
 * Contains basic methods for testing identity.
 */
public class Token {
	boolean negated = false;
	CharTokens charToken = null;
	String wordToken = null;			
	ArrayList<String> phraseToken = null;

	public Token(CharTokens token) {
		charToken = token;		
	}

	public Token (String word, boolean negated) {
		this.wordToken = word;
		this.negated = negated;
	}

	public Token (ArrayList<String> phrase) {
		this.phraseToken = phrase;
	}

	public boolean isLeftParen() {
		if (charToken == null)
			return false;
		else if (charToken == CharTokens.LeftParen)
			return true;
		return false;
	}
	
	public boolean isRightParen() {
		if (charToken == null)
			return false;
		else if (charToken == CharTokens.RightParen)
			return true;
		return false;
	}
	
	public boolean isNegation() {
		return (wordToken != null) && negated;
	}
	
	public boolean isWord() {
		return (wordToken != null) && !negated;
	}
	
	public boolean isPhrase() {
		return (phraseToken != null);
	}
	
	/**
	 * Tests if the contained token is an operator:
	 * (&, |)
	 * @return true if this token is an operator, false otherwise
	 */
	public boolean isOperator() {
		if (charToken == null) {
			return false;
		} else if (charToken == CharTokens.And 
				|| charToken == CharTokens.Or) {
			return true;
		} 			
		return false;			
	}
	
	/**
	 * Return String representation of the token, depending
	 * on what kind of token it is.
	 */
	public String toString() {
		if (isWord()) {
			return wordToken;
		} else if (isNegation()) {
			return "!" + wordToken;
		}
			else if (isPhrase()) {
			String output = "";
			for (int i = 0; i < phraseToken.size(); i++) {
				output += phraseToken.get(i);
				if (i != phraseToken.size() - 1) {
					output += " ";
				}
			}		
			return output;
		} else {
			switch (charToken) {
			case And: return "&";
			case Or: return "|";
			case LeftParen: return "(";
			case RightParen: return ")";
			default: return "";
			}
		}
	}
}
