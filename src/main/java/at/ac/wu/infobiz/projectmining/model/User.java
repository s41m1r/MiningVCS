package at.ac.wu.infobiz.projectmining.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7070250951763951247L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	@Column(name="name")
	private String name;
	@Column(name="email")
	private String email;
	@OneToMany(mappedBy="user", cascade={CascadeType.ALL})
	private Collection<Commit> commits;
	
	public User() {
		commits = new HashSet<Commit>();
	}
	
	public void addCommit(Commit c){
		commits.add(c);
	}
		
	/**
	 * @return the commits
	 */
	public Collection<Commit> getCommits() {
		return commits;
	}

	/**
	 * @param commits the commits to set
	 */
	public void setCommits(Collection<Commit> commits) {
		this.commits = commits;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + "]";
	}
	
}
