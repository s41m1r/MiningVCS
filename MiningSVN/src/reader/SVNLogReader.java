/**
 * 
 */
package reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import model.LogEntry;

/**
 * @author Saimir Bala
 *
 */
public class SVNLogReader implements LogReader<LogEntry> {
	
	BufferedReader br;
	
	/**
    * 
    */
   public SVNLogReader(BufferedReader reader) {
	   br = reader;
   }
	

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
   @Override
   public Iterator<LogReader<LogEntry>> iterator() {
	   return null;
   }

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
   @Override
   public void close() throws IOException {
   	br.close();
   }

	/* (non-Javadoc)
	 * @see reader.LogReader#readAll()
	 */
   @Override
   public List<LogEntry> readAll() throws IOException {
	   return null;
   }

	/* (non-Javadoc)
	 * @see reader.LogReader#readNext()
	 */
   @Override
   public LogEntry readNext() throws IOException {
//   	TODO: complete this!!!
   	
   	LogEntry le;// = new SVNLogEntry(); 
   	
   	String line = br.readLine();
   	String[] revision = line.split("Revision:");
   	   	
   	return null;
   }

}
