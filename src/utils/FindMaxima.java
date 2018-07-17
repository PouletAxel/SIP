package utils;

import java.util.ArrayList;
import java.util.HashMap;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * 
 * @author axel poulet
 *
 */
public class FindMaxima{
	/**	 raw image*/
	private ImagePlus m_img;
	/**	 name of teh chromosome*/
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
		m_img = img;
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
	public FindMaxima(ImagePlus img, ImagePlus imgFilter, String chr, double noiseTolerance, int diag, int resolution,ArrayList<Integer> countNonZero){
		m_img = img;
		m_imgFilter = imgFilter;
		m_noiseTolerance = noiseTolerance;
		m_chr = chr;
		m_diagSize = diag;
		m_resolution = resolution;
		m_countNonZero= countNonZero;
	}
	
	/**
	 * Method to find loops in the image for observed and oMe, and fill the loop collection. This method also initiate the object loop,
	 * @param isObserved
	 * @return HashMap<String,Loop>
	 */
	public HashMap<String,Loop> findloop(boolean isObserved){
		run(isObserved);
		ArrayList<String> temp = getMaxima();
		int nb =0;
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			double value = Double.parseDouble(parts[2]);
			String name= m_chr+"\t"+temp.get(j);
			double avg = average(x,y);
			double std =standardDeviation(x,y,avg);
			DecayAnalysis da = new DecayAnalysis(m_img,x,y);
			if(da.getNeighbourhood1()>1 && da.getNeighbourhood2() > 1 && da.getNeighbourhood2()-da.getNeighbourhood1()>1 && (testStripNeighbour(x)==true && testStripNeighbour(y)==true)){
				Loop maxima = new Loop(temp.get(j),x,y,m_chr,avg,std,value);
				maxima.setNeigbhoord1(da.getNeighbourhood1());
				maxima.setNeigbhoord2(da.getNeighbourhood2());
				maxima.setPercentageOfZero(PercentOfneihgboordEqual0(x,y));
				maxima.setResolution(m_resolution);
				maxima.setDiagSize(m_diagSize);
				maxima.setMatrixSize(m_img.getWidth());
				m_data.put(name, maxima);
			}
			else{++nb;}
		}
		System.out.println("nb of loops supressed: "+nb);
		return m_data;
	}
	
	/**
	 * Method to find loops in the image for the compare method, and fill the loop collection. This method also initiate the object loop,
	 * @return HashMap<String,Loop>
	 */
	public HashMap<String,Loop> findloopCompare(){
		runForComparison();
		ArrayList<String> temp = getMaxima();
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			String name= m_chr+"\t"+temp.get(j);
			Loop maxima = new Loop(temp.get(j),x,y,m_chr);
			maxima.setResolution(m_resolution);
			maxima.setDiagSize(m_diagSize);
			maxima.setMatrixSize(m_img.getWidth());
			m_data.put(name, maxima);
		}
		return m_data;
	}
	
	/**
	 * Detect maxima with the oMe or observed methods, call the different methods 
	 * to detect the maxima and correct them. 
	 * @param isObserved, if true =>obersved method, else oMe
	 */
	private void run(boolean isObserved){
		ImagePlus temp = m_imgFilter.duplicate();
		ImageProcessor ip = temp.getProcessor();
		MaximumFinder mf = new MaximumFinder(); 
		ByteProcessor bp = mf.findMaxima(ip, m_noiseTolerance, MaximumFinder.SINGLE_POINTS, true);
		m_imgResu.setProcessor(bp);
		this.removedCloseMaxima();
		this.correctMaxima();
		this.removeMaximaCloseToZero(isObserved);
	}
	
	/**
	 * detect the maxima for the comparison method, to detect the maxima and correct them.
	 */
	private void runForComparison(){
		ImagePlus temp = m_imgFilter.duplicate();
		ImageProcessor ip = temp.getProcessor();
		MaximumFinder mf = new MaximumFinder(); 
		ByteProcessor bp = mf.findMaxima(ip, m_noiseTolerance, MaximumFinder.SINGLE_POINTS, true);
		m_imgResu.setProcessor(bp);
		this.removedCloseMaxima();
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
		ImageProcessor rawIp  = m_img.getProcessor();
		int w = rawIp.getWidth();
		int h = rawIp.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
		for(int i = 2; i< w-2; ++i){
			for(int j=2; j< h-2; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					int max = rawIp.get(i,j);
					int imax = i;
					int jmax =j;
					if(Math.abs(i-j) <=10){
						for(int ii=i-1; ii<=i+1; ++ii){
							for(int jj = j-1; jj <= j+1; ++jj){
								if(max < rawIp.get(ii, jj) && Math.abs(ii-jj) >= Math.abs(i-j)){
									imax = ii;
									jmax = jj;
									max = rawIp.get(ii, jj);	
								}
							}
						}
					}
					else{
						for(int ii=i-2; ii<=i+2; ++ii){
							for(int jj = j-2; jj <= j+2; ++jj){
								//System.out.println(ii+" "+jj);
								if(max < rawIp.get(ii, jj)){
									imax = ii;
									jmax = jj;
									max = rawIp.get(ii, jj);	
								}
							}
						}
					}
					if (max > rawIp.get(i,j)){
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
		ImageProcessor rawIp  = m_img.getProcessor();
		int w = rawIp.getWidth();
		int h = rawIp.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
		for(int i = 1; i < w-1; ++i){
			for(int j= 1; j < h-1; ++j){		
				if (ipMaxima.getPixel(i,j) > 0){
					for(int ii = i-3; ii <= i+3; ++ii){
						for(int jj = j-3; jj <= j+3; ++jj){	
							if(ipMaxima.getPixel(ii,jj) > 0){
								if(i != ii || j != jj){
									if(rawIp.get(ii, jj) > rawIp.get(i,j))	ipMaxima.set(i,j,0);
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
		ImageProcessor ip = m_img.getProcessor();
		ArrayList<String> listMaxima = new ArrayList<String>();
		for(int i = 0; i < w; ++i){
			for(int j = 0; j < h; ++j){
				if (ipResu.getPixel(i,j) > 0 && i-j > 0){
					listMaxima.add(i+"\t"+j+"\t"+ip.getPixel(i, j));
				}
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
		ImageProcessor ip = m_img.getProcessor();
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){		
				sum +=ip.getPixel(i, j);
				++nb;
			}
		}
		return sum/nb;
	}
	
	/**
	 * Compute the percentage of the zero around the loop on the 24 neighbourhood and return the value 
	 * @param x: int x coordinate's of the loop
	 * @param y: int y coordinate's of the loop
	 * @return double: percenatge of 0 around the loop
	 */
	private double PercentOfneihgboordEqual0(int x, int y){
		double sum = 0;
		int nb=0;
		ImageProcessor ip = m_img.getProcessor();
		for(int i = x-2; i <= x+2; ++i){
			for(int j = y-2; j <= y+2; ++j){
				if((i != x || j != y) && ip.getPixel(i, j)<=0) ++sum;
				++nb;
			}
		}
		return 100*(sum/nb);
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
		ImageProcessor ip = m_img.getProcessor();
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				semc += (ip.getPixel(i, j)-avg)*(ip.getPixel(i, j)-avg);
			}	
		}	
		semc = semc/(double)9;
		return Math.sqrt(semc);
	}
	
	/**
	 *	Removed maxima surrounded by several pixel with the 0 value. 
	 *The method search the pixel with value 0 in the 24 neighbourhood around the initial maxima.
	 * for the oMe method if the loops is suurounded by more than 6 0 the loops will be removed. For observed the thsreshold is smaller, 3.
	 * ig the loops is closed too the diagonal the test is less stringent 7 for oMe methods and 4 for observed method. 
	 * @param isObserved: boolean to know which methods is used allow to manage. if true it is the observed methode else the oMe method.
	 */
	private void removeMaximaCloseToZero(boolean isObserved){ 
		int w = m_imgResu.getWidth();
		int h = m_imgResu.getHeight();
		ImageProcessor ipResu = m_imgResu.getProcessor();
		ImageProcessor ip = m_img.getProcessor();
		for(int i =0; i< w; ++i){
			for(int j=0;j< h;++j){
				if(ipResu.getPixel(i, j) > 0){
					int thresh = 6;
					if (j-i <= m_diagSize+2){
						thresh = 7;
						if (isObserved)	thresh = 4;
					}
					else if(isObserved){ thresh = 3;}
					int nb =0;
					for(int ii = i-2; ii <= i+2; ++ii){
						for(int jj = j-2; jj <= j+2; ++jj){
							if (ip.getPixel(ii, jj)<=0)	nb++;
						}
						if(nb > thresh){
							ipResu.set(i,j,0);
							break;
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Method testing the presence of white strip in the neighboorhood 
	 * 
	 * @param i coordinates to test
	 * @return boolean if true no white stripes detected else false 
	 */
	private boolean testStripNeighbour(int i){
		if(m_countNonZero.get(i) > 0 && m_countNonZero.get(i+1) > 0 && m_countNonZero.get(i-1) > 0 ){
			return true;
		}
		else return false;
	}
	
	
	
	/**
	 * Setter of the noise tolerance parameter 
	 * @param n int the value of the new noiseTolerance
	 */
	public void setNoiseTolerance( int n){
		this.m_noiseTolerance = n;
	}
	
	/*private void findOtherMaxima(){
		ImagePlus img = m_img.duplicate();
		ProcessMethod pm = new ProcessMethod(img,1.25);
		pm.runGaussian();
		ImageProcessor rawIp = img.getProcessor();
		int w = rawIp.getWidth();
		int h = rawIp.getHeight();
		ImageProcessor ipMaxima = m_imgResu.getProcessor();
		for(int i = 12; i< w-12; ++i){
			for(int j=12; j< h-12; ++j){		
				if (ipMaxima.getPixel(i,j) == 255){ 
					double avg = average(i,j,rawIp);
					double var =variance(i, j, rawIp, avg);
					double std = Math.sqrt(var);
					double up = rawIp.get(i, j)+std*4;
					double down = rawIp.get(i, j)-std*4;
					if(down <= 0 ){
						down = avg-std;
						if(down <= 0 ){down = avg;}
					}
					if(var <= rawIp.get(i, j)){
						for(int ii=i-10; ii<=i+10; ++ii){
							for(int jj = j-10; jj <= j+10; ++jj){
								if( rawIp.get(ii, jj) >= down && rawIp.get(ii, jj) <= up && 
									((ii <= i-2 ||ii >= i+2) && (jj <= j-2 || jj >= j+2))){
									if(testMaxima(img, ii, jj, avg, Math.sqrt(var))){
										double avgTest = average(ii,jj,rawIp);
										double varTest = variance(ii, jj, rawIp, avgTest);		
										if(varTest <= rawIp.get(ii, jj) && varTest <= var && avgTest >= avg-std*2 && avgTest <= avg+std*2){
											ipMaxima.set(ii,jj,125);
											//System.out.println(rawIp.get(i, j)+"\t"+i+"\t"+j+"\t"+avgDiff+"\t"+std+"\t"+var+"\t"+ii+"\t"+jj+"\t"+avgDiffTest+"\t"+avgTest+"\t"+stdTest);
										}
									}
								}
							}
						}
					}
				}			
			}
		}
		this.m_imgResu.setProcessor(ipMaxima);
	}
	
	private boolean testMaxima(ImagePlus img, int i, int j, double avg, double std){
		ImageProcessor rawIp  = img.getProcessor();
		boolean test = true;
		int nb = 0;
		double up = rawIp.get(i, j)+std*4;
		double down = avg-std*4;
	
		if (down <= 0 ){
			down = avg-std*2;
			//System.out.println(down);
		}
	
		for(int ii = i-2; ii <= i+2; ++ii){
			for(int jj = j-2; jj <= j+2; ++jj){
				if(ii != i && jj != j){
					if(rawIp.get(ii, jj) >= rawIp.get(i, j)){
						test = false;
						break;
					}
					else if(rawIp.get(ii, jj) <=  down || rawIp.get(ii, jj) >= up){
						nb++;
						if(nb++ > 8){
							test = false;
							break;
						}
					}
				}
			}
			if(test ==false) break;
		}
		return test;
	}*/
}
