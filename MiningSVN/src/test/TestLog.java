/**
 * 
 */
package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.Change;
import model.Log;
import model.LogEntry;
import reader.GITLogReader;
import reader.LogReader;

/**
 * @author Saimir Bala
 *
 */
public class TestLog {
	static PrintStream console = System.out;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		LogReader<LogEntry> lr = new GITLogReader("/home/saimir/mysql-server.log");
//		LogReader<LogEntry> lr = new GITLogReader("/home/saimir/data.gov.log");
//		LogReader<LogEntry> lr = new GITLogReader("resources/MiningSvn.log");
//		LogReader<LogEntry> lr = new GITLogReader("resources/20150205_GIT_LOG_FROM_ECSPI_PROPOSAL.log");
		Log log = new Log(lr.readAll());
		System.out.println("Read "+log.size()+" entries.");
//		System.out.println(log);
//		toFile();
//		System.out.println("Entries="+log.size());
//		System.out.println("Authors="+log.getAllAuthors());
//		LinkedList<DateTime> dates = new LinkedList<DateTime>(log.getAllDates());
//		Collections.sort(dates);
//		System.out.println("Dates="+dates);
//		Collection<List<Change>> changesDistinct = log.getGroupedChanges();
//		System.out.println("Grouped changes="+ changesDistinct.size());
//		Collection<Change> changesAll = log.getAllChanges(); 
//		System.out.println("All Changes="+ changesAll);
//		System.out.println("Distinct="+ log.getAllDistinctChanges());
		lr.close();
	}
	
	public static void toFile() throws FileNotFoundException{
		File file = new File("/home/saimir/Downloads/out.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
	}
	
	public static void toConsole(){
		System.setOut(console);
	}
	
//	Doesn't make sense
//	public static Collection<Change> diff(Collection<Change> a, Collection<Change> b){
//		Collection<Change> c = new ArrayList<Change>();
//		Collection<Change> biggest = a.size() > b.size()? a: b;
//		Collection<Change> smallest = (biggest==a) ? b: a;
//		
//		for (Change object : biggest) {
//	      if(!smallest.contains(object))
//	      	c.add(object);
//      }
//		return c;
//	}
	
	public static Collection<Change> toCollection(Collection<List<Change>> changesDistinct){
		Collection<Change> co = new ArrayList<Change>();
		for (List<Change> list : changesDistinct) {
			co.addAll(list);
      }
		return co;
	}

}
