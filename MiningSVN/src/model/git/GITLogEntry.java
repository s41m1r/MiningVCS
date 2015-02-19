/**
 * 
 */
package model.git;

import java.util.List;

import model.Change;
import model.LogEntry;

import org.joda.time.DateTime;

/**
 * @author saimir
 *
 */
public class GITLogEntry extends LogEntry{
	
	/**
    * 
    */
   public GITLogEntry() {
   }
   
   /**
	 * @param startingToken
	 * @param author
	 * @param date
	 * @param comment
	 * @param List<Change> changeList
	 */
   public GITLogEntry(String commit, String author, DateTime date,
         String comment, List<Change> changeList) {
   	
	   this.startingToken = commit;
	   this.author = author;
	   this.date = date;
	   this.comment = comment;
	   this.changeList = changeList;
   }

	/* (non-Javadoc)
	 * @see model.LogEntry#getStartingToken()
	 */
	@Override
	public String getStartingToken() {
		// TODO Auto-generated method stub
		return super.getStartingToken();
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#setStartingToken(java.lang.String)
	 */
	@Override
	public void setStartingToken(String startingToken) {
		super.setStartingToken(startingToken);
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#getAuthor()
	 */
	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return super.getAuthor();
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#setAuthor(java.lang.String)
	 */
	@Override
	public void setAuthor(String author) {
		// TODO Auto-generated method stub
		super.setAuthor(author);
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#getDate()
	 */
	@Override
	public DateTime getDate() {
		// TODO Auto-generated method stub
		return super.getDate();
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#setDate(org.joda.time.DateTime)
	 */
	@Override
	public void setDate(DateTime date) {
		// TODO Auto-generated method stub
		super.setDate(date);
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#getComment()
	 */
	@Override
	public String getComment() {
		// TODO Auto-generated method stub
		return super.getComment();
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#setComment(java.lang.String)
	 */
	@Override
	public void setComment(String comment) {
		// TODO Auto-generated method stub
		super.setComment(comment);
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#getChangeList()
	 */
	@Override
	public List<Change> getChangeList() {
		// TODO Auto-generated method stub
		return super.getChangeList();
	}

	/* (non-Javadoc)
	 * @see model.LogEntry#setChangeList(java.util.List)
	 */
	@Override
	public void setChangeList(List<Change> changeList) {
		// TODO Auto-generated method stub
		super.setChangeList(changeList);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogEntry [commit=" + startingToken + ", author="
				+ author + ", date=" + date + ", comment=" + comment
				+ ", changeList=" + changeList + "]";
	}
}
