/**
 * 
 */
package db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBUtil;
import db.to.EntryTransferObject;

/**
 * @author Saimir Bala
 *
 */
public class EntryDAO extends GenericDAO<EntryTransferObject> {
	//Private
	private final static String TABLENAME = "entry";
	private final static String INSERT = "insert into miningsvn.entry values (?, ?, ?, ?)";

	public EntryDAO(java.sql.Connection con) {
		super(con, TABLENAME);
	}

	@Override
	public int count() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM miningsvn."+this.tableName;
		java.sql.PreparedStatement counter;
		ResultSet res = null;
		try
		{
			counter = this.con.prepareStatement(query);
			res = counter.executeQuery();
			res.next();
			return res.getInt("count");
		}
		catch(SQLException e){ throw e; }
		finally{
			if(res!=null)
				DBUtil.close(res);
		}
	}

	public List<EntryTransferObject> getAll(){
		List<EntryTransferObject> allEntryTransferObjects = new ArrayList<EntryTransferObject>();
		String query = "SELECT * FROM miningsvn."+this.tableName;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;
		try{
			 ps = con.prepareStatement(query);
			 rs = ps.executeQuery();

			while (rs.next()) {
				EntryTransferObject eto = new EntryTransferObject(
						rs.getString("id"), 
						rs.getString("author"), 
						rs.getString("date"), 
						rs.getString("comment"));
				allEntryTransferObjects.add(eto);
			}
			if(ps!=null)
				ps.close();
		} catch (SQLException e) {
	      System.out.println(e);
      }finally{
			DBUtil.close(rs);
		}
		return allEntryTransferObjects;
	}

	public void insertEntry(EntryTransferObject eto){
		try {
			java.sql.PreparedStatement ps = con.prepareStatement(INSERT);
			ps.setString(1, eto.getId());
			ps.setString(2, eto.getAuthor());
			ps.setString(3, eto.getDate());
			ps.setString(4, eto.getComment());
			ps.execute();
		} catch (SQLException e) {
			//	      e.printStackTrace();
			System.out.println(e);
		}
	}
}
