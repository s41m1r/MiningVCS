/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.joda.time.Days;

import test.EventComparator;
import model.Activity;
import model.Event;
import model.tree.Node;

/**
 * @author Saimir Bala
 *
 */
public abstract class TreeUtils {
	
	
	/**
	 * @param fem
	 * @return a tree representation of the map
	 */
	public static Node toTree(Map<String, List<Event>> fem){
		Node root = new Node("/");
		Set<String> keys = fem.keySet();
		for (String filePath : keys) {
			Node n = toNode(filePath, fem.get(filePath));
			root.getChildList().add(n);
			n.setParent(root);
		}
		return root;
	}

	private static Node toNode(String filePath, List<Event> list) {
		Node n = null, last;
		last = n;
		Scanner s = new Scanner(filePath);
		s.useDelimiter("/");
		
		if(s.hasNext()){//create the first node
			n = new Node(s.next());
			last = n;
		}
		while (s.hasNext()) {
			String string = (String) s.next();
			Node n2 = new Node(string);
			last.getChildList().add(n2);
			
			if(!s.hasNext()){// leaf
				n2.setEventList(list);
			}
			//otherwise it's a directory. no need to set the event list
			n2.setParent(last);
			last = n2;
		}
		s.close();
		return n;
	}
	
	public static void printTree(Node root){
		if(root != null){
			System.out.println(" [ "+ root.getValue()+ 
					"->"+ root.getEventList()+" ]");
			for(Node childs : root.getChildList()){
				printTree(childs);
			}
		}
	}
	
	public static String toStringOnlyEventType(Node root, int level){
		String st = "";
		if(root != null){
			String s = "";
			for(Event n : root.getEventList()){
				s += " " +n.getType();
			}
			String lvl = "";
			for(int i=0;i<level;i++)
				lvl+=" ";
			
			st+=lvl+"+"+"[ "+ root.getValue()+"->"+ s +" ]";
			for(Node childs : root.getChildList()){
				st+="\n"+lvl+toStringOnlyEventType(childs, level+1);
			}
		}
		return st;
	}
	
	public static void mergeTree(Node root){
		if(root!=null){
			List<Node> seen = new ArrayList<Node>();
			mergeChilds(root.getChildList(), seen);
			for (Node child : root.getChildList()) {
				mergeTree(child, seen);
			}
		}
	}

	private static void mergeTree(Node root, List<Node> seen) {
		if(root!=null){
			mergeChilds(root.getChildList(), seen);
			for (Node child : root.getChildList()) {
				mergeTree(child, seen);
			}
		}
	}

	private static void mergeChilds(List<Node> childList, List<Node> seen) {
		for (Node node : childList) {
			if(seen.contains(node)){
				Node n = seen.get(seen.indexOf(node));
//				merge childs:
				n.getChildList().addAll(node.getChildList());
			}
			else{
				seen.add(node);
			}
		}
	}

	public static Collection<Event> collectSubEvents(Node root){
		Collection<Event> c = new ArrayList<Event>();
		c.addAll(root.getEventList());
		collectSubEvents(root, c);
		return c;
	}

	public static void collectSubEvents(Node root, Collection<Event> collectedEvents){
		if(root==null)
			return;
		List<Node> children = root.getChildList();
		for (Node ch : children) {
			collectedEvents.addAll(ch.getEventList());
			collectSubEvents(ch,collectedEvents);
		}
	}

	public static Activity aggregate(Node n, int threshold){
		Collection<Event> allEvents = collectSubEvents(n);
		Activity a = aggregateFromEventList(allEvents, threshold);
		return a;
	}

	private static Activity aggregateFromEventList(
			Collection<Event> allEvents, int threshold) {
		
		List<Event> all = new ArrayList<Event>(allEvents);
		all.sort(new EventComparator());
		Activity result = new Activity();
		Event lastEvent = all.get(0);
		ArrayList<Event> chunk = new ArrayList<Event>();
		for (Iterator<Event> it = all.iterator(); it.hasNext();) {
			Event thisEvent = it.next();
			if(Days.daysBetween(lastEvent.getStart(), thisEvent.getStart()).getDays() < threshold){
				chunk.add(thisEvent);
			}
			else{
				result.addChunk(chunk);
				chunk = new ArrayList<Event>();
				chunk.add(thisEvent);
			}
			lastEvent = thisEvent;
		}
		result.addChunk(chunk);
		return result;
	}
	
//	private static Node toTree(Node n1, Node n2){
//		if(n1 == null || n2 == null)
//			return null;
//		if(!n1.getValue().equals(n2.getValue()))
//			return null;
//		Node r = new Node(n1.getValue());
//		
//		while(!(n1.hasChildren() || 
//				n2.hasChildren())){
//			if(n1.getChildList().equals(n2.getChildList())){
//				r.addChildren(new Node(n1.getValue()));
//			}
//			r.addChildren(n1.getChildList());
//			r.addChildren(n2.getChildList());
//		}
//		return r;
//	}
}
