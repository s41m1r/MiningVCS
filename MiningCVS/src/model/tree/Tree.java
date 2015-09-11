/**
 * 
 */
package model.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import model.Activity;
import model.Event;
import util.TreeUtils;

/**
 * @author Saimir Bala
 *
 */
public class Tree {

	private Node root;

	public Tree(){
		root = new Node("");
	}

	public void add(String str) {
		Node current = root;
		Scanner s = new Scanner(str);
		s.useDelimiter("/");//no whitespace preceding "/"
		while(s.hasNext())
		{
			str = s.next();
			if(str.contains("(Copy from path")){
				while(s.hasNext()){
					str+=s.next();
				}
			}
			Node child = current.getChild(str);
			if(child==null)
			{
				current.getChildList().add(new Node(str));
				child = current.getChild(str);
			}
			current = child;
		}
		s.close();
	}

	public void add(String str, List<Event> eventList){
		Node current = root;
		Scanner s = new Scanner(str);
		s.useDelimiter("/");//no whitespace preceding "/"
		//		s.skip("[Copy from path].*");
		while(s.hasNext())
		{
			str = s.next();
			if(str.contains("(Copy from path")){
				while(s.hasNext()){
					str+=s.next();
				}
			}
			Node child = current.getChild(str);
			if(child==null)
			{
				current.getChildList().add(new Node(str));
				child = current.getChild(str);
			}
			current = child;
		}
		current.getEventList().addAll(eventList);
		s.close();
	}

	public Tree aggregateAt(Node n, int threshold){
		Activity a = TreeUtils.aggregate(n, threshold);
		return this.copy(n, a);
	}

	public Tree copy(Node n, Activity a){
		Tree t = new Tree();
		t.setRoot(root.copy(n, a));
		return t;
	}

	public Tree copyWithAggregationListsInNodes(int threshold) {
		return copyWithAggregationListsInNodes(this.root, threshold);
	}

	public Tree copyWithAggregationListsInNodes(Node n, int threshold){
		Tree t = new Tree();
		t.setRoot(root.copyWithAggregationLists(n, threshold));
		return t;
	}
	
	public Node getNodeByPath(Collection<Integer> path){
		return getNodeByPath(root, new ArrayList<Integer>(path));
	}


	private Node getNodeByPath(Node root, ArrayList<Integer> path) {
		if(path.size()==0)
			return root;
		Node nextChild = null;
		try{
			nextChild = root.getChildList().get(path.remove(0)); //get the first and remove it
		}
		catch(IndexOutOfBoundsException e){
			System.err.println("Path not valid.");
			return null;
		}
		return getNodeByPath(nextChild, path);
	}

//	private void printEventTypes(int level){
//		printEventTypes(root,0,level);
//	}

	public Node getRoot() {
		return root;
	}

	public boolean isEmpty(){
		return root==null;
	}

	public void print(){
		print(this.root, 1, false);
	}

	private void print(Node n, int level, boolean withEvents)
	{
		if(n==null)
			return;
		for(Node c : n.getChildList())
		{
			for(int l=level;l>0;l--)
				System.out.print(" ");
			System.out.print("+-");
			System.out.println("["+level+"] "+c.getValue() + " "+(withEvents? c.getEventList(): ""));
			print(c, level+1, withEvents);
		}
	}

	public void printAll(){
		printAll(this.getRoot(),0);
	}
	
	
	private void printAll(Node n, int level)
	{
		if(n==null)
			return;
		for(Node c : n.getChildList())
		{
			for(int l=level;l>0;l--)
				System.out.print(" ");
			System.out.print("+-");
			System.out.println("["+level+"] "+c.getValue() + 
					" "+c.getActivity()+" eventList: "+c.getEventList());
			printAll(c, level+1);
		}
	}

	public void printEventTypes(){
		printEventTypes(root,0);
	}
	
	private void printEventTypes(Node n, int level)
	{
		if(n==null)
			return;
				
		for(Node c : n.getChildList())
		{
			for(int l=level;l>0;l--)
				System.out.print(" ");
			System.out.print("+-");
			List<Event> eList = c.getEventList();

			String s = "";
			for (Event event : eList) {
				s+=" "+event.getType();
			}
			System.out.println("["+level+"] "+c.getValue() + " "+s);
			printEventTypes(c, level+1);
		}		
	}
	
	public void printWithEvents(){
		print(this.root, 1, true);
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	@Override
	public String toString() {
		return "Tree "+toString(this.getRoot(),0);
	}
	
	private String toString(Node n, int level) {
		if(n==null)
			return null;
		String res = "";
		for(int l=level;l>0;l--)
			res+=" ";
		for(Node c: n.getChildList()){
			res+=c+"\n";
			res+=toString(c,level+1);
		}
		return res;
	}

	public String toStringAll(){
		return this.getRoot().toStringAll();
	}
	
}
