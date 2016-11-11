package at.ac.wu.infobiz.projectmining.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import at.ac.wu.infobiz.projectmining.io.CommandLineOptions;
import at.ac.wu.infobiz.projectmining.parsing.mercurial.MercurialToDB;

public class TestMercurialToDB {
	//  jgit-cookbookWithRename.log
	public static String inputFile = "data/mercurial.log";
	static String outputFile= "out.txt";

	public final static String START_COMMIT_DELIMITER = "";
	public final static String END_MESSAGE_DELIMITER = "\\^s*$";


	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Map<String, String> optionsMap = CommandLineOptions.parseArgs(args);
		String input = optionsMap.get(CommandLineOptions.LOGFILE);

		if(input!=null)
			inputFile=input;
		String output = optionsMap.get(CommandLineOptions.LOGFILE);
		if(output!=null)
			outputFile=output;
		MercurialToDB mercurialToDB = new MercurialToDB(inputFile, START_COMMIT_DELIMITER, END_MESSAGE_DELIMITER);
		try {
			mercurialToDB.toCSV(new File(inputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		long stop = System.currentTimeMillis();
		System.out.println("All done in "+ getElapsedTime(stop, startTime));
	}

	/**
	 * @param startTime
	 */
	private static String getElapsedTime(long time, long startTime) {
		long elapsed = time-startTime;
		long second = (elapsed / 1000) % 60;
		long minute = (elapsed / (1000 * 60)) % 60;
		long hour = (elapsed / (1000 * 60 * 60)) % 24;

		return String.format("%02d:%02d:%02d.%03d [h:m:s.msec]", hour, minute, second, elapsed%1000);
	}
}
