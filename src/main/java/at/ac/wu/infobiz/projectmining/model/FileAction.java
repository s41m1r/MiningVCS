package at.ac.wu.infobiz.projectmining.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FileAction {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne(optional=false)
	private File file;
	
	@ManyToOne(optional=false)
	private Commit commit;
	
	@Enumerated(EnumType.STRING)
	private ActionType type;
	
	@Column
	private Integer totalLines;
	
	public FileAction() {
	}

	public Commit getCommit() {
		return commit;
	}

	public File getFile() {
		return file;
	}

	public Integer getId() {
		return id;
	}

	public Integer getTotalLines() {
		return totalLines;
	}

	public ActionType getType() {
		return type;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTotalLines(Integer totalLines) {
		this.totalLines = totalLines;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "FileAction [id=" + id + ", type=" + type + ", totalLines=" + totalLines + "]";
	}
	
}
