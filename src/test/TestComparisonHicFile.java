package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import core.HiCFileComparison;
import core.HicFileProcessing;
import gui.GuiAnalysis;
import utils.WholeGenomeAnalysis;

public class TestComparisonHicFile {
		/** Path of input data directory if dumped or processed data else .hic for hic option*/
		private static String m_input = "";
		/** Path of input data directory if dumped or processed data else .hic for hic option*/
		private static String m_input2 = "";
		/**	 Path of output if not existed it is created*/
		private static String m_output = "";
		/** Path to the jucier_tools_box to dump the data not necessary for Processed and dumped method */
		private static String m_juiceBoxTools = "";
		/**Normalisation method to dump the the data with hic method (KR,NONE.VC,VC_SQRT)*/
		private static String m_juiceBoXNormalisation = "KR";
		/**Size of the core for the gaussian blur filter allow to smooth the signal*/
		private static double m_gauss = 1;
		/**Size of the core for the Minimum filter*/
		private static double m_min = 1.5;
		/**Size of the core for the Maximum filter*/
		private static double m_max = 1.5;
		/**Matrix size: size in bins of the final image and defined the zone of interest in the hic map*/
		private static int m_matrixSize = 2000;
		/**Distance to the diagonal where the loops are ignored*/
		private static int m_diagSize = 2;
		/**Resolution of the matric in bases*/
		private static int m_resolution = 5000;
		/** % of saturated pixel in the image, allow the enhancement of the contrast in the image*/
		private static double m_saturatedPixel = 0.05;
		/** Threshold to accepet a maxima in the images as a loop*/
		private static int m_thresholdMax = 3000;// or 100
		/**Step to process the chromosomes (maybe removed this parameters and only put m_matrixSize/2)*/
		private static int m_step = 1000;
		/** boolean to know if data used is observed or Observed minus Expected */
		private static boolean m_isObserved = false;
		/** boolean if true run all the process (dump data + image +image processing*/
		private static boolean m_isHic = true;
		/** if true run only image Processing step*/
		private static boolean m_isProcessed = false;
		/** hash map stocking in key the name of the chr and in value the size*/
		private static HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
		/**the doc of the prog*/
		private static String m_doc = ("No Name Version 0.0.1 run with java 8\n"
				+"Usage: hic <observed/oMe> <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
				+"\tprocessed <observed/oMe> <Directory with porocessed data> <chrSizeFile> <Output> [options]\n"
				+"chrSizeFile: path to the chr file size, with the same name of the chr than in the hic file\n"
				+"-res: resolution in bases (default 5000 bases)\n"
				+"-mat: matrix size in bins (default 2000 bins)\n"
				+"-s: step in bins size (default 1000 bins)\n"
				+"-d: diagonal size in bins, allow to removed the maxima found at this size (eg: a size of 2 at 5000 bases resolution removed all the maxima "
				+"with a distances inferior or equal to 10kb) (default 2 bins)\n"
				+"-g: Gaussian filter: smooth the image to reduce the noise (default 1)\n"
				+"-max: Maximum filter: increase the region of high intensity (default 1.5) only oMe\n"
				+"-min: minimum filter: removed the isolated high value (default 1.5) only oMe\n"
				+"-sat: % of staturated pixel: enhance the contrast in the image (default 0.05) only oMe\n"
				+"-t Threshold for loops detection (default 3000 for only oMe and 100 for observed)\n"
				+ "-norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)\n"
				+"-h, --help print help\n");
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
		public static void main(String[] args) throws IOException, InterruptedException{
			m_output= "/home/plop/Bureau/DataSetImageHiC/rao2017/TestCompare";
			m_input = "/home/plop/Bureau/DataSetImageHiC/rao2017/Untreated";
			m_input2 = "/home/plop/Bureau/DataSetImageHiC/rao2017/6hr";
			//readChrSizeFile("/home/plop/Documents/Genome/dm6_bis.chrom.sizes");
			
			//m_juiceBoxTools = "/home/plop/Tools/juicer_tools.1.8.9_jcuda.0.8.jar";
			m_step = 500;
			m_matrixSize = 1000;
			m_resolution = 10000;
			m_diagSize = 2;
			m_thresholdMax = 10;
			//m_juiceBoXNormalisation = "KR";
			m_isObserved = true;
			m_isHic = false;
			
			System.out.println("input "+m_input+"\n"
					+ "output "+m_output+"\n"
					+ "juiceBox "+m_juiceBoxTools+"\n"
					+ "norm "+ m_juiceBoXNormalisation+"\n"
					+ "gauss "+m_gauss+"\n"
					+ "min "+m_min+"\n"
					+ "max "+m_max+"\n"
					+ "matrix size "+m_matrixSize+"\n"
					+ "diag size "+m_diagSize+"\n"
					+ "resolution "+m_resolution+"\n"
					+ "saturated pixel "+m_saturatedPixel+"\n"
					+ "threshold "+m_thresholdMax+"\n"
					+ "step "+m_step+"\n"
					+ "isObserved "+m_isObserved+"\n"
					+ "isHic "+m_isHic+"\n"
					+ "isProcessed "+m_isProcessed+"\n");
			
			
			File file = new File(m_output);
			if (file.exists()==false){file.mkdir();}
			
			
			WholeGenomeAnalysis wga = new WholeGenomeAnalysis(m_output, m_chrSize, m_gauss, m_min, m_max, m_step, 
					m_resolution, m_saturatedPixel, m_thresholdMax, m_diagSize, m_matrixSize);
			
			HiCFileComparison plopi = new HiCFileComparison(m_input,m_input2, wga);
			plopi.run();
			System.out.println("End");
		}
		
}
