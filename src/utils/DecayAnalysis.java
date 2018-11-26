package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Class for the loop decay analysis. Take in input the raw imagePlus, and the loop coordiantes.
 * Compute the average between the loop and the neighbourhood 8 or 24.
 *  
 * @author axel poulet
 *
 */
public class DecayAnalysis {
	/**raw imagePlus */
	private ImagePlus m_img;
	/** x coordinate's of the loop of interest*/
	private int m_x;
	/** y coordinate's of the loop of interest*/
	private int m_y;
	
	/**
	 * DecayAnalysis constructor.
	 * 
	 * @param img: ImagePlus the raw image 
	 * @param x: int for the x coordinate's of the loop
	 * @param y: int for the y coordinat's of the loop
	 */
	public DecayAnalysis(ImagePlus img, int x, int y){
		m_x = x;
		m_y = y;
		m_img = img;
	}
	
	
	/**
	 * getter computing the average difference beetwen the loop value and the neighbourhood 8 values
	 * @return double stocking the average differential value of the neighbourhood 8.
	 */
	public double getNeighbourhood1(){
		return computeDiff(1);
	}
	
	/**
	 * getter computing the average difference beetwen the loop value and the neighbourhood 24 values (exclude the values of the 8 neighbourhood)
	 * @return double stocking the average differential value of the neighbourhood 24.
	 */
	public double getNeighbourhood2(){
		return computeDiff(2);
	}

	
	/**
	 * getter computing the average difference beetwen the loop value and the neighbourhood 24 values (exclude the values of the 8 neighbourhood)
	 * @return double stocking the average differential value of the neighbourhood 24.
	 */
	public double getNeighbourhood3(){
		return computeDiff(3);
	}
	/**
	 * Private method computing the average difference between the loop value and the chosen neighbourhood. 
	 * @param c int; stock the choice: 1 = neighbourhood 8; 2 = neighbourhood 24
	 * @return double differentila average
	 */
	private double computeDiff (int c ){
		double sum = 0;
		ImageProcessor ip = m_img.getProcessor();
		int nb = 0; 
		for(int i = m_x-c; i <= m_x+c; ++i){
			for(int j = m_y-c ; j <= m_y+c; ++j)
				if((i != m_x || j != m_y)  && (i-m_x == -c || j-m_y == -c || i-m_x == c || j-m_y == c)){
					if(i >=0 && j>= 0){
						double a =  ip.get(m_x, m_y)- ip.get(i, j);
						sum+= a;
						++nb;
					}
				}
		}
		sum = sum/nb;
		return sum;
	}
}
