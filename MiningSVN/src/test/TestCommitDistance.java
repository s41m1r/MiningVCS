/**
 * 
 */
package test;

import java.io.IOException;

import model.Log;
import model.LogEntry;
import model.svn.SVNLog;
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
		String file1 = "/example/ToyStation_0Loop.bpmn";
		String file2 = "/example/ToyStation_nLoop.bpmn";
		
		LogReader<LogEntry> lr = new SVNLogReader(
				"resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		
//		String file1 = "LaTeX/proposal.tex";
//		String file2 = "LaTeX/proposal-bib.bib";
//		
//		LogReader<LogEntry> lr = new GITLogReader(
//				"resources/20150205_GIT_LOG_FROM_ECSPI_PROPOSAL.log");
		
		Log log = new SVNLog(lr.readAll());
		System.out.println("Loaded "+log.size()+" entries.");
//		System.out.println(CommitDistance.timesOccurTogether(file1, file2, log));
//		System.out.println(CommitDistance.timesOccurs(file1, log));
//		System.out.println(CommitDistance.timesOccurs(file2, log));
		System.out.println(CommitDistance.commitDistance(
				file1, file2, log));
		lr.close();
	}

}
