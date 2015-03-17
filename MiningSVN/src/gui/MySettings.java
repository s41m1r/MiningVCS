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
	
	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.ganttchart.AbstractSettings#allowArrowKeysToScrollChart()
	 */
	@Override
	public boolean allowArrowKeysToScrollChart() {
	   // TODO Auto-generated method stub
	   return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.ganttchart.AbstractSettings#getMonthDayWidth()
	 */
	@Override
	public int getMonthDayWidth() {
	   // TODO Auto-generated method stub
	   return 36;
	}
	
	public int getHeaderMonthHeight() {
		return 36;
	}
	
	public int getEventHeight() {
		return 36;
	}

	public int getEventPercentageBarHeight() {
		return 4;
	}

	public int getHeaderDayHeight() {
		return 36;
	}

}
