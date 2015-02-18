/**
 * 
 */
package model;

import org.joda.time.DateTime;

/**
 * @author Saimir Bala
 *
 */
public class Event {
	
	private String id;
	private DateTime start;
	private DateTime end;
	private String fileID; //where did it happen
	private String type; // whether it's a Modify, Delete or Add 
	/**
    * 
    */
   public Event() {
   }
	/**
	 * @param id - id of the event
	 * @param start - when started
	 * @param end - when finished (to implement duration)
	 * @param fileID - which file affected
	 * @param type - type of modification
	 */
   public Event(String id, DateTime start, DateTime end, String fileID,
         String type) {
	   super();
	   this.id = id;
	   this.start = start;
	   this.end = end;
	   this.fileID = fileID;
	   this.type = type;
   }
	/**
	 * @param start
	 * @param fileID
	 * @param type
	 */
   public Event(DateTime start, String fileID, String type) {
	   super();
	   this.start = start;
	   this.fileID = fileID;
	   this.type = type;
   }
	/**
	 * @param start
	 * @param fileID
	 */
   public Event(DateTime start, String fileID) {
	   super();
	   this.start = start;
	   this.fileID = fileID;
   }   
   /**
	 * @param start
	 * @param fileID
	 */
   public Event(DateTime start, DateTime end, String fileID) {
	   super();
	   this.start = start;
	   this.end = end;
	   this.fileID = fileID;
   }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DateTime getStart() {
		return start;
	}
	public void setStart(DateTime start) {
		this.start = start;
	}
	public DateTime getEnd() {
		return end;
	}
	public void setEnd(DateTime end) {
		this.end = end;
	}
	public String getFileID() {
		return fileID;
	}
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
   
}
