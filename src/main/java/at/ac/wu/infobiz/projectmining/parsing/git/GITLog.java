/**
 * 
 */
package at.ac.wu.infobiz.projectmining.parsing.git;

import java.util.Collection;
import java.util.List;

import at.ac.wu.infobiz.projectmining.parsing.Log;
import at.ac.wu.infobiz.projectmining.parsing.LogEntry;

/**
 * @author saimir
 *
 */
public class GITLog extends Log {
	/**
	 * @param readAll
	 */
   public GITLog(List<LogEntry> list) {
   	this.entries = list;
   	MODIFIED = "M";
   	ADDED = "A";
   	DELETED = "D";

   }

	@Override
	public Collection<LogEntry> getAllEntries() {
		// TODO Auto-generated method stub
		return super.getAllEntries();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
