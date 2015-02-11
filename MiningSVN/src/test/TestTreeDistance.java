/**
 * 
 */
package test;

import distance.TreeDistance;

/**
 * @author saimir
 *
 */
public class TestTreeDistance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * LaTeX/proposal.tex
		 * LaTeX/sections/JKU.tex
		 */
		System.out.println(TreeDistance.levelsOf("LaTeX/sections/JKU.tex"));
		System.out.println(TreeDistance.removeFirstNode("LaTeX/sections/JKU.tex"));
		System.out.println(TreeDistance.treeDistance("a/b/d", "a/b/e/f"));
	}

}
