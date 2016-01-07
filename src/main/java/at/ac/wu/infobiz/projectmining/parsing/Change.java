/**
 * 
 */
package at.ac.wu.infobiz.projectmining.parsing;

/**
 * @author Saimir Bala
 *
 */
public class Change {
	private String action;
	private String path;
	
	/**
	 * @param action
	 * @param path
	 */
   public Change(String action, String path) {
	   super();
	   this.action = action;
	   this.path = path;
   }

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	@Override
   public String toString() {
	   return "Change [action=" + action + ", path=" + path + "]";
   }
	
}
