package model;

import java.sql.Date;

/**
 * @author saimir
 *
 */
public class LogEntry {
	private String startingToken;
	private String author;
	private Date date;
	private String comment;
	private String action; //changed, modified, added, etc
	
	public LogEntry() {
		// TODO Auto-generated constructor stub
	}
	
	public LogEntry(String startingToken, String author, Date date,
			String comment, String action) {
		super();
		this.startingToken = startingToken;
		this.author = author;
		this.date = date;
		this.comment = comment;
		this.action = action;
	}



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
}
