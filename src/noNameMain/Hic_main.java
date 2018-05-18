/**
 * 
 */
package noNameMain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import core.HiCFileComparison;
import core.HicFileProcessing;
import gui.GuiAnalysis;
import utils.WholeGenomeAnalysis;

/**
 * 
 * @author axel poulet
 *
 */
public class Hic_main {
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
	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length >0 && args.length <5){
			System.out.println(m_doc);
			System.exit(0);
		}
		else if(args.length >=5){
			m_input = args[2];
			m_output = args[4];
			readChrSizeFile(args[3]);
			///// test observed or oMe
			if(args[1].equals("observed")){
				m_isObserved = true ;
				m_thresholdMax = 100;
			}
			else if(args[1].equals("oMe")) m_isObserved = false;
			else{
				System.out.println(args[1]+" doesn't existed\n");
				System.out.println(m_doc);
				System.exit(0);
			}
			
			//// test hic, or dumped or processed
			if (args[0].equals("hic")){
				readOption(args,6);
				m_juiceBoxTools = args[5];
			}	
			else if(args[0].equals("processed")){
				m_isHic = false;
				m_isProcessed = true;
				readOption(args,5);
			}
				
			else{
				System.out.println(args[0]+" not defined\n");
				System.out.println(m_doc);
				System.exit(0);
			}
						
		}
		
		////////////////////////////////////////GUI parameter initialisation
		else{
			GuiAnalysis gui = new GuiAnalysis();
			while( gui.isShowing())
			{
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
		    }	
			if (gui.isStart()){
				
				readChrSizeFile(gui.getChrSizeFile());
				m_output = gui.getOutputDir();
				m_input = gui.getRawDataDir();
				
				m_step= gui.getStep();
				m_matrixSize = gui.getMatrixSize();
				m_diagSize = gui.getDiagSize();
				m_resolution = gui.getResolution();
				
				m_gauss = gui.getGaussian();
				m_max = gui.getMax();
				m_min = gui.getMin();
				
				m_saturatedPixel = gui.getEnhanceSignal();
				m_thresholdMax = gui.getNoiseTolerance();
					
				m_isObserved = gui.isObserved();
				m_isHic  = gui.isHic();
				m_isProcessed = gui.isProcessed();
				m_juiceBoxTools = gui.getJuiceBox();
				
				if(gui.isNONE()) m_juiceBoXNormalisation = "NONE";
				else if (gui.isVC()) m_juiceBoXNormalisation = "VC";
				else if (gui.isVC_SQRT()) m_juiceBoXNormalisation = "VC_SQRT";				
			}
			else {
				System.out.println("program NO Name closed: if you want the help: -h");
				System.exit(0);
			}
		}
		
		/*m_input= "/home/plop/Bureau/DataSetImageHiC/Droso/test/plop";
		m_output= "/home/plop/Bureau/DataSetImageHiC/Droso/test/plop";
		//m_input = "/home/plop/Bureau/DataSetImageHiC/Droso/Kc167_combo_q30.hic";
		m_input2 = "/home/plop/Bureau/DataSetImageHiC/Droso/S2_cattoni_wang_ramirez_q30.hic";
		readChrSizeFile("/home/plop/Documents/Genome/dm6.chrom.sizes");
		
		m_juiceBoxTools = "/home/plop/Tools/juicer_tools.1.8.9_jcuda.0.8.jar";
		m_step = 500;
		m_matrixSize = 1000;
		m_resolution = 5000;
		m_diagSize = 4;
		m_gauss = 0.5;
		m_thresholdMax =70;
		m_juiceBoXNormalisation = "KR";
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
				+ "isProcessed "+m_isProcessed+"\n");*/
		
		
		File file = new File(m_output);
		if (file.exists()==false){file.mkdir();}
		
		
		WholeGenomeAnalysis wga = new WholeGenomeAnalysis(m_output, m_chrSize, m_gauss, m_min, m_max, m_step, 
				m_resolution, m_saturatedPixel, m_thresholdMax, m_diagSize, m_matrixSize);
		
	//	HiCFileComparison plopi = new HiCFileComparison(m_input,m_input2,m_chrSize,m_juiceBoxTools,m_juiceBoXNormalisation, wga);
	//	plopi.runOmE();
	
		
	
		if(m_isHic){
			
			HicFileProcessing hfp =  new HicFileProcessing(m_input, wga, m_chrSize, m_juiceBoxTools, m_juiceBoXNormalisation);
			if(m_isObserved) hfp.run(true);
			else		hfp.run(false);
			
		}
		else{
			if(m_isObserved) wga.run("o",m_input);
			else wga.run("oMe",m_input);
		}
				
		System.out.println("End");
	}
	
	/**
	 * 
	 * @param chrSizeFile
	 * @throws IOException
	 */
	private static void readChrSizeFile( String chrSizeFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null){
			sb.append(line);
			String[] parts = line.split("\\t");				
			String chr = parts[0]; 
			int size = Integer.parseInt(parts[1]);
			m_chrSize.put(chr, size);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
	} 
	
	/**
	 * -res: resolution in bases (default 5000 bases)
	 * -mat: matrix size in bins (default 2000 bins)
	 * -s: step in bins size (default 1000 bins)
	 * -d: diagonal size in bins (default 2 bins)
	 * -g: Gaussian filter (default 1)
	 * -max: Maximum filter: increase the region of high intensity (default 1.5)
	 * -min: minimum filter: removed the isolated high value (default 1.5)
	 * -sat: % of staturated pixel: enhance the contrast in the image (default 0.05)
	 * -t Threshold for loops detection (default 3000)
	 * -norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)"
	 * @param chrSizeFile
	 * @throws IOException
	 */
	private static void readOption(String args[], int index) throws IOException{
		if(index < args.length){
			for(int i = index; i < args.length;i+=2){
				if(args[i].equals("-s")){
					try{m_step =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-s",args[i+1],"int");} 
				}
				else if(args[i].equals("-res")){
					try{m_resolution =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-res",args[i+1],"int");} 
				}
				else if(args[i].equals("-mat")){
					try{m_matrixSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-mat",args[i+1],"int");} 
				}
				else if(args[i].equals("-d")){
					try{m_diagSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");} 
				}
				else if(args[i].equals("-g")){
					try{m_gauss =Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-g",args[i+1],"double");}
				}
				else if(args[i].equals("-max")){
					try{m_max = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-max",args[i+1],"double");}
					
				}
				else if(args[i].equals("-min")){
					try{m_min = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-min",args[i+1],"double");}
					
				}
				else if(args[i].equals("-sat")){
					try{m_saturatedPixel = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-sat",args[i+1],"double");}
					
				}
				else if(args[i].equals("-t")){
					try{m_thresholdMax =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-t",args[i+1],"int");}
				}
				else if(args[i].equals("-norm")){
					if(args[i+1].equals("NONE") || args[i+1].equals("VC") 
							|| args[i+1].equals("VC_SQRT") || args[i+1].equals("KR")){
						m_juiceBoXNormalisation = args[i+1];
					}
					else{
						System.out.println("-norm = args[i+1], not defined\n");
						System.out.println(m_doc);
						System.exit(0);
					}
				}
				else{
					System.out.println(args[i]+" doesn't existed\n");
					System.out.println(m_doc);
					System.exit(0);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param param
	 * @param value
	 * @param type
	 */
	private static void returnError(String param, String value, String type){
		System.out.println(param+" has to be an integer "+value+" can't be convert in "+type+"\n");
		System.out.println(m_doc);
		System.exit(0);
	}
}
