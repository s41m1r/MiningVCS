package model;

import java.util.ArrayList;
import java.util.Collection;

/***
 * 
 * @author saimir
 * 
 * Activities are lists of lists like [[e1 e2 e3] [e4 e5] [e7 e8 e9]]
 *
 */

public class Activity {
	private Collection<ArrayList<Event>> eventsCollections;
	
	public Activity() {
		eventsCollections = new ArrayList<ArrayList<Event>>();
	}
	
	public Activity(Collection<ArrayList<Event>> activites){
		eventsCollections = activites;
	}

	public Collection<ArrayList<Event>> getEventsCollections() {
		return eventsCollections;
	}
	
	public void addChunk(ArrayList<Event> chunk){
		eventsCollections.add(chunk);
	}

	@Override
	public String toString() {
		return "Activity [eventsCollections=" + eventsCollections + "]";
	}
	
}
