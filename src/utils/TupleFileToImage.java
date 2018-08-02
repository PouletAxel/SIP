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
	private ImagePlus m_img = new ImagePlus();
	/** Path of the tuple file*/
	private String m_file = "";
	/** Size of the matrix*/
	private int m_size = 0 ;
	/**	 Resolution of the image in base*/
	private int m_resolution = 0 ;
	/** Step to process the whole chromosme*/
	private int m_step ;
	/** Image value average*/
	private double m_avg = 0;
	/** Image standard deviation */
	private double m_std = 0;
	
	/**
	 * TupleFileToImage constructor
	 * @param fileMatrix tuple file path 
	 * @param size int size of the image
	 * @param resolution int size of the bin
	 */
	public TupleFileToImage(String fileMatrix, int size, int resolution){
		m_file = fileMatrix;
		m_size = size;
		m_resolution = resolution;
		m_step = size/2;
	}
	
	
	/**
	 * Method to make the image with an input tuple file return the image results
	 *  
	 * @return ImagePlus results
	 */
	public ImagePlus readTupleFile(){
		BufferedReader br;
		ShortProcessor p = new ShortProcessor(m_size,m_size);
		String[] tfile = m_file.split("_");
		int numImage = Integer.parseInt(tfile[tfile.length-2])/(m_step*m_resolution);
		try {
			p.abs();
			br = new BufferedReader(new FileReader(m_file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null){
				sb.append(line);
				String[] parts = line.split("\\t");
				float a = 0;
				
				if(!(parts[2].equals("NAN"))){
					a =Float.parseFloat(parts[2]);
					if (a < 0){ a = 0;}
				}
				int correction = numImage*m_step*m_resolution;
				int i = (Integer.parseInt(parts[0]) - correction)/m_resolution; 
				int j = (Integer.parseInt(parts[1]) - correction)/m_resolution;
				//System.out.println(i+" "+j+" "+a);
				if(i < m_size && j< m_size){
					p.setf(i, j, a);
					p.setf(j, i, a);
				}
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) { e.printStackTrace();}
		m_img.setProcessor(p);
		return m_img;
	}
	
	/**
	 * Method to correct the image, remove the high value close to the diagonal to allow 
	 * the dection of the structure of interest
	 * @param img ImagePlus to correct
	 */
	public void correctImage(ImagePlus img){
		ImageProcessor ip = img.getProcessor();
		int noZeroPixel = 0;
		int sum = 0;
		for(int i = 0; i < ip.getWidth(); ++i){
			for(int j = 0; j < ip.getWidth(); ++j){
				if(ip.getPixel(i, j) > 0){
					++noZeroPixel;
					sum += ip.getPixel(i, j);
				}
			}
		}
		m_avg = (double)sum/(double)noZeroPixel;
		m_std = std(m_avg);
		System.out.println(img.getTitle()+"avg: "+m_avg+"\tstd: "+m_std);
		for(int i = 0; i < ip.getWidth(); ++i){
			for(int j = 0; j < ip.getWidth(); ++j){
				int a = ip.getPixel(i, j);
				if (Math.abs(j-i) <= 2 && a >= m_avg+m_std*2)
					ip.set(i,j,(int)m_avg);
			}
		}
		img.setProcessor(ip);
	}
	
	/**
	 * Compute the standard deviation of the pixel non zero values of m_img 
	 * @param mean average value in m_img
	 * @return double satndard deivation
	 */
	private double std(double mean){
		double semc = 0;
		ImageProcessor ip = m_img.getProcessor();
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
}
