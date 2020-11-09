package utils;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import ij.process.ImageProcessor;

/**
 * Method of image pre-processing before call maxima, to enhance the loops and reduce the noise
 * This class is using the ImageJ methode for the different filters (min, max, gaussian).
 * 
 * @author axel poulet
 *
 */
public class ImageProcessingMethod{
	/** Raw image*/
	private ImagePlus _img;
	/** ImageProcessor of the raw image*/
	private ImageProcessor _ip;
	/** value for the min filter strength*/
	private double _minFilterRadius = 0;
	/** max filter strength*/
	private double _maxFilterRadius = 0;
	/** gaussian filter strength*/
	private double _gaussianFilterRadius = 0;
	/** ImageJ object to run the different filters*/
	private RankFilters _rF = new RankFilters();

	/**
	 * Constructor of ProcessMethod
	 * @param img ImagePlus, raw image  
	 * @param minFilterRad double, value of the min strength filter
	 * @param maxFilterRad double, value of the max strength filter
	 * @param gaussianFilterRad double, value of the gaussian strength filter
	 */
	public ImageProcessingMethod(ImagePlus img, double minFilterRad, double maxFilterRad, double gaussianFilterRad){
		this._img = img;
		this._ip = this._img.getProcessor();
		this._gaussianFilterRadius = gaussianFilterRad;
		this._maxFilterRadius = maxFilterRad;
		this._minFilterRadius = minFilterRad;		
	}
	
	/**
	 * Constructor of ProcessMethod
	 * @param img ImagePlus, raw image 
	 * @param gaussianFilterRad double, value of the gaussian strength filter
	 */
	public ImageProcessingMethod(ImagePlus img, double gaussianFilterRad){
		this._img = img;
		this._ip = this._img.getProcessor();
		this._gaussianFilterRadius = gaussianFilterRad;		
	}

	
	/**
	 * ImageJ method to enhance the image contrats, and enhance the structures of interest
	 * @param saturatedPixel double of the % of the saturated pixel want in the ehanced image
	 * 
	 */
	public void enhanceContrast(double saturatedPixel){
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.stretchHistogram(this._img, saturatedPixel);
		
	}
	
	/**
	 * Minimum filter method
	 * @param min double strength of the min filter
	 */
	public void runMin(double min){	
		this._rF.rank(this._ip, min, RankFilters.MIN);
	}

	
	/**
	 * Setter of the raw image
	 * @param img raw Image
	 */
	public void setImg(ImagePlus img){ this._img = img;}
	

	/**
	 * Gaussian blur method
	 * run this methode on m_img
	 */
	public void runGaussian() {
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(_ip, this._gaussianFilterRadius);
	}

	/**
	 * 
	 * @param max max filter strength
	 */
	public void runMax(double max) {
		_rF.rank(_ip, max, RankFilters.MAX);
	}
}
