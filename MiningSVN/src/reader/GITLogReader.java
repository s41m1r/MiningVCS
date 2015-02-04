package reader;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Change;
import model.LogEntry;
import model.git.GITLogEntry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GITLogReader implements LogReader<LogEntry>, Closeable{

	private BufferedReader br;

	public GITLogReader(BufferedReader reader) {
		br = reader;
	}

	public GITLogReader(File file) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(file));
	}
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public GITLogReader(String fileName) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(fileName));
	}

	@Override
	public List<LogEntry> readAll() throws IOException {

		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		LogEntry entry;

		while((entry=readNext())!=null)
			logEntries.add(entry);

		return logEntries;
	}

	/* (non-Javadoc)
	 * @see reader.LogReader#readNext()
	 */
	@Override
	public LogEntry readNext() throws IOException {

		String line = br.readLine();

		if(line==null)
			return null;
		
		while(!line.startsWith("commit"))
			br.readLine();

		String revision = line.split("commit ")[1];
		line = br.readLine();
		String author = readAuthor(line);
		line = br.readLine().trim();
		String dateString = line.split("Date: ")[1];
		line = br.readLine();
		
		String message = readMessage(br); 
		
		//When a merge happens, GIT automatically generates a message describing it. Plus, there is no change-list to be read.
		List<Change> changeList = (message.startsWith("Merge"))? new ArrayList<Change>(): readChangeList(br);

	   DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z");
		DateTime date = gitFmt.parseDateTime(dateString.trim());

		LogEntry gitLogEntry = new GITLogEntry(revision,author,date,message,changeList);

		return gitLogEntry;
	}

	/**
	 * @param line
	 * @return
	 * @throws IOException 
	 */
   private String readAuthor(String line) throws IOException {
   	String author = "";
   	
   	if(line.equals(""))
   		return author;
   	
   	if(line.startsWith("Author"))
   		 author = line.split("Author: ")[1];
   	
   	else if(line.startsWith("Merge"))
   		author = readAuthor(br.readLine());
   	
	   return author;
   }

	private static List<Change> readChangeList(BufferedReader br2) throws IOException {
		List<Change> changeList = new ArrayList<Change>();
		//read the first line
		String line = br2.readLine();
		
		while(line!=null && !line.equals("")){
			String[] changeLine = line.trim().split("\\s+");
			Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
			changeList.add(ch);
			line = br2.readLine();
		}
		return changeList;
	}

	private static String readMessage(BufferedReader br2) throws IOException {
		String msg = "";
		String line = br2.readLine();
		while(!line.equals("") && line!=null){
			msg+=line.trim();
			line = br2.readLine();
		}
		return msg;
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		br.close();
	}

}
