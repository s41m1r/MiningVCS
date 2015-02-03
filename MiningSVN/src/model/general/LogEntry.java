package model.general;

import java.sql.Date;

/**
 * @author saimir
 *
 */
public abstract class LogEntry {
	protected String startingToken;
	protected String author;
	protected Date date;
	protected String comment;
	protected String action; //changed, modified, added, etc

	public String getStartingToken() {
		return startingToken;
	}

	public void setStartingToken(String startingToken) {
		this.startingToken = startingToken;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	@Override
   public String toString() {
	   return "LogEntry [startingToken=" + startingToken + ", author=" + author
	         + ", date=" + date + ", comment=" + comment + ", action=" + action
	         + "]";
   }
}
