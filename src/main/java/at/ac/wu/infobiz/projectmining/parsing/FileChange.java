package at.ac.wu.infobiz.projectmining.parsing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.joda.time.DateTime;

import at.ac.wu.infobiz.projectmining.model.Edit;
import at.ac.wu.infobiz.projectmining.model.File;

public class FileChange {
	private File file;
	private String commitID;
	private DateTime timeOfChange;
	private String author;//the resources who changed
	private boolean renamed;
	private boolean deleted;
	private boolean created;
	private List<Edit> edits;
	private Deque<String> oldNames;
	private Deque<String> handOverStack;
	private int linesCount;
	private String comment;
	
	public FileChange() {
		edits = new ArrayList<Edit>();
		oldNames = new ArrayDeque<String>();
		handOverStack = new ArrayDeque<String>();
	}
	
	public FileChange(File fileName) {
		this.edits = new ArrayList<Edit>();
		this.file = fileName;
		this.renamed = false;
		this.oldNames = new ArrayDeque<String>();
		handOverStack = new ArrayDeque<String>();
	}

	public FileChange(List<Edit> edits, File fileName) {
		this.edits = edits;
		this.file = fileName;
		this.renamed = false;
		this.oldNames = new ArrayDeque<String>();
		handOverStack = new ArrayDeque<String>();
	}
		
	public void addOldName(String oldName){
		oldNames.push(oldName);
	}
	
	public void addEdit(Edit e){
		edits.add(e);
	}
	

	public void addToHandOverStack(String resource){
		handOverStack.push(resource);
	}

	public String getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
	}

	public String getCommitID() {
		return commitID;
	}

	public String getCurrentResouce(){
		return handOverStack.peek();
	}

	public File getFile() {
		return file;
	}

	public Deque<String> getHandOverStack() {
		return handOverStack;
	}

	public int getLinesCount() {
		return linesCount;
	}

	public Deque<String> getOldNames() {
		return oldNames;
	}

	public List<Edit> getEdits() {
		return edits;
	}

	public DateTime getTimeOfChange() {
		return timeOfChange;
	}

	public boolean isCreated() {
		return created;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isRenamed() {
		return renamed;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}


	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setHandOverStack(Deque<String> handOverStack) {
		this.handOverStack = handOverStack;
	}


	public void setLinesCount(int linesCount) {
		this.linesCount = linesCount;
	}

	public void setOldNames(Deque<String> oldNames) {
		this.oldNames = oldNames;
	}

	public void setRenamed(boolean renamed) {
		this.renamed = renamed;
	}

	public void setTimeOfChange(DateTime timeOfChange) {
		this.timeOfChange = timeOfChange;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileChange [fileName=" + file + ", commitID=" + commitID
				+ ", timeOfChange=" + timeOfChange + ", author=" + author
				+ ", renamed=" + renamed + ", deleted=" + deleted
				+ ", created=" + created + ", edits=" + edits + ", oldNames="
				+ oldNames + ", handOverStack=" + handOverStack
				+ ", linesCount=" + linesCount + ", comment=" + comment + "]";
	}

	public void setEdits(List<Edit> edits) {
		this.edits = edits;
	}
	
	
}
