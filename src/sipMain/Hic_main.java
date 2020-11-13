package sipMain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cli.CLISipOption;
import gui.GuiAnalysis;
import multiProcesing.ProcessCoolerDumpData;
import multiProcesing.ProcessDumpData;
import process.HiC;
import process.MCool;
import process.Processed;
import utils.MultiResProcess;
import sip.SIPIntra;

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
	private static int _diagSize = 6;
	/**Resolution of the matric in bases*/
	private static int _resolution = 5000;
	/** % of saturated pixel in the image, allow the enhancement of the contrast in the image*/
	private static double _saturatedPixel = 0.01;
	/** Threshold to accepet a maxima in the images as a loop*/
	private static int _thresholdMax = 2800;
	/**number of pixel = 0 allowed around the loop*/
	private static int _nbZero = 6;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isHic = true;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isCool = false;
	/** boolean if true run all the process (dump data + image +image processing*/
	private static boolean _isDroso = false;
	/** factor(s) used to nalyse the matrix*/
	private static ArrayList<Integer> _factor = new ArrayList<Integer>();
	/** String factor option*/
	private static String _factOption = "1";
	/** if true run only image Processing step*/
	private static boolean _isProcessed = false;
	/** hash map stocking in key the name of the chr and in value the size*/
//	private static HashMap<String,Integer> _chrSize =  new HashMap<String,Integer>();
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


			
	/**
	 * Main function to run all the process, can be run with gui or in command line.
	 * With command line with 1 or less than 5 parameter => run only the help
	 * With zero parameter only java -jar SIP.jar  => gui
	 * With more than 5 paramter => command line mode
	 * 
	 * @param args table with parameters for command line
	 * @throws IOException  exception
	 * @throws InterruptedException exception
	 */
	public static void main(String[] args) throws Exception {



		_factor.add(1);
		if(args.length >= 1) {
			if (args[0].equals("hic")) {
				HiC hic = new HiC(args);
				hic.run();

			} else if (args[0].equals("processed")) {
				Processed processed = new Processed(args);
			} else if (args[0].equals("cool")) {
				MCool mCool = new MCool(args);

			} else {
				System.out.println(CLISipOption.getHelperInfos());
			}
		}
/*				_input = args[1];
				_output = args[3];
				_chrSizeFile = args[2];	
				if (args[0].equals("hic")){
					readOption(args,5);
					_juiceBoxTools = args[4];
				}else if(args[0].equals("processed")){
					_isHic = false;
					_isProcessed = true;
					readOption(args,4);
				}else if(args[0].equals("cool")){
					_isHic = false;
					_isProcessed = false;
					_isCool = true;
					_cooler = args[5];
					_cooltools = args[4];
					readOption(args,6);
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
				_diagSize = gui.getDiagSize();
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
				else if (gui.isVC_SQRT()) _juiceBoXNormalisation = "VC_SQRT";				
			}else {
				System.out.println("SIP closed: if you want the help: -h");
				return;
			}
		}
		File f = new File(_input);
			
		if(!f.exists() && !_input.startsWith("https")){
				System.out.println(_input+" doesn't existed !!! \n\n");
				SIPIntra.docError();
				return;
		}
		
		f = new File(_chrSizeFile);
		if(!f.exists()){
				System.out.println(_chrSizeFile+" doesn't existed !!! \n\n");
				SIPIntra.docError();
				return;
		}

		
		SIPIntra sip;
		if(_isHic){
			f = new File(_juiceBoxTools);
			if(!f.exists()){
				System.out.println(_juiceBoxTools+" doesn't existed !!! \n\n");
				SIPIntra.docError();
				return;
			}
			System.out.println("hic mode: \n"+ "input: "+_input+"\n"+ "output: "+_output+"\n"+ "juiceBox: "+_juiceBoxTools+"\n"+ "norm: "+ _juiceBoXNormalisation+"\n"
					+ "gauss: "+_gauss+"\n"+ "min: "+_min+"\n"+ "max: "+_max+"\n"+ "matrix size: "+_matrixSize+"\n"+ "diag size: "+_diagSize+"\n"+ "resolution: "+_resolution+"\n"
					+ "saturated pixel: "+_saturatedPixel+"\n"+ "threshold: "+_thresholdMax+"\n"+ "number of zero :"+_nbZero+"\n"+ "factor "+ _factOption+"\n"+ "fdr "+_fdr+"\n"
					+ "del "+_delImages+"\n"+ "cpu "+ _cpu+"\n-isDroso "+_isDroso+"\n");
			
			sip = new SIPIntra(_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel,
					_thresholdMax, _diagSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			sip.setIsGui(_gui);
			ProcessDumpData processDumpData = new ProcessDumpData();
			processDumpData.go(_input, sip, _juiceBoxTools, _juiceBoXNormalisation, _cpu);
			System.out.println("########### End of the dump Step");
		}else if(_isCool){
			f = new File(_cooltools);
			if(!f.exists()){
				System.out.println(_cooltools+" doesn't existed or wrong path !!! \n\n");
				SIPIntra.docError();
				return;
			}
			f = new File(_cooler);
			if(!f.exists()){
				System.out.println(_cooler+" doesn't existed or wrong path !!! \n\n");
				SIPIntra.docError();
				return;
			}
			if(!testTools(_cooltools, 0, 3, 0) || !testTools(_cooler, 0, 8, 6)) {
				System.out.println( _cooltools +" or" + _cooler+" is not the good version for SIP (it needs cooltools version >= 0.3.0 and cooler version >= 0.8.6) !!! \n\n");
				SIPIntra.docError();
				if(_gui){
					JOptionPane.showMessageDialog(null, "Error SIP program", _cooltools +" or" + _cooler+" is not the good version for SIP (it needs cooltools version >= 0.3.0 and cooler version >= 0.8.6) !!!"
							 , JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			System.out.println("cool mode: \n"+ "input: "+_input+"\n"+ "output: "+_output+"\n"+"cooltools: "+_cooltools+"\n"+ "cooler: "+_cooler+"\n"+ "norm: "+ _juiceBoXNormalisation+"\n"
					+ "gauss: "+_gauss+"\n"+ "min: "+_min+"\n"+ "max: "+_max+"\n"+ "matrix size: "+_matrixSize+"\n"+ "diag size: "+_diagSize+"\n"+ "resolution: "+_resolution+"\n"
					+ "saturated pixel: "+_saturatedPixel+"\n"+ "threshold: "+_thresholdMax+"\n"+ "number of zero :"+_nbZero+"\n"+ "factor "+ _factOption+"\n"+ "fdr "+_fdr+"\n"
					+ "del "+_delImages+"\n"+ "cpu "+ _cpu+"\n-isDroso "+_isDroso+"\n");
			sip = new SIPIntra(_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax, _diagSize, _matrixSize, _nbZero, _factor,_fdr, _isProcessed,_isDroso);
			sip.setIsCooler(_isCool);

			ProcessCoolerDumpData processDumpData = new ProcessCoolerDumpData();
			processDumpData.go(_cooltools, _cooler, sip, _input, _cpu);
			
			}else{
			System.out.println("processed mode:\n"+ "input: "+_input+"\n"+ "output: "+_output+"\n"+ "juiceBox: "+_juiceBoxTools+"\n"
					+ "norm: "+ _juiceBoXNormalisation+"\n"+ "gauss: "+_gauss+"\n"+ "min: "+_min+"\n"+ "max: "+_max+"\n"+ "matrix size: "+_matrixSize+"\n"
					+ "diag size: "+_diagSize+"\n"+ "resolution: "+_resolution+"\n"+ "saturated pixel: "+_saturatedPixel+"\n"+ "threshold: "+_thresholdMax+"\n"
					+ "isHic: "+_isHic+"\n"	+ "isProcessed: "+_isProcessed+"\n"+ "number of zero:"+_nbZero+"\n"+ "factor "+ _factOption+"\n"+ "fdr "+_fdr+ "\n"
					+ "del "+_delImages+"\n"+"cpu "+ _cpu+"\n-isDroso "+_isDroso+"\n");
			
			sip = new SIPIntra(_input,_output, _chrSizeFile, _gauss, _min, _max, _resolution, _saturatedPixel, _thresholdMax,
					_diagSize, _matrixSize, _nbZero,_factor,_fdr);
			sip.setIsDroso(_isDroso);
			sip.setIsProcessed(_isProcessed);
			sip.setIsGui(_gui);
		}
		System.out.println("Start loop detction step");
		
		MultiResProcess multi = new MultiResProcess(sip, _cpu, _delImages,_chrSizeFile);
		multi.run();
		System.out.println("###########End loop detction step");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_output+File.separator+"parameters.txt")));
		if(_isProcessed){
				writer.write("java -jar Sip_HiC.jar processed "+ _input+" "+ _chrSizeFile+" "+_output+" -g "+_gauss+" -mat "+_matrixSize+" -d "+_diagSize
					+" -res "+_resolution+" -t "+_thresholdMax+" -min "+_min+" -max "+_max+" -sat "+_saturatedPixel+" -nbZero "+_nbZero
					+" -factor "+ _factOption+" -fdr "+_fdr+" -del "+_delImages+" -cpu "+ _cpu+" -isDroso "+_isDroso+"\n");
			
		}else if(_isCool){
			writer.write("java -jar SIP_HiC.jar hic "+_input+" "+_chrSizeFile+" "+_output+" "+_cooltools+" "+_cooler+
					" -g "+_gauss+" -min "+_min+" -max "+_max+" -mat "+_matrixSize+
					" -d "+_diagSize+" -res "+_resolution+" -sat "+_saturatedPixel+" -t "+_thresholdMax+" -nbZero "+_nbZero+
					" -factor "+ _factOption+" -fdr "+_fdr+" -del "+_delImages+" -cpu "+ _cpu+" -isDroso "+_isDroso+"\n");
		}else{
			writer.write("java -jar SIP_HiC.jar hic "+_input+" "+_chrSizeFile+" "+_output+" "+_juiceBoxTools+
					" -norm "+ _juiceBoXNormalisation+" -g "+_gauss+" -min "+_min+" -max "+_max+" -mat "+_matrixSize+
					" -d "+_diagSize+" -res "+_resolution+" -sat "+_saturatedPixel+" -t "+_thresholdMax+" -nbZero "+_nbZero+
					" -factor "+ _factOption+" -fdr "+_fdr+" -del "+_delImages+" -cpu "+ _cpu+" -isDroso "+_isDroso+"\n");
		}
		writer.close();	
		
		if(_gui){
			JOptionPane.showMessageDialog(null,"Results available: "+_output , "End of SIP program", JOptionPane.INFORMATION_MESSAGE);
		}
		System.out.println("End of SIP loops are available in "+_output);*/
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
					else if(a != 1)	returnError("-factor ",args[i+1]," int or not correct choice (1, 2, 3, 4)");
				}else if(args[i].equals("-d")){
					try{_diagSize =Integer.parseInt(args[i+1]);}
					catch(NumberFormatException e){ returnError("-d",args[i+1],"int");}
				}else if(args[i].equals("-cpu")){
						try{_cpu =Integer.parseInt(args[i+1]);}
						catch(NumberFormatException e){ returnError("-cpu",args[i+1],"int");}
						if(_cpu > Runtime.getRuntime().availableProcessors() || _cpu <= 0){
							System.out.println("the number of CPU "+ _cpu+" is superior of the server/computer' cpu "+Runtime.getRuntime().availableProcessors()+"\n");
							SIPIntra.docError();
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
							|| args[i+1].equals("VC_SQRT") || args[i+1].equals("KR")){
						_juiceBoXNormalisation = args[i+1];
					}else{
						System.out.println("-norm = "+args[i+1]+", not defined\n");
						SIPIntra.docError();
						System.exit(0);
					}
				}else if(args[i].equals("-del")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						_delImages = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						_delImages = false;
					else{
						System.out.println("-del = "+args[i+1]+", not defined\n");
						SIPIntra.docError();
						System.exit(0);
					}
				}else if(args[i].equals("-isDroso")){
					if(args[i+1].equals("true") || args[i+1].equals("T") || args[i+1].equals("TRUE"))
						_isDroso = true;
					else if(args[i+1].equals("false") || args[i+1].equals("F") || args[i+1].equals("False"))
						_isDroso = false;
					else{
						System.out.println("-_isDroso = "+args[i+1]+", not defined\n");
						SIPIntra.docError();
						System.exit(0);
					}
				}else{
					System.out.println(args[i]+" doesn't existed\n");
					SIPIntra.docError();
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
		SIPIntra.docError();
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
			if(Integer.parseInt(tline[0]) >= first && Integer.parseInt(tline[1]) >= second) //&& Integer.parseInt(tline[2]) >= third)
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
