package at.ac.wu.infobiz.projectmining.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import at.ac.wu.infobiz.projectmining.visualization.FileStory;
import at.ac.wu.infobiz.projectmining.visualization.FileStoryRecord;
import at.ac.wu.infobiz.projectmining.visualization.TimeSeriesTable;

public class ExportTimeSeriesCorrelation {

	private static final double STRENGTH_THRESHOLD = 0.7;
	private static final double WEAK_THRESHOLD = 0.3;
	private static final String SEP = "\t";
	//#Artifacts	#Users	#Containments	#MaxPossibleContainments	
//	#Strong Dependencies	#Weak Dependencies	#Intra-Containment Dependencies	
//	#Inter-Containment Dependencies	#Strong Inter-Containment Dependencies	
//	AvgTreeDepth	MaxTreeDepth	#AvgActivitiesPerProcess	#AvgUsersPerArtifact
	
	public static int ARTIFACTS;
//	public static int USERS;
	public static int CONTAINMENTS;
	public static int MAX_CONTAINMENTS;
	public static int STRONG_DEPENDENCIES;
	public static int WEAK_DEPENDENCIES;
	public static int INTRA_CONTA_DEPEND;
	public static int WEAK_INTRA_CONTA_DEPEND;
	public static int STRONG_INTRA_CONTA_DEPEND;
	public static int INTER_CONTA_DEPEND;
	public static int STRONG_INTER_CONTA_DEP;
	public static int WEAK_INTER_CONTA_DEP;
	public static double AVG_TREE_DEPTH;
	public static double MAX_TREE_DEPTH;
	public static double AVG_ACITIVIES_PROCESS;
	public static double AVG_USER_ARTIFACT;


	public static void main(String[] args) throws FileNotFoundException, IOException {
		String folder = null;
		String outFile = null;
		if(args.length == 1){
			folder = args[0];
			folder = JOptionPane.showInputDialog("Stories-folder?");
			if(folder==null)
				System.exit(-1);
			outFile = folder+"-"+DateTime.now()+".csv";
		}
		if(folder == null)
			System.exit(-1);
		
		doAnalysisForFoler(folder, outFile);
	}

	/**
	 * @param folder
	 * @param outFile
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private static void doAnalysisForFoler(String folder, String outFile) throws FileNotFoundException, IOException {
		long start = System.currentTimeMillis();
		System.out.println("Reading stories from folder: "+folder);

		List<File> allFiles = listFilesForFolder(new File(folder));
		Map<File, FileStory> map = getAllFileStories(allFiles);
		
		for (File file : map.keySet()) {
			Collections.sort(map.get(file).story);
		}
		
		TimeSeriesTable tst = new TimeSeriesTable(map);
		map = tst.getExpandedFileStoryMap();
//		System.out.println(map);

		Map<String, String> namesMap = loadNamesMap(folder.split("-")[0]+"-namesMap/namesMap.csv");

		System.out.println("Computing correlations.");
		double[][] correlationMatrix = doPairWiseCorrelations2(namesMap, map, 3);
		
//		System.out.println(map.size()+" Total files");
		ARTIFACTS = map.size();
		
		System.out.println("Computing containments.");
//		boolean[][] containmentsMatrix = doPairWiseContainmentsCheck(map);
		boolean[][] containmentsMatrix = doPairWiseContainmentsCheck2(namesMap);
		
		printCorrelationMatrix(correlationMatrix, map, "correlations/"+outFile);
		printContainmentsMatrix2(containmentsMatrix, namesMap, "containments/"+outFile);
		
//		System.out.println(computeUsersPerFile(map) + " average users per file");
//		System.out.println(computeAverageProcessLength(map)+ " average process length");
		
		AVG_USER_ARTIFACT = computeUsersPerFile(map);
		AVG_ACITIVIES_PROCESS = computeAverageProcessLength(map);
		
		System.out.println("Done. "+(System.currentTimeMillis()-start)/1000.0+ " Sec.");
		System.out.println("Results written into: "+outFile);
		
		System.out.println("ARTIFACTS"+SEP+"CONTAINMENTS"+SEP+"STRONG_DEPENDENCIES"+SEP+"WEAK_DEPENDENCIES"+SEP+""
				+ "INTRA_CONTA_DEPEND"+SEP+" WEAK_INTRA_CONTA_DEPEND"+SEP+" STRONG_INTRA_CONTA_DEPEND"+SEP+" "
				+ "INTER_CONTA_DEPEND"+SEP+"WEAK_INTER_CONTA_DEP"+SEP+" STRONG_INTER_CONTA_DEP"+SEP+""
				+ "AVG_TREE_DEPTH"+SEP+" MAX_TREE_DEPTH"+SEP+""
				+ "AVG_ACITIVIES_PROCESS"+SEP+"AVG_USER_ARTIFACT");
		System.out.println(folder+SEP+""+SEP+""+SEP+""+SEP+ARTIFACTS+SEP+CONTAINMENTS+SEP+
				STRONG_DEPENDENCIES+SEP+WEAK_DEPENDENCIES+SEP+INTRA_CONTA_DEPEND+SEP+WEAK_INTRA_CONTA_DEPEND+SEP+
				STRONG_INTRA_CONTA_DEPEND+SEP+INTER_CONTA_DEPEND+SEP+WEAK_INTER_CONTA_DEP+SEP+STRONG_INTER_CONTA_DEP+SEP+
				AVG_TREE_DEPTH+SEP+MAX_TREE_DEPTH+SEP+
				AVG_ACITIVIES_PROCESS+SEP+AVG_USER_ARTIFACT);
	}
	
	private static double[][] doPairWiseCorrelations2(Map<String, String> namesMap, Map<File, FileStory> map, int field) {
		List<File> files = new ArrayList<File>(map.keySet());
//		String[] header = new String[files.size()+1];
		double[][] correlationMatrix = new double[files.size()][files.size()];
		int strongCorCount = 0, corCount=0;
		int interContainmentCount = 0, weakInterContainmentCount = 0, strongInterContainmentCount = 0;
		int intraContainmentCount = 0, weakIntraContainmentCount = 0, strongIntraContainmentCount = 0;
		int weakCorCount = 0;
		for(int i=0; i<files.size(); i++){
			for(int j=i; j<files.size(); j++){
				FileStory fs1 = map.get(files.get(i));
				FileStory fs2 = map.get(files.get(j));
				if(fs1.story.size()!=fs2.story.size()){
					System.err.println("Size of stories not matching! "+files.get(i)+" has "+fs1.story.size()+ ""
							+ " and "+files.get(j)+ " has "+fs2.story.size());
					continue;
				}
				double cor = 0;
				try {
					File f1 = new File(namesMap.get(files.get(i).toString()));
					File f2 = new File(namesMap.get(files.get(j).toString()));
					cor = correlation(fs1, fs2, field);
					corCount++;
					
					boolean sameContainment = sameContainment(f1, f2);
					
					if(sameContainment){
						intraContainmentCount++;
						if(Math.abs(cor)>STRENGTH_THRESHOLD){
							strongIntraContainmentCount++;
							strongCorCount++;
						}
						if(Math.abs(cor)<WEAK_THRESHOLD){
							weakIntraContainmentCount++;
							weakCorCount++;
						}
					}
					else{
						interContainmentCount++;
						if(Math.abs(cor)>STRENGTH_THRESHOLD){
							strongInterContainmentCount++;
							strongCorCount++;
						}
						if(Math.abs(cor)<WEAK_THRESHOLD){
							weakInterContainmentCount++;
							weakCorCount++;
						}
					}
						
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				correlationMatrix[i][j] = cor;
//				System.out.println(""+files.get(i).getName()+" & "+files.get(j).getName()+ " = "+cor);
			}
		}
		System.out.println(corCount +" total correlations");
		
		STRONG_DEPENDENCIES = strongCorCount;
		WEAK_DEPENDENCIES = weakCorCount;
		INTRA_CONTA_DEPEND = intraContainmentCount;
		STRONG_INTRA_CONTA_DEPEND = strongIntraContainmentCount;
		WEAK_INTRA_CONTA_DEPEND = weakIntraContainmentCount;
		INTER_CONTA_DEPEND = interContainmentCount;
		WEAK_INTER_CONTA_DEP = weakInterContainmentCount;
		STRONG_INTER_CONTA_DEP = strongInterContainmentCount;
		
		return correlationMatrix;
	}

	private static boolean[][] doPairWiseContainmentsCheck2(Map<String, String> namesMap) {
		
		boolean[][] containmentsMatrix = new boolean[namesMap.size()][namesMap.size()];
		int containments = 0;
		List<String> filenames = new ArrayList<String>(namesMap.values());
		for (int i = 0; i < containmentsMatrix.length; i++) {				
			for(int j=i+1; j<containmentsMatrix[0].length; j++){
				File f1 = new File(filenames.get(i));
				File f2 = new File(filenames.get(j));
				boolean c = sameContainment(f1, f2);				
				containmentsMatrix[i][j] = c;
				if(c){
					containments++;
//						System.out.println("Containment "+f1+ " and "+f2);
				}
			}
		}
//			System.out.println(containments+ " Containments");
		CONTAINMENTS = containments;
		return containmentsMatrix;

	}

	/**
	 * @param path
	 * @param namesMap
	 * @return 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static Map<String, String> loadNamesMap(String path)
			throws FileNotFoundException, IOException {
		Map<String, String> nm = new TreeMap<String, String>();
		CSVReader csvReader = new CSVReader(new FileReader(path), '\t');
		csvReader.readNext();//skip header
		List<String[]> names = csvReader.readAll();
		csvReader.close();
		
		//load map from names file
		for (String[] strings : names) {
			nm.put(strings[0], strings[1]);
		}
		
		return nm;
	}

	private static double computeAverageProcessLength(Map<File, FileStory> map) {
		Set<File> files = map.keySet();
		DescriptiveStatistics ds = new DescriptiveStatistics();
		for (File f : files) {
			FileStory fs = map.get(f);
			List<FileStoryRecord> fileStoryRecords = fs.story;
			for (FileStoryRecord fileStoryRecord : fileStoryRecords) {
				String[] comments = fileStoryRecord.comments.split("§");
				if(comments.length>0 && !comments[0].isEmpty())
					ds.addValue(comments.length);
			}
		}
		return ds.getMean();
	}

	private static double computeUsersPerFile(Map<File, FileStory> map) {
		Set<File> files = map.keySet();
		DescriptiveStatistics ds = new DescriptiveStatistics();
		for (File f : files) {
			FileStory fs = map.get(f);
			List<FileStoryRecord> fileStoryRecords = fs.story;
			for (FileStoryRecord fileStoryRecord : fileStoryRecords) {
				if(fileStoryRecord.getUsers().length>0)
					ds.addValue(fileStoryRecord.getUsers().length);
			}
		}
		return ds.getMean();
	}
	
	private static void printContainmentsMatrix2(boolean[][] containmentsMatrix, Map<String, String> map,
			String filename) {
		List<String> files = new ArrayList<String>(map.values());
		String[] header = new String[files.size()+1];
		
//		for (int i = 0; i < correlationMatrix.length; i++) {
//			for (int j = 0; j < correlationMatrix.length; j++) {
//				System.out.print(correlationMatrix[i][j]+"\t");
//			}
//			System.out.println();
//		}
		
		DescriptiveStatistics ds = new DescriptiveStatistics();
		
		try {
			File file = new File(filename);
			File absolute = file.getAbsoluteFile();
			absolute.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(absolute, true);
			
			CSVWriter csvWriter = new CSVWriter(writer);
			header[0] = "Containments";
			for(int i = 1; i<header.length;i++){
				header[i] = files.get(i-1);
			}
			csvWriter.writeNext(header);
			
			List<String[]> csvRows = new ArrayList<String[]>();
			
			for(int i = 0; i<containmentsMatrix.length; i++){
				String[] row = new String[containmentsMatrix[i].length+1];
				row[0] = files.get(i);
				ds.addValue(Paths.get(row[0]).getNameCount());
				for(int j=0;j<containmentsMatrix[i].length; j++){
					boolean val = containmentsMatrix[i][j];
					row[j+1] = val? "1": "0";
				}
				csvRows.add(row);
			}
			csvWriter.writeAll(csvRows);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(ds.getMax() +" Tree depth");
//		System.out.println(ds.getMean() +" Mean tree depth");
		
		MAX_TREE_DEPTH = ds.getMax();
		AVG_TREE_DEPTH = ds.getMean();
	}

	private static void printContainmentsMatrix(boolean[][] containmentsMatrix, Map<File, FileStory> map,
			String filename) {
		List<File> files = new ArrayList<File>(map.keySet());
		String[] header = new String[files.size()+1];
		
//		for (int i = 0; i < correlationMatrix.length; i++) {
//			for (int j = 0; j < correlationMatrix.length; j++) {
//				System.out.print(correlationMatrix[i][j]+"\t");
//			}
//			System.out.println();
//		}
		
		DescriptiveStatistics ds = new DescriptiveStatistics();
		
		try {
			File file = new File(filename);
			File absolute = file.getAbsoluteFile();
			absolute.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(absolute, true);
			
			CSVWriter csvWriter = new CSVWriter(writer);
			header[0] = "Containments";
			for(int i = 1; i<header.length;i++){
				header[i] = files.get(i-1).getName().replaceAll("§", "/");
			}
			csvWriter.writeNext(header);
			
			List<String[]> csvRows = new ArrayList<String[]>();
			
			for(int i = 0; i<containmentsMatrix.length; i++){
				String[] row = new String[containmentsMatrix[i].length+1];
				row[0] = files.get(i).getName().replaceAll("§", "/");
				ds.addValue(Paths.get(row[0]).getNameCount());
				for(int j=0;j<containmentsMatrix[i].length; j++){
					boolean val = containmentsMatrix[i][j];
					row[j+1] = val? "1": "0";
				}
				csvRows.add(row);
			}
			csvWriter.writeAll(csvRows);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(ds.getMax() +" Tree depth");
//		System.out.println(ds.getMean() +" Mean tree depth");
		
		MAX_TREE_DEPTH = ds.getMax();
		AVG_TREE_DEPTH = ds.getMean();
	}

	private static boolean[][] doPairWiseContainmentsCheck(Map<File, FileStory> map ) {
		List<File> files = new ArrayList<File>(map.keySet());
//		String[] header = new String[files.size()+1];
		boolean[][] containmentsMatrix = new boolean[files.size()][files.size()];
		int containments = 0;
		for(int i=0; i<files.size(); i++){
			for(int j=i+1; j<files.size(); j++){
				File f1 = new File(files.get(i).getName().replaceAll("§", "/"));
				File f2 = new File(files.get(j).getName().replaceAll("§", "/"));
				boolean c = sameContainment(f1, f2);				
				containmentsMatrix[i][j] = c;
				if(c){
					containments++;
//					System.out.println("Containment "+f1+ " and "+f2);
				}
			}
		}
//		System.out.println(containments+ " Containments");
		CONTAINMENTS = containments;
		return containmentsMatrix;
	}

	private static boolean sameContainment(File file, File file2) {
		if(file.getParent() == null){
			if(file2.getParent() == null)
				return true;
		}
		else
			return file.getParent().equals(file2.getParent());
		
		return false;
	}

	/**
	 * 
	 * @param map
	 * @param field:  case 1:
				values[i++]=fileStoryRecord.getTotalLinesAdded();
				break;
			case 2:				
				values[i++]=fileStoryRecord.getTotalLinesRemoved();
				break;
			case 3:
				values[i++]=fileStoryRecord.getTotalChangeInTheDay();
				break;
			case 4:
				values[i++]=fileStoryRecord.getLinesUntilThisDay();
				break;
			case 5:
				values[i++]=fileStoryRecord.getTotalDiffInTheDay();
				break;
	 */
	private static double[][] doPairWiseCorrelations(Map<File, FileStory> map, int field ) {
		List<File> files = new ArrayList<File>(map.keySet());
//		String[] header = new String[files.size()+1];
		double[][] correlationMatrix = new double[files.size()][files.size()];
		int strongCorCount = 0, corCount=0;
		int interContainmentCount = 0, weakInterContainmentCount = 0, strongInterContainmentCount = 0;
		int intraContainmentCount = 0, weakIntraContainmentCount = 0, strongIntraContainmentCount = 0;
		int weakCorCount = 0;
		for(int i=0; i<files.size(); i++){
			for(int j=i; j<files.size(); j++){
				FileStory fs1 = map.get(files.get(i));
				FileStory fs2 = map.get(files.get(j));
				if(fs1.story.size()!=fs2.story.size()){
					System.err.println("Size of stories not matching! "+files.get(i)+" has "+fs1.story.size()+ ""
							+ " and "+files.get(j)+ " has "+fs2.story.size());
					continue;
				}
				double cor = 0;
				try {
					File f1 = new File(files.get(i).getName().replaceAll("§", "/"));
					File f2 = new File(files.get(j).getName().replaceAll("§", "/"));
					cor = correlation(fs1, fs2, field);
					corCount++;
					
					boolean sameContainment = sameContainment(f1, f2);
					
					if(sameContainment){
						intraContainmentCount++;
						if(Math.abs(cor)>STRENGTH_THRESHOLD){
							strongIntraContainmentCount++;
							strongCorCount++;
						}
						if(Math.abs(cor)<WEAK_THRESHOLD){
							weakIntraContainmentCount++;
							weakCorCount++;
						}
					}
					else{
						interContainmentCount++;
						if(Math.abs(cor)>STRENGTH_THRESHOLD){
							strongInterContainmentCount++;
							strongCorCount++;
						}
						if(Math.abs(cor)<WEAK_THRESHOLD){
							weakInterContainmentCount++;
							weakCorCount++;
						}
					}
						
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				correlationMatrix[i][j] = cor;
//				System.out.println(""+files.get(i).getName()+" & "+files.get(j).getName()+ " = "+cor);
			}
		}
		System.out.println(corCount +" total correlations");
		
//		System.out.println(strongCorCount + " strong correlations.");
//		System.out.println(weakCorCount + " weak correlations.");
//		System.out.println(intraContainmentCount +" total intra-containment correlations");
//		System.out.println(interContainmentCount +" total inter-containment correlations");
//		System.out.println(strongInterContainmentCount + " strong inter-containment correlations.");
		
		STRONG_DEPENDENCIES = strongCorCount;
		WEAK_DEPENDENCIES = weakCorCount;
		INTRA_CONTA_DEPEND = intraContainmentCount;
		STRONG_INTRA_CONTA_DEPEND = strongIntraContainmentCount;
		WEAK_INTRA_CONTA_DEPEND = weakIntraContainmentCount;
		INTER_CONTA_DEPEND = interContainmentCount;
		WEAK_INTER_CONTA_DEP = weakInterContainmentCount;
		STRONG_INTER_CONTA_DEP = strongInterContainmentCount;
		
		return correlationMatrix;
	}
	
	public static void printCorrelationMatrix(double[][] correlationMatrix, Map<File, FileStory> map, String filename){
		
		List<File> files = new ArrayList<File>(map.keySet());
		String[] header = new String[files.size()+1];
		
		try {
			File file = new File(filename);
			File absolute = file.getAbsoluteFile();
			absolute.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(absolute, true);
			
			CSVWriter csvWriter = new CSVWriter(writer);
			header[0] = "Correlations";
			for(int i = 1; i<header.length;i++){
				header[i] = files.get(i-1).getName();
			}
			csvWriter.writeNext(header);
			
			List<String[]> csvRows = new ArrayList<String[]>();
			
			for(int i = 0; i<correlationMatrix.length; i++){
				String[] row = new String[correlationMatrix[i].length+1];
				row[0] = files.get(i).getName();
				for(int j=0;j<correlationMatrix[i].length; j++){
					double val = correlationMatrix[i][j];
					row[j+1] = (val!=0)? ""+correlationMatrix[i][j]: "";
					
				}
				csvRows.add(row);
			}
			csvWriter.writeAll(csvRows);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Double correlation(FileStory fileStory1, FileStory fileStory2, int field) throws FileNotFoundException {
		double[] fs1 = filterBy(fileStory1, field);
		double[] fs2 = filterBy(fileStory2,field);
//		boolean found = false;
		
//		if(fileStory1.filename.equals("js-ace-snippets-batchfile.js.csv") && 
//				(fileStory2.filename.equals("js-ace-ace.js.csv") || 
//						fileStory2.filename.equals("js-ace-snippets-diff.js.csv"))){
//			found = true;
//			OutputRedirect.toFile(DateTime.now()+"timesseries");
//			System.out.println(fileStory1.filename + "\t" + fileStory2.filename);
//			for (int i = 0; i<fs1.length; i++) {
//				System.out.println(fs1[i] + "\t"+ fs2[i]);				
//			}
//		}
				
		PearsonsCorrelation p = new PearsonsCorrelation();
//		if(found)
//			System.out.println("Correlation = "+p.correlation(fs1, fs2));
//		
//		if(!found){
//			OutputRedirect.toConsole();
//		}
		return new Double(p.correlation(fs1, fs2));
	}

	

	private static double[] filterBy(FileStory fileStory1, int field) {
		
		double[] values = new double[fileStory1.story.size()];
		List<FileStoryRecord> fileStoryRecords = fileStory1.story;
		int i = 0;
		for (FileStoryRecord fileStoryRecord : fileStoryRecords) {
			
			switch (field) {
			case 1:
				values[i++]=fileStoryRecord.getTotalLinesAdded();
				break;
			case 2:				
				values[i++]=fileStoryRecord.getTotalLinesRemoved();
				break;
			case 3:
				values[i++]=fileStoryRecord.getTotalChangeInTheDay();
				break;
			case 4:
				values[i++]=fileStoryRecord.getLinesUntilThisDay();
				break;
			case 5:
				values[i++]=fileStoryRecord.getTotalDiffInTheDay();
				break;

			default:
				break;
			}

		}
		return values;
	}

	public static List<File> listFilesForFolder(File folder) {
		List<File> allFiles = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				allFiles.addAll(listFilesForFolder(fileEntry));
			} else {
				//	            System.out.println(fileEntry.getName());
				allFiles.add(fileEntry);
			}
		}
		return allFiles;
	}

	public static List<FileStoryRecord> getStoryDataForFile(File file){
		List<FileStoryRecord> storyRecords =  new ArrayList<FileStoryRecord>();
//		System.out.println("Got file "+file);
		
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file), '\t');
			reader.readNext();
			String[] line;
			while ((line = reader.readNext()) != null) {
				String comment = line[0];
				String dateString = line[1];
				String tlA = line[2];
				String tlR = line[3];
				String tcD = line[4];
				String tdD = line[5];
				String lUT = line[6];
				String usersString = line[7];

				//convert
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
				Date date = df.parse(dateString);
				int totalLinesAdded = Integer.parseInt(tlA);
				int totalLinesRemoved = Integer.parseInt(tlR);
				int totalChangeInTheDay = Integer.parseInt(tcD);
				int totalDiffInTheDay = Integer.parseInt(tdD);
				int linesUntilThisDay = Integer.parseInt(lUT);
				String[] users = usersString.split(" . ");

				FileStoryRecord fsr = new FileStoryRecord(comment, date, totalLinesAdded, totalLinesRemoved, totalChangeInTheDay, totalDiffInTheDay, linesUntilThisDay, users);

				storyRecords.add(fsr);
			}
		} catch (IOException | ParseException | ArrayIndexOutOfBoundsException e) {
			System.err.println("Problem when reading file "+file);
			e.printStackTrace();
		}

		return storyRecords;
	}

	public static Map<File, FileStory> getAllFileStories(List<File> allFiles){
		Map<File, FileStory> stories = new TreeMap<File,FileStory>();

		for (File file : allFiles) {
//			if(hasEmptyStory(file))
//				continue;
			FileStory fStory = new FileStory(file.getName(), getStoryDataForFile(file));
			stories.put(file,fStory);
		}

		return stories;
	}

//	private static boolean hasEmptyStory(File file) {
//		return false;
//	}
	
}
