/**
 * 
 */
package distance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import model.Log;

/**
 * @author Saimir Bala
 *
 */
public class ClosenessMatrix {

	private double[][] M;
	private ArrayList<String> headers;
	
	/**
    * Quadratic matrix
    */
   public ClosenessMatrix(int dimension) {
   	M = new double[dimension][dimension];
   	this.init();
   }
   
   public ClosenessMatrix(int dimension, String headers) {
   	M = new double[dimension][dimension];
   	this.init();
   }
   
   /**
    * 
    */
   public ClosenessMatrix() {
   }
   
   /**
    * Set all zeros
    */
   private void init(){
   	for (int i = 0; i < M.length; i++) {
	      for (int j = 0; j < M[0].length; j++) {
	         M[i][j] = 0;
         }
      }
   }
   
   public double[][] toBidimensionalArray(){
   	return M; 
   }
   
   public double[] getRow(int row){
   	return M[row];
   }
   
   public ArrayList<String> getHeaders() {
		return headers;
	}

	public void setHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}

	public double[] getColumn(int column){
   	double[] col = new double[M.length];
   	for (int i = 0; i < M.length; i++) {
	      col[i] = M[i][column];
      }
   	return col;
   }
   
   public void buildMatrix(Set<String> fileSet, double alpha1, double alpha2, Log log){
   	String[] a = (String[]) fileSet.toArray();
   	Arrays.sort(a);
   	List<String> list = Arrays.asList(a);
   	Collections.copy(headers, list);
   	
   	int dim = headers.size();
   	this.M = new double[dim][dim];
   	
   	//self distances to 0
   	for (int i = 0; i < M.length; i++) 
	      M[i][i] = 0;
   	
//   	set the distances
   	for (int i = 0; i < headers.size(); i++) {
	      for (int j = i+1; j < headers.size(); j++) {
	         M[i][j] = M[j][i] = 1 -  
	         		alpha1 * TreeDistance.treeDistance(headers.get(i),headers.get(j), log) + 
	         		alpha2 * CommitDistance.commitDistance(headers.get(i), headers.get(j), log);
         }
      }
   }
   
	@Override
   public String toString() {
	   return "ClosenessMatrix [M=" + Arrays.toString(M) + "]";
   }
	
}
