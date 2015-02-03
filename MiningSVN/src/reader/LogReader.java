/**
 * 
 */
package reader;

import java.io.IOException;
import java.util.List;

/**
 * @author Saimir Bala
 *
 */
public interface LogReader<LogEntry>{	
	/**
	 * @return all the log entries
	 * @throws IOException
	 */
	public List<LogEntry> readAll() throws IOException;
	
	/**
	 * @return the next LogEntry, if there is one
	 * @throws IOException
	 */
	public LogEntry readNext() throws IOException;
	
}
