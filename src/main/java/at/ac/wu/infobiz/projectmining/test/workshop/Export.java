/**
 * 
 */
package at.ac.wu.infobiz.projectmining.test.workshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import at.ac.wu.infobiz.projectmining.util.DatabaseConnector;

/**
 * @author saimir
 *
 */
public class Export {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String queryString = "select path from File";
		long start = System.currentTimeMillis();
		exportQueryResults(queryString, "filenames.txt");
		System.out.println("Done. Time "+(System.currentTimeMillis()-start));
	}
	
	public static void exportQueryResults(String queryString, String filename){
		
		SessionFactory connector = DatabaseConnector.getSessionFactory();
		Session session = connector.openSession();
		
		Query q = session.createQuery(queryString);
		
		@SuppressWarnings("rawtypes")
		List list = q.list();
		int tick = list.size()/10;
		int i = 0;
		try {
			System.out.println("Going to write "+list.size()+" elements");
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(filename)));
			for (Object object : list) {
				i++;
				if(i%tick==0)
					System.out.print(".");
				pw.println(object.toString());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		DatabaseConnector.shutdown();
	}
	
}
