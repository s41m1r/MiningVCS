/**
 * 
 */
package db.dao;

import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Saimir Bala
 *
 */
public abstract class GenericDAO<T> {

   public abstract int count() throws SQLException;
   protected abstract Collection<T> getAll();

   //Protected
   protected final String tableName;
   protected java.sql.Connection con;

   protected GenericDAO(java.sql.Connection con2, String tableName) {
       this.tableName = tableName;
       this.con = con2;
   }

}