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
	private Collection<Collection<Event>> eventsCollections;
	
	public Activity() {
		eventsCollections = new ArrayList<Collection<Event>>();
	}
	
	public Activity(Collection<Collection<Event>> newCollections){
		eventsCollections = newCollections;
	}

	public Collection<Collection<Event>> getEventsCollections() {
		return eventsCollections;
	}
	
	public void addChunk(Collection<Event> chunk){
		eventsCollections.add(chunk);
	}
	
}
