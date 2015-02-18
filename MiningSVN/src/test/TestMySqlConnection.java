/**
 * 
 */
package test;

import java.util.ArrayList;

import model.Log;
import model.LogEntry;
import model.svn.SVNLog;
import reader.LogReader;
import reader.SVNLogReader;
import db.dao.EntryDAO;
import db.jdbc.ConnectionFactory;
import db.to.EntryTransferObject;


/**
 * @author Saimir Bala
 *
 */
public class TestMySqlConnection {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		MySQLAccess dao = new MySQLAccess();
//	    dao.readDataBase();
	   
		java.sql.Connection con = ConnectionFactory.getConnection();
		System.out.println(con.getMetaData());
		
		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
//		lr.close();
		Log log = new SVNLog(lr.readAll());
		ArrayList<LogEntry> entries = (ArrayList<LogEntry>) log.getAllEntries();
		EntryDAO edao = null;
		for (LogEntry logEntry : entries) {
			EntryTransferObject eto = new EntryTransferObject(
					logEntry.getStartingToken(), logEntry.getAuthor(),logEntry.getDate().toString(), logEntry.getComment());
			edao = new EntryDAO(con);
			edao.insertEntry(eto);
      }
		System.out.println("Entries = "+edao.count());
		lr.close();
	}
}
