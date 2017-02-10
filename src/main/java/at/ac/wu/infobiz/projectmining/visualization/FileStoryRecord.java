package at.ac.wu.infobiz.projectmining.visualization;

import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;

public class FileStoryRecord implements Comparable<FileStoryRecord> {
	
	public String comments;
	public Date	date;
	public int totalLinesAdded;
	public int totalLinesRemoved;
	public int totalChangeInTheDay;
	public int totalDiffInTheDay;
	public int linesUntilThisDay;
	public String[] users;


	public FileStoryRecord() {
	}


	public FileStoryRecord(String comments, Date date, int totalLinesAdded, int totalLinesRemoved, int totalChangeInTheDay,
			int totalDiffInTheDay, int linesUntilThisDay, String[] users) {
		super();
		this.comments = comments;
		this.date = date;
		this.totalLinesAdded = totalLinesAdded;
		this.totalLinesRemoved = totalLinesRemoved;
		this.totalChangeInTheDay = totalChangeInTheDay;
		this.totalDiffInTheDay = totalDiffInTheDay;
		this.linesUntilThisDay = linesUntilThisDay;
		this.users = users;
	}


	public String getComments() {
		return comments;
	}


	public Date getDate() {
		return date;
	}


	public int getLinesUntilThisDay() {
		return linesUntilThisDay;
	}


	public int getTotalChangeInTheDay() {
		return totalChangeInTheDay;
	}


	public int getTotalDiffInTheDay() {
		return totalDiffInTheDay;
	}


	public int getTotalLinesAdded() {
		return totalLinesAdded;
	}


	public int getTotalLinesRemoved() {
		return totalLinesRemoved;
	}


	public String[] getUsers() {
		return users;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public void setLinesUntilThisDay(int linesUntilThisDay) {
		this.linesUntilThisDay = linesUntilThisDay;
	}


	public void setTotalChangeInTheDay(int totalChangeInTheDay) {
		this.totalChangeInTheDay = totalChangeInTheDay;
	}


	public void setTotalDiffInTheDay(int totalDiffInTheDay) {
		this.totalDiffInTheDay = totalDiffInTheDay;
	}


	public void setTotalLinesAdded(int totalLinesAdded) {
		this.totalLinesAdded = totalLinesAdded;
	}


	public void setTotalLinesRemoved(int totalLinesRemoved) {
		this.totalLinesRemoved = totalLinesRemoved;
	}


	public void setUsers(String[] users) {
		this.users = users;
	}


	@Override
	public String toString() {
		return "FileStoryRecord [comments=" + comments + ", date=" + date + ", totalLinesAdded=" + totalLinesAdded
				+ ", totalLinesRemoved=" + totalLinesRemoved + ", totalChangeInTheDay=" + totalChangeInTheDay
				+ ", totalDiffInTheDay=" + totalDiffInTheDay + ", linesUntilThisDay=" + linesUntilThisDay + ", users="
				+ Arrays.toString(users) + "]";
	}


	@Override
	public int compareTo(FileStoryRecord o) {
		DateTime d1 = new DateTime(this.date.getTime());
		DateTime d2 = new DateTime(o.getDate().getTime());
		return d1.compareTo(d2);
	}

}
