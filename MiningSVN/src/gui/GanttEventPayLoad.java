/**
 * 
 */
package gui;

import model.Event;

import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Saimir Bala
 *
 */
public class GanttEventPayLoad implements Comparable<GanttEventPayLoad>{

	private TreeItem treeItem;
	private model.Event eventData;

	/**
	 * @param treeItem
	 * @param eventData
	 */
	public GanttEventPayLoad(TreeItem treeItem, Event eventData) {
		super();
		this.treeItem = treeItem;
		this.eventData = eventData;
	}
	public model.Event getEventData() {
		return eventData;
	}
	public TreeItem getTreeItem() {
		return treeItem;
	}
	public void setEventData(model.Event eventData) {
		this.eventData = eventData;
	}
	public void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}
	@Override
	public String toString() {
		return "GanttEventPayLoad [treeItem=" + treeItem + ", eventData="
				+ eventData + "]";
	}
	
	public int compareTo(GanttEventPayLoad o) {
		return this.getEventData().getStart().compareTo(o.getEventData().getStart());
	}

}
