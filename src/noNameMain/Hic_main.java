package noNameMain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import core.HicFileProcessing;
import gui.GuiAnalysis;
import utils.HiCExperimentAnalysis;

/**
 * 
 * SIP Version 1 run with java 8
 * Usage:
 * hic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]
 * tprocessed <Directory with porocessed data> <chrSizeFile> <Output> [options]
 * chrSizeFile: path to the chr file size, with the same name of the chr than in the hic file
 * -res: resolution in bases (default 5000 bases)
 * -mat: matrix size in bins (default 2000 bins)
 * -d: diagonal size in bins, allow to removed the maxima found at this size (eg: a size of 2 at 5000 bases resolution removed all the maxima
 * with a distances inferior or equal to 10kb) (default 6 bins)
 * -g: Gaussian filter: smooth the image to reduce the noise (default 1)
 * -factor: Multiple resolutions can be specified using: 
 * -factor 1: run only for the input res
 * -factor 2: res and res*2
 * -factor 3: res and res*5
 * -factor 4: res, res*2 and res*5 (default 2)
 * -max: Maximum filter: increase the region of high intensity (default 1.5)
 * -min: Minimum filter: removed the isolated high value (default 1.5)
 * -sat: % of staturated pixel: enhance the contrast in the image (default 0.02)
 * -t Threshold for loops detection (default 1800)
 * -nbZero: number of zero: number of pixel equal at zero allowed in the 24 neighboorhood of the detected maxima, parameter for hic and processed method (default parameter 6)
 * -norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)
 * -del: true or false, delete tif files used for loop detection (default true)
 * -h, --help print help
 * 
 * @author Axel Poulet
 *
 */
public class Hic_main {
	/** Path of input data directory if dumped or processed data else .hic for hic option*/
	private static String m_input = "";
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
	private static int m_diagSize = 6;
	/**Resolution of the matric in bases*/
	private static int m_resolution = 5000;
	/** % of saturated pixel in the image, allow the enhancement of the contrast in the image*/
	private static double m_saturatedPixel = 0.02;
	/** Threshold to accepet a maxima in the images as a loop*/
	private static int m_thresholdMax = 1800;// or 100
	/**number of pixel = 0 allowed around the loop*/
	private static int m_nbZero = 6;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean m_isHic = true;
	/** factor(s) used to nalyse the matrix*/
	private static ArrayList<Integer> m_factor = new ArrayList<Integer>();
	private static String m_factOption = "2";
	/** if true run only image Processing step*/
	private static boolean m_isProcessed = false;
	/** */
	private static boolean m_isHiChip = false;
	/** hash map stocking in key the name of the chr and in value the size*/
	private static HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
	/** path to the chromosome size file */
	private static String m_chrSizeFile;
	/**boolean is true supress all the image created*/
	private static boolean m_delImages = true;
	/**Strin for the documentation*/
	private static String m_doc = ("SIP Version 1 run with java 8\n"
			+"Usage:\n"
			+"\thic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
			+"\tprocessed <Directory with porocessed data> <chrSizeFile> <Output> [options]\n"
			+"chrSizeFile: path to the chr file size, with the same name of the chr than in the hic file\n"
			+"-res: resolution in bases (default 5000 bases)\n"
			+"-mat: matrix size in bins (default 2000 bins)\n"
			+"-d: diagonal size in bins, allow to removed the maxima found at this size (eg: a size of 2 at 5000 bases resolution removed all the maxima"
			+"with a distances inferior or equal to 10kb) (default 6 bins)\n"
			+"-g: Gaussian filter: smooth the image to reduce the noise (default 1)\n"
			+"-hichip: true or false (default false), use true if you are analysing HiChIP data\n"
			+"-factor: Multiple resolutions can be specified using: "
			+ "\t-factor 1: run only for the input res\n"
			+ "\t-factor 2: res and res*2\n"
			+ "\t-factor 3: res and res*5\n"
			+ "\t-factor 4: res, res*2 and res*5 (default 2)\n"
			+"-max: Maximum filter: increase the region of high intensity (default 1.5)\n"
			+"-min: Minimum filter: removed the isolated high value (default 1.5)\n"
			+"-sat: % of staturated pixel: enhance the contrast in the image (default 0.005 for hic and 0.5 for hichip)\n"
			+"-t Threshold for loops detection (default 1800 for hic and 1 for hichip)\n"
			+ "-nbZero: number of zero: number of pixel equal at zero allowed in the 24 neighboorhood of the detected maxima, parameter for hic and processed method (default parameter 6 for hic and 25 for hichip)\n"
			+ "-norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)\n"
			+ "-del: true or false, delete tif files used for loop detection (default true)\n"
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
		m_factor.add(1);
		m_factor.add(2);
		if (args.length > 0 && args.length < 4){
			System.out.println(m_doc);
			System.exit(0);
		}
		else if(args.length >= 5){
			if (args[0].equals("hic") || args[0].equals("processed")){
				m_input = args[1];
				m_output = args[3];
				readChrSizeFile(args[2]);
				m_chrSizeFile = args[2];
				
				if (args[0].equals("hic")){
					readOption(args,5);
					m_juiceBoxTools = args[4];
				}	
				else if(args[0].equals("processed")){
					m_isHic = false;
					m_isProcessed = true;
					readOption(args,4);
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
			while( gui.isShowing()){
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
		    }	
			if (gui.isStart()){
				m_chrSizeFile = gui.getChrSizeFile();
				readChrSizeFile(gui.getChrSizeFile());
				m_output = gui.getOutputDir();
				m_input = gui.getRawDataDir();
				m_matrixSize = gui.getMatrixSize();
				m_diagSize = gui.getDiagSize();
				m_resolution = gui.getResolution();
				m_delImages = gui.isDeletTif();
				m_gauss = gui.getGaussian();
				m_max = gui.getMax();
				m_min = gui.getMin();
				m_isHiChip= gui.isHiChIP();
				m_nbZero = gui.getNbZero();
				m_saturatedPixel = gui.getEnhanceSignal();
				m_thresholdMax = gui.getNoiseTolerance();
				if(gui.getFactorChoice() == 1){
					m_factor = new ArrayList<Integer>();
					m_factor.add(1);
				}
				else if(gui.getFactorChoice() == 4){
					m_factor = new ArrayList<Integer>();
					m_factor.add(1);
					m_factor.add(2);
					m_factor.add(5);
				}
				else if(gui.getFactorChoice() == 3){
					m_factor = new ArrayList<Integer>();
					m_factor.add(1);
					m_factor.add(5);
				}
				
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
		
		File file = new File(m_output);
		if (file.exists()==false){file.mkdir();}
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(m_output+File.separator+"parameters.txt")));
		HiCExperimentAnalysis wga = new HiCExperimentAnalysis(m_output, m_chrSize, m_gauss, m_min, m_max, m_resolution, m_saturatedPixel, m_thresholdMax, m_diagSize, m_matrixSize, m_nbZero,m_factor);
		wga.setIsHichip(m_isHiChip);
		if(m_isHic){
			System.out.println("hic mode:\ninput: "+m_input+"\noutput: "+m_output+"\njuiceBox: "+m_juiceBoxTools+"\nnorm: "+ m_juiceBoXNormalisation+"\ngauss: "+m_gauss+"\n"
					+ "min: "+m_min+"\nmax: "+m_max+"\nmatrix size: "+m_matrixSize+"\ndiag size: "+m_diagSize+"\nresolution: "+m_resolution+"\nsaturated pixel: "+m_saturatedPixel
					+"\nthreshold: "+m_thresholdMax+"\n number of zero:"+m_nbZero+"\n factor "+ m_factOption+"\n");
			HicFileProcessing hfp =  new HicFileProcessing(m_input, wga, m_chrSize, m_juiceBoxTools, m_juiceBoXNormalisation);
				writer.write("java -jar noName hic "+m_input+" "+m_chrSizeFile+" "+m_output+" "+m_juiceBoxTools+" -norm "+ m_juiceBoXNormalisation+" -g "+m_gauss+
						" -min "+m_min+" -max "+m_max+" -mat "+m_matrixSize+" -d "+m_diagSize+" -res "+m_resolution+" -sat "+m_saturatedPixel+" -t "+m_thresholdMax+" -nbZero "
						+m_nbZero+" -factor "+ m_factOption+" -del "+m_delImages+"\n");
				hfp.run();
		}
		else if (m_isProcessed){
			System.out.println("processed mode:\ninput: "+m_input+"\noutput: "+m_output+"\njuiceBox: "+m_juiceBoxTools+"\nnorm: "+ m_juiceBoXNormalisation+"\ngauss: "+m_gauss
					+"\nmin: "+m_min+"\nmax: "+m_max+"\nmatrix size: "+m_matrixSize+"\ndiag size: "+m_diagSize+"\nresolution: "+m_resolution+"\nsaturated pixel: "+m_saturatedPixel
					+"\nthreshold: "+m_thresholdMax+"\nisHic: "+m_isHic+"\nisProcessed: "+m_isProcessed+"\n number of zero:"
					+m_nbZero+"\n factor "+ m_factOption+"\n");
				writer.write("java -jar noName processed "+m_input+" "+m_chrSizeFile+" "+m_output+" "+m_gauss+" -mat "+m_matrixSize+" -d "+m_diagSize+" -res "+m_resolution
						+" -t "+m_thresholdMax+" -min "+m_min+" -max "+m_max+" -sat "+m_saturatedPixel+" -nbZero "+m_nbZero+" -factor "+ m_factOption+" -del "+m_delImages+"\n");
				wga.run(m_input);
		}
		writer.close();		
		if(m_delImages){
			for(int i = 0; i< wga.m_tifList.size();++i)
				wga.m_tifList.get(i).delete();
		}
		System.out.println("End");
	}
	
	/**
	 * Run the input file and stock the info of name chr and their size in hashmap
	 * @param chrSizeFile path chr size file
	 * @throws IOException if file does't exist
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
	 * -d: diagonal size in bins (default 2 bins)
	 * -g: Gaussian filter (default 1)
	 * -max: Maximum filter: increase the region of high intensity (default 1.5)
	 * -min: minimum filter: removed the isolated high value (default 1.5)
	 * -sat: % of staturated pixel: enhance the contrast in the image (default 0.05)
	 * -t Threshold for loops detection (default 3000)
	 * -norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)
	 * -nbZero: 
	 * 
	 * @param args table of String stocking the arguments for the program
	 * @param index table index where start to read the arguments
	 * @throws IOException if some parameters don't exist
	 */
	private static void readOption(String args[], int index) throws IOException{
		if(index < args.length){
			for(int i = index; i < args.length;i+=2){
				if(args[i].equals("-res")){
					try{m_resolution =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-res",args[i+1],"int");} 
				}
				else if(args[i].equals("-mat")){
					try{m_matrixSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-mat",args[i+1],"int");} 
				}
				else if(args[i].equals("-factor")){
					int a  = Integer.parseInt(args[i+1]);
					m_factOption = args[i+1];
					if(a == 1){
						m_factor = new ArrayList<Integer>();
						m_factor.add(1);
					}
					else if(a == 4){
						m_factor = new ArrayList<Integer>();
						m_factor.add(1);
						m_factor.add(2);
						m_factor.add(5);
					}
					else if(a == 3){
						m_factor = new ArrayList<Integer>();
						m_factor.add(1);
						m_factor.add(5);
						
					}
					else if(a != 2){
						returnError("-mat",args[i+1],"int or not correct choice");
					} 
				}
				else if(args[i].equals("-d")){
					try{m_diagSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");} 
				}
				else if(args[i].equals("-nbZero")){
					try{m_nbZero =Integer.parseInt(args[i+1]);}
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
						System.out.println("-norm = "+args[i+1]+", not defined\n");
						System.out.println(m_doc);
						System.exit(0);
					}
				}
				else if(args[i].equals("-del")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						m_delImages = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						m_delImages = false;
					else{
						System.out.println("-del = "+args[i+1]+", not defined\n");
						System.out.println(m_doc);
						System.exit(0);
					}
				}
				else if(args[i].equals("-hichip")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						m_isHiChip = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						m_isHiChip = false;
					else{
						System.out.println("-hichip = "+args[i+1]+", not defined\n");
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
	 * Return specifci error on function of thearugnent problems 
	 * 
	 * @param param String name of the arugment
	 * @param value	String value of the argument
	 * @param type Strint type of the argument
	 */
	private static void returnError(String param, String value, String type){
		System.out.println(param+" has to be an integer "+value+" can't be convert in "+type+"\n");
		System.out.println(m_doc);
		System.exit(0);
	}
}
