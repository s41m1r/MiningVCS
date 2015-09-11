package util;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import model.Change;
import model.Log;
import model.LogEntry;
import model.svn.SVNLog;
import reader.LogReader;
import reader.SVNLogReader;
import test.TestLog;

public class CVS2CSV {

	public static void toCSV() throws IOException{
		LogReader<LogEntry> lr = new SVNLogReader("resources/running_example.log");
		Log log = new SVNLog(lr.readAll());
		Collection<LogEntry> entryList = log.getAllEntries();
		System.out.println("fileID, Author, Change, Timestamp, Comment");
		for (Iterator iterator = entryList.iterator(); iterator.hasNext();) {
			LogEntry logEntry = (LogEntry) iterator.next();
			List<Change> changeList = logEntry.getChangeList();
			for (Change change : changeList) {
				String fileId = change.getPath();
				String editType = change.getAction();
				System.out.println("'"+fileId + "', " 
						+ "'"+logEntry.getAuthor() + "'," 
						+ "'"+editType + "',"
						+ "'"+logEntry.getDate() + "'," 
						+ "'"+logEntry.getComment()+"'");
			}
		}	
	} 

	public static void main(String[] args) throws IOException {
		TestLog.toFile("/home/saimir/out.csv");
		toCSV();
	}

}
