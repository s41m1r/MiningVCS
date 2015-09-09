package test;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.mysql.fabric.xmlrpc.base.Array;

import model.Event;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.svn.SVNLog;
import model.tree.Node;
import model.tree.Tree;
import reader.GITLogReader;
import reader.LogReader;
import reader.SVNLogReader;
import util.FileEventMap;
import util.Opts;
import util.TreeUtils;

public class TestTreeImpl {
	final static String outFile = "/home/saimir/directory-tree.txt";

	public static void main(String[] args) throws IOException {
		String in = Opts.getInputFile(args);
		
		if(in==null) 
			System.exit(-1);
		
		LogReader<LogEntry> lr = new GITLogReader(in);
		Log log = new GITLog(lr.readAll());
//		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
//		Log log = new SVNLog(lr.readAll());
		lr.close();
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
		
		Tree t = new Tree(); 
//		
//		System.out.println("Output written to "+outFile);
//		TestLog.toFile(outFile);
		
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
		
		t.printEventTypes();
		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add(0); 
		path.add(1);
		Node node = t.getNodeByPath(path);
		System.out.println(node);
		
	}
	
	public static Collection<Event> collectSubEvents(Node root){
		Collection<Event> c = new ArrayList<Event>();
		c.addAll(root.getEventList());
		collectSubEvents(root, c);
		return c;
	}
	
	private static void collectSubEvents(Node root, Collection<Event> collectedEvents){
		if(root==null)
			return;
		List<Node> children = root.getChildList();
		for (Node ch : children) {
			collectedEvents.addAll(ch.getEventList());
			collectSubEvents(ch,collectedEvents);
		}
	}
	
	
//	public static Node l1l2Tre(){
//		Node a = new Node("a"); //root
//		Node b = new Node("b");
//		Node c = new Node("c");
//		Node d = new Node("d");
//		Node e = new Node("e");
//		Node f = new Node("f");
//		Node g = new Node("g");
//		Node h = new Node("h");
//		
//		List<Node> childsOfA = new ArrayList<Node>();
//		childsOfA.add(new Node(b.getValue()));
//		childsOfA.add(new Node(e.getValue()));
//		childsOfA.add(new Node(b.getValue()));
//		childsOfA.add(new Node(b.getValue()));
//		
//		a.setChildList(childsOfA);
//		b.getChildList().add(new Node("e"));
//		
//		System.out.println(childsOfA);
//		
//		return a;
//	} 
	
}
