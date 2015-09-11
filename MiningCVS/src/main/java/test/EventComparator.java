package test;

import java.util.Comparator;

import model.Event;

public class EventComparator implements Comparator<Event> {

	@Override
	public int compare(Event o1, Event o2) {
		if(o1.getStart().isBefore(o2.getStart().getMillis()))
			return -1;
		else if(o1.getStart().isAfter(o2.getStart().getMillis()))
			return 1;
		else return 0;
	}

}
