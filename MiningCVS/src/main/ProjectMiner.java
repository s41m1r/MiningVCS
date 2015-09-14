/**
 * 
 */
package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.tree.Tree;
import reader.GitStreamReader;
import util.FileEventMap;
import util.Opts;
import util.SysUtils;

/**
 * @author saimir
 *
 */
public class ProjectMiner {
	public static Tree getTree(InputStream in, int threshold) throws IOException{
		Tree result = null;
		GitStreamReader gsr = new GitStreamReader(in);
		List<LogEntry> logEntries =  gsr.readAll();
		gsr.close();
		
		Log log = new GITLog(logEntries);
		Map<String, List<Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
		
		Tree t = new Tree(); 
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
		result = t.aggr(threshold);
		result.printWithActivites();
		return result;
	}
	
	public static void main(String[] args) throws IOException {
//		String logString = "commit 6f3668d34114758df2ae11ffc5c3d86c37c1a884\n" + 
//				"Author: Salvatore Interos <salvatore.interos@siemens.com>\n" + 
//				"Date: Mon Aug 31 22:02:40 2015 +0200\n" + 
//				"\n" + 
//				"    Initial Commit\n" + 
//				"\n" + 
//				"A	/project/wp2/test/factory/SignalMappingProvider\n" + 
//				"A	/project/wp2/renderer/CarCollectionModule\n" + 
//				"\n" + 
//				"commit 7ad255b6c05ad50c84c276f17fc826f74271cd69\n" + 
//				"Author: Salvatore Interos <salvatore.interos@siemens.com>\n" + 
//				"Date: Wed Dec 09 07:01:17 2015 +0100\n" + 
//				"\n" + 
//				"    Bugfix\n" + 
//				"\n" + 
//				"A	/project/wp2/renderer/serializer/access/TrainRulesModule\n" + 
//				"M	/project/wp2/renderer/mapping/ApplicationCollectionFactory\n" + 
//				"A	/project/wp2/test/factory/manager/TurnDisplayRef\n" + 
//				"\n" + 
//				"commit a8f86f9ea4a266ac760e5e8a4679b158fa965808\n" + 
//				"Author: Salvatore Interos <salvatore.interos@siemens.com>\n" + 
//				"Date: Wed Jan 27 03:24:54 2016 +0100\n" + 
//				"\n" + 
//				"    Bugfix\n" + 
//				"\n" + 
//				"M	/project/wp2/renderer/mapping/TrackHandlerProvider\n" + 
//				"M	/project/wp2/test/factory/qualifier/SystemVerifierTest\n" + 
//				"\n" + 
//				"\n" + 
//				"commit c93f31f5c93b9177dff00fc0716601e274029232\n" + 
//				"Author: Cesare Manzella <cesare.manzella@siemens.com>\n" + 
//				"Date: Sun Oct 04 09:29:01 2015 +0200\n" + 
//				"\n" + 
//				"    Initial Commit\n" + 
//				"\n" + 
//				"A	/project/wp1/rules/CounterWriterProvider\n" + 
//				"A	/project/wp1/rules/StationManagerFactoryTest\n" + 
//				"";
//		InputStream is = new ByteArrayInputStream(logString.getBytes());
		
		Opts.parseArgs(args);
		String inputFile = Opts.optionValueMap.get(Opts.LOGFILE);
		int threshold = Integer.parseInt(Opts.optionValueMap.get(Opts.THRESHOLD));
		System.out.println(inputFile);
		InputStream is = new FileInputStream(inputFile);
		SysUtils.toFile(SysUtils.DEFAULT_OUTPUT_FILE);
		System.out.println(getTree(is, threshold));
	}
}
