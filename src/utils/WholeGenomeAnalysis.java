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

import ij.IJ;
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
	/** */
	private int m_nbZero = -1;
	public ArrayList<File> m_tifList = new ArrayList<File>();
	/** loop coolection*/
	static HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	/** loop coolection*/
	static HashMap<Integer,String> m_normVector = new HashMap<Integer,String>();
	/** */
	ArrayList<Integer> m_listFactor = new ArrayList<Integer>();
	/**
	 * 
	 */
	Strel m_strel = Strel.Shape.SQUARE.fromRadius(40);
	
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
			int diagSize, int matrixSize, int nbZero,ArrayList<Integer> listFactor) {
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
		m_nbZero = nbZero;
		m_listFactor = listFactor;
	}

	/**
	 * run the whole genome analysis to detect the maxima
	 * @param choice isObserved or oMe
	 * @throws IOException
	 */
	public void run() throws IOException{
		Iterator<String> key = m_chrSize.keySet().iterator();
		String resuFile = m_output+File.separator+"loops.bed";
		int nb = 0;
		while(key.hasNext()){
			String chr = key.next();
			String dir = m_output+File.separator+chr+File.separator;
			File[] listOfFiles = fillList(dir);
			System.out.println(listOfFiles.length);
			this.testNormaVectorValue(m_output+File.separator+"normVector"+File.separator+chr+".norm");
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
	 * run the whole genome analysis to detect the maxima
	 * 
	 * @param choice choice isObserved or oMe
	 * @param input path of the input file
	 * @throws IOException
	 */
	public void run(String input) throws IOException{
		Iterator<String> key = m_chrSize.keySet().iterator();
		String resuFile = m_output+File.separator+"loops.bed";
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
	 * Save the result file in tabulated file
	 * 
	 * @param pathFile path of output file
	 * @throws IOException
	 */
	
	public void saveFile(String pathFile, boolean first) throws IOException{
		BufferedWriter writer;
		if(first){
			writer = new BufferedWriter(new FileWriter(new File(pathFile), true));
		}
		
		else{
			writer = new BufferedWriter(new FileWriter(new File(pathFile)));
			writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAvg\tRegAPScoreAvg\tAPScoreMed\tRegAPScoreMed\tAvg_diffMaxNeihgboor_1\tAvg_diffMaxNeihgboor_2\tAvg_diffMaxNeihgboor_3\tavg\tstd\tvalue\n");
		}
		
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		
		
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			if(loop.getPaScoreAvg() >= 1.2 && loop.getRegionalPaScoreAvg() > 1){
				if(loop.getAvg() >= 1.2 && loop.getValue()>=2){
					writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
						+"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()+"\t"+loop.getPaScoreMed()+"\t"+loop.getRegionalPaScoreMed()
						+"\t"+loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getNeigbhoord3()+"\t"+loop.getAvg()+"\t"+loop.getStd()+"\t"+loop.getValue()+"\n");
				}
			}
		}
		writer.close();
	}
	

	/**
	 * 
	 * @param file
	 * @return
	 */
	private ImagePlus doImage(String file){	
		TupleFileToImage readFile = new TupleFileToImage(file,m_matrixSize,m_resolution);
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
	 * 
	 * @param fileList list fo the tuple file
	 * @param chr name of the chr
	 * @throws IOException
	 */
	private void detectLoops(File[] fileList, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection();
		HashMap<String,Loop> hLoop= new HashMap<String,Loop>();
		for(int i = 0; i < fileList.length; ++i){
			if(fileList[i].toString().contains(".txt")){
				// make and save image at two differents resolution (m_resolution and m_resolution*2)
				String[] tfile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
				System.out.println(numImage+" "+fileList[i]);

				ImagePlus imgRaw = doImage(fileList[i].toString());
				
				// non zero correction loops calling
				NonZeroCorrection nzc =new NonZeroCorrection(imgRaw);
				ArrayList<Integer> countNonZero = nzc.getNonZeroList();
				ImagePlus imgFilter = imgRaw.duplicate();
				TupleFileToImage.correctImage(imgFilter);
				
				/// call the loops first in the smaller resolution 
				// then making image with bigger resolution and fill no Zero list
				// faire un gros for deguelasse por passer les faceteur de grossissement seulement si listDefacteur > 1.
				ImagePlus imgCorrect = imgFilter.duplicate();
				ProcessMethod m = new ProcessMethod(imgFilter,m_min,m_max,m_gauss);
				imageProcessing(imgFilter,fileList[i].toString(), m);
				m_tifList.add(new File(imgRaw.getTitle()));
				m_tifList.add(new File(imgRaw.getTitle().replaceAll(".tif", "_N.tif")));
				ImagePlus imgNorm = IJ.openImage(imgRaw.getTitle().replaceAll(".tif", "_N.tif"));
				int thresh = m_thresholdMaxima;
				double plop = 100*TupleFileToImage.m_noZeroPixel/(this.m_matrixSize*this.m_matrixSize);
				if(plop <= 5)
					thresh =  m_thresholdMaxima/10;
				
				FindMaxima findLoop = new FindMaxima(imgNorm, imgFilter, chr, thresh, m_diagSize, m_resolution,countNonZero);
				HashMap<String,Loop> temp = findLoop.findloop(numImage, m_nbZero,imgRaw, 1);
				PeakAnalysisScore pas = new PeakAnalysisScore(imgNorm,temp);
				pas.computeScore();
				if (m_listFactor.size() > 1){
					for (int j = 1; j < m_listFactor.size(); ++j ){
						ChangeImageRes test =  new ChangeImageRes(imgCorrect,  m_listFactor.get(j));
						ImagePlus imgRawBiggerRes = test.run();
						
						test =  new ChangeImageRes(imgNorm,  m_listFactor.get(j));
						ImagePlus imgRawBiggerResNorm = test.run();
						
						saveFile(imgRawBiggerRes,fileList[i].toString().replaceAll(".txt", "_"+m_listFactor.get(j)+".tif")); 
						saveFile(imgRawBiggerResNorm,fileList[i].toString().replaceAll(".txt", "_"+m_listFactor.get(j)+"_N.tif"));
						m_tifList.add(new File(fileList[i].toString().replaceAll(".txt", "_"+m_listFactor.get(j)+".tif")));
						m_tifList.add(new File(fileList[i].toString().replaceAll(".txt", "_"+m_listFactor.get(j)+"_N.tif")));
						nzc = new NonZeroCorrection(imgRawBiggerRes);
						ArrayList<Integer> countNonZeroBigger = nzc.getNonZeroList();
						ImagePlus imgFilterBiggerRes = imgRawBiggerRes.duplicate();
						m = new ProcessMethod(imgFilterBiggerRes,m_min/m_listFactor.get(j),m_max/m_listFactor.get(j),m_gauss);
						imageProcessing(imgFilterBiggerRes, fileList[i].toString().replaceAll(".txt", "_"+m_listFactor.get(j)+".tif"), m);
			
						int diag = m_diagSize/m_listFactor.get(j);
						int res = m_resolution*m_listFactor.get(j);
						if (diag < 2){diag = 2 ;}
						findLoop = new FindMaxima(imgRawBiggerResNorm, imgFilterBiggerRes, chr,thresh, diag, res, countNonZeroBigger);
						HashMap<String,Loop>tempBiggerRes = findLoop.findloop(numImage,(int)m_nbZero/m_listFactor.get(j)-1,imgRawBiggerRes,m_listFactor.get(j)*3);
						
						pas = new PeakAnalysisScore(imgRawBiggerResNorm,tempBiggerRes);
						pas.computeScore();
						temp.putAll(tempBiggerRes);
					}
					
				}
				coord.setData(hLoop);
				hLoop = coord.imageToGenomeCoordinate(temp, numImage);
				
			}
		}
		System.out.println("before "+ hLoop.size());
		hLoop = removedBadLoops(hLoop);
		System.out.println("after "+ hLoop.size());
		m_data = hLoop;
		System.out.println("chr "+ chr +"\t"+hLoop.size());
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	private boolean removedVectoNorm(Loop loop) {
		boolean test = false;
		int x = loop.getCoordinates().get(0);
		int y = loop.getCoordinates().get(2);
		if(loop.getResolution() == m_resolution){
			if(m_normVector.containsKey(x) || m_normVector.containsKey(y))
				test = true;
		}
		else if(loop.getResolution() == m_resolution*2){
			if(m_normVector.containsKey(x) || m_normVector.containsKey(y) || m_normVector.containsKey(x+m_resolution) || m_normVector.containsKey(y+m_resolution))
				test = true;
		}
		else if(loop.getResolution() == m_resolution*5){
			for(int i = x; i <= x+5*m_resolution; i+=m_resolution)
				if(m_normVector.containsKey(i))
					test = true;
				
			if(test == false){
				for(int i = y; i <= y+5*m_resolution; y+=m_resolution)
					if(m_normVector.containsKey(y))
						test = true;
			}
		}
			
		return test;
	}

	/**
	 * Compare loops between the two resolution, if at the same location the are one loops call at bigger resolution and snmaller resolution,
	 * keep the smaller one 
	 * 
	 * @param input collection of the loop call at small resolution
	 * @param tempBiggerRes collection of the loop call at smaller resolution
	 * @return HashMap<String, Loop> the sorted HashMap
	 */
	private HashMap<String, Loop>  removedBadLoops(HashMap<String, Loop> input){
		Set<String> key = input.keySet();
		Iterator<String> it = key.iterator();
		ArrayList<String> removed = new ArrayList<String>();
		while (it.hasNext()){
			String name = it.next();
			if(!(removed.contains(name))){
				String [] tname = name.split("\t");
				int x = Integer.parseInt(tname[1]);
				int y = Integer.parseInt(tname[2]);
				Loop loop = input.get(name);
				boolean testBreak = false;
				Boolean testRemoved = removedVectoNorm(loop);
				if(testRemoved)
					removed.add(name);
				else{
					for(int i = x-5*m_resolution; i <= x+5*m_resolution; i+=m_resolution){
						for(int j = y-5*m_resolution; j <= y+5*m_resolution; j+=m_resolution){
							String test = tname[0]+"\t"+i+"\t"+j;
							if(!test.equals(name)){
								if(input.containsKey(test)){
									if(input.get(test).getResolution() < input.get(name).getResolution()){
										removed.add(name);
										testBreak =true;
										break;
									}
									else if(input.get(test).getResolution() == input.get(name).getResolution()){
										if((Math.abs(x-input.get(test).getX()) < m_resolution*3 || Math.abs(y-input.get(test).getY()) < m_resolution*3)){
											if(input.get(test).getAvg() > input.get(name).getAvg()){
												removed.add(name);
												testBreak =true;
												break;
											}
											else if(input.get(test).getAvg() < input.get(name).getAvg())
												removed.add(test);
									
											else{
												if(input.get(test).getPaScoreAvg() > input.get(name).getPaScoreAvg()){
													removed.add(name);
													testBreak =true;
													break;
												}
												else removed.add(test);
											}
										}
									}
									else	removed.add(test);
								
								}
							}
						}
						if(testBreak){break;}
					}
				}
			}
		}
		//System.out.println("PROUT  "+removed.size());
		for (int i = 0; i< removed.size(); ++i){
			input.remove(removed.get(i));
		}
		return input;
	}

	/**
	 * method to process the image with oMe
	 * 
	 * @param imgFilter image filtered
	 * @param fileName input file
	 */
	private void imageProcessing(ImagePlus imgFilter, String fileName, ProcessMethod pm){ 
		pm.enhanceContrast(this.m_saturatedPixel);
		pm.runGaussian();
		
		imgFilter.setProcessor(Morphology.whiteTopHat(imgFilter.getProcessor(), m_strel));
		pm.setImg(imgFilter);
		pm.runGaussian();
		pm.runMin(m_min);
		pm.runMax(m_min);
		pm.runMax(m_min);
		pm.runMin(m_min);
		if(fileName.contains(".tif")){
			m_tifList.add(new File(fileName.replaceAll(".tif", "_processed.tif")));
			saveFile(imgFilter, fileName.replaceAll(".tif", "_processed.tif"));
		}
		else{
			m_tifList.add(new File(fileName.replaceAll(".txt", "_processed.tif")));
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
	

	/**
	 * 
	 * @param normFile
	 */
	private void testNormaVectorValue(String normFile){
		m_normVector = new HashMap<Integer,String>();
		BufferedReader br;
		int lineNumber = 0;
		try {
		br = new BufferedReader(new FileReader(normFile));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null){
			sb.append(line);
			if((line.equals("NaN")|| line.equals("NAN") || line.equals("nan") || line.equals("na")  || Double.parseDouble(line) < 0.30)){ // tester les autre vecteur...
				m_normVector.put(lineNumber*m_resolution, "plop");
			}
			++lineNumber;
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
	} catch (IOException e) { e.printStackTrace();}
}
	
	
}
