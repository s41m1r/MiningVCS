package at.ac.wu.infobiz.projectmining.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class DBUtil {

	public DBUtil() {
	}
	
	public static Session connect(){
		return DatabaseConnector.getSessionFactory().openSession();
	}
	
	public static void disconnect(){
		DatabaseConnector.shutdown();
	}
	
	public static void exportQueryResults(Session session, String queryString, String filename){
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
