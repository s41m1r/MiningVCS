/**
 * 
 */
package test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
		LogReader<LogEntry> lr = new GITLogReader("resources/MiningSvn.log");
		//		LogReader<LogEntry> lr = new GITLogReader("/home/saimir/ownCloud/project mining/data/data.gov.log");
		Log log = new GITLog(lr.readAll());
		//		Log log = new SVNLog(lr.readAll());
		//		System.out.println("Read "+log.size()+" entries.");
		//		TestLog.toFile("/home/saimir/out.txt");
		lr.close();
		//include also the files that were deleted
		Map<String, List<Event>> historicalMap = FileEventMap.buildHistoricalFileEventMap(log);

		System.out.println("Files: "+historicalMap.size());
		FileEventMap.printMap(historicalMap); 

		Map<String, List<Event>> map = FileEventMap.buildFileEventMap(log);
		FileEventMap.printMap(map);
	}

}
