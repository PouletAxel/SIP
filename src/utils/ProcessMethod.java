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
public class ProcessMethod{
	/** Raw image*/
	ImagePlus m_img;
	/** ImageProcessor of the raw image*/
	ImageProcessor m_ip;
	/** value for the min filter strength*/
	double m_minFilterRadius = 0;
	/** max filter strength*/
	double m_maxFilterRadius = 0;
	/** gaussian filter strength*/
	double m_gaussianFilterRadius = 0;
	/** ImageJ object to run the different filters*/
	RankFilters m_rF = new RankFilters();

	/**
	 * Constructor of ProcessMethod
	 * @param img ImagePlus, raw image  
	 * @param minFilterRad double, value of the min strength filter
	 * @param maxFilterRad double, value of the max strength filter
	 * @param gaussianFilterRad double, value of the gaussian strength filter
	 */
	public ProcessMethod(ImagePlus img, double minFilterRad, double maxFilterRad, double gaussianFilterRad){
		m_img = img;
		m_ip = m_img.getProcessor();
		m_gaussianFilterRadius = gaussianFilterRad;
		m_maxFilterRadius = maxFilterRad;
		m_minFilterRadius = minFilterRad;		
	}
	
	/**
	 * Constructor of ProcessMethod
	 * @param img ImagePlus, raw image 
	 * @param gaussianFilterRad double, value of the gaussian strength filter
	 */
	public ProcessMethod(ImagePlus img, double gaussianFilterRad){
		m_img = img;
		m_ip = m_img.getProcessor();
		m_gaussianFilterRadius = gaussianFilterRad;		
	}
	
	/**
	 * ImageJ method to enhance the image contrats, and enhance the structures of interest
	 * @param saturatedPixel double of the % of the saturated pixel want in the ehanced image
	 * 
	 */
	public void enhanceContrast(double saturatedPixel){
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.stretchHistogram(m_img, saturatedPixel);
		
	}
	
	/**
	 * Minimum filter method
	 * @param min double strength of the min filter
	 */
	public void runMin(double min){	
		m_rF.rank(m_ip, min, RankFilters.MIN);
	}
	
		
	/**
	 * White "tophat" method to enhance the light structure
	 * 
	 */
	public void topHat(){
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(m_ip, this.m_gaussianFilterRadius);
		m_rF.rank(m_ip, this.m_minFilterRadius, RankFilters.MIN);
		m_rF.rank(m_ip, this.m_maxFilterRadius, RankFilters.MAX);
		m_rF.rank(m_ip, this.m_maxFilterRadius, RankFilters.MAX);
		m_rF.rank(m_ip, this.m_minFilterRadius, RankFilters.MIN);
	}
	
	/**
	 * Getter of the image filtered
	 * @return ImagePlus image filtered
	 */
	public ImagePlus getImg(){
		return m_img;
	}
	
	/**
	 * Setter of the raw image
	 * @param m_img raw Image
	 */
	public void setImg(ImagePlus img){
		this.m_img = img;
	}
	

	/**
	 * Gaussian blur method
	 * run this methode on m_img
	 */
	public void runGaussian() {
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(m_ip, this.m_gaussianFilterRadius);
	}
}
