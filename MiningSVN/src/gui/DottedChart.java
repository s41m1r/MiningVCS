package gui;

/**
 * @author Saimir Bala
 */

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Log;
import model.LogEntry;
import model.svn.SVNLog;

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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.joda.time.DateTimeComparator;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import reader.GITLogReader;
import reader.LogReader;
import reader.SVNLogReader;
import util.FileEventMap;
import weka.classifiers.bayes.net.search.fixed.FromFile;

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

	public static void main(String[] args) {
		// standard display and shell etc
		Display display = new Display();
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
		final GanttChart chart = new GanttChart(sf, SWT.NONE);

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
				event.height = oneRowHeight;
			}
		});

		// a few columns
		//		TreeColumn tc1 = new TreeColumn(tree, SWT.BORDER);
		//		tc1.setText("Directory");
		//		tc1.setWidth(300);

		//		TreeColumn tc2 = new TreeColumn(tree, SWT.BORDER);
		//		tc2.setText("File");
		//		tc2.setWidth(300);

		// this matches the "root" item
		GanttGroup rootGroup = new GanttGroup(chart);
		final GanttEvent scopeEvent = new GanttEvent(chart, "Scope");
		scopeEvent.setVerticalEventAlignment(SWT.CENTER);
		rootGroup.addEvent(scopeEvent);

		// our root node that matches our scope
		final TreeItem root = new TreeItem(tree, SWT.BORDER);
		//        root.setText("Project structure");
		//		root.setExpanded(false);
		//
		//		// create 20 events, and 20 tree items that go under "root", dates don't really matter as we're an example
		//		for (int i = 1; i < 21; i++) {
		//			Calendar start = Calendar.getInstance();
		//			Calendar end = Calendar.getInstance();
		//			start.add(Calendar.DATE, 0);
		//			end.add(Calendar.DATE, i + 5);
		//			GanttEvent ge = new GanttEvent(chart, "Event " + i, start, end, i * 5);
		//			ge.setVerticalEventAlignment(SWT.CENTER);
		//			TreeItem ti = new TreeItem(root, SWT.NONE);
		//			ti.setExpanded(true);
		//			ti.setText(new String[] { "Event " + i, "" + start.getTime() + " - " + end.getTime() });
		//
		//			// note how we set the data to be the event for easy access in the tree listeners later on
		//			ti.setData(ge);
		//
		//			// add the event to the scope
		//			scopeEvent.addScopeEvent(ge);
		//		}
		//
		//		// root node needs the scope event as data
		//		root.setData(scopeEvent);
		//		root.setExpanded(true);

		Log log = null; 
		try {
			LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
//			LogReader<LogEntry> lr = new GITLogReader("resources/MiningSvn.log");
//			LogReader<LogEntry> lr = new GITLogReader("resources/abc.log");
			log = new SVNLog(lr.readAll());
			lr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, List<model.Event>> fem = FileEventMap.buildHistoricalFileEventMap(log);

		model.tree.Tree t = new model.tree.Tree();
		Set<String> files = fem.keySet();
		for (String string : files) {
			t.add(string, fem.get(string));
		}
		
		

		t.fillInGanttTree(root, chart, scopeEvent, log);
		//root node needs the scope event as data
		root.setData(new OurTreeData(rootGroup));
		root.setExpanded(true);
		chart.redrawGanttChart();

		// sashform sizes
		sf.setWeights(new int[] { 30, 70 });

		// when the tree scrolls, we want to set the top visible item in the gantt chart to the top most item in the tree
		tree.getVerticalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TreeItem ti = tree.getTopItem();
				// this will put the chart right where the event starts. There is also a method call setTopItem(GanttEvent, yOffset) where
				// you can fine-tune the scroll location if you need to.
				if(!(ti.getData()==null || ((OurTreeData)ti.getData()).getGanttGroup() instanceof GanttGroup)){
					GanttGroup gg = ((OurTreeData)ti.getData()).getGanttGroup();
					GanttEvent ge = (GanttEvent) gg.getEventMembers().get(0);
					ganttComposite.setTopItem(ge, ge.getY(), SWT.LEFT);
					ganttComposite.setDate(ge.getActualEndDate());
				}
			}
		});

		// when an item is selected in the tree, we highlight it by setting it selected in the chart as well
		tree.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("default selected");
				GanttEvent ge = null;
				OurTreeData data = (OurTreeData) tree.getSelection()[0].getData();
				
				ge = (GanttEvent) ((GanttGroup) data.getGanttGroup()).getEventMembers().get(0);
				
				if(ge!=null)
//					ganttComposite.setTopItem(ge, -100, SWT.CENTER);
					ganttComposite.showEvent(ge, SWT.CENTER);
			}

			public void widgetSelected(SelectionEvent e) {
				System.out.println("specific selected");
				if (tree.getSelectionCount() == 0)
					return;

				// set the selection
				TreeItem sel = tree.getSelection()[0];
				OurTreeData data = (OurTreeData) sel.getData();
//				System.out.println(e);
				if (data == null){
					return;
				}
				GanttGroup group = (GanttGroup)data.getGanttGroup();
				List<GanttEvent> events = group.getEventMembers();
				for (GanttEvent ganttEvent : events) {
					Color c = null;
//						if(ganttEvent.getStatusColor().equals(ColorCache.getBlack()))
//							c = ganttComposite.get
					ganttEvent.setStatusColor(ColorCache.getWhite());
					//	               chart	              
				}
//					ganttComposite.setSelection(events);
				ganttComposite.refresh();
				//					ganttComposite.setSelection((GanttEvent) ((GanttGroup)data).getEventMembers().get(0));
			}

		});

		//		// when a root node is collapsed/expanded, we collapse the entire scope in a similar fashion
		//		Listener expandCollapseListener = new Listener() {
		//			public void handleEvent(Event event) {
		//				GanttEvent ge = (GanttEvent) root.getData();
		//
		//				if (event.type == SWT.Collapse) {
		//					//					ge.hideAllChildren();
		//					chart.redrawGanttChart();
		//					//					GanttGroup group = new GanttGroup(root.getData(key));
		//				} else {
		//					ge.showAllChildren();
		//					chart.redrawGanttChart();
		//				}
		//			}
		//		};

		//		tree.addListener(SWT.Collapse, expandCollapseListener);
		//		tree.addListener(SWT.Expand, expandCollapseListener);

//		printStructure(root);

		TreeListener treeListener = new TreeListener() {

			@Override
			public void treeExpanded(TreeEvent arg0) {
				TreeItem ti =  (TreeItem) arg0.item;
				if (ti.getData() != null){
					for (GanttEvent e : ((OurTreeData)ti.getData()).getCollapsedEvents()){
						e.setHidden(false);
					}
					for (GanttEvent e : ((OurTreeData)ti.getData()).getSuperEvents()){
						e.setHidden(true);
					}
				}
//				TreeItem[] items = ti.getItems();
//				GanttGroup data = (GanttGroup) ti.getData();
//				for (int i = 0; i < items.length; i++) {
//					System.out.println(" data "+items[i].getData());
//					if(items[i].getData() instanceof GanttGroup){
//						GanttGroup gg = (GanttGroup) items[i].getData();
////						GanttGroup gg =  new GanttGroup(chart);
//						List<GanttEvent> eList = gg.getEventMembers();
//						eList.sort(new GanttEventComparator());
//						Iterator<GanttEvent> iter = eList.iterator();
//						while(iter.hasNext()){
//							GanttEvent ganttEvent = iter.next();
////							iter.remove();
////							gg.removeEvent(ganttEvent);
////							ganttComposite.removeEvent(ganttEvent);
////							scopeEvent.removeScopeEvent(ganttEvent);
//							ganttEvent.setHidden(false);
//						}
////						ganttComposite.removeGroup(gg);
//					}
//				}
				ganttComposite.refresh();
				ganttComposite.redraw();
				ganttComposite.update();
				
//				System.out.println("expanded "+arg0);
//				TreeItem ti = (TreeItem) arg0.item;
//				System.out.println("childs="+ti.getItemCount());
//				System.out.println(ti.getData());
//				Object data = ti.getData();
//				if(data instanceof GanttGroup){
//					System.out.println("data instanceof GanttGroup!!!");
//					GanttGroup gg = (GanttGroup) data;
//					List<GanttEvent> list = (List<GanttEvent>) gg.getEventMembers();
//					System.out.println("list="+list);
//					if (list==null || list.isEmpty())
//						return;
//					//					System.out.println("events "+list.get(0));
//					TreeItem[] tis = ti.getItems();
//					if(tis==null)
//						return;
//					for (int i = 0; i < ti.getItemCount(); i++) {
//						if(i<list.size())
//							tis[i].setData(list.get(i));
//						//list.get(i).hideAllChildren();
//					}
//					gg.dispose();
//					ganttComposite.redraw();
//				}
//				else { 
//					if(data instanceof GanttEvent){
//						GanttEvent ge = (GanttEvent) data;
//						System.out.println("expanded GanttEvent:"+ge);
//					}
//				}
			}
			
//			@Override
//			public void treeCollapsed(TreeEvent arg0) {
//				TreeItem ti = (TreeItem) arg0.item;
//				TreeItem[] items = ti.getItems();
//				
//				GanttGroup ganttGroup = (GanttGroup) ti.getData();
////				List<GanttEvent> oldEvents = new ArrayList<GanttEvent>(1000);
////				for (Object e : ganttGroup.getEventMembers()){
////					oldEvents.add((GanttEvent) e);
////				}
//				
//				List<GanttEvent> subEvents = new LinkedList<GanttEvent>();
//				for (int i = 0; i < items.length; i++) {
//					System.out.println(" data "+items[i].getData());
//					if(items[i].getData() instanceof GanttGroup){
//						GanttGroup gg = (GanttGroup) items[i].getData();
////						GanttGroup gg =  new GanttGroup(chart);
//						List<GanttEvent> eList = gg.getEventMembers();
//						eList.sort(new GanttEventComparator());
//						Iterator<GanttEvent> iter = eList.iterator();
//						while(iter.hasNext()){
//							GanttEvent ganttEvent = iter.next();
//							subEvents.add(ganttEvent);
////							iter.remove();
////							gg.removeEvent(ganttEvent);
////							ganttComposite.removeEvent(ganttEvent);
//						}
////						ganttComposite.removeGroup(gg);
//					}
//				}
//				
//				for (GanttEvent ganttEvent : subEvents){
//					ganttGroup.addEvent(ganttEvent);
////					Calendar date = (Calendar) currentEndDate.clone();
////					date.add(Calendar.DATE, NUM_OF_DAYS_THRESHOLD);
////					if (date.before(ganttEvent.getActualEndDate())){
////						// start a new one
////						addNewGanttEvent(chart, newGroup, currentStartDate, currentEndDate, "");
////						currentStartDate = ganttEvent.getActualStartDate();
////						currentEndDate = ganttEvent.getActualEndDate();
////					} else {
////						// extend thresholds
////						//ganttGroup.addEvent(new GanttEvent(chart, "", currentStartDate, currentEndDate, 50));
////						currentEndDate = (Calendar) ganttEvent.getActualEndDate().clone();
////					}
//				}
//				ganttComposite.redraw();
//				chart.update();
//			}
			
			
			@Override
			public void treeCollapsed(TreeEvent arg0) {
				TreeItem ti = (TreeItem) arg0.item;
				OurTreeData data = (OurTreeData) ti.getData();
				
				TreeItem[] items = ti.getItems();
				if (data.getCollapsedEvents().isEmpty()){ 
					GanttGroup ganttGroup = (GanttGroup) data.getGanttGroup();
					int index = ganttComposite.getGroups().indexOf(ganttGroup);
							
					GanttGroup newGroup = ganttGroup;// new GanttGroup(chart);
					 
					List<GanttEvent> oldEvents = new ArrayList<GanttEvent>(1000);
					for (Object e : ganttGroup.getEventMembers()){
						oldEvents.add((GanttEvent) e);
					}
					
					//ganttGroup.getEventMembers().remove(0);
	//				while(iter.hasNext()){
	//					GanttEvent event = iter.next();
	//					ganttGroup.removeEvent(event);
	//				}
					
	//				List<GanttEvent> events = ganttGroup.getEventMembers();
	//				
	//				java.util.Collections.sort(events, new GanttEventComparator());
					
					List<GanttEvent> subEvents = setSubEventsVisible(items, oldEvents, false);
					data.setCollapsedEvents(subEvents);
					
					List<GanttEvent> superEvents = new LinkedList<GanttEvent>();
					
					subEvents.sort(new GanttEventComparator());
					if (subEvents.size() > 0){
						Calendar currentStartDate = subEvents.get(0).getActualStartDate();
						Calendar currentEndDate = subEvents.get(0).getActualEndDate();
						
						for (GanttEvent ganttEvent : subEvents){
							Calendar date = (Calendar) currentEndDate.clone();
							date.add(Calendar.DATE, NUM_OF_DAYS_THRESHOLD);
							if (date.before(ganttEvent.getActualEndDate())){
								// start a new one
								superEvents.add(addNewGanttEvent(chart, newGroup, currentStartDate, currentEndDate, ""));
								currentStartDate = ganttEvent.getActualStartDate();
								currentEndDate = ganttEvent.getActualEndDate();
							} else {
								// extend thresholds
								//ganttGroup.addEvent(new GanttEvent(chart, "", currentStartDate, currentEndDate, 50));
								currentEndDate = (Calendar) ganttEvent.getActualEndDate().clone();
							}
						}
						superEvents.add(addNewGanttEvent(chart, newGroup, currentStartDate, currentEndDate, ""));
						
						data.setSuperEvents(superEvents);
						//ganttComposite.removeGroup(ganttGroup);
						
						//ganttComposite.getGroups().remove(newGroup);
						//ganttComposite.getGroups().add(index, newGroup);
						assert ganttComposite.getGroups().indexOf(newGroup) == index;
						
						ganttComposite.refresh();
						ganttComposite.redraw();
						chart.update();
						try {
		               Thread.sleep(100);
	               } catch (InterruptedException e1) {
		               // TODO Auto-generated catch block
		               e1.printStackTrace();
	               }
						
						chart.redrawGanttChart();
						
						
		//				ti.setData(ganttGroup);
						for (GanttEvent event: oldEvents){
	//						event.dispose();
	//						scopeEvent.removeScopeEvent(event);
	//						event.setHidden(true);
						}
	//					ganttGroup.removeEvent((GanttEvent) ganttGroup.getEventMembers().get(0));
						
		//				ganttComposite.heavyRedraw();
		//				ganttComposite.refresh();
		//				ganttComposite.redraw();
		//				ganttComposite.update();
		//				
		////				ganttComposite.heavyRedraw();
						ganttComposite.refresh();
						ganttComposite.redraw();
						ganttComposite.update();

		//				ganttComposite.redraw();
						
		//				System.out.println("collapsed");
		//				TreeItem ti = (TreeItem) arg0.item;
		//				if(ti.getData() instanceof GanttEvent){
		//					GanttEvent g = (GanttEvent)ti.getData();
		//					g.dispose();
		//					TreeItem[] tis = ti.getItems();
		//					GanttEvent[] events = new GanttEvent[tis.length];
		//					GanttGroup ganttGroup = new GanttGroup(chart);
		//					for (int i = 0; i < tis.length; i++) {
		//						if(tis[i].getData() instanceof GanttGroup){
		//							System.out.println(tis[i].getData() + "is GanttGroup");
		//							continue;
		//						}
		//						events[i] = (GanttEvent) tis[i].getData();
		//						ganttGroup.addEvent(events[i]);
		//					}
		//					ganttComposite.redraw();
		//					ti.setData(ganttGroup);
		//				}
		//				else{
		//					GanttGroup gr = (GanttGroup) ti.getData();
		//					if(gr!=null)
		//						gr.dispose();
		//				}
		//				//				System.out.println("events="+events.length);
					}
				} else {
					for (GanttEvent e : data.getCollapsedEvents()){
						e.setHidden(true);
					}
					for (GanttEvent e : data.getSuperEvents()){
						e.setHidden(false);
					}
					setSubEventsVisible(ti.getItems(), new LinkedList<GanttEvent>(),false);
					ganttComposite.refresh();
					ganttComposite.redraw();
					ganttComposite.update();
				}
			}

			/**
			 * @param items
			 * @param oldEvents
			 * @return
			 */
         private List<GanttEvent> setSubEventsVisible(TreeItem[] items,
               List<GanttEvent> oldEvents, boolean visible) {
	         List<GanttEvent> subEvents = new LinkedList<GanttEvent>(oldEvents);
	         for (int i = 0; i < items.length; i++) {
	         	System.out.println(" data "+items[i].getData());
	         	Object object = ((OurTreeData)items[i].getData()).getGanttGroup();
	         	if(object instanceof GanttGroup){
	         		GanttGroup gg = (GanttGroup) object;
//						GanttGroup gg =  new GanttGroup(chart);
	         		List<GanttEvent> eList = gg.getEventMembers();
	         		eList.sort(new GanttEventComparator());
	         		Iterator<GanttEvent> iter = eList.iterator();
	         		while(iter.hasNext()){
	         			GanttEvent ganttEvent = iter.next();
	         			subEvents.add(ganttEvent);
//							iter.remove();
//							gg.removeEvent(ganttEvent);
//							ganttComposite.removeEvent(ganttEvent);
//							scopeEvent.removeScopeEvent(ganttEvent);
	         			ganttEvent.setHidden(!visible);
	         		}
//						ganttComposite.removeGroup(gg);
	         	}
	         }
	         return subEvents;
         }
			private GanttEvent addNewGanttEvent(GanttChart parent, GanttGroup gg,
            Calendar currentStartDate, Calendar currentEndDate, String name) {
				currentEndDate.add(Calendar.DATE, 0);
				GanttEvent ganttEvent = new GanttEvent(parent, name, (Calendar)currentStartDate.clone(), (Calendar)currentEndDate.clone(), 100);
//				ganttEvent.hideAllChildren();
//				ganttEvent.setName(name);
				ganttEvent.setGradientStatusColor(ColorCache.getColor(240, 120, 50));
				gg.addEvent(ganttEvent);
				ganttEvent.setVerticalEventAlignment(SWT.CENTER);
				scopeEvent.addScopeEvent(ganttEvent);
				return ganttEvent;
	         
         }
		};

		tree.addTreeListener(treeListener);

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
}

