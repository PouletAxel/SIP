package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import utils.HiCExperimentAnalysis;
import utils.HicFileProcessing;

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
		String output= "/home/plop/Bureau/DataSetImageHiC/HiChip/ring1b/test";
		String input = "/home/plop/Bureau/DataSetImageHiC/HiChip/ring1b/rep1_allValidPairs.hic"; //"https://hicfiles.s3.amazonaws.com/hiseq/gm12878/in-situ/combined_30.hic"; //";
		//String output= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1";
		//String input= "/home/plop/Bureau/DataSetImageHiC/Hichip_H3k4me1/NT_H3K4me1_2Reps.cis18797450.allValidPairs.hic";
		HashMap<String,Integer> chrsize = readChrSizeFile("/home/plop/Documents/Genome/mammals/HumanGenomeHg19/chr2.size");//HumanGenomeHg19/chr2.size");
				//readChrSizeFile("/home/plop/Documents/Genome/HumanGenomeHg19/hg19_withoutChr.sizes");
		///home/plop/Documents/Genome/HumanGenomeHg19/hg19_withoutChr.sizes
		String juiceBoxTools = "/home/plop/Tools/juicer_tools.1.8.9_jcuda.0.8.jar";
		int matrixSize = 3000;
		int resolution = 10000;
		int diagSize = 5;
		double gauss = 1;
		double min = 1;
		double max = 1;
		int nbZero = 25;
		int thresholdMax = 1;
		String juiceBoXNormalisation = "NONE";
		double saturatedPixel = 0.5;
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		boolean keepTif = false;
		factor.add(2);
		factor.add(5);
		
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
			
			HiCExperimentAnalysis wga = new HiCExperimentAnalysis(output, chrsize, gauss, min, max, resolution, saturatedPixel, thresholdMax, diagSize, matrixSize, nbZero,factor,0.01);
			HicFileProcessing hfp =  new HicFileProcessing(input, wga, chrsize, juiceBoxTools, juiceBoXNormalisation);
			wga.setIsHichip(true);
			
			hfp.run(false);
				
			if(keepTif == false){
				for(int i = 0; i< wga._tifList.size();++i)
					wga._tifList.get(i).delete();
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
