package at.ac.wu.infobiz.projectmining.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import at.ac.wu.infobiz.projectmining.io.OutputRedirect;
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
import at.ac.wu.infobiz.projectmining.parsing.git.GitToDB;
import at.ac.wu.infobiz.projectmining.parsing.git.Merge;

public abstract class ParsingUtils {

	public static void populateDB(Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> fileChangesMap, String projectName) {
		GitToDB.logger.info("Parsing filechanges map to create the entities and relationships");
		
		Set<at.ac.wu.infobiz.projectmining.model.File> keySet = fileChangesMap.keySet();
		Project p = new Project();
		p.setName(projectName);
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
				u = GitToDB.parseUser(author);
				if(users.containsKey(u.getName()+u.getEmail()))
					u=users.get(u.getName()+u.getEmail());
				//collect user
				else 
					users.put(u.getName()+u.getEmail(), u);
	
				if (commits.containsKey(fileChange.getCommitID())){
					c = commits.get(fileChange.getCommitID());
				} else {
					c = new Commit(fileChange.getCommitID(), fileChange.getTimeOfChange().toDate(), fileChange.getComment());
					c.setProject(p);
					c.setUser(u);
					u.addCommit(c);
					//collect commit
					commits.put(fileChange.getCommitID(), c);
				}
				
				if(files.containsKey(fileChange.getFile().getPath())){
					file = files.get(fileChange.getFile().getPath());
				}
				else{
					file = fileChange.getFile();
					files.put(fileChange.getFile().getPath(), file);
				}
				
				FileAction fa = new FileAction();
				fa.setCommit(c);
				fa.setFile(file);
				
				if(fileChange.isCreated())
					fa.setType(ActionType.CREATED);
				else 
					if(fileChange.isDeleted())
						fa.setType(ActionType.DELETED);
					else 
						fa.setType(ActionType.MODIFIED);
						
				c.addFileAction(fa);
				file.addFileAction(fa);
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
		
		GitToDB.logger.info("Persisting "+commits.size()+""
		+ " commits, "+users.size()+" users, "+files.size()+ " files, "+edits.size()+ " edits");
		
		GitToDB.persistEntities(p, commits, files, fileActions, users);
		GitToDB.logger.info("Data inserted into DB.");
	}

	/**
	 * 
	 * @param fileChangesMap
	 * @param outputFile
	 */
	public static void printToFile(
			Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> fileChangesMap, String outputFile) {
		try {
			OutputRedirect.toFile(outputFile);
			Set<at.ac.wu.infobiz.projectmining.model.File> keySet = fileChangesMap.keySet();
			for (at.ac.wu.infobiz.projectmining.model.File key : keySet) {
				System.out.println(fileChangesMap.get(key));
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		OutputRedirect.toConsole();
	}

	/**
	 * Not complete!
	 * 
	 * @return
	 */
	public static Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> loadFileChangeMapNoNumStat(GitToDB gitToDB) {
		Map<at.ac.wu.infobiz.projectmining.model.File,List<FileChange>> fileChangesMap = 
				new HashMap<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>>();
		Set<Merge> allMerges = new TreeSet<Merge>();
		try{
			Scanner interCommitScanner = new Scanner(new FileInputStream(new java.io.File(gitToDB.getInputFile())));
			interCommitScanner.useDelimiter(gitToDB.getStartCommitDelimiter());
			int numCommits = 0;
			String devNull = "/dev/null";
			while (interCommitScanner.hasNext()) {
				numCommits++;
				String entry = interCommitScanner.next();
				String[] entries = entry.split("\n");
				String commitLine = null;
				String commitID = null;
				
				String author = null; 
				DateTime dateOfChange = null;
				String comment = null; 
				
				for (String s : entries) {
					if(s.toLowerCase().startsWith("commit")) {
						commitLine = s;
						String[] comms = GitToDB.parseCommitId(commitLine);
						commitID = comms[0];
					}
					else 
						if(s.toLowerCase().startsWith("author"))
							author = GitToDB.parseAuthor(s);
						else 
							if(s.toLowerCase().startsWith("date"))
								dateOfChange = GitToDB.parseDate(s);
							else
								if(s.toLowerCase().startsWith("message"))
									comment = GitToDB.parseMessage(s);//trim
								else
									if(s.startsWith(gitToDB.getEndMessageDelimiter()))
										break;
				}
				
				Scanner intraCommit = new Scanner(entry);
				intraCommit.useDelimiter(gitToDB.getEndMessageDelimiter());
				intraCommit.next();
				// parse diffs
				intraCommit.useDelimiter("diff");
				intraCommit.next();
				String diffString = intraCommit.next().trim();
				List<DiffResults> diffResults = GitToDB.parseDiffsWithEdit(diffString);
	
				for (DiffResults diffRes : diffResults) {
					List<FileChange> fChList;				
					//TODO: capture the case when a file is renamed
					//if a file is created/modified use it's last name
					if(!diffRes.getFileTo().getPath().equals(devNull)){ 
						fChList = fileChangesMap.get(diffRes.getFileTo());
					}
					else //if a file is deleted use it's name in file-from
						fChList = fileChangesMap.get(diffRes.getFileFrom());
					
					ParsingUtils.addChangePositionsFromDiffResults(diffRes, fChList);
				}
				intraCommit.close();
			}
			interCommitScanner.close();
			System.out.println("Loaded "+numCommits+" commits. Merge commits = "+allMerges.size());
		} catch (InputMismatchException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileChangesMap;
	}

	/**
		 * @return
		 */
		public static Map<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>> loadFileChangeMapWithNumStat(GitToDB gitToDB) {
			Map<at.ac.wu.infobiz.projectmining.model.File,List<FileChange>> fileChangesMap = 
					new HashMap<at.ac.wu.infobiz.projectmining.model.File, List<FileChange>>();
			try{
				Scanner interCommitScanner = new Scanner(new FileInputStream(new java.io.File(gitToDB.getInputFile())));
				interCommitScanner.useDelimiter(gitToDB.getStartCommitDelimiter());
				int numCommits = 0;
				String devNull = "/dev/null";
				Set<Merge> allMerges = new TreeSet<Merge>();
				while (interCommitScanner.hasNext()) {
					numCommits++;
					String entry = interCommitScanner.next().trim();
					Scanner intraCommit = new Scanner(entry);
					boolean foundMerge = false;
					String commitLine = intraCommit.nextLine();
					String[] comms =  GitToDB.parseCommitId(commitLine);
					String commitID = comms[0];
					String author = GitToDB.parseAuthor(intraCommit.nextLine());
					DateTime dateOfChange = GitToDB.parseDate(intraCommit.nextLine());
					intraCommit.useDelimiter(gitToDB.getEndMessageDelimiter());
					String comment = GitToDB.parseMessage(intraCommit.next()); //I need to perform this step to move on.
					intraCommit.nextLine();
	//Here I might have a merge!!!!!
					foundMerge = entry.endsWith(gitToDB.getEndMessageDelimiter());
					if(foundMerge){
						allMerges .add(ParsingUtils.createMergeFromCommitLine(commitLine));
					}
					
					else{
						//parse changes (up to diff)
						intraCommit.useDelimiter("\ndiff");
						List<FileChange> changes = ParsingUtils.parseChanges(intraCommit.next().trim());
	//					TODO:delete
	//					System.out.println(numCommits);
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
						intraCommit.useDelimiter(gitToDB.getStartCommitDelimiter());
						String diffString = intraCommit.next().trim();
						List<DiffResults> diffResults = ParsingUtils.parseDiffs(diffString);
		
						for (DiffResults diffRes : diffResults) {
							List<FileChange> fChList;				
							//TODO: capture the case when a file is renamed
							//if a file is created/modified use it's last name
							if(!diffRes.getFileTo().getPath().equals(devNull)){ 
								fChList = fileChangesMap.get(diffRes.getFileTo());
							}
							else //if a file is deleted use it's name in file-from
								fChList = fileChangesMap.get(diffRes.getFileFrom());
							
							ParsingUtils.addChangePositionsFromDiffResults(diffRes, fChList);
						}
						intraCommit.close();
					}
				}
				interCommitScanner.close();
				System.out.println("Loaded "+numCommits+" commits. Merges = "+allMerges.size());
			} catch (InputMismatchException | FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			return fileChangesMap;
		}

	public static void parsePositions(String positions, List<Edit> edits,
			at.ac.wu.infobiz.projectmining.model.File affectedFile) {
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
				edit.setLinesAdded(GitToDB.getLinesStartingWith(editString, "+"));
				edit.setLinesRemoved(GitToDB.getLinesStartingWith(editString, "-"));
				edit.setFile(affectedFile);
				edits.add(edit);
			}
		}
	}

	public static Merge createMergeFromCommitLine(String commitLine) {
		String[] comms = commitLine.split(" ");
		return new Merge(comms[0],comms[1], comms[2]);
	}

	/**
	 * @param diffRes
	 * @param fChList
	 */
	public static void addChangePositionsFromDiffResults(DiffResults diffRes,
			List<FileChange> fChList) {
		if(fChList==null)
			return;
		FileChange fc = fChList.get(fChList.size()-1);
		fc.setEdits(diffRes.getEdits());
		//		fc.setPositionsFrom(new ArrayList<Position>(diffRes.getChangesFrom()));
		//		fc.setPositionsTo(new ArrayList<Position>(diffRes.getChangesTo()));
		GitToDB.setTypeOfChange(fc, diffRes.getFileFrom(), diffRes.getFileTo());
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
	public static ArrayList<FileChange> parseChanges(String changesString){
		ArrayList<FileChange> changes = new ArrayList<FileChange>();
		//		int adds, dels;
		String fileName = "";
		String cleanChangeString = ParsingUtils.cleanOutBinaryEntries(changesString);
		Scanner s = new Scanner(cleanChangeString);
		s.useDelimiter("\n");
		while (s.hasNext()) {
			String line = s.next();
			String[] parts = line.split("\\s+");
			//parts[0], parts[1] are the total number of lines added, removed, respectively
			//part[2] is the filename if it does not contain spaces
			//otherwise append the subsequent parts
			fileName="";
			for (int i = 2; i < parts.length; i++) {
				fileName+=parts[i];
			}
			changes.add(new FileChange(new at.ac.wu.infobiz.projectmining.model.File(fileName)));
		}
		s.close();
		return changes;
	}

	public static String cleanOutBinaryEntries(String changesString) {
		String res = "";
		Scanner s = new Scanner(changesString);
		s.useDelimiter("\n");
		while (s.hasNext()) {
			String line = s.next();
			if(!line.matches("\\d+\\s+\\d+\\s+\\p{Graph}+.*")){
				continue;
			}
			res+=line+"\n";
		}
		s.close();
		return res.trim();
	}

	public static List<DiffResults> parseDiffs(String diffString){
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
			scanner.useDelimiter("\ndiff");	
			String str = scanner.next();
			List<Edit> edits = new ArrayList<Edit>();
			GitToDB.parseEdits(str.trim(), edits);
			DiffResults dr = new DiffResults(fileFrom, fileTo, edits);
			diffResults.add(dr); 
		}
		scanner.close();
		return diffResults;
	}

}
