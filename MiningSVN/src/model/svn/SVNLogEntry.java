/**
 * 
 */
package model.svn;

import java.sql.Date;

import model.general.LogEntry;


/**
 * @author Saimir Bala
 *
 */
public class SVNLogEntry extends LogEntry {
	/**
    * 
    */
   public SVNLogEntry() {
   	
   }
   
	/**
	 * @param startingToken
	 * @param author
	 * @param date
	 * @param comment
	 * @param action
	 */
   public SVNLogEntry(String startingToken, String author, Date date,
         String comment, String action) {
//	   super();
	   this.startingToken = startingToken;
	   this.author = author;
	   this.date = date;
	   this.comment = comment;
	   this.action = action;
   }

   
   

	@Override
   public String getStartingToken() {
	   // TODO Auto-generated method stub
	   return super.getStartingToken();
   }

	@Override
   public void setStartingToken(String startingToken) {
	   // TODO Auto-generated method stub
	   super.setStartingToken(startingToken);
   }

	@Override
   public String getAuthor() {
	   // TODO Auto-generated method stub
	   return super.getAuthor();
   }

	@Override
   public void setAuthor(String author) {
	   // TODO Auto-generated method stub
	   super.setAuthor(author);
   }

	@Override
   public Date getDate() {
	   // TODO Auto-generated method stub
	   return super.getDate();
   }

	@Override
   public void setDate(Date date) {
	   // TODO Auto-generated method stub
	   super.setDate(date);
   }

	@Override
   public String getComment() {
	   // TODO Auto-generated method stub
	   return super.getComment();
   }

	@Override
   public void setComment(String comment) {
	   // TODO Auto-generated method stub
	   super.setComment(comment);
   }

	@Override
   public String getAction() {
	   // TODO Auto-generated method stub
	   return super.getAction();
   }

	@Override
   public void setAction(String action) {
	   // TODO Auto-generated method stub
	   super.setAction(action);
   }

	@Override
   public String toString() {
	   // TODO Auto-generated method stub
	   return super.toString();
   }
}
