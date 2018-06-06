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
import ij.process.ImageProcessor;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel;

public class WholeGenomeAnalysis {
	private String m_input;
	private String m_output;
	private double m_gauss;
	private double m_min;
	private double m_max;
	private double m_saturatedPixel;
	private int m_matrixSize = 0;
	private int m_resolution;
	private int m_thresholdMaxima;
	HashMap<String,Integer> m_chrSize =  new HashMap<String,Integer>();
	private int m_diagSize;
	private int m_step;
	
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
		
	/*	System.out.println("output "+m_output+"\n"
				+ "matrix size "+m_matrixSize+" \n"
				+ "resolution "+m_resolution+"\n"
				+ "step "+m_step+"\n"
				+ "thresh "+m_thresholdMaxima);*/
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
				if(choice.equals("oMe") ) detectLoopsOmE(listOfFiles,chr);
				else if (choice.equals("o") ){
					detectLoopsO(listOfFiles,chr);
					
				}
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
			if (listOfFiles.length==0){ System.out.println("dumped directory of chromosome"+chr+"empty");}
			else{
				if(choice.equals("oMe") ) detectLoopsOmE(listOfFiles,chr);
				else if (choice.equals("o") ){
					detectLoopsO(listOfFiles,chr);
					System.out.println("bite chatte cul");
				}
				saveFile(resuFile);
			}
		}
	}
	
	/**
	 * 
	 * @param resu
	 * @param pathFile
	 * @throws IOException
	 */
	public void saveFile (String pathFile) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathFile)));
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreMed\tRegAPScoreMED\tAPScoreAVG\tRegAPScoreAVG\t%OfPixelInfToTheCenter\t%of0\tnbZero\n");
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			double plop = loop.getNbZeroInTheImage()/(m_matrixSize*m_matrixSize);
			writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t255,125,255"
				+"\t"+loop.getPaScoreMed()+"\t"+loop.getRegionalPaScoreMed()+"\t"+loop.getPaScoreAvg()+"\t"+loop.getRegionalPaScoreAvg()
				+"\t"+loop.getPercentage()+"\t"+loop.getPercentageOfZero()+"\t"+plop+"\n"); 
		}
		writer.close();
	}
	

	
	
	
	/**
	 * rao 2014 good results with default parameters
	 * droso cubenas ProcessMethod(imgFilter,2,1.25,1.25); => 4000
	 * @throws IOException 
	 * 
	 */
	private void detectLoopsOmE(File[] fileList, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection(m_step, m_resolution,m_matrixSize, m_diagSize);
		for(int i =0; i < fileList.length;++i){
			if(fileList[i].toString().contains(".txt")){
				String[] tfile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
				TupleFileImage readFile = new TupleFileImage(fileList[i].toString(),m_matrixSize,m_step,m_resolution);
				String imageName = fileList[i].toString().replaceAll(".txt", ".tif");
				ImagePlus imgRaw = readFile.readTupleFile();
				NonZeroCorrection nzc =new NonZeroCorrection(imgRaw);
				ArrayList<Integer> countNonZero = nzc.getNonZeroList();
				imgRaw.setTitle(imageName);
				saveFile(imgRaw,imageName);
				ImagePlus img = imgRaw.duplicate();
				readFile.correctImage(img);
				ImagePlus imgFilter = img.duplicate();
				imageProcessing(imgFilter,fileList[i].toString());	
				FindMaxima findLoop = new FindMaxima(imgRaw, imgFilter, chr, m_thresholdMaxima);
				HashMap<String,Loop> temp = findLoop.findloop();
				System.out.println("before "+ temp.size());
				removeMaximaCloseToZero(imgRaw,temp,false);
				System.out.println("after "+ temp.size());
			
				PeakAnalysisScore pas = new PeakAnalysisScore(imgRaw,temp,countNonZero);
				pas.computeScore();
			
				coord.setData(m_data);
				if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, false, numImage,countNonZero);}
				else if(i == fileList.length-1){	m_data = coord.imageToGenomeCoordinate(temp, false, true, numImage, countNonZero);}
				else m_data = coord.imageToGenomeCoordinate(temp, false, false, numImage, countNonZero);
			}
		}
	}
	
	/**
	 * 
	 * @param lmatrixFile
	 * @param chr
	 * @throws IOException
	 */
	private void detectLoopsO(File[] fileList, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection(m_step, m_resolution,m_matrixSize, m_diagSize);
		for(int i =0; i < fileList.length;++i){
			if(fileList[i].toString().contains(".txt")){
				String[] tfile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
				TupleFileImage readFile = new TupleFileImage(fileList[i].toString(),m_matrixSize,m_step,m_resolution);
				String imageName = fileList[i].toString().replaceAll(".txt", ".tif");
				ImagePlus imgRaw = readFile.readTupleFile();
				NonZeroCorrection nzc = new NonZeroCorrection(imgRaw);
				ArrayList<Integer> countNonZero = nzc.getNonZeroList();

				imgRaw.setTitle(imageName);
				saveFile(imgRaw,imageName);
				ImagePlus img = imgRaw.duplicate();
				readFile.correctImage(img);
			
				ImagePlus imgFilter = img.duplicate();
				ProcessMethod pm = new ProcessMethod(imgFilter,this.m_gauss);
				//pm.enhanceContrast(m_saturatedPixel);
				pm.runGaussian();
				imageName = fileList[i].toString().replaceAll(".txt", "_processed.tif");
				saveFile(imgFilter,imageName);
				FindMaxima findLoop = new FindMaxima(imgRaw, imgFilter, chr, m_thresholdMaxima);
				HashMap<String,Loop> temp = findLoop.findloop();
			
				System.out.println("before "+ temp.size());
				removeMaximaCloseToZero(imgRaw,temp,true);
				System.out.println("after "+ temp.size());
			
				PeakAnalysisScore pas = new PeakAnalysisScore(imgRaw,temp,countNonZero);
				pas.computeScore();
			
				coord.setData(m_data);
				if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, true, numImage,countNonZero);}
				else if(i == fileList.length-1){	m_data = coord.imageToGenomeCoordinate(temp, true, true, numImage, countNonZero);}
				else m_data = coord.imageToGenomeCoordinate(temp, true, true, numImage, countNonZero);
				//if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, false, i,countNonZero);}
				//else if(i == lmatrixFile.size()-1){	m_data = coord.imageToGenomeCoordinate(temp, false, true, i, countNonZero);}
				//else m_data = coord.imageToGenomeCoordinate(temp, false, false, i, countNonZero);
			}
		}
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
	 * @param img
	 * @param fileName
	 */
	
	private void  removeMaximaCloseToZero(ImagePlus img, HashMap<String,Loop> temp,boolean isObserved){ 
		Set<String> key = temp.keySet();
		Iterator<String> it = key.iterator();
		ImageProcessor ip = img.getProcessor();
		ArrayList<String> toRemove = new ArrayList<String>();
		int thresh = 3;
		if (isObserved)	thresh = 1;
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = temp.get(cle);
			int i = loop.getX();
			int j = loop.getY();
			//System.out.println("cle  "+cle+" "+i+" "+j);
			int nb =0;
			if (i-1> 0 && i+1< img.getWidth()){
				if(j-1 > 0 && j+1 < img.getHeight()){
					for(int ii = i-1; ii <= i+1; ++ii){
						for(int jj = j-1; jj <= j+1; ++jj){
							if (ip.getPixel(ii, jj)<=0){
								nb++;
								if (nb >= thresh ){
									toRemove.add(cle);
									break;
								}
							}
						}
					}
				}
			}
			else{
				
			}
		}
		
		for(int i = 0; i < toRemove.size();++i){
			temp.remove(toRemove.get(i));
		}
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
	public String getinputDir() { return m_input; }
	
	/**
	 * 
	 * @return
	 */
	public int getMatrixSize() { return this.m_matrixSize; }

	
	/**
	 * 
	 * @return
	 */
	public int getStep() { return this.m_step; }
	
	/**
	 * 
	 * @param inputDir
	 */
	public void setInputDir(String inputDir) { this.m_input = inputDir; }

	/**
	 * 
	 * @return
	 */
	public String getOutputDir() { return m_output; }

	/**
	 * 
	 * @param outputDir
	 */
	public void setOutputDir(String outputDir) { this.m_output = outputDir; }

	/**
	 * 
	 * @return
	 */
	public double getGauss() { return m_gauss; }
	
	/**
	 * 
	 * @param gauss
	 */
	public void setGauss(double gauss) { this.m_gauss = gauss; }

	public void setDiagSize(int diagSize) { this.m_diagSize = diagSize; }
	/**
	 * 
	 * @return
	 */
	public double getMin() { return m_min;}

	/**
	 * 
	 * @param min
	 */
	public void setMin(double min) { this.m_min = min; }

	/**
	 * 
	 * @return
	 */
	public double getMax() { return m_max; }
	
	/**
	 * 
	 * @param max
	 */
	public void setMax(double max) { this.m_max = max; }

	/**
	 * 
	 * @return
	 */
	public double getSaturatedPixel() { return m_saturatedPixel; }

	/**
	 * 
	 * @param saturatedPixel
	 */
	public void setSaturatedPixel(double saturatedPixel) { this.m_saturatedPixel = saturatedPixel; }

	/**
	 * 
	 * @return
	 */
	public int getResolution() { return m_resolution;}
	
	/**
	 * 
	 * @param resolution
	 */
	public void setResolution(int resolution) {this.m_resolution = resolution;}

	public void setMatrixSize(int size) {this.m_matrixSize = size;}
	
	public void setStep(int step) {this.m_step = step;}
	
	/**
	 * 
	 * @return
	 */
	public int getThresholdMaxima() { return m_thresholdMaxima; }

	/**
	 * 
	 * @param thresholdMaxima
	 */
	public void setThresholdMaxima(int thresholdMaxima) {	this.m_thresholdMaxima = thresholdMaxima; }
	
	/**
	 * 
	 * @param createFile
	 * @param m_nbFile
	 * @param matrixSize
	 * @param step
	 * @param outdir
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
