package plop.sipMain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import plop.gui.GuiAnalysis;
import plop.multiProcesing.ProcessCoolerDumpData;
import plop.multiProcesing.ProcessHicDumpData;
import plop.process.MultiResProcess;
import plop.utils.SIPObject;

import java.time.LocalTime; // import the LocalTime class




/**
 * 
 * command line eg:
 * java -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... paramaters
 * java -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar
 * 
 * @author axel poulet 
 *
 */
public class Hic_main {
	/** Path of input data directory if dumped or processed data else .hic for hic option*/
	private static String _input = "";
	/**	 Path of output if not existed it is created*/
	private static String _output = "";
	/** Path to the jucier_tools_box to dump the data not necessary for Processed and dumped method */
	private static String _juiceBoxTools = "";
	/** Path to the jucier_tools_box to dump the data not necessary for Processed and dumped method */
	private static String _cooler = "";
	/** Path to the jucier_tools_box to dump the data not necessary for Processed and dumped method */
	private static String _cooltools = "";
	/**Normalisation method to dump the data with hic method (KR,NONE.VC,VC_SQRT)*/
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
	private static int _diagonalSize = 6;
	/**Resolution of the matrix in bases*/
	private static int _resolution = 5000;
	/** % of saturated pixel in the image, allow the enhancement of the contrast in the image*/
	private static double _saturatedPixel = 0.01;
	/** Threshold to accept a maxima in the images as a loop*/
	private static int _thresholdMax = 2800;
	/**number of pixel = 0 allowed around the loop*/
	private static int _nbZero = 6;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isHic = true;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isCool = false;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isDroso = false;
	/** factor(s) used to analyse the matrix*/
	private static ArrayList<Integer> _factor = new ArrayList<Integer>();
	/** String factor option*/
	private static String _factOption = "1";
	/** if true run only image Processing step*/
	private static boolean _isProcessed = false;
	/** hash map stocking in key the name of the chr and in value the size*/
	private static HashMap<String,Integer> _chrSize =  new HashMap<String,Integer>();
	/** path to the chromosome size file */
	private static String _chrSizeFile;
	/**boolean is true supress all the image created*/
	private static boolean _delImages = true;
	/** double FDR value for filtering */
	private static double _fdr = 0.01;
	/** int number of cpu*/
	private static int _cpu = 1;
	/**boolean is true supress all the image created*/
	private static boolean _gui = false;
	private static String _logError = "";
	/**String for the documentation*/
	private static String _doc = ("#SIP Version 1 run with java 8\n"
			+ "\nUsage:\n"
			+ "\thic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
			+ "\tcool <mcoolFile> <chrSizeFile> <Output> <cooltoolsPath> <coolerPath> [options]\n"
			+ "\tprocessed <Directory with processed data> <chrSizeFile> <Output> [options]\n"
			+ "\nParameters:\n"
			+ "\t chrSizeFile: path to the chr size file, with the same name of the chr as in the hic file" +
			" (i.e. chr1 does not match Chr1 or 1)\n"
			+ "\t-res: resolution in bp (default 5000 bp)\n"
			+ "\t-mat: matrix size to use for each chunk of the chromosome (default 2000 bins)\n"
			+ "\t-d: diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at " +
			"5000 bp resolution removes all maxima"
			+ " detected at a distance inferior or equal to 10kb) (default 6 bins).\n"
			+ "\t-g: Gaussian filter: smoothing factor to reduce noise during primary maxima detection (default 1.5)\n"
			+ "\t-cpu: Number of CPU used for SIP processing (default 1)\n"
			+ "\t-factor: Multiple resolutions can be specified using:\n"
			+ "\t\t-factor 1: run only for the input res (default)\n"
			+ "\t\t-factor 2: res and res*2\n"
			+ "\t\t-factor 3: res and res*5\n"
			+ "\t\t-factor 4: res, res*2 and res*5\n"
			+ "\t-max: Maximum filter: increases the region of high intensity (default 2)\n"
			+ "\t-min: Minimum filter: removes the isolated high value (default 2)\n"
			+ "\t-sat: % of saturated pixel: enhances the contrast in the image (default 0.01)\n"
			+ "\t-t Threshold for loops detection (default 2800)\n"
			+ "\t-nbZero: number of zeros: number of pixels equal to zero that are allowed in the 24 pixels " +
			"surrounding the detected maxima (default 6)\n"
			+ "\t-norm: <NONE/VC/VC_SQRT/KR/SCALE> (default KR)\n"
			+ "\t-del: true or false, whether not to delete tif files used for loop detection (default true)\n"
			+ "\t-fdr: Empirical FDR value for filtering based on random sites (default 0.01)\n"
			+ "\t-isDroso: default false, if true apply extra filter to help detect loops similar to those found" +
			" in D. mel cells\n"
			+ "\t-h, --help print help\n"
			+ "\nCommand line eg:\n"
			+ "\tjava -jar SIP_HiC.jar processed inputDirectory pathToChromosome.size OutputDir .... parameters\n"
			+ "\tjava -jar SIP_HiC.jar hic inputDirectory pathToChromosome.size OutputDir juicer_tools.jar\n"
			+ "\nAuthors:\n"
			+ "Axel Poulet\n"
			+ "\tDepartment of Molecular, Cellular  and Developmental Biology Yale University 165 Prospect St\n"
			+ "\tNew Haven, CT 06511, USA\n"
			+ "M. Jordan Rowley\n"
			+ "\tDepartment of Genetics, Cell Biology and Anatomy, University of Nebraska Medical Center" +
			" Omaha,NE 68198-5805\n"
			+ "\nContact: pouletaxel@gmail.com OR jordan.rowley@unmc.edu");
			
	/**
	 * Main function to run all the process, can be run with plop.gui or in command line.
	 * With command line with 1 or less than 5 parameter => run only the help
	 * With zero parameter only java -jar SIP.jar  => plop.gui
	 * With more than 5 parameter => command line mode
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		_factor.add(1);
		if (args.length >= 1 && args.length < 4){
			System.out.println(_doc);
			System.exit(0);
		}else if(args.length >= 4){
			if (args[0].equals("hic") || args[0].equals("processed") || args[0].equals("cool")){
				_input = args[1];
				_output = args[3];
				_chrSizeFile = args[2];
				switch (args[0]) {
					case "hic":
						readOption(args, 5);
						_juiceBoxTools = args[4];
						break;
					case "processed":
						_isHic = false;
						_isProcessed = true;
						readOption(args, 4);
						break;
					case "cool":
						_isHic = false;
						_isProcessed = false;
						_isCool = true;
						_cooler = args[5];
						_cooltools = args[4];
						readOption(args, 6);
						break;
					default:

						break;
				}
			}else{
				System.out.println(args[0]+" not defined\n");
				System.out.println(_doc);
				return;
			}
						
		}else{////////////////////////////////////////GUI parameter initialisation
			GuiAnalysis gui = new GuiAnalysis();
			_gui =true;
			while( gui.isShowing()){
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
		    }	
			if (gui.isStart()){
				_isCool = gui.isCool();
				_chrSizeFile = gui.getChrSizeFile();
				_output = gui.getOutputDir();
				_input = gui.getRawDataDir();
				_matrixSize = gui.getMatrixSize();
				_diagonalSize = gui.getDiagSize();
				_resolution = gui.getResolution();
				_delImages = gui.isDeletTif();
				_gauss = gui.getGaussian();
				_max = gui.getMax();
				_min = gui.getMin();
				_isDroso= gui.isDroso();
				_nbZero = gui.getNbZero();
				_saturatedPixel = gui.getEnhanceSignal();
				_thresholdMax = gui.getNoiseTolerance();
				_fdr = gui.getFDR();
				_cpu = gui.getNbCpu();
				if(gui.getFactorChoice() == 2){
					_factor.add(2);
				}else if(gui.getFactorChoice() == 4){
					_factor.add(2);
					_factor.add(5);
				}else if(gui.getFactorChoice() == 3){
					_factor.add(5);
				}
				_isHic  = gui.isHic();
				_isProcessed = gui.isProcessed();
				_juiceBoxTools = gui.getJuiceBox();
				_cooltools = gui.getCooltools();
				_cooler = gui.getCooler();
				
				if(gui.isNONE()) _juiceBoXNormalisation = "NONE";
				else if (gui.isVC()) _juiceBoXNormalisation = "VC";
				else if (gui.isScale()) _juiceBoXNormalisation = "SCALE";
				else if (gui.isVC_SQRT()) _juiceBoXNormalisation = "VC_SQRT";				
			}else {
				System.out.println("SIP closed: if you want the help: -h");
				return;
			}
		}
		File f = new File(_input);
			
		if(!f.exists() && !_input.startsWith("https")){
				System.out.println(_input+" doesn't existed !!! \n\n");
				System.out.println(_doc);
				return;
		}
		
		f = new File(_chrSizeFile);
		if(!f.exists()){
				System.out.println(_chrSizeFile+" doesn't existed !!! \n\n");
				System.out.println(_doc);
				return;
		}

		
		SIPObject sip;
		readChrSizeFile(_chrSizeFile);
		LocalDateTime myObj = LocalDateTime.now();
		String date = myObj.toString().replaceAll(":","");
		date = date.replaceAll("\\.","_");
		if(_isHic){
			f = new File(_juiceBoxTools);
			if(!f.exists()){
				System.out.println(_juiceBoxTools+" doesn't existed !!! \n\n");
				System.out.println(_doc);
				return;
			}
			System.out.println("hic mode: \n"+ "input: "+_input+"\n"+ "output: "+_output+"\n"+ "juiceBox: "+
					_juiceBoxTools+"\n"+ "norm: "+ _juiceBoXNormalisation+"\n" + "gauss: "+_gauss+"\n"+ "min: "+
					_min+"\n"+ "max: "+_max+"\n"+ "matrix size: "+_matrixSize+"\n"+ "diag size: "+ _diagonalSize +"\n"+
					"resolution: "+_resolution+"\n" + "saturated pixel: "+_saturatedPixel+"\n"+ "threshold: "+
					_thresholdMax+"\n"+ "number of zero :"+_nbZero+"\n"+ "factor "+ _factOption+"\n"+ "fdr "+
					_fdr+"\n"+ "del "+_delImages+"\n"+ "cpu "+ _cpu+"\n-isDroso "+_isDroso+"\n");
			
			sip = new SIPObject(_output, _chrSize, _gauss, _min, _max, _resolution, _saturatedPixel,
					_thresholdMax, _diagonalSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			sip.setIsGui(_gui);
			ProcessHicDumpData processDumpData = new ProcessHicDumpData();
			processDumpData.go(_input, sip, _chrSize, _juiceBoxTools, _juiceBoXNormalisation, _cpu);
			System.out.println("########### End of the dump Step");
		}else if(_isCool){
			f = new File(_cooltools);
			if(!f.exists()){
				System.out.println(_cooltools+" doesn't existed or wrong path !!! \n\n");
				System.out.println(_doc);
				return;
			}
			f = new File(_cooler);
			if(!f.exists()){
				System.out.println(_cooler+" doesn't existed or wrong path !!! \n\n");
				System.out.println(_doc);
				return;
			}
			if( !testTools(_cooltools, 0, 3, 0) || !testTools(_cooler, 0, 8, 6)) {
				System.out.println( _cooltools +" or" + _cooler+" is not the good version for SIP (it needs cooltools " +
						"version >= 0.3.0 and cooler version >= 0.8.6) !!! \n\n");
				System.out.println(_doc);
				if(_gui){
					JOptionPane.showMessageDialog(null, "Error SIP program",
							_cooltools +" or" + _cooler+" is not the good version for SIP (it needs cooltools " +
									"version >= 0.3.0 and cooler version >= 0.8.6) !!!"
							 , JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			System.out.println("cool mode: \n"+ "input: "+_input+"\n"+ "output: "+_output+"\n"+"cooltools: "+
					_cooltools+"\n"+ "cooler: "+_cooler+"\n"+ "norm: "+ _juiceBoXNormalisation+"\n" + "gauss: "+
					_gauss+"\n"+ "min: "+_min+"\n"+ "max: "+_max+"\n"+ "matrix size: "+_matrixSize+"\n"+
					"diag size: "+ _diagonalSize +"\n"+ "resolution: "+_resolution+"\n"+ "saturated pixel: "+
					_saturatedPixel+ "\n"+ "threshold: "+_thresholdMax+"\n"+ "number of zero :"+_nbZero+"\n"+
					"factor "+ _factOption+ "\n"+ "fdr "+_fdr+"\n" + "del "+_delImages+"\n"+ "cpu "+ _cpu+
					"\n-isDroso "+_isDroso+"\n");
			sip = new SIPObject(_output, _chrSize, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax,
					_diagonalSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			sip.setIsCooler(_isCool);

			ProcessCoolerDumpData processDumpData = new ProcessCoolerDumpData();
			processDumpData.go(_cooltools, _cooler, sip, _input, _chrSize,_cpu);
			
			}else{
			System.out.println("processed mode:\n"+ "input: "+_input+"\n"+ "output: "+_output+"\n"+ "juiceBox: "+
					_juiceBoxTools+"\n" + "norm: "+ _juiceBoXNormalisation+"\n"+ "gauss: "+_gauss+"\n"+ "min: "+
					_min+"\n"+ "max: "+_max+"\n"+ "matrix size: "+_matrixSize+"\n" + "diag size: "+ _diagonalSize +
					"\n"+ "resolution: "+_resolution+"\n"+ "saturated pixel: "+_saturatedPixel+"\n"+ "threshold: "+
					_thresholdMax+"\n" + "isHic: "+_isHic+"\n"	+ "isProcessed: "+_isProcessed+"\n"+
					"number of zero:"+_nbZero+"\n"+ "factor "+ _factOption+"\n"+ "fdr "+_fdr+ "\n" + "del "
					+_delImages+"\n"+"cpu "+ _cpu+"\n-isDroso "+_isDroso+"\n");
			
			sip = new SIPObject(_input,_output, _chrSize, _gauss, _min, _max, _resolution, _saturatedPixel,
					_thresholdMax, _diagonalSize, _matrixSize, _nbZero,_factor,_fdr,_isProcessed, _isDroso);
			sip.setIsGui(_gui);
		}
		System.out.println("Start loop detection step");
		



		MultiResProcess multi = new MultiResProcess(sip, _cpu, _delImages,_chrSizeFile);
		multi.run();
		System.out.println("###########End loop detection step");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new
				File(_output+File.separator+"parameters_"+date+".txt"))
		);
		if(_isProcessed){
				writer.write("java -jar Sip_HiC.jar processed "+ _input+" "+ _chrSizeFile+" "+_output+" -g "+
						_gauss+" -mat "+_matrixSize+" -d "+ _diagonalSize +" -res "+_resolution+" -t "+_thresholdMax+
						" -min "+_min+" -max "+_max+" -sat "+_saturatedPixel+" -nbZero "+_nbZero+" -factor "+
						_factOption+" -fdr "+_fdr+" -del "+_delImages+" -cpu "+ _cpu+" -isDroso "+_isDroso+"\n");
		}else if(_isCool){
			writer.write("java -jar SIP_HiC.jar hic "+_input+" "+_chrSizeFile+" "+_output+" "+_cooltools+" "+
					_cooler+ " -g "+_gauss+" -min "+_min+" -max "+_max+" -mat "+_matrixSize+" -d "+ _diagonalSize +
					" -res "+_resolution+" -sat "+_saturatedPixel+" -t "+_thresholdMax+" -nbZero "+_nbZero+
					" -factor "+ _factOption+" -fdr "+_fdr+" -del "+_delImages+" -cpu "+ _cpu+" -isDroso "+
					_isDroso+"\n");
		}else{
			writer.write("java -jar SIP_HiC.jar hic "+_input+" "+_chrSizeFile+" "+_output+" "+_juiceBoxTools+
					" -norm "+ _juiceBoXNormalisation+" -g "+_gauss+" -min "+_min+" -max "+_max+" -mat "+_matrixSize+
					" -d "+ _diagonalSize +" -res "+_resolution+" -sat "+_saturatedPixel+" -t "+_thresholdMax+
					" -nbZero "+_nbZero+ " -factor "+ _factOption+" -fdr "+_fdr+" -del "+_delImages+" -cpu "+
					_cpu+" -isDroso "+_isDroso+"\n");
		}
		writer.close();	
		
		if(_gui){
			JOptionPane.showMessageDialog(null,"Results available: "+_output ,
					"End of SIP program", JOptionPane.INFORMATION_MESSAGE);
		}
		System.out.println("End of SIP loops are available in "+_output+"\n Bye!!! ");
		System.exit(0);
	}
	
	/**
	 * Run the input file and stock the info of name chr and their size in hashmap
	 * @param chrSizeFile path chr size file
	 * @throws IOException if file does not exist
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
	 * -cpu
	 * -del
	 * -fdr
	 * 
	 * @param args table of String stocking the arguments for the program
	 * @param index table index where start to read the arguments
	 * @throws IOException if some parameters don't exist
	 */
	private static void readOption(String args[], int index) throws IOException{
		if(index < args.length){
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
					if(a == 2){	_factor.add(2);}
					else if(a == 4){
						_factor.add(2);
						_factor.add(5);
					}else if(a == 3){ _factor.add(5);}
					else if(a != 1)	returnError("-factor ",args[i+1]," int or not" +
							" correct choice (1, 2, 3, 4)");
				}else if(args[i].equals("-d")){
					try{
						_diagonalSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");}
				}else if(args[i].equals("-cpu")){
						try{_cpu =Integer.parseInt(args[i+1]);}
						catch(NumberFormatException e){ returnError("-cpu",args[i+1],"int");}
						if(_cpu > Runtime.getRuntime().availableProcessors() || _cpu <= 0){
							System.out.println("the number of CPU "+ _cpu+" is superior of the " +
									"server/computer' cpu "+Runtime.getRuntime().availableProcessors()+"\n");
							System.out.println(_doc);
							System.exit(0);
						}
				}else if(args[i].equals("-nbZero")){
					try{_nbZero =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");} 
				}else if(args[i].equals("-g")){
					try{_gauss =Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-g",args[i+1],"double");}
				}else if(args[i].equals("-fdr")){
					try{_fdr =Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-fdr",args[i+1],"double");}				
				}else if(args[i].equals("-max")){
					try{_max = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-max",args[i+1],"double");}
					
				}else if(args[i].equals("-min")){
					try{_min = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-min",args[i+1],"double");}
				}else if(args[i].equals("-sat")){
					try{_saturatedPixel = Double.parseDouble(args[i+1]);}
					catch(NumberFormatException e){ returnError("-sat",args[i+1],"double");}
				}else if(args[i].equals("-t")){
					try{_thresholdMax =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-t",args[i+1],"int");}
				}else if(args[i].equals("-norm")){
					if(args[i+1].equals("NONE") || args[i+1].equals("VC") 
							|| args[i+1].equals("VC_SQRT") || args[i+1].equals("KR") || args[i+1].equals("SCALE")){
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
				}else if(args[i].equals("-isDroso")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						_isDroso = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						_isDroso = false;
					else{
						System.out.println("-_isDroso = "+args[i+1]+", not defined\n");
						System.out.println(_doc);
						System.exit(0);
					}
				}else{
					System.out.println(args[i]+" doesn't existed\n");
					System.out.println(_doc);
					System.exit(0);
				}
			}
		}
	}
	
	/**
	 * Return specific error on function of the argument
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
	
	
	public static boolean testTools(String pathTools, int first, int second, int third) {
		Runtime runtime = Runtime.getRuntime();
		String cmd = pathTools+" --version";
		Process process;
		try {
			process = runtime.exec(cmd);
	
		new ReturnFlux(process.getInputStream()).start();
		new ReturnFlux(process.getErrorStream()).start();
		process.waitFor();
		
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] tline = _logError.split(" ");
		System.out.println(_logError);
		_logError = "";
		if(tline.length > 0){
			tline = tline[tline.length-1].split("\\.");
			tline[2] = tline[2].replace("\n", "");
			if(Integer.parseInt(tline[0]) >= first && Integer.parseInt(tline[1]) >= second)
				//&& Integer.parseInt(tline[2]) >= third)
				return true;
			else
				return false;
		}else
			return false;
	}
	
	public static class ReturnFlux extends Thread {  

		/**  Flux to redirect  */
		private InputStream _flux;

		/**
		 * <b>Constructor of ReturnFlux</b>
		 * @param flux
		 *  flux to redirect
		 */
		public ReturnFlux(InputStream flux){this._flux = flux; }
		
		/**
		 * 
		 */
		public void run(){
			try {    
				InputStreamReader reader = new InputStreamReader(this._flux);
				BufferedReader br = new BufferedReader(reader);
				String line=null;
				while ( (line = br.readLine()) != null) {
					if(line.contains("WARN")== false) _logError = _logError+line+"\n";
				}
			}
			catch (IOException ioe){
				ioe.printStackTrace();
			}
		}		
	}
}
