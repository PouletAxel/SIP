 package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import ij.ImagePlus;
import ij.io.FileSaver;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel;

/**
 * Analyse and detect a whole genome HiC with .hic file or already processed data.
 * The class is used for the observed and oMe method.
 * 
 * @author axel poulet
 *
 */
public class WholeGenomeAnalysis {
	/** String path of the input data*/
	private String m_input;
	/** Path of the output file*/
	private String m_output;
	/** Strength of the gaussian filter*/
	private double m_gauss;
	/** Strength of the min filter*/
	private double m_min;
	/** Strength of the max filter*/
	private double m_max;
	/** % of staurated pixel after enhance contrast*/
	private double m_saturatedPixel;
	/** Image size*/
	private int m_matrixSize = 0;
	/** Resolution of the bin dump in base*/
	private int m_resolution;
	/** Threshold for the maxima detection*/
	private int m_thresholdMaxima;
	/** HashMap of the chr size, the key = chr name, value = size of chr*/
	HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
	/** Diage size to removed maxima close to diagonal*/
	private int m_diagSize;
	/** Size of the step to process each chr (step = matrixSize/2)*/
	private int m_step;
	/** boolean: true = observed, false = oMe*/
	private boolean m_isObserved = false;
	/** loop coolection*/
	static HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	
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
	 
	
	public WholeGenomeAnalysis(String output, HashMap<String, Integer> chrSize, double gauss, double min,
			double max, int resolution, double saturatedPixel, int thresholdMax,
			int diagSize, int matrixSize) {
		m_output = output;
		m_input = output;
		m_chrSize = chrSize;
		m_gauss = gauss;
		m_min = min;
		m_max = max;
		m_matrixSize = matrixSize;
		m_resolution = resolution;
		m_saturatedPixel = saturatedPixel;
		m_thresholdMaxima = thresholdMax;
		m_diagSize = diagSize;
		m_step = matrixSize/2;
	}

	/**
	 * run the whole genome analysis to detect the maxima
	 * @param choice isObserved or oMe
	 * @throws IOException
	 */
	public void run(String choice) throws IOException{
		Iterator<String> key = m_chrSize.keySet().iterator();
		String resuFile = m_output+File.separator+"loops.bed";
		while(key.hasNext()){
			String chr = key.next();
			String dir = m_output+File.separator+chr+File.separator;
			File[] listOfFiles = fillList(dir);
			System.out.println(listOfFiles.length);
			if (listOfFiles.length == 0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				if (choice.equals("o"))
					m_isObserved = true; 
				detectLoops(listOfFiles,chr);
				saveFile(resuFile);
			}
		}
	}

	/**
	 * run the whole genome analysis to detect the maxima
	 * 
	 * @param choice choice isObserved or oMe
	 * @param input path of the input file
	 * @throws IOException
	 */
	public void run(String choice,String input) throws IOException{
		Iterator<String> key = m_chrSize.keySet().iterator();
		String resuFile = m_output+File.separator+"loops.bed";
		while(key.hasNext()){
			String chr = key.next();
			String dir = input+File.separator+chr+File.separator;
			File[] listOfFiles =fillList(dir);
			System.out.println(dir);
			if (listOfFiles.length == 0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				if (choice.equals("o"))m_isObserved = true; 
				detectLoops(listOfFiles,chr);
				saveFile(resuFile);
			}
		}
	}
	
	/**
	 * Save the result file in tabulated file
	 * 
	 * @param pathFile path of output file
	 * @throws IOException
	 */
	
	public void saveFile(String pathFile) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathFile)));
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAVG\tRegAPScoreAVG\tAvg_diffMaxNeihgboor_1\tAvg_diffMaxNeihgboor_2\tavg\tstd\tvalue\t%ofZeroInneigh\n");
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			//if(loop.getPaScoreAvg()>0){
				writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
					+"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()
					+"\t"+loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"+loop.getStd()+"\t"+loop.getValue()+"\t"+loop.getPercentageOfZero()+"\n");
			//}
		}
		writer.close();
	}
	

	
	
	
	/**
	 * Detect loops methods
	 * detect the loops at two different resolution, initial resolution + 2 fold bigger
	 * 
	 * @param fileList list fo the tuple file
	 * @param chr name of the chr
	 * @throws IOException
	 */
	private void detectLoops(File[] fileList, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection(m_resolution);
		for(int i = 0; i < fileList.length; ++i){
			if(fileList[i].toString().contains(".txt")){
				// make and save image at two differents resolution (m_resolution and m_resolution*2)
				String[] tfile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
				TupleFileToImage readFile = new TupleFileToImage(fileList[i].toString(),m_matrixSize,m_resolution);
				String imageName = fileList[i].toString().replaceAll(".txt", ".tif");
				ImagePlus imgRaw = readFile.readTupleFile();
				imgRaw.setTitle(imageName);
				saveFile(imgRaw,imageName);
				
				
				// non zero correction loops calling
				NonZeroCorrection nzc =new NonZeroCorrection(imgRaw);
				ArrayList<Integer> countNonZero = nzc.getNonZeroList();
				ImagePlus imgFilter = imgRaw.duplicate();
				readFile.correctImage(imgFilter);
				
				/// making image with bigger resolution and fill no Zero list
				ChangeImageRes test =  new ChangeImageRes(imgFilter);
				ImagePlus imgRawBiggerRes = test.run();
				String testName = fileList[i].toString().replaceAll(".txt", "_10kb.tif");
				saveFile(imgRawBiggerRes,testName); 
				nzc = new NonZeroCorrection(imgRawBiggerRes);
				ArrayList<Integer> countNonZeroBigger = nzc.getNonZeroList();
				ImagePlus imgFilterBiggerRes = imgRawBiggerRes.duplicate();

				int noiseBigger = m_thresholdMaxima;
				if(m_isObserved){
					ProcessMethod pm = new ProcessMethod(imgFilter,this.m_gauss);
					pm.runGaussian();
					pm = new ProcessMethod(imgFilterBiggerRes,this.m_gauss);					
				}
				else{
					imageProcessing(imgFilter,fileList[i].toString());
					testName = testName.replaceAll(".tif", "_10kb.txt");
					imageProcessing(imgFilterBiggerRes, testName);
				}
				
				//appel des loop pour n et 2*2 resolution
				//stocker les deux une liste temporaire
				//comparer les deux liste pour savoir si y a des loops prensent dans les meme region 
				// ensuite corriger les coordonnees
				
				FindMaxima findLoop = new FindMaxima(imgRaw, imgFilter, chr, m_thresholdMaxima, m_diagSize, m_resolution,countNonZero);
				HashMap<String,Loop> temp = findLoop.findloop(m_isObserved);
				PeakAnalysisScore pas = new PeakAnalysisScore(imgRaw,temp,countNonZero);
				pas.computeScore();
				System.out.println(temp.size());
				
				findLoop = new FindMaxima(imgRawBiggerRes, imgFilterBiggerRes, chr, noiseBigger, m_diagSize/2, m_resolution*2,countNonZeroBigger);
				HashMap<String,Loop> tempBiggerRes = findLoop.findloop(m_isObserved);
				pas = new PeakAnalysisScore(imgRawBiggerRes,tempBiggerRes,countNonZeroBigger);
				pas.computeScore();
				
				compareLoops(temp,tempBiggerRes);
				System.out.println(temp.size());
				coord.setData(m_data);
				System.out.println("before "+ m_data.size());
				m_data = coord.imageToGenomeCoordinate(temp, numImage);
				System.out.println("after "+ m_data.size());
			}
		}
	}
	
	/**
	 * Compare loops between the two resolution, if at the same location the are one loops call at bigger resolution and snmaller resolution,
	 * keep the smaller one 
	 * 
	 * @param temp collection of the loop call at small resolution
	 * @param tempBiggerRes collection of the loop call at smaller resolution
	 * @return HashMap<String, Loop> the sorted HashMap
	 */
	private HashMap<String, Loop>  compareLoops(HashMap<String, Loop> temp, HashMap<String, Loop> tempBiggerRes){
		Set<String> key = tempBiggerRes.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			boolean keepLoop = true;
			String name = it.next();
			String [] tname = name.split("\t");
			int x = Integer.parseInt(tname[1])*2;
			int y = Integer.parseInt(tname[2])*2;
			for(int i = x-3; i <= x+3; ++i){
				for(int j = y-3; j <= y+3; ++j){
					String test = tname[0]+"\t"+i+"\t"+j+"\t"+tname[3];
					if(temp.containsKey(test)){
						removed.add(name);
						keepLoop = false;
						//System.out.println("plop : "+test);
						break;
					}
				}
			}
			if(keepLoop)
				temp.put(name, tempBiggerRes.get(name));
		}
		return temp;
	}

	/**
	 * method to process the image with oMe
	 * 
	 * @param imgFilter image filtered
	 * @param fileName input file
	 */
	private void imageProcessing(ImagePlus imgFilter, String fileName){ 
		ProcessMethod pm = new ProcessMethod(imgFilter,this.m_min,this.m_max,this.m_gauss);
		pm.enhanceContrast(this.m_saturatedPixel);
		pm.topHat();
		Strel strel = Strel.Shape.DISK.fromRadius(1);
		imgFilter.setProcessor(Morphology.erosion(imgFilter.getProcessor(), strel));
		saveFile(imgFilter, fileName.replaceAll(".txt", "_processed.tif"));
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
	 * Getter of the input dir
	 * @return path of the input dir
	 */
	public String getinputDir(){
		return m_input;
	}
	
	/**
	 * Getter of the matrix size
	 * 
	 * @return the size of the image
	 */
	public int getMatrixSize(){
		return this.m_matrixSize;
	}

	
	/**
	 * Getter of step 
	 * @return the step
	 */
	public int getStep(){
		return this.m_step;
	}
	
	/**
	 * Setter of the path of the input directory
	 * @param inputDir String of the input directory
	 */
	public void setInputDir(String inputDir){
		this.m_input = inputDir;
	}

	/**
	 * Getter of the path of the output directory
	 * @return path 
	 */
	public String getOutputDir(){
		return m_output;
	}

	/**
	 * Setter of the path of the output directory
	 * @param outputDir
	 */
	public void setOutputDir(String outputDir){
		this.m_output = outputDir;
	}

	/**
	 * Getter of the gaussian blur strength
	 * @return double gaussian 
	 */
	public double getGauss(){
		return m_gauss;
	}
	
	/**
	 * Setter of the gaussian blur strength
	 * @param gauss double
	 */
	public void setGauss(double gauss){
		this.m_gauss = gauss;
	}
	
	/**
	 * Setter of the diagonal size
	 * @param diagSize int of the size of the diagonal
	 */
	public void setDiagSize(int diagSize){
		this.m_diagSize = diagSize;
	}
	
	/**
	 * Getter of the min filter strength
	 * @return double strength of the min filter
	 */
	public double getMin(){
		return m_min;
	}

	/**
	 * Setter of the min filter strength
	 * @param min
	 */
	public void setMin(double min){
		this.m_min = min;
	}

	/**
	 * Getter of the max filter strength
	 * @return double max filter
	 */
	public double getMax(){
		return m_max;
	}
	
	/**
	 * Setter of the min filter strength
	 * @param max
	 */
	public void setMax(double max){
		this.m_max = max;
	}

	/**
	 * Getter % of saturated pixel for the contrast enhancement
	 * @return double percentage of saturated
	 */
	public double getSaturatedPixel(){
		return m_saturatedPixel;
	}

	/**
	 * Setter % of saturated pixel for the contrast enhancement
	 * @param saturatedPixel
	 */
	public void setSaturatedPixel(double saturatedPixel){
		this.m_saturatedPixel = saturatedPixel;
	}

	/**
	 * Getter of resolution of the bin 
	 * @return
	 */
	public int getResolution(){
		return m_resolution;
	}
	
	/**
	 * Setter of resolution of the bin 
	 * @param resolution
	 */
	public void setResolution(int resolution){
		this.m_resolution = resolution;
	}

	/**
	 * Setter of size of the matrix 
	 * @param size
	 */
	public void setMatrixSize(int size){
		this.m_matrixSize = size;
	}
	
	/**
	 * c 
	 * @param step
	 */
	public void setStep(int step){
		this.m_step = step;
	}
	
	/**
	 * Getter of threshold for the detction of the maxima 
	 * @return
	 */
	public int getThresholdMaxima(){
		return m_thresholdMaxima;
	}

	/**
	 * Setter of threshold for the detection of the maxima
	 * @param thresholdMaxima
	 */
	public void setThresholdMaxima(int thresholdMaxima) {
		this.m_thresholdMaxima = thresholdMaxima;
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
}
