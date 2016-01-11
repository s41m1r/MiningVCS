package at.ac.wu.infobiz.projectmining.reader;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import at.ac.wu.infobiz.projectmining.parsing.Change;
import at.ac.wu.infobiz.projectmining.parsing.LogEntry;
import at.ac.wu.infobiz.projectmining.parsing.TypeOfChange;
import at.ac.wu.infobiz.projectmining.parsing.git.GITLogEntry;

public class GitLogPatchStatsReader implements LogReader<LogEntry>, Closeable{

	private RandomAccessFile raf;

	/**
	 * @throws IOException 
	 * 
	 */
	public GitLogPatchStatsReader(String file) throws IOException {
		raf = new RandomAccessFile(file, "r");
	}

	@Override
	public List<LogEntry> readAll() throws IOException {

		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		LogEntry entry;

		while((entry=readNext())!=null){
			logEntries.add(entry);
		}
		return logEntries;
	}

	/* (non-Javadoc)
	 * @see reader.LogReader#readNext()
	 */
	@Override
	public LogEntry readNext() throws IOException {
		boolean merge = false;
		String line = raf.readLine();
//		System.out.println("Merge="+merge+" Line:"+line);
		if(line==null)
			return null;

		while(!line.startsWith("commit") && (line=raf.readLine())!=null);
		if(line == null)
			return null;

		String revision = line.split("commit ")[1];
		line = raf.readLine();

		if(line.startsWith("Merge")){
			merge = true;
			line = raf.readLine();
		}

		String author = readAuthor(line);
		line = raf.readLine().trim();
		String dateString = line.split("Date: ")[1];
		line = raf.readLine();
		
		String message = readMessage(line); 
		
		List<Change> changeList = merge? new ArrayList<Change>(): readChangeList();
		
//		System.out.println("Merge="+merge+" Message:"+message);
//		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
//		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy ZZ").withOffsetParsed();
//		System.out.println(new DateTime().toString(gitFmt));
		DateTime date = gitFmt.parseDateTime(dateString.trim());
//		System.out.println(date);
		LogEntry gitLogEntry = new GITLogEntry(revision,author,date,message,changeList);
//		System.out.println("Merge="+merge+" Message:"+message);
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

		return author;
	}

	private List<Change> readChangeList() throws IOException {
		List<Change> changeList = new ArrayList<Change>();
		//read the first line
		long fp = raf.getFilePointer();
		String line = raf.readLine();
		if(line==null)
			return changeList;
		String start = line.trim().split("\\s+")[0];

		switch (start) {
		case TypeOfChange.ADDED:
			break;
		case TypeOfChange.MODIFIED:
			break;
		case TypeOfChange.DELETED:
			break;

		default:
			//			System.out.println("seek");
			raf.seek(fp);
			//			System.out.println("seeked.");
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