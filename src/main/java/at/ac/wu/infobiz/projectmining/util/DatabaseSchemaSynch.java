package at.ac.wu.infobiz.projectmining.util;

/*
 * Updates the Schema definition on the Relational Database to keep it in-synch with the persistent classes definition.
 */
public class DatabaseSchemaSynch {
	public static void main(String[] args) {
		boolean sideEffectOnDatabase = true;
		if (args.length > 0)
			sideEffectOnDatabase = Boolean.valueOf(args[0]);
		
		DatabaseConnector.synch(sideEffectOnDatabase);
		DatabaseConnector.shutdown();
		
		System.exit(0);
	}
}