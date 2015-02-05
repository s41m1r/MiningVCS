/**
 * 
 */
package db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import db.to.EntryTransferObject;

/**
 * @author Saimir Bala
 *
 */
public class EntryDAO extends GenericDAO<EntryTransferObject> {
	 //Private
	  private final static String TABLENAME = "entry";

   public EntryDAO(java.sql.Connection con) {
       super(con, TABLENAME);
   }

   @Override
   public int count() throws SQLException {
       String query = "SELECT COUNT(*) AS count FROM "+this.tableName;
       java.sql.PreparedStatement counter;
       try
       {
       counter = this.con.prepareStatement(query);
       ResultSet res = counter.executeQuery();
       res.next();
       return res.getInt("count");
       }
       catch(SQLException e){ throw e; }
   }
}
