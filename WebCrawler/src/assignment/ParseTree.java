package assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public class ParseTree {
	private WebIndex webIndex;
	private PTreeNode root;
	private Tokenizer tokenizer;	
	
	public ParseTree(String query, WebIndex webIndex) {
		this.webIndex = webIndex;
		this.tokenizer = new Tokenizer(query);
		root = parseQuery();
	}
	
	/**
	 * Recursively creates the tree using a recursive descent parser.
	 * Interior nodes are binary operators. The leaves may be a negation,
	 *  a phrase, or a word.
	 */
	private PTreeNode parseQuery() {
		Token t = tokenizer.nextToken();
		
		if (t == null) {
			return null;
		}
		
		if (t.isLeftParen()) {
			PTreeNode left = parseQuery();
			Token op = tokenizer.getOperator();
			PTreeNode right = parseQuery();			
			tokenizer.nextToken();
			return new PTreeNode(op, left, right);
		} else if (t.isNegation()) {
			return makeNegation(t);
		} else if (t.isPhrase()) {
			return makePhrase(t);
		} else if (t.isWord()) {
			return makeWord(t);
		} else {
			System.err.println("Error parsing.");
			System.err.println("Encountered unexpected token: " + t.toString());
			throw new IllegalStateException();
		}
	}
	
	/**
	 * Creates a PTreeNode representing a word. Also finds all 
	 * URL indices that contain the word and stores the data in the node. 
	 * @param t the Token containing a word
	 * @return the node representing the word and its URL indices
	 */
	private PTreeNode makeWord(Token t) {
		if (!t.isWord()) {
			throw new IllegalArgumentException("This token must be a word.");
		}
		
		if (webIndex == null) {
			System.err.println("Index does not exist.");
			return null;
		}
		
		// grab all indices for this word
		HashMap<Integer, HashSet<Integer>> wordPositions = 
				webIndex.getWordPositions(t.wordToken);
		// check if there is no entry
		if (wordPositions == null)
			return new PTreeNode(t, null, null, new HashSet<Integer>());
		else {
			// initialize node with the token and URL indices
			return new PTreeNode(t, null, null, wordPositions.keySet());
		}
	}
	
	/**
	 * Creates a PTreeNode representing a negation. Also finds all URL indices that
	 * contain the word negation and stores the data in the node.
	 * @param t the Token containing the negation
	 * @return the node representing the negation and its URL indices
	 */
	private PTreeNode makeNegation(Token t) {
		if (!t.isNegation())
			throw new IllegalArgumentException("This token must be a word.");
		if (webIndex == null) {
			System.err.println("Index does not exist.");
			return null;
		}

		// temporarily make the negation node act as a word node
		t.negated = false;
		PTreeNode negation = makeWord(t);
		t.negated = true;
		
		// construct negation indices
		Set<Integer> currentIndices = negation.urlIndices;
		Set<Integer> negatedIndices = new HashSet<Integer>();

		int numURLs = webIndex.getURLList().size();
		
		for (int num = 0; num < numURLs; num++) {
			if (!currentIndices.contains(num)) {
				negatedIndices.add(num);
			}
		}		
		
		negation.urlIndices = negatedIndices;

		// initialize node with the token and URL indices
		return negation;
	}

	/**
	 * Creates a PTreeNode representing a phrase. Also finds all
	 * URL indices that contain that phrase and stores the data in the node.
	 * @param t the Token containing a phrase
	 * @return the node representing the phrase and its URL indices
	 */
	private PTreeNode makePhrase(Token t) {
		if (!t.isPhrase() || t.phraseToken.size() == 0) {
			throw new IllegalArgumentException("This token must be a nonempty phrase.");
		}
		
		// grab sequence of words
		ArrayList<String> words = t.phraseToken;
		
		// check the phrase is non-empty
		if (words == null || words.size() == 0) {
			return new PTreeNode(t, null, null, new HashSet<Integer>());
		}
		
		// grab indices of first word
		HashMap<Integer, HashSet<Integer>> positions = webIndex.getWordPositions(words.get(0));
		if (positions == null || positions.keySet() == null) {
			return new PTreeNode(t, null, null, new HashSet<Integer>());
		}
		
		Set<Integer> indices = webIndex.getWordPositions(words.get(0)).keySet();
		
		// check indices are non-empty
		if (indices == null)
			return new PTreeNode(t, null, null, new HashSet<Integer>());
		else if (indices.isEmpty()) {
			return new PTreeNode(t, null, null, new HashSet<Integer>());
		}
		
		// continually take intersection to find subset of indices that contain all words in phrase
		for (int wordIndex = 1; wordIndex < words.size(); wordIndex++) {
			HashMap<Integer, HashSet<Integer>> wordPosns = 
					webIndex.getWordPositions(words.get(wordIndex));
			// check that this word is in the WebIndex
			if (wordPosns == null)
				return new PTreeNode(t, null, null, new HashSet<Integer>());
			else {
				Set<Integer> tempIndices = webIndex.getWordPositions(words.get(wordIndex)).keySet();
				indices.retainAll(tempIndices);
			}
		}
		
		// store all indices corresponding to URLs that contain all the words in the 
		// correct order
		Set<Integer> phraseIndices = new HashSet<Integer>();
		
		// for each URL index, determine if it contains the phrase
		for (int index: indices) {
			// store first set of positions
			Set<Integer> positionSet = webIndex.getWordPositions(words.get(0)).get(index);
			
			// go through and take positions of each successive word for this URL page		
			for (int ind = 1; ind < words.size(); ind++) {
				// decrement each successive position set by ind 
				Set<Integer> tempSet = webIndex.getWordPositions(words.get(ind)).get(index);
				Set<Integer> adjustedSet = new HashSet<Integer>();
				Iterator<Integer> posnIter = tempSet.iterator();
								
				while (posnIter.hasNext()) {
					adjustedSet.add(posnIter.next() - ind);
				}

				// continually take intersection of main set with each successive adjusted set
				positionSet.retainAll(adjustedSet);
			}
			
			if (!positionSet.isEmpty()) {
				phraseIndices.add(index);
			}
			
		}
				
		return new PTreeNode(t, null, null, phraseIndices);
	}
	
	/**
	 * Returns an iterator that performs an in-order traversal of the 
	 * current state of the parse tree.
	 * The tree does not support any insert or removal operations, so checks for
	 * concurrent modifications are not needed.
	 * 
	 * @return an iterator valid for the current tree
	 */
	public Iterator<PTreeNode> iterator() {
		return new Iterator<PTreeNode>() {
			PTreeNode next = null;
			Stack<PTreeNode> nodeStack = null;
			
			@Override
			public boolean hasNext() {
				if (nodeStack == null || !nodeStack.isEmpty()) {
					// make sure that the root is initialized, meaning there is a
					// tree to be iterated over
					if (root != null) {
						return true;
					} else {
						return false;
					}
				} else {
					// the iteration has completed, no more next nodes
					return false;
				}
			}
			
			// Initialize the stack used and fill with tree's values
			private void initStack(PTreeNode current) {
				if (current == null) 
					return;
				
				// push right subtree, then root, then left subtree
				// all are pushed in decreasing order
				initStack(current.right);
				nodeStack.push(current);
				initStack(current.left);	
			}
			
			@Override
			public PTreeNode next() {
				// initialize stack if not already initialized
				if (nodeStack == null) {
					nodeStack = new Stack<PTreeNode>();
					initStack(root);
				}
				if (!hasNext()) {
					throw new NoSuchElementException();
				} else {
					next = nodeStack.pop();
					return next;
				}						
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};		
	}
	
		
	/** 
	 * Print a pre-order traversal of the tree, showing the 
	 * child links
	 */ 
	@Override
	public String toString() {
		// initialize stacks for pre-order traversal
		Stack<PTreeNode> nodeStack = new Stack<PTreeNode>();
		nodeStack.add(root);
		Stack<Integer> depthStack = new Stack<Integer>();
		depthStack.add(0);
		
		String output = "";

		while (!nodeStack.isEmpty()) {
			// pop current values from both stacks
			PTreeNode currentNode = nodeStack.pop();
			int currentDepth = depthStack.pop();
			
			// check if treap is empty (only the root can be null)
			if (currentNode == null) {
				return "";
			}
			
			// if non-null children exist, push them onto the stack,
			// right node first, then left node,
			// so that a preorder traversal is conducted.
			if (currentNode.right != null) {
				nodeStack.add(currentNode.right);
				depthStack.add(currentDepth + 1);
			}
			
			if (currentNode.left != null) {
				nodeStack.add(currentNode.left);
				depthStack.add(currentDepth + 1);
			}
				
			// add the current node to the string representation
			String nodeString = "";
			for (int i = 0; i < currentDepth; i++) {
				nodeString += "\t";
			}
			nodeString += currentNode.toString() + "\n";
			
			output += nodeString;
		}
		return output;
	}
}
