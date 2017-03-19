/**
 * 
 */
package at.ac.wu.infobiz.projectmining.visualization;

import java.util.Collection;
import java.util.Scanner;

import at.ac.wu.infobiz.projectmining.parsing.Change;
import at.ac.wu.infobiz.projectmining.parsing.Log;

/**
 * @author saimir
 *
 */
public final class TreeDistance {

	/**
	 * Algorithm
	 * =========
	 * 
	 * 1. start parsing f1 and f2 at the same time (parse stopword = /)
	 * 2. lca = root
	 * 2. for each token t1 of f1 and t2 of f2
	 * 3. if next token t1 of f1 = next token t2 of f2
	 * 4. 	lca = t1
	 * 5.	remove t1 from f1 and remove t2 from f2
	 * 6. return levelsOf(f1) + levelsOf(f2) / 2*TreeLevel
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static double treeDistance(String f1, String f2, Log log){
		String delimiter = "/";
		Scanner s1 = new Scanner(f1);
		Scanner s2 = new Scanner(f2);
		s1.useDelimiter(delimiter);
		s2.useDelimiter(delimiter);
		String path1 = f1;
		String path2 = f2;
		while(s1.hasNext() && s2.hasNext()){
			
			if(!s1.next().equals(s2.next()))
				break;

			path1 = removeFirstNode(path1);
			path2 = removeFirstNode(path2);
		}
		s1.close();
		s2.close();
		
		//case of same file:
		if(path1.equals(path2))
			return 0;
		
		return (double)(levelOf(path1)+levelOf(path2)-1) / (2*treeLevel(log));
	}
	
	/**
	 * 
	 * @param f1  a/b/c/d
	 * @param f2  a/b/f 
	 * @return
	 */
	
	public static int lca(String f1, String f2){
		String prefix = greatestCommonPrefix(f1, f2);
		int depthF1 = f1.split("/").length;
		int depthF2 = f2.split("/").length;
		int depthPrefix = prefix.split("/").length;
		return depthF1 + depthF2 - 2*depthPrefix;
	}
	
	public static String greatestCommonPrefix(String a, String b) {
	    int minLength = Math.min(a.length(), b.length());
	    for (int i = 0; i < minLength; i++) {
	        if (a.charAt(i) != b.charAt(i)) {
	            return a.substring(0, i);
	        }
	    }
	    return a.substring(0, minLength);
	}
	
	public static String removeFirstNode(String filePath) {
		return filePath.substring(filePath.indexOf("/")+1);
	}
	
	/**
	 * Returns the number of levels of the current file path t1
	 * @param t1
	 * @return
	 */
	public static int levelOf(String t1){
		Scanner s = new Scanner(t1);
		s.useDelimiter("/");
		int lvCount = 0;
		while (s.hasNext()){
			s.next();
			lvCount++;
		}
		s.close();
		return lvCount;
	}
	
	public static int treeLevel(Log log){
		int maxL = 0;
		int curL = 0;
		Collection<Change> changes = log.getAllChanges();
		for (Change change : changes) {
			curL = levelOf(change.getPath());
			if(maxL < curL)
				maxL = curL;
		}
		return maxL;
	}

}
