package plop.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import plop.loops.Loop;

public class TestMergeLoops {

	
	/**	
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		ArrayList<String> listOfFile = new ArrayList<String> ();
		
		listOfFile.add("/home/plop/Desktop/chr11/5kbLoops.txt");
		listOfFile.add("/home/plop/Desktop/chr11/10kbLoops.txt");
		listOfFile.add("/home/plop/Desktop/chr11/25kbLoops.txt");
		mergedFile(listOfFile);
		System.out.println("End");
	}
	/**
	 * @throws IOException 
	 * 
	 */
	private static void mergedFile(ArrayList<String> listOfFile) throws IOException{
		//chromosome1	x1	x2	chromosome2	y1	y2	color	APScoreAvg	ProbabilityofEnrichment	RegAPScoreAvg	Avg_diffMaxNeihgboor_1	Avg_diffMaxNeihgboor_2	avg	std	value
		//22	33915000	33920000	22	34510000	34515000	0,0,0	5.228523	0.99861	2.336731	3.4115214	3.979847	3.546672	1.5443684	6.5791354
		//HashMap<String,Loop>  data = new HashMap<String,Loop>(); 
		HashMap<String,Integer> chrsize = readChrSizeFile("/home/plop/Desktop/w_hg19.sizes");
		Set<String> key = chrsize.keySet();
		ArrayList <HashMap<String,Loop>> data = new ArrayList <HashMap<String,Loop>>();
		for (int i = 0; i < key.size();++i) {
			HashMap<String,Loop> plop = new HashMap<String,Loop>();
			data.add(plop);
		}
		
		BufferedReader br = new BufferedReader(new FileReader(listOfFile.get(0)));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		String title = line;
		sb.append(System.lineSeparator());
		line = br.readLine();
		while (line != null){
			sb.append(line);
			Loop loop = new Loop(line);
			String chr = loop.getChr();
			int index = chrsize.get(chr);
			HashMap<String,Loop> plop = data.get(index);
			plop.put(line, loop);
			data.set(index, plop);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		
		for (int i = 1; i < listOfFile.size();++i) {
			br = new BufferedReader(new FileReader((listOfFile.get(i))));
			sb = new StringBuilder();
			line = br.readLine();
			sb.append(System.lineSeparator());
			line = br.readLine();
			while (line != null){
				Loop loop = new Loop(line);
				String chr = loop.getChr();
				int index = chrsize.get(chr);
				HashMap<String,Loop> plop = data.get(index);
				if(compareLoop(plop,loop)){
					plop.put(line,loop);
					data.set(index, plop);
				}
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		}	
		
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(new File("/home/plop/Desktop/chr11/finalLoops.txt")));
		writer.write(title+"\n");
		for (int i = 0; i < data.size();++i){
			HashMap<String,Loop> plop = data.get(i);
			Set<String> keyBis = plop.keySet();
			List<String> output = keyBis.stream().collect(Collectors.toList());

			for (int j = 0; j < output.size();++j){
				writer.write(output.get(j)+"\n");
			}	
		}
		writer.close();
	}
	
	
	
	private static boolean compareLoop(HashMap<String,Loop> plop, Loop a) {
		Set<String> key = plop.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String loopName = it.next();
			Loop loop = plop.get(loopName);
			int xEnd = loop.getCoordinates().get(1);
			int yEnd = loop.getCoordinates().get(3);
			
			
			int xtest = a.getCoordinates().get(0);
			int xtestEnd = a.getCoordinates().get(1);
			
			int res = xtestEnd-xtest;
			int ytest = a.getCoordinates().get(2);
			int ytestEnd = a.getCoordinates().get(3);
			xtest = xtest-res*2;
			xtestEnd = xtestEnd+res*2;
			ytest = ytest-res*2;
			ytestEnd = ytestEnd+res*2;
			//System.out.println(x+"\t"+xEnd+"\t"+ xtestEnd);
			if((xtest <= xEnd && xtestEnd >= xEnd) && (ytest <= yEnd && ytestEnd >= yEnd)){
				return false;
				
			}
		}		
		return true;
	}
	/**
	 * 	
	 * @param chrSizeFile
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String, Integer> readChrSizeFile( String chrSizeFile) throws IOException{
		HashMap<String,Integer> chrSize =  new HashMap<String,Integer>();
		BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		int i = 0;
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String chr = parts[0];
			chrSize.put(chr, i);
			sb.append(System.lineSeparator());
			line = br.readLine();
			++i;
		}
		br.close();
		return  chrSize;
	} 

}
