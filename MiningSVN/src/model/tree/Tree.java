/**
 * 
 */
package model.tree;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import model.Event;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import reader.GITLogReader;
import reader.LogReader;
import test.TestLog;
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
	
	public static void main(String[] args) throws IOException
	{
		Tree t = new Tree();
//		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
//		Log log = new SVNLog(lr.readAll());
		LogReader<LogEntry> lr = new GITLogReader("resources/MiningCVS.log");
		Log log = new GITLog(lr.readAll());
		TestLog.toFile("/home/saimir/out.txt");
//		System.out.println("Read "+log.size()+" entries.");
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
		lr.close();
//		System.out.println(fem);
//		Collection<Change> c = log.getAllChanges();
//		for (Change ch : c) {
//	      System.out.println(ch.getPath());
//      }
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
//		t.add("/templates");
//		t.add("/slides");
//		t.add("The World/Asia/Iran");
//		t.add("/templates/checkliste_kostenplan.doc");
//		t.add("The World/Asia/China");    // Even if you insert only this statement.
//		// You get the desired output, 
//		// As any string not found is inserted
//		t.print();
//		t.printWithEvents();
		t.printEventTypes();	
	}
}
