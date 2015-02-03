/**
 * 
 */
package model;

/**
 * @author Saimir Bala
 *
 */
public class Change {
	public String action;
	public String path;
	
	/**
	 * @param action
	 * @param path
	 */
   public Change(String action, String path) {
	   super();
	   this.action = action;
	   this.path = path;
   }

	@Override
   public String toString() {
	   return "Change [action=" + action + ", path=" + path + "]";
   }
	
}
