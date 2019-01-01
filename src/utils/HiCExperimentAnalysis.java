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

import gui.Progress;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel;

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
public class HiCExperimentAnalysis {
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
	/** loop coolection*/
	private static HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	/** raw or line with biais value in the hic matrix*/
	private static HashMap<Integer,String> _normVector = new HashMap<Integer,String>();
	/** list of the image resolution to find loop*/
	private ArrayList<Integer> _listFactor = new ArrayList<Integer>();
	/**	 struturing element for the MM method used (MorpholibJ)*/
	private Strel m_strel = Strel.Shape.SQUARE.fromRadius(40);
	/**	boolean if true hichip data if false hic */
	private boolean m_hichip = false;
	/**	 image background value*/
	private int _backgroudValue = 1;
	
	
	/**
	 * WholeGenomeAnalysis constructor
	 * 
	 * @param output: String path of the results 
	 * @param chrSize: HashMap<String, Integer> hashmap with the chr size info
	 * @param gauss: double of the strength of the gaussian filter
	 * @param min: double of the strength of the min filter
	 * @param max: double of the strength of the max filter
	 * @param resolution: int size of the bins
	 * @param saturatedPixel: % of staurated pixel after enhance contrast
	 * @param thresholdMax: threshold for the maxima detection 
	 * @param diagSize: size of the diag
	 * @param matrixSize: size of the image to analyse
	 */
	 
	public HiCExperimentAnalysis(String output, HashMap<String, Integer> chrSize, double gauss, double min,
			double max, int resolution, double saturatedPixel, int thresholdMax,
			int diagSize, int matrixSize, int nbZero,ArrayList<Integer> listFactor) {
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
	}

	/**
	 * run the whole genome analysis to detect the maxima
	 * @throws IOException
	 */
	public void run() throws IOException{
		Iterator<String> key = this._chrSize.keySet().iterator();
		String resuFile = this._output+File.separator+"loops.bedpe";
		int nb = 0;
		while(key.hasNext()){
			String chr = key.next();
			String dir = this._output+File.separator+chr+File.separator;
			File[] listOfFiles = fillList(dir);
			System.out.println(listOfFiles.length);
			this.testNormaVectorValue(this._output+File.separator+"normVector"+File.separator+chr+".norm");
			System.out.println("normVector end loading file: "+chr+".norm");
			if (listOfFiles.length == 0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				detectLoops(listOfFiles,chr);
				if(nb == 0)		saveFile(resuFile, false);
				else saveFile(resuFile, true);
			}
			++nb;
		}
	}
	
	/**
	 * run the whole genome analysis to detect the maxima with the GUI
	 * 
	 * @throws IOException
	 */
	public void runGUI() throws IOException{
		Iterator<String> key = this._chrSize.keySet().iterator();
		String resuFile = this._output+File.separator+"loops.bedpe";
		int nb = 0;
		Progress p = new Progress("Loops detection",_chrSize.size());
		p.bar.setValue(nb);
		while(key.hasNext()){
			String chr = key.next();
			String dir = this._output+File.separator+chr+File.separator;
			File[] listOfFiles = fillList(dir);
			this.testNormaVectorValue(this._output+File.separator+"normVector"+File.separator+chr+".norm");
			System.out.println("normVector end loading file: "+chr+".norm");
			if (listOfFiles.length == 0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				detectLoops(listOfFiles,chr);
				if(nb == 0)		saveFile(resuFile, false);
				else saveFile(resuFile, true);
			}
			++nb;
			p.bar.setValue(nb);
		}
	}

	/**
	 * run the whole genome analysis to detect the maxima with processed data set
	 * 
	 * @param input path of the input file
	 * @throws IOException
	 */
	public void run(String input) throws IOException{
		Iterator<String> key = this._chrSize.keySet().iterator();
		String resuFile = this._output+File.separator+"loops.bedpe";
		int nb = 0;
		while(key.hasNext()){
			String chr = key.next();
			String dir = input+File.separator+chr+File.separator;
			File[] listOfFiles =fillList(dir);
			System.out.println(dir);
			this.testNormaVectorValue(input+File.separator+"normVector"+File.separator+chr+".norm");
			System.out.println("normVector end loading file: "+chr+".norm");
			if (listOfFiles.length == 0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				detectLoops(listOfFiles,chr);
				if (nb == 0) saveFile(resuFile,false);
				else saveFile(resuFile,true);
			}
			++nb;
		}
	}
	
	/**
	 * run the whole genome analysis to detect the maxima in processed dat
	 * run prog with teh GUI
	 * 
	 * @param input path of the input file
	 * @throws IOException
	 */
	public void runGUI(String input) throws IOException{
		Iterator<String> key = this._chrSize.keySet().iterator();
		String resuFile = this._output+File.separator+"loops.bedpe";
		int nb = 0;
		Progress p = new Progress("Loops detection",_chrSize.size());
		p.bar.setValue(nb);
		while(key.hasNext()){
			String chr = key.next();
			String dir = input+File.separator+chr+File.separator;
			File[] listOfFiles =fillList(dir);
			System.out.println(dir);
			this.testNormaVectorValue(input+File.separator+"normVector"+File.separator+chr+".norm");
			System.out.println("normVector end loading file: "+chr+".norm");
			if (listOfFiles.length == 0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				detectLoops(listOfFiles,chr);
				if (nb == 0) saveFile(resuFile,false);
				else saveFile(resuFile,true);
			}
			++nb;
			p.bar.setValue(nb);
		}		
	}
	
	/**
	 * Save the result file in tabulated file
	 * 
	 * @param pathFile String path for the results file
	 * @param first boolean to know idf it is teh first chromo
	 * @throws IOException
	 */
	private void saveFile(String pathFile, boolean first) throws IOException{
		BufferedWriter writer;
		if(first)
			writer = new BufferedWriter(new FileWriter(new File(pathFile), true));
		else{
			writer = new BufferedWriter(new FileWriter(new File(pathFile)));
			writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAvg\tRegAPScoreAvg\tAPScoreMed\tRegAPScoreMed\tAvg_diffMaxNeihgboor_1\tAvg_diffMaxNeihgboor_2\tavg\tstd\tvalue\n");
		}
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
					+"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()+"\t"+loop.getPaScoreMed()+"\t"+loop.getRegionalPaScoreMed()
					+"\t"+loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"+loop.getStd()+"\t"+loop.getValue()+"\n");
		}
		writer.close();
	}
	
	
	/**
	 * Make Image 
	 * 
	 * @param file path 
	 * @return ImagePlus with oMe  value
	 */
	private ImagePlus doImage(String file){	
		TupleFileToImage readFile = new TupleFileToImage(file,this._matrixSize,this._resolution);
		readFile.readTupleFile();
		saveFile(readFile.getNormImage(),file.replaceAll(".txt", "_N.tif"));
		ImagePlus imageOutput = readFile.getRawImage();
		imageOutput.setTitle(file.replaceAll(".txt", ".tif"));
		saveFile(imageOutput,file.replaceAll(".txt", ".tif"));
		return imageOutput;
	}
	
	
	/**
	 * Detect loops methods
	 * detect the loops at two different resolution, initial resolution + 2 fold bigger
	 * call the loops first in the smaller resolution 
	 * then making image with bigger resolution and fill no Zero list
	 * faire un gros for deguelasse por passer les faceteur de grossissement seulement si listDefacteur > 1.
	 * make and save image at two differents resolution (m_resolution and m_resolution*2)
	 * if there is a lot pixel at zero in the images adapt the threshold for the maxima detection
	 * @param fileList list fo the tuple file
	 * @param chr name of the chr
	 * @throws IOException
	 */
	private void detectLoops(File[] fileList, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection();
		HashMap<String,Loop> hLoop= new HashMap<String,Loop>();
		for(int i = 0; i < fileList.length; ++i){
			if(fileList[i].toString().contains(".txt")){
				this._backgroudValue = 1; 
				String[] tfile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tfile[tfile.length-2])/(this._step*this._resolution);
				System.out.println(numImage+" "+fileList[i]);
				ImagePlus imgRaw = doImage(fileList[i].toString());
				ImagePlus imgFilter = imgRaw.duplicate();
				TupleFileToImage.correctImage(imgFilter);
				ImagePlus imgCorrect = imgFilter.duplicate();
				ProcessMethod m = new ProcessMethod(imgFilter,this._min,this._max,this._gauss);
				imageProcessing(imgFilter,fileList[i].toString(), m);
				this._tifList.add(new File(imgRaw.getTitle()));
				imgRaw.getTitle().replaceAll(".tif", "_N.tif");
				this._tifList.add(new File(imgRaw.getTitle().replaceAll(".tif", "_N.tif")));
				ImagePlus imgNorm = IJ.openImage(imgRaw.getTitle().replaceAll(".tif", "_N.tif"));
				
				int thresh = this._thresholdMaxima;
				double pixelPercent = 100*TupleFileToImage._noZeroPixel/(this._matrixSize*this._matrixSize);
				if(pixelPercent <= 5) 
					thresh =  _thresholdMaxima/10;
				
				FindMaxima findLoop = new FindMaxima(imgNorm, imgFilter, chr, thresh, this._diagSize, this._resolution);
				HashMap<String,Loop> temp = findLoop.findloop(this.m_hichip,numImage, this._nbZero,imgRaw, this._backgroudValue);
				PeakAnalysisScore pas = new PeakAnalysisScore(imgNorm,temp);
				pas.computeScore();
				
				if (this._listFactor.size() > 1){
					for (int j = 1; j < this._listFactor.size(); ++j ){
						//this._backgroudValue = this._listFactor.get(j);
						ChangeImageRes test =  new ChangeImageRes(imgCorrect, this._listFactor.get(j));
						ImagePlus imgRawBiggerRes = test.run();
						
						test =  new ChangeImageRes(imgNorm,  this._listFactor.get(j));
						ImagePlus imgRawBiggerResNorm = test.run();
						
						saveFile(imgRawBiggerRes,fileList[i].toString().replaceAll(".txt", "_"+this._listFactor.get(j)+".tif")); 
						saveFile(imgRawBiggerResNorm,fileList[i].toString().replaceAll(".txt", "_"+this._listFactor.get(j)+"_N.tif"));
						this._tifList.add(new File(fileList[i].toString().replaceAll(".txt", "_"+this._listFactor.get(j)+".tif")));
						this._tifList.add(new File(fileList[i].toString().replaceAll(".txt", "_"+this._listFactor.get(j)+"_N.tif")));
											
						ImagePlus imgFilterBiggerRes = imgRawBiggerRes.duplicate();
						m = new ProcessMethod(imgFilterBiggerRes,this._min/this._listFactor.get(j),this._max/this._listFactor.get(j),this._gauss);
						imageProcessing(imgFilterBiggerRes, fileList[i].toString().replaceAll(".txt", "_"+this._listFactor.get(j)+".tif"), m);
			
						int diag = this._diagSize/this._listFactor.get(j);
						int res = _resolution*_listFactor.get(j);
						if (diag < 2)
							diag = 2 ;
						findLoop = new FindMaxima(imgRawBiggerResNorm, imgFilterBiggerRes, chr,thresh, diag, res);
						HashMap<String,Loop>tempBiggerRes = findLoop.findloop(this.m_hichip,numImage,(int)this._nbZero/this._listFactor.get(j)-1,imgRawBiggerRes,this._backgroudValue);
						pas = new PeakAnalysisScore(imgRawBiggerResNorm,tempBiggerRes);
						pas.computeScore();
						temp.putAll(tempBiggerRes);
					}
				}
				temp = removedBadLoops(temp);
				coord.setData(hLoop);
				hLoop = coord.imageToGenomeCoordinate(temp, numImage);
			}
		}
		m_data = removedLoopCloseToWhiteStrip(hLoop);
		System.out.println("chr "+ chr +"\t"+m_data.size());
	}
	
	/**
	 * Removed loop close to white  strip
	 * 
	 * @param hLoop loop collection before correction of the loops
	 * @return loop collection sfter correction of the loops
	 */
	private HashMap<String,Loop> removedLoopCloseToWhiteStrip(HashMap<String,Loop> hLoop){
		Set<String> key = hLoop.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			String name = it.next();
			Loop loop = hLoop.get(name);
			Boolean testRemoved = removedVectoNorm(loop);
			boolean testBreak = false;
			if(testRemoved)
				removed.add(name);
			else{
				String [] tname = name.split("\t");
				int x = Integer.parseInt(tname[1]);
				int y = Integer.parseInt(tname[2]);
				for(int i = x-5*this._resolution; i <= x+5*this._resolution; i+=this._resolution){
					for(int j = y-5*this._resolution; j <= y+5*this._resolution; j+=this._resolution){
						String test = tname[0]+"\t"+i+"\t"+j;
						if(!test.equals(name)){
							if(hLoop.containsKey(test)){
								if(hLoop.get(test).getResolution() < hLoop.get(name).getResolution()){
									removed.add(name);
									testBreak =true;
									break;
								}else if(hLoop.get(test).getResolution() == hLoop.get(name).getResolution()){
									if((Math.abs(x-hLoop.get(test).getX()) < this._resolution*3 || Math.abs(y-hLoop.get(test).getY()) < this._resolution*3)){
										if(hLoop.get(test).getAvg() > hLoop.get(name).getAvg()){
											removed.add(name);
											testBreak =true;
											break;
										}else if(hLoop.get(test).getAvg() < hLoop.get(name).getAvg())
											removed.add(test);
										else{
											if(hLoop.get(test).getPaScoreAvg() > hLoop.get(name).getPaScoreAvg()){
												removed.add(name);
												testBreak =true;
												break;
											}else removed.add(test);
										}
									}
								}else removed.add(test);
							}
						}
					}
					if(testBreak) break;
				}
			}
		}
		for (int i = 0; i< removed.size(); ++i)
			hLoop.remove(removed.get(i));
		return hLoop;
	}
	
	
	
	/**
	 * Removed loops close to biased HiC signal
	 * @param loop Loop to test
	 * @return boolean true if loop have to be removed else false
	 */
	private boolean removedVectoNorm(Loop loop){
		boolean test = false;
		int x = loop.getCoordinates().get(0);
		int y = loop.getCoordinates().get(2);
		if(loop.getResolution() == this._resolution){
			if(_normVector.containsKey(x) || _normVector.containsKey(y))
				test = true;
		}
		else if(loop.getResolution() == this._resolution*2){
			if(_normVector.containsKey(x) || _normVector.containsKey(y) || _normVector.containsKey(x+_resolution) || _normVector.containsKey(y+_resolution))
				test = true;
		}
		else if(loop.getResolution() == this._resolution*5){
			for(int i = x; i <= x+5*this._resolution; i+=this._resolution){
				if(_normVector.containsKey(i)){
					test = true;
					break;
				}
				for(int j = y; j <= y+5*this._resolution; j+=this._resolution){
					if(_normVector.containsKey(j)){
						test = true;
						break;
					}
				}		
			}
		}
		return test;
	}

	/**
	 * Remove loops which doesn't respect the rule
	 * 
	 * @param input loop collection before correction
	 * @return loop collection after correction
	 */
	private HashMap<String, Loop>  removedBadLoops(HashMap<String, Loop> input){
		Set<String> key = input.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			String name = it.next();
			Loop loop = input.get(name);
			if(!(removed.contains(name))){
				if(loop.getPaScoreAvg() < 1.2 || loop.getRegionalPaScoreAvg() < 1 )
					removed.add(name);
				else
					removed = removeOverlappingLoops(loop,input,removed);
			}
		}
		for (int i = 0; i< removed.size(); ++i)
			input.remove(removed.get(i));
		return input;
	}
	
	/**
	 * Remove overlapping loops
	 * @param loop loop to test
	 * @param input loop collection
	 * @param removed arrayList of loop
	 * @return removed arrayList of loop 
	 */
	private ArrayList<String> removeOverlappingLoops(Loop loop, HashMap<String, Loop> input, ArrayList<String> removed){
		Set<String> key = input.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String name = it.next();
			Loop looptest = input.get(name);
			if(!(removed.contains(name)) && loop.getResolution() < looptest.getResolution()){
				int factor = looptest.getResolution()/loop.getResolution();
				//System.out.println(factor);
				int xtest = loop.getX()/factor;
				int ytest = loop.getY()/factor;
				for(int i = xtest-1; i <= xtest+1; ++i ){
					for(int j = ytest-1; j <= ytest+1; ++j ){
						if(i == looptest.getX() && j == looptest.getY()){
							removed.add(name);
						}
					}
				}
			}
		}
		return removed;
	}

	/**
	 * Image processing method
	 * @param imgFilter ImagePlus to correct
	 * @param fileName Strin file name
	 * @param pm ProcessMethod object
	 */
	private void imageProcessing(ImagePlus imgFilter, String fileName, ProcessMethod pm){ 
		pm.enhanceContrast(this._saturatedPixel);
		pm.runGaussian();
		imgFilter.setProcessor(Morphology.whiteTopHat(imgFilter.getProcessor(), this.m_strel));
		pm.setImg(imgFilter);
		pm.runGaussian();
		pm.runMin(this._min);
		pm.runMax(this._max);
		pm.runMax(this._max);
		pm.runMin(this._min);
		if(fileName.contains(".tif")){
			this._tifList.add(new File(fileName.replaceAll(".tif", "_processed.tif")));
			saveFile(imgFilter, fileName.replaceAll(".tif", "_processed.tif"));
		}
		else{
			this._tifList.add(new File(fileName.replaceAll(".txt", "_processed.tif")));
			saveFile(imgFilter, fileName.replaceAll(".txt", "_processed.tif"));
		}
	}
	
		
	/**
	 * Save the image file
	 * 
	 * @param imagePlusInput image to save
	 * @param pathFile path to save the image
	 */	
	public void saveFile ( ImagePlus imagePlusInput, String pathFile){
		FileSaver fileSaver = new FileSaver(imagePlusInput);
	    fileSaver.saveAsTiff(pathFile);
	}
	

	/**
	 * Full the list with file in directory
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	private File[] fillList(String dir) throws IOException{
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
	

	/**
	 * Test the normalized vector by chromosme
	 * @param normFile
	 */
	private void testNormaVectorValue(String normFile){
		_normVector = new HashMap<Integer,String>();
		BufferedReader br;
		int lineNumber = 0;
		try {
			br = new BufferedReader(new FileReader(normFile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null){
				sb.append(line);
				if((line.equals("NaN")|| line.equals("NAN") || line.equals("nan") || line.equals("na")  || Double.parseDouble(line) < 0.30)){ // tester les autre vecteur...
					_normVector.put(lineNumber*_resolution, "plop");
				}
				++lineNumber;
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) { e.printStackTrace();}
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
	 * Setter of threshold for the detection of the maxima
	 * @param thresholdMaxima
	 */
	public void setThresholdMaxima(int thresholdMaxima) { this._thresholdMaxima = thresholdMaxima;}

	
	/**
	 * Setter of hichip,
	 * false run with hic parameter
	 * true rune with hichip parameter 
	 * @param hichip boolean
	 */
	public void setIsHichip(boolean hichip){	this.m_hichip = hichip;}
}
