package at.ac.wu.infobiz.projectmining.timeseries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileStory implements Comparable<FileStory> {
	
	public String filename;
	public List<FileStoryRecord> story;

	public FileStory() {
		story = new ArrayList<FileStoryRecord>();
	}

	public FileStory(List<FileStoryRecord> story) {
		super();
		this.story = story;
	}
	
	
	public FileStory(String filename, List<FileStoryRecord> story) {
		super();
		this.filename = filename;
		this.story = story;
	}
	
	public boolean containsDate(Date d){
		for (FileStoryRecord fileStoryRecord : story) {
			if(fileStoryRecord.date.equals(d))
				return true;
		}
		return false;
	}

	public Date getMinDate(){
		java.time.Instant min = story.get(0).date.toInstant();
		for (FileStoryRecord fileStoryRecord : story) {
			if(min.isBefore(fileStoryRecord.getDate().toInstant()))
				min = fileStoryRecord.getDate().toInstant();
		}
		return Date.from(min);
	}
	
	public Date getMaxDate(){
		java.time.Instant max = story.get(0).date.toInstant();
		for (FileStoryRecord fileStoryRecord : story) {
			if(max.isAfter((fileStoryRecord.getDate().toInstant())))
				max = fileStoryRecord.getDate().toInstant();
		}
		return Date.from(max);
	}
	
	public Set<Date> getAllDates(){
		Set<Date> allDates = new HashSet<Date>();
		for (FileStoryRecord fileStoryRecord : story) {
			allDates.add(fileStoryRecord.date);
		}
		return allDates;
	}

	@Override
	public String toString() {
		return "FileStory [story=" + story + "]";
	}

	@Override
	public int compareTo(FileStory o) {
		return this.getMinDate().compareTo(o.getMinDate());
	}

	public void addDateWithZeroChange(Date date) {
		this.story.add(new FileStoryRecord("",date,0,0,0,0,0,new String[]{}));
		Collections.sort(story);
	}	
}
