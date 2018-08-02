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
	private ImagePlus m_img;
	/** int factor to re scale the image */
	private int m_factor; 
	/**
	 * 
	 * @param img
	 */
	public ChangeImageRes(ImagePlus img, int factor){
		m_img = img;
		m_factor = factor;
	}
	
	/**
	 * Sum the value of neighborhood of 4 pixels to make the image with bigger resolution.
	 * eg: pixel(0,0) on the new image is the snew value = pixel(0,0)+pixel(0,1)+pixel(1,0)+pixel(1,1).
	 * 
	 * @return ImagePlus
	 */
	public ImagePlus run(){
		ShortProcessor p = new ShortProcessor(m_img.getWidth()/m_factor,m_img.getWidth()/m_factor);
		ImageProcessor ip =  m_img.getProcessor();
		for(int i = 0; i <= m_img.getWidth()-1; i+=m_factor){
			for(int j = 0; j <= m_img.getWidth()-1; j+=m_factor){
				float sum = 0;
				for(int ii = i; ii <= i+m_factor-1; ++ii)
					for(int jj = j ; jj <= j+m_factor-1; ++jj)
						sum+= ip.getPixel(ii, jj);
				//System.out.println(m_factor+"\t"+i/m_factor+"\t"+j/m_factor+"\t"+sum);
				p.setf(i/m_factor,j/m_factor,sum);
			}
		}
		ImagePlus imgResu = new ImagePlus();
		imgResu.setProcessor(p);
		return imgResu;
	}
}