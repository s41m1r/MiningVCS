package at.ac.wu.infobiz.projectmining.parsing.mercurial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import at.ac.wu.infobiz.projectmining.model.Commit;
import at.ac.wu.infobiz.projectmining.model.MercurialCommit;
import at.ac.wu.infobiz.projectmining.model.Project;
import at.ac.wu.infobiz.projectmining.model.User;

public class MercurialToDB {

	private String inputFile;
	private String startCommitDelimiter;
	private String endMessageDelimiter;

	static int BATCH_SIZE = 100000;

	public static Logger logger = Logger.getLogger(MercurialToDB.class.getName());

	public MercurialToDB(String inputFile) {
		this.inputFile = inputFile;
	}

	public MercurialToDB(String inputFile, String startCommitDelimiter,
			String endMessageDelimiter) {
		this.inputFile = inputFile;
		this.startCommitDelimiter = startCommitDelimiter;
		this.endMessageDelimiter = endMessageDelimiter;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getStartCommitDelimiter() {
		return startCommitDelimiter;
	}

	public void setStartCommitDelimiter(String startCommitDelimiter) {
		this.startCommitDelimiter = startCommitDelimiter;
	}

	public String getEndMessageDelimiter() {
		return endMessageDelimiter;
	}

	public void setEndMessageDelimiter(String endMessageDelimiter) {
		this.endMessageDelimiter = endMessageDelimiter;
	}

	public void toCSV(File logFile, File output) throws FileNotFoundException{

		logger.info("Parsing of "+logFile+" started");

		FileInputStream fis = new FileInputStream(logFile);
		Scanner scanner = new Scanner(fis);

		Project thisProject = new Project();
		thisProject.setName(inputFile);
	
		List<User> usersList = new ArrayList<User>();
		List<MercurialCommit> commitsList = new ArrayList<MercurialCommit>();		
		
		int numCommits = 0;
		while (scanner.hasNext()) {
			String commitChunk = "";
			
			for(String s=""; scanner.hasNextLine() && !(s=scanner.nextLine()).isEmpty();commitChunk+="\n"+s);
			
			usersList.add(parseUser(commitChunk));
			commitsList.add(parseCommit(commitChunk));
			numCommits++;
		}
		scanner.close();
		logger.info("Done. Read "+numCommits+" commits.");
		logger.info("Writing to "+output+".");
		PrintWriter pw = new PrintWriter(new FileOutputStream(output));
		String csvHeader = "revisionId\tbranch\ttimestamp\tuser\tcomment\tparent1\tparent2";
		pw.println(csvHeader);
		
		for (int i = 0; i < commitsList.size(); i++) {
			String csvRow = commitsList.get(i).getRevisionId()+
					"\t"+
					commitsList.get(i).getBranch()+
					"\t"+
					commitsList.get(i).getTimeStamp()+
					"\t"+
					usersList.get(i).getName()+
					"\t"+
					commitsList.get(i).getComment()+
					"\t";
			
			if(commitsList.get(i).getParents().size()==2)
				csvRow+=commitsList.get(i).getParents().get(0).getRevisionId()+ 
				"\t" +commitsList.get(i).getParents().get(0).getRevisionId();
			if(commitsList.get(i).getParents().size()==1)
				csvRow+=commitsList.get(i).getParents().get(0).getRevisionId()+ 
				"\t" + null;
			if(commitsList.get(i).getParents().size()==0)
				csvRow+=null+ "\t" + null;
			
			pw.println(csvRow);
		}
		pw.close();
		logger.info("Done.");
	}

	private MercurialCommit parseCommit(String commitChunk) {
		MercurialCommit mc;
		String commitId = parseCommitId(commitChunk);
		Date date = parseDate(commitChunk);
		String comment = parseComment(commitChunk);
		String branch = parseBranch(commitChunk);
		List<Commit> parents = parseParents(commitChunk);
		
		mc = new MercurialCommit(commitId, date, comment, branch);
		mc.setParents(parents);
		return mc;
	}

	private List<Commit> parseParents(String commitChunk) {
		List<String> parents = lookFor("parent:", commitChunk);
		List<Commit> res = new ArrayList<Commit>();
		for (String string : parents){
			Commit c = new Commit();
			c.setRevisionId(string);
			res.add(c);
		}
		return res;
	}

	private String parseBranch(String commitChunk) {
		List<String> allBraches = lookFor("branch:", commitChunk);
		if (allBraches.size()==0)
			return null;
		return allBraches.get(0);
	}

	private User parseUser(String commitChunk) {
		User user = new User();
		user.setName(lookFor("user:", commitChunk).get(0).split("<")[0].trim());
		return user;
	}
	
	private List<String> lookFor(String startingWith, String commitChunk){
		List<String> allTermsWith = new ArrayList<String>();
		Scanner s = new Scanner(commitChunk);
		while (s.hasNext()) {
			String line = s.nextLine(); //e.g. user:        tmertes
			if(line.startsWith(startingWith)){
				allTermsWith.add(line.split(startingWith)[1].trim());
			}
		}
		s.close();
		return allTermsWith;
	}

	private String parseComment(String commitChunk) {
		return lookFor("summary:", commitChunk).get(0);
	}

	private Date parseDate(String commitChunk) {
		List<String> res = lookFor("date:", commitChunk);
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
		DateTime theDate = fmt.parseDateTime(res.get(0).trim());
		return theDate.toDate();
	}

	/* Commit string is like
	 * changeset:   7656:93bde685ce19
	 */
	private String parseCommitId(String commitChunk) {
		return lookFor("changeset:", commitChunk).get(0).trim();
	}

}
