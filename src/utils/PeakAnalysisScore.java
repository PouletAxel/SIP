package utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Peak analysis score. 
 * peaks analysis score is adapted from the APA score from Rao&Huntley et al., 2014: 
 * @author axel poulet
 *
 */
public class PeakAnalysisScore {
	/** Raw image of the matrix*/
	private ImagePlus m_imgRaw = new ImagePlus();
	/** HashMap of object loops*/
	private HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	/** ImageProcessor of the raw ImagePlus*/
	private ImageProcessor m_ipRaw;
		
	/**
	 * Constructor of PeakAnalysisScore
	 * @param imgRaw ImagePlus raw image
	 * @param data HashMap of loops
	 */
	public PeakAnalysisScore(ImagePlus imgRaw, HashMap<String,Loop> data){
		this.m_imgRaw = imgRaw;
		this.m_data = data;
		this.m_ipRaw = m_imgRaw.getProcessor();
	}
		
	
	/**
	 * Method to compute the score of each loop. on a 11*11 square, the average of the corner (3*3) are computed. then the ration between the loops value and this avg is computed.
	 * For the regional value, the avg of the n_8 value of the loops are done, then a ratio is computed with the avg value of the corner,
	 * This method is used for the observed and oMe method.
	 * 
	 */
	public void computeScore(){
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			int x = loop.getX();
			int y = loop.getY();
			double corner = 0;
			double cornerAvg = 0;
			double center = m_ipRaw.getPixel(x, y);
			double squareCenterAvg = process3By3SquareAvg(x,y);
			int nbCorner = 0;
			if(x >= 5 && y >= 5 && x < m_imgRaw.getWidth()-5 && y < m_imgRaw.getHeight()-5){
				cornerAvg += process3By3SquareAvg(x-4,y-4); 
				cornerAvg += process3By3SquareAvg(x-4,y+4);
				cornerAvg += process3By3SquareAvg(x+4,y-4);
				cornerAvg += process3By3SquareAvg(x+4,y+4);
				nbCorner = 4;
			}
		
			if(nbCorner > 0){
				corner = corner/nbCorner;
				cornerAvg = cornerAvg/nbCorner;
				loop.setPaScoreAvg(center/cornerAvg);
				loop.setRegionalPaScoreAvg(squareCenterAvg/cornerAvg);	
			}
		}
	}
	

	
	/**
	 * compute the avg of3*3 square
	 * @param x int coordinate of the pixel center
	 * @param y int coordinat of the pixel center
	 * @return
	 */
	private double process3By3SquareAvg(int x, int y){
		int sum = 0;
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				sum += this.m_ipRaw.getPixel(i,j);
			}
		}
		return sum/9;
	}
}
