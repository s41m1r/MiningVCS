package at.ac.wu.infobiz.projectmining.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Rename {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne(optional=false)
	private File from;
	
	@ManyToOne(optional=false)
	private File to;
	
	@ManyToOne(optional=false)
	private Commit commit;
	
	public Rename() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public File getFrom() {
		return from;
	}

	public void setFrom(File from) {
		this.from = from;
	}

	public File getTo() {
		return to;
	}

	public void setTo(File to) {
		this.to = to;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rename [id=" + id + ", from=" + from + ", to=" + to
				+ ", commit=" + commit + "]";
	}
	
}
