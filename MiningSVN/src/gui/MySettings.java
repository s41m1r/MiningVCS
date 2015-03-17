/**
 * 
 */
package gui;

import java.util.Calendar;
import java.util.Locale;

import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Saimir Bala
 *
 */
public class MySettings extends DefaultSettings {
	
	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.ganttchart.AbstractSettings#lockHeaderOnVerticalScroll()
	 */
	@Override
	public boolean lockHeaderOnVerticalScroll() {
	   // TODO Auto-generated method stub
	   return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.ganttchart.AbstractSettings#drawEventsDownToTheHourAndMinute()
	 */
	@Override
	public boolean drawEventsDownToTheHourAndMinute() {
	   // TODO Auto-generated method stub
	   return false;
	}

}
