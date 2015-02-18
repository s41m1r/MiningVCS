/**
 * 
 */
package test;

import java.io.File;
import java.io.IOException;

import model.Log;
import model.LogEntry;
import model.git.GITLog;
import reader.GITLogReader;
import reader.LogReader;
import distance.ClosenessMatrix;

/**
 * @author Saimir Bala
 *
 */
public class TestClosenessMatrix {
	static double alpha1 = 0.3d;
	static double alpha2 = 1-alpha1;
	
	public static void main(String[] args) throws IOException {
		LogReader<LogEntry> lr = new GITLogReader("resources/MiningSvn.log");
		Log log = new GITLog(lr.readAll());
		ClosenessMatrix cm = new ClosenessMatrix();
		cm.buildMatrix(alpha1, alpha2, log);
		System.out.println(cm);
		lr.close();
		cm.toCSV(new File("/home/saimir/Downloads/closenessMatrix.csv"));
   }
}
