/**
 * 
 */
package model.svn;

import java.util.Collection;
import java.util.List;

import model.Log;
import model.LogEntry;

/**
 * @author Saimir Bala
 *
 */
public class SVNLog extends Log {

	public static final String MODIFIED = "Modified";
	public static final String ADDED = "Added";
	public static final String DELETED = "Deleted";
	
	/**
	 * @param readAll
	 */
   public SVNLog(List<LogEntry> list) {
   	this.entries = list;
   }

	@Override
	public Collection<LogEntry> getAllEntries() {
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
