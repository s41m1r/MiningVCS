/**
 * 
 */
package test;

import java.util.ArrayList;

import model.Log;
import model.LogEntry;
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
		Log log = new Log(lr.readAll());
		ArrayList<LogEntry> entries = (ArrayList<LogEntry>) log.getAllEntries();
		LogEntry le = entries.get(1);
		EntryTransferObject eto = new EntryTransferObject(
				le.getStartingToken(), le.getAuthor(),le.getDate().toString(), le.getComment());
		EntryDAO edao = new EntryDAO(con);
		System.out.println(edao.getAll());
		edao.insertEntry(eto);
		lr.close();
	}
}
