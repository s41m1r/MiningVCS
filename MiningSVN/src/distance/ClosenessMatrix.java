/**
 * 
 */
package distance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
   	headers = new ArrayList<String>();
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
   
   public void buildMatrix(double alpha1, double alpha2, Log log){
   	Set<String> fileSet = new HashSet<String>(log.getAllFiles());
   	String[] a = fileSet.toArray(new String[0]);
   	Arrays.sort(a);
   	List<String> list = Arrays.asList(a);
   	headers = new ArrayList<String>(list);
   	int dim = headers.size();
   	this.M = new double[dim][dim];
   	
   	//self distances to 0
   	for (int i = 0; i < M.length; i++) 
	      M[i][i] = 0;
   	
   	int progress = 0; 
//   	set the distances
   	for (int i = 0; i < headers.size(); i++) {
	      for (int j = i+1; j < headers.size(); j++) {
	         M[i][j] = M[j][i] = 1 - ( 
	         		alpha1 * TreeDistance.treeDistance(headers.get(i),headers.get(j), log) + 
	         		alpha2 * CommitDistance.commitDistance(headers.get(i), headers.get(j), log));
         }
	      if(progress<(i*dim)*100/(dim*dim)){
	      	progress = (i*dim)*100/(dim*dim);
	      	System.out.println(progress+"%");
	      }
      }
   }
   
	@Override
   public String toString() {
		String m = "";
		for (int i = 0; i < M.length; i++) {
	      for (int j = 0; j < M[0].length; j++) {
	         m+="\t"+ new DecimalFormat("#0.00").format(M[i][j]);
         }
	      m+="\n";
      }
	   return "ClosenessMatrix [M=\n" + m + "]";
   }
	
   public void toCSV(File file) throws IOException{
   	PrintWriter pw = new PrintWriter(file);
   	BufferedWriter bw = new BufferedWriter(pw);
   	String comma = ",";
   	for (int i = 0; i < M.length; i++) {
	      for (int j = 0; j < M[0].length; j++) {
	      	if(j==M[0].length-1)
	      		bw.write(new DecimalFormat("#0.00").format(M[i][j]));
	      	else 
	      		bw.write(new DecimalFormat("#0.00").format(M[i][j])+comma);
         }
	      bw.write("\n");
      }
   	bw.close();
   	pw.close();
   }
	
}
