/**
 * 
 */
package to;

import java.sql.Date;

import org.joda.time.DateTime;

/**
 * @author Saimir Bala
 *
 */
public class EntryTransferObject {
	private String id;
	private String author;
	private String date;
	private String comment;
	/**
	 * @param id
	 * @param author
	 * @param date
	 * @param comment
	 */
   public EntryTransferObject(String id, String author, String date,
         String comment) {
	   super();
	   this.id = id;
	   this.author = author;
	   this.date = date;
	   this.comment = comment;
   }
   
   /**
	 * @param id
	 * @param author
	 * @param date
	 * @param comment
	 */
   public EntryTransferObject(String id, String author, DateTime date,
         String comment) {
	   super();
	   this.id = id;
	   this.author = author;
	   this.date = date.toString();
	   this.comment = comment;
   }
   
   /**
  	 * @param id
  	 * @param author
  	 * @param date
  	 * @param comment
  	 */
     public EntryTransferObject(String id, String author, Date date,
           String comment) {
  	   super();
  	   this.id = id;
  	   this.author = author;
  	   this.date = date.toString();
  	   this.comment = comment;
     }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
