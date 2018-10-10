package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import core.HicFileProcessing;
import utils.WholeGenomeAnalysis;

public class TestCallLoopsHicFile{
	/**
	 * Main function to run all the process, can be run with gui or in command line.
	 * With command line with 1 or less than 5 parameter => run only the help
	 * With zero parameter only java -jar noname.jar  => gui
	 * With more than 5 paramter => command line mode
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		String output= "/home/plop/Bureau/DataSetImageHiC/Mouse_Yoonhee_Bri_Project/test";
		String input = "/home/plop/Bureau/DataSetImageHiC/Mouse_Yoonhee_Bri_Project/BPArep1rep2_map30-25kbnorm_nochr.hic"; //"https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined_30.hic"; //";
		//String output= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1";
		//String input= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1/NT_H3K4me1_2Reps.cis18797450.allValidPairs.hic";
		HashMap<String,Integer> chrsize = readChrSizeFile("/home/plop/Documents/Genome/mm9/mm9_sizes_noY_1.txt");//HumanGenomeHg19/chr2.size");
				//readChrSizeFile("/home/plop/Documents/Genome/HumanGenomeHg19/hg19_withoutChr.sizes");
		///home/plop/Documents/Genome/HumanGenomeHg19/hg19_withoutChr.sizes
		String juiceBoxTools = "/home/plop/Tools/juicer_tools.1.8.9_jcuda.0.8.jar";
		int matrixSize = 1000;
		int resolution = 25000;
		int diagSize = 3;
		double gauss = 2;
		double min = 3;
		double max = 3;
		int nbZero = 3;
		int thresholdMax = 700;
		String juiceBoXNormalisation = "KR";
		double saturatedPixel = 0.005;
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
			
			WholeGenomeAnalysis wga = new WholeGenomeAnalysis(output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor);
			HicFileProcessing hfp =  new HicFileProcessing(input, wga, chrsize, juiceBoxTools, juiceBoXNormalisation);
			hfp.run();
				
			if(keepTif == false){
				for(int i = 0; i< wga.m_tifList.size();++i)
					wga.m_tifList.get(i).delete();
			}
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
