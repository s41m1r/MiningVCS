/**
 * 
 */
package test;

import java.io.IOException;

import model.LogEntry;
import reader.GITLogReader;
import reader.LogReader;

/**
 * @author Saimir Bala
 *
 */
public class TestReadGIT {
	
	final static String fileName = "resources/20150205_GIT_LOG_FROM_ECSPI_PROPOSAL.log";
//	final static String fileName = "resources/MiningSvn.log";
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		LogReader<LogEntry> lr = new GITLogReader(fileName);
		System.out.println(lr.readNext());
		System.out.println(lr.readNext());
		System.out.println(lr.readNext());
		System.out.println(lr.readAll());
		lr.close();
	}

}
