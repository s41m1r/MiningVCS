/**
 * 
 */
package at.ac.wu.infobiz.projectmining.workshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import at.ac.wu.infobiz.projectmining.model.Commit;
import at.ac.wu.infobiz.projectmining.util.DatabaseConnector;

/**
 * Pick random commits and exports them in a .csv file
 * 
 * @author saimir
 *
 */
public class PickRandomCommit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int pickSize = 200;
		long start = System.currentTimeMillis();
		Set<Commit> commits = getSample(pickSize);
		String fileName = "random-pick.csv";
		toCSV(fileName,commits);
		System.out.println("Done. Time "+(System.currentTimeMillis()-start));
		
	}

	private static void toCSV(String fileName, Set<Commit> commits) {
		String sep = "\t";
		String header = "id" + sep + "comment"+ sep + "timestamp";
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(fileName)));
			pw.println(header);
			for (Commit c : commits) {
				String record = c.getId() + sep + 
						"\""+ c.getComment() +"\"" + sep + 
						c.getTimeStamp();
				pw.println(record);
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Set<Commit> getSample(int pickSize) {
		SessionFactory sf = DatabaseConnector.getSessionFactory();
		Session se = sf.openSession();
		
		Query q  = se.createQuery("from Commit");
		
		@SuppressWarnings("unchecked")
		List<Commit> commitList = q.list();
		

		Map<Integer,Commit> pool = new HashMap<Integer,Commit>();
		
		for (Commit commit : commitList) {
			pool.put(commit.getId(), commit);
		}
		
		int poolSize = commitList.size();
		
		Set<Commit> sample = new HashSet<Commit>(pickSize);
		
		//start random picking
		Random random = new Random();
		while(sample.size()<pickSize){
			int key = random.nextInt(poolSize);
			Commit c = pool.remove(key);
			if(c!=null)
				sample.add(c);
		}
		
		DatabaseConnector.shutdown();
		
		return sample;
	}

}
