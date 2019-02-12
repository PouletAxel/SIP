package utils;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * Change th eimage resolution on function of a specific factor, the new value of the pixel is the sum of the original pixel value merged 
 * to create the new image.
 * 
 * @author Axel Poulet
 *
 */
public class ChangeImageRes{
	/** Image Plus of the raw image*/
	private ImagePlus _img;
	/** int factor to re scale the image */
	private int _factor; 
	
	/**
	 * Constructor 
	 * @param img ImagePlus raw image
	 * @param factor int factor of the change
	 */
	public ChangeImageRes(ImagePlus img, int factor){
		this._img = img;
		this._factor = factor;
	}
	
	/**
	 * Sum the value of neighborhood (eg:4 pixels for factor 2) to make the image with bigger resolution.
	 * eg: pixel(0,0) on the new image is the snew value = pixel(0,0)+pixel(0,1)+pixel(1,0)+pixel(1,1).
	 * 
	 * @return ImagePlus ImagePlus results 
	 */
	public ImagePlus run(){
		ShortProcessor p = new ShortProcessor(this._img.getWidth()/this._factor,this._img.getWidth()/this._factor);
		ImageProcessor ip =  this._img.getProcessor();
		for(int i = 0; i <= this._img.getWidth()-1; i+=this._factor){
			for(int j = 0; j <= this._img.getWidth()-1; j+=this._factor){
				float sum = 0;
				for(int ii = i; ii <= i+this._factor-1; ++ii)
					for(int jj = j ; jj <= j+this._factor-1; ++jj){
						sum+= ip.getf(ii, jj);
					}
				p.setf(i/this._factor,j/this._factor,sum);
				
			}
		}
		ImagePlus imgResu = new ImagePlus();
		imgResu.setProcessor(p);
		return imgResu;
	}
	
	/**
	 * Sum the value of neighborhood (eg:4 pixels for factor 2) to make the image with bigger resolution.
	 * eg: pixel(0,0) on the new image is the snew value = pixel(0,0)+pixel(0,1)+pixel(1,0)+pixel(1,1).
	 * 
	 * @return ImagePlus ImagePlus results 
	 */
	public ImagePlus runNormalized(){
		FloatProcessor p = new FloatProcessor(this._img.getWidth()/this._factor,this._img.getWidth()/this._factor);
		ImageProcessor ip =  this._img.getProcessor();
		for(int i = 0; i <= this._img.getWidth()-1; i+=this._factor){
			for(int j = 0; j <= this._img.getWidth()-1; j+=this._factor){
				float sum = 0;
				for(int ii = i; ii <= i+this._factor-1; ++ii)
					for(int jj = j ; jj <= j+this._factor-1; ++jj){
						sum+= ip.getf(ii, jj);
					}
				p.setf(i/this._factor,j/this._factor,sum);
				
			}
		}
		ImagePlus imgResu = new ImagePlus();
		imgResu.setProcessor(p);
		return imgResu;
	}
}