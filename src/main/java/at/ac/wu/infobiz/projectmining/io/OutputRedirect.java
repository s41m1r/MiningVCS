package at.ac.wu.infobiz.projectmining.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public abstract class OutputRedirect {
	
	static PrintStream console = System.out;
	
	public static void toFile(String filePath) throws FileNotFoundException{
		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos,true);
		System.setOut(ps);		
	}
	
	public static void toConsole(){
		System.setOut(console);
	}
}
