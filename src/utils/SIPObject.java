package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Analyse and detect a whole genome HiC with .hic file or already processed data.
 * The class is used for the observed and oMe method.
 * 
 * MorpholibJ method used
 * 
 * Collection of mathematical morphology methods and plugins for ImageJ, created at the INRA-IJPB Modeling and Digital Imaging lab.
 * David Legland, Ignacio Arganda-Carreras, Philippe Andrey; MorphoLibJ: integrated library and plugins for mathematical morphology with ImageJ.
 * Bioinformatics 2016; 32 (22): 3532-3534. doi: 10.1093/bioinformatics/btw413
 * 
 * @author axel poulet
 *
 */
public class SIPObject {
	/** String path of the input data*/
	private String _input;
	/** Path of the output file*/
	private String _output;
	/** Strength of the gaussian filter*/
	private double _gauss;
	/** Strength of the min filter*/
	private double _min;
	/** Strength of the max filter*/
	private double _max;
	/** % of staurated pixel after enhance contrast*/
	private double _saturatedPixel;
	/** Image size*/
	private int _matrixSize = 0;
	/** Resolution of the bin dump in base*/
	private int _resolution;
	/** Threshold for the maxima detection*/
	private int _thresholdMaxima;
	/** HashMap of the chr size, the key = chr name, value = size of chr*/
	private HashMap<String,Integer> _chrSize =  new HashMap<String,Integer>();
	/** Diage size to removed maxima close to diagonal*/
	private int _diagSize;
	/** Size of the step to process each chr (step = matrixSize/2)*/
	private int _step;
	/** Number of pixel = 0 allowed around the loop*/
	private int _nbZero = -1;
	/** List of file containing the path of the image*/
	public ArrayList<File> _tifList = new ArrayList<File>();
	/** list of the image resolution to find loop*/
	private ArrayList<Integer> _listFactor = new ArrayList<Integer>();
	/**	boolean if true hichip data if false hic */
	private boolean _isHichip = false;
	/** fdr value */
	private double _fdr;
	/** is processed booelan*/
	private boolean _isProcessed = false;
	/** id is gui analysis*/
	private boolean _isGui = false;
	
	
	
	/**
	 * SIPObject constructor
	 * 
	 * @param output
	 * @param chrSize
	 * @param gauss
	 * @param min
	 * @param max
	 * @param resolution
	 * @param saturatedPixel
	 * @param thresholdMax
	 * @param diagSize
	 * @param matrixSize
	 * @param nbZero
	 * @param listFactor
	 * @param fdr
	 * @param isProcessed
	 * @param isHichip
	 */
	public SIPObject(String output, HashMap<String, Integer> chrSize, double gauss, double min,
			double max, int resolution, double saturatedPixel, int thresholdMax,
			int diagSize, int matrixSize, int nbZero,ArrayList<Integer> listFactor,
			double fdr, boolean isProcessed, boolean isHichip) {
		this._output = output;
		this._input = output;
		this._chrSize = chrSize;
		this._gauss = gauss;
		this._min = min;
		this._max = max;
		this._matrixSize = matrixSize;
		this._resolution = resolution;
		this._saturatedPixel = saturatedPixel;
		this._thresholdMaxima = thresholdMax;
		this._diagSize = diagSize;
		this._step = matrixSize/2;
		this._nbZero = nbZero;
		this._listFactor = listFactor;
		this._fdr = fdr;
		this._isProcessed = isProcessed;
		this._isHichip = isHichip;
	}

	/**
	 * SIPObject constructor
	 * @param input
	 * @param output
	 * @param chrSize
	 * @param gauss
	 * @param min
	 * @param max
	 * @param resolution
	 * @param saturatedPixel
	 * @param thresholdMax
	 * @param diagSize
	 * @param matrixSize
	 * @param nbZero
	 * @param listFactor
	 * @param fdr
	 * @param isProcessed
	 * @param isHichip
	 */
	public SIPObject(String input, String output, HashMap<String, Integer> chrSize, double gauss, double min,
			double max, int resolution, double saturatedPixel, int thresholdMax,
			int diagSize, int matrixSize, int nbZero,ArrayList<Integer> listFactor,
			double fdr, boolean isProcessed, boolean isHichip) {
		this._output = output;
		this._input = input;
		this._chrSize = chrSize;
		this._gauss = gauss;
		this._min = min;
		this._max = max;
		this._matrixSize = matrixSize;
		this._resolution = resolution;
		this._saturatedPixel = saturatedPixel;
		this._thresholdMaxima = thresholdMax;
		this._diagSize = diagSize;
		this._step = matrixSize/2;
		this._nbZero = nbZero;
		this._listFactor = listFactor;
		this._fdr = fdr;
		this._isProcessed = isProcessed;
		this._isHichip = isHichip;
	}
	
	/**
	 * Save the result file in tabulated file
	 * 
	 * @param pathFile String path for the results file
	 * @param first boolean to know idf it is teh first chromo
	 * @param data
	 * @throws IOException
	 */
	public void saveFile(String pathFile, HashMap<String,Loop> data, boolean first) throws IOException{
		FDR fdrDetection = new FDR ();
		fdrDetection.run(this._fdr, data);
		double RFDRcutoff = fdrDetection.getRFDRcutoff();
		double FDRcutoff = fdrDetection.getFDRcutoff();
		System.out.println("Filtering value at "+this._fdr+" FDR is "+FDRcutoff+" APscore and "+RFDRcutoff+" RegionalAPscore\n");
		BufferedWriter writer;
		if(first) writer = new BufferedWriter(new FileWriter(new File(pathFile), true));
		else{
			writer = new BufferedWriter(new FileWriter(new File(pathFile)));
			writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAvg\tProbabilityofEnrichment\tRegAPScoreAvg\tAvg_diffMaxNeihgboor_1\tAvg_diffMaxNeihgboor_2\tavg\tstd\tvalue\n");
		}
		Set<String> key = data.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String name = it.next();
			Loop loop = data.get(name);
			ArrayList<Integer> coord = loop.getCoordinates();
			if(loop.getPaScoreAvg()> 1.2 && loop.getPaScoreAvg() > 1 && loop.getPaScoreAvg() > FDRcutoff && loop.getRegionalPaScoreAvg() > RFDRcutoff && loop.getPaScoreAvgdev() > .9){
				writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
						+"\t"+loop.getPaScoreAvg()+"\t"+loop.getPaScoreAvgdev()+"\t"+loop.getRegionalPaScoreAvg()+"\t"
						+loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"
						+loop.getStd()+"\t"+loop.getValue()+"\n");
			}
		}
		writer.close();
	}

	/**
	 * Full the list with file in directory
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	
	public File[] fillList(String dir) throws IOException{
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
	

	/**
	 * Test the normalized vector by chromosme
	 * @param normFile
	 */
	public HashMap<Integer,String> testNormaVectorValue(String normFile){
		HashMap<Integer,String> normVector = new HashMap<Integer,String>();
		BufferedReader br;
		int lineNumber = 0;
		try {
			br = new BufferedReader(new FileReader(normFile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null){
				sb.append(line);
				if((line.equals("NaN")|| line.equals("NAN") || line.equals("nan") || line.equals("na")  || Double.parseDouble(line) < 0.30)){
					normVector.put(lineNumber*this._resolution, "plop");
				}
				++lineNumber;
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) { e.printStackTrace();}
		return normVector;
	}

	/**
	 * Getter of the input dir
	 * @return path of the input dir
	 */
	public String getinputDir(){ return this._input; }
	
	/**
	 * Getter of the matrix size
	 * 
	 * @return the size of the image
	 */
	public int getMatrixSize(){ return this._matrixSize; }

	
	/**
	 * Getter of step 
	 * @return the step
	 */
	public int getStep(){ return this._step;}
	
	/**
	 * Setter of the path of the input directory
	 * @param inputDir String of the input directory
	 */
	public void setInputDir(String inputDir){ this._input = inputDir; }

	/**
	 * Getter of the path of the output directory
	 * @return path 
	 */
	public String getOutputDir(){ return this._output; }

	/**
	 * Setter of the path of the output directory
	 * @param outputDir
	 */
	public void setOutputDir(String outputDir){	this._output = outputDir;}

	/**
	 * Getter of the gaussian blur strength
	 * @return double gaussian 
	 */
	public double getGauss(){ return this._gauss; }
	
	/**
	 * Setter of the gaussian blur strength
	 * @param gauss double
	 */
	public void setGauss(double gauss){ this._gauss = gauss; }
	
	/**
	 * Setter of the diagonal size
	 * @param diagSize int of the size of the diagonal
	 */
	public void setDiagSize(int diagSize){ 	this._diagSize = diagSize; }
	
	/**
	 * Getter of the min filter strength
	 * @return double strength of the min filter
	 */
	public double getMin(){ return this._min;}

	/**
	 * Setter of the min filter strength
	 * @param min
	 */
	public void setMin(double min){ this._min = min;}

	/**
	 * Getter of the max filter strength
	 * @return double max filter
	 */
	public double getMax(){	return this._max; }
	
	/**
	 * Setter of the min filter strength
	 * @param max
	 */
	public void setMax(double max){	this._max = max;}

	/**
	 * Getter % of saturated pixel for the contrast enhancement
	 * @return double percentage of saturated
	 */
	public double getSaturatedPixel(){ return this._saturatedPixel; }

	/**
	 * Setter % of saturated pixel for the contrast enhancement
	 * @param saturatedPixel
	 */
	public void setSaturatedPixel(double saturatedPixel){ this._saturatedPixel = saturatedPixel; }

	/**
	 * Getter of resolution of the bin 
	 * @return
	 */
	public int getResolution(){	return this._resolution;}
	
	/**
	 * Setter of resolution of the bin 
	 * @param resolution
	 */
	public void setResolution(int resolution){	this._resolution = resolution;}

	/**
	 * Setter of size of the matrix 
	 * @param size
	 */
	public void setMatrixSize(int size){ this._matrixSize = size; }
	
	/**
	 * setter step between image 
	 * @param step int step
	 */
	public void setStep(int step){ this._step = step;}
	
	/**
	 * Getter of threshold for the detction of the maxima 
	 * @return
	 */
	public int getThresholdMaxima(){ return _thresholdMaxima;}
	/**
	 * Getter of diagSize 
	 * @return
	 */
	public int getDiagSize(){ return this._diagSize;}
	
	/**
	 * Getter of getNbZero 
	 * @return
	 */
	public int getNbZero(){ return this._nbZero;}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Integer> getListFactor() {return this._listFactor;}
	
	public boolean isHichip(){return this._isHichip;}
	/**
	 * Setter of threshold for the detection of the maxima
	 * @param thresholdMaxima
	 */
	public void setThresholdMaxima(int thresholdMaxima) { this._thresholdMaxima = thresholdMaxima;}

	/**
	 * 
	 * @return
	 */
	public HashMap<String,Integer> getChrSizeHashMap(){return this._chrSize;} 
	
	/**
	 * Setter of hichip,
	 * false run with hic parameter
	 * true rune with hichip parameter 
	 * @param hichip boolean
	 */
	public void setIsHichip(boolean hichip){	this._isHichip = hichip;}

	/**
	 * 
	 * @return
	 */
	public boolean isProcessed() { return _isProcessed;}

	/**
	 * 
	 * @param _isProcessed
	 */
	public void setIsProcessed(boolean _isProcessed) { this._isProcessed = _isProcessed;}

	/**
	 * 
	 * @return
	 */
	public boolean isGui() { return _isGui;}
	
	/**
	 * 
	 * @param _isGui
	 */
			
	public void setIsGui(boolean _isGui) { this._isGui = _isGui;}
}