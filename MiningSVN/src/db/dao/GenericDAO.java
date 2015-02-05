/**
 * 
 */
package db.dao;

import java.sql.SQLException;

/**
 * @author Saimir Bala
 *
 */
public abstract class GenericDAO<T> {

   public abstract int count() throws SQLException; 

   //Protected
   protected final String tableName;
   protected java.sql.Connection con;

   protected GenericDAO(java.sql.Connection con2, String tableName) {
       this.tableName = tableName;
       this.con = con2;
   }

}