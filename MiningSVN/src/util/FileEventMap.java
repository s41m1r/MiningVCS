/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Change;
import model.Event;
import model.Log;
import model.LogEntry;

import org.joda.time.DateTime;

/**
 * @author Saimir Bala
 *
 */
public abstract class FileEventMap {

	public static Map<String, List<Event>> buildHistoricalFileEventMap(Log log) {
		Map<String, List<Event>> fem = new HashMap<String, List<Event>>();
		Collection<LogEntry> entries = log.getAllEntries();

		for (LogEntry logEntry : entries) {
			String author = logEntry.getAuthor();
			DateTime date = logEntry.getDate();
			String commitID = logEntry.getStartingToken()+"";
			String comment = logEntry.getComment();
			List<Change> changeList = logEntry.getChangeList();
			//	      add each event into the list
			for (Change change : changeList) {
				String filePath = change.getPath();
				String action = change.getAction();
				Event e = new Event(date, filePath, action, author, commitID, comment);
				if(fem.containsKey(filePath))
					fem.get(filePath).add(e);
				else{
					List<Event> eList = new ArrayList<Event>();
					eList.add(e);
					fem.put(filePath, eList);
				}
			}
		}
		return fem;
	}

	public static Map<String, List<Event>> buildFileEventMap(Log log) {
		Map<String, List<Event>> fem = new HashMap<String, List<Event>>();
		Collection<LogEntry> entries = log.getAllEntries();

		for (LogEntry logEntry : entries) {
			String author = logEntry.getAuthor();
			DateTime date = logEntry.getDate();
			String commitID = logEntry.getStartingToken()+"";
			String comment = logEntry.getComment();
			List<Change> changeList = logEntry.getChangeList();
			//	      add each event into the list
			for (Change change : changeList) {
				String filePath = change.getPath();
				String action = change.getAction();
				//	      	if a file is deleted I have to remove it from the mapping
				if(action.equals(Log.DELETED)){
					if(fem.containsKey(filePath)){
						fem.remove(filePath);
						continue; //do not continue with the following instructions
					}
				}
				Event e = new Event(date, filePath, action, author, commitID, comment);
				if(fem.containsKey(filePath))
					fem.get(filePath).add(e);
				else{
					List<Event> eList = new ArrayList<Event>();
					eList.add(e);
					fem.put(filePath, eList);
				}
			}
		}
		return fem;
	}
	
	public static Map<String, List<String>> buildCommitFileMap(Log log){
		HashMap<String, List<String>> res = new HashMap<String, List<String>>();
		Collection<LogEntry> entries = log.getAllEntries();
		for (LogEntry logEntry : entries) {
			String comment = logEntry.getStartingToken();//commit or revision
			List<Change> changeList = logEntry.getChangeList();
	      if(res.containsKey(comment)){
	      	List<String> fileList = res.get(comment);
	      	changesToStringList(fileList, changeList);
	      }
	      else{
	      	List<String> fileList = new ArrayList<String>();
	      	changesToStringList(fileList, changeList);
	      	res.put(comment, fileList);
	      }
      }
		return res;
	}

	/**
	 * @param fileList
	 * @param changeList
	 */
   private static void changesToStringList(List<String> fileList,
         List<Change> changeList) {
	   for (Change change : changeList) {
	      if(!fileList.contains(change.getPath()))
	      	fileList.add(change.getPath());
	   }
   }

	public static void printMap(Map<String, List<Event>> map) {
		Set<String> s = map.keySet();
		for (String string : s) {
			List<Event> events = map.get(string);
			String st = "";
			for (Event event : events) {
//				st+=event.getType()+" ";
				st+=event;
			}
			System.out.println("["+string + " -> "+ st+"]");
		}
	}
	
	public static FileTable toMatrix(Map<String, List<Event>> map) {
		Set<String> headers = map.keySet();
		int[][] matrix = new int[headers.size()][headers.size()];
		
		return new FileTable(headers, matrix);
	}
}
