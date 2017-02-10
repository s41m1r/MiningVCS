package at.ac.wu.infobiz.projectmining.visualization;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class TimeSeriesCorrelation {

	public static void main(String[] args) {
		String folder = null;
		if(args.length == 1)
			folder = args[0];
		if(folder == null)
			System.exit(-1);

		System.out.println("Reading stories from folder: "+folder);

		List<File> allFiles = listFilesForFolder(new File(folder));
		Map<File, FileStory> map = getAllFileStories(allFiles);
//		System.out.println();
		
		for (File file : map.keySet()) {
			Collections.sort(map.get(file).story);
		}
		
		TimeSeriesTable tst = new TimeSeriesTable(map);
		map = tst.getExpandedFileStoryMap();
		System.out.println(map);

		Double[][] correlationMatrix = doPairWiseCorrelations(map, 3);
		
		printCorrelationMatrix(correlationMatrix, map, "corremat");
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
	private static Double[][] doPairWiseCorrelations(Map<File, FileStory> map, int field ) {
		List<File> files = new ArrayList<File>(map.keySet());
		String[] header = new String[files.size()+1];
		Double[][] correlationMatrix = new Double[files.size()][files.size()];
		for(int i=0; i<files.size(); i++){
			for(int j=i+1; j<files.size(); j++){
				FileStory fs1 = map.get(files.get(i));
				FileStory fs2 = map.get(files.get(j));
				if(fs1.story.size()!=fs2.story.size()){
					System.err.println("Size of stories not matching! "+files.get(i)+" has "+fs1.story.size()+ ""
							+ " and "+files.get(j)+ " has "+fs2.story.size());
					continue;
				}
				double cor = correlation(fs1, fs2, field);
				correlationMatrix[i][j] = cor;
				System.out.println(""+files.get(i).getName()+" & "+files.get(j).getName()+ " = "+cor);
			}
		}
		return correlationMatrix;
	}
	
	public static void printCorrelationMatrix(Double[][] correlationMatrix, Map<File, FileStory> map, String filename){
		
		List<File> files = new ArrayList<File>(map.keySet());
		String[] header = new String[files.size()+1];
		
		
		try {
			CSVWriter csvWriter = new CSVWriter(new FileWriter(filename,true));
			header[0] = "";
			for(int i = 1; i<header.length;i++){
				header[i] = files.get(i-1).getName();
			}
			csvWriter.writeNext(header);
			
			List<String[]> csvRows = new ArrayList<String[]>();
			
			for(int i = 0; i<correlationMatrix.length; i++){
				String[] row = new String[correlationMatrix[i].length+1];
				row[0] = files.get(0).getName();
				for(int j=0;j<correlationMatrix[i].length-2; j++)
					row[j+1] = correlationMatrix[i][j].toString();
			
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

	private static Double correlation(FileStory fileStory1, FileStory fileStory2, int field) {
		double[] fs1 = filterBy(fileStory1, field);
		double[] fs2 = filterBy(fileStory2,field);
		
		PearsonsCorrelation p = new PearsonsCorrelation();
		
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
			reader = new CSVReader(new FileReader(file), '\t', '"', 1);
			String[] line;
			while ((line = reader.readNext()) != null) {
				//                System.out.println("Country [id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
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
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		return storyRecords;
	}

	public static Map<File, FileStory> getAllFileStories(List<File> allFiles){
		Map<File, FileStory> stories = new HashMap<File,FileStory>();

		for (File file : allFiles) {
			FileStory fStory = new FileStory(file.getName(), getStoryDataForFile(file));
			stories.put(file,fStory);
		}

		return stories;
	}
	
}
