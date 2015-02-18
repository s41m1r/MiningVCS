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
	private String author;
	private String commitID;
	private String comment;
	
	/**
	 * @param id
	 * @param start
	 * @param end
	 * @param fileID
	 * @param type
	 * @param author
	 * @param commitID
	 * @param comment
	 */
   public Event(String id, DateTime start, DateTime end, String fileID,
         String type, String author, String commitID, String comment) {
	   super();
	   this.id = id;
	   this.start = start;
	   this.end = end;
	   this.fileID = fileID;
	   this.type = type;
	   this.author = author;
	   this.commitID = commitID;
	   this.comment = comment;
   }
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
    * 
    */
   public Event() {
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
	   id = fileID;
   }
	/**
	 * @param start
	 * @param fileID
	 */
   public Event(DateTime start, String fileID) {
	   super();
	   this.start = start;
	   this.fileID = fileID;
	   id = fileID;
   }
	/**
	 * @param start
	 * @param fileID
	 * @param type
	 */
   public Event(DateTime start, String fileID, String type) {
	   super();
	   id = fileID;
	   this.start = start;
	   this.fileID = fileID;
	   this.type = type;
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
	 * @param id
	 * @param start
	 * @param end
	 * @param fileID
	 * @param type
	 * @param author
	 */
   public Event(String id, DateTime start, DateTime end, String fileID,
         String type, String author) {
	   super();
	   this.id = id;
	   this.start = start;
	   this.end = end;
	   this.fileID = fileID;
	   this.type = type;
	   this.author = author;
   }
	/**
	 * @param id
	 * @param start
	 * @param end
	 * @param fileID
	 * @param type
	 * @param author
	 * @param commitID
	 */
   public Event(String id, DateTime start, DateTime end, String fileID,
         String type, String author, String commitID) {
	   super();
	   this.id = id;
	   this.start = start;
	   this.end = end;
	   this.fileID = fileID;
	   this.type = type;
	   this.author = author;
	   this.commitID = commitID;
   }
	
	/**
	 * @param start
	 * @param fileID
	 * @param type
	 * @param author
	 * @param commitID
	 * @param comment
	 */
   public Event(DateTime start, String fileID, String type, String author,
         String commitID, String comment) {
	   super();
	   this.start = start;
	   this.fileID = fileID;
	   this.type = type;
	   this.author = author;
	   this.commitID = commitID;
	   this.comment = comment;
	   end = start;
	   id = fileID;
   }
	public String getAuthor() {
		return author;
	}
	public String getCommitID() {
		return commitID;
	}
	public DateTime getEnd() {
		return end;
	}   
   public String getFileID() {
		return fileID;
	}
	public String getId() {
		return id;
	}
	public DateTime getStart() {
		return start;
	}
	public String getType() {
		return type;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}
	public void setEnd(DateTime end) {
		this.end = end;
	}
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setStart(DateTime start) {
		this.start = start;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
   public String toString() {
	   return "Event [id=" + id + ", start=" + start + ", end=" + end
	         + ", fileID=" + fileID + ", type=" + type + ", author=" + author
	         + ", commitID=" + commitID + ", comment=" + comment + "]";
   }
   
}
