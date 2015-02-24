/**
 * 
 */
package model.tree;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import model.Event;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import util.FileEventMap;

/**
 * @author Saimir Bala
 *
 */
public class Tree {

	private Node root;

	public Tree()
	{
		root = new Node("");
	}

	public boolean isEmpty()
	{
		return root==null;
	}

	public Node getRoot() {
		return root;
	}

	public void add(String str, List<Event> eventList)
	{
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

	public void add(String str)
	{
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

	public void print()
	{
		print(this.root, 1, false);
	}

	public void printWithEvents()
	{
		print(this.root, 1, true);
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

	public void fillInGanttTree(org.eclipse.swt.widgets.Tree tree, GanttChart chart, GanttEvent scopeEvent){
		final TreeItem root = new TreeItem(tree, SWT.BORDER);
		// our root node that matches our scope
		root.setText("Project structure");
		root.setExpanded(true);
		fillInGanttTree(root, chart, this.root, scopeEvent);		
	}

	public void fillInGanttTree(TreeItem root, GanttChart chart, Node n, GanttEvent scopeEvent){

		if(n==null)
			return;

		GanttEvent ge = null;
		TreeItem ti = null;

		for(Node c : n.getChildList()){

			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();

			List<Event> events = c.getEventList();
			System.out.println(events);
			for (Event event : events) {
				start = event.getStart().toCalendar(Locale.ENGLISH);
				end = event.getEnd()==null? start: event.getEnd().toCalendar(Locale.ENGLISH);
				ge = new GanttEvent(chart, event.getAuthor()+" "+event.getType(),start, end,100);
				ge.setVerticalEventAlignment(SWT.CENTER);
				ti = new TreeItem(root, SWT.NONE);
				ti.setExpanded(true);
				ti.setText(c.getValue());
				// note how we set the data to be the event for easy access in the tree listeners later on
				ti.setData(ge);
				scopeEvent.addScopeEvent(ge);
			}

			fillInGanttTree(ti, chart, c, scopeEvent);
		}

		//		ti = new TreeItem(root, SWT.NONE);
		//		ti.setExpanded(true);
		//		ti.setText(n.getValue());
		//		// note how we set the data to be the event for easy access in the tree listeners later on
		//		ti.setData(ge);

	}
}
