package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * 
 * @author plop
 *
 */
public class TupleFileImage {
	
	/** */
	private ImagePlus m_img = new ImagePlus();
	/** */
	private String m_file = "";
	/** */
	private int m_size = 0 ;
	private int m_resolution = 0 ;
	private int m_step = 0 ;
	/** */
	private double m_avg = 0;
	private double m_std = 0;
	
	/**
	 * 
	 */
	public TupleFileImage(String fileMatrix, int size, int step, int resolution){
		m_file = fileMatrix;
		m_size = size;
		m_resolution = resolution;
		System.out.println(fileMatrix);
		m_step = step;
	}
	
	
	/**
	 * 
	 * @param matrixSize
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
			//System.out.println("\t"+line);
			while (line != null){
				sb.append(line);
				String[] parts = line.split("\\t");
				float a = 0;
				
				if(!(parts[2].equals("NAN"))){
					a =Float.parseFloat(parts[2]);
					if (a < 0){ a = 0;}
				}
				int plop = numImage*m_step*m_resolution;
				int i = (Integer.parseInt(parts[0]) - plop)/m_resolution; 
				int j = (Integer.parseInt(parts[1]) - plop)/m_resolution;
		
				//System.out.println(m_size+"\t"+parts[0]+" "+parts[1]+" "+i+"\t"+j+"\t"+a);
				p.setf(i, j, a);
				p.setf(j, i, a);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) { e.printStackTrace();}
		m_img.setProcessor(p);
		//correctImage();
		return m_img;
	}
	
	/**
	 * 
	 * @param img
	 */
	public void correctImage(ImagePlus img){
		ImageProcessor ip = img.getProcessor();
		int noZeroPixel = 0;
		int sum = 0;
		for(int i = 0; i< ip.getWidth(); i++){
			for(int j = 0; j< ip.getWidth(); j++){
				if(ip.getPixel(i, j)>0){
					noZeroPixel++;
					sum += ip.getPixel(i, j);
				}
			}
		}
		m_avg = (double)sum/(double)noZeroPixel;
		m_std = std(m_avg);
		System.out.println(img.getTitle()+"avg: "+m_avg+"\tstd: "+m_std);
		for(int i = 0; i< ip.getWidth(); i++){
			for(int j = 0; j< ip.getWidth(); j++){
				int a = ip.getPixel(i, j);
				if (Math.abs(j-i) <= 2 && a >= m_avg+m_std*2){ ip.set(i,j,(int)m_avg);}
			}
		}
		img.setProcessor(ip);
	}
	
	/**
	 * 
	 */
	private double std(double mean){
		double semc = 0;
		ImageProcessor ip = m_img.getProcessor();
		int noZeroPixel = 0;
		for(int i = 0; i< ip.getWidth(); i++){
			for(int j = 0; j< ip.getWidth(); j++){
				if(ip.getPixel(i, j)>0){
					noZeroPixel++;
					semc += (ip.getPixel(i, j)-mean)*(ip.getPixel(i, j)-mean);
				}
			}
		}
		semc = Math.sqrt(semc/(double)noZeroPixel);
		return semc;
	}
}
