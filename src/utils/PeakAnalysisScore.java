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
				corner += process3By3SquareAvg(x-10,y-10); 
				corner += process3By3SquareAvg(x-10,y+10);
				corner += process3By3SquareAvg(x+10,y-10);
				corner += process3By3SquareAvg(x+10,y+10);
				corner = corner/4;
				loop.setPaScoreAvg(center/corner);
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
				loop.setRegionalPaScoreAvg(squareCenterAvg/corner);	
				int xFDR = x+10;
				int yFDR = y+10;
				if(xFDR > this._imgRaw.getWidth()-12 && yFDR > this._imgRaw.getHeight()-12){
					xFDR = x-10;
					yFDR = y-10;
				}
				float cornerFDR = 0;
				float centerFDR = this._ipRaw.getf(xFDR,yFDR);
				float squareCenterAvgFDR = process3By3SquareAvg(xFDR,yFDR);	
				cornerFDR += process3By3SquareAvg(xFDR-10,yFDR-10); 
				cornerFDR += process3By3SquareAvg(xFDR-10,yFDR+10);
				cornerFDR += process3By3SquareAvg(xFDR+10,yFDR-10);
				cornerFDR += process3By3SquareAvg(xFDR+10,yFDR+10);
				cornerFDR = cornerFDR/4;
				loop.setPaScoreAvgFDR(centerFDR/cornerFDR);
				loop.setRegionalPaScoreAvgFDR(squareCenterAvgFDR/cornerFDR);
				
				int xFDR2 = x+25;
				int yFDR2 = y+25;
				if(xFDR2 > this._imgRaw.getWidth()-12 && yFDR2 > this._imgRaw.getHeight()-12){
					xFDR2 = x-25;
					yFDR2 = y-25;
				}
				float cornerFDR2 = 0;
				float centerFDR2 = this._ipRaw.getf(xFDR2,yFDR2);
				float squareCenterAvgFDR2 = process3By3SquareAvg(xFDR2,yFDR2);	
				cornerFDR2 += process3By3SquareAvg(xFDR2-10,yFDR2-10); 
				cornerFDR2 += process3By3SquareAvg(xFDR2-10,yFDR2+10);
				cornerFDR2 += process3By3SquareAvg(xFDR2+10,yFDR2-10);
				cornerFDR2 += process3By3SquareAvg(xFDR2+10,yFDR2+10);
				cornerFDR2 = cornerFDR2/4;
				loop.setPaScoreAvgFDR2(centerFDR2/cornerFDR2);
				loop.setRegionalPaScoreAvgFDR2(squareCenterAvgFDR2/cornerFDR2);
			
				int xFDR3 = x+40;
				int yFDR3 = y+40;
				if(xFDR3 > this._imgRaw.getWidth()-12 && yFDR3 > this._imgRaw.getHeight()-12){
					xFDR3 = x-40;
					yFDR3 = y-40;
				}
				float cornerFDR3 = 0;
				float centerFDR3 = this._ipRaw.getf(xFDR3,yFDR3);
				float squareCenterAvgFDR3 = process3By3SquareAvg(xFDR3,yFDR3);	
				cornerFDR3 += process3By3SquareAvg(xFDR3-10,yFDR3-10); 
				cornerFDR3 += process3By3SquareAvg(xFDR3-10,yFDR3+10);
				cornerFDR3 += process3By3SquareAvg(xFDR3+10,yFDR3-10);
				cornerFDR3 += process3By3SquareAvg(xFDR3+10,yFDR3+10);
				cornerFDR3 = cornerFDR3/4;
				loop.setPaScoreAvgFDR3(centerFDR3/cornerFDR3);
				loop.setRegionalPaScoreAvgFDR3(squareCenterAvgFDR3/cornerFDR3);
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
}
