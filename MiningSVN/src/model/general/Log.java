/**
 * This class represents a Log as a set of LogEntries
 */
package model.general;

import java.util.Set;

/**
 * @author Saimir Bala
 *
 */
public abstract class Log {
	protected Set<LogEntry> entries;
	
	/**
	 * @return all the entries
	 */
	public Set<LogEntry> getAllEntries(){
		return entries;
	}
	
	public int size(){
		return entries.size();
	}
	
	@Override
   public String toString() {
	   return "Log [entries=" + entries + "]";
   }

}
