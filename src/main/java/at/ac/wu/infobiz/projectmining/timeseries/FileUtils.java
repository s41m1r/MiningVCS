package at.ac.wu.infobiz.projectmining.timeseries;

import java.nio.file.Path;
import java.nio.file.Paths;

import at.ac.wu.infobiz.projectmining.model.File;

public class FileUtils {

//	public FileUtils() {
//	}
	
	/**
	 * 
	 * @param f1 e.g. a/b
	 * @param f2 e.g. a/b/c
	 * @return true is f1 is a suffix of f2
	 */
	public static boolean isContained(File f1, File f2){
		String p1 = f1.getPath();
		String p2 = f2.getPath();
		
		String[] parts1 = p1.split("/");
		String[] parts2 = p2.split("/");
		
		if(parts2.length >= parts1.length)
			return false;
		
		return p1.compareTo(p2)<0;
	}
	
	public static boolean isContained(String f1, String f2){
		Path p1 = Paths.get(f1);
		Path p2 = Paths.get(f2);
		
		return p1.compareTo(p2)<0;
	}
	
	public static void main(String[] args){
		String p1 = "/d/d/f/c";
		String p2 = "/d/a/a";
		
		System.out.println(TreeDistance.lca(p1, p2));
		
		System.out.println(isContained(p1, p2));
	}

}
