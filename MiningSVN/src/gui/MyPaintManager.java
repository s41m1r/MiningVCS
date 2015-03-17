/**
 * 
 */
package gui;

import org.eclipse.nebula.widgets.ganttchart.DefaultPaintManager;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.IColorManager;
import org.eclipse.nebula.widgets.ganttchart.ISettings;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author Saimir Bala
 *
 */
public class MyPaintManager extends DefaultPaintManager{
	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.ganttchart.AbstractPaintManager#drawEvent(org.eclipse.nebula.widgets.ganttchart.GanttComposite, org.eclipse.nebula.widgets.ganttchart.ISettings, org.eclipse.nebula.widgets.ganttchart.IColorManager, org.eclipse.nebula.widgets.ganttchart.GanttEvent, org.eclipse.swt.graphics.GC, boolean, boolean, int, int, int, int, org.eclipse.swt.graphics.Rectangle)
	 */
	@Override
	public void drawEvent(GanttComposite ganttComposite, ISettings settings,
	      IColorManager colorManager, GanttEvent event, GC gc,
	      boolean isSelected, boolean threeDee, int dayWidth, int xStart, int y,
	      int eventWidth, Rectangle bounds) {
	   // TODO Auto-generated method stub
//		System.out.println("quiiiiiiii");
	   super.drawEvent(ganttComposite, settings, colorManager, event, gc, isSelected,
	         threeDee, dayWidth, xStart, y-24, eventWidth, new Rectangle(bounds.x, bounds.y, bounds.width+24, bounds.width+24));
	}
	
}