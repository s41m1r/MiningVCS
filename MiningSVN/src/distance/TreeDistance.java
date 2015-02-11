/**
 * 
 */
package distance;

import java.util.Scanner;

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
	public static int treeDistance(String f1, String f2){
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
		
		return levelsOf(path1)+levelsOf(path2)-1;
	}

	public static String removeFirstNode(String filePath) {
		return filePath.substring(filePath.indexOf("/")+1);
	}

	public static int levelsOf(String t1){
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
}
