package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * 
 * @author axel poulet
 *
 */
public class DecayAnalysis {
	/** */
	private ImagePlus m_img;
	/** */
	private int m_x;
	/** */
	private int m_y;
	
	/**
	 * 
	 * @param img
	 * @param x
	 * @param y
	 */
	public DecayAnalysis(ImagePlus img, int x, int y){
		m_x = x;
		m_y = y;
		m_img = img;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	private double computeDiff (int c ){
		double sum = 0;
		ImageProcessor ip = m_img.getProcessor();
		int nb = 0; 
		for(int i = m_x-c; i <= m_x+c; ++i){
			for(int j = m_y-c ; j <= m_y+c; ++j)
				if((i != m_x || j != m_y)  && (i-m_x == -c || j-m_y == -c || i-m_x == c || j-m_y == c)){
					double a =  ip.getPixel(m_x, m_y)- ip.getPixel(i, j);
					//int b = i-m_x;
					//int d = j-m_y;
					//System.out.println(b+" "+d+" "+c);
					sum+= a;
					++nb;
				}
		}
		sum = sum/nb;
		return sum;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getNeighbourhood1(){
		//System.out.println("prout 1");
		return computeDiff(1);
	}
	
	/**
	 * 
	 * @return
	 */
	public double getNeighbourhood2(){
		//System.out.println("prout 2");
		return computeDiff(2);
	}
}
