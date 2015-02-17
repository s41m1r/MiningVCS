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
		String file1 = "/misc/hint_01_28v2.pdf";
		String file2 = "/misc/reconcile_partA_090921_submission-final.pdf";
		LogReader<LogEntry> lr = new SVNLogReader(
				"resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		Log log = new Log(lr.readAll());
		System.out.println(CommitDistance.timesOccurTogether(file1, file2, log));
		System.out.println(CommitDistance.timesOccurs(file1, log));
		System.out.println(CommitDistance.timesOccurs(file2, log));
		System.out.println(CommitDistance.commitDistance(
				file1, file2, log));
		lr.close();
	}

}
