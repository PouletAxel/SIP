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
 * 
 * @author plop
 *
 */
public class WholeGenomeAnalysis {
	/** */
	private String m_input;
	/** */
	private String m_output;
	/** */
	private double m_gauss;
	/** */
	private double m_min;
	/** */
	private double m_max;
	/** */
	private double m_saturatedPixel;
	/** */
	private int m_matrixSize = 0;
	/** */
	private int m_resolution;
	/** */
	private int m_thresholdMaxima;
	/** */
	HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
	/** */
	private int m_diagSize;
	/** */
	private int m_step;
	/** */
	private boolean m_isObserved = false;
	/** */
	static HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	
	/**
	 * 
	 * @param input
	 * @param output
	 * @param lbw
	 * @param gauss
	 * @param min
	 * @param max
	 * @param step
	 * @param res
	 * @param saturatedPixel
	 * @param chrSize
	 * @param thresholdMaxima
	 * @throws IOException 
	 */
	
	public WholeGenomeAnalysis(String output, HashMap<String, Integer> chrSize, double gauss, double min,
			double max, int step, int resolution, double saturatedPixel, int thresholdMax,
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
		m_step = step;
	}

	/**
	 * 
	 * @param resu
	 * @param pathFile
	 * @return 
	 * @throws IOException
	 */
	public void run(String choice) throws IOException{
		Iterator<String> key = m_chrSize.keySet().iterator();
		String resuFile = m_output+File.separator+"loops.bed";
		while(key.hasNext()){
			String chr = key.next();
			String dir = m_output+File.separator+chr+File.separator;
			File[] listOfFiles =fillList(dir);
			System.out.println(listOfFiles.length);
			if (listOfFiles.length==0){ System.out.println("dumped directory of chromosome"+chr+"empty");}
			else{
				if (choice.equals("o")) 	m_isObserved = true; 
				detectLoops(listOfFiles,chr);
				saveFile(resuFile);
			}
		}
	}

	/**
	 * 
	 * @param resu
	 * @param pathFile
	 * @return 
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
			if (listOfFiles.length==0)
				System.out.println("dumped directory of chromosome"+chr+"empty");
			else{
				if (choice.equals("o")) 	m_isObserved = true; 
				detectLoops(listOfFiles,chr);
				saveFile(resuFile);
			}
		}
	}
	
	/**
	 * 
	 * @param pathFile
	 * @throws IOException
	 */
	
	public void saveFile (String pathFile) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathFile)));
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAVG\tRegAPScoreAVG\t%OfPixelInfToTheCenter\tAvg_diffMaxNeihgboor_1\tAvg_diffMaxNeihgboor_2\tavg\n");
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			double a =  loop.getAvg();//getNeigbhoord1()/loop.getNeigbhoord2();
			writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
				+"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()
				+"\t"+loop.getPercentage()+"\t"+loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+a+"\n"); 
		}
		writer.close();
	}
	

	
	
	
	/**
	 * rao 2014 good results with default parameters
	 * droso cubenas ProcessMethod(imgFilter,2,1.25,1.25); => 4000
	 * @throws IOException 
	 * 
	 * @param fileList
	 * @param chr
	 * @throws IOException
	 */
	private void detectLoops(File[] fileList, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection(m_resolution);
		for(int i =0; i < fileList.length;++i){
			if(fileList[i].toString().contains(".txt")){
				// make and save image at two differents resolution (m_resolution and m_resolution*2)
				String[] tfile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
				TupleFileImage readFile = new TupleFileImage(fileList[i].toString(),m_matrixSize,m_step,m_resolution);
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
				nzc =new NonZeroCorrection(imgRawBiggerRes);
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
				int finChr = Integer.parseInt(tfile[tfile.length-1].replaceAll(".txt", ""));
				m_data = coord.imageToGenomeCoordinate(temp, numImage);
				System.out.println("after "+ m_data.size());
			}
		}
	}
	
	/**
	 * 
	 * @param temp
	 * @param tempBiggerRes
	 * @return
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
			for(int i = x-2; i <= x+2; ++i){
				for(int j = y-2; j <= y+2; ++j){
					String test = tname[0]+"\t"+i+"\t"+j+"\t"+tname[3];
					if(temp.containsKey(test)){
						removed.add(name);
						keepLoop = false;
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
	 * 
	 * @param imgFilter
	 * @param fileName
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
	 * 
	 * @param imagePlusInput
	 * @param pathFile
	 */	
	public void saveFile ( ImagePlus imagePlusInput, String pathFile)
	{
		FileSaver fileSaver = new FileSaver(imagePlusInput);
	    fileSaver.saveAsTiff(pathFile);
	}

	/**
	 * 
	 * @return
	 */
	public String getinputDir(){
		return m_input;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMatrixSize(){
		return this.m_matrixSize;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getStep(){
		return this.m_step;
	}
	
	/**
	 * 
	 * @param inputDir
	 */
	public void setInputDir(String inputDir){
		this.m_input = inputDir;
	}

	/**
	 * 
	 * @return
	 */
	public String getOutputDir(){
		return m_output;
	}

	/**
	 * 
	 * @param outputDir
	 */
	public void setOutputDir(String outputDir){
		this.m_output = outputDir;
	}

	/**
	 * 
	 * @return
	 */
	public double getGauss(){
		return m_gauss;
	}
	
	/**
	 * 
	 * @param gauss
	 */
	public void setGauss(double gauss){
		this.m_gauss = gauss;
	}
	
	/**
	 * 
	 * @param diagSize
	 */
	public void setDiagSize(int diagSize){
		this.m_diagSize = diagSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getMin(){
		return m_min;
	}

	/**
	 * 
	 * @param min
	 */
	public void setMin(double min){
		this.m_min = min;
	}

	/**
	 * 
	 * @return
	 */
	public double getMax(){
		return m_max;
	}
	
	/**
	 * 
	 * @param max
	 */
	public void setMax(double max){
		this.m_max = max;
	}

	/**
	 * 
	 * @return
	 */
	public double getSaturatedPixel(){
		return m_saturatedPixel;
	}

	/**
	 * 
	 * @param saturatedPixel
	 */
	public void setSaturatedPixel(double saturatedPixel){
		this.m_saturatedPixel = saturatedPixel;
	}

	/**
	 * 
	 * @return
	 */
	public int getResolution(){
		return m_resolution;
	}
	
	/**
	 * 
	 * @param resolution
	 */
	public void setResolution(int resolution){
		this.m_resolution = resolution;
	}

	/**
	 * 
	 * @param size
	 */
	public void setMatrixSize(int size){
		this.m_matrixSize = size;
	}
	
	/**
	 * 
	 * @param step
	 */
	public void setStep(int step){
		this.m_step = step;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getThresholdMaxima(){
		return m_thresholdMaxima;
	}

	/**
	 * 
	 * @param thresholdMaxima
	 */
	public void setThresholdMaxima(int thresholdMaxima) {
		this.m_thresholdMaxima = thresholdMaxima;
	}

	/**
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	private File[] fillList(String dir) throws IOException
	{
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
}
