package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import multiProcesing.ProcessDetectLoops;
import multiProcesing.ProcessDumpCooler;
import sip.SIPInter;
import utils.MultiResProcess;
import sip.SIPIntra;
@SuppressWarnings("unused")
public class TestCoolFormat {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String chr = "chr21";
		//String input = "/home/plop/Desktop/SIP/testCooler/GM12878_4DNFIXP4QG5B.mcool";
		String input = "/home/plop/Desktop/SIP/hicData/4DNFIFLDVASC_untreated.mcool";
		String output = "/home/plop/Desktop/CoolTest";
		//String expectedFile = "/home/plop/Desktop/chr21Cooler/expected.txt";
		String chrSizeFile = "/home/plop/Desktop/SIP/hg38_small.size";
		String cooler = "/home/plop/anaconda3/bin/cooler";
		String cooltools = "/home/plop/anaconda3/bin/cooltools";

		int resolution = 100000;
		int matrixSize = 500;
		//CoolerExpected expected = new CoolerExpected(input,  resolution, matrixSize);
		//expected.dumpExpected(expectedFile);
		//CoolerExpected expected = new CoolerExpected(expectedFile, matrixSize);
		//expected.parseExpectedFile();
		//ArrayList<Double> plop = expected.getExpected(chr);
		//CoolerDumpIntra cooler = new CoolerDumpIntra(input, resolution, plop);
		//run(resolution,matrixSize,matrixSize/2,chr,output, cooler, 46709983);

		int diagSize = 3;
		double gauss = 1.5;
		double min = 2;
		double max = 2;
		int nbZero = 8;
		int thresholdMax = 2800;
		double saturatedPixel = 0.01;
		int factor = 1;
		double fdr =0.025;
		boolean keepTif = true;
		int cpu = 1;
				
		//SIPIntra sip = new SIPIntra(output, chrSizeFile, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.03,false,keepTif,cpu);
		SIPInter sip = new SIPInter(output, chrSizeFile, gauss, resolution, thresholdMax, matrixSize, nbZero, fdr , true,2);
		sip.setIsGui(false);
		sip.setIsCooler(true);
		sip.setIsProcessed(false);


		ProcessDumpCooler processDumpData = new ProcessDumpCooler();
		processDumpData.go(cooler, sip, input);
		System.out.println("########### End of the dump step\n");
		System.out.println("########### Start loop detection\n");
		String loopFileRes = sip.getOutputDir()+"finalLoops.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(loopFileRes)));
		ProcessDetectLoops detectLoops = new ProcessDetectLoops();

		System.out.println("########### Starting loop detection");
		detectLoops.go(sip, loopFileRes);
		System.out.println("########### !!!!!!! end loops detection");
		
		System.out.println("end");
		
	}
	

}
