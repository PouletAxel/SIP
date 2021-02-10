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
		String input = "/home/plop/Desktop/Cool";
		String output= "/home/plop/Desktop/Bis";
		int matrixSize = 500;
		int resolution = 25000;
		int diagSize = 2;
		double gauss = 1.5;
		int thresholdMax = 2800;// 2800;//1800
		int nbZero = 8;//6;
		double min = 2;//1.5;
		double max = 2;//1.5;
		double saturatedPixel = 0.01;//0.005;
		//boolean keepTif = false;

		int factor = 1;
		//factor.add(2);
		//factor.add(5);
		//SIP_HiC_v1.3.6.jar hic SIP/Kc_allcombined.hic SIP/armsizes.txt SIP/Droso/ ../Tools/juicer_tools_1.13.02.jar 
		String chrSizeFile = "/home/plop/Desktop/SIP/hg38_small.size";
		SIPIntra sip = new SIPIntra(input,output, chrSizeFile, gauss, min, max, resolution, saturatedPixel,
				thresholdMax, diagSize, matrixSize, nbZero,factor,0.01,false,false,2);
		sip.setIsGui(false);
		sip.setIsProcessed(true);
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
		MultiResProcess multi = new MultiResProcess(sip, chrSizeFile);
		multi.run();
		//ProcessDetectLoops processDetectloops = new ProcessDetectLoops();
		//processDetectloops.go(sip, 2,false);
		//Testpb plop = new Testpb(sip, true);
		//plop.run(2);
		System.out.println("End");
	}
			
}
