package reader;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
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
		String author = line.split("Author: ")[1];
		line = br.readLine();
		String dateString = line.split("Date: ")[1];
		line = br.readLine().trim();
		
		String message = readMessage(br); 

		List<Change> changeList = readChangeList(br);

		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter germanFmt = DateTimeFormat.forPattern("EEEE, dd. MMMM yyyy HH:mm:ss").withLocale(locale);
		DateTime date = germanFmt.parseDateTime(dateString);

		SVNLogEntry svnLogEntry = new SVNLogEntry(revision,author,date,message,changeList);

		return svnLogEntry;
	}

	private static List<Change> readChangeList(BufferedReader br2) throws IOException {
		List<Change> changeList = new ArrayList<Change>();
		//read the first line
		String line = br2.readLine();
		
		while(line.startsWith("   "))
			line = br2.readLine();
		// changes do not start with many spaces
		
		if(line.equals(""))
			br2.readLine();
		
//		TODO: cambia qui
		while(!line.equals("")){
			String[] changeLine = line.trim().split("|");
			Change ch = new Change(changeLine[1].trim(), changeLine[2].trim());
			changeList.add(ch);
			line = br2.readLine();
		}
		return null;
	}

	private static String readMessage(BufferedReader br2) throws IOException {
		String msg = "";
		String line = null;
		while((line=br2.readLine())!="")
			msg+=line.trim();
		return msg;
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
