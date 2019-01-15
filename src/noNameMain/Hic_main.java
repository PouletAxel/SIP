package noNameMain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import gui.GuiAnalysis;
import utils.HiCExperimentAnalysis;
import utils.HicFileProcessing;

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
 * -g: Gaussian filter: smooth the image to reduce the noise (default 1.5 for hic and 1 for hichip)
 * -factor: Multiple resolutions can be specified using: 
 * -factor 1: run only for the input res
 * -factor 2: res and res*2
 * -factor 3: res and res*5
 * -factor 4: res, res*2 and res*5 (default 2)
 * -max: Maximum filter: increase the region of high intensity (default 2 for hic 1 for hichip)
 * -min: Minimum filter: removed the isolated high value (default 2)
 * -sat: % of staturated pixel: enhance the contrast in the image (default 0.005 for hic and 0.5 for hichip)
 * -t Threshold for loops detection (default 2600 for hic and 1 for hichip)
 * -nbZero: number of zero: number of pixel equal at zero allowed in the 24 neighboorhood of the detected maxima, parameter for hic and processed method (default parameter 6)
 * -norm: <NONE/VC/VC_SQRT/KR> only for hic option (default KR)
 * -del: true or false, delete tif files used for loop detection (default true)
 * -hichip: true or false (default false), use true if you are analysing HiChIP data
 * -h, --help print help
 * 
 * @author Axel Poulet
 *
 */
public class Hic_main {
	/** Path of input data directory if dumped or processed data else .hic for hic option*/
	private static String _input = "";
	/**	 Path of output if not existed it is created*/
	private static String _output = "";
	/** Path to the jucier_tools_box to dump the data not necessary for Processed and dumped method */
	private static String _juiceBoxTools = "";
	/**Normalisation method to dump the the data with hic method (KR,NONE.VC,VC_SQRT)*/
	private static String _juiceBoXNormalisation = "KR";
	/**Size of the core for the gaussian blur filter allow to smooth the signal*/
	private static double _gauss = 1.5;
	/**Size of the core for the Minimum filter*/
	private static double _min = 2.0;
	/**Size of the core for the Maximum filter*/
	private static double _max = 2.0;
	/**Matrix size: size in bins of the final image and defined the zone of interest in the hic map*/
	private static int _matrixSize = 2000;
	/**Distance to the diagonal where the loops are ignored*/
	private static int _diagSize = 5;
	/**Resolution of the matric in bases*/
	private static int _resolution = 5000;
	/** % of saturated pixel in the image, allow the enhancement of the contrast in the image*/
	private static double _saturatedPixel = 0.005;
	/** Threshold to accepet a maxima in the images as a loop*/
	private static int _thresholdMax = 2500;
	/**number of pixel = 0 allowed around the loop*/
	private static int _nbZero = 6;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isHic = true;
	/** factor(s) used to nalyse the matrix*/
	private static ArrayList<Integer> _factor = new ArrayList<Integer>();
	/** String factor option*/
	private static String _factOption = "2";
	/** if true run only image Processing step*/
	private static boolean _isProcessed = false;
	/**boolean true: hichip data if fals hic data */
	private static boolean _isHiChip = false;
	/** hash map stocking in key the name of the chr and in value the size*/
	private static HashMap<String,Integer> _chrSize =  new HashMap<String,Integer>();
	/** path to the chromosome size file */
	private static String _chrSizeFile;
	/**boolean is true supress all the image created*/
	private static boolean _delImages = true;
	/**boolean is true supress all the image created*/
	private static boolean _gui = false;
	/**Strin for the documentation*/
	private static String _doc = ("SIP Version 1 run with java 8\n"
			+"Usage:\n"
			+"\thic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
			+"\tprocessed <Directory with porocessed data> <chrSizeFile> <Output> [options]\n"
			+"chrSizeFile: path to the chr file size, with the same name of the chr than in the hic file\n"
			+"-res: resolution in bases (default 5000 bases)\n"
			+"-mat: matrix size in bins (default 2000 bins)\n"
			+"-d: diagonal size in bins, allow to removed the maxima found at this size (eg: a size of 2 at 5000 bases resolution removed all the maxima"
			+"with a distances inferior or equal to 10kb) (default 6 bins)\n"
			+"-g: Gaussian filter: smooth the image to reduce the noise (default 1.5 for hic and 1 for hichip)\n"
			+"-hichip: true or false (default false), use true if you are analysing HiChIP data\n"
			+"-factor: Multiple resolutions can be specified using: "
			+ "\t-factor 1: run only for the input res\n"
			+ "\t-factor 2: res and res*2\n"
			+ "\t-factor 3: res and res*5\n"
			+ "\t-factor 4: res, res*2 and res*5 (default 2)\n"
			+"-max: Maximum filter: increase the region of high intensity (default 2 for hic and 1 hichip)\n"
			+"-min: Minimum filter: removed the isolated high value (default 2 for hic and 1 hichip)\n"
			+"-sat: % of staturated pixel: enhance the contrast in the image (default 0.005 for hic and 0.5 for hichip)\n"
			+"-t Threshold for loops detection (default 2500 for hic and 1 for hichip)\n"
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
		_factor.add(1);
		_factor.add(2);
		if (args[0].equals("-h")||args[0].equals("-help")||args[0].equals("--help")||args[0].equals("-h")){
			System.out.println(_doc);
			System.exit(0);
		}else if (args.length > 0 && args.length < 4){
			System.out.println(_doc);
			System.exit(0);
		}else if(args.length >= 4){
			if (args[0].equals("hic") || args[0].equals("processed")){
				_input = args[1];
				_output = args[3];
				readChrSizeFile(args[2]);
				_chrSizeFile = args[2];
				
				if (args[0].equals("hic")){
					readOption(args,5);
					_juiceBoxTools = args[4];
				}else if(args[0].equals("processed")){
					_isHic = false;
					_isProcessed = true;
					readOption(args,4);
				}
			}else{
				System.out.println(args[0]+" not defined\n");
				System.out.println(_doc);
				System.exit(0);
			}
						
		}else{////////////////////////////////////////GUI parameter initialisation
			GuiAnalysis gui = new GuiAnalysis();
			_gui =true;
			while( gui.isShowing()){
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
		    }	
			if (gui.isStart()){
				_chrSizeFile = gui.getChrSizeFile();
				readChrSizeFile(gui.getChrSizeFile());
				_output = gui.getOutputDir();
				_input = gui.getRawDataDir();
				_matrixSize = gui.getMatrixSize();
				_diagSize = gui.getDiagSize();
				_resolution = gui.getResolution();
				_delImages = gui.isDeletTif();
				_gauss = gui.getGaussian();
				_max = gui.getMax();
				_min = gui.getMin();
				_isHiChip= gui.isHiChIP();
				_nbZero = gui.getNbZero();
				_saturatedPixel = gui.getEnhanceSignal();
				_thresholdMax = gui.getNoiseTolerance();
				if(gui.getFactorChoice() == 1){
					_factor = new ArrayList<Integer>();
					_factor.add(1);
				}else if(gui.getFactorChoice() == 4){
					_factor = new ArrayList<Integer>();
					_factor.add(1);
					_factor.add(2);
					_factor.add(5);
				}else if(gui.getFactorChoice() == 3){
					_factor = new ArrayList<Integer>();
					_factor.add(1);
					_factor.add(5);
				}
				_isHic  = gui.isHic();
				_isProcessed = gui.isProcessed();
				_juiceBoxTools = gui.getJuiceBox();
				
				if(gui.isNONE()) _juiceBoXNormalisation = "NONE";
				else if (gui.isVC()) _juiceBoXNormalisation = "VC";
				else if (gui.isVC_SQRT()) _juiceBoXNormalisation = "VC_SQRT";				
			}else {
				System.out.println("program NO Name closed: if you want the help: -h");
				System.exit(0);
			}
		}
		
		File file = new File(_output);
		if (file.exists()==false) file.mkdir();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_output+File.separator+"parameters.txt")));
		HiCExperimentAnalysis wga = new HiCExperimentAnalysis(_output, _chrSize, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax, _diagSize, _matrixSize, _nbZero,_factor);
		wga.setIsHichip(_isHiChip);
		if(_isHic){
			System.out.println("hic mode:\ninput: "+_input+"\noutput: "+_output+"\njuiceBox: "+_juiceBoxTools+"\nnorm: "+ _juiceBoXNormalisation+"\ngauss: "+_gauss+"\n"
					+ "min: "+_min+"\nmax: "+_max+"\nmatrix size: "+_matrixSize+"\ndiag size: "+_diagSize+"\nresolution: "+_resolution+"\nsaturated pixel: "+_saturatedPixel
					+"\nthreshold: "+_thresholdMax+"\n number of zero:"+_nbZero+"\n factor "+ _factOption+"\n");
			HicFileProcessing hfp =  new HicFileProcessing(_input, wga, _chrSize, _juiceBoxTools, _juiceBoXNormalisation);
				writer.write("java -jar noName hic "+_input+" "+_chrSizeFile+" "+_output+" "+_juiceBoxTools+" -norm "+ _juiceBoXNormalisation+" -g "+_gauss+
						" -min "+_min+" -max "+_max+" -mat "+_matrixSize+" -d "+_diagSize+" -res "+_resolution+" -sat "+_saturatedPixel+" -t "+_thresholdMax+" -nbZero "
						+_nbZero+" -factor "+ _factOption+" -del "+_delImages+"\n");
			hfp.run(_gui);
		}else if (_isProcessed){
			System.out.println("processed mode:\ninput: "+_input+"\noutput: "+_output+"\njuiceBox: "+_juiceBoxTools+"\nnorm: "+ _juiceBoXNormalisation+"\ngauss: "+_gauss
					+"\nmin: "+_min+"\nmax: "+_max+"\nmatrix size: "+_matrixSize+"\ndiag size: "+_diagSize+"\nresolution: "+_resolution+"\nsaturated pixel: "+_saturatedPixel
					+"\nthreshold: "+_thresholdMax+"\nisHic: "+_isHic+"\nisProcessed: "+_isProcessed+"\n number of zero:"
					+_nbZero+"\n factor "+ _factOption+"\n");
				writer.write("java -jar noName processed "+_input+" "+_chrSizeFile+" "+_output+" "+_gauss+" -mat "+_matrixSize+" -d "+_diagSize+" -res "+_resolution
						+" -t "+_thresholdMax+" -min "+_min+" -max "+_max+" -sat "+_saturatedPixel+" -nbZero "+_nbZero+" -factor "+ _factOption+" -del "+_delImages+"\n");
				if(_gui) wga.runGUI(_input);
				else wga.run(_input);
		}
		writer.close();		
		if(_delImages){
			System.out.println("Deleting image file");
			for(int i = 0; i< wga._tifList.size();++i)
				wga._tifList.get(i).delete();
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
			_chrSize.put(chr, size);
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
			boolean tresh = false;
			boolean norm = false;
			boolean  nbZero = false;
			boolean  gauss = false;
			boolean  min = false;
			boolean  max = false;
			boolean sat =false;
			for(int i = index; i < args.length;i+=2){
				if(args[i].equals("-res")){
					try{_resolution =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-res",args[i+1],"int");} 
				}else if(args[i].equals("-mat")){
					try{_matrixSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-mat",args[i+1],"int");} 
				}else if(args[i].equals("-factor")){
					int a  = Integer.parseInt(args[i+1]);
					_factOption = args[i+1];
					if(a == 1){
						_factor = new ArrayList<Integer>();
						_factor.add(1);
					}else if(a == 4){
						_factor = new ArrayList<Integer>();
						_factor.add(1);
						_factor.add(2);
						_factor.add(5);
					}else if(a == 3){
						_factor = new ArrayList<Integer>();
						_factor.add(1);
						_factor.add(5);
						
					}else if(a != 2)
						returnError("-mat",args[i+1],"int or not correct choice");
				}else if(args[i].equals("-d")){
					try{_diagSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");} 
				}else if(args[i].equals("-nbZero")){
					nbZero = true;
					try{_nbZero =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");} 
				}else if(args[i].equals("-g")){
					gauss = true;
					try{_gauss =Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-g",args[i+1],"double");}
				
				}else if(args[i].equals("-max")){
					max = true;
					try{_max = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-max",args[i+1],"double");}
					
				}else if(args[i].equals("-min")){
					min =  true;
					try{_min = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-min",args[i+1],"double");}
				
				}else if(args[i].equals("-sat")){
					sat = true;
					try{_saturatedPixel = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-sat",args[i+1],"double");}
				
				}else if(args[i].equals("-t")){
					tresh = true;
					try{_thresholdMax =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-t",args[i+1],"int");}
				
				}else if(args[i].equals("-norm")){
					norm = true;
					if(args[i+1].equals("NONE") || args[i+1].equals("VC") 
							|| args[i+1].equals("VC_SQRT") || args[i+1].equals("KR")){
						_juiceBoXNormalisation = args[i+1];
					}else{
						System.out.println("-norm = "+args[i+1]+", not defined\n");
						System.out.println(_doc);
						System.exit(0);
					}
				}else if(args[i].equals("-del")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						_delImages = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						_delImages = false;
					else{
						System.out.println("-del = "+args[i+1]+", not defined\n");
						System.out.println(_doc);
						System.exit(0);
					}
				}else if(args[i].equals("-hichip")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						_isHiChip = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						_isHiChip = false;
					else{
						System.out.println("-hichip = "+args[i+1]+", not defined\n");
						System.out.println(_doc);
						System.exit(0);
					}
				}else{
					System.out.println(args[i]+" doesn't existed\n");
					System.out.println(_doc);
					System.exit(0);
				}
			}
			if(_isHiChip){
				if(sat == false) _saturatedPixel = 0.5;
				if(norm == false) _juiceBoXNormalisation = "NONE";
				if(gauss == false) _gauss = 1.5;
				if(max == false) _max = 1;
				if(min == false) _min = 1;
				if(tresh == false) _thresholdMax = 1;
				if(nbZero == false ) _nbZero = 25;			
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
		System.out.println(_doc);
		System.exit(0);
	}
}
