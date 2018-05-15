package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * 
 * @author plop
 *
 */
public class EnhanceNoiseScore {
	// calcul pour chaque des maxima en changeant le seuil de noise
	private ImagePlus m_imgFilter = new ImagePlus();
	private ImagePlus m_imgRaw = new ImagePlus();
	private int m_minNoiseTolreance=0;
	private int m_maxNoiseTolreance=0;
	private HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	private int m_nbStep = 0;
	private String m_chr ="";
	private int m_nbZero = 0;
	
	/**
	 * 
	 * @param img
	 */
	public EnhanceNoiseScore(ImagePlus imgRaw, ImagePlus imgFilter, String chr, int min, int max){
		m_imgRaw = imgRaw;
		m_imgFilter = imgFilter;
		m_minNoiseTolreance = min;
		m_maxNoiseTolreance = max;
		m_chr = chr;
		System.out.println(m_chr);
		m_nbZero = nbZero(m_imgRaw);
	}
	
	
	public void findMaxima(){
		FindMaxima fm = new FindMaxima(m_imgFilter,m_minNoiseTolreance);
		fm.runSimple(m_imgRaw);
		ArrayList<String> temp = fm.getMaxima(m_imgRaw);
		for(int j = 0; j < temp.size();++j){
			String[] parts = temp.get(j).split("\\t");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			String name= m_chr+"\t"+temp.get(j);
			//if(m_data.containsKey(temp.get(j))==true){
				Loop maxima = new Loop(temp.get(j),x,y,m_chr);
				maxima.addNoiseScore(m_minNoiseTolreance);
				maxima.setNbZeroInTheImage(m_nbZero);
				m_data.put(name, maxima);
			/*}
			else{
				
				maxima.setNoiseScore(m_minNoiseTolreance);
				m_data.put(temp.get(j), maxima);
			}*/
		}
	}
	
	
	/**
	 * 
	 */
	public void computeEnhanceScore(){
		for(int i = m_minNoiseTolreance; i<= m_maxNoiseTolreance; i +=250){
			FindMaxima fm = new FindMaxima(m_imgFilter,i);
			fm.runSimple(m_imgRaw);
			ArrayList<String> temp = fm.getMaxima(m_imgRaw);
			for(int j = 0; j < temp.size();++j){
				String[] parts = temp.get(j).split("\\t");
				int x = Integer.parseInt(parts[0]);
				int y = Integer.parseInt(parts[1]);
				if(m_data.containsKey(temp.get(j))==true){
					Loop maxima = m_data.get(temp.get(j));
					maxima.addNoiseScore(i);
					maxima.setNbZeroInTheImage(m_nbZero);
					m_data.put(temp.get(j), maxima);
				}
				else{
					Loop maxima = new Loop(temp.get(j),x,y,m_chr);
					maxima.setNoiseScore(i);
					maxima.setNbZeroInTheImage(m_nbZero);
					m_data.put(temp.get(j), maxima);
				}
			}
			++m_nbStep;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Loop> getDataMaxima (){
		Set<String> key = m_data.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			String cle = it.next();
			Loop loop = m_data.get(cle);
			loop.meanNoiseScore(m_nbStep);
		}
		return this.m_data;
	}
	
	private int nbZero(ImagePlus img){ 
		int nb = 0;  
		ImageProcessor ip = img.getProcessor();
		for(int i = 0; i < img.getWidth(); ++i){
			for(int j = 0; j < img.getHeight(); ++j){
				if(ip.getPixel(i, j)==0){
					nb++;
				}
			}
		}
		return nb;
	}

}
