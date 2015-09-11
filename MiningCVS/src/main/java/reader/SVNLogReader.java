/**
 * 
 */
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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import model.Change;
import model.LogEntry;
import model.svn.SVNLogEntry;

/**
 * @author Saimir Bala
 *
 */
public class SVNLogReader implements LogReader<LogEntry>, Closeable {

	private BufferedReader br = null;		
	/**
	 * 
	 */
	public SVNLogReader(BufferedReader reader) {
		br = reader;
	}

	public SVNLogReader(File file) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(file));
	}
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public SVNLogReader(String fileName) throws FileNotFoundException {
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
		
		while (line.equals("")) 
	      line = br.readLine();

		String revision = line.split("Revision: ")[1];
		line = br.readLine();
//		System.out.println(revision);
		String[] l = line.split("Author: "); 
		String author = (l.length>1)? l[1] : "";
		line = br.readLine();
		String dateString = line.split("Date: ")[1];
		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter germanFmt = DateTimeFormat.forPattern("EEEE, dd. MMMM yyyy HH:mm:ss").withLocale(locale);
		DateTime date = null;
		try {
			date = germanFmt.parseDateTime(dateString);
      } catch (Exception e) {
      }
		
		line = br.readLine().trim();
		if(!line.equals("Message:")){
			br.close();
			throw new IOException();
		}
		String message = readMessage(br); 

		List<Change> changeList = new ArrayList<Change>();

		while(!(line = br.readLine()).trim().equals("")){
			String[] changeLine = line.split(" : ");
			Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
			changeList.add(ch);
		}

		SVNLogEntry svnLogEntry = new SVNLogEntry(revision,author,date,message,changeList);

		return svnLogEntry;
	}

	/**
	 * @param br2
	 * @return
	 * @throws IOException 
	 */
	private static String readMessage(BufferedReader br2) throws IOException {
		String line = br2.readLine().trim();
		String msg = "";

		while(line!=null && !line.equals("----")){
			msg+=line;
			line = br2.readLine().trim();
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
