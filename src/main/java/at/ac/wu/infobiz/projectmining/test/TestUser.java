package at.ac.wu.infobiz.projectmining.test;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import at.ac.wu.infobiz.projectmining.model.Commit;
import at.ac.wu.infobiz.projectmining.model.Edit;
import at.ac.wu.infobiz.projectmining.model.File;
import at.ac.wu.infobiz.projectmining.model.Position;
import at.ac.wu.infobiz.projectmining.model.User;
import at.ac.wu.infobiz.projectmining.util.DatabaseConnector;


/**
 * @author Saimir Bala
 *
 */

public class TestUser {

	public static void main(String[] args) {
		
		SessionFactory sf = DatabaseConnector.getSessionFactory();		
		Session session = sf.openSession();
		
//		insertUser(session, "Jim", "Dough");
		Commit c = new Commit();
		c.setComment("bla bla from John");
		c.setRevisionId("213dsoersap00w021039021389eßwq");
		c.setTimeStamp(new Date());
		
		Edit edit = new Edit();
		edit.setCommit(c);
		edit.setLinesAdded(121);
		edit.setLinesRemoved(12);
		edit.setFromPos(new Position(0, 1));
		edit.setToPos(new Position(0,1));
		File file = new File();
		file.addEdit(edit);
		file.setPath("path1");
		User u1 = new User();
		u1.setEmail("john@wu.at");
		u1.setName("John");
		insertUser(session, u1);
		c.setUser(u1);
		c.addEdit(edit);
		
		edit.setFile(file);
		insertEdit(session, edit);
		
		edit = new Edit();
		edit.setCommit(c);
		edit.setLinesAdded(22);
		edit.setLinesRemoved(21);
		edit.setFromPos(new Position(10, 20));
		edit.setToPos(new Position(11,23));
		
		edit.setFile(file);
		insertEdit(session, edit);

		session.close();
		DatabaseConnector.shutdown();
	}

	private static void insertEdit(Session session, Edit edit) {
		Transaction tx = session.getTransaction();
		tx.begin();
		session.saveOrUpdate(edit);
		session.flush();
		tx.commit();
	}

	private static void insertUser(Session session, User u) {				
		Transaction tx = session.getTransaction();
		tx.begin();
		session.save(u);
		session.flush();
		tx.commit();
	}

}
