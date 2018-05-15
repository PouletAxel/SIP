package core;

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
import utils.CoordinatesCorrection;
import utils.EnhanceNoiseScore;
import utils.Loop;
import utils.NonZeroCorrection;
import utils.PeakAnalysisScore;
import utils.ProcessMethod;
import utils.ProcessTuplesFile;
import utils.TupleFileImage;

public class WholeGenomeAnalysis {
	private String m_input;
	private String m_output;
	private double m_gauss;
	private double m_min;
	private double m_max;
	private double m_saturatedPixel;
	private static int m_matrixSize;
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
	public WholeGenomeAnalysis(String input, String output, HashMap<String,Integer> chrSize, double gauss, double min, double max,
			int step, int res, double saturatedPixel, int thresholdMaxima, int diagSize, int matrixSize) throws IOException{
		m_input = input;
		m_output = output;
		m_chrSize = chrSize;
		m_gauss = gauss;
		m_min = min;
		m_max = max;
		m_matrixSize = matrixSize;
		m_resolution = res;
		m_saturatedPixel = saturatedPixel;
		m_thresholdMaxima = thresholdMaxima;
		m_diagSize = diagSize;
		m_step = step;
		System.out.println("input "+m_input+"\n"
				+ "output "+m_output+"\n"
				+ "matrix size "+m_matrixSize+" \n"
				+ "resolution "+m_resolution+"\n"
				+ "step "+m_step);
	}
	
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
		
		System.out.println("output "+m_output+"\n"
				+ "matrix size "+m_matrixSize+" \n"
				+ "resolution "+m_resolution+"\n"
				+ "step "+m_step+"\n"
				+ "thresh "+m_thresholdMaxima);
	}

	/**
	 * 
	 * @param resu
	 * @param pathFile
	 * @return 
	 * @throws IOException
	 */
	public void runAll(String choice) throws IOException{
		File folder = new File(m_input);
		File[] listOfFiles = folder.listFiles();
		System.out.println(listOfFiles.length);
		
		
		String resuFile = m_output+File.separator+"loops.bed";
		
		if (listOfFiles.length==0){ System.out.println("Input directory empty");}
		else{
			for(int i = 0; i < listOfFiles.length;++i){
				String fileName = listOfFiles[i].toString();
				if(fileName.contains(".txt")){
					
					String tab[] = fileName.split("/");
					String chr = tab[tab.length-1];
					chr = chr.replaceAll(".txt", "");
					String outdir = m_output+File.separator+chr+File.separator;
					int nbOfbins = m_chrSize.get(chr)/m_resolution;
					System.out.println(chr+"\t"+m_chrSize.get(chr)+"\t"+nbOfbins);
					ProcessTuplesFile ptf = new ProcessTuplesFile(listOfFiles[i].toString(), m_resolution, m_matrixSize,m_step,nbOfbins,outdir);
					ArrayList<String> lmatrixFile = ptf.getList();
					if(choice.equals("oMe") ) detectLoopsOmE(lmatrixFile,chr);
					else if (choice.equals("o") ) detectLoopsO(lmatrixFile,chr);
					saveFile(resuFile);
				}
			}
		}
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void runImageProcessing(String choice) throws IOException{
		File folder = new File(m_input);
		File[] listOfFiles = folder.listFiles();
		
		if (listOfFiles.length==0){	System.out.println("Input directory empty");}
		else{
			String resuFile = m_output+File.separator+"loops.bed";
			for(int i = 0; i < listOfFiles.length;++i){
				String fileName = listOfFiles[i].toString();
				if(fileName.contains(".txt")){
					String tab[] = fileName.split("/");
					String chr = tab[tab.length-1];
					chr = chr.replaceAll(".txt", "");
					String outdir = m_output+File.separator+chr+File.separator;
					int nbOfbins = m_chrSize.get(chr)/m_resolution;
					System.out.println(chr+"\t"+m_chrSize.get(chr)+"\t"+nbOfbins);
					ProcessTuplesFile ptf = new ProcessTuplesFile( m_resolution, m_matrixSize,m_step,nbOfbins,outdir);
					ArrayList<String> lmatrixFile = ptf.getList();
					if(choice.equals("oMe") ) detectLoopsOmE(lmatrixFile,chr);
					else if (choice.equals("o") ) detectLoopsO(lmatrixFile,chr);
					saveFile(resuFile);
				}
				
			}
		}
	}

	
	/**
	 * 
	 * @param resu
	 * @param pathFile
	 * @throws IOException
	 */
	public static void saveFile (String pathFile) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathFile)));
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tNoiseScore\tAPScoreMed\tRegAPScoreMED\tAPScoreAVG\tRegAPScoreAVG\t%OfPixelInfToTheCenter\t%of0\tnbZero\n");
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			ArrayList<Integer> coord = loop.getCoordinates();
			double plop = loop.getNbZeroInTheImage()/(m_matrixSize*m_matrixSize);
			writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t255,125,255\t"+loop.getNoiseScore()
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
	
	private void detectLoopsOmE(ArrayList<String> lmatrixFile, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection(m_step, m_resolution,m_matrixSize, m_diagSize);
		for(int i =0; i < lmatrixFile.size();++i){
			TupleFileImage readFile = new TupleFileImage(lmatrixFile.get(i),m_matrixSize,m_step,i);
			String imageName = lmatrixFile.get(i).replaceAll(".txt", ".tif");
			ImagePlus imgRaw = readFile.readTupleFile();
			NonZeroCorrection nzc =new NonZeroCorrection(imgRaw);
			ArrayList<Integer> countNonZero = nzc.getNonZeroList();
			imgRaw.setTitle(imageName);
			saveFile(imgRaw,imageName);
			ImagePlus img = imgRaw.duplicate();
			readFile.correctImage(img);
			
			ImagePlus imgFilter = img.duplicate();
			
			imageProcessing(imgFilter,lmatrixFile.get(i));
			
			
			EnhanceNoiseScore ens = new EnhanceNoiseScore(imgRaw, imgFilter, chr, m_thresholdMaxima,m_thresholdMaxima+2000);
			ens.computeEnhanceScore();
			HashMap<String,Loop> temp = ens.getDataMaxima();
			System.out.println("before "+ temp.size());
			removeMaximaCloseToZero(imgRaw,temp);
			System.out.println("after "+ temp.size());
			
			PeakAnalysisScore pas = new PeakAnalysisScore(imgRaw,temp,countNonZero);
			pas.computeScore();
			
			coord.setData(m_data);
			if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, false, i,countNonZero);}
			else if(i == lmatrixFile.size()-1){	m_data = coord.imageToGenomeCoordinate(temp, false, true, i, countNonZero);}
			else m_data = coord.imageToGenomeCoordinate(temp, false, false, i, countNonZero);
		}
	}
	
	/**
	 * 
	 * @param lmatrixFile
	 * @param chr
	 * @throws IOException
	 */
	private void detectLoopsO(ArrayList<String> lmatrixFile, String chr) throws IOException{	
		CoordinatesCorrection coord = new CoordinatesCorrection(m_step, m_resolution,m_matrixSize, m_diagSize);
		for(int i =0; i < lmatrixFile.size();++i){
			TupleFileImage readFile = new TupleFileImage(lmatrixFile.get(i),m_matrixSize,m_step,i);
			String imageName = lmatrixFile.get(i).replaceAll(".txt", ".tif");
			ImagePlus imgRaw = readFile.readTupleFile();
			NonZeroCorrection nzc = new NonZeroCorrection(imgRaw);
			ArrayList<Integer> countNonZero = nzc.getNonZeroList();

			imgRaw.setTitle(imageName);
			saveFile(imgRaw,imageName);
			ImagePlus img = imgRaw.duplicate();
			readFile.correctImage(img);
			
			ImagePlus imgFilter = img.duplicate();
			ProcessMethod pm = new ProcessMethod(imgFilter,this.m_gauss,this.m_max,this.m_min);
			//pm.enhanceContrast(m_saturatedPixel);
			pm.runGaussian();
			imageName = lmatrixFile.get(i).replaceAll(".txt", "_processed.tif");
			saveFile(imgFilter,imageName);
			EnhanceNoiseScore ens = new EnhanceNoiseScore(imgRaw, imgFilter, chr, m_thresholdMaxima,m_thresholdMaxima+2000);
			ens.computeEnhanceScore();
			//ens.findMaxima();
			
			HashMap<String,Loop> temp = ens.getDataMaxima();
			System.out.println("before "+ temp.size());
			removeMaximaCloseToZero(imgRaw,temp);
			System.out.println("after "+ temp.size());
			
			PeakAnalysisScore pas = new PeakAnalysisScore(imgRaw,temp,countNonZero);
			pas.computeScore();
			
			coord.setData(m_data);
			if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, true, i,countNonZero);}
			else if(i == lmatrixFile.size()-1){	m_data = coord.imageToGenomeCoordinate(temp, true, true, i, countNonZero);}
			else m_data = coord.imageToGenomeCoordinate(temp, true, true, i, countNonZero);
			//if(i == 0){	m_data = coord.imageToGenomeCoordinate(temp, true, false, i,countNonZero);}
			//else if(i == lmatrixFile.size()-1){	m_data = coord.imageToGenomeCoordinate(temp, false, true, i, countNonZero);}
			//else m_data = coord.imageToGenomeCoordinate(temp, false, false, i, countNonZero);
		}
	}
	
	/**
	 * 
	 * @param imgFilter
	 * @param fileName
	 */
	
	private void imageProcessing(ImagePlus imgFilter, String fileName){ 
		ProcessMethod pm = new ProcessMethod(imgFilter,this.m_gauss,this.m_max,this.m_min);
		pm.enhanceContrast(this.m_saturatedPixel);
		pm.topHat();
		Strel strel = Strel.Shape.DISK.fromRadius(1);
		imgFilter.setProcessor(Morphology.erosion(imgFilter.getProcessor(), strel));
		saveFile(imgFilter, fileName.replaceAll(".txt", "_processed.tif"));
	}
	
	/**
	 * 
	 * @param imgFilter
	 * @param fileName
	 */
	
	private void  removeMaximaCloseToZero(ImagePlus imgFilter, HashMap<String,Loop> temp){ 
		Set<String> key = temp.keySet();
		Iterator<String> it = key.iterator();
		ImageProcessor ip = imgFilter.getProcessor();
		ArrayList<String> toRemove = new ArrayList<String>();
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = temp.get(cle);
			int i = loop.getX();
			int j = loop.getY();
			//System.out.println("cle  "+cle+" "+i+" "+j);
			int nb =0;
			if (i-1> 0 && i+1< imgFilter.getWidth()){
				if(j-1 > 0 && j+1 < imgFilter.getHeight()){
					for(int ii = i-1; ii <= i+1; ++ii){
						for(int jj = j-1; jj <= j+1; ++jj){
							if (ip.getPixel(ii, jj)<=0){
								nb++;
								if (nb >=1 ){
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
	public static void saveFile ( ImagePlus imagePlusInput, String pathFile)
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
}
