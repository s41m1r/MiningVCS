package reader;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import model.Change;
import model.LogEntry;
import model.git.GITFileChanges;
import model.git.GITLogEntry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GITLogReader implements LogReader<LogEntry>, Closeable{

	private RandomAccessFile raf;

//	public GITLogReader(BufferedReader reader) {
//		br = reader;
//	}
//
//	public GITLogReader(File file) throws FileNotFoundException {
//		br = new BufferedReader(new FileReader(file));
//	}
//	/**
//	 * @throws FileNotFoundException 
//	 * 
//	 */
//	public GITLogReader(String fileName) throws FileNotFoundException {
//		br = new BufferedReader(new FileReader(fileName));
//	}
	
	/**
	 * @throws FileNotFoundException 
    * 
    */
   public GITLogReader(String file) throws FileNotFoundException {
   	raf = new RandomAccessFile(file, "r");
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

		String line = raf.readLine();

		if(line==null)
			return null;
		
		while(!line.startsWith("commit"))
			raf.readLine();

		String revision = line.split("commit ")[1];
		line = raf.readLine();
		String author = readAuthor(line);
		line = raf.readLine().trim();
		String dateString = line.split("Date: ")[1];
		line = raf.readLine();
		String message = readMessage(line); 
		
		//When a merge happens, GIT automatically generates a message describing it. Plus, there is no change-list to be read.
		List<Change> changeList = (message.startsWith("Merge"))? new ArrayList<Change>(): readChangeList();

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
   		author = readAuthor(raf.readLine());
   	
	   return author;
   }

	private List<Change> readChangeList() throws IOException {
		List<Change> changeList = new ArrayList<Change>();
		//read the first line
		long fp = raf.getFilePointer();
		String line = raf.readLine();
		String start = line.trim().split("\\s+")[0];
		switch (start) {
		case GITFileChanges.ADDED:
			break;
		case GITFileChanges.MODIFIED:
			break;
		case GITFileChanges.DELETED:
			break;
			
		default: raf.seek(fp);
			return changeList;
			
		}
		while(line!=null && !line.equals("")){
			String[] changeLine = line.trim().split("\\s+");
			Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
			changeList.add(ch);
			line = raf.readLine();
		}
		return changeList;
	}

	private String readMessage(String line) throws IOException {
		String msg = "";
		line = raf.readLine().trim();
		while(line!=null && !line.equals("")){
			msg+=line;
			line = raf.readLine();
		}
		return msg;
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		raf.close();
	}

}
