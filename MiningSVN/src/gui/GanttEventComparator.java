/**
 * 
 */
package gui;

import java.util.Comparator;

import org.eclipse.nebula.widgets.ganttchart.GanttEvent;

/**
 * @author Saimir Bala
 *
 */
public class GanttEventComparator implements Comparator<GanttEvent> {
	
		@Override
		public int compare(GanttEvent o1, GanttEvent o2) {
			return o1.getActualStartDate().compareTo(o2.getActualStartDate());
		}
}
