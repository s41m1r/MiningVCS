package at.ac.wu.infobiz.projectmining.model;

import java.util.Date;

public class MercurialCommit extends Commit {
	
	private String branch;

	public MercurialCommit() {
	}

	public MercurialCommit(String id, Date timeStamp, String comment, String branch) {
		super(id, timeStamp, comment);
		this.setBranch(branch);
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

}
