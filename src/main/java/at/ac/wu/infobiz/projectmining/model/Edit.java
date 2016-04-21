package at.ac.wu.infobiz.projectmining.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Edit {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column
	private Integer linesAdded;
	@Column
	private Integer linesRemoved;

	@ManyToOne
	private Commit commit;

	@ManyToOne
	private File file;

	@Column
	private Position fromPos;

	@Column
	private Position toPos;

	public Edit() {
	}
	public Commit getCommit() {
		return commit;
	}

	public File getFile() {
		return file;
	}

	public Position getFromPos() {
		return fromPos;
	}

	public Integer getId() {
		return id;
	}

	public Integer getLinesAdded() {
		return linesAdded;
	}

	public Integer getLinesRemoved() {
		return linesRemoved;
	}

	public Position getToPos() {
		return toPos;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFromPos(Position fromPos) {
		this.fromPos = fromPos;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLinesAdded(Integer linesAdded) {
		this.linesAdded = linesAdded;
	}

	public void setLinesRemoved(Integer linesRemoved) {
		this.linesRemoved = linesRemoved;
	}

	public void setToPos(Position toPos) {
		this.toPos = toPos;
	}
	
	@Override
	public String toString() {
		return "Edit [id=" + id + ", linesAdded=" + linesAdded + ", linesRemoved=" + linesRemoved + ", fromPos="
				+ fromPos + ", toPos=" + toPos + "]";
	}

}
