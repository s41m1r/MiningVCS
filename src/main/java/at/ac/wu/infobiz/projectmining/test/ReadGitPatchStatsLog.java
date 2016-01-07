package at.ac.wu.infobiz.projectmining.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import at.ac.wu.infobiz.projectmining.io.CommandLineOptions;
import at.ac.wu.infobiz.projectmining.io.OutputRedirect;
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

public class ReadGitPatchStatsLog {

	static String inputFile = "data/jgit-cookbook.log";
	static String outputFile= "out.txt";

	final static String START_COMMIT_DELIMITER = "§§--§§\n";
	final static String END_MESSAGE_DELIMITER = "---§";

	public static void main(String[] args) {

		Map<String, String> optionsMap = CommandLineOptions.parseArgs(args);

		String input = optionsMap.get(CommandLineOptions.LOGFILE);
		if(input!=null)
			inputFile=input;
		String output = optionsMap.get(CommandLineOptions.LOGFILE);
		if(output!=null)
			outputFile=output;

		Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> fileChangesMap = loadFileChangeMap();

		printToFile(fileChangesMap, outputFile);
		OutputRedirect.toConsole();

		String projectName = inputFile;

		populateDB(fileChangesMap, projectName);

	}

	private static void populateDB(Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> fileChangesMap, String projectName) {
		SessionFactory sf = DatabaseConnector.getSessionFactory();		
		Session session = sf.openSession();

		Set<at.ac.wu.infobiz.projectmining.model.File> keySet = fileChangesMap.keySet();
		Project p = new Project();
		p.setName(projectName);
		persist(session, p);

		Map<String,Commit> commits = new HashMap<String,Commit>();
		Set<Edit> edits = new HashSet<Edit>();
		Map<String, at.ac.wu.infobiz.projectmining.model.File> files = new HashMap<String,at.ac.wu.infobiz.projectmining.model.File>();
		Set<FileAction> fileActions = new HashSet<FileAction>();
		Set<Rename> renames = new HashSet<Rename>();
		Map<String, User> users = new HashMap<String,User>();

		//collect
		for (at.ac.wu.infobiz.projectmining.model.File key : keySet) {

			Commit c;
			User u;
			at.ac.wu.infobiz.projectmining.model.File file = null;

			List<FileChange> fileChanges = fileChangesMap.get(key);
			for (FileChange fileChange : fileChanges) {
				String author = fileChange.getAuthor();
				u = parseUser(author);
				//collect user
				users.put(u.getName()+u.getEmail(), u);

				if (commits.containsKey(fileChange.getCommitID())){
					c = commits.get(fileChange.getCommitID());
				} else {
					c = new Commit(fileChange.getCommitID(), fileChange.getTimeOfChange().toDate(), fileChange.getComment());
					c.setProject(p);
					//persist commit
					commits.put(fileChange.getCommitID(), c);
				}
				if(files.containsKey(fileChange.getFile().getPath())){
					for (FileChange fch : fileChanges) {
						if(fch.getFile().equals(fileChange.getFile())){
							file = fch.getFile();
							break;
						}
					}
				}
				else{
					file = fileChange.getFile();
					files.put(fileChange.getFile().getPath(), file);
				}
				FileAction fa = new FileAction();
				fa.setCommit(c);
				fa.setFile(file);
				c.addFileAction(fa);
				file.addFileActions(fa);
				//persist file
				files.put(file.getPath(), file);
				List<Edit> editList = fileChange.getEdits();

				for(Edit eddy : editList){
					eddy.setCommit(c);
					eddy.setFile(file);
					edits.add(eddy);
					c.addEdit(eddy);
					file.addEdit(eddy);
				}

				fileActions.add(fa);
			}
		}


		//persist

		for (String key : users.keySet()) {
			persist(session, users.get(key));
		}

		for (String path : files.keySet()) {
			persist(session, files.get(path));
		}

//		for (String key : commits.keySet()) {
//			persist(session, commits.get(key));
//		}

		//		for (Edit edit : edits) {
		//			persist(session, edit);
		//		}

		session.flush();
		session.close();
		DatabaseConnector.shutdown();
		System.out.println("Done.");
	}


	private static void persist(Session session, Object object) {				
		Transaction tx = session.getTransaction();
		tx.setTimeout(100);
		tx.begin();
		session.save(object);
		tx.commit();
	}

	/**
	 * @param author
	 * @return 
	 */
	private static User parseUser(String author) {
		String[] authSplit = author.split("<");
		String userName = authSplit[0].trim();
		String userEmail = authSplit[1].substring(0, authSplit[1].length()-1);
		User u = new User();
		u.setName(userName);
		u.setEmail(userEmail);
		return u;
	}


	private static void printToFile(
			Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> fileChangesMap, String outputFile) {
		try {
			OutputRedirect.toFile(outputFile);
			//			String header = "fileName,commitID,timeOfChange,linesAdded,linesRemoved,deleted,WhereChanged,handOverStack";
			//			System.out.println(header);
			Set<at.ac.wu.infobiz.projectmining.model.File> keySet = fileChangesMap.keySet();
			for (at.ac.wu.infobiz.projectmining.model.File key : keySet) {
				//				System.out.println(key);
				System.out.println(fileChangesMap.get(key));
				//				System.out.println();
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private static Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> loadFileChangeMap() {
		Map<at.ac.wu.infobiz.projectmining.model.File,List<FileChange>> fileChangesMap = 
				new HashMap<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>>();
		try{
			Scanner interCommitScanner = new Scanner(new File(inputFile));
			interCommitScanner.useDelimiter(START_COMMIT_DELIMITER);
			int numCommits = 0;
			String devNull = "/dev/null";
			while (interCommitScanner.hasNext()) {
				numCommits++;
				String entry = interCommitScanner.next().trim();
				Scanner intraCommit = new Scanner(entry);
				String commitLine = intraCommit.nextLine();
				String[] comms =  parseCommitId(commitLine);
				String commitID = comms[0];
				String author = parseAuthor(intraCommit.nextLine());
				DateTime dateOfChange = parseDate(intraCommit.nextLine());
				intraCommit.useDelimiter(END_MESSAGE_DELIMITER);
				String comment = parseMessage(intraCommit.next()); //I need to perform this step to move on.
				intraCommit.nextLine();

				//parse changes (up to diff)
				intraCommit.useDelimiter("diff");
				List<FileChange> changes = parseChanges(intraCommit.next().trim());
				for (FileChange fCh : changes) {
					fCh.setCommitID(commitID);
					fCh.setAuthor(author);
					fCh.setTimeOfChange(dateOfChange);
					fCh.setComment(comment);
					if(fileChangesMap.containsKey(fCh.getFile())){
						List<FileChange> changeList = fileChangesMap.get(fCh.getFile());
						changeList.add(fCh);
					}
					else{
						List<FileChange> chList = new ArrayList<FileChange>();
						chList.add(fCh);
						fileChangesMap.put(fCh.getFile(),chList);
					}
				}		
				// parse diffs
				intraCommit.useDelimiter(START_COMMIT_DELIMITER);
				String diffString = intraCommit.next().trim();
				List<DiffResults> diffResults = parseDiffs(diffString);

				for (DiffResults diffRes : diffResults) {
					List<FileChange> fChList;				
					//TODO: capture the case when a file is renamed
					//if a file is created/modified use it's last name
					if(!diffRes.getFileTo().getPath().equals(devNull)){ 
						fChList = fileChangesMap.get(diffRes.getFileTo());
					}
					else //if a file is deleted use it's name in file-from
						fChList = fileChangesMap.get(diffRes.getFileFrom());

					addChangePositionsFromDiffResults(diffRes, fChList);
				}
				intraCommit.close();
			}
			interCommitScanner.close();
			System.out.println("loaded "+numCommits+" commits");
		} catch (InputMismatchException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileChangesMap;
	}

	/**
	 * @param diffRes
	 * @param fChList
	 */
	private static void addChangePositionsFromDiffResults(DiffResults diffRes,
			List<FileChange> fChList) {
		if(fChList==null)
			return;
		FileChange fc = fChList.get(fChList.size()-1);
		fc.setEdits(diffRes.getEdits());
		//		fc.setPositionsFrom(new ArrayList<Position>(diffRes.getChangesFrom()));
		//		fc.setPositionsTo(new ArrayList<Position>(diffRes.getChangesTo()));
		setTypeOfChange(fc, diffRes.getFileFrom(), diffRes.getFileTo());
	}

	/**
	 * 
	 * @param fc
	 * @param fileFrom 	a/path/to/file
	 * @param fileTo	b/path/to/file
	 */
	private static void setTypeOfChange(FileChange fc, at.ac.wu.infobiz.projectmining.model.File fileFrom,
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

	static String[] parseCommitId(String commitString){
		String[] commitIDandParent = new String[2];
		String[] allTokens = commitString.split(" ");
		commitIDandParent[0] = allTokens[1];
		if(allTokens.length==3)
			commitIDandParent[1] = allTokens[2];
		return commitIDandParent;
	}

	static String parseAuthor(String authorString){
		String authorWithEmail = authorString.split("Author: ")[1].trim();
		return authorWithEmail;
	} 

	static DateTime parseDate(String dateString){
		String date = dateString.split("Date: ")[1].trim();		 
		//		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
		DateTime theDate = gitFmt.parseDateTime(date);
		return theDate;
	}

	static String parseMessage(String messageString) {
		String theMessage = messageString.split(END_MESSAGE_DELIMITER)[0].trim();
		return theMessage;
	}

	/**
	 * 
	 * @param changesString. a changeString is like:
	 * 	52	42	MiningCVS/src/main/ProjectMiner.java
		32	5	MiningCVS/src/model/tree/Node.java
		38	4	MiningCVS/src/model/tree/Tree.java
		2	3	MiningCVS/src/reader/GITLogReader.java
		0	14	MiningCVS/src/test/TestLog.java
	 * @return
	 */
	static ArrayList<FileChange> parseChanges(String changesString){
		ArrayList<FileChange> changes = new ArrayList<FileChange>();
		//		int adds, dels;
		String fileName;
		String cleanChangeString = cleanOutBinaryEntries(changesString);
		//		System.out.println(" **Clean String:"+cleanChangeString+"*************");
		Scanner s = new Scanner(cleanChangeString);
		while (s.hasNext()) {
			//			adds = 
			s.nextInt();
			//			dels = 
			s.nextInt();
			fileName = s.next();
			changes.add(new FileChange(new at.ac.wu.infobiz.projectmining.model.File(fileName)));
		}
		s.close();
		return changes;
	}

	private static String cleanOutBinaryEntries(String changesString) {
		String res = "";
		Scanner s = new Scanner(changesString);
		s.useDelimiter("\n");
		while (s.hasNext()) {
			String line = s.next();
			if(line.startsWith("-"))
				continue;
			res+=line+"\n";
		}
		s.close();
		return res.trim();
	}

	static List<DiffResults> parseDiffs(String diffString){
		List<DiffResults> diffResults = new ArrayList<DiffResults>();
		Scanner scanner = new Scanner(diffString);
		while(scanner.hasNext()){
			scanner.useDelimiter("[\\-]{3}");
			scanner.next();
			if(!scanner.hasNext())
				break; //is means that the file is binary
			scanner.reset();
			scanner.next();
			String fileFrom = scanner.next();
			//file-to
			scanner.useDelimiter("[\\+]{3}");
			scanner.next();
			scanner.reset();
			scanner.next();
			String fileTo = scanner.next().trim();
			//what part of the file was affected 
			scanner.useDelimiter("diff");	
			String str = scanner.next();
			List<Edit> edits = new ArrayList<Edit>();
			parsePositions(str.trim(), edits);
			DiffResults dr = new DiffResults(fileFrom, fileTo, edits);
			diffResults.add(dr); 
		}
		scanner.close();
		return diffResults;
	}

	/**
	 * @param next
	 * @return
	 */
	private static void parsePositions(String positions, List<Edit> edits) {
		Pattern pattern = Pattern.compile("(?<=@@)(.*)(?=@@)");

		String[] editStrings = positions.split("@@ (.*) @@");
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

			String editString = editStrings[index++];
			Edit edit = new Edit();
			edit.setFromPos(new Position(lOld, sOld));
			edit.setToPos(new Position(lNew, sNew));
			edit.setLinesAdded(getLinesStartingWith(editString, "+"));
			edit.setLinesRemoved(getLinesStartingWith(editString, "-"));
			edits.add(edit);
		}
	}

	private static Integer getLinesStartingWith(String editString, String prefix) {
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
