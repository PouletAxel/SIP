package utils;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class FindMaxima{
	/**	 */
	private ImagePlus m_img;
	private ImagePlus m_imgResu = new ImagePlus();
	private double m_noiseTolerance;

	/**
	 * 
	 * @param ip
	 */
	public FindMaxima(ImagePlus img, double noiseTolerance){
		m_img = img;
		m_noiseTolerance = noiseTolerance;
	}
	
	/**
	 * 
	 * @return
	 */
	
	
	/**
	 * 
	 * @return
	 */
	public ImagePlus runSimple(ImagePlus rawImage){
		ImagePlus temp = m_img.duplicate();
		ImageProcessor ip = temp.getProcessor();
		MaximumFinder mf = new MaximumFinder(); 
		ByteProcessor bp = mf.findMaxima(ip, m_noiseTolerance, MaximumFinder.SINGLE_POINTS, true);
		m_imgResu.setProcessor(bp);
		this.correctMaxima(rawImage);
		this.removedCloseMaxima(rawImage);
		return m_imgResu;
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
	private void correctMaxima(ImagePlus rawImage){
		ImageProcessor rawIp  = rawImage.getProcessor();
		int w = rawIp.getWidth();
		int h = rawIp.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
		for(int i = 1; i< w-1; ++i){
			for(int j=1; j< h-1; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					int max = rawIp.get(i,j);
					int imax = i;
					int jmax =j;
					for(int ii=i-1; ii<=i+1; ++ii){
						for(int jj = j-1; jj <= j+1; ++jj){	
							if(max < rawIp.get(ii, jj)){
								imax = ii;
								jmax = jj;
								max = rawIp.get(ii, jj);	
							}
						}
					}
					if (max > rawIp.get(i,j)){
						ipMaxima.set(i,j,0);
						ipMaxima.set(imax,jmax,255);
					}
				}			
			}
		}
		this.m_imgResu.setProcessor(ipMaxima);
	}
	
	/**
	 * 
	 * @param rawImage
	 */
	private void removedCloseMaxima(ImagePlus rawImage){
		ImageProcessor rawIp  = rawImage.getProcessor();
		int w = rawIp.getWidth();
		int h = rawIp.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
		for(int i = 1; i< w-1; ++i){
			for(int j= 1; j< h-1; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					for(int ii=i-2; ii<=i+2; ii++){
						for(int jj = j-2; jj <= j+2; jj++){	
							if(ipMaxima.getPixel(ii,jj) > 0){
								if(i != ii && j !=jj){
									if(rawIp.get(ii, jj) > rawIp.get(i,j))	ipMaxima.set(i,j,0);
									else ipMaxima.set(ii,jj,0);
								}
							}
						}
					}
				}			
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getMaxima(ImagePlus rawImage){
		int w = m_imgResu.getWidth();
		int h = m_imgResu.getHeight();
		ImageProcessor ipResu = m_imgResu.getProcessor();
		ImageProcessor ip = rawImage.getProcessor();
		ArrayList<String> listMaxima = new ArrayList<String>();
		for(int i =0; i< w; ++i){
			for(int j=0;j< h;++j){
				if (ipResu.getPixel(i,j) > 0 && i-j > 0){
					listMaxima.add(i+"\t"+j+"\t"+ip.getPixel(i, j));
				}
			}
		}
		return listMaxima;
	}	
	
	
	public double getNoiseTolerance(){	return this.m_noiseTolerance; }
	public void setNoiseTolerance(double noiseTolerance){	this.m_noiseTolerance = noiseTolerance; }
		
}
