/**
 * 
 */
package util;

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
	
	public static Options initOpts() {
		Options opts = new Options();
		
		Option logfile   = new Option("f", "logFile", true, "use given file for log");  
		
		opts.addOption(logfile);
		
		return opts;
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
}
