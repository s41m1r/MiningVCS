/**
 * Commit distance
 */
package distance;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import model.Change;
import model.Log;

/**
 * @author saimir
 *
 * CommitDistance(f1, f2) = 1 - timesOccurTogether(f1, f2) / 
 * (timesOccurs(f1) + timesOccurs(f2))
 * 
 */
public final class CommitDistance {
	
	public static double commitDistance(String f1, String f2, Log log){
		double timesTogether = timesOccurTogether(f1, f2, log);
		
		if(timesTogether == 0)
			return 1;
		
		double timesAtLeastOne = timesAtLeastOneOccurs(f1, f2, log);
		double cd = 1 - timesTogether / timesAtLeastOne;  
		return cd;
	}
	
	public static int timesOccurs(String filePath, Log log){
		int count = 0;
		Collection<Change> changes = log.getAllChanges();
		for (Iterator<Change> iterator = changes.iterator(); iterator.hasNext();) {
			Change change = iterator.next();
			if(change.getPath().equals(filePath))
				count++;
		}
		return count;
	}

	public static int timesOccurTogether(String filePath1, String filePath2, Log log){
		int count = 0;
		//gives the set of lists of files changes together in a commit 
		Collection<List<Change>> changes = log.getGroupedChanges();
		for (List<Change> listChanges : changes) {
			if(containsFile(listChanges, filePath1) &&
					containsFile(listChanges, filePath2))
				count++;
		}
//		System.out.println(filePath1+" and "+filePath2+" occur together "+count+" times");
		return count;
	}
	
	public static int timesAtLeastOneOccurs(String filePath1, String filePath2, Log log){
		int count = 0;
		//gives the set of lists of files changes together in a commit 
		Collection<List<Change>> changes = log.getGroupedChanges();
		for (List<Change> listChanges : changes) {
			if(containsFile(listChanges, filePath1) ||
					containsFile(listChanges, filePath2))
				count++;
		}
//		System.out.println(filePath1+" and "+filePath2+" occur (at least one) "+count+" times");
		return count;
	}

	public static boolean containsFile(List<Change> changeList, String filePath){
		for (Change change : changeList) {
			if(change.getPath().equals(filePath)){
//				System.out.println(change.getPath()+"="+filePath);
				return true;
			}
		}
		return false;
	}
}
