package at.ac.wu.infobiz.projectmining.parsing.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import at.ac.wu.infobiz.projectmining.model.ActionType;
import at.ac.wu.infobiz.projectmining.model.Commit;
import at.ac.wu.infobiz.projectmining.model.Edit;
import at.ac.wu.infobiz.projectmining.model.FileAction;
import at.ac.wu.infobiz.projectmining.model.Position;
import at.ac.wu.infobiz.projectmining.model.Project;
import at.ac.wu.infobiz.projectmining.model.Rename;
import at.ac.wu.infobiz.projectmining.model.User;
import at.ac.wu.infobiz.projectmining.parsing.DiffResults;
import at.ac.wu.infobiz.projectmining.parsing.FileChange;
import at.ac.wu.infobiz.projectmining.util.DatabaseConnector;
import at.ac.wu.infobiz.projectmining.util.ParsingUtils;

public class GitToDB {
	
	private String inputFile;
	private String startCommitDelimiter;
	private String endMessageDelimiter;
	
	static int BATCH_SIZE = 100000;
	
	public static Logger logger = Logger.getLogger(GitToDB.class.getName());
	
	/**
	 * Default delimiters:
	 * startCommitDelimiter = "§§--§§\n";
	 * endMessageDelimiter = "---§";
	 */
	public GitToDB() {
		startCommitDelimiter = "§§--§§\n";
		endMessageDelimiter = "---§";
	}
	
	public GitToDB(String inputFile, String startCommitDelimiter,
			String endMessageDelimiter) {
		this.inputFile = inputFile;
		this.startCommitDelimiter = startCommitDelimiter;
		this.endMessageDelimiter = endMessageDelimiter;
	}
	
	
	/**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the startCommitDelimiter
	 */
	public String getStartCommitDelimiter() {
		return startCommitDelimiter;
	}

	/**
	 * @param startCommitDelimiter the startCommitDelimiter to set
	 */
	public void setStartCommitDelimiter(String startCommitDelimiter) {
		this.startCommitDelimiter = startCommitDelimiter;
	}

	/**
	 * @return the endMessageDelimiter
	 */
	public String getEndMessageDelimiter() {
		return endMessageDelimiter;
	}

	/**
	 * @param endMessageDelimiter the endMessageDelimiter to set
	 */
	public void setEndMessageDelimiter(String endMessageDelimiter) {
		this.endMessageDelimiter = endMessageDelimiter;
	}

	public void toDB(File logFile) throws FileNotFoundException{
		
		logger.info("Parsing of "+logFile+" started");
		
		FileInputStream fis = new FileInputStream(logFile);
		Scanner scanner = new Scanner(fis);
		
		Project thisProject = new Project();
		thisProject.setName(inputFile);
		
		Map<String,Commit> commits = new HashMap<String,Commit>();
		Set<Edit> edits = new HashSet<Edit>();
		Map<String, at.ac.wu.infobiz.projectmining.model.File> files = new HashMap<String, at.ac.wu.infobiz.projectmining.model.File>();
		Set<FileAction> fileActions = new HashSet<FileAction>();
		Set<Rename> renames = new HashSet<Rename>();
		Map<String, User> users = new HashMap<String,User>();
		Map<at.ac.wu.infobiz.projectmining.model.File, List<Edit>> editsToFile = 
				new HashMap<at.ac.wu.infobiz.projectmining.model.File, List<Edit>>();
		
		scanner.useDelimiter(startCommitDelimiter);
		int numCommits = 0;
		
		Commit headCommit = null;
		while (scanner.hasNext()) {
			String commitChunk = scanner.next();
			Scanner intraCommitScanner = new Scanner(commitChunk);
			intraCommitScanner.useDelimiter(endMessageDelimiter);
			String commitHeaders = intraCommitScanner.next();
			
			Scanner headersScanner = new Scanner(commitHeaders);
			headersScanner.useDelimiter("\n");
			//Commit
			String commitLine = headersScanner.next();
			Commit commit = new Commit();
			commit.setProject(thisProject);
			String[] parsedCommitIds = parseCommitId(commitLine);
			commit.setRevisionId(parsedCommitIds[0]);
			if (parsedCommitIds[1]!=null){
				if (commits.containsKey(parsedCommitIds[1])){
					commit.addParent(commits.get(parsedCommitIds[1]));
					if (commits.containsKey(parsedCommitIds[2])){
						commit.addParent(commits.get(parsedCommitIds[2]));
					}
				} else {
					System.out.println("DEbug me!");
				}
			}
			headCommit = commit;
			
			//User
			String authorLine = headersScanner.next();
			User user = parseUser(parseAuthor(authorLine));
			//do we already know this user?
			if(users.containsKey(user.getName()+user.getEmail()))
				user = users.get(user.getName()+user.getEmail());
			else //unseen user. store him.
				users.put(user.getName()+user.getEmail(), user);
			
			user.addCommit(commit);
			commit.setUser(user);
			
			//Timestamp
			String dateString = headersScanner.next();
			DateTime timneStamp = parseDate(dateString);
			commit.setTimeStamp(timneStamp.toDate());
			
			//Comment
			headersScanner.useDelimiter(endMessageDelimiter);
			String messageString = headersScanner.next();
			String comment = parseMessage(messageString);
			commit.setComment(comment);
			headersScanner.close();
			
			
			//From here on there are the diffs and changes
			intraCommitScanner.useDelimiter("\ndiff");
			
			while (intraCommitScanner.hasNext()) {
				String afterDiff = intraCommitScanner.next();
				if(afterDiff.startsWith(" --git")){
					at.ac.wu.infobiz.projectmining.model.File fileFrom = new at.ac.wu.infobiz.projectmining.model.File();
					at.ac.wu.infobiz.projectmining.model.File fileTo = new at.ac.wu.infobiz.projectmining.model.File();
					Rename rename = new Rename();
					FileAction fileAction = new FileAction();
					
					if(isBinaryChange(afterDiff))
						continue; //ignore the changes for binary files  
					
					parseDiffs(afterDiff, fileFrom, fileTo, rename, fileAction);
					
					if(fileAction.getFile()==null)
						continue;
					
					//link to user, commit,
					fileAction.setCommit(commit);
					commit.addFileAction(fileAction);
					
					at.ac.wu.infobiz.projectmining.model.File file = null;
					switch (fileAction.getType()) {
					case DELETED:
						//take the first one
						file=fileFrom;
						break;
					case RENAMED:	
					case CREATED:
					case CHMOD:
					case MODIFIED: //take the second one
						file=fileTo;
						break;

					default:file=fileFrom;
						break;
					}
					
					//store into sets/maps
					if(files.containsKey(file.getPath())){//if it is already there, get it
						file = files.get(file.getPath());
					}
					else{
						files.put(file.getPath(),file);
					}
					
					//parse file actions
					fileAction.setFile(file);
					fileActions.add(fileAction);
					file.addFileAction(fileAction);
					
					if(rename.getFrom()!=null && rename.getTo()!=null){ //file was renamed
						rename.setCommit(commit);
						commit.addRename(rename);
						renames.add(rename);
						//make sure files referenced from rename are in the files collection
						if(!files.containsKey(rename.getFrom().getPath()))
							files.put(rename.getFrom().getPath(), rename.getFrom());
						if(!files.containsKey(rename.getTo().getPath()))
							files.put(rename.getTo().getPath(), rename.getTo());
					}
					//parse edits
					List<Edit> editsForThisFile = new ArrayList<Edit>();
					parseEdits(afterDiff, editsForThisFile);
					file.addEdits(editsForThisFile);
					for (Edit edit : editsForThisFile) {
						edit.setCommit(commit);
						commit.addEdit(edit);
						edit.setFile(file);
					}
					edits.addAll(editsForThisFile);
					
					//mapping file to corresponding edits
					if (editsToFile.containsKey(file)) {
						editsToFile.get(file).addAll(editsForThisFile);
					}
					else 
						editsToFile.put(file, editsForThisFile);
					
					//calculate size at this commit
					fileAction.setTotalLines(countLinesFromEdits(file, editsToFile));
				}
			}
			
			intraCommitScanner.close();
			commits.put(commit.getRevisionId(), commit);
			numCommits++;
			if(numCommits%100==0)
				System.out.print(".");
		}
		System.out.println();
		updateCommitParentsOf(headCommit);
		logger.info("Entities created. Going to persist entries relative to "+numCommits+" commits into database.");		
//		batchPersistEntities(thisProject, commits, files, fileActions, users, renames);
		statelessBatchPersistEntities(thisProject, commits, files, fileActions, users, renames, edits);
		scanner.close();
	}
	
	/**
	 * 
	 * @param commit the commit to which 
	 * @param file
	 * @param editsToFile 
	 * @return
	 */
	private Integer countLinesFromEdits(at.ac.wu.infobiz.projectmining.model.File file, Map<at.ac.wu.infobiz.projectmining.model.File, List<Edit>> editsToFile) {
		Integer count = 0;
		List<Edit> editsForFile = editsToFile.get(file);
		for (Edit e : editsForFile) {
			count+=e.getLinesAdded()-e.getLinesRemoved();
		}
		return count;
	}

	private void updateCommitParentsOf(Commit headCommit) {
		headCommit.setInTrunk(true);
		List<Commit> commitsInTrunk = new ArrayList<Commit>();
		commitsInTrunk.addAll(headCommit.getParents());
		while (!commitsInTrunk.isEmpty()){
			Commit cInTrunk = commitsInTrunk.remove(0);
			if (cInTrunk.isInTrunk()){
				// do nothing...
			} else {
				cInTrunk.setInTrunk(true);
				for (Commit c : cInTrunk.getParents()){
					if (!c.isInTrunk()){
						commitsInTrunk.add(0, c);
					}
				}
			}
		}
	}

	public void batchPersistEntities(Project thisProject,
			Map<String, Commit> commits,
			Map<String, at.ac.wu.infobiz.projectmining.model.File> files,
			Set<FileAction> fileActions, Map<String, User> users, Set<Rename> renames) {
		
		SessionFactory sessionFactory = DatabaseConnector.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		int total = users.size()+commits.size()+files.size()+renames.size();
		
		int i = 0;
		session.save(thisProject); //persist project
		
		//users
		for (String key : users.keySet()) {
//			System.out.println("Saving ... "+key);
			session.save(users.get(key));
			if ( ++i % BATCH_SIZE == 0 ) { 
				session.flush();
		        session.clear();
		    }
			
			if((100*i) % total == 0){
				System.out.println( 100*i / total);
			}
		}
//		System.out.println(100*(users.size()+commits.size())/total+"% done");
		for (String path : files.keySet()) {
			session.save(files.get(path));
			if ( ++i % BATCH_SIZE == 0 ) { 
				session.flush();
		        session.clear();
		    }
			if((100*i) % total == 0){
				System.out.println( 100*i / total);
			}
		}
		session.flush();
		session.clear();
//		System.out.println(100*(users.size()+commits.size()+files.size())/total+"% done");
		for (Rename rename : renames) {
			session.save(rename);
			if ( ++i % BATCH_SIZE == 0 ) { 
				session.flush();
		        session.clear();
		    }
			if((100*i) % total == 0){
				System.out.println(100* i / total);
			}
		}
		tx.commit();
		session.flush();
		session.close();
//		System.out.println(100*(users.size()+commits.size()+files.size()+renames.size())/total+"% done");
		DatabaseConnector.shutdown();
	}
	
	private void statelessBatchPersistEntities(Project thisProject,
			Map<String, Commit> commits,
			Map<String, at.ac.wu.infobiz.projectmining.model.File> files,
			Set<FileAction> fileActions, Map<String, User> users, Set<Rename> renames, Set<Edit> edits) {
		
		SessionFactory sessionFactory = DatabaseConnector.getSessionFactory();
		StatelessSession session = sessionFactory.openStatelessSession();
		Transaction tx = session.beginTransaction();
		
		long i = 0;
		session.insert(thisProject); //persist project
		
		//users
		logger.info("Persisting "+users.size()+" users");
		for (String key : users.keySet()) {
			session.insert(users.get(key));
			if(++i%(users.size()/10) == 0){
				System.out.print(".");
			}
		}
		System.out.println();
		
		logger.info("Persisting "+files.size()+" files");
		for (String path : files.keySet()) {
			session.insert(files.get(path));
			if(++i%(files.size()/10) == 0){
				System.out.print(".");
			}
		}
		System.out.println();
		
		logger.info("Persisting "+commits.size()+" commits");
		for (String key : commits.keySet()) {
			session.insert(commits.get(key));
			if(++i%(commits.size()/10) == 0){
				System.out.print(".");
			}
		}
		System.out.println();
		
		logger.info("Persisting "+fileActions.size()+" fileActions");
		for (FileAction fileAction : fileActions) {
			session.insert(fileAction);
			if(++i%(fileActions.size()/10) == 0){
				System.out.print(".");
			}
		}
		System.out.println();
		
		logger.info("Persisting "+renames.size()+" renames");
		for (Rename rename : renames) {
			session.insert(rename);
			if(++i%(renames.size()/10) == 0){
				System.out.print(".");
			}
		}
		System.out.println();
		
		logger.info("Persisting "+edits.size()+" edits");
		for (Edit edit : edits) {
			session.insert(edit);
			if(++i%(edits.size()/10) == 0){
				System.out.print(".");
			}
		}
		System.out.println();
		
		tx.commit();
		session.close();
//		System.out.println(100*(users.size()+commits.size()+files.size()+renames.size())/total+"% done");
		DatabaseConnector.shutdown();
	}


	/**
	 * This method iterates the part between two diffs and parses files, file actions and renames entities
	 * 
	 * @param afterDiff the part between two diffs
	 * @param fileFrom
	 * @param fileTo
	 * @param rename
	 * @param fileAction
	 */
	private static void parseDiffs(String afterDiff, at.ac.wu.infobiz.projectmining.model.File fileFrom,
			at.ac.wu.infobiz.projectmining.model.File fileTo, Rename rename, FileAction fileAction) {
		String[] headerLines = afterDiff.split("\n");
		String fileFromPath = "";
		String fileToPath="";
		boolean filePathsParsed = false;
		
//		if(isBinaryChange(afterDiff)) //we ignore binary changes
//			return;
		
		for (int i = 0; !filePathsParsed && i < headerLines.length; i++) {
			if(headerLines[i].startsWith("---")){
				fileFromPath+= parseFileFrom(headerLines[i]);//.split(" ")[1]; // {"---" : " " : "/path/from"}
			}
			if(headerLines[i].startsWith("+++")){
				fileToPath+=parseFileTo(headerLines[i]);//.split(" ")[1]; // {"+++" : " " : "/path/to"}
				filePathsParsed=true;
			}
		}
		
		if(filePathsParsed){
			
			String devNull = "/dev/null";
			boolean nullFirst = false;
			boolean nullSecond = false;
			
			if(fileFromPath.equals(devNull)){
				fileFrom.setPath(devNull);
				nullFirst=true;
			}
			else
				fileFrom.setPath(fileFromPath);
			
			if(fileToPath.equals(devNull)){
				fileTo.setPath(devNull);
				nullSecond=true;
			}
			else
				fileTo.setPath(fileToPath);
			
			if(nullFirst&&!nullSecond){
				//creation
				fileAction.setFile(fileTo);
				fileAction.setType(ActionType.CREATED);
			}
			else if (!nullFirst&&nullSecond) {
				//deletion
				fileAction.setFile(fileFrom);
				fileAction.setType(ActionType.DELETED);
			}
			else if (!nullFirst&&!nullSecond) {
				//modification
				fileAction.setFile(fileFrom);
				
				if(fileFrom.getPath().equals(fileTo.getPath())){
					fileAction.setType(ActionType.MODIFIED);
				}
				else{//modified & different pathnames
					//rename
					rename.setFrom(fileFrom);
					rename.setTo(fileTo);
					fileFrom.addRenameFrom(rename);
					fileTo.addRenameTo(rename);
					fileAction.setType(ActionType.MODIFIED);
				}
			}
		}
		else{ //here we can have a CHMOD or just a rename
			boolean chmod=isChangeMode(afterDiff);
			boolean isRename = false;
			
			for(int i=0;i<headerLines.length;i++){
				if(headerLines[i].startsWith("rename from")){
					fileFromPath = headerLines[i].replaceAll("rename from ","");
					isRename=true;
				}
				if(headerLines[i].startsWith("rename to")){
					fileToPath = headerLines[i].replaceAll("rename to ","").trim();
				}
			}
			
			if(!isRename){
				String[] arr = getFileNameFromSingleHeaderLine(headerLines[0]);
				fileFromPath = arr[0];
				fileToPath = arr[1];
			}
			
			fileFrom.setPath(fileFromPath);
			fileTo.setPath(fileToPath);
			
			if(chmod){
				fileAction.setFile(fileTo);
				fileAction.setType(ActionType.CHMOD);
				fileFrom.addFileAction(fileAction);
				fileTo.addFileAction(fileAction);
			}
			if(isRename){
//				if(chmod)
//					System.out.println(afterDiff);
				rename.setFrom(fileFrom);
				rename.setTo(fileTo);
				fileFrom.addRenameFrom(rename);
				fileTo.addRenameTo(rename);
				
				fileAction.setFile(fileTo);
				fileAction.setType(ActionType.RENAMED);
				fileFrom.addFileAction(fileAction);
				fileTo.addFileAction(fileAction);
			}
		}
	}
	
	/**
	 * 
	 * @param string ex. --git a/file1 b/file2
	 * @return
	 */
	private static String[] getFileNameFromSingleHeaderLine(String string) {
		String[] res = new String[2];
		String[] splits = string.split("\\s+");
		for (String s : splits) {
			if(s.startsWith("a/")){
				res[0] = parseFileFrom(s);
			}
			if(s.startsWith("b/")){
				res[1] = parseFileTo(string);
			}
		}
		return res;
	}

	private static boolean isChangeMode(String afterDiff) {
		return afterDiff.contains("mode");
	}

	/**
	 * 
	 * @param string it expects one line
	 * @return
	 */
	private static String parseFileTo(String string) {
		String devNull = "/dev/null";
		if(string.contains(devNull))
			return devNull;
		int start = string.indexOf("b/");
		return string.substring(start+2);
	}
	
	/**
	 * 
	 * @param string it expects one line
	 * @return
	 */
	private static String parseFileFrom(String string) {
		String devNull = "/dev/null";
		
		if(string.contains(devNull))
			return devNull;
		
		int start = string.indexOf("a/");
		
		return string.substring(start+2);
	}

	private static boolean isBinaryChange(String afterDiff) {
		/*	afterDiff wil be like:
		 * 	 --git a/c.jar b/c.jar
			deleted file mode 100644
			index b377d9a..0000000
			Binary files a/activiti-engine-test-api/target/activiti-engine-test-api-5.0.alpha3-SNAPSHOT.jar and /dev/null differ
		 */
		return afterDiff.contains("Binary files");
	}

	/**
	 * @param p 
	 * @param session
	 * @param commits
	 * @param files
	 * @param fileActions
	 * @param users
	 */
	public static void persistEntities(Project p, Map<String, Commit> commits,
			Map<String, at.ac.wu.infobiz.projectmining.model.File> files,
			Set<FileAction> fileActions, Map<String, User> users) {

		SessionFactory sf = DatabaseConnector.getSessionFactory();		
		Session session = sf.openSession();

		persist(session, p); //persist project
		
		for (String key : users.keySet()) {
			persist(session, users.get(key));
		}
		session.flush();
		
//		for (String fileName : files.keySet()) {
//			persist(session, files.get(fileName));
//		}
//		session.flush();
		
//		for (FileAction fileAction : fileActions) {
//			persist(session, fileAction);
//			
//		}
//		session.flush();
		
		for (String key : commits.keySet()) {
			persist(session, commits.get(key));
		}
		
		session.flush();
		session.close();
		DatabaseConnector.shutdown();
	}

	private static void persist(Session session, Object object) {	
		Transaction tx = session.getTransaction();
		tx.setTimeout(100);
		tx.begin();
		System.out.println("Saving ... "+object);
		session.save(object);
		tx.commit();
	}

	/**
	 * @param author
	 * @return 
	 */
	public static User parseUser(String author) {
		String[] authSplit = author.split("<");
		String userName = authSplit[0].trim();
		String userEmail = authSplit[1].substring(0, authSplit[1].length()-1);
		User u = new User();
		u.setName(userName);
		u.setEmail(userEmail);
		return u;
	}

	public static List<DiffResults> parseDiffsWithEdit(String diffString) {
		List<DiffResults> diffResults = new ArrayList<DiffResults>();
		String affectedFile = null;
		String devNull = "/dev/null";
		Scanner scanner = new Scanner(diffString);
		while(scanner.hasNext()){
			scanner.useDelimiter("[\\-]{3}");
			scanner.next();
			if(!scanner.hasNext())
				break; //is means that the file is binary
			scanner.reset();
			scanner.next();
			String fileFrom = scanner.next();
			if(!fileFrom.equals(devNull))
				fileFrom=fileFrom.substring(2);
			//file-to
			scanner.useDelimiter("[\\+]{3}");
			scanner.next();
			scanner.reset();
			scanner.next();
			String fileTo = scanner.next().trim();
			if(!fileTo.equals(devNull))
				fileTo=fileTo.substring(2);
			//what part of the file was affected 
			scanner.useDelimiter("diff");	
			String str = scanner.next();
			List<Edit> edits = new ArrayList<Edit>();
			
			if(!fileFrom.equals(devNull))
				affectedFile = fileFrom;
			else 
				affectedFile = fileTo;
			
			ParsingUtils.parsePositions(str.trim(), edits, new at.ac.wu.infobiz.projectmining.model.File(affectedFile));
			
			DiffResults dr = new DiffResults(fileFrom, fileTo, edits);
			diffResults.add(dr); 
		}
		scanner.close();
		return diffResults;
	}

	/**
	 * 
	 * @param fc
	 * @param fileFrom 	a/path/to/file
	 * @param fileTo	b/path/to/file
	 */
	public static void setTypeOfChange(FileChange fc, at.ac.wu.infobiz.projectmining.model.File fileFrom,
			at.ac.wu.infobiz.projectmining.model.File fileTo) {
		String devNull = "/dev/null";
		//		System.out.println(fc.getCommitID()+", from:"+fileFrom+",to: "+fileTo);

		if(fileFrom.getPath().equals(devNull)){
			fc.setCreated(true);
		}
		else{
			if(fileTo.getPath().equals(devNull)){
				fc.setDeleted(true);
			}
		}
	}

	/**
	 * Returns an array of strings (first position is the commit id itself)
	 * second and third are optional parent commit ids.
	 * @param commitString
	 * @return
	 */
	public static String[] parseCommitId(String commitString){
		String[] commitIDandParents = new String[3];
		String[] allTokens = commitString.split(" ");
		commitIDandParents[0] = allTokens[1];
		if(allTokens.length>=3){
			commitIDandParents[1] = allTokens[2];
			if(allTokens.length>=4){
				commitIDandParents[2] = allTokens[3];
			}
		}
			
		return commitIDandParents;
	}

	public static String parseAuthor(String authorString){
		String authorWithEmail = authorString.split("Author: ")[1].trim();
		return authorWithEmail;
	} 

	public static DateTime parseDate(String dateString){
		String date = dateString.split("Date: ")[1].trim();		 
		//		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
		DateTime theDate = gitFmt.parseDateTime(date);
		return theDate;
	}

	public static String parseMessage(String messageString) {
		String theMessage = messageString.split("Message:")[1].trim();
		return theMessage;
	}

	/**
	 * @param next
	 * @return
	 */
	public static void parseEdits(String positions, List<Edit> edits) {
		Pattern pattern = Pattern.compile("(?<=@@)( .*)(?=@@)");
		String[] editStrings = positions.split("\n@@ (.*) @@");
		int index = 1;
		Matcher matcher = pattern.matcher(positions);

		while (matcher.find())
		{
			String nextPositionChange = matcher.group(0).trim();
			//split into s1 = ["-l,s","+l,s"]
			String s1[] = nextPositionChange.split("\\s");

			//split s1[0] by ',' into ["-l","s"]
			String lsOld[] = s1[0].split(",");

			//split s1[1] by ',' into ["-l","s"]
			String lsNew[] = s1[1].split(",");

			int lOld = 0;
			int sOld = 0;
			int lNew = 0;
			int sNew = 0;

			lOld = Integer.parseInt(lsOld[0].substring(1));
			if(lsOld.length==2)
				sOld = Integer.parseInt(lsOld[1]);

			lNew = Integer.parseInt(lsNew[0].substring(1));

			if(lsNew.length==2)
				sNew = Integer.parseInt(lsNew[1]);
			
			while(index<editStrings.length){
				String editString = editStrings[index++];
				
				Edit edit = new Edit();
				edit.setFromPos(new Position(lOld, sOld));
				edit.setToPos(new Position(lNew, sNew));
//				int linesAdded = getLinesStartingWith(editString, "+");
//				int linesRemoved = getLinesStartingWith(editString, "-");
//				int unchangedLines = getLinesStartingWith(editString, " ");
				edit.setLinesAdded(getLinesStartingWith(editString, "+"));
				edit.setLinesRemoved(getLinesStartingWith(editString, "-"));
				edits.add(edit);
			}
		}
	}

	public static Integer getLinesStartingWith(String editString, String prefix) {
		int count = 0;		
		String[] lines = editString.split("\\n");
		for (String line : lines){
			if (line.startsWith(prefix)){
				count++;
			}
		}
		return count;
	}
}
