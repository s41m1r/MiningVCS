/**
 * 
 */
package gui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;

/**
 * @author Saimir Bala
 *
 */
public class OurTreeData {

	
	private List<GanttEvent> collapsedEvents = new LinkedList<GanttEvent>();
	private List<GanttEvent> superEvents = new LinkedList<GanttEvent>();
	
	private GanttGroup ganttGroup =  null;
	
	/**
	 */
   public OurTreeData() {
   }

	/**
	 * @param group
	 */
   public OurTreeData(GanttGroup group) {
   	this.ganttGroup = group;
   }

	/**
	 * @return the collapsedEvents
	 */
   public List<GanttEvent> getCollapsedEvents() {
	   return collapsedEvents;
   }

	/**
	 * @param collapsedEvents the collapsedEvents to set
	 */
   public void setCollapsedEvents(List<GanttEvent> collapsedEvents) {
	   this.collapsedEvents = collapsedEvents;
   }

	/**
	 * @return the ganttGroup
	 */
   public GanttGroup getGanttGroup() {
	   return ganttGroup;
   }

	/**
	 * @param ganttGroup the ganttGroup to set
	 */
   public void setGanttGroup(GanttGroup ganttGroup) {
	   this.ganttGroup = ganttGroup;
   }

	/**
	 * @return the superEvents
	 */
   public List<GanttEvent> getSuperEvents() {
	   return superEvents;
   }

	/**
	 * @param superEvents the superEvents to set
	 */
   public void setSuperEvents(List<GanttEvent> superEvents) {
	   this.superEvents = superEvents;
   }
   
}
