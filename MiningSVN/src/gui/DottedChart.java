package gui;

/**
 * @author Saimir Bala
 */

import java.io.IOException;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import reader.GITLogReader;
import reader.LogReader;
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

	public static void main(String[] args) {
		// standard display and shell etc
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Gantt Chart - Tree Connector Example");
		shell.setSize(900, 500);
		shell.setLayout(new FillLayout());

		// split the view horizontally (which makes the splitter vertical)
		SashForm sf = new SashForm(shell, SWT.HORIZONTAL);

		// create the control parent. This composite expects either a Tree or a Table, anything else or a multiple
		// widgets and you will have to create your own class to control the layout.
		// see the source of the GanttControlParent for an idea, it's not overly difficult and could possibly be done using a normal
		// layout manager where you set the top margin to the height of the
		// calculated GANTT header
		GanttControlParent left = new GanttControlParent(sf, SWT.NONE);

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
		TreeColumn tc1 = new TreeColumn(tree, SWT.BORDER);
		tc1.setText("Directory");
		tc1.setWidth(300);

		//		TreeColumn tc2 = new TreeColumn(tree, SWT.BORDER);
		//		tc2.setText("File");
		//		tc2.setWidth(300);

		// this matches the "root" item
		GanttEvent scopeEvent = new GanttEvent(chart, "Scope");
		scopeEvent.setVerticalEventAlignment(SWT.CENTER);

		// our root node that matches our scope
		final TreeItem root = new TreeItem(tree, SWT.BORDER);
//		root.setText(new String[] { "Dir", "Events" });
//		root.setExpanded(true);
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
//			LogReader<LogEntry> lr = new SVNLogReader("resources/20150129_SNV_LOG_FROM_SHAPE_PROPOSAL_new.log");
			LogReader<LogEntry> lr = new GITLogReader("resources/MiningSvn.log");
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
		
		t.fillInGanttTree(tree, chart, scopeEvent);
		//root node needs the scope event as data
		root.setData(scopeEvent);
		root.setExpanded(true);
		
		// sashform sizes
		sf.setWeights(new int[] { 30, 70 });

		// when the tree scrolls, we want to set the top visible item in the gantt chart to the top most item in the tree
		tree.getVerticalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TreeItem ti = tree.getTopItem();
				// this will put the chart right where the event starts. There is also a method call setTopItem(GanttEvent, yOffset) where
				// you can fine-tune the scroll location if you need to.
				if(ti!=null && ti.getData()!=null){
					if(ti.getData() instanceof GanttEvent)
						ganttComposite.setTopItem((GanttEvent) ti.getData(), SWT.LEFT);
					}
			}
		});

		// when an item is selected in the tree, we highlight it by setting it selected in the chart as well
		tree.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				if (tree.getSelectionCount() == 0)
					return;

				// set the selection
				TreeItem sel = tree.getSelection()[0];
				Object data = sel.getData();
				if(data instanceof GanttEvent){
				GanttEvent ge = (GanttEvent) data;
				if(ge!=null)
					ge.setStatusColor(ColorCache.getRandomColor());
				ganttComposite.setSelection(ge);
				}
				else{
					ganttComposite.setSelection((GanttEvent) ((GanttGroup)data).getEventMembers().get(0));
				}
			}

		});

		// when a root node is collapsed/expanded, we collapse the entire scope in a similar fashion
		Listener expandCollapseListener = new Listener() {
			public void handleEvent(Event event) {
				GanttEvent ge = (GanttEvent) root.getData();

				if (event.type == SWT.Collapse) {
//					ge.hideAllChildren();
					chart.redrawGanttChart();
				} else {
					ge.showAllChildren();
					chart.redrawGanttChart();
				}
			}
		};
		
		tree.addListener(SWT.Collapse, expandCollapseListener);
		tree.addListener(SWT.Expand, expandCollapseListener);
		
		chart.getGanttComposite().jumpToEarliestEvent();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}

