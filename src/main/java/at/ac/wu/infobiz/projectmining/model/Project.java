package at.ac.wu.infobiz.projectmining.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Project {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;
	
	@OneToMany(mappedBy="project")
	private Collection<Commit> commits;
	
	public Project() {
		commits = new ArrayList<Commit>();
	}
	
	public void addCommit(Commit c){
		commits.add(c);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Commit> getCommits() {
		return commits;
	}

	public void setCommits(Collection<Commit> commits) {
		this.commits = commits;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + "]";
	}
	
}
