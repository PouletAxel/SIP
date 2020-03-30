package utils;

import java.util.HashMap;
import java.lang.Math;
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

			if(x >= 12 && y >= 12 && x < this._imgRaw.getWidth()-12 && y < this._imgRaw.getHeight()-12){
				corner = computeAvgCorner(x,y);
				loop.setPaScoreAvg(center/corner);
				loop.setRegionalPaScoreAvg(squareCenterAvg/corner);	
				
				float probnum = 0;
				float factorial = 1;
				int countnumber = (int) (corner);
				for (int i = 0; i < countnumber; i++) {
					if (i == 0) {
						factorial = 1;
					}else {
						factorial = 1;
						for (int j = 1; j<= i; j++) factorial = factorial*j;
					}
					float tmpprobnum = (float) ((Math.pow(2.718,((center)*-1))*Math.pow((center),i))/factorial);
					probnum = probnum + tmpprobnum;
				}
				loop.setPaScoreAvgdev(1-probnum);
				
				int xFDR = x+10;
				int yFDR = y+10;
				if(xFDR > this._imgRaw.getWidth()-12 || yFDR > this._imgRaw.getHeight()-12){
					xFDR = x-10;
					yFDR = y-10;
				}
				
				float centerFDR = this._ipRaw.getf(xFDR,yFDR);
				float squareCenterAvgFDR = process3By3SquareAvg(xFDR,yFDR);	
				loop.setPaScoreAvgFDR(centerFDR/computeAvgCorner(xFDR,yFDR));
				loop.setRegionalPaScoreAvgFDR(squareCenterAvgFDR/computeAvgCorner(xFDR,yFDR));
				
				int xFDR2 = x+25;
				int yFDR2 = y+25;
				if(xFDR2 > this._imgRaw.getWidth()-12 || yFDR2 > this._imgRaw.getHeight()-12){
					xFDR2 = x-25;
					yFDR2 = y-25;
					if(xFDR2 <= 0) xFDR2 = 13;
					if(yFDR2 <= 0) yFDR2 = 13;
				}
				//System.out.println(xFDR2+" "+yFDR2);
				float centerFDR2 = this._ipRaw.getf(xFDR2,yFDR2);
				float squareCenterAvgFDR2 = process3By3SquareAvg(xFDR2,yFDR2);	
				loop.setPaScoreAvgFDR2(centerFDR2/computeAvgCorner(xFDR2,yFDR2));
				loop.setRegionalPaScoreAvgFDR2(squareCenterAvgFDR2/computeAvgCorner(xFDR2,yFDR2));
			
				int xFDR3 = x+40;
				int yFDR3 = y+40;
				if(xFDR3 > this._imgRaw.getWidth()-12 || yFDR3 > this._imgRaw.getHeight()-12){
					xFDR3 = x-40;
					yFDR3 = y-40;
					if(xFDR3 <= 0) xFDR3 = 13;
					if(yFDR3 <= 0) yFDR3 = 13;
				}
				
				float centerFDR3 = this._ipRaw.getf(xFDR3,yFDR3);
				float squareCenterAvgFDR3 = process3By3SquareAvg(xFDR3,yFDR3);	
				loop.setPaScoreAvgFDR3(centerFDR3/computeAvgCorner(xFDR3,yFDR3));
				loop.setRegionalPaScoreAvgFDR3(squareCenterAvgFDR3/computeAvgCorner(xFDR3,yFDR3));
			}
		}
	}
	
	/**
	 * compute the avg of5*5 square
	 * @param x int coordinate of the pixel center
	 * @param y int coordinat of the pixel center
	 * @return float average
	 */
	private float process3By3SquareAvg(int x, int y){
		float sum = 0;
		int nb = 0;
		for(int i = x-2; i <= x+2; ++i){
			for(int j = y-2; j <= y+2; ++j){
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
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private float computeAvgCorner(int x, int y){
		float corner = process3By3SquareAvg(x-10,y-10); 
		corner += process3By3SquareAvg(x-10,y+10);
		corner += process3By3SquareAvg(x+10,y-10);
		corner += process3By3SquareAvg(x+10,y+10);
		if( corner == 0) corner = (float) 0.1;
		return corner/4;
	}
}
