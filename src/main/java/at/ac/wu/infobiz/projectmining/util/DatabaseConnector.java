package at.ac.wu.infobiz.projectmining.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

public class DatabaseConnector {
	private static Configuration cfg = new Configuration().configure();
	private static SessionFactory sessionFactory = buildSessionFactory();
	private static ServiceRegistry serviceRegistry;
	
	private static SessionFactory buildSessionFactory() {
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		return cfg.buildSessionFactory(serviceRegistry);
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static SessionFactory getSessionFactory(String forDatabase){
		  cfg = new Configuration().configure();
	      cfg.setProperty("hibernate.default_schema", forDatabase);
	      cfg.configure();
	      sessionFactory = buildSessionFactory();
	      return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

	public static void synch(boolean sideEffectOnDatabase) {
		new SchemaUpdate(cfg).execute(true, sideEffectOnDatabase);
	}
}
