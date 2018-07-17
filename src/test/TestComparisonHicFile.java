package test;

import java.io.File;
import java.io.IOException;
import core.HiCFileComparison;

/**
 * 
 * @author plop
 *
 */
public class TestComparisonHicFile {
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException{
		String output= "/home/plop/Bureau/DataSetImageHiC/TestCompareTestMouse";
		String input = "/home/plop/Bureau/DataSetImageHiC/BPASperm_test/";
		String input2 = "/home/plop/Bureau/DataSetImageHiC/SpermCtl_test/";
		String loopsFile1 = "/home/plop/Bureau/DataSetImageHiC/BPASperm_test/loops_filtered.bed";
		String loopsFile2 = "/home/plop/Bureau/DataSetImageHiC/SpermCtl_test/loops.bed";
		int matrixSize = 1000;
		int resolution = 10000;
		int diagSize = 4;
		int thresholdMax = 3;
		double gauss = 1;
		double min = 1;
			
		System.out.println("input "+input+"\n"
				+ "loops file 1 "+ loopsFile1+"\n"
				+ "output "+output+"\n"
				+ "gauss "+gauss+"\n"
				+ "min "+min+"\n"
				+ "matrix size "+matrixSize+"\n"
				+ "diag size "+diagSize+"\n"
				+ "resolution "+resolution+"\n"
				+ "threshold "+thresholdMax+"\n"
				+ "\n");
			
		File file = new File(output);
		if (file.exists()==false){file.mkdir();}
			
		HiCFileComparison plopi = new HiCFileComparison(input,loopsFile1,input2,loopsFile2, output, resolution, matrixSize);
		plopi.setDiag(diagSize);
		plopi.setGauss(gauss);
		plopi.setMin(min);
		plopi.setThreshold(thresholdMax);	
		plopi.run();
		System.out.println("End");
	}			
}