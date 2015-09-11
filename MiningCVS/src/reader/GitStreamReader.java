package reader;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import model.Change;
import model.LogEntry;
import model.git.GITFileChanges;
import model.git.GITLogEntry;

public class GitStreamReader implements LogReader<LogEntry>, Closeable {
	
	private BufferedReader br;
	
	public GitStreamReader(InputStream in) {
		this.br = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public void close() throws IOException {
		this.br.close();
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

	@Override
	public LogEntry readNext() throws IOException {
		boolean merge = false;
		String line = this.br.readLine();
		
		if(line==null)
			return null;

		while(!line.startsWith("commit") && (line=this.br.readLine())!=null);
		
		if(line == null)
			return null;
		
		String revision = line.split("commit ")[1];
		line = this.br.readLine();

		if(line.startsWith("Merge")){
			merge = true;
			line = this.br.readLine();
		}
		
		String author = readAuthor(line);
		line = this.br.readLine().trim();
		
		String dateString = line.split("Date: ")[1];
		line = this.br.readLine();
		
		String message = readMessage(line); 		
		List<Change> changeList = merge? new ArrayList<Change>(): readChangeList();
		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
		DateTime date = gitFmt.parseDateTime(dateString.trim());
		LogEntry gitLogEntry = new GITLogEntry(revision,author,date,message,changeList);
		
		return gitLogEntry;
	}

	private List<Change> readChangeList() throws IOException {
		List<Change> changeList = new ArrayList<Change>();

		String line = this.br.readLine();
		if(line==null)
			return changeList;
		String start = line.trim().split("\\s+")[0];

		switch (start) {
		case GITFileChanges.ADDED:
			break;
		case GITFileChanges.MODIFIED:
			break;
		case GITFileChanges.DELETED:
			break;

		default:
			return changeList;
		}

		while(line!=null && !line.equals("")){
			String[] changeLine = line.trim().split("\\s+");
			Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
			changeList.add(ch);
			line = this.br.readLine();
		}
		return changeList;
	}

	private String readMessage(String line) throws IOException {
		String msg = "";
		line = this.br.readLine().trim();
		while(line!=null && !line.equals("")){
			msg+=line;
			line = this.br.readLine();
		}
		return msg;
	}

	private String readAuthor(String line) {
		String author = "";
		if(line.equals(""))
			return author;
		if(line.startsWith("Author"))
			author = line.split("Author: ")[1];
		return author;
	}

}
