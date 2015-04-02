package assignment;

import java.util.Set;

/**
 *  Defines a single node in the parse tree.
 *  Contains flag to check if it has already been evaluated,
 *  left and right children, its token, and the list of URL indices if 
 *  they have already been calculated.
 * @author Aaron
 *
 */	
public class PTreeNode {
	PTreeNode left, right;
	Set<Integer> urlIndices;
	Token t;

	public PTreeNode(Token t, PTreeNode left, PTreeNode right) {
		this.t = t;
		this.left = left;
		this.right = right;
	}

	public PTreeNode(Token t, PTreeNode left, PTreeNode right,
			Set<Integer> indices) {
		this(t, left, right);
		urlIndices = indices;
	}		

	public String toString() {
		return t.toString();
	}
}

