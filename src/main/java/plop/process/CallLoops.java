package plop.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel;
import plop.utils.CoordinatesCorrection;
import plop.utils.FilterLoops;
import plop.utils.FindMaxima;
import plop.utils.SIPObject;
import plop.utils.ImageProcessingMethod;
import plop.utils.Loop;
import plop.utils.PeakAnalysisScore;

/**
 * Class with all the methods to call the regional maxima in the images and filter and write the output loops file list
 *  
 * @author axel poulet
 *
 */
public class CallLoops {

	/** Strength of the gaussian filter*/
	private double _gauss;
	/** Strength of the min filter*/
	private double _min;
	/** Strength of the max filter*/
	private double _max;
	/** % of saturated pixel after enhance contrast*/
	private double _saturatedPixel;
	/** Image size*/
	private int _matrixSize = 0;
	/** Resolution of the bin dump in base*/
	private int _resolution;
	/** Threshold for the maxima detection*/
	private int _thresholdMaxima;
	/** Diage size to removed maxima close to diagonal*/
	private int _diagSize;
	/** Size of the step to process each chr (step = matrixSize/2)*/
	private int _step;
	/** Number of pixel = 0 allowed around the loop*/
	private int _nbZero = -1;
	/**	 structuring element for the MM method used (MorpholibJ)*/
	private Strel _strel = Strel.Shape.SQUARE.fromRadius(40);
	/**	 image background value*/
	private float _backgroudValue = (float) 0.25;
	private boolean _isCooler = false;

	/**
	 * Constructor
	 *  
	 * @param sip SIPObject
	 */
	public CallLoops(SIPObject sip){
		this._gauss = sip.getGauss();
		this._min = sip.getMin();
		this._max= sip.getMax();
		this._saturatedPixel = sip.getSaturatedPixel();
		this._matrixSize = sip.getMatrixSize();
		this._resolution = sip.getResolution();
		this._thresholdMaxima = sip.getThresholdMaxima();
		this._diagSize = sip.getDiagSize();
		this._step = sip.getStep();
		this._nbZero = sip.getNbZero();
		this._isCooler = sip.isCooler();
	}
	
	/**
	 * Detect loops method, detect the loops at two different resolution, initial resolution + 2 folds bigger
	 * call the loops first in the smaller resolution then making image with bigger resolution and fill no Zero list
	 * Make and save image at two different resolution (m_resolution and m_resolution*2)
	 * If there is a lot pixel at zero in the images adapt the threshold for the maxima detection.
	 * @param fileList list of file to process
	 * @param chr chromosome name
	 * @param normVector normalized vector of the chromosome of interest
	 * @return HashMap of loop
	 * @throws IOException throw an exception
	 */
	public HashMap<String, Loop> detectLoops(File[] fileList, String chr,
											 HashMap<Integer,String> normVector)
			throws IOException{
		CoordinatesCorrection coord = new CoordinatesCorrection();
		HashMap<String,Loop> hLoop= new HashMap<String,Loop>();
		FilterLoops filterLoops = new FilterLoops(this._resolution);
		for(int i = 0; i < fileList.length; ++i){
			if(fileList[i].toString().contains(".txt")){
				TupleFileToImage tuple = new  TupleFileToImage(fileList[i].toString(),this._matrixSize,this._resolution);
				String[] tFile = fileList[i].toString().split("_");
				int numImage = Integer.parseInt(tFile[tFile.length-2])/(this._step*_resolution);
				System.out.println(numImage+"\t"+fileList[i]);
				ImagePlus imgRaw = makeImage(tuple);
				ImagePlus imgFilter = imgRaw.duplicate();
				tuple.correctImage(imgFilter);
				ImageProcessingMethod m = new ImageProcessingMethod(imgFilter,this._min,this._max,this._gauss);
				imageProcessing(imgFilter,fileList[i].toString(), m);
				imgRaw.getTitle().replaceAll(".tif", "_N.tif");
				ImagePlus imgNorm = IJ.openImage(imgRaw.getTitle().replaceAll(".tif", "_N.tif"));
				int thresh = this._thresholdMaxima;
				double pixelPercent = (double) (100 * tuple.getNbZero()) /(this._matrixSize*this._matrixSize);
				if(pixelPercent < 7)  
					thresh =  _thresholdMaxima/5;
				FindMaxima findLoop = new FindMaxima(imgNorm, imgFilter, chr, thresh, this._diagSize, this._resolution);
				HashMap<String,Loop> temp = findLoop.findLoop(numImage, this._nbZero,imgRaw, this._backgroudValue);
				PeakAnalysisScore pas = new PeakAnalysisScore(imgNorm,temp);
				pas.computeScore();
				temp = filterLoops.removedBadLoops(temp);
				coord.setData(hLoop);
				coord.imageToGenomeCoordinate(temp, numImage,_step);
				hLoop = coord.getData();
			}
		}
		if(_isCooler)
			hLoop = filterLoops.removedLoopCloseToWhiteStrip(hLoop);
		else
			hLoop = filterLoops.removedLoopCloseToWhiteStrip(hLoop,normVector);

		System.out.println("####### End loops detection for chr "+ chr +"\t"+hLoop.size()+" loops before the FDR filter");
		return hLoop;
	}	
	

	
	/**
	 * Image processing method
	 * @param imgFilter ImagePlus to correct
	 * @param fileName Strin file name
	 * @param pm ProcessMethod object
	 */
	private void imageProcessing(ImagePlus imgFilter, String fileName, ImageProcessingMethod pm){
		pm.enhanceContrast(this._saturatedPixel);
		pm.runGaussian();
		imgFilter.setProcessor(Morphology.whiteTopHat(imgFilter.getProcessor(), _strel));
		pm.setImg(imgFilter);
		pm.runGaussian();
		pm.topHat(this._min, this._max);
		if(fileName.contains(".tif"))
			saveFile(imgFilter, fileName.replaceAll(".tif", "_processed.tif"));
		else 
			saveFile(imgFilter, fileName.replaceAll(".txt", "_processed.tif"));
	}
	

	/**
	 * Make Image
	 *
	 * @param readFile file to make the image
	 * @return ImagePlus
	 */
	private ImagePlus makeImage(TupleFileToImage readFile){
		String file = readFile.getInputFile();
		readFile.readTupleFile();
		saveFile(readFile.getNormImage(),file.replaceAll(".txt", "_N.tif"));
		ImagePlus imageOutput = readFile.getRawImage();
		imageOutput.setTitle(file.replaceAll(".txt", ".tif"));
		saveFile(imageOutput,file.replaceAll(".txt", ".tif"));
		return imageOutput;
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
}
