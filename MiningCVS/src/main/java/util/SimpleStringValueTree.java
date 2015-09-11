/**
 * 
 */
package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import model.Change;
import model.Event;
import model.Log;
import model.LogEntry;
import model.svn.SVNLog;
import reader.LogReader;
import reader.SVNLogReader;

/**
 * @author Saimir Bala
 *
 */
class SimpleStringValueTree
{
	class Node
	{
		String data;
		ArrayList<Node> children;

		public Node(String data)
		{
			this.data = data;
			children = new ArrayList<Node>();
		}

		public Node getChild(String data)
		{
			for(Node n : children)
				if(n.data.equals(data))
					return n;

			return null;
		}

		@Override
		public String toString() {
			return "Node [data=" + data + ", children=" + children + "]";
		}
	}

	private Node root;

	public SimpleStringValueTree()
	{
		root = new Node("");
	}

	public boolean isEmpty()
	{
		return root==null;
	}

	public void add(String str)
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
				current.children.add(new Node(str));
				child = current.getChild(str);
			}
			current = child;
		}
		s.close();
	}

	public void print()
	{
		print(this.root, 0);
	}

	private void print(Node n, int level)
	{
		if(n==null)
			return;
		for(Node c : n.children)
		{
			for(int l=level;l>0;l--)
				System.out.print(" ");
			System.out.print("+-");
			System.out.println("["+level+"] "+c.data + " ");
			print(c, level+1);
		}
	}

	public static void main(String[] args) throws IOException
	{
		SimpleStringValueTree t = new SimpleStringValueTree();
		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		Log log = new SVNLog(lr.readAll());
//		TestLog.toFile("/home/saimir/out.txt");
		System.out.println("Read "+log.size()+" entries.");
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
		lr.close();
		Collection<Change> c = log.getAllChanges();
		for (Change ch : c) {
	      System.out.println(ch.getPath());
      }
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string);
		}
//		t.add("/templates");
//		t.add("/slides");
//		t.add("The World/Asia/Iran");
//		t.add("/templates/checkliste_kostenplan.doc");
//		t.add("The World/Asia/China");    // Even if you insert only this statement.
//		// You get the desired output, 
//		// As any string not found is inserted
		t.print();
	}
}