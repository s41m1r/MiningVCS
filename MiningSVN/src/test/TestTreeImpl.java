package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Event;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.tree.Node;
import reader.GITLogReader;
import reader.LogReader;
import util.FileEventMap;
import util.TreeUtils;

public class TestTreeImpl {

	public static void main(String[] args) throws IOException {
		LogReader<LogEntry> lr = new GITLogReader("resources/abc.log");
		Log log = new GITLog(lr.readAll());
		lr.close();
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
//		Node n = TreeUtils.toTree(fem);
		Node n = abcTree();
//		TestLog.toFile("/home/saimir/out.txt");
		System.out.println(n);
		TreeUtils.mergeTree(n);
		System.out.println(TreeUtils.toStringOnlyEventType(n,0));
	}
	
	public static Node l1l2Tre(){
		Node a = new Node("a"); //root
		Node b = new Node("b");
		Node c = new Node("c");
		Node d = new Node("d");
		Node e = new Node("e");
		Node f = new Node("f");
		Node g = new Node("g");
		Node h = new Node("h");
		
		List<Node> childsOfA = new ArrayList<Node>();
		childsOfA.add(new Node(b.getValue()));
		childsOfA.add(new Node(e.getValue()));
		childsOfA.add(new Node(b.getValue()));
		childsOfA.add(new Node(b.getValue()));
		
		a.setChildList(childsOfA);
		b.getChildList().add(new Node("e"));
		
		System.out.println(childsOfA);
		
		return a;
	}
	
	public static Node abcTree(){
		Node a = new Node("a"); //root
		Node b = new Node("b");
		Node c = new Node("c");
		Node d = new Node("d");
		Node e = new Node("e");
		Node f = new Node("f");
		Node g = new Node("g");
		Node h = new Node("h");
		
		List<Node> childsOfA = new ArrayList<Node>();
		childsOfA.add(new Node(b.getValue()));
		childsOfA.add(new Node(e.getValue()));
		childsOfA.add(new Node(b.getValue()));
		childsOfA.add(new Node(b.getValue()));
		
		a.setChildList(childsOfA);
		b.getChildList().add(new Node("e"));
		
		System.out.println(childsOfA);
		
		return a;
	}
}
