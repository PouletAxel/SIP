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
	private ImagePlus _imgRaw = new ImagePlus();
	/** HashMap of object loops*/
	private HashMap<String,Loop> _data = new HashMap<String,Loop>();
	/** ImageProcessor of the raw ImagePlus*/
	private ImageProcessor _ipRaw;
		
	/**
	 * Constructor of PeakAnalysisScore
	 * @param imgRaw ImagePlus raw image
	 * @param data HashMap of loops
	 */
	public PeakAnalysisScore(ImagePlus imgRaw, HashMap<String,Loop> data){
		this._imgRaw = imgRaw;
		this._data = data;
		this._ipRaw = _imgRaw.getProcessor();
	}
		
	
	/**
	 * Method to compute the score of each loop. on a 11*11 square, the average of the corner (3*3) are computed. then the ration between the loops value and this avg is computed.
	 * For the regional value, the avg of the n_8 value of the loops are done, then a ratio is computed with the avg value of the corner,
	 * This method is used for the observed and oMe method.
	 * 
	 */
	public void computeScore(){
		Set<String> key = this._data.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = this._data.get(cle);
			int x = loop.getX();
			int y = loop.getY();
			float corner = 0;
			float center = this._ipRaw.getf(x, y);
			float squareCenterAvg = process3By3SquareAvg(x,y);

			if(x >= 5 && y >= 5 && x < this._imgRaw.getWidth()-5 && y < this._imgRaw.getHeight()-5){
				corner += process3By3SquareAvg(x-4,y-4); 
				corner += process3By3SquareAvg(x-4,y+4);
				corner += process3By3SquareAvg(x+4,y-4);
				corner += process3By3SquareAvg(x+4,y+4);
				corner = corner/4;
				loop.setPaScoreAvg(center/corner);
				loop.setRegionalPaScoreAvg(squareCenterAvg/corner);	
				int xFDR = x+10;
				int yFDR = y+10;
				if(xFDR > this._imgRaw.getWidth()-5 && yFDR > this._imgRaw.getHeight()-5){
					xFDR = x-10;
					yFDR = y-10;
				}
				float cornerFDR = 0;
				float centerFDR = this._ipRaw.getf(xFDR,yFDR);
				float squareCenterAvgFDR = process3By3SquareAvg(xFDR,yFDR);	
				cornerFDR += process3By3SquareAvg(xFDR-4,yFDR-4); 
				cornerFDR += process3By3SquareAvg(xFDR-4,yFDR+4);
				cornerFDR += process3By3SquareAvg(xFDR+4,yFDR-4);
				cornerFDR += process3By3SquareAvg(xFDR+4,yFDR+4);
				cornerFDR = cornerFDR/4;
				loop.setPaScoreAvgFDR(centerFDR/cornerFDR);
				loop.setRegionalPaScoreAvgFDR(squareCenterAvgFDR/cornerFDR);
			}
		}
	}
	

	
	/**
	 * compute the avg of3*3 square
	 * @param x int coordinate of the pixel center
	 * @param y int coordinat of the pixel center
	 * @return float average
	 */
	private float process3By3SquareAvg(int x, int y){
		float sum = 0;
		int nb = 0;
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				if(i < this._ipRaw.getWidth() && i>0 && j < this._ipRaw.getWidth() && j > 0){
					sum += this._ipRaw.getf(i,j);
					nb++;
				}
			}
		}
		if(nb == 0)
			return 0;
		return sum/nb;
	}
}
