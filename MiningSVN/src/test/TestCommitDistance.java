/**
 * 
 */
package test;

import java.io.IOException;

import model.Log;
import model.LogEntry;
import reader.LogReader;
import reader.SVNLogReader;
import distance.CommitDistance;

/**
 * @author saimir
 *
 */
public class TestCommitDistance {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		LogReader<LogEntry> lr = new SVNLogReader(
				"resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		Log log = new Log(lr.readAll());
		int occurenceCnt = CommitDistance.timeOccurs(
				"/example/ToyStation_0Loop.bpmn", log);
		System.out.println(occurenceCnt);
		System.out.println(CommitDistance.timesOccurTogether(
				"/example/ToyStation_0Loop.bpmn", "/example/ToyStation_nLoop.bpmn", log));
		lr.close();
	}

}
