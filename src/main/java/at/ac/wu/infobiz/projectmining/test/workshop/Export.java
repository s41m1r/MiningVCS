/**
 * 
 */
package at.ac.wu.infobiz.projectmining.test.workshop;

import at.ac.wu.infobiz.projectmining.util.DBUtil;

/**
 * @author saimir
 *
 */
public class Export {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String queryString = "select path from File";
		long start = System.currentTimeMillis();
		DBUtil.exportQueryResults(queryString, "filenames.txt");
		System.out.println("Done. Time "+(System.currentTimeMillis()-start));
	}
	
}
