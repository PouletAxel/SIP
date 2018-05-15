package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class PeakAnalysisScore {

	private ImagePlus m_imgRaw = new ImagePlus();
	private HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	private ImageProcessor m_ipRaw;
	private ArrayList<Integer> m_countNonZero = new ArrayList<Integer> (); 
	/**
	 * 
	 * @param imgRaw
	 * @param data
	 */
	public PeakAnalysisScore(ImagePlus imgRaw, HashMap<String,Loop> data,ArrayList<Integer> countNonZero){
		this.m_imgRaw = imgRaw;
		this.m_data = data;
		this.m_ipRaw = m_imgRaw.getProcessor();
		m_countNonZero = countNonZero;
	}
	
	/**
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
			int nbPixel = 0;
			int nbZero = 0;
			//System.out.println("x:"+x+" y: "+y);
			// close de la diag
			double squareCenterMed = process3By3Square(x,y);
			double squareCenterAvg = process3By3SquareAvg(x,y);
			int nbCorner = 0;
			if(x>=5 && y>=5 && x<m_imgRaw.getWidth()-5 && y < m_imgRaw.getHeight()-5){
				if(m_countNonZero.get(x-3)> 0 && m_countNonZero.get(x-4)>0 &&  m_countNonZero.get(x-5)> 0){
					if(m_countNonZero.get(y-3)> 0 && m_countNonZero.get(y-4)>0 &&  m_countNonZero.get(y-5)> 0){
						corner += process3By3Square(x-4,y-4);
						cornerAvg += process3By3SquareAvg(x-4,y-4); 
						nbPixel += process3By3SquareSup(x-4,y-4,(int)center);
						nbZero += process3By3SquareZero(x-4,y-4);
						nbCorner++;
					}
					if(m_countNonZero.get(y+3)> 0 && m_countNonZero.get(y+4)>0 &&  m_countNonZero.get(y+5)> 0){
						corner += process3By3Square(x-4,y+4);
						cornerAvg += process3By3SquareAvg(x-4,y+4);
						nbPixel += process3By3SquareSup(x-4,y+4,(int)center);
						nbZero += process3By3SquareZero(x-4,y+4);
						nbCorner++;
					}
				}
			
				if(m_countNonZero.get(x+3)> 0 && m_countNonZero.get(x+4)>0 &&  m_countNonZero.get(x+5)> 0){
					if(m_countNonZero.get(y-3)> 0 && m_countNonZero.get(y-4)>0 &&  m_countNonZero.get(y-5)> 0){
						corner += process3By3Square(x+4,y-4);
						cornerAvg += process3By3SquareAvg(x+4,y-4);
						nbPixel += process3By3SquareSup(x+4,y-4,(int)center);
						nbZero += process3By3SquareZero(x+4,y-4);
						nbCorner++;
					}
					if(m_countNonZero.get(y+3)> 0 && m_countNonZero.get(y+4)>0 &&  m_countNonZero.get(y+5)> 0 && x-y >= 7){
						corner += process3By3Square(x+4,y+4);
						cornerAvg += process3By3SquareAvg(x+4,y+4);
						nbPixel += process3By3SquareSup(x+4,y+4,(int)center);
						nbZero += process3By3SquareZero(x+4,y+4);
						nbCorner++;
					}
				}
			}
		
			if(nbCorner > 0){
				corner = corner/nbCorner;
				cornerAvg = cornerAvg/nbCorner;
				loop.setPaScoreMed(center/corner);
				loop.setPaScoreAvg(center/cornerAvg);
				loop.setRegionalPaScoreMed(squareCenterMed/corner);
				loop.setRegionalPaScoreAvg(squareCenterAvg/cornerAvg);	
				loop.setPercentage(100*nbPixel/(nbCorner*9));
				loop.setPercentageOfZero(100*nbZero/(nbCorner*9));
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param pixelValue
	 * @return
	 */
	private int process3By3SquareSup(int x, int y, int pixelValue){
		int cmp = 0;
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){		
				if(pixelValue > this.m_ipRaw.getPixel(i, j)) ++cmp;
			}
		}
		return cmp;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int process3By3SquareZero(int x, int y){
		int cmp = 0;
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){		
				if(this.m_ipRaw.getPixel(i, j) == 0) ++cmp;
			}
		}
		return cmp;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private double process3By3Square(int x, int y){
		int []value = new int [9];
		int cmp = 0;
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){		
				value[cmp] = this.m_ipRaw.getPixel(i, j);
				++cmp;
			}
		}
		return median(value);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
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
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private double median (int[] value ){
		Arrays.sort(value);
		double median;
		if (value.length % 2 == 0)
			median = ((double)value[value.length/2] + (double)value[value.length/2 - 1])/2;
		else
			median = (double) value[value.length/2];
		return median;
	}
}
