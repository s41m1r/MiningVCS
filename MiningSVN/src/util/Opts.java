/**
 * 
 */
package util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author saimir
 *
 */
public abstract class Opts {
	
	public static Map<String, String> optionValueMap = null;
	
	public static String LOGFILE = "logFile";
	public static String THRESHOLD = "threshold";
	public static String USEGITLOG = "useGitLog";
	public static String USESVNLOG = "useSvnLog";
	
	
	public static Options initOpts() {
		Options opts = new Options();
		
		Option logfile   = new Option("f", "logFile", true, "use given file for log");  
		Option threshold   = new Option("t", "threshold", true, "use given this threshold for aggregation");
		Option isGitLog   = new Option("g", "useGitLog", false, "the input log is from a Git repository");
		Option isSvnLog   = new Option("svn", "useSvnLog", false, "the input log is from a Subversion repository");
		
		opts.addOption(logfile);
		opts.addOption(threshold);
		opts.addOption(isSvnLog);
		opts.addOption(isGitLog);
		
		return opts;
	}
	
	public static void parseArgs(String[] args){
		
		CommandLineParser parser = new DefaultParser();
		Options options = Opts.initOpts();
		CommandLine line = null;
		
	    try {
	        // parse the command line arguments
	        line = parser.parse(options, args );
	    }
	    catch(ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
		
	    optionValueMap = new HashMap<String, String>();
	    
	    optionValueMap.put("logFile", line.getOptionValue("f"));
	    optionValueMap.put("threshold", line.getOptionValue("t"));
	    optionValueMap.put("useGitLog", line.getOptionValue("g"));
	    optionValueMap.put("useSvnLog", line.getOptionValue("svn"));
	    
	} 
	
	public static String getInputFile(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = Opts.initOpts();
		CommandLine line = null;
		
	    try {
	        // parse the command line arguments
	        line = parser.parse(options, args );
	    }
	    catch(ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
		
	    String input = null;
	    input = line.getOptionValue("f");
		return input;
	}
	
	public static String getThreshold(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = Opts.initOpts();
		CommandLine line = null;
		
	    try {
	        // parse the command line arguments
	        line = parser.parse(options, args );
	    }
	    catch(ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
		
	    String input = null;
	    input = line.getOptionValue("t");
		return input;
	}
}
