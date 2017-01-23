package at.ac.wu.infobiz.projectmining.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Commit")
@Access(AccessType.FIELD)
public class Commit {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String revisionId;
	
	@ManyToMany
	private List<Commit> parents;
	
	private Date timeStamp;
	@Column(columnDefinition="TEXT")
	private String comment;
	@ManyToOne
	private User user;
	@OneToMany(mappedBy="commit")
	private Collection<Edit> edits;
	@OneToMany(mappedBy="commit")
	private Collection<Rename> renames;
	@OneToMany(mappedBy="commit")
	private Collection<FileAction> fileActions;
	@ManyToOne
	private Project project;
	
	/**
	 * Stores whether the commit made it into the trunk.
	 * (Whether it is already a commit made directly on the trunk, or it was merged into the trunk)
	 */
	private boolean inTrunk;
	
	public Commit() {
		edits = new ArrayList<Edit>();
		renames = new ArrayList<Rename>();
		fileActions = new ArrayList<FileAction>();
		parents = new ArrayList<Commit>();
	}
	
	public Commit(String id, Date timeStamp, String comment) {
		super();
		this.revisionId = id;
		this.timeStamp = timeStamp;
		this.comment = ""+comment;
		edits = new ArrayList<Edit>();
		renames = new ArrayList<Rename>();
		fileActions = new ArrayList<FileAction>();
		parents = new ArrayList<Commit>();
	}
	
	public boolean addEdit(Edit e){
		return edits.add(e);
	}
	
	public boolean addFileAction(FileAction action) {
		return fileActions.add(action);
		
	}
	
	public boolean addRename(Rename rename){
		return renames.add(rename);
	}
	
	public String getComment() {
		return comment;
	}

	
	public Collection<Edit> getEdits() {
		return edits;
	}


	public Collection<FileAction> getFileActions() {
		return fileActions;
	}


	public Integer getId() {
		return id;
	}


	public Project getProject() {
		return project;
	}


	public Collection<Rename> getRenames() {
		return renames;
	}


	/**
	 * @return the revisionId
	 */
	public String getRevisionId() {
		return revisionId;
	}


	public Date getTimeStamp() {
		return timeStamp;
	}


	public User getUser() {
		return user;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setEdits(Collection<Edit> edits) {
		this.edits = edits;
	}

	public void setFileActions(Collection<FileAction> fileActions) {
		this.fileActions = fileActions;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public void setProject(Project project) {
		this.project = project;
	}


	public void setRenames(Collection<Rename> renames) {
		this.renames = renames;
	}


	/**
	 * @param revisionId the revisionId to set
	 */
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}


	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public List<Commit> getParents() {
		return parents;
	}

	public void setParents(List<Commit> parents) {
		this.parents = parents;
	}

	public void addParent(Commit parent){
		this.parents.add(parent);
	}
	
	public boolean isInTrunk() {
		return inTrunk;
	}

	public void setInTrunk(boolean inTrunk) {
		this.inTrunk = inTrunk;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Commit [id=" + id + ", revisionId=" + revisionId
				+ ", timeStamp=" + timeStamp + ", comment=" + comment
				+ ", project=" + project + "]";
	}

}
