package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Class to change the image coordinate in genome coordinate, with the name of the image, the resolution, the size of the image,
 * the step and the size of the diagonal.
 * 
 * Loops detected with white strips are removed. Furthermore the corner of the images are not take account. We test also the proximal loops to avoid overlaping loops.
 * 
 * @author axel poulet
 *
 */
public class CoordinatesCorrection {

	/** HashMap of the loops with new coordinates*/
	private HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	int m_resolution = 0;
	
	/**
	 * 
	 */
	public CoordinatesCorrection(int resolution){
		m_resolution =resolution;
	
	}	
	
	/**
	 * Method to change the image coordinates in genomes coordinates, the method calls other methods to remove the loops close to some withe strips, or close to other loops.
	 * 
	 * @param temp: HashMap of loops with the image coordinates
	 * @param first: boolean if true it is the first image of the chromosome
	 * @param last: boolean if true it is the last image of the chromosome
	 * @param index: num of the image
	 * @param nonZero: list of int containing the non zero information
	 * @return a HashMap containing the loops with the new coordinates
	 */
	public HashMap<String,Loop> imageToGenomeCoordinate(HashMap<String,Loop> temp, int index){
		int x;
		int y;
		/*int xtop_lim = 0;
		int ytop_lim = 0;
		int xbottom_lim = m_imageSize;
		int ybottom_lim = m_imageSize;
		
		if(first == false){
			System.out.println("plop");
			xtop_lim = m_imageSize/15;
			ytop_lim = m_imageSize/15;
		}
		if(last==false){
			 xbottom_lim = m_imageSize-m_imageSize/15;
			 ybottom_lim = m_imageSize-m_imageSize/15;
		}*/
		Set<String> key = temp.keySet();
		Iterator<String> it = key.iterator();
		
		while (it.hasNext()){
			Loop loop = temp.get(it.next());
			x = loop.getX();
			y = loop.getY();
			int resolution = loop.getResolution(); 
			int diagSize = loop.getDiagSize()*resolution;
			int imageSize = loop.getMatrixSize();
			int step = imageSize/2;
			int a = (x+(index*step))*resolution;
			int a_end = a+resolution;
			int b = (y+(index*step))*resolution;
			int b_end =b+resolution;
			String newName = loop.getChr()+"\t"+a+"\t"+b;
			//if((x > xtop_lim || y > ytop_lim) && (x < xbottom_lim || y < ybottom_lim) &&
			if(a!=b && testProximalLoop(loop.getChr(),a,b,newName,resolution) == false && Math.abs(a-b) > diagSize){
				if (x > 1 && y > 1 && y < imageSize-2 && x < imageSize-2){
					if(m_data.containsKey(newName)==false){//  && (testStripNeighbour(x)==true && testStripNeighbour(y)==true)){
						loop.setCoordinates(a, a_end, b, b_end);
						loop.setName(newName);
						m_data.put(newName, loop);
					}
				}
			}	 
		}
		return m_data;
	}
	
	/**
	 * Method similar to the previous use for the HiC comparison.
	 * 
	 * @param temp
	 * @param index
	 * @return
	 */
	public HashMap<String,Loop> imageToGenomeCoordinateCompare(HashMap<String,Loop> temp, int index){
		int x;
		int y;
		Set<String> key = temp.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			Loop loop = temp.get(it.next());
			int resolution = loop.getResolution(); 
			int diagSize = loop.getDiagSize()*resolution;
			int imageSize = loop.getMatrixSize();
			int step = imageSize/2;
			x = loop.getX();
			y = loop.getY();
			int a = (x+(index*step))*resolution;
			int a_end = a+resolution;
			int b = (y+(index*step))*resolution;
			int b_end =b+resolution;
			String newName = loop.getChr()+"\t"+a+"\t"+b;
			if(Math.abs(a-b) > diagSize){
				loop.setCoordinates(a, a_end, b, b_end);
				loop.setName(newName);
				m_data.put(newName, loop);
			}
		}
		return m_data;
	}
	
	/**
	 * test the presence of proximal loops in the hashMap m_data, return false if no proximal loops are detected.
	 * else retrun true,
	 * 
	 * @param x: int x loop coordinate's
	 * @param y: int y loop coordinate's 
	 * @param newKey: new key in teh hashMap.
	 * @return boolean true (proximal loop detected) flase (no loops detected)
	 */
	private boolean testProximalLoop(String chr, int x, int y, String newKey, int resolution){
		boolean test = false;
		if (resolution > m_resolution){
			for(int i = x-resolution; i <= x+resolution; i = i+resolution){
				for(int j = y-resolution; j <= y+resolution; j = j+resolution){
					String key = chr+"\t"+i+"\t"+j;
					if (newKey != key && m_data.containsKey(key)){
						test = true;
						return true;
					}
				}
			}
			if(test == false){
				for(int i = x-m_resolution; i <= x+m_resolution; i = i+m_resolution){
					for(int j = y-m_resolution; j <= y+m_resolution; j = j+m_resolution){
						String key = chr+"\t"+i+"\t"+j;
						//System.out.println("prout "+key);
						if (newKey != key && m_data.containsKey(key)){
							System.out.println("prout "+key);
							test = true;
							return true;
						}
					}
				}
			}
		}
		else{
			for(int i = x-resolution; i <= x+resolution; i = i+resolution){
				for(int j = y-resolution; j <= y+resolution; j = j+resolution){
					String key = chr+"\t"+i+"\t"+j;
					if (newKey != key && m_data.containsKey(key)){
						test = true;
						return true;
					}
				}
			}
		}
			return test;
	}
	
	
	/**
	 * getter of m_data
	 * @return HashMap of loops
	 */
	public HashMap<String,Loop> getData(){
		return this.m_data;
	}
	
	/**
	 * setter of m_data
	 * @param data 
	 */
	public void setData(HashMap<String,Loop> data){
		this.m_data = data;
	}
}