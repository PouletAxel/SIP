package test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import multiProcesing.ProcessDetectLoops;
import process.SIPProcess;
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
		String input = "/home/plop/Desktop/SIP/Droso/";
		String output= "/home/plop/Desktop/SIP/DrosoTest/";
		int matrixSize = 2000;
		int resolution = 5000;
		int diagSize = 5;
		double gauss = 1.5;
		int thresholdMax = 2800;// 2800;//1800
		int nbZero = 6;//6;
		double min = 2;//1.5;
		double max = 2;//1.5;
		double saturatedPixel = 0.01;//0.005;
		boolean keepTif = false;
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		//factor.add(2);
		//factor.add(5);
		//SIP_HiC_v1.3.6.jar hic SIP/Kc_allcombined.hic SIP/armsizes.txt SIP/Droso/ ../Tools/juicer_tools_1.13.02.jar 
		HashMap<String,Integer> chrsize = readChrSizeFile("/home/plop/Desktop/SIP/armsizes.txt");
		SIPObject sip = new SIPObject(input,output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.01,true,false,false);
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
		System.out.println("ahhhhhhhhhhhhh\n");
		ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
		processDetectloops.go(sip, 2,true);
		//Testpb plop = new Testpb(sip, true);
		//plop.run(2);
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
