package at.ac.wu.infobiz.projectmining.model;

import java.io.Serializable;

public class Position implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5900390766482394369L;
	int startLine;
	int numLines;
	public Position(int startLine, int numLines) {
		super();
		this.startLine = startLine;
		this.numLines = numLines;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numLines;
		result = prime * result + startLine;
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
		Position other = (Position) obj;
		if (numLines != other.numLines)
			return false;
		if (startLine != other.startLine)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Position [startLine=" + startLine + ", numLines=" + numLines
				+ "]";
	}
	
}
