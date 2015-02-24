/**
 * 
 */
package model.tree;

import java.util.ArrayList;
import java.util.List;

import model.Event;

/**
 * @author Saimir Bala
 *
 */
public class Node {
	private String value;
	private List<Event> eventList;
	private Node parent;
	private List<Node> childList;

	public Node() {
		this.eventList = new ArrayList<Event>();
		this.childList = new ArrayList<Node>();
	}

	public Node(String value) {
		this.value = value;
		this.eventList = new ArrayList<Event>();
		this.childList = new ArrayList<Node>();
	}

	public Node(String value, List<Event> eventList, Node previous,
			List<Node> childList) {
		super();
		this.value = value;
		this.eventList = eventList;
		this.parent = previous;
		this.childList = childList;
	}
	/**
	 * @return the childList
	 */
	public List<Node> getChildList() {
		return childList;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return this==null;
		Node o = (Node) obj;
		return value.equals(o.getValue());
	}

	/**
	 * @return the eventList
	 */
	public List<Event> getEventList() {
		return eventList;
	}
	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param childList the childList to set
	 */
	public void setChildList(List<Node> childList) {
		this.childList = childList;
	}

	/**
	 * @param eventList the eventList to set
	 */
	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Node p) {
		parent = p;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String st = "Node [";
		st+= value==null? "" : value;
		st+= eventList==null || eventList.isEmpty()? "" : eventList;
		st+= parent==null? "" : parent;
		st+= childList==null || eventList.isEmpty()? "" : childList;
		st+="]";
		return st;
	}
	
	public boolean hasChildren(){
		return this.childList==null || this.childList.isEmpty();
	}
	
	public void addChildren(List<Node> children){
		this.childList.addAll(children);
	}

	/**
	 * @param str
	 * @return
	 */
   public Node getChild(String data) {
   	for(Node n : childList)
			if(n.value.equals(data))
				return n;
	   return null;
   }

}
