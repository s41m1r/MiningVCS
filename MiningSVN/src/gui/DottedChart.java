package gui;

/**
 * @author Saimir Bala
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Log;
import model.LogEntry;
import model.svn.SVNLog;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.eclipse.nebula.widgets.ganttchart.ColorCache;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttControlParent;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Interval;

import reader.GITLogReader;
import reader.LogReader;
import reader.SVNLogReader;
import util.FileEventMap;

/**
 * This Snippet shows how to create a Tree on the left and a GanttChart widget on the right where the row heights match for both the tree and the chart. It also shows some simple
 * examples of how to tie some listeners with method calls to the GANTT chart. <br>
 * <br>
 * Important:<br>
 * Please note that this is a "Best attempt" at matching a tree and events in the chart. Some aspects may not be perfect as it's hard to sync two completely different widgets in
 * general, and even harder when one is a native widget that can look different in many different ways, and one is a GANTT chart that can look different as well.
 * <br><br>
 * Also, please note that this is an Example. There are probably a thousand ways of accomplishing something, so if this does not suit your needs, make the necessary changes.
 * <br><br>
 * Some notes:<br><br>
 * - Setting a fixed row height to a lesser value than the minimum row height in the tree/table will cause events not to line up. The GANTT chart supports tighter rows than the table/tree.<br>
 * - If you use a global fixed row override, individual fixed row heights are ignored. If you want different-height rows, set them manually.<br>
 * - Setting a vertical alignment is supported individually on both global row height overrides and normal fixed row height settings.<br>
 */
public class DottedChart {

	public static final int NUM_OF_DAYS_THRESHOLD = 7;
//	private static final String gitLogFileName =  "/home/saimir/git/MiningCVS/MiningSVN.log";
//	private static final String gitLogFileName =  "/home/saimir/git/MiningCVS/MiningSVN/resources/commitlog123_20150902.log";
	private static final String gitLogFileName =  "resources/adobe.log";
	private static final String svnLogFileName = null;//"resources/20150302_SNV_LOG_FROM_Papers_new.log";
	private static String fileName = null;

	public static void main(String[] args) {
		// standard display and shell etc
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Gantt Chart");
		shell.setSize(900, 500);
		shell.setLayout(new FillLayout());

		// split the view horizontally (which makes the splitter vertical)
		SashForm sf = new SashForm(shell, SWT.HORIZONTAL);

		// create the control parent. This composite expects either a Tree or a Table, anything else or a multiple
		// widgets and you will have to create your own class to control the layout.
		// see the source of the GanttControlParent for an idea, it's not overly difficult and could possibly be done using a normal
		// layout manager where you set the top margin to the height of the
		// calculated GANTT header
		final GanttControlParent left = new GanttControlParent(sf, SWT.NONE);

		// our GANTT chart, will end up on the right in the sash
		final GanttChart chart = new GanttChart(sf, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		//		final ScrolledComposite sc2 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		// we will be using method calls straight onto the chart itself, so we set it to a variable
		final GanttComposite ganttComposite = chart.getGanttComposite();

		// values we will be using further down (see comments in related sections)
		// row height
		final int oneRowHeight = 24;
		// spacer between each event, in this case 2 pixels as the horizontal lines in the tree take up 2 pixels per section (1 top, 1 bottom)
		final int spacer = 2;

		// usually whether to draw certain things are fetched from the ISettings implementing class, but there are a few overrides available for setting
		// non-default values, two of those are the options to draw horizontal and vertical lines. Here we flip the defaults to disable vertical lines but to show horizontal lines.
		ganttComposite.setDrawHorizontalLinesOverride(true);
		ganttComposite.setDrawVerticalLinesOverride(true);

		// set each item height on the chart to be the same height as one item in the tree. This call basically sets the fixed row height for each event instead of
		// setting it programatically. It's just a convenience method.
		// we take off the spacer as we're setting the row height which doesn't account for spacing, spacing is between rows, not in rows.
		ganttComposite.setFixedRowHeightOverride(oneRowHeight-spacer);

		// if you zoom in closely on the tree you'll see that the horizontal lines (that we activated) take up 2 in space (one at top, one at bottom)
		// so we space the events using that value
		ganttComposite.setEventSpacerOverride(spacer);

		// as we want the chart to be created on the right side, we created the TreeControlParent without the chart as a parameter
		// but as that control needs the chart to operate, we set it here (this is a must or you won't see a thing!)
		left.setGanttChart(chart);

		// create the tree. As it goes onto our special composite that will align it, we don't have to do any special settings on it
		final Tree tree = new Tree(left, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		//		final Tree tree = new Tree(left,SWT.BORDER);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// normally a tree item height on XP is 16 pixels. This is rather tight for a GANTT chart as it leaves little space for connecting lines etc.
		// As we want some air, we force each item height to be 24 pixels.
		tree.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = oneRowHeight-spacer;
			}
		});

		GanttGroup scopeGroup = new GanttGroup(chart);

		GanttGroup rootGroup = new GanttGroup(chart);
		final GanttEvent scopeEvent = new GanttEvent(chart, "Scope");
		scopeEvent.setVerticalEventAlignment(SWT.CENTER);
		scopeEvent.setTextDisplayFormat("#name#");
		scopeEvent.setName("");
		scopeGroup.addEvent(scopeEvent);
		//		scopeEvent.setMoveable(false);

		// our root node that matches our scope
		final TreeItem root = new TreeItem(tree, SWT.BORDER);
		Log log = null; 
		LogReader<LogEntry> lr = null;
		try {
			
			if(gitLogFileName!=null){
				lr = new GITLogReader(gitLogFileName);
				fileName = gitLogFileName;
			}
			
			else if(svnLogFileName!=null){
				lr = new SVNLogReader(svnLogFileName);
				fileName = svnLogFileName;
			}
			
			//			LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
//			LogReader<LogEntry> lr = new SVNLogReader("resources/shape_proposal.log");
//			LogReader<LogEntry> lr = new SVNLogReader("resources/running_example.log");
			//			LogReader<LogEntry> lr = new SVNLogReader("resources/20150302_SNV_LOG_FROM_Study_new.log");
//						LogReader<LogEntry> lr = new SVNLogReader("resources/20150302_SNV_LOG_FROM_TRAC_new.log");
//						LogReader<LogEntry> lr = new SVNLogReader("resources/20150302_SNV_LOG_FROM_Papers_new.log");
			//			LogReader<LogEntry> lr = new SVNLogReader("resources/out.log");
//						LogReader<LogEntry> lr = new GITLogReader("resources/MiningCVS.log");
//			LogReader<LogEntry> lr = new GITLogReader("resources/abc.log");
			log = new SVNLog(lr.readAll());
			lr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final Map<String, List<model.Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);
//		final Map<String, List<model.Event>> fem = FileEventMap.buildFileEventMap(log);
		
		System.out.println("Loaded "+fem.size()+" objects (files).");
		System.out.println("Extracted "+log.getAllChanges().size()+ " total changes.");
		System.out.println("Commits = "+log.getAllEntries().size());
		Object[] dates = (Object[]) log.getAllDates().toArray();
		Arrays.sort(dates,DateTimeComparator.getInstance());
		DateTime startD = (DateTime)dates[0];
		DateTime endD = (DateTime)dates[dates.length-1];
//		System.out.println(new Interval(startD.getMillis(), endD.getMillis()));

		model.tree.Tree t = new model.tree.Tree();
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
		
		root.setText(fileName.split("/")[1]);
		
		t.fillInGanttTree(root, chart, scopeEvent);
//		System.out.println(new DateTime(diff)-);
		//root node needs the scope event as data
		Map<String, Color> commitColorMap = model.tree.Tree.mapCommitToColor(FileEventMap.buildCommitFileMap(log));
		root.setData(new OurTreeData(rootGroup));
		root.setExpanded(true);
		chart.redrawGanttChart();
		t.colorEvents(root, commitColorMap);
		// sashform sizes
		sf.setWeights(new int[] { 30, 70 });
		

		// when the tree scrolls, we want to set the top visible item in the gantt chart to the top most item in the tree
		//		tree.getVerticalBar().addListener(SWT.Selection, new Listener() {
		//			public void handleEvent(Event event) {
		//				TreeItem ti = tree.getTopItem();
		//				// this will put the chart right where the event starts. There is also a method call setTopItem(GanttEvent, yOffset) where
		//				// you can fine-tune the scroll location if you need to.
		//				GanttGroup gg = ((OurTreeData)ti.getData()).getGanttGroup();
		//				if(gg.getEventMembers().size()==0)
		//					return;
		//				GanttEvent ge = (GanttEvent) gg.getEventMembers().get(0);
		////				ganttComposite.setTopItem(ge, SWT.NONE);
		////				ganttComposite.setTopItem(ge, ti.getBounds().y, SWT.CENTER);
		////				ganttComposite.setDate(ge.getActualEndDate());
		////				ganttComposite.jumpToEvent(ge, true, SWT.CENTER);
		//			}
		//		});

		ganttComposite.getVerticalBar().addListener(SWT.SCROLL_LINE, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				TreeItem ti = tree.getTopItem();
				OurTreeData data = (OurTreeData) ti.getData();
				GanttEvent ge = (GanttEvent) data.getGanttGroup().getEventMembers().get(0);
				ganttComposite.setTopItem(ge, SWT.CENTER);
			}
		});

		// when an item is selected in the tree, we highlight it by setting it selected in the chart as well
		tree.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				//				System.out.println("default selected");
				GanttEvent ge = null;
				OurTreeData data = (OurTreeData) tree.getSelection()[0].getData();

				if(data.getGanttGroup().getEventMembers().size()==0)
					return;

				ge = (GanttEvent) data.getGanttGroup().getEventMembers().get(0);

				if(ge!=null){
					//					ganttComposite.setTopItem(ge, -100, SWT.CENTER);
					//					System.out.println(tree.getSelection()[0].getBounds());
					//					ganttComposite.showEvent(ge, SWT.CENTER);
					//					ganttComposite.setTopItem(ge, SWT.CENTER);
					ganttComposite.jumpToEvent(ge, true, SWT.CENTER);	
				}
			}

			public void widgetSelected(SelectionEvent e) {

				if (tree.getSelectionCount() == 0)
					return;

				// set the selection
				TreeItem sel = tree.getSelection()[0];
				OurTreeData data = (OurTreeData) sel.getData();

				if (data == null){
					return;
				}
				GanttGroup group = (GanttGroup) data.getGanttGroup();
				List<GanttEvent> events = group.getEventMembers();

				for (GanttEvent ganttEvent : events) {
					//					Color c = null;
					ganttEvent.setStatusColor(ColorCache.getWhite());
				}
				ganttComposite.refresh();
			}

		});

		TreeListener treeListener = new TreeListener() {

			@Override
			public void treeExpanded(TreeEvent arg0) {
				TreeItem ti =  (TreeItem) arg0.item;
				OurTreeData data = (OurTreeData) ti.getData();

				if (data != null){	

					//					List<GanttEvent> listGanttEvents = (ArrayList<GanttEvent>) data.getGanttGroup().getEventMembers();
					//					for (GanttEvent ge : listGanttEvents)
					//						ge.setHidden(true);

					for (GanttEvent e : data.getCollapsedEvents()){
						e.setHidden(false);
						e.setTextDisplayFormat("");
					}
					for (GanttEvent e : data.getSuperEvents()){
						e.setHidden(true);
						e.setTextDisplayFormat("");
					}
				}
				Object[][] d = new Object[fem.size()][4];
				printNumberOfIdleTimes(ti, d);
				ganttComposite.heavyRedraw();
			}

			@Override
			public void treeCollapsed(TreeEvent arg0) {
				TreeItem ti = (TreeItem) arg0.item;
				OurTreeData data = (OurTreeData) ti.getData();

				TreeItem[] items = ti.getItems();
				GanttGroup ganttGroup = (GanttGroup) data.getGanttGroup();
				GanttGroup newGroup = ganttGroup;// new GanttGroup(chart);
				List<GanttEvent> subEvents = new ArrayList<GanttEvent>();
				setSubEventsVisible(items, false, subEvents);
				data.setCollapsedEvents(subEvents);

				List<GanttEvent> superEvents = new LinkedList<GanttEvent>();
				String fatherName = (ganttGroup.getEventMembers().size()>0)? 
						((GanttEvent)ganttGroup.getEventMembers().get(0)).getName(): "Project";
				Collections.sort(subEvents, new GanttEventComparator());		
				
				DescriptiveStatistics commitDistances = new DescriptiveStatistics(); // stores busy commit distances in millis
				
				if (subEvents.size() > 0){
					Calendar currentStartDate = subEvents.get(0).getActualStartDate();
					Calendar currentEndDate = subEvents.get(0).getActualEndDate();

					for (GanttEvent ganttEvent : subEvents){
						Calendar date = (Calendar) currentEndDate.clone();
						date.add(Calendar.DATE, NUM_OF_DAYS_THRESHOLD);
						if (date.before(ganttEvent.getActualEndDate())){
							// start a new one
							superEvents.add(addNewGanttEvent(chart, newGroup, currentStartDate, currentEndDate, fatherName));
							currentStartDate = ganttEvent.getActualStartDate();
							currentEndDate = ganttEvent.getActualEndDate();
						} else {
							// extend thresholds
							//ganttGroup.addEvent(new GanttEvent(chart, "", currentStartDate, currentEndDate, 50));
							if (currentEndDate.compareTo(ganttEvent.getActualEndDate())!=0){
								commitDistances.addValue((double)(ganttEvent.getActualEndDate().getTimeInMillis() - currentEndDate.getTimeInMillis()));	
							}
							currentEndDate = (Calendar) ganttEvent.getActualEndDate().clone();
						}
					}
					superEvents.add(addNewGanttEvent(chart, newGroup, currentStartDate, currentEndDate, fatherName));
					
					// extend superEvents with an additional start time
					
					long sumActivityDuration = 0;
					Interval interval;
					if (commitDistances.getN() > 0){
						double mean = commitDistances.getMean();
						for (GanttEvent superE : superEvents){
							Calendar adjustedStartDate = (Calendar) superE.getActualStartDate().clone();
							adjustedStartDate.add(Calendar.MILLISECOND, -1*(int)mean);
							interval = new Interval(
									superE.getActualStartDate().getTimeInMillis()- (int)mean,
									superE.getActualStartDate().getTimeInMillis());
							
							superE.setStartDate(adjustedStartDate);
							
									
							sumActivityDuration += superE.getActualEndDate().getTimeInMillis() - superE.getActualStartDate().getTimeInMillis();
						}
					}
					
					long totalDuration = superEvents.get(superEvents.size()-1).getActualEndDate().getTimeInMillis() - 
							superEvents.get(0).getActualStartDate().getTimeInMillis();
					
					String ratioString = String.format("%,.0f%%", 100*sumActivityDuration / (double)totalDuration);
					
					//String idleTimeAverage = String.format("%,.0f%%", (totalDuration- sumActivityDuration) / (superEvents.size()-1));
					String idleTimeAverage = String.format("%,.1f%% days average idle time", ((totalDuration- sumActivityDuration) / (double)(superEvents.size()-1)) / (24*1000*3600) );
					System.out.println(idleTimeAverage);
					
//					scopeEvent.setName("coverage "+ratioString);
					GanttEvent ge = ((GanttEvent)ganttGroup.getEventMembers().get(ganttGroup.getEventMembers().size()-1));
					ge.setName("coverage "+ratioString);
					ge.setTextDisplayFormat("#name#");
//					scopeEvent.setTextDisplayFormat("#name#");
					System.out.println("coverage = "+ratioString);
					data.setSuperEvents(superEvents);

					//					assert ganttComposite.getGroups().indexOf(newGroup) == index;
					//
					//					try {
					//						Thread.sleep(100);
					//					} catch (InterruptedException e1) {
					//						// TODO Auto-generated catch block
					//						e1.printStackTrace();
					//					}
				}
				Object[][] d = new Object[fem.size()][4];
				printNumberOfIdleTimes(ti,d);
				ganttComposite.heavyRedraw();
			}

			/**
			 * @param items
			 * @param visible
			 * @param subEvents 
			 */
			private void setSubEventsVisible(TreeItem[] items,
					boolean visible, List<GanttEvent> subEvents) {
				//	         
				for (int i = 0; i < items.length; i++) {
					OurTreeData data = (OurTreeData) items[i].getData();
					//	         	System.out.println(" data "+data);
					GanttGroup gg = data.getGanttGroup();

					List<GanttEvent> eList = gg.getEventMembers();
					eList.addAll(data.getSuperEvents());

					Collections.sort(eList, new GanttEventComparator());
					Iterator<GanttEvent> iter = eList.iterator();
					while(iter.hasNext()){
						GanttEvent ganttEvent = iter.next();
						subEvents.add(ganttEvent);
						ganttEvent.setHidden(!visible);
					}
					setSubEventsVisible(items[i].getItems(), visible, subEvents);
				}
			}
			private GanttEvent addNewGanttEvent(GanttChart parent, GanttGroup gg,
					Calendar currentStartDate, Calendar currentEndDate, String name) {
				//				currentEndDate.add(Calendar.DATE, 0);
				GanttEvent ganttEvent = new GanttEvent(parent, name, (Calendar)currentStartDate.clone(), (Calendar)currentEndDate.clone(), 100);
				//				ganttEvent.hideAllChildren();

				ganttEvent.setName(name);
				ganttEvent.setGradientStatusColor(ColorCache.getColor(240, 120, 50));
				//				ganttEvent.setStatusColor(ColorCache.getColor(240, 120, 50));
				ganttEvent.setTextDisplayFormat("");
				gg.addEvent(ganttEvent);
				ganttEvent.setVerticalEventAlignment(SWT.CENTER);
				scopeEvent.addScopeEvent(ganttEvent);
				return ganttEvent;
			}
		};

		tree.addTreeListener(treeListener);
		//
		//			@Override
		//			public void handleEvent(Event arg0) {
		//				// TODO Auto-generated method stub
		//				if(arg0.button==3){//right click
		//
		//					//	c1 = new 
		//					//c1.setLayoutData(new GridData(300, 300));
		//					final Text text = new Text(tree, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		//					text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		//					text.setText("");
		//					Composite c1 = new Composite(tree, SWT.NONE);
		//					Decorations d = new Decorations(c1, SWT.NONE);
		//					Menu menu = new Menu (d, SWT.POP_UP);
		//					MenuItem item = new MenuItem (menu, SWT.PUSH);
		//					item.setText ("Popup");
		//				}
		//			}
		//		});

		chart.getGanttComposite().jumpToEarliestEvent();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}


	private static void printStructure(TreeItem root) {
		System.out.println("Data="+root.getData()+" Text="+root.getText());
		TreeItem[] tis = root.getItems();
		for (int i = 0; i < tis.length; i++) {
			printStructure(tis[i]);
		}
	}

	/*
	 * number of identified idle times
	 */
	public static void printNumberOfIdleTimes(TreeItem root, Object[][] data){
		//		System.out.println("============================================================================");
		//		System.out.println("Level \t\t File \t\t Idle periods \t\t Through periods");
		System.out.println();
		final Object[] columnNames = { "SubLvl", "Time-disjoint events in file", "Idle periods", "Through periods"};
		System.out.format("%-5s %50s %15s %15s\n", columnNames);
		System.out.println("==============================================================================================");
		
		numberOfIdleTimesToTabularForm(root, 0, data, 0);
		for (final Object[] row : data) {
			if(row[0]==null)
				break;
			System.out.format("%-5s %50s %10s %15s\n", row);
		}
		//		TextTable tt = new TextTable(columnNames, data);
		//		tt.printTable();
//		System.out.println("==========================================================================");
		System.out.println();
	}

	public static void numberOfIdleTimesToTabularForm(TreeItem root, int level, Object[][] data2, int row){
		
//		if(row >= data2.length || data2[row] == null || data2[row].length!=4)
//			return;

		OurTreeData data = (OurTreeData) root.getData();
		List<GanttEvent> events = (List<GanttEvent>) data.getGanttGroup().getEventMembers();

		ArrayList<GanttEvent> distinctEvents = new ArrayList<GanttEvent>();
		for (GanttEvent ganttEvent : events) {
			if(!containsEvent(distinctEvents,ganttEvent) && !ganttEvent.isHidden()){
				distinctEvents.add(ganttEvent);
			}
		}
		
		if(distinctEvents.size()==0)
			return;

		String s = "";
		
		int c = countIdleTimes(distinctEvents);

		//		for(int j=level; j>0; j--)
		//			System.out.print(" ");

		//		s+="\t\t "+c;
		//		s+="\t\t "+(c+1);
//		System.out.println("+-["+level+"] \t"+root.getText()+" "+s);
		
		while(data2[row][0]!=null) //getting to the next free row
			row++;
		
		data2[row][0] = level;
		data2[row][1] = root.getText();
		data2[row][2] = c;
		data2[row][3] = c+1;

		TreeItem[] tis = root.getItems();
		
		for (int i = 0; i < tis.length; i++) {
			numberOfIdleTimesToTabularForm(tis[i], level+1, data2, row+1);
		}
	}

	/**
	 * @param distinctEvents
	 * @return number of idle times
	 */
	private static int countIdleTimes(ArrayList<GanttEvent> distinctEvents) {
		Collections.sort(distinctEvents, new GanttEventComparator());
		int res = 0;
		for (int i=0; i<distinctEvents.size()-1; i++) {
			Calendar end = (Calendar) distinctEvents.get(i).getActualEndDate().clone();
			Calendar start = (Calendar) distinctEvents.get(i+1).getActualStartDate().clone();
			end.add(Calendar.DATE, NUM_OF_DAYS_THRESHOLD);
			if(end.before(start)){
				//distinct groups
				res++;
			}
		}
		return res;
	}


	/**
	 * @param distinctEvents
	 * @param ganttEvent
	 * @return
	 */
	private static boolean containsEvent(ArrayList<GanttEvent> distinctEvents,
			GanttEvent ganttEvent) {
		for (GanttEvent ge : distinctEvents) {
			if(sameStartAndEnd(ge, ganttEvent))
				return true;
		}
		return false;
	}


	/*
	 * number of through-going work periods
	 */
	public static int numberOfThroughGoingPeriods() {
		int res=0;
		return res;
	}

	/*
	 * Simplification: if they have same start and end dates, then they are the same GanttEvent
	 */
	public static boolean sameStartAndEnd(GanttEvent e1, GanttEvent e2){
		return e1.getActualStartDate().compareTo(e2.getActualStartDate()) == 0 
				&& e1.getActualEndDate().compareTo(e2.getActualEndDate()) == 0;
	}

}

