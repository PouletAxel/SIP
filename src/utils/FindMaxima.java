package utils;

import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Class to detect the Maxima in image. Return the HashMap<String,Loop>, the loop can be corrected.
 * 
 * the class use the imageJ class to detect the maxima.
 * 
 * @author axel poulet
 *
 */
public class FindMaxima{
	/**	 raw image*/
	private ImagePlus m_imgNorm;
	/**	 name of the chromosome*/
	private String m_chr;
	/**	 Image fileterred with min, max and gaussian filter*/
	private ImagePlus m_imgFilter;
	/**	binary image pixel white = the maxima detected*/
	private ImagePlus m_imgResu = new ImagePlus();
	/**	 threshold for the imageJ class MaximumFinder, this class is call to detecte the maxima */
	private double m_noiseTolerance =-1;
	/**	diagonal size in bin*/
	private int m_diagSize =-1;
	/**	 Resolution of the image in base*/
	private int m_resolution = -1;
	/**	 HashMap<String,Loop>  collection of Object loop initialised in this class.*/
	private HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	/** arrayList of int each occurence is a x coordinate of the image, the value of this arrayList is an integer if = 0 it is a whithe strip*/
	ArrayList<Integer> m_countNonZero = new ArrayList<Integer>(); 
	/** */
	HashMap <String,Loop> m_tmp = new HashMap <String,Loop>(); 
	
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
		m_imgNorm =  img;
		m_imgFilter = imgFilter;
		m_noiseTolerance = noiseTolerance;
		m_chr = chr;
		m_diagSize = diag;
		m_resolution = resolution;
	}
	
	/**
	 * Constructor of FindMaxima
	 * @param img ImagePlus raw image
	 * @param imgFilter ImagePlus filtered image
	 * @param chr String chromosome
	 * @param noiseTolerance double threshold to detect maxima
	 * @param diag	int the size of the diagonal
	 * @param resolution int  the size of the diagonal
	 * @param countNonZero ArrayList<Integer> array list locate the whit strip in the original matrix
	 */
	public FindMaxima(ImagePlus img, ImagePlus imgFilter, String chr, double noiseTolerance, int diag, int resolution, ArrayList<Integer> countNonZero){
		m_imgNorm =  img;
		m_imgFilter = imgFilter;
		m_noiseTolerance = noiseTolerance;
		m_chr = chr;
		m_diagSize = diag;
		m_resolution = resolution;
		m_countNonZero= countNonZero;
	}
	
	public FindMaxima(ImagePlus img, ImagePlus imgFilter, String chr, int noiseTolerance,int diag, int resolution, ArrayList<Integer> countNonZero, HashMap<String, Loop> temp) {
		m_imgNorm =  img;
		m_imgFilter = imgFilter;
		m_noiseTolerance = noiseTolerance;
		m_chr = chr;
		m_diagSize = diag;
		m_resolution = resolution;
		m_countNonZero= countNonZero;
		m_tmp = temp;
	}

	/**
	 * Method to find loops in the image for observed and oMe, and fill the loop collection. This method also initiate the object loop,
	 * @param isObserved
	 * @return HashMap<String,Loop>
	 */
	public HashMap<String,Loop> findloop(int numImage, int nbZero, ImagePlus raw, int val){
		run(nbZero, raw, val);
		ArrayList<String> temp = getMaxima();
		ImageProcessor ipN = m_imgNorm.getProcessor();
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			String name= m_chr+"\t"+temp.get(j)+"\t"+numImage;
			double avg = average(x,y);
			double std =standardDeviation(x,y,avg);
			DecayAnalysis da = new DecayAnalysis(this.m_imgNorm,x,y);
			double n1 =da.getNeighbourhood1();
			double n2 =da.getNeighbourhood2();
			if(n1<=n2 && n1 >= 0.125 && n2 >= 0.125 && (testStripNeighbour(x)==true && testStripNeighbour(y)==true)){
				Loop maxima = new Loop(temp.get(j),x,y,m_chr,avg,std,ipN.getf(x, y));
				maxima.setNeigbhoord1(n1);
				maxima.setNeigbhoord2(n2);
				maxima.setResolution(m_resolution);
				maxima.setDiagSize(m_diagSize);
				maxima.setMatrixSize(m_imgNorm.getWidth());
				m_data.put(name, maxima);
			}
		}
		return m_data;
	}
	
	
		
	/**
	 * Detect maxima with the oMe or observed methods, call the different methods 
	 * to detect the maxima and correct them. 
	 * @param isObserved, if true =>obersved method, else oMe
	 */
	private void run(int nbZero, ImagePlus rawImage, int val){
		ImagePlus temp = m_imgFilter.duplicate();
		ImageProcessor ip = temp.getProcessor();
		MaximumFinder mf = new MaximumFinder(); 
		ByteProcessor bp = mf.findMaxima(ip, m_noiseTolerance, MaximumFinder.SINGLE_POINTS, true);
		m_imgResu.setProcessor(bp);
		this.removedCloseMaxima();
		this.correctMaxima();
		this.removeMaximaCloseToZero(nbZero,rawImage, val);
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
	 *   
	 * 
	 */
	private void correctMaxima(){
		ImageProcessor rawIpNorm  = this.m_imgNorm.getProcessor();
		int w = rawIpNorm.getWidth();
		int h = rawIpNorm.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
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
								max = rawIpNorm.get(ii, jj);
							}
						}
					}
					if (max > rawIpNorm.get(i,j)){
						ipMaxima.set(i,j,0);
						ipMaxima.set(imax,jmax,255);
					}
				}			
			}
		}
		this.m_imgResu.setProcessor(ipMaxima);
	}
	
	/**
	 * Test if two maxima are close removed this one which possess the smaller value.
	 * Test a 48 neighbourhood region
	 */
	private void removedCloseMaxima(){
		ImageProcessor rawIpNorm  = m_imgNorm.getProcessor();
		int w = rawIpNorm.getWidth();
		int h = rawIpNorm.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
		for(int i = 1; i < w-1; ++i){
			for(int j= 1; j < h-1; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					for(int ii = i-2; ii <= i+2; ++ii){
						for(int jj = j-2; jj <= j+2; ++jj){	
							if(ipMaxima.getPixel(ii,jj) > 0){
								if(i != ii || j != jj){
									if(rawIpNorm.getf(ii, jj) > rawIpNorm.getf(i,j))	ipMaxima.set(i,j,0);
									else ipMaxima.set(ii,jj,0);							
								}
							}
						}
					}
				}			
			}
		}
		this.m_imgResu.setProcessor(ipMaxima);
	}

	/**
	 * getter of maxima
	 * retrun the list of maxima as list of string i\tj\tvalue
	 * @return  ArrayList<String> 
	 */
	public ArrayList<String> getMaxima(){
		int w = m_imgResu.getWidth();
		int h = m_imgResu.getHeight();
		ImageProcessor ipResu = m_imgResu.getProcessor();
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
	private double average(int x, int y){
		double sum = 0;
		int nb=0;
		ImageProcessor ip = m_imgNorm.getProcessor();
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
	 * Compute standard deviation of the loop region at the neighbourhood 8.
	 * 
	 * @param x: int the x coordinate's of the loop
	 * @param y: int y coordinate's of the loop
	 * @param avg: double average of the same region
	 * @return double: teh standard deviation
	 */
	private double standardDeviation(int x, int y, double avg){
		double semc = 0;
		int nb = 0;
		ImageProcessor ip = m_imgNorm.getProcessor();
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				if(i < ip.getWidth() && i>0 && j < ip.getWidth() && j > 0){
					semc += (ip.getf(i, j)-avg)*(ip.getf(i, j)-avg);
					++nb;
				}
			}	
		}	
		semc = semc/(double)nb;
		return Math.sqrt(semc);
	}
	
	/**
	 *	Removed maxima surrounded by several pixel with the 0 value. 
	 *The method search the pixel with value 0 in the 24 neighbourhood around the initial maxima.
	 * for the oMe method if the loops is suurounded by more than 6 0 the loops will be removed. For observed the thsreshold is smaller, 3.
	 * ig the loops is closed too the diagonal the test is less stringent 7 for oMe methods and 4 for observed method. 
	 * @param isObserved: boolean to know which methods is used allow to manage. if true it is the observed methode else the oMe method.
	 */
	private void removeMaximaCloseToZero(int nbZero,ImagePlus rawImage, int val){ 
		int w = m_imgResu.getWidth();
		int h = m_imgResu.getHeight();
		ImageProcessor ipResu = m_imgResu.getProcessor();
		ImageProcessor ip = rawImage.getProcessor();
		for(int i = 2; i< w-2; ++i){
			for(int j= 2;j< h-2;++j){
				if(ipResu.getPixel(i, j) > val){
					int thresh = nbZero;
					if (j-i <= m_diagSize+2)
						thresh = thresh+1;
					int nb = 0;
					for(int ii = i-2; ii <= i+2; ++ii){
						for(int jj = j-2; jj <= j+2; ++jj){
							if (ip.get(ii, jj)<=0)	nb++;
						}
						if(nb >= thresh){
							ipResu.set(i,j,0);
							break;
						}
					}
				}
			}
		}
		m_imgResu.setProcessor(ipResu);
	}
	
	
	/**
	 * Method testing the presence of white strip in the neighboorhood 
	 * 
	 * @param i coordinates to test
	 * @return boolean if true no white stripes detected else false 
	 */
	private boolean testStripNeighbour(int i){
		if(i+1 >=  m_countNonZero.size() ||	i-1 < 0)
			return false;
		else{
			if(m_countNonZero.get(i) > 0 && m_countNonZero.get(i+1) > 0 && m_countNonZero.get(i-1) > 0 )
				return true;
			else return false;
		}
	}
	
	
	
	/**
	 * Setter of the noise tolerance parameter 
	 * @param n int the value of the new noiseTolerance
	 */
	public void setNoiseTolerance( int n){
		this.m_noiseTolerance = n;
	}
}
