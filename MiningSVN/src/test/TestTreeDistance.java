/**
 * 
 */
package test;

import java.io.IOException;

import model.Log;
import model.LogEntry;
import reader.LogReader;
import reader.SVNLogReader;
import distance.TreeDistance;

/**
 * @author saimir
 *
 */
public class TestTreeDistance {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/**
		 * LaTeX/proposal.tex
		 * LaTeX/sections/JKU.tex
		 */
		LogReader<LogEntry> lr = new SVNLogReader(
				"resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		Log log = new Log(lr.readAll());
		System.out.println(TreeDistance.treeDistance(
				"LaTeX/proposal.tex", "LaTeX/sections/JKU.tex",log));
		System.out.println(TreeDistance.treeDistance(
				"LaTeX/proposal.tex", "LaTeX/JKU.tex",log));
		System.out.println(TreeDistance.treeDistance(
				"LaTeX/proposal.tex", 
				"/example/log_generator/generator/src/at/siemens/ct/shape/generator/ExcelLog.java",
				log));
		System.out.println(TreeDistance.treeDistance(
				"/proposal_FFG_IKT2013/templates (Copy from path: /templates, Revision, 197)", 
				"/example/log_generator/generator/src/at/siemens/ct/shape/generator/ExcelLog.java",
				log));
		lr.close();
	}

}
