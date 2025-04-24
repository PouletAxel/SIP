package plop.test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import plop.multiProcesing.ProcessHicDumpData;
import plop.process.MultiResProcess;
import plop.utils.SIPObject;



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
	static String _logError = "";
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, InterruptedException {
		String output = "/home/plop/Desktop/testSip";
		//output= "/home/plop/Bureau/SIPpaper/chr1/testNewNew";

		String input = "/home/plop/Desktop/GSE255264_MegaMap_q30_hg38_juicer1.hic";
		//input =  "/home/plop/Bureau/SIPpaper/hicFileIer_0.hic"; //"https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined_30.hic"; //";
		//String output= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1";
		//String input= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1/NT_H3K4me1_2Reps.cis18797450.allValidPairs.hic";
		//HumanGenomeHg19/chr2.size");
		//readChrSizeFile("/home/plop/Documents/Genome/HumanGenomeHg19/hg19_withoutChr.sizes");
		//chrsize = readChrSizeFile("/home/plop/Documents/Genome/mammals/HumanGenomeHg19/chr1.size");
		String fileChr = "/home/plop/Desktop/hg38.chr";
		HashMap<String, Integer> chrsize = readChrSizeFile(fileChr);
		String juiceBoxTools = "/home/plop/Tools/juicer_tools_1.13.01.jar";
		int matrixSize = 2000;
		int resolution = 500;
		int diagSize = 5;
		double gauss = 1.5;
		double min = 2;
		double max = 2;
		int nbZero = 6;
		int thresholdMax = 2800;
		String juiceBoXNormalisation = "KR";
		double saturatedPixel = 0.01;

		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		//factor.add(2);
		//factor.add(5);
		boolean keepTif = true;
		int cpu = 1;

		System.out.println("input " + input + "\n"
				+ "output " + output + "\n"
				+ "juiceBox " + juiceBoxTools + "\n"
				+ "norm " + juiceBoXNormalisation + "\n"
				+ "gauss " + gauss + "\n"
				+ "min " + min + "\n"
				+ "max " + max + "\n"
				+ "matrix size " + matrixSize + "\n"
				+ "diag size " + diagSize + "\n"
				+ "resolution " + resolution + "\n"
				+ "saturated pixel " + saturatedPixel + "\n"
				+ "threshold " + thresholdMax + "\n");

		File file = new File(output);
		if (file.exists() == false) {
			file.mkdir();
		}

		SIPObject sip = new SIPObject(output, chrsize, gauss, min, max,
				resolution, saturatedPixel, thresholdMax, diagSize,
				matrixSize, nbZero, factor, 0.01, keepTif, false);
		sip.setIsGui(false);
		ProcessHicDumpData processDumpData = new ProcessHicDumpData();
		processDumpData.go(input, sip, chrsize, juiceBoxTools, juiceBoXNormalisation, cpu);

		//MultiResProcess multi = new MultiResProcess(sip, cpu, keepTif,fileChr);
		MultiResProcess multi = new MultiResProcess(sip, cpu, false, fileChr);
		multi.run();
		//String cooler = "/home/plop/anaconda3/bin/cooler";
		//String cooltools = "/home/plop/anaconda3/bin/cooltools";


		System.out.println("End ");
	}

		/**
		 * 
		 * @param chrSizeFile
		 * @throws IOException
		 */
	@SuppressWarnings("unused")
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
	
	public static boolean testTools(String pathTools, int first, int second, int third) {
		Runtime runtime = Runtime.getRuntime();
		String cmd = pathTools+" --version";
		//System.out.println(cmd);
		Process process;
		try {
			process = runtime.exec(cmd);
	
		new ReturnFlux(process.getInputStream()).start();
		new ReturnFlux(process.getErrorStream()).start();
		process.waitFor();
		
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] tline = _logError.split(" ");
		System.out.println(_logError);
		if(tline.length > 0){
			//tline[tline.length-1].replaceAll(, "")
			
			tline = tline[tline.length-1].split("\\.");
			System.out.println("aa"+tline[tline.length-3]+"aa");
			System.out.println("aa"+tline[tline.length-2]+"aa");
			System.out.println("aa"+tline[tline.length-1]+"aa");
			tline[tline.length-1] = tline[tline.length-1].replace("\n", "");
			System.out.println("aa"+tline[tline.length-1]+"aa");
			/*int a = Integer.parseInt(tline[tline.length-3]);
			int b = Integer.parseInt(tline[tline.length-2]);
			int c = Integer.parseInt(tline[tline.length-1]);
			if(a >= first && b >= second && c >= third)
				return true;
			else
				return false;
		}else*/
		}
			return false;
	}
	
	public static class ReturnFlux extends Thread {  

		/**  Flux to redirect  */
		private InputStream _flux;

		/**
		 * <b>Constructor of ReturnFlux</b>
		 * @param flux
		 *  flux to redirect
		 */
		public ReturnFlux(InputStream flux){this._flux = flux; }
		
		/**
		 * 
		 */
		public void run(){
			try {    
				InputStreamReader reader = new InputStreamReader(this._flux);
				BufferedReader br = new BufferedReader(reader);
				String line=null;
				while ( (line = br.readLine()) != null) {
					if(line.contains("WARN")== false) _logError = _logError+line+"\n";
				}
			}
			catch (IOException ioe){
				ioe.printStackTrace();
			}
		}		
	}
	
}
