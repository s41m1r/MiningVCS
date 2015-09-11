/**
 * 
 */
package util;

import java.util.Set;

/**
 * @author Saimir Bala
 *
 */
public class FileTable {
	Set<String> headers;
	int[][] matrix;
	
	public FileTable() {
	}

	public FileTable(Set<String> headers2, int[][] matrix) {
		super();
		this.headers = headers2;
		this.matrix = matrix;
	}
}
