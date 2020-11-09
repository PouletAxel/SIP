package test;
import java.io.IOException;
import java.util.ArrayList;

import utils.MultiResProcess;
import sip.SIPIntra;

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
		String input = "/home/plop/Desktop/GM12878TestBis";
		String output= "/home/plop/Desktop/testChr";
		int matrixSize = 2000;
		int resolution = 5000;
		int diagSize = 5;
		double gauss = 1.5;
		int thresholdMax = 2800;// 2800;//1800
		int nbZero = 6;//6;
		double min = 2;//1.5;
		double max = 2;//1.5;
		double saturatedPixel = 0.01;//0.005;
		//boolean keepTif = false;
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		//factor.add(2);
		//factor.add(5);
		//SIP_HiC_v1.3.6.jar hic SIP/Kc_allcombined.hic SIP/armsizes.txt SIP/Droso/ ../Tools/juicer_tools_1.13.02.jar 
		String chrSizeFile = "/home/plop/Desktop/w_hg19.sizes";
		SIPIntra sip = new SIPIntra(input,output, chrSizeFile, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.01,true,false);
		sip.setIsGui(false);
		int cpu = 2;
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
		MultiResProcess multi = new MultiResProcess(sip, cpu, false,chrSizeFile);
		multi.run();
		//ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
		//processDetectloops.go(sip, 2,false);
		//Testpb plop = new Testpb(sip, true);
		//plop.run(2);
		System.out.println("End");
	}
			
}
