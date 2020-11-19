package test;

import java.io.IOException;
import java.util.ArrayList;

import utils.MultiResProcess;
import sip.SIPIntra;
@SuppressWarnings("unused")
public class TestCoolFormat {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String chr = "chr21";
		//String input = "/home/plop/Desktop/SIP/testCooler/GM12878_4DNFIXP4QG5B.mcool";
		String input = "/home/plop/Desktop/CoolTest21";
		String output = "/home/plop/Desktop/CoolTest21Bis";
		String expectedFile = "/home/plop/Desktop/chr21Cooler/expected.txt";
		String fileChr = "/home/plop/Desktop/w_hg19.sizes";
		String cooler = "/home/plop/anaconda3/bin/cooler";
		String cooltools = "/home/plop/anaconda3/bin/cooltools";

		int resolution = 5000;
		int matrixSize = 2000;
		//CoolerExpected expected = new CoolerExpected(input,  resolution, matrixSize);
		//expected.dumpExpected(expectedFile);
		//CoolerExpected expected = new CoolerExpected(expectedFile, matrixSize);
		//expected.parseExpectedFile();
		//ArrayList<Double> plop = expected.getExpected(chr);
		//CoolerDumpData cooler = new CoolerDumpData(input, resolution, plop);
		//run(resolution,matrixSize,matrixSize/2,chr,output, cooler, 46709983);

		int diagSize = 5;
		double gauss = 1.5;
		double min = 2;
		double max = 2;
		int nbZero = 6;
		int thresholdMax = 2800;
		double saturatedPixel = 0.01;
		int factor = 4;

		boolean keepTif = false;
		int cpu = 1;
				
		SIPIntra sip = new SIPIntra(input,output, fileChr, gauss, min, max, resolution, saturatedPixel, thresholdMax,
				diagSize, matrixSize, nbZero,factor,0.03,false,keepTif,cpu);

		sip.setIsGui(false);
		sip.setIsCooler(true);
		sip.setIsProcessed(true);


		

		//ProcessCoolerDumpData processDumpData = new ProcessCoolerDumpData();
	//	 go(String coolTools, String cooler, SIPIntra sip, String coolFile, HashMap<String,Integer> chrSize,int nbCPU)
		//processDumpData.go(cooltools, cooler, sip, input, chrsize,2);
		MultiResProcess multi = new MultiResProcess(sip,fileChr);
		multi.run();
		
		System.out.println("end");
		
	}
	

}
