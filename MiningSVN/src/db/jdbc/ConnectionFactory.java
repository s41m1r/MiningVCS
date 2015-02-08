/**
 * 
 */
package db.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Saimir Bala
 *
 */
public class ConnectionFactory {
	public static final String URL = "jdbc:mysql://localhost/";
	public static final String USER = "miningsvn_user";
	public static final String PASSWORD = "miningsvn";
	public static final String DB = "miningsvn";
//	public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
	
	public static java.sql.Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL+""+DB+"?"+"user="+USER+"&password="+PASSWORD);
	}
}