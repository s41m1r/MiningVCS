/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.svn.SVNLog;
import reader.GitStreamReader;
import reader.LogReader;
import reader.SVNLogReader;

/**
 * @author saimir
 *
 */
public class ParseOutComments {
	
	public static void main(String[] args) {
		try {
			parseOutComments(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args -f = path/to/input/file 
	 * @throws IOException 
	 */
	public static Collection<String> parseOutComments(String[] args) throws IOException {

		Opts.parseArgs(args);

		String inputFile = Opts.optionValueMap.get(Opts.LOGFILE);
		System.out.println("Input is "+inputFile);

		System.out.println("Opts.USEGITLOG " + Opts.optionValueMap.get(Opts.USEGITLOG));

		InputStream is = new FileInputStream(inputFile);

		Boolean usingGitLog = false;
		LogReader<LogEntry> logReader = null;
		Log log = null;


		if(Opts.optionValueMap.get(Opts.USEGITLOG)!=null)
			usingGitLog = true;

		if(usingGitLog){
			logReader = new GitStreamReader(is);
			log = new GITLog(logReader.readAll());
		}
		else{ 
			logReader = new SVNLogReader(new BufferedReader(new InputStreamReader(is)));
			log = new SVNLog(logReader.readAll());
		}
		logReader.close();

		if(Opts.optionValueMap.get(Opts.OUTFILE)!=null)
			SysUtils.toFile(Opts.optionValueMap.get(Opts.OUTFILE));

//		Collection<LogEntry> entries = log.getAllEntries();
//		Collection<String> commentsCollection = new LinkedList<String>();
//		for (LogEntry logEntry : entries) {
//			commentsCollection.add(logEntry.getComment());
//		}
//		return commentsCollection;
		
		return log.getAllComments().values();
		
	}

}
