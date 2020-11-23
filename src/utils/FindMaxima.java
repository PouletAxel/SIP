package utils;

import java.util.ArrayList;
import java.util.HashMap;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import loops.Loop;

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
	/**	 name of the chromosome2*/
	private String _chr2;
	/**	 Image fileterred with min, max and gaussian filter*/
	private ImagePlus _imgFilter;
	/**	binary image pixel white = the maxima detected*/
	private ImagePlus _imgResu = new ImagePlus();
	/**	 threshold for the imageJ class MaximumFinder, this class is call to detecte the maxima */
	private double _thresholdMaxima;
	/**	diagonal size in bin*/
	private int _diagonalSize;
	/**	 Resolution of the image in base*/
	private int _resolution;
	/** gaussian Filter strength*/
	private double _gaussian;

	
	/**
	 * Constructor of FindMaxima for intra chromosomal method
	 *
	 * @param img ImagePlus raw image
	 * @param imgFilter ImagePlus filtered image
	 * @param chr String chromosome
	 * @param thresholdMaxima double threshold to detect maxima
	 * @param diagonalSize int the size of the diagonal
	 * @param resolution int size of the pixel in base
	 */
	public FindMaxima(ImagePlus img, ImagePlus imgFilter, String chr, double thresholdMaxima, int diagonalSize, int resolution){
		this._imgNorm =  img;
		this._imgFilter = imgFilter;
		this._thresholdMaxima = thresholdMaxima;
		this._chr = chr;
		this._diagonalSize = diagonalSize;
		this._resolution = resolution;
	}

	/**
	 * Constructor of FindMaxima for inter chromosomal method
	 * @param imgDiff ImagePlus image diff
	 * @param chr1 chr1 name
	 * @param chr2 chr2 name
	 * @param thresholdMaxima  double threshold to detect maxima
	 * @param resolution int size of the pixel in base
	 * @param gaussian gaussian filter strength
	 */
	public FindMaxima( ImagePlus imgDiff, String chr1,String chr2, double thresholdMaxima, int resolution, double gaussian){
		this._imgFilter = imgDiff;
		this._thresholdMaxima = thresholdMaxima;
		this._chr2 = chr2;
		this._chr = chr1;
		this._resolution = resolution;
		this._gaussian = gaussian;
	}


	/**
	 * findLoop for intra chromosomal interaction
	 * call ImageJ findMaxima method
	 * filter loops with low value in oMe image and normalize image
	 * if the loops didn't possess a decay => suppress
	 * Loop correction: => avoid overlapping loops, correct also the coordinate if higher value pixel is in the loops
	 * neighbor
	 *
	 * @param index image index
	 * @param nbZero	nb zeor allowe around the loop
	 * @param raw ImagePlus raw image
	 * @param val value used as min in the image
	 * @return HashMap loop loop name => Loop object
	 */

	public HashMap<String, Loop> findLoop(int index, int nbZero, ImagePlus raw, float val){
		run(nbZero, raw, val);
		ArrayList<String> temp = this.getMaxima();
		ImageProcessor ipN = this._imgNorm.getProcessor();
		HashMap<String,Loop>  data = new HashMap<String,Loop>();
		//System.out.println("size raw maxima !!!!!!!!!!!!!! "+raw.getTitle()+"  "+temp.size());
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			String name= this._chr+"\t"+temp.get(j)+"\t"+index;
			float avg = average(x,y);
			float std =standardDeviation(x,y,avg);
			if(avg > 1.45 && ipN.getf(x, y) >= 1.85){ // filter on the loop value and region value
				DecayAnalysis da = new DecayAnalysis(this._imgNorm,x,y);
				float n1 =da.getNeighbourhood1();
				float n2 =da.getNeighbourhood2();
				if(n1 < n2 && n1 >= 0.15 && n2 >= 0.25){ // filter on the neighborood for hic datatset
					Loop maxima = new Loop(temp.get(j),x,y,this._chr,avg,std,ipN.getf(x, y));
					maxima.setNeigbhoord1(n1);
					maxima.setNeigbhoord2(n2);
					maxima.setResolution(this._resolution);
					//System.out.println(_resolution+" "+maxima.getResolution());
					maxima.setDiagSize(this._diagonalSize);
					maxima.setMatrixSize(this._imgNorm.getWidth());
					data.put(name, maxima);
				}
			}
		}
		//System.out.println("after filter ################# "+raw.getTitle()+"  "+data.size());
		return data;
	}

	/**
	 * findLoop for inter chromosomal interaction
	 * call ImageJ findMaxima method
	 * filter loops with more than the nb of zero allowed in its neighbor
	 * if the loops didn't possess a decay => suppress
	 * Loop correction:  correct also the coordinate if higher value pixel is in the loops
	 * neighbor
	 * @param pathRaw path to the raw image
	 * @return HashMap loop loop name => Loop object
	 */
	public HashMap<String,Loop> findLoopInter(String pathRaw){

		ImagePlus raw = IJ.openImage(pathRaw);
		ImageProcessor ipFilter = _imgFilter.getProcessor();
		runInter(raw);
		ArrayList<String> temp = this.getMaxima();
		ImageProcessor ipRaw = raw.getProcessor();
		HashMap<String,Loop>  data = new HashMap<>();
		//System.out.println("size raw maxima !!!!!!!!!!!!!! "+raw.getTitle()+"  "+temp.size());
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			String name= this._chr+"\t"+this._chr2+"\t"+temp.get(j);
			float avg = average(x,y);
			float std = standardDeviation(x,y,avg);
			int nbOfZero = detectNbOfZero(x,y,ipRaw.getf(x, y));
			if(nbOfZero <= 3){ // filter on the loop value and region value
				DecayAnalysis da = new DecayAnalysis(raw,x,y);
				float n1 =da.getNeighbourhood1();
				float n2 =da.getNeighbourhood2();
					if(n2>n1 && n1 >= 0 && n2 >= 0){ // filter on the neighborood for hic datatset
					Loop maxima = new Loop(temp.get(j),x,y,this._chr,this._chr2,avg,std,ipRaw.getf(x, y));
					maxima.setValueDiff(ipFilter.getf(x,y));
					maxima.setNeigbhoord1(n1);
					maxima.setNeigbhoord2(n2);
					maxima.setResolution(this._resolution);
					maxima.setNbOfZero(nbOfZero);
					maxima.setMatrixSize(raw.getWidth());
					data.put(name, maxima);
				}
			}
		}
		//System.out.println("after filter ################# "+raw.getTitle()+"  "+data.size());
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
		ByteProcessor bp = mf.findMaxima(ip, this._thresholdMaxima, MaximumFinder.SINGLE_POINTS, true);
		this._imgResu.setProcessor(bp);
		this.putLoopLowerTriangle();
		this.removedCloseMaxima();
		this.correctMaxima();
		this.removeMaximaCloseToZero(nbZero,rawImage, backgroundValue);
	}


	/**
	 * Detect maxima with the oMe or observed methods, call the different methods
	 * 	to detect the maxima and correct them.
	 * @param rawImage ImagePlus raw image
	 */
	private void runInter(ImagePlus rawImage){
		ImagePlus temp = this._imgFilter.duplicate();
		ImageProcessor ip = temp.getProcessor();
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(ip, this._gaussian);
		MaximumFinder mf = new MaximumFinder();
		ByteProcessor bp = mf.findMaxima(ip, this._thresholdMaxima, MaximumFinder.SINGLE_POINTS, true);
		this._imgResu.setProcessor(bp);
		this._imgNorm = rawImage;
		this.correctMaxima();
	}


	/**
	 *
	 * @param x
	 * @param y
	 * @param val
	 * @return
	 */
	private int detectNbOfZero(int x, int y, float val){
		int w = this._imgNorm.getWidth();
		int h = this._imgNorm.getHeight();
		ImageProcessor ip = _imgNorm.getProcessor();
		int nbZero = -1;
		if(x >= 3 && x <= w-3 && y >= 3 && y <= h-3) {
			nbZero = 0;
			for (int i = x - 2; i <= x + 2; ++i) {
				for (int j = y - 2; j <= y + 2; ++j) {
					if (ip.getf(i, j) <= 1) {
						nbZero++;
					} else if (Double.isNaN(ip.getf(i, j))) {
						nbZero++;
					}
				}
			}
		}
		return nbZero;
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
	 *
	 */
	private void putLoopLowerTriangle() {
		ImageProcessor ipMaxima = this._imgResu.getProcessor();
		int w = ipMaxima.getWidth();
		int h = ipMaxima.getHeight();
		for(int i = 1; i < w-1; ++i){
			for(int j= 1; j < h-1; ++j){	
				if(ipMaxima.getPixel(i,j) > 0){
					if(j > i){
					ipMaxima.set(j,i,255);
					ipMaxima.set(i,j,0);
					}
				}
			}
		}
		this._imgResu.setProcessor(ipMaxima);
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
		for(int i = 2; i< w-2; ++i){
			for(int j = 2; j< h-2; ++j){		
				if (ipMaxima.getPixel(i,j) == 255 ){
					double max = rawIpNorm.getf(i,j);
					int imax = i;
					int jmax =j;
					for(int ii=i-2; ii<=i+2; ++ii){
						for(int jj = j-2; jj <= j+2; ++jj){
							if(max < rawIpNorm.getf(ii, jj)){
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
				if (ipResu.getf(i,j) > 0 )//&& i-j > 0)
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
					if(	Double.isNaN(ip.getf(i, j))){
						sum +=0;
					}else{	sum +=ip.getf(i, j);}
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
	private float standardDeviation(int x, int y, double avg){
		float semc = 0;
		int nb = 0;
		ImageProcessor ip = this._imgNorm.getProcessor();
		for(int i = x-1; i <= x+1; ++i){
			for(int j = y-1; j <= y+1; ++j){
				if(i < ip.getWidth() && i>0 && j < ip.getWidth() && j > 0){
					if(	Double.isNaN(ip.getf(i, j))){
						semc += (0-avg)*(0-avg);
					}else{	semc += (ip.getf(i, j)-avg)*(ip.getf(i, j)-avg);}
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
					if (j-i <= this._diagonalSize +2)
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
}
