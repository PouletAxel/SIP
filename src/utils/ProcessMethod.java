package utils;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import ij.process.ImageProcessor;

/**
 * 
 * @author plop
 *
 */
public class ProcessMethod{
	/** */
	ImagePlus m_img = new ImagePlus();
	/** */
	ImageProcessor m_ip;
	/** */
	double m_minFilterRadius = 0;
	/** */
	double m_maxFilterRadius = 0;
	/** */
	double m_gaussianFilterRadius = 0;
	/** */
	RankFilters m_rF = new RankFilters();

	/**
	 * 
	 * @param img
	 * @param minFilterRad
	 * @param maxFilterRad
	 * @param gaussianFilterRad
	 */
	public ProcessMethod(ImagePlus img, double minFilterRad, double maxFilterRad, double gaussianFilterRad){
		m_img = img;
		m_ip = m_img.getProcessor();
		m_gaussianFilterRadius = gaussianFilterRad;
		m_maxFilterRadius = maxFilterRad;
		m_minFilterRadius = minFilterRad;		
	}
	
	/**
	 * 
	 * @param img
	 * @param gaussianFilterRad
	 */
	public ProcessMethod(ImagePlus img, double gaussianFilterRad){
		m_img = img;
		m_ip = m_img.getProcessor();
		m_gaussianFilterRadius = gaussianFilterRad;		
	}
	
	/**
	 * //rao 2015 => 0.05
	 * //cubenas 0.02
	 */
	public void enhanceContrast(double saturatedPixel){
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.stretchHistogram(m_img, saturatedPixel);
		
	}
	
	/**
	 * 
	 * @param min
	 */
	public void runMin(double min){	
		m_rF.rank(m_ip, min, RankFilters.MIN);
	}
	
		
	/**
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
	 * 
	 * @return
	 */
	public ImagePlus getImg(){
		return m_img;
	}
	
	/**
	 * 
	 * @param m_img
	 */
	public void setImg(ImagePlus img){
		this.m_img = img;
	}
	

	/**
	 * 
	 */
	public void runGaussian() {
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(m_ip, this.m_gaussianFilterRadius);
	}
}
