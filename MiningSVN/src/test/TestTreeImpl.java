package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import util.TreeUtils;

public class TestTreeImpl {

	public static void main(String[] args) throws IOException {
//		LogReader<LogEntry> lr = new GITLogReader("resources/MiningCVS.log");
//		Log log = new GITLog(lr.readAll());
		LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
		Log log = new SVNLog(lr.readAll());
		lr.close();
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
//		Node n = TreeUtils.toTree(fem);
		TestLog.toFile("/home/saimir/out.txt");
		Tree t = new Tree();
		
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
//		t.print();
//		t.printWithEvents();
		t.printEventTypes();	
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
