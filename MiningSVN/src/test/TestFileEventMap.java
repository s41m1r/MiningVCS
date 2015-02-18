/**
 * 
 */
package test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import reader.GITLogReader;
import reader.LogReader;
import util.FileEventMap;

/**
 * @author Saimir Bala
 *
 */
public class TestFileEventMap {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
//		LogReader<LogEntry> lr = new GITLogReader("resources/MiningSvn.log");
		LogReader<LogEntry> lr = new GITLogReader("/home/saimir/data.gov.log");
		Log log = new GITLog(lr.readAll());
//		Log log = new SVNLog(lr.readAll());
//		System.out.println("Read "+log.size()+" entries.");
		TestLog.toFile("/home/saimir/Downloads/out.txt");
		lr.close();
		Map<String, List<Event>> map = FileEventMap.buildFileEventMap(log);
		Set<String> s = map.keySet();
		for (String string : s) {
	      System.out.println(string + " -> "+ map.get(string).size());
      }
		System.out.println();
	}

}
