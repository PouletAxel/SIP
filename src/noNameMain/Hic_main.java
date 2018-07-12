/**
 * 
 */
package noNameMain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	/** Path of input data directory if dumped or processed data else .hic for hic option*/
	private static String m_bedfile2 = "";
	/** Path of input data directory if dumped or processed data else .hic for hic option*/
	private static String m_bedfile1 = "";
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
	private static boolean m_isCompare = false;
	/** hash map stocking in key the name of the chr and in value the size*/
	private static HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
	private static String m_chrSizeFile;
	/**the doc of the prog*/
	private static String m_doc = ("No Name Version 0.0.1 run with java 8\n"
			+"Usage:\n"
			+"\thic <observed/oMe> <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
			+"\tprocessed <observed/oMe> <Directory with porocessed data> <chrSizeFile> <Output> [options]\n"
			+"\tcompare <Directory with porocessed data 1> <loops file 1> <Directory with porocessed data 2> <loops file 2> <Output> [options]"
			+"chrSizeFile: path to the chr file size, with the same name of the chr than in the hic file\n"
			+"-res: resolution in bases (default 5000 bases)\n"
			+"-mat: matrix size in bins (default 2000 bins)\n"
			+"-s: step in bins size (default 1000 bins)\n"
			+"-d: diagonal size in bins, allow to removed the maxima found at this size (eg: a size of 2 at 5000 bases resolution removed all the maxima"
			+"with a distances inferior or equal to 10kb) (default 4 bins)\n"
			+"-g: Gaussian filter: smooth the image to reduce the noise (default 1)\n"
			+"-max: Maximum filter: increase the region of high intensity (default 1.5) only oMe\n"
			+"-min: Minimum filter: removed the isolated high value (default 1.5) only oMe and compare\n"
			+"-sat: % of staturated pixel: enhance the contrast in the image (default 0.05) only oMe\n"
			+"-t Threshold for loops detection (default 3000 for only oMe, 100 for observed, 3 for compare)\n"
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
			if (args[0].equals("compare")){
				m_isHic = false;
				m_isProcessed = false;
				m_isCompare = true;
				m_input2 = args[3];
				m_bedfile2 = args[4];
				m_output = args[5];
				m_bedfile1 = args[2];
				m_input = args[1];
				readOption(args,6);
			}
			else if (args[0].equals("hic") || args[0].equals("processed")){
				m_input = args[2];
				m_output = args[4];
				readChrSizeFile(args[3]);
				m_chrSizeFile = args[3];
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
				
				if (args[0].equals("hic")){
					readOption(args,6);
					m_juiceBoxTools = args[5];
				}	
				else if(args[0].equals("processed")){
					m_isHic = false;
					m_isProcessed = true;
					readOption(args,5);
				}
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
				m_chrSizeFile = gui.getChrSizeFile();
				readChrSizeFile(gui.getChrSizeFile());
				m_output = gui.getOutputDir();
				m_input = gui.getRawDataDir();
				m_input2 = gui.getRawDataDir2();
				m_bedfile1 = gui.getLoopFile1();
				m_bedfile2 = gui.getLoopFile2();
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
				m_isCompare = gui.isCompare();
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
		
		File file = new File(m_output);
		if (file.exists()==false){file.mkdir();}
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(m_output+File.separator+"parameters.txt")));
		
		if (m_isCompare==false){
			WholeGenomeAnalysis wga = new WholeGenomeAnalysis(m_output, m_chrSize, m_gauss, m_min, m_max, m_step, m_resolution, m_saturatedPixel, m_thresholdMax, m_diagSize, m_matrixSize);
			if(m_isHic){
				System.out.println("hic mode:\ninput: "+m_input+"\noutput: "+m_output+"\njuiceBox: "+m_juiceBoxTools+"\nnorm: "+ m_juiceBoXNormalisation+"\ngauss: "+m_gauss+"\n"
						+ "min: "+m_min+"\nmax: "+m_max+"\nmatrix size: "+m_matrixSize+"\ndiag size: "+m_diagSize+"\nresolution: "+m_resolution+"\nsaturated pixel: "+m_saturatedPixel
						+"\nthreshold: "+m_thresholdMax+"\nstep: "+m_step+"\nisObserved: "+m_isObserved+"\n");
				HicFileProcessing hfp =  new HicFileProcessing(m_input, wga, m_chrSize, m_juiceBoxTools, m_juiceBoXNormalisation);
				if(m_isObserved){
					writer.write("java -jar noName hic observed "+m_input+" "+m_chrSizeFile+" "+m_output+" "+m_juiceBoxTools+" -norm: "+ m_juiceBoXNormalisation+" -g: "+m_gauss+" -mat "
				+m_matrixSize+" -d "+m_diagSize+" -res "+m_resolution+" -t "+m_thresholdMax+" -s "+m_step+"\n");
					hfp.run(true);
				}
				else{
					writer.write("java -jar noName hic oMe "+m_input+" "+m_chrSizeFile+" "+m_output+" "+m_juiceBoxTools+" -norm: "+ m_juiceBoXNormalisation+" -g: "+m_gauss+
							" -min: "+m_min+" -max: "+m_max+" -mat "+m_matrixSize+" -d "+m_diagSize+" -res "+m_resolution+" -sat "+m_saturatedPixel+" -t "+m_thresholdMax+" -s "+m_step+"\n");
					hfp.run(false);
				}
			
			}
			else if (m_isProcessed){
				System.out.println("processed mode:\ninput: "+m_input+"\noutput: "+m_output+"\njuiceBox: "+m_juiceBoxTools+"\nnorm: "+ m_juiceBoXNormalisation+"\ngauss: "+m_gauss
						+"\nmin: "+m_min+"\nmax: "+m_max+"\nmatrix size: "+m_matrixSize+"\ndiag size: "+m_diagSize+"\nresolution: "+m_resolution+"\nsaturated pixel: "+m_saturatedPixel
						+"\nthreshold: "+m_thresholdMax+"\nstep: "+m_step+"\nisObserved: "+m_isObserved+"\nisHic: "+m_isHic+"\nisProcessed: "+m_isProcessed+"\n");
				if(m_isObserved){
					writer.write("java -jar noName processed observed "+m_input+" "+m_chrSizeFile+" "+m_output+" "+m_gauss+" -mat "+m_matrixSize+" -d "+m_diagSize+" -res "+m_resolution
							+" -t "+m_thresholdMax+" -s "+m_step+"\n");
					wga.run("o",m_input);
				}
				else{
					writer.write("java -jar noName processed oMe "+m_input+" "+m_chrSizeFile+" "+m_output+" "+m_gauss+" -mat "+m_matrixSize+" -d "+m_diagSize+" -res "+m_resolution
							+" -t "+m_thresholdMax+" -s "+m_step+" -min: "+m_min+" -max: "+m_max+" -sat "+m_saturatedPixel+"\n");
					wga.run("oMe",m_input);
				}
			}
		}
		else if (m_isCompare){
		
			System.out.println("Compare parameter:\ninput 1 "+m_input+"\nbed file 1: "+m_bedfile1+"\ninput 2:"+m_input2+"\nbed file 2:"+m_bedfile2+"\noutput: "+m_output
					+"\ngauss: "+m_gauss+"\nmin: "+m_min+"\nmatrix size: "+m_matrixSize+"\ndiag size: "+m_diagSize+"\nresolution: "+m_resolution+"\nthreshold: "+m_thresholdMax
					+"\nstep: "+m_step+"\n");
			writer.write("java -jar noNameCompare "+m_input+" "+m_bedfile1+" "+m_input2+" "+m_bedfile2+" "+m_output+" -g "+m_gauss+" -min "+m_min+" -mat "+m_matrixSize
					+" -d "+m_diagSize+" -res "+m_resolution+" -t "+m_thresholdMax+" -s "+m_step+"\n");
			HiCFileComparison plopi = new HiCFileComparison(m_input,m_bedfile1,m_input2,m_bedfile2, m_output, m_resolution, m_matrixSize, m_step);
			plopi.setDiag(m_diagSize);
			plopi.setGauss(m_gauss);
			plopi.setMin(m_min);
			plopi.setThreshold(m_thresholdMax);
			//plopi.run();
		}
		writer.close();		
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
	 * @param m_chrSizeFile
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
