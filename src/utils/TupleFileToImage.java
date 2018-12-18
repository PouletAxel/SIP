package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * Make an image with a tuple file (x y value). Take in entrees the resolution the matrix size and the other parameters of the tuple
 * file.
 * 
 * @author axel poulet
 *
 */
public class TupleFileToImage {
	
	/** Image results */
	private ImagePlus _img = new ImagePlus();
	/** Image results */
	private ImagePlus _imgNorm = new ImagePlus();
	/** Path of the tuple file*/
	private String _file = "";
	/** Size of the matrix*/
	private int _size = 0 ;
	/**	 Resolution of the image in base*/
	private int _resolution = 0 ;
	/** Step to process the whole chromosme*/
	private int _step ;
	/** Image value average*/
	private static double _avg = 0;
	/** Image standard deviation */
	private static double _std = 0;
	/** */
	public static int _noZeroPixel = 0;
	
	/**
	 * TupleFileToImage constructor
	 * @param fileMatrix tuple file path 
	 * @param size int size of the image
	 * @param resolution int size of the bin
	 */
	public TupleFileToImage(String fileMatrix, int size, int resolution){
		this._file = fileMatrix;
		this._size = size;
		this._resolution = resolution;
		this._step = size/2;
	}
	
	
	/**
	 * Method to make the image with an input tuple file return the image results
	 *  
	 * @return ImagePlus results
	 */
	public void readTupleFile(){
		BufferedReader br;
		ShortProcessor pRaw = new ShortProcessor(this._size,this._size);
		ShortProcessor pNorm = new ShortProcessor(this._size,this._size);
		String[] tfile = this._file.split("_");
		int numImage = Integer.parseInt(tfile[tfile.length-2])/(this._step*this._resolution);
		try {
			pRaw.abs();
			br = new BufferedReader(new FileReader(this._file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null){
				sb.append(line);
				String[] parts = line.split("\\t");
				float raw = 0;
				float norm = 0;
				
				if(!(parts[2].equals("NAN"))){
					raw =Float.parseFloat(parts[2]);
					if (raw < 0) raw = 0;
				}
				
				if(!(parts[3].equals("NAN"))){
					norm =Float.parseFloat(parts[3]);
					if (norm < 0) norm = 0;
				}
				
				int correction = numImage*this._step*this._resolution;
				int i = (Integer.parseInt(parts[0]) - correction)/this._resolution; 
				int j = (Integer.parseInt(parts[1]) - correction)/this._resolution;
				if(i < this._size && j< this._size){
					pRaw.setf(i, j, raw);
					pRaw.setf(j, i, raw);
					pNorm.setf(i, j, norm);
					pNorm.setf(j, i, norm);
				}
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) { e.printStackTrace();}
		this._img.setProcessor(pRaw);
		this._imgNorm.setProcessor(pNorm);
	}
	
	/**
	 * Method to correct the image, remove the high value close to the diagonal to allow 
	 * the dection of the structure of interest
	 * @param img ImagePlus to correct
	 */
	public static void correctImage(ImagePlus img){
		ImageProcessor ip = img.getProcessor();
		_noZeroPixel = 0;
		int sum = 0;
		for(int i = 0; i < ip.getWidth(); ++i){
			for(int j = 0; j < ip.getWidth(); ++j){
				if(ip.getPixel(i, j) > 0){
					++_noZeroPixel;
					sum += ip.getPixel(i, j);
				}
			}
		}
		_avg = (double)sum/(double)_noZeroPixel;
		_std = std(_avg,img);
		for(int i = 0; i < ip.getWidth(); ++i){
			for(int j = 0; j < ip.getWidth(); ++j){
				int a = ip.getPixel(i, j);
				if (Math.abs(j-i) <= 2 && a >= _avg+_std*2)
					ip.set(i,j,(int)_avg);
			}
		}
		img.setProcessor(ip);
	}
	
	/**
	 * Compute the standard deviation of the pixel non zero values of m_img 
	 * @param mean average value in m_img
	 * @param img ImagePlus
	 * @return double satndard deivation
	 */
	private static double std(double mean,ImagePlus img){
		double semc = 0;
		ImageProcessor ip = img.getProcessor();
		int noZeroPixel = 0;
		for(int i = 0; i < ip.getWidth(); ++i){
			for(int j = 0; j < ip.getWidth(); ++j){
				if(ip.getPixel(i, j) > 0){
					++noZeroPixel;
					semc += (ip.getPixel(i, j)-mean)*(ip.getPixel(i, j)-mean);
				}
			}
		}
		semc = Math.sqrt(semc/(double)noZeroPixel);
		return semc;
	}
	
	public ImagePlus getRawImage(){return this._img;}
	public ImagePlus getNormImage(){return this._imgNorm;}
	public void setRawImage(ImagePlus img){this._img = img;}
	public void setNormImage(ImagePlus img){this._imgNorm = img;}
}