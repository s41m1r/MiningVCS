/**
 * This class represents a Log as a set of LogEntries
 */
package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * @author Saimir Bala
 *
 */
public abstract class Log {

	public static String MODIFIED = "Modified";
	public static String ADDED = "Added";
	public static String DELETED = "Deleted";

	protected Collection<LogEntry> entries;

//	/**
//	 * 
//	 */
//	public Log() {
//	}
//
//	/**
//	 * @param list
//	 */
//	public Log(List<LogEntry> list) {
//		super();
//		this.entries = list;
//	}

	/**
	 * @return all the entries
	 */
	public Collection<LogEntry> getAllEntries(){
		return entries;
	}

	public Collection<String> getAllAuthors(){
		Set<String> authors = new HashSet<String>();
		for (LogEntry logEntry : entries) {
			authors.add(logEntry.getAuthor());
		}
		return authors;
	}

	public Collection<DateTime> getAllDates(){
		Set<DateTime> dates = new HashSet<DateTime>();
		for (LogEntry logEntry : entries) {
			dates.add(logEntry.getDate());
		}
		return dates;
	}

	public Collection<List<Change>> getGroupedChanges(){
		Collection<List<Change>> changes = new ArrayList<List<Change>>();
		for (LogEntry logEntry : entries) {
			changes.add(logEntry.getChangeList());
		}
		return changes;
	}

	public Collection<Change> getAllChanges(){
		ArrayList<Change> changes = new ArrayList<Change>();
		for (LogEntry logEntry : entries) {
			changes.addAll(logEntry.getChangeList());
		}
		return changes;
	}
	/**
	 * 
	 * @return all file paths
	 */
	public Collection<String> getAllFiles(){
		Set<String> changes = new HashSet<String>();
		for (LogEntry logEntry : entries) {
			for(Change ch : logEntry.getChangeList()){
				changes.add(ch.getPath());
			}
		}
		return changes;
	}
	
	public Map<String,String> getAllComments(){
		Map<String, String> allComments = new LinkedHashMap<String, String>();
		for(LogEntry logEntry : this.entries)
			allComments.put(logEntry.getCommitID(), logEntry.getComment());
		return allComments;
	}

	public int size(){
		return entries.size();
	}

	@Override
	public String toString() {
		return "Log [entries=" + entries + "]";
	}

}
