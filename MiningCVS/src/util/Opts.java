/**
 * 
 */
package util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author saimir
 *
 */
public abstract class Opts {
	

	public static Map<String, String> optionValueMap = null;
	
	public static final String LOGFILE = "logFile";
	public static final String THRESHOLD = "threshold";
	public static final String USEGITLOG = "useGitLog";
	public static final String USESVNLOG = "useSvnLog";
	public static final String HELP = "help";
	public static final String OUTFILE = "outFile";
	
	private static Options initOpts() {
		Options opts = new Options();
		
		Option help = new Option( "h", "help", false, "print this message" );
		Option logfile   = new Option("f", "logFile", true, "use given file for log");  
		Option outfile   = new Option("o", "outFile", true, "print output to given file");
		Option threshold   = new Option("t", "threshold", true, "use given threshold for aggregation");
		Option isGitLog   = new Option("g", "useGitLog", false, "the input log is from a Git repository");
		Option isSvnLog   = new Option("svn", "useSvnLog", false, "the input log is from a Subversion repository");
		
		opts.addOption(help);
		opts.addOption(logfile);
		opts.addOption(outfile);
		opts.addOption(threshold);
		opts.addOption(isSvnLog);
		opts.addOption(isGitLog);
		
		return opts;
	}
	
	public static void parseArgs(String[] args){
		
		CommandLineParser parser = new DefaultParser();
		Options options = Opts.initOpts();
		CommandLine line = null;
		HelpFormatter formatter = new HelpFormatter();
		
	    try {
	        // parse the command line arguments
	        line = parser.parse(options, args );
	        if(line.hasOption("h")) {
	        	formatter.printHelp("ProjectMiner", options);
	        	System.exit(0);
	        }
	        	
	    }
	    catch(ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	        formatter.printHelp("ProjectMiner", options);
	    }
		
	    optionValueMap = new HashMap<String, String>();
	    
	    optionValueMap.put(HELP, line.getOptionValue("h"));
	    optionValueMap.put(LOGFILE, line.getOptionValue("f"));
	    optionValueMap.put(OUTFILE, line.getOptionValue("o"));
	    optionValueMap.put(THRESHOLD, line.getOptionValue("t"));
	    optionValueMap.put(USEGITLOG, line.hasOption("g")?"true":null);
	    optionValueMap.put(USESVNLOG, line.hasOption("svn")?"true":null);
	} 
	
//	public static String getInputFile(String[] args) {
//		CommandLineParser parser = new DefaultParser();
//		Options options = Opts.initOpts();
//		CommandLine line = null;
//		
//	    try {
//	        // parse the command line arguments
//	        line = parser.parse(options, args );
//	    }
//	    catch(ParseException exp ) {
//	        // oops, something went wrong
//	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
//	    }
//		
//	    String input = null;
//	    input = line.getOptionValue("f");
//		return input;
//	}
//	
//	public static String getThreshold(String[] args) {
//		CommandLineParser parser = new DefaultParser();
//		Options options = Opts.initOpts();
//		CommandLine line = null;
//		
//	    try {
//	        // parse the command line arguments
//	        line = parser.parse(options, args );
//	    }
//	    catch(ParseException exp ) {
//	        // oops, something went wrong
//	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
//	    }
//		
//	    String input = null;
//	    input = line.getOptionValue("t");
//		return input;
//	}
}
