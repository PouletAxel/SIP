package plop.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import plop.process.MultiResProcess;
import plop.utils.SIPObject;
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
		HashMap<String,Integer> chrsize = readChrSizeFile(fileChr);
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
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		factor.add(2);
		factor.add(5);
		boolean keepTif = false;
		int cpu = 1;
				
		SIPObject sip = new SIPObject(input,output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.03,keepTif,false);
		sip.setIsGui(false);
		sip.setIsCooler(true);
		sip.setIsProcessed(true);
		

		//ProcessCoolerDumpData processDumpData = new ProcessCoolerDumpData();
	//	 go(String coolTools, String cooler, SIPObject sip, String coolFile, HashMap<String,Integer> chrSize,int nbCPU)
		//processDumpData.go(cooltools, cooler, sip, input, chrsize,2);
		MultiResProcess multi = new MultiResProcess(sip, cpu, keepTif,fileChr);
		multi.run();
		
		System.out.println("end");
		
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
