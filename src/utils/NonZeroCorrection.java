package utils;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * 
 * @author plop
 *
 */
public class NonZeroCorrection {
	/** */
	ImagePlus m_img = new ImagePlus();
	/** */
	ImageProcessor m_ip;
	/** */
	private ArrayList<Integer> m_countNonZero = new ArrayList<Integer>();


	/**
	 * 
	 * @param img
	 */
	public NonZeroCorrection(ImagePlus img){
		 m_img = img;
		 m_ip = m_img.getProcessor();
		
	}
	
	
	/**
	 * 
	 */
	private void nonZeroTestDetection(){
		int w = m_ip.getWidth();
		int h = m_ip.getHeight();
		for(int i=0; i<w; ++i){
			m_countNonZero.add(i, 0);
			for(int j=0;j<h;++j){
				int v = (int)m_ip.getPixelValue(i, j);
				if(v>0){
					int num = 1+m_countNonZero.get(i);
					m_countNonZero.set(i,num);
				}
			}
		}
		double avg = avg();
		double std = std(avg);
		double up = avg+5*std;
		double down = avg-3*std;
		System.out.println(down+"\t"+up);
		for(int i = 0; i< m_countNonZero.size(); i++){
			if(m_countNonZero.get(i) < down || m_countNonZero.get(i) > up){
				m_countNonZero.set(i, 0);
			}
		}
	}
		
	/**
	 * 
	 * @return
	 */
	private double avg(){
		int sum = 0;
		int nbNonZero = 0;
		for(int i = 0; i< m_countNonZero.size(); i++){
			if(m_countNonZero.get(i)>0){
				sum+=m_countNonZero.get(i);
				++nbNonZero;
			}
		}
		if(nbNonZero > 0) return sum/nbNonZero;
		else return 0;
	}
	
	/**
	 * 
	 * @param mean
	 * @return
	 */
	private double std(double mean){
		double semc = 0;
		int nbNonZero = 0;
		for(int i = 0; i< m_countNonZero.size(); i++){
			if(m_countNonZero.get(i)>0){
				nbNonZero++;
				semc += (m_countNonZero.get(i)-mean)*(m_countNonZero.get(i)-mean);
			}	
		}	
		semc = Math.sqrt(semc/(double)nbNonZero);
		return semc;
	}	
		
	/**
	 * 
	 * @return
	 */
	public ArrayList<Integer>  getNonZeroList() {
		nonZeroTestDetection();
		return this.m_countNonZero;
	}
}
