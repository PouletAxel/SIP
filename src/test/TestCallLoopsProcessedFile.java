package test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import multiProcesing.ProcessDetectLoops;

import utils.SIPObject;

/**
 * Test of calling loops on processed files
 * 
 * @author Axel Poulet
 *
 */
public class TestCallLoopsProcessedFile {

	/**	
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		//String input = "/home/plop/Bureau/DataSetImageHiC/GM12878/subsample/GM12878_100mil/";
		//String output= "/home/plop/Bureau/DataSetImageHiC/GM12878/subsample/100mil_test/";
		//String output= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1_test";
		//String input= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1_test";
		///home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1_test
		String input = "/home/plop/Bureau/kc_1kb";
		String output= "/home/plop/Bureau/kc_1kbTestBis";
		//String input = "/home/plop/Bureau/DataSetImageHiC/GM12878/subsample/GM12878_full/";
		//String input = "/home/plop/Bureau/DataSetImageHiC/HiChip/ring1b/test/";//"/home/plop/Bureau/DataSetImageHiC/GM12878/test";
		//String output = "/home/plop/Bureau/DataSetImageHiC/HiChip/ring1b/testTer/";//"/home/plop/Bureau/DataSetImageHiC/GM12878/test";
		int matrixSize = 500;
		int resolution = 1000;
		int diagSize = 20;
		double gauss = 1.5;
		int thresholdMax = 6000;// 2800;//1800
		int nbZero = 10;//6;
		double min = 2;//1.5;
		double max = 2;//1.5;
		double saturatedPixel = 0.01;//0.005;
		boolean keepTif = false;
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		//factor.add(2);
		//factor.add(5);
		
		HashMap<String,Integer> chrsize = readChrSizeFile("/home/plop/Bureau/armsizes.txt");
		SIPObject sip = new SIPObject(input,output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.05,true,false,false);
		sip.setIsGui(false);
		System.out.println("Processed Data\n");
		System.out.println("input "+input+"\n"
			+ "output "+output+"\n"
			+ "gauss "+gauss+"\n"
			+ "min "+min+"\n"
			+ "max "+max+"\n"
			+ "matrix size "+matrixSize+"\n"
			+ "diag size "+diagSize+"\n"
			+ "resolution "+resolution+"\n"
			+ "saturated pixel "+saturatedPixel+"\n"
			+ "threshold "+thresholdMax+"\n"
			+ "isProcessed "+sip.isProcessed()+"\n");		
		ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
		processDetectloops.go(sip, 3);
		
		if(keepTif == false){
			for(int i = 0; i< sip._tifList.size();++i)
				sip._tifList.get(i).delete();
		}
	
		System.out.println("End");
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
