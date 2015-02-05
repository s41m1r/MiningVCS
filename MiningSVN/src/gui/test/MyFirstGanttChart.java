/**
 * 
 */
package gui.test;

/**
 * @author Saimir Bala
 *
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;


public class MyFirstGanttChart extends Application {
 
  @Override
  public void start(Stage stage) throws Exception {
 
    // <- Our Gantt chart
    GanttChart<?> gantt = new GanttChart<>();
     
    Scene scene = new Scene(gantt);
    
    Flight f = new Flight();
    Layer l = new Layer("ciccioooo");
    
    gantt.getLayers().add(l);
    Row<?, ?, Flight> r = new Row<Row<?,?,?>, Row<?,?,?>, Flight>() {
	};
	
	 r.addActivity(l, f);

	 stage.setScene(scene);
    stage.centerOnScreen();
    stage.sizeToScene();
    stage.show();   
  }
 
  public static void main(String[] args) {
    Application.launch(args);
  }
}