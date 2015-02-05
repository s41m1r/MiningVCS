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
	//static reference to itself
	private static ConnectionFactory instance = new ConnectionFactory();
	public static final String URL = "jdbc:mysql://localhost/";
	public static final String USER = "miningsvn_user";
	public static final String PASSWORD = "miningsvn";
	public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
	
	//private constructor
	private ConnectionFactory() {
		try {
			Class.forName(DRIVER_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private java.sql.Connection createConnection() {
		java.sql.Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			System.out.println("ERROR: Unable to Connect to Database.");
		}
		return connection;
	}	
	
	public static java.sql.Connection getConnection() {
		return instance.createConnection();
	}
}