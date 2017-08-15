package at.ac.wu.infobiz.projectmining.timeseries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class TimeSeriesTable {
	
	public List<Date> dates;
	public List<FileStory> files;
	public Map<File, FileStory> map;

	public TimeSeriesTable(Map<File, FileStory> map) {
		List<Date> allDates = new ArrayList<Date>();
		
		for (File f : map.keySet()) {
			FileStory fs = map.get(f);
			allDates.add(fs.getMinDate());
			allDates.add(fs.getMaxDate());
		} 
		
		//Sorting dates
		Collections.sort(allDates, new Comparator<Date>() {
		    @Override
		    public int compare(Date lhs, Date rhs) {
		        if (lhs.getTime() < rhs.getTime())
		            return -1;
		        else if (lhs.getTime() == rhs.getTime())
		            return 0;
		        else
		            return 1;
		    }
		});
		
		dates = this.createDatesArray(allDates.get(0), allDates.get(allDates.size()-1));
		this.map = this.expandFileStory(map);
	}
	
	public List<Date> createDatesArray(Date minDate, Date maxDate){
		List<Date> res = new ArrayList<Date>();
		
		DateTime d1 = new DateTime(minDate);
		DateTime d2 = new DateTime(maxDate);
		Days d =  Days.daysBetween(d1, d2);
		
		for(int i=0; i<=d.getDays(); i++){
			res.add(d1.plusDays(i).toDate());
		}
		
		return res;
	}
	
	public Map<File, FileStory> expandFileStory(Map<File, FileStory> map){
		Map<File, FileStory> res = new TreeMap<File, FileStory>();
		
		for (Date date : dates) {
			for(File f : map.keySet()){
				if(!map.get(f).containsDate(date)){
					FileStoryRecord fsr = 
							new FileStoryRecord("", date, 0, 0, 0, 0, 0, new String[]{});
					map.get(f).story.add(fsr);
					res.put(f, map.get(f));
				}
				else {
					res.put(f, map.get(f));
				}
			}
		}
		return res;
	}
	
	
	public Map<File, FileStory> sortByDate(Map<File, FileStory> map) {
		Map<File, FileStory> res = new HashMap<File, FileStory>();
		for(File file : map.keySet()){
			List<FileStoryRecord> storyRecords = map.get(file).story;
			Collections.sort(storyRecords);
			res.put(file, new FileStory(map.get(file).filename, storyRecords));
		}
		return res;	
	}

	public Map<File, FileStory> getExpandedFileStoryMap(){
		return map;
	}
	

}
