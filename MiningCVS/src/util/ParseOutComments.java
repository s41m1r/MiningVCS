/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

	/**
	 * @param args -f = path/to/input/file 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Opts.parseArgs(args);
		
		String inputFile = Opts.optionValueMap.get(Opts.LOGFILE);
		System.out.println("Input is "+inputFile);
		
		System.out.println("Opts.USEGITLOG " + Opts.optionValueMap.get(Opts.USEGITLOG));
		
		InputStream is = new FileInputStream(inputFile);
		SysUtils.toFile(Opts.optionValueMap.get(Opts.OUTFILE));
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
		System.out.println(log.getAllComments());
	}

}
