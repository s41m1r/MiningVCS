package at.ac.wu.infobiz.projectmining.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="File")
public class File implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5044129239587030486L;
	@Id @Column(columnDefinition="varchar(256) COLLATE latin1_general_cs")
	private String path;
	@OneToMany(mappedBy="file", cascade={CascadeType.ALL})
	public Collection<Edit> edits;
	@OneToMany(mappedBy="from")
	public Collection<Rename> renameFrom;
	@OneToMany(mappedBy="file", cascade={CascadeType.ALL})
	public Collection<FileAction> fileActions;
	@OneToMany(mappedBy="to")
	public Collection<Rename> renameTo;
	
	public File() {
		renameFrom = new ArrayList<Rename>();
		renameTo = new ArrayList<Rename>();
		fileActions = new ArrayList<FileAction>();
		edits = new ArrayList<Edit>();
	}
	
	public File(String fileName) {
		path = fileName;
		renameFrom = new ArrayList<Rename>();
		renameTo = new ArrayList<Rename>();
		fileActions = new ArrayList<FileAction>();
		edits = new ArrayList<Edit>();
	}

	public boolean addEdit(Edit e){
		return edits.add(e);
	}
	
	public boolean addEdits(List<Edit> edits){
		return this.edits.addAll(edits);
	}
	
	public boolean addFileAction(FileAction action) {
		return fileActions.add(action);
	}
	
	public boolean addRenameFrom(Rename renFrom) {
		return this.renameFrom.add(renFrom);
	}
	
	public boolean addRenameTo(Rename renTo){
		return this.renameTo.add(renTo);
	}

	public Collection<Edit> getEdits() {
		return edits;
	}

	public Collection<FileAction> getFileActions() {
		return fileActions;
	}

	public String getPath() {
		return path;
	}

	public Collection<Rename> getRenameFrom() {
		return renameFrom;
	}

	public Collection<Rename> getRenameTo() {
		return renameTo;
	}

	public void setEdits(Collection<Edit> edits) {
		this.edits = edits;
	}

	public void setFileActions(Collection<FileAction> fileActions) {
		this.fileActions = fileActions;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setRenameFrom(Collection<Rename> renameFrom) {
		this.renameFrom = renameFrom;
	}
	
	public void setRenameTo(Collection<Rename> renameTo) {
		this.renameTo = renameTo;
	}
	
	@Override
	public String toString() {
		return "File [path=" + path + ", edits=" + edits.size() + ", renameFrom="
				+ renameFrom.size() + ", fileActions=" + fileActions.size() + ", renameTo="
				+ renameTo.size() + "]";
	}

}
