package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import multiProcesing.ProcessDetectLoops;
import multiProcesing.ProcessDumpData;
import utils.SIPObject;

/**
 * Test loops calling on Hic file
 * 
 * @author Axel Poulet
 *
 */
public class TestCallLoopsHicFile{
	/**
	 *
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		String output= "/home/plop/Desktop/test";
		//output= "/home/plop/Bureau/SIPpaper/chr1/testNewNew";
		
		String input = "/home/plop/Desktop/Z4_HiChIP_rep12.hic";
		//input =  "/home/plop/Bureau/SIPpaper/hicFileIer_0.hic"; //"https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined_30.hic"; //";
		//String output= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1";
		//String input= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1/NT_H3K4me1_2Reps.cis18797450.allValidPairs.hic";
		HashMap<String,Integer> chrsize = readChrSizeFile("/home/plop/Desktop/SIP/arm_Y.txt");//HumanGenomeHg19/chr2.size");
		//readChrSizeFile("/home/plop/Documents/Genome/HumanGenomeHg19/hg19_withoutChr.sizes");
		//chrsize = readChrSizeFile("/home/plop/Documents/Genome/mammals/HumanGenomeHg19/chr1.size");
		String juiceBoxTools = "/home/plop/Tools/juicer_tools_1.13.02.jar";
		int matrixSize = 5000;
		int resolution = 1000;
		int diagSize = 5;
		double gauss = 1.5;
		double min = 2;
		double max = 2;
		int nbZero = 6;
		int thresholdMax = 2800;
		String juiceBoXNormalisation = "NONE";
		double saturatedPixel = 0.01;
		
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		boolean keepTif = true;
		//factor.add(2);
		//factor.add(5);
	
		System.out.println("input "+input+"\n"
				+ "output "+output+"\n"
				+ "juiceBox "+juiceBoxTools+"\n"
				+ "norm "+ juiceBoXNormalisation+"\n"
				+ "gauss "+gauss+"\n"
				+ "min "+min+"\n"
				+ "max "+max+"\n"
				+ "matrix size "+matrixSize+"\n"
				+ "diag size "+diagSize+"\n"
				+ "resolution "+resolution+"\n"
				+ "saturated pixel "+saturatedPixel+"\n"
				+ "threshold "+thresholdMax+"\n");
			
			File file = new File(output);
			if (file.exists()==false){file.mkdir();}
			
			SIPObject sip = new SIPObject(output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.01,false,false,false);
			sip.setIsGui(false);
			ProcessDumpData processDumpData = new ProcessDumpData();
			processDumpData.go(input, sip, chrsize, juiceBoxTools, juiceBoXNormalisation,1);
			
			ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
			processDetectloops.go(sip, 1,true);
			
			System.out.println("End");
		}
		
		/**
		 * 
		 * @param chrSizeFile
		 * @throws IOException
		 */
	private static HashMap<String, Integer> readChrSizeFile( String chrSizeFile) throws IOException{
		HashMap<String,Integer> chrSize =  new HashMap<String,Integer>();
		BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String chr = parts[0]; 
			int size = Integer.parseInt(parts[1]);
			chrSize.put(chr, size);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		return  chrSize;
	} 
}
