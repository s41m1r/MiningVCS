/**
 * 
 */
package test;

import java.io.IOException;
import java.util.List;

import model.LogEntry;
import reader.GITLogReader;
import reader.LogReader;

/**
 * @author Saimir Bala
 *
 */
public class TestReadGIT {
	
	final static String fileName = "resources/commitlog123_20150902.log";
//	final static String fileName = "resources/MiningSvn.log";
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		LogReader<LogEntry> lr = new GITLogReader(fileName);
		List<LogEntry> allEntries = lr.readAll();
		System.out.println("Read "+ allEntries.size()+ "entries.");
		for (LogEntry logEntry : allEntries) {
			System.out.println(logEntry);
		}
		lr.close();
	}

}
