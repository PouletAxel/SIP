package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * Class to make the image with 2 fold bigger resolution than the original image.
 * 
 * @author axel poulet
 *
 */
public class ChangeImageRes{
	/** Image Plus of the raw image*/
	ImagePlus m_img;
	
	/**
	 * 
	 * @param img
	 */
	public ChangeImageRes(ImagePlus img){
		m_img = img;
	}
	
	/**
	 * Sum the value of neighborhood of 4 pixels to make the image with bigger resolution.
	 * eg: pixel(0,0) on the new image is the snew value = pixel(0,0)+pixel(0,1)+pixel(1,0)+pixel(1,1).
	 * 
	 * @return ImagePlus
	 */
	public ImagePlus run(){
		ShortProcessor p = new ShortProcessor(m_img.getWidth()/2,m_img.getWidth()/2);
		ImageProcessor ip =  m_img.getProcessor();
		for(int i = 0; i <= m_img.getWidth()-1; i+=2){
			for(int j = 0; j <= m_img.getWidth()-1; j+=2){
				float sum = 0;
				for(int ii = i; ii <= i+1; ++ii)
					for(int jj = j ; jj <= j+1; ++jj)
						sum+= ip.getPixel(ii, jj);
				
				p.setf(i/2,j/2,sum);
			}
		}
		ImagePlus imgResu = new ImagePlus();
		imgResu.setProcessor(p);
		return imgResu;
	}
}