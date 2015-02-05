/**
 * 
 */
package db.jdbc;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.DateTime;

/**
 * @author Saimir Bala
 *
 */
public class MySQLAccess {
	  private java.sql.Connection connect = null;
	  private java.sql.Statement statement = null;
	  private java.sql.PreparedStatement preparedStatement = null;
	  private ResultSet resultSet = null;

	  public void readDataBase() throws Exception {
	    try {
	      // This will load the MySQL driver, each DB has its own driver
	      Class.forName("com.mysql.jdbc.Driver");
	      // Setup the connection with the DB
	      connect = DriverManager
	          .getConnection("jdbc:mysql://localhost/miningsvn?"
	              + "user=miningsvn_user&password=miningsvn");

	      // Statements allow to issue SQL queries to the database
	      statement = connect.createStatement();
	      // Result set get the result of the SQL query
	      resultSet = statement.executeQuery("select * from miningsvn.entry");
	      writeResultSet(resultSet);

	      // PreparedStatements can use variables and are more efficient
	      preparedStatement = connect
	          .prepareStatement("insert into  miningsvn.entry values (?, ?, ?, ?)");
	      // "myuser, webpage, datum, summery, COMMENTS from feedback.comments");
	      // Parameters start with 1
	      preparedStatement.setString(1, "Test2");
	      preparedStatement.setString(2, "TestEmail");
	      preparedStatement.setString(3, DateTime.now().toString());
	      preparedStatement.setString(4, "TestComment");
	      
	      preparedStatement.executeUpdate();

	      preparedStatement = connect
	          .prepareStatement("SELECT * from miningsvn.entry");
	      resultSet = preparedStatement.executeQuery();
	      writeResultSet(resultSet);

//	      // Remove again the insert comment
//	      preparedStatement = connect
//	      .prepareStatement("delete from feedback.comments where myuser= ? ; ");
//	      preparedStatement.setString(1, "Test");
//	      preparedStatement.executeUpdate();
//	      
//	      resultSet = statement
//	      .executeQuery("select * from feedback.comments");
	      writeMetaData(resultSet);
	      
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      close();
	    }

	  }

	  private void writeMetaData(ResultSet resultSet) throws SQLException {
	    //   Now get some metadata from the database
	    // Result set get the result of the SQL query
	    
	    System.out.println("The columns in the table are: ");
	    
	    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
	    for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
	      System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
	    }
	  }

	  private void writeResultSet(ResultSet resultSet) throws SQLException {
	    // ResultSet is initially before the first data set
	    while (resultSet.next()) {
	      // It is possible to get the columns via name
	      // also possible to get the columns via the column number
	      // which starts at 1
	      // e.g. resultSet.getSTring(2);
	      String user = resultSet.getString("id");
	      String website = resultSet.getString("author");
	      String summery = resultSet.getString("date");
	      String comment = resultSet.getString("comment");
	      System.out.println("id: " + user);
	      System.out.println("autho: " + website);
	      System.out.println("date: " + summery);
	      System.out.println("comment: " + comment);
	    }
	  }

	  // You need to close the resultSet
	  private void close() {
	    try {
	      if (resultSet != null) {
	        resultSet.close();
	      }

	      if (statement != null) {
	        statement.close();
	      }

	      if (connect != null) {
	        connect.close();
	      }
	    } catch (Exception e) {

	    }
	  }

	} 