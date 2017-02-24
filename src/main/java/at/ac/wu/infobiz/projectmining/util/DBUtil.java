package at.ac.wu.infobiz.projectmining.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.opencsv.CSVWriter;

import at.ac.wu.infobiz.projectmining.io.OutputRedirect;

public abstract class DBUtil {

	public static Session session = null; 

	public DBUtil() {
	}

	public static Session connectTo(String dbname){
		session = DatabaseConnector.getSessionFactory(dbname).openSession();
		return session;
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

	public static void toCSV(String dirname, String theFile, String[] header, List<Object[]> results, String sep) {

		if(results==null || results.size()==0 || results.get(0).length==0)
			return; //avoid writing empty stories 

		String dName = dirname+"-stories";
		File dir = new File(dName);
		dir.mkdir();
		String filename = dName+"/"+theFile.replaceAll("/", "§")+".csv";	
		String head = "";

		for(int i=0;i<header.length-1;i++){
			head+=header[i] + sep;
		}
		head+=header[header.length-1];

		try {
			OutputRedirect.toFile(filename);
			System.out.println(head);
			for (Object[] row : results) {
				for (Object cell : row) {
					System.out.print(cell+sep);
				}
				System.out.println();
			}
			OutputRedirect.toConsole();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//		System.out.println("Ouput written into directory "+dirname);
	}

	public static void toCSVLongFileNames(String filename, String[] header, List<Object[]> results, char sep){

		if(results==null || results.size()==0 || results.get(0).length==0)
			return; //avoid writing empty stories
		
		File file = new File(filename);
		File absolute = file.getAbsoluteFile();
		absolute.getParentFile().mkdirs();
		
		try {
			FileWriter writer = new FileWriter(file);
			CSVWriter csvWriter = new CSVWriter(writer, sep);
			csvWriter.writeNext(header);
			List<String[]> out = new ArrayList<String[]>();
			for (Object[] objects : results) {
				String[] row = new String[objects.length];
				for (int i = 0; i < row.length; i++) {
					row[i] = objects[i].toString();
				}
				out.add(row);
			}
			csvWriter.writeAll(out);
			csvWriter.flush();
			csvWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void disconnect(Session session) {
		session.flush();
		session.close();
		DatabaseConnector.shutdown();
	}

	public static void toCSV2(String filename, String[] header, List<String[]> results,
			char sep) {
		if(results==null || results.size()==0 || results.get(0).length==0)
			return; //avoid writing empty stories
		
		File file = new File(filename);
		File absolute = file.getAbsoluteFile();
		absolute.getParentFile().mkdirs();
		
		try {
			FileWriter writer = new FileWriter(file);
			CSVWriter csvWriter = new CSVWriter(writer, sep);
			csvWriter.writeNext(header);
			csvWriter.writeAll(results);
			csvWriter.flush();
			csvWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
