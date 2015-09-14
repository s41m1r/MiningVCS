package test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.tree.Tree;
import reader.GITLogReader;
import reader.LogReader;
import util.FileEventMap;
import util.Opts;
import util.SysUtils;

public class TestTreeImpl {
	final static String outFile = "/home/saimir/directory-tree.txt";
	public static int threshold = 0; //number of days

	public static void main(String[] args) throws IOException {
		
		Opts.parseArgs(args);
		
		String in = Opts.optionValueMap.get(Opts.LOGFILE);
		String thresholdString = Opts.optionValueMap.get(Opts.THRESHOLD);
		threshold = Integer.parseInt(thresholdString);
		
		if(in==null) 
			System.exit(-1);
		
		LogReader<LogEntry> lr = new GITLogReader(in);
		Log log = new GITLog(lr.readAll());
		lr.close();
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
		
		Tree t = new Tree(); 
		
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
		SysUtils.toFile("/home/saimir/out.txt");
		Tree aggregatedCopy = t.copyWithAggregationListsInNodes(threshold);
		System.out.println(aggregatedCopy);
	}
	
//	private static ArrayList<Integer> toPath(String inp) {
//		String[] pathStringArray = inp.split("/");
//		ArrayList<Integer> path = new ArrayList<Integer>();
//		for (int i = 0; i < pathStringArray.length; i++) {
//			String stringPath = pathStringArray[i];
//			path.add(Integer.parseInt(stringPath));
//		}
//		return path;
//	}
	
	
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
