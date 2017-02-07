package at.ac.wu.infobiz.projectmining.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

public class DatabaseConnector {
	private static Configuration cfg = new Configuration().configure();
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	
	private static SessionFactory buildSessionFactory() {
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		return cfg.buildSessionFactory(serviceRegistry);
	}

	public static SessionFactory getSessionFactory() {
		return buildSessionFactory();
	}
	
	public static SessionFactory getSessionFactory(String forDatabase){
		  cfg = new Configuration();
	      cfg.setProperty("hibernate.default_schema", forDatabase);
//	      System.out.println("setting up: "+"jdbc:mysql://localhost:3306/"+forDatabase);
	      cfg.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/"+forDatabase+"?createDatabaseIfNotExist=true");
	      cfg.configure();
	      serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
	      sessionFactory = cfg.buildSessionFactory(serviceRegistry);
	      return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection poolsd
		sessionFactory.close();
		StandardServiceRegistryBuilder.destroy(serviceRegistry);
	}

	public static void synch(boolean sideEffectOnDatabase) {
		new SchemaUpdate(cfg).execute(true, sideEffectOnDatabase);
	}
}
