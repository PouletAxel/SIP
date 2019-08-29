package utils;

import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Detection of regional maxima in image. Return the HashMap<String,Loop>, the loop can be corrected. 
 * the class uses is the imageJ class to detect the maxima.
 * 
 * @author Axel Poulet
 *
 */
public class FindMaxima{
	/**	 raw image*/
	private ImagePlus _imgNorm;
	/**	 name of the chromosome*/
	private String _chr;
	/**	 Image fileterred with min, max and gaussian filter*/
	private ImagePlus _imgFilter;
	/**	binary image pixel white = the maxima detected*/
	private ImagePlus _imgResu = new ImagePlus();
	/**	 threshold for the imageJ class MaximumFinder, this class is call to detecte the maxima */
	private double _noiseTolerance =-1;
	/**	diagonal size in bin*/
	private int _diagSize =-1;
	/**	 Resolution of the image in base*/
	private int _resolution = -1;

	
	/**
	 * Constructor of FindMaxima
	 * @param img ImagePlus raw image
	 * @param imgFilter ImagePlus filtered image
	 * @param chr String chromosome
	 * @param noiseTolerance double threshold to detect maxima
	 * @param diag int the size of the diagonal
	 * @param resolution int size of the pixel in base
	 */
	public FindMaxima(ImagePlus img, ImagePlus imgFilter, String chr, double noiseTolerance, int diag, int resolution){
		this._imgNorm =  img;
		this._imgFilter = imgFilter;
		this._noiseTolerance = noiseTolerance;
		this._chr = chr;
		this._diagSize = diag;
		this._resolution = resolution;
	}
	
	
	/**
	 * Method to find loops in oMe image, and fill the loop collection.
	 * 
	 * @param hichip
	 * @param index int index of the image
	 * @param nbZero int nb zero allowed around the loop
	 * @param raw ImagePlus raw image
	 * @param val int background value of the image
	 * @param factor
	 * @return
	 */
	public HashMap<String,Loop> findloop(boolean hichip, int index, int nbZero, ImagePlus raw, float val, int factor){
		run(nbZero, raw, val);
		ArrayList<String> temp = this.getMaxima();
		ImageProcessor ipN = this._imgNorm.getProcessor();
		HashMap<String,Loop>  data = new HashMap<String,Loop>(); 
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			String name= this._chr+"\t"+temp.get(j)+"\t"+index;
			float avg = average(x,y);
			float std =standardDeviation(x,y,avg);
			if(avg > 1.45*factor*factor && ipN.getf(x, y) >= 1.85*factor*factor){ // filter on the loop value and region value
				DecayAnalysis da = new DecayAnalysis(this._imgNorm,x,y);
				float n1 =da.getNeighbourhood1();
				float n2 =da.getNeighbourhood2();				
				/*if(hichip){
					if(n1 > 2 && n2 > 2){ // filter on the neighborood for hichip datatset
						Loop maxima = new Loop(temp.get(j),x,y,this._chr,avg,std,ipN.getf(x, y));
						maxima.setNeigbhoord1(n1);
						maxima.setNeigbhoord2(n2);
						maxima.setResolution(this._resolution);
						maxima.setDiagSize(this._diagSize);
						maxima.setMatrixSize(this._imgNorm.getWidth());
						data.put(name, maxima);
					}
				}else{*/
					if(n1 < n2 && n1 >= 0.15 && n2 >= 0.25){ // filter on the neighborood for hic datatset
						Loop maxima = new Loop(temp.get(j),x,y,this._chr,avg,std,ipN.getf(x, y));
						maxima.setNeigbhoord1(n1);
						maxima.setNeigbhoord2(n2);
						maxima.setResolution(this._resolution);
						maxima.setDiagSize(this._diagSize);
						maxima.setMatrixSize(this._imgNorm.getWidth());
						data.put(name, maxima);
					}
				//}
			}
		}
		return data;
	}
		
	/**
	 * Detect maxima with the oMe or observed methods, call the different methods 
	 * to detect the maxima and correct them. 
	 * @param nbZero nb zero allowed around the loops
	 * @param rawImage	ImagePlus raw image
	 * @param backgroundValue background value of the image
	 */
	private void run(int nbZero, ImagePlus rawImage, float backgroundValue){
		ImagePlus temp = this._imgFilter.duplicate();
		ImageProcessor ip = temp.getProcessor();
		MaximumFinder mf = new MaximumFinder(); 
		ByteProcessor bp = mf.findMaxima(ip, this._noiseTolerance, MaximumFinder.SINGLE_POINTS, true);
		this._imgResu.setProcessor(bp);
		this.removedCloseMaxima();
		this.correctMaxima();
		this.removeMaximaCloseToZero(nbZero,rawImage, backgroundValue);
	}
	
	
	/**
	 * Save the image 
	 * @param imagePlusInput: ImagePlus to save
	 * @param pathFile: path file for the image
	 */	
	public static void saveFile ( ImagePlus imagePlusInput, String pathFile){
		FileSaver fileSaver = new FileSaver(imagePlusInput);
	    fileSaver.saveAsTiff(pathFile);
	}

	/**
	 * Correction of the maxima. Search around the detected maxima on the raw image,
	 * To correct the shift of maxima due to the gaussian, min and max filter.
	 */
	private void correctMaxima(){
		ImageProcessor rawIpNorm  = this._imgNorm.getProcessor();
		int w = rawIpNorm.getWidth();
		int h = rawIpNorm.getHeight();
		ImageProcessor ipMaxima = this._imgResu.getProcessor();
		for(int i = 1; i< w-1; ++i){
			for(int j=2; j< h-2; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					double max = rawIpNorm.getf(i,j);
					int imax = i;
					int jmax =j;
					for(int ii=i-1; ii<=i+1; ++ii){
						for(int jj = j-1; jj <= j+1; ++jj){
							if(max < rawIpNorm.getf(ii, jj) && Math.abs(ii-jj) >= Math.abs(i-j)){
								imax = ii;
								jmax = jj;
								max = rawIpNorm.getf(ii, jj);
							}
						}
					}
					if (max > rawIpNorm.getf(i,j)){
						ipMaxima.set(i,j,0);
						ipMaxima.set(imax,jmax,255);
					}
				}			
			}
		}
		this._imgResu.setProcessor(ipMaxima);
	}
	
	/**
	 * Test if two maxima are close removed this one which possess the smaller value.
	 * Test a 48 neighbourhood region
	 */
	private void removedCloseMaxima(){
		ImageProcessor rawIpNorm  = this._imgNorm.getProcessor();
		int w = rawIpNorm.getWidth();
		int h = rawIpNorm.getHeight();
		ImageProcessor ipMaxima = this._imgResu.getProcessor();
		for(int i = 1; i < w-1; ++i){
			for(int j= 1; j < h-1; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					for(int ii = i-2; ii <= i+2; ++ii){
						for(int jj = j-2; jj <= j+2; ++jj){	
							if(ipMaxima.getPixel(ii,jj) > 0){
								if(i != ii || j != jj){
									if(rawIpNorm.getf(ii, jj) > rawIpNorm.getf(i,j))
										ipMaxima.set(i,j,0);
									else
										ipMaxima.set(ii,jj,0);							
								}
							}
						}
					}
				}			
			}
		}
		this._imgResu.setProcessor(ipMaxima);
	}

	/**
	 * getter of maxima
	 * retrun the list of maxima as list of string i\tj\tvalue
	 * @return  ArrayList<String> 
	 */
	public ArrayList<String> getMaxima(){
		int w = this._imgResu.getWidth();
		int h = this._imgResu.getHeight();
		ImageProcessor ipResu = this._imgResu.getProcessor();
		ArrayList<String> listMaxima = new ArrayList<String>();
		for(int i = 0; i < w; ++i){
			for(int j = 0; j < h; ++j){
				if (ipResu.getf(i,j) > 0 && i-j > 0)
					listMaxima.add(i+"\t"+j);
			}
		}
		return listMaxima;
	}	

	/**
	 * Compute the average on the neighbourhood 9
	 * @param x  int x coordinate's of the loop
	 * @param y  int y coordinate's of the loop
	 * @return double avg around the loop on the neighbourhood
	 */
	private float average(int x, int y){
		float sum = 0;
		int nb=0;
		ImageProcessor ip = this._imgNorm.getProcessor();
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				if(i < ip.getWidth() && i>0 && j < ip.getWidth() && j > 0){
					sum +=ip.getf(i, j);
					++nb;
				}
			}
		}
		return sum/nb;
	}
	
	
	/**
	 * 
	 * @param x  int x coordinate's of the loop
	 * @param y  int y coordinate's of the loop
	 * @return Strip
	 */
	@SuppressWarnings("unused")
	private Strip stripX(int x, int y){
		Strip strip = null;
		float sum = 0;
		float sumLeft = 0;
		float sumRight = 0;
		int nb = 0;
		ArrayList<Float> list = new ArrayList<Float>();
		ImageProcessor ip = this._imgNorm.getProcessor();
		for(int i = x; i >= y; --i){
			sum +=ip.getf(i, y)+ip.getf(x-1, y)+ip.getf(x+1, y);
			sumLeft +=ip.getf(i, y-2)+ip.getf(i, y-3)+ip.getf(i, y-4);
			sumRight +=ip.getf(i, y+2)+ip.getf(i, y+3)+ip.getf(i, y+4);
			sum +=ip.getf(i, y)+ip.getf(i, y-1)+ip.getf(i, y+1);
			nb+=3;
			list.add( ip.getf(i,y-1));
			list.add(ip.getf(i,y));
			list.add(ip.getf(i,y+1));
		}
		sumLeft = sumLeft/nb;
		sumRight = sumRight/nb;
		float semc = 0;
		for(int i = 0; i < list.size();++i){
			semc += (list.get(i)-sum)*(list.get(i)-sum);
		}
		semc = (float)Math.sqrt(semc);
		//System.out.println("avg "+sum+" std "+semc);
		if(sum > sumLeft && sum > sumRight){
			String name = "X\t"+x;
			strip = new Strip(name,this._chr, x-1,y-1,x-1,x+1);
			strip.setLeftNeigStrip(sumLeft);
			strip.setLeftNeigStrip(sumRight);
			strip.setStripValue(sum);
			strip.setStripStd(semc);
			strip.setSize(nb*this._resolution);
		}
		return strip;
	}
	
	/**
	 * 
	 * @param x  int x coordinate's of the loop
	 * @param y  int y coordinate's of the loop
	 * @return Strip
	 */
	@SuppressWarnings("unused")
	private Strip stripY(int x, int y){
		Strip strip = null;
		float sum = 0;
		float sumLeft = 0;
		float sumRight = 0;
		int nb = 0;
		ArrayList<Float> list = new ArrayList<Float>();
		ImageProcessor ip = this._imgNorm.getProcessor();
		for(int j = y; j <= x; ++j){
			sum +=ip.getf(x, j)+ip.getf(x-1, j)+ip.getf(x+1, j);
			sumLeft = sumLeft+ip.getf(x-2, j)+ip.getf(x-3, j)+ip.getf(x-4, j);
			sumRight = sumRight+ip.getf(x+2, j)+ip.getf(x+3, j)+ip.getf(x+4, j);
			nb+=3;
			list.add(ip.getf(x-1,j));
			list.add(ip.getf(x,j));
			list.add(ip.getf(x+1,j));
		}
		sum = sum/nb;
		sumLeft = sumLeft/nb;
		sumRight = sumRight/nb;
		float semc = 0;
		for(int i = 0; i < list.size();++i)
			semc += (list.get(i)-sum)*(list.get(i)-sum);
		semc = (float)Math.sqrt(semc);
		if((sumLeft/sum) < 0.9 || sumRight/sum < 0.9){
			String name = "Y_"+y;
			strip = new Strip(name,this._chr,y+1,x+1,x-1,x+1);
			strip.setLeftNeigStrip(sumLeft/sum);
			strip.setRightNeigStrip(sumRight/sum);
			strip.setStripValue(sum);
			strip.setStripStd(semc);
			strip.setSize(nb*this._resolution);
		}
		return strip;
	}
	/**
	 * Compute standard deviation of the loop region at the neighbourhood 8.
	 * 
	 * @param x: int the x coordinate's of the loop
	 * @param y: int y coordinate's of the loop
	 * @param avg: double average of the same region
	 * @return double: teh standard deviation
	 */
	private float standardDeviation(int x, int y, double avg){
		float semc = 0;
		int nb = 0;
		ImageProcessor ip = this._imgNorm.getProcessor();
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				if(i < ip.getWidth() && i>0 && j < ip.getWidth() && j > 0){
					semc += (ip.getf(i, j)-avg)*(ip.getf(i, j)-avg);
					++nb;
				}
			}	
		}	
		semc = semc/nb;
		return (float)Math.sqrt(semc);
	}
	
	/**
	 * Removed maxima surrounded by several pixel with the 0 value. 
	 * The method search the pixel with value 0 in the 24 neighbourhood around the initial maxima.
	 * for the oMe method if the loops is suurounded by more than 6 0 the loops will be removed. For observed the thsreshold is smaller, 3.
	 * ig the loops is closed too the diagonal the test is less stringent 7 for oMe methods and 4 for observed method. 
	 *  
	 * @param nbZero nb zero allowed around the loops
	 * @param rawImage	ImagePlus raw image
	 * @param val background value of the image
	 */
	private void removeMaximaCloseToZero(int nbZero,ImagePlus rawImage, float val){ 
		int w = this._imgResu.getWidth();
		int h = this._imgResu.getHeight();
		ImageProcessor ipResu = this._imgResu.getProcessor();
		ImageProcessor ip = rawImage.getProcessor();
		for(int i = 2; i< w-2; ++i){
			for(int j= 2;j< h-2;++j){
				if(ipResu.getPixel(i, j) > 0){
					int thresh = nbZero;
					if (j-i <= this._diagSize+2)
						thresh = thresh+1;
					int nb = 0;
					for(int ii = i-2; ii <= i+2; ++ii){
						for(int jj = j-2; jj <= j+2; ++jj){
							if (ip.getf(ii, jj)<= val)
								nb++;
						}
						if(nb >= thresh){
							ipResu.set(i,j,0);
							break;
						}
					}
				}
			}
		}
		this._imgResu.setProcessor(ipResu);
	}
	
	/**
	 * Setter of the noise tolerance parameter 
	 * @param n int the value of the new noiseTolerance
	 */
	public void setNoiseTolerance( int n){ this._noiseTolerance = n;}
}
