/**
 * 
 */
package at.ac.wu.infobiz.projectmining.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import at.ac.wu.infobiz.projectmining.util.DBUtil;

/**
 * @author Saimir
 *
 */
public class ExportStories {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dbname = null;
		int i=0;
//		for (String string : args) {
//			System.out.println(i+++" "+string);
//		}
		if(args.length == 1)
			dbname = args[0];
//		dbname = JOptionPane.showInputDialog("String dbname?");
//		dbname = "node";
		System.out.println(dbname);
		if(dbname != null)
			exportFileStories(dbname);
		//exportFileStories("smsr");
	}

	public static void exportFileStories(String fromDB) {
		long start = System.currentTimeMillis();
		Session session = DBUtil.connectTo(fromDB);
		List<String> allfiles = getAllFilesFrom(session, fromDB);
		int i = 0;
//		String codename = fromDB+"-stories/f";
		String codename = "f";
		Map<String, String> namesMap = new TreeMap<String, String>();
		for (String file : allfiles) {
			String newName = codename+i+++".csv";
			extractStory(session, file, fromDB+"-stories/"+newName);
			namesMap.put(newName, file);
		}
		DBUtil.disconnect(session);
		DBUtil.toCSV2(fromDB+"-namesMap/namesMap.csv", 
				new String[]{"coded","original name"}, listofObjectArrayFrom(namesMap), '\t');
//		printNamesMap(namesMap, fromDB+"/namesMap/namesMap.csv");
		System.out.println("Results written into directory "+fromDB+"-stories");
		System.out.println("Extracted "+allfiles.size()+ " stories in "+ (System.currentTimeMillis()-start)/1000.0+ " sec.");
	}

	private static List<String[]> listofObjectArrayFrom(Map<String, String> namesMap) {
		Iterator<String> it = namesMap.keySet().iterator();
		List<String[]> rows = new ArrayList<String[]>();
		while (it.hasNext()) {
			String string = it.next();
			String[] r = new String[2];
			r[0] = string;
			r[1] = namesMap.get(string);

			rows.add(r);
		}
		return rows;
	}

	public static void extractStory(Session session, String file, String newName) {

//		String queryString = "SELECT `Commit`.*, `User`.`name` "
//				+ " FROM `File`, `FileAction`, `Commit`, `User` "
//				+ " WHERE `File`.`path` = "+ "'" + file + "'" 
//				+ " AND `FileAction`.`commit_id` = `Commit`.`id`"
//				+ "	AND `FileAction`.`file_path` = `File`.`path`"
//				+ "	AND `User`.`id` = `Commit`.`user_id`"
//				+ " ORDER BY `Commit`.eStamp` ASC ";
		
//		String queryString = "SELECT `Commit`.`id`, `Commit`.`comment`, `Commit`.`timeStamp`, "
//				+ " sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, "
//				+ " sum(linesAdded)/(sum(linesAdded)+sum(linesRemoved)) as PercentAdded, "
//				+ " (sum(linesAdded)-sum(linesRemoved))/(sum(linesAdded)+sum(linesRemoved)) as DeltaIndex, "
//				+ " `User`.`name` "
//				+ " FROM `File`, `Edit`, `Commit`, `User`"
//				+ " WHERE `File`.`path` = "+"'"+file+"'"
//				+ " AND `Edit`.`commit_id` = `Commit`.`id` "
//				+ " AND `Edit`.`file_path` = `File`.`path` "
//				+ " AND `User`.`id` = `Commit`.`user_id` "
//				+ " GROUP BY `Edit`.`id`"
//				+ " ORDER BY `Commit`.`timeStamp` ASC"; 
		
		String queryString = "SELECT GROUP_CONCAT(`Commit`.`comment` SEPARATOR ' § ') as Comments, DATE(`Commit`.`timeStamp`) as Date, sum(linesAdded) as TotalLinesAdded, sum(linesRemoved) as TotalLinesRemoved, sum(linesAdded+linesRemoved) TotalChangeInTheDay, sum(linesAdded-linesRemoved) TotalDiffInTheDay, sum(DISTINCT `FileAction`.`totalLines`) as LinesUntilThisDay, GROUP_CONCAT(`User`.`name` SEPARATOR ' § ') as Users "
				+ "FROM `File`, `Edit`, `FileAction`,`Commit`, `User` "
				+ "WHERE `File`.`path` = "+"'"+file+"'"
				+ "AND `Edit`.`commit_id` = `Commit`.`id` "
				+ "AND `Edit`.`file_path` = `File`.`path` "
				+ "AND `User`.`id` = `Commit`.`user_id` "
				+ "AND `FileAction`.`file_path` = `File`.`path`"
				+ "AND `FileAction`.`commit_id` = `Commit`.`id` "
				+ "GROUP BY Date "
				+ "ORDER By Date ASC"; 
		
//		System.out.println("Executing query: "+queryString);
		
		SQLQuery q = session.createSQLQuery(queryString);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.list();
		
		String[] header = {"Comments","Date","TotalLinesAdded","TotalLinesRemoved", "TotalChangeInTheDay", "TotalDiffInTheDay", "LinesUntilThisDay", "Users"};
		DBUtil.toCSVLongFileNames(newName, header, results, '\t');
	}

	@SuppressWarnings("unchecked")
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
