/**
 * 
 */
package jdbc;


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
	}
}
