/**
 * Useful methods
 */
package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author saimir
 *
 */
public class SysUtils {

	public static PrintStream console = System.out;
	public static final String DEFAULT_OUTPUT_FILE = "output.txt";

	public static void toFile(String filePath) throws FileNotFoundException{
		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
	}

	public static void toConsole(){
		System.setOut(console);
	}

}
