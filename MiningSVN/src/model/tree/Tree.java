/**
 * 
 */
package model.tree;

import gui.OurTreeData;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import model.Event;
import model.Log;

import org.eclipse.nebula.widgets.ganttchart.AdvancedTooltip;
import org.eclipse.nebula.widgets.ganttchart.ColorCache;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.TreeItem;
import org.joda.time.DateTime;

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
	
	public static Map<String, Color> mapCommitToColor(Map<String, List<String>> commitFileMap){
		Map<String, Color> resultMap = new HashMap<String, Color>();
		Set<String> keys = commitFileMap.keySet();
		for (String key : keys) {
	      resultMap.put(key, ColorCache.getRandomColor());
      }
		return resultMap;
	}

	public void fillInGanttTree(TreeItem root, GanttChart chart, GanttEvent scopeEvent, Log log){
//		final TreeItem root = new TreeItem(tree, SWT.BORDER);
		// our root node that matches our scope
		root.setText("Project structure");		
		root.setExpanded(true);
		Map<String, Color> commitColorMap = mapCommitToColor(FileEventMap.buildCommitFileMap(log));
//		System.out.println(commitColorMap);
		fillInGanttTree(root, chart, this.root, scopeEvent, commitColorMap);
	}

	public void fillInGanttTree(TreeItem root, GanttChart chart, Node n, GanttEvent scopeEvent, Map<String, Color> commitFileMap){

		if(n==null)
			return;
		GanttEvent ge = null;
		
		n.getChildList().sort(new Comparator<Node>() {
			@Override
         public int compare(Node o1, Node o2) {
				if (o1.hasChildren() && o2.hasChildren() || !o1.hasChildren() && !o2.hasChildren()){
					return o1.getValue().compareTo(o2.getValue());
				} else {
					if (o1.hasChildren()){
						return 1;
					} else {
						return -1;
					}
				}
         }
		});
		
		for(Node c : n.getChildList()){
			List<Event> events = c.getEventList();
//			System.out.println(events);
			GanttGroup group = new GanttGroup(chart);
			for (Event event : events) {
				Calendar start = Calendar.getInstance();
				Calendar end = new DateTime(0).toCalendar(Locale.ENGLISH);
				start = event.getStart().toCalendar(Locale.ENGLISH);
//				System.out.println(start.getTime());
				end = event.getEnd()==null? start: event.getEnd().toCalendar(Locale.ENGLISH);
				ge = new GanttEvent(chart, "",
//						"["+event.getAuthor().split("@|<")[0]+
						//" - "+event.getFileID()+
//						" - "+event.getType()+"]",
						start, end,5);
				ge.setTextDisplayFormat("");
				ge.setGradientStatusColor(commitFileMap.get(event.getCommitID()));
				AdvancedTooltip att = new AdvancedTooltip(
						"Commit ID: "+event.getCommitID(), 
						"Author:"+event.getAuthor().split("@|<")[0]+
						"\n"+event.getType()+
						"\n"+event.getFileID()+
						"\nTimestamp: "+event.getStart());
				
				ge.setAdvancedTooltip(att);
				group.addEvent(ge);
				ge.setVerticalEventAlignment(SWT.CENTER);
				scopeEvent.addScopeEvent(ge);
//				ge.hideAllChildren();
			}

			TreeItem ti = new TreeItem(root, SWT.NONE);
			ti.setText(c.getValue());
			ti.setExpanded(true);
//			// note how we set the data to be the event for easy access in the tree listeners later on
			ti.setData(new OurTreeData(group));
//			ti.setBackground(commitFileMap.get(c.getValue()));

			fillInGanttTree(ti, chart, c, scopeEvent, commitFileMap);
		}
	}	
}
