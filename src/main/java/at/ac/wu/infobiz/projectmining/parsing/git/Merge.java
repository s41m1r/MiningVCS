/**
 * 
 */
package at.ac.wu.infobiz.projectmining.parsing.git;


/**
 * @author Saimir Bala
 *
 */
public class Merge {
	
	private String thisCommitRevisionId;
	private String firstParentRevisionId;
	private String secondParentRevisionId;
	
	
	public Merge(String thisCommitRevisionId, String firstParentRevisionId,
			String secondParentRevisionId) {
		this.thisCommitRevisionId = thisCommitRevisionId;
		this.firstParentRevisionId = firstParentRevisionId;
		this.secondParentRevisionId = secondParentRevisionId;
	}


	/**
	 * @return the thisCommitRevisionId
	 */
	public String getThisCommitRevisionId() {
		return thisCommitRevisionId;
	}


	/**
	 * @param thisCommitRevisionId the thisCommitRevisionId to set
	 */
	public void setThisCommitRevisionId(String thisCommitRevisionId) {
		this.thisCommitRevisionId = thisCommitRevisionId;
	}


	/**
	 * @return the firstParentRevisionId
	 */
	public String getFirstParentRevisionId() {
		return firstParentRevisionId;
	}


	/**
	 * @param firstParentRevisionId the firstParentRevisionId to set
	 */
	public void setFirstParentRevisionId(String firstParentRevisionId) {
		this.firstParentRevisionId = firstParentRevisionId;
	}


	/**
	 * @return the secondParentRevisionId
	 */
	public String getSecondParentRevisionId() {
		return secondParentRevisionId;
	}


	/**
	 * @param secondParentRevisionId the secondParentRevisionId to set
	 */
	public void setSecondParentRevisionId(String secondParentRevisionId) {
		this.secondParentRevisionId = secondParentRevisionId;
	}
	
}
