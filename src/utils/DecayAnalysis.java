package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Class for the loop decay analysis. Take in input the raw imagePlus, and the loop coordiantes.
 * Compute the average between the loop and the neighbourhood 8 or 24.
 *  
 * @author Axel Poulet
 *
 */
public class DecayAnalysis {
	/**raw imagePlus */
	private ImagePlus _img;
	/** x coordinate's of the loop of interest*/
	private int _x;
	/** y coordinate's of the loop of interest*/
	private int _y;
	
	/**
	 * DecayAnalysis constructor.
	 * 
	 * @param img: ImagePlus the raw image 
	 * @param x: int for the x coordinate's of the loop
	 * @param y: int for the y coordinat's of the loop
	 */
	public DecayAnalysis(ImagePlus img, int x, int y){
		this._x = x;
		this._y = y;
		this._img = img;
	}
	
	/**
	 * getter computing the average difference beetwen the loop value and the neighbourhood 8 values
	 * @return double stocking the average differential value of the neighbourhood 8.
	 */
	public float getNeighbourhood1(){return computeDiff(1);}
	
	/**
	 * getter computing the average difference beetwen the loop value and the neighbourhood 24 values (exclude the values of the 8 neighbourhood)
	 * @return double stocking the average differential value of the neighbourhood 24.
	 */
	public float getNeighbourhood2(){return computeDiff(2);}

	
	/**
	 * getter computing the average difference beetwen the loop value and the neighbourhood 24 values (exclude the values of the 8 neighbourhood)
	 * @return double stocking the average differential value of the neighbourhood 24.
	 */
	public float getNeighbourhood3(){return computeDiff(3);}
	
	/**
	 * Private method computing the average difference between the loop value and the chosen neighbourhood. 
	 * @param c int; stock the choice: 1 = neighbourhood 8; 2 = neighbourhood 24
	 * @return double differentila average
	 */
	private float computeDiff (int c ){
		float sum = 0;
		ImageProcessor ip = this._img.getProcessor();
		int nb = 0; 
		for(int i = this._x-c; i <= this._x+c; ++i){
			for(int j = this._y-c ; j <= this._y+c; ++j){
				if((i != this._x || j != this._y)  && (i-this._x == -c || j-this._y == -c || i-this._x == c || j-this._y == c)){
					if(i >=0 && j>= 0){
						double a =  ip.getf(this._x, this._y)- ip.getf(i, j);
						sum+= a;
						++nb;
					}
				}
			}
		}
		sum = sum/nb;
		return sum;
	}
}