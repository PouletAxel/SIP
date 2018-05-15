package utils;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class NonZeroCorrection {
	

	ImagePlus m_img = new ImagePlus();
	ImageProcessor m_ip;
	private ArrayList<Integer> m_countNonZero = new ArrayList<Integer>();


	/** */
	/**
	 * 
	 */
	public NonZeroCorrection(ImagePlus img){
	
		 m_img = img;
		 m_ip = m_img.getProcessor();
		
	}
	
	
	/**
	 * 
	 */
	private void nonZeroTestDetection(){
		ArrayList<Integer> max = new ArrayList<Integer>();
		int w = m_ip.getWidth();
		int h = m_ip.getHeight();
		for(int i=0; i<w; ++i){
			m_countNonZero.add(i, 0);
			max.add(i, 0);
			for(int j=0;j<h;++j){
				int v = (int)m_ip.getPixelValue(i, j);
				if(v>0){
					int num = 1+m_countNonZero.get(i);
					m_countNonZero.set(i,num);
					if(v>max.get(i)){max.set(i, v);}
				}
			}
		}
		double avg = avg();
		double std = std(avg);
		//System.out.println(avg+"\t"+std+"\t"+(avg-2*std)+"\t"+(avg+2*std));
		int nb = 0;
		for(int i = 0; i< m_countNonZero.size(); i++){
			if(m_countNonZero.get(i)>avg+8*std || m_countNonZero.get(i)<avg-8*std){
				m_countNonZero.set(i, 0);
				nb++;
			}
		}
		//System.out.println(" remove "+nb+" raw due to low nb of value");
	}
		
	private double avg(){
		int sum = 0;
		int nbNonZero = 0;
		for(int i = 0; i< m_countNonZero.size(); i++){
			if(m_countNonZero.get(i)>0){
				sum+=m_countNonZero.get(i);
				++nbNonZero;
			}
		}
		//System.out.print(nbNonZero+"\n");
		if(nbNonZero > 0) return sum/nbNonZero;
		else return 0;
	}
	
	/**
	 * 
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
