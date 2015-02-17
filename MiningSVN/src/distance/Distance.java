/**
 * 
 */
package distance;

/**
 * @author Saimir Bala
 *
 */
public class Distance {
	public String a;
	public String b;
	public double d;
	
	/**
	 * @param a
	 * @param b
	 * @param d
	 */
   public Distance(String a, String b, double d) {
	   super();
	   this.a = a;
	   this.b = b;
	   this.d = d;
   }

	@Override
   public String toString() {
	   return "[ Distance(a,b)= "+ d + "]";
   }
}
