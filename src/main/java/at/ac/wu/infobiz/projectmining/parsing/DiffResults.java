/**
 * 
 */
package at.ac.wu.infobiz.projectmining.parsing;

import java.util.ArrayList;
import java.util.List;

import at.ac.wu.infobiz.projectmining.model.Edit;
import at.ac.wu.infobiz.projectmining.model.File;

/**
 * @author saimir
 *
 */
public class DiffResults {
	private File fileFrom;
	private File fileTo;
	private List<Edit> edits;
	
	public DiffResults() {
		edits = new ArrayList<Edit>();
	}
	
	public DiffResults(String fileFrom, String fileTo,
			List<Edit> edits) {
		
		if(fileFrom.startsWith("a/"))
			this.fileFrom = new File(fileFrom.substring(2));
		else
			this.fileFrom = new File(fileFrom);
		
		if(fileTo.startsWith("b/"))
			this.fileTo = new File(fileTo.substring(2));
		else
			this.fileTo = new File(fileTo);
		
		this.edits = edits;
	}

	public List<Edit> getEdits() {
		return edits;
	}


	public File getFileFrom() {
		return fileFrom;
	}

	public File getFileTo() {
		return fileTo;
	}


	public void setFileFrom(File fileFrom) {
		this.fileFrom = fileFrom;
	}

	public void setFileTo(File fileTo) {
		this.fileTo = fileTo;
	}

	@Override
	public String toString() {
		return "DiffResults [fileFrom=" + fileFrom + ", fileTo=" + fileTo
				+ ", edits=" + edits + "]";
	}	
	
}
