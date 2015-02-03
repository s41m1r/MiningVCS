/**
 * 
 */
package model.svn;

import java.util.Map;

import model.LogEntry;

import org.joda.time.DateTime;


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
	 * @param @Map<String,String> changeList
	 */
   public SVNLogEntry(String startingToken, String author, DateTime date,
         String comment, Map<String, String> changeList) {
//	   super();
	   this.startingToken = startingToken;
	   this.author = author;
	   this.date = date;
	   this.comment = comment;
	   this.changeList = changeList;
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
   public DateTime getDate() {
	   // TODO Auto-generated method stub
	   return super.getDate();
   }

	@Override
   public void setDate(DateTime date) {
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
   public Map<String, String> getChangeList() {
	   // TODO Auto-generated method stub
	   return super.getChangeList();
   }

	@Override
   public void setChangeList(Map<String, String> changeList) {
	   // TODO Auto-generated method stub
	   super.setChangeList(changeList);
   }

	@Override
   public String toString() {
	   // TODO Auto-generated method stub
	   return super.toString();
   }
}
