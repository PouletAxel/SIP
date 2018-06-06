package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import core.HiCFileComparison;
import utils.WholeGenomeAnalysis;

public class TestComparisonHicFile {

		/**
		 * 
		 * @param args
		 * @throws IOException 
		 * @throws InterruptedException 
		 */
		public static void main(String[] args) throws IOException, InterruptedException{
			String output= "/home/plop/Bureau/DataSetImageHiC/rao2017/TestCompareTest";
			String input = "/home/plop/Bureau/DataSetImageHiC/rao2017/Untreated";
			String input2 = "/home/plop/Bureau/DataSetImageHiC/rao2017/6hr";
			String loopsFile1 = "/home/plop/Bureau/DataSetImageHiC/rao2017/Untreated1_15_3500/untreated_loops_filtered.bed";
			String loopsFile2 = "/home/plop/Bureau/DataSetImageHiC/rao2017/6hr1_15_3500/6hr_loops_filtered.bed";
			int step = 500;
			int matrixSize = 1000;
			int resolution = 10000;
			int diagSize = 4;
			int thresholdMax = 3;
			double gauss = 0.5;
			double min = 1;
			
			System.out.println("input "+input+"\n"
					+ "output "+output+"\n"
					+ "gauss "+gauss+"\n"
					+ "min "+min+"\n"
					+ "matrix size "+matrixSize+"\n"
					+ "diag size "+diagSize+"\n"
					+ "resolution "+resolution+"\n"
					+ "threshold "+thresholdMax+"\n"
					+ "step "+step+"\n");
		
			
			File file = new File(output);
			if (file.exists()==false){file.mkdir();}
			
			HiCFileComparison plopi = new HiCFileComparison(input,loopsFile1,input2,loopsFile2, output, resolution, matrixSize, step);
			plopi.setDiag(diagSize);
			plopi.setGauss(gauss);
			plopi.setMin(min);
			plopi.setThreshold(thresholdMax);
			
			plopi.run();
			System.out.println("End");
		}	
		
}
