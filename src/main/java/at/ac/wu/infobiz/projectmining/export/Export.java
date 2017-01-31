/**
 * 
 */
package at.ac.wu.infobiz.projectmining.export;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import at.ac.wu.infobiz.projectmining.util.DBUtil;

/**
 * @author Saimir
 *
 */
public class Export {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		exportFileStories("smsr");
	}

	public static void exportFileStories(String fromDB) {
		long start = System.currentTimeMillis();
		Session session = DBUtil.connectTo(fromDB);
		List<String> allfiles = getAllFilesFrom(session, fromDB);
		for (String file : allfiles) {
			extractStory(session, fromDB, file);
		}
		DBUtil.disconnect(session);
		System.out.println("Extracted "+allfiles.size()+ " stories in "+ (System.currentTimeMillis()-start)/1000.0+ " sec.");
	}

	public static void extractStory(Session session, String dbname, String file) {

//		String queryString = "SELECT `Commit`.*, `User`.`name` "
//				+ " FROM `File`, `FileAction`, `Commit`, `User` "
//				+ " WHERE `File`.`path` = "+ "'" + file + "'" 
//				+ " AND `FileAction`.`commit_id` = `Commit`.`id`"
//				+ "	AND `FileAction`.`file_path` = `File`.`path`"
//				+ "	AND `User`.`id` = `Commit`.`user_id`"
//				+ " ORDER BY `Commit`.`timeStamp` ASC ";
		
		String queryString = "SELECT `Commit`.`id`, `Commit`.`comment`, `Commit`.`timeStamp`, "
				+ " sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, "
				+ " sum(linesAdded)/(sum(linesAdded)+sum(linesRemoved)) as PercentAdded, "
				+ " (sum(linesAdded)-sum(linesRemoved))/(sum(linesAdded)+sum(linesRemoved)) as DeltaIndex, "
				+ " `User`.`name` "
				+ " FROM `File`, `Edit`, `Commit`, `User`"
				+ " WHERE `File`.`path` = "+"'"+file+"'"
				+ " AND `Edit`.`commit_id` = `Commit`.`id` "
				+ " AND `Edit`.`file_path` = `File`.`path` "
				+ " AND `User`.`id` = `Commit`.`user_id` "
				+ " GROUP BY `Edit`.`id`"
				+ " ORDER BY `Commit`.`timeStamp` ASC"; 
		
//		System.out.println("Executing query: "+queryString);
		
		SQLQuery q = session.createSQLQuery(queryString);
		
		List<Object[]> results = q.list();
		
		DBUtil.toCSV(dbname, file,results);
	}

	public static List<String> getAllFilesFrom(Session session, String dbname) {
		List<String> allfiles = null;
		String queryString = "select * from File";
		SQLQuery q = session.createSQLQuery(queryString);
		
		allfiles = q.list();
		
//		for (String file : allfiles) {
//			System.out.println(file);
//		}
		return allfiles;
	}

	public static void exportAllFiles(String outfile) {
		String queryString = "select path from File";
		long start = System.currentTimeMillis();
		DBUtil.exportQueryResults(queryString, outfile);
		System.out.println("Done. Time "+(System.currentTimeMillis()-start));
	}
	
}
