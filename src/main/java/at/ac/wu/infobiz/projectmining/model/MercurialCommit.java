package at.ac.wu.infobiz.projectmining.model;

import java.util.Date;

public class MercurialCommit extends Commit {
	
	private String branch;

	public MercurialCommit() {
	}

	public MercurialCommit(String id, Date timeStamp, String comment, String branch) {
		super(id, timeStamp, comment);
		this.branch = branch;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	@Override
	public String toString() {
		return "MercurialCommit [branch=" + branch + ", getComment()=" + getComment() + ", getEdits()=" + getEdits()
				+ ", getFileActions()=" + getFileActions() + ", getId()=" + getId() + ", getProject()=" + getProject()
				+ ", getRenames()=" + getRenames() + ", getRevisionId()=" + getRevisionId() + ", getTimeStamp()="
				+ getTimeStamp() + ", getUser()=" + getUser() + ", getParents()=" + getParents() + ", isInTrunk()="
				+ isInTrunk() + "]";
	}
}
