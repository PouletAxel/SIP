package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import utils.HiCExperimentAnalysis;

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
		String input = "/home/plop/Bureau/DataSetImageHiC/GM12878/test";
		String output= "/home/plop/Bureau/DataSetImageHiC/GM12878/test/plop";
		//String input = "/home/plop/Bureau/DataSetImageHiC/GM12878/subsample/GM12878_full/";
		//String input = "/home/plop/Bureau/DataSetImageHiC/HiChip/ring1b/test/";//"/home/plop/Bureau/DataSetImageHiC/GM12878/test";
		//String output = "/home/plop/Bureau/DataSetImageHiC/HiChip/ring1b/testTer/";//"/home/plop/Bureau/DataSetImageHiC/GM12878/test";
		int matrixSize = 2000;
		int resolution = 5000;
		int diagSize = 5;
		double gauss = 1.5;
		int thresholdMax = 2800;// 2800;//1800
		int nbZero = 6;//6;
		double min = 2;//1.5;
		double max = 2;//1.5;
		double saturatedPixel =0.005;//0.005;
		boolean keepTif = true;
		ArrayList<Integer> factor = new ArrayList<Integer>();
		factor.add(1);
		factor.add(2);
		factor.add(5);
		HiCExperimentAnalysis wga = new HiCExperimentAnalysis(output, readChrSizeFile("/home/plop/Documents/Genome/mammals/HumanGenomeHg19/chr2.size"), gauss, min, max, 
				resolution, saturatedPixel, thresholdMax, diagSize, matrixSize,nbZero, factor);
		
		wga.setIsHichip(false);
		System.out.println("input "+input+"\n"
			+ "output "+output+"\n"
			+ "gauss "+gauss+"\n"
			+ "min "+min+"\n"
			+ "max "+max+"\n"
			+ "matrix size "+matrixSize+"\n"
			+ "diag size "+diagSize+"\n"
			+ "resolution "+resolution+"\n"
			+ "saturated pixel "+saturatedPixel+"\n"
			+ "threshold "+thresholdMax+"\n");
	
		File file = new File(output);
		if (file.exists()==false) 
			file.mkdir();
		//
		///home/plop/Documents/Genome/hg38.chr2.sizes
		///home/plop/Documents/Genome/mm9/mm9_sizes_noY_1.txt
		
		
		wga.run(input);
						
		if(keepTif == false){
			for(int i = 0; i< wga.m_tifList.size();++i)
				wga.m_tifList.get(i).delete();
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
