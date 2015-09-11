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
import model.tree.Tree;
import reader.GITLogReader;
import reader.LogReader;
import util.FileEventMap;
import util.Opts;

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
		
		System.out.println("Tree written to "+outFile);
		TestLog.toFile(outFile);
		t.printEventTypes();
//		TestLog.toConsole();
		TestLog.toFile(outFile+"2");
		Tree aggregatedCopy = t.copyWithAggregationListsInNodes();
//		System.out.println(aggregatedCopy.toStringAll());
//		aggregatedCopy.printAll();
		System.out.println(aggregatedCopy);
//		ArrayList<Integer> path = new ArrayList<Integer>();
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		
//		String inp = "";
//		while(true){
//			TestLog.toConsole();
//			System.out.print("Enter a path to the node to aggregate (e.g 0/0/1): ");
//			inp=br.readLine();
//			if(inp==null || inp.equals("quit")){
//				System.out.println("Bye.");
//				break;
//			}
//			path = toPath(inp);
//			Node node = t.getNodeByPath(path);
//			System.out.println(node);
//			Activity a = TreeUtils.aggregate(node);
//			Collection<ArrayList<Event>> col = a.getEventsCollections();
//			int i=0;
//			for (ArrayList<Event> chunk : col) {
//				System.out.println(++i+"-chunk: ["+chunk.get(0).getStart()+
//						","+chunk.get(chunk.size()-1).getStart()+"], "+chunk.size()+
//						" events.");
//			}
//			Tree aggregated = t.aggregateAt(node);
//			TestLog.toFile(outFile+"2");
//			System.out.println(aggregated.getRoot().treeToString());
//		}
	}
	
	private static ArrayList<Integer> toPath(String inp) {
		String[] pathStringArray = inp.split("/");
		ArrayList<Integer> path = new ArrayList<Integer>();
		for (int i = 0; i < pathStringArray.length; i++) {
			String stringPath = pathStringArray[i];
			path.add(Integer.parseInt(stringPath));
		}
		return path;
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
