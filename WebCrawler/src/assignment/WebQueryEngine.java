package assignment;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import javax.sound.midi.SysexMessage;

public class WebQueryEngine {
	private WebIndex webIndex;
	private ArrayList<String> urlList;
	private ParseTree pTree;	
	
    /**
     * Selects the WebIndex from which answers to queries will be constructed.
     *
     * @param index    the WebIndex this WebQueryEngine should use
     */
    public void useWebIndex(WebIndex index) {
    	webIndex = index;
    	urlList = webIndex.getURLList();
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying
     * the query expression.
     *
     * @param query a query expression
     * @return a Collection of URLs of web pages satisfying the query
     */
    public Collection<URL> query(String query) {
    	if (query == null || query.length() == 0)
    		return new LinkedList<>();
    	
    	pTree = new ParseTree(query, webIndex);
    	
        return evaluateQuery(pTree);
    }
    
    /**
	 * Go through the tree and perform a in-order traversal to evaluate 
	 * each operator and its children. Continue until one list of links satisfying
	 * all conditions is created and return it to the query engine.
	 * @param pTree the parse tree representing the query
	 * @return a LinkedList containing all URLs satisfying the query this tree represents, 
	 * may be empty
	 */
	private LinkedList<URL> evaluateQuery(ParseTree pTree) {
		// create the stack used for evaluation
		Stack<PTreeNode> evalStack = new Stack<PTreeNode>();
		
		Iterator<PTreeNode> treeIter = pTree.iterator();
		while (treeIter.hasNext()) {
			PTreeNode node = treeIter.next();
			Token token = node.t;
			
			// should not encounter null token
			if (token == null)
				return null;

			// evaluate each possible case
			if (token.isOperator()) {
				if (!treeIter.hasNext()) 
					throw new NoSuchElementException();
				
				// grab the two potential children and adjust the stack
				PTreeNode before = evalStack.pop();
				PTreeNode next = treeIter.next();
				
				if (next == null)
					System.err.println("Operator should have two children.");
				
				if (next.t.isOperator()) {
					System.err.println("Operator should not be followed by another operator.");
					return null;
				}				
				
				// perform correct operation
				Set<Integer> indices;				
				switch (token.charToken) {
				case And: indices = evaluateAnd(before, next);  break;
				case Or: indices = evaluateOr(before, next); break;
				default: indices = new HashSet<Integer>(); break;
				}
				
				// push new node containing the result
				evalStack.push(new PTreeNode(new Token("", false), null, null, indices));

			} else if (token.isWord() || token.isPhrase() || token.isNegation()) {
				evalStack.push(node);
			} else {
				System.err.println("Invalid node detected: " + token.toString());
				return null;
			}
		}
		
		if (evalStack.isEmpty()) {
			System.err.println("Tree should reduce to one node.");
			return null;
		} 
		
		// pop final result off of the stack
		PTreeNode result = evalStack.pop();
		Set<Integer> finalIndices = result.urlIndices;
		
		LinkedList<URL> indices = new LinkedList<URL>();
		
		// add each URL that satisfies the query
		Iterator<Integer> iter = finalIndices.iterator();
		while (iter.hasNext()) {
			int urlIndex = iter.next();
			String url = urlList.get(urlIndex);
			try {
				indices.add(new URL(url));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		return indices;
	}		
	
	/**
	 * Helper method that simplifies an expression of the form:
	 * word/phrase & word/phrase
	 * @param left the first Node containing a list of URL indices
	 * @param right the second Node containing a list of URL indices
	 * @return the intersection of the two lists
	 */
	private Set<Integer> evaluateAnd(PTreeNode left, PTreeNode right) {
		if (left.t.isOperator() || right.t.isOperator())
			return null;
		else if (left.urlIndices == null || right.urlIndices == null)
			return null;
		
		Set<Integer> indices = left.urlIndices;
		indices.retainAll(right.urlIndices);
		return indices;
	}
	
	/**
	 * Helper method that simplifies an expression of the form:
	 * word/phrase | word/phrase
	 * @param left the first Node containing a list of URL indices
	 * @param right the second Node containing a list of URL indices
	 * @return the union of the two lists
	 */
	private Set<Integer> evaluateOr(PTreeNode left, PTreeNode right) {
		if (left.t.isOperator() || right.t.isOperator())
			return null;
		else if (left.urlIndices == null || right.urlIndices == null)
			return null;
		
		// explicitly perform union operation
		Set<Integer> indices = new HashSet<Integer>();
		Iterator<Integer> leftIter = left.urlIndices.iterator();
		while (leftIter.hasNext()) {
			indices.add(leftIter.next());
		}
		
		Set<Integer> rightIndices = right.urlIndices;
		
		Iterator<Integer> rightIter = rightIndices.iterator();
		while (rightIter.hasNext()) {
			int urlIndex = rightIter.next();
			if (!indices.contains(urlIndex))
				indices.add(urlIndex);
		}
		return indices;
	}
}
