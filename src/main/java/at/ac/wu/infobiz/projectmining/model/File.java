package at.ac.wu.infobiz.projectmining.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="File")
public class File implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5044129239587030486L;
	@Id()
	private String path;
	@OneToMany(mappedBy="file", cascade={CascadeType.ALL})
	public Collection<Edit> edits;
	@OneToMany(mappedBy="from")
	public Collection<Rename> renameFrom;
	@OneToMany(mappedBy="file")
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
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edits == null) ? 0 : edits.hashCode());
		result = prime * result
				+ ((fileActions == null) ? 0 : fileActions.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((renameFrom == null) ? 0 : renameFrom.hashCode());
		result = prime * result
				+ ((renameTo == null) ? 0 : renameTo.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "File [path=" + path + ", edits=" + edits.size() + ", renameFrom="
				+ renameFrom.size() + ", fileActions=" + fileActions.size() + ", renameTo="
				+ renameTo.size() + "]";
	}

}
