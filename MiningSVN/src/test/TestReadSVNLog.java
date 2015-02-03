/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Change;
import model.LogEntry;
import model.svn.SVNLogEntry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tmatesoft.svn.core.SVNException;

import reader.LogReader;
import reader.SVNLogReader;

/**
 * @author Saimir Bala
 *
 */
public class TestReadSVNLog {
	
	final static String fileName = "resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log";

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SVNException 
	 */
	public static void main(String[] args) throws IOException {
		LogReader<LogEntry> lr = new SVNLogReader(fileName);
		System.out.println(lr.readNext());
		System.out.println(lr.readNext());
		System.out.println(lr.readNext());
		System.out.println(lr.readAll());
	}
	
	public static void readOne() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine();
		String revision = line.split("Revision: ")[1];
		line = br.readLine();
		String author = line.split("Author: ")[1];
		line = br.readLine();
		String dateString = line.split("Date: ")[1];
		line = br.readLine().trim();
		if(!line.equals("Message:")){
			br.close();
			throw new IOException();
		}
		String message = br.readLine().trim();
		message=br.readLine();
		
		List<Change> changeList = new ArrayList<Change>();
		
		while(!(line = br.readLine()).trim().equals("")){
			String[] changeLine = line.split(":");
			Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
			changeList.add(ch);
		}
		
		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter germanFmt = DateTimeFormat.forPattern("EEEE, dd. MMMM yyyy HH:mm:ss").withLocale(locale);
	   DateTime date = germanFmt.parseDateTime(dateString);
	   
		SVNLogEntry svnLogEntry = new SVNLogEntry(revision,author,date,message,changeList);
		
		System.out.println(svnLogEntry);  		
		br.close();
	}

}
