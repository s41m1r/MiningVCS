/**
 * 
 */
package to;

/**
 * @author Saimir Bala
 *
 */
public class ChangesTranferObject {
	
	private String entry;
	private String path;
	/**
	 * @param entry
	 * @param path
	 */
   public ChangesTranferObject(String entry, String path) {
	   super();
	   this.entry = entry;
	   this.path = path;
   }
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
