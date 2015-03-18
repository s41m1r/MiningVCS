/**
 * 
 */
package gui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;

/**
 * @author Saimir Bala
 *
 */
public class TreeData{
	/**
	 * 
	 */
	private HashSet<GanttEvent> hiddenEvents = new HashSet<GanttEvent>();;
	private HashSet<GanttEvent> shownEvents = new HashSet<GanttEvent>();
	private GanttGroup ganttGroup;

	/**
	 * 
	 */
	public TreeData() {
	}

	public TreeData(GanttGroup group){
		ganttGroup = group;
	}

	public Set<GanttEvent> getHiddenEvents() {
		return hiddenEvents;
	}

	public void setHiddenEvents(Set<GanttEvent> hiddenEvents) {
		this.hiddenEvents = new HashSet<GanttEvent>(hiddenEvents);
	}

	public Set<GanttEvent> getShownEvents() {
		return shownEvents;
	}

	public void setShownEvents(Set<GanttEvent> shownEvents) {
		this.shownEvents = new HashSet<GanttEvent>(shownEvents);
	}

	public GanttGroup getGanttGroup() {
		return ganttGroup;
	}

	public void setGanttGroup(GanttGroup ganttGroup) {
		this.ganttGroup = ganttGroup;
	}

	public void collapse(){
		hiddenEvents.addAll(shownEvents);
		shownEvents.clear();
		update();
	}

	private void update(){
		for (GanttEvent ganttEvent : hiddenEvents) {
			ganttEvent.setHidden(true);
		}
		for (GanttEvent ganttEvent : shownEvents) {
			ganttEvent.setHidden(false);
		}
		for (GanttEvent ganttEvent : (List<GanttEvent>)ganttGroup.getEventMembers()) {
	      if(hiddenEvents.contains(ganttEvent))
	      	ganttEvent.setHidden(true);
	      if(shownEvents.contains(ganttEvent))
	      	ganttEvent.setHidden(false);
      }
	}

	@Override
	public String toString() {
		String hiddenEventsString = null;
		String shownEventsString = null;
		String groupEventsString = null;

		Object[] hidden = hiddenEvents.toArray();
		if(hidden.length>0){
			hiddenEventsString="";
			for (int i = 0; i < hidden.length-1; i++) {
				hiddenEventsString+=hidden[i] + ",";
			}
			hiddenEventsString+=hidden[hidden.length-1];
		}

		Object[] shown = shownEvents.toArray();

		if(shown.length>0){
			shownEventsString="";
			for (int i = 0; i < shown.length-1; i++) {
				shownEventsString+=shown[i] + ",";
			}
			shownEventsString+=shown[shown.length-1];
		}

		List<GanttEvent> group = this.getGanttGroup().getEventMembers();
		if(group.size()>0){
			groupEventsString="";
			for (int i = 0; i < group.size()-1; i++) {
				groupEventsString+=group.get(i).getData() + ",";
			}
			groupEventsString+=group.get(group.size()-1);
		}

		return "TreeData [hiddenEvents=" + hiddenEventsString + ", shownEvents="
		+ shownEventsString + ", ganttGroup=" + groupEventsString + "]";
	}   
}
