package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class CoordinatesCorrection {

	private HashMap<String,Loop> m_data = new HashMap<String,Loop>();
	private int index;
	private ArrayList<Integer> m_countNonZero = new ArrayList<Integer>();
	private int m_step;
	private int m_resolution;
	private int m_imageSize;
	private int m_diagSize;
	
	/**
	 * 
	 * @param step
	 * @param resolution
	 * @param imageSize
	 * @param diagSize
	 */
	public CoordinatesCorrection(int step, int resolution, int imageSize, int diagSize){
		this.m_step = step;
		this.m_resolution = resolution;
		this.m_imageSize = imageSize;
		this.m_diagSize = diagSize*resolution;
	}	
	
	/**
	 * 
	 * @param temp
	 * @param first
	 * @param last
	 * @param index
	 */
	public HashMap<String,Loop> imageToGenomeCoordinate(HashMap<String,Loop> temp, boolean first, boolean last, int index, ArrayList<Integer> nonZero){
		int x;
		int y;
		m_countNonZero = nonZero;
		int xtop_lim = 0;
		int ytop_lim = 0;
		int xbottom_lim = m_imageSize;
		int ybottom_lim = m_imageSize;
		
		if(first == false){
			xtop_lim = 100;
			ytop_lim = 100;
		}
		if(last==false){
			 xbottom_lim = m_imageSize-100;
			 ybottom_lim = m_imageSize-100;
		}
		Set<String> key = temp.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			Loop loop = temp.get(it.next());
			x = loop.getX();
			y = loop.getY();
			int a = (x+(index*m_step))*m_resolution;
			int a_end = a+m_resolution;
			int b = (y+(index*m_step))*m_resolution;
			int b_end =b+m_resolution;
			String newName = loop.getChr()+"\t"+a+"\t"+b;
			//System.out.println(x+" "+y);
			if((x > xtop_lim || y > ytop_lim) && 
				(x < xbottom_lim || y < ybottom_lim) &&
				a!=b && testProximalLoop(loop.getChr(),a,b,newName) == false &&
				Math.abs(a-b) > m_diagSize){
				if (x > 1 && y > 1 && y < m_imageSize-2 && x < m_imageSize-2){
					if(m_data.containsKey(newName)==false  && (testStripNeighbour(x)==true && testStripNeighbour(y)==true)){
						loop.setCoordinates(a, a_end, b, b_end);
						loop.setName(newName);
						m_data.put(newName, loop);
					}
				}
			}	 
				//else{
				//	if(m_data.containsKey(newName)==false && (testStrip(x)==true && testStrip(y)==true)){
				//		loop.setCoordinates(a, a_end, b, b_end);
				//		loop.setName(newName);
				//		m_data.put(newName, loop);
				//	}
				//}
			//}
		}
		return m_data;
	}
	
	
	public HashMap<String,Loop> imageToGenomeCoordinate(HashMap<String,Loop> temp, int index){
		int x;
		int y;
		Set<String> key = temp.keySet();
		Iterator<String> it = key.iterator();
		while (it.hasNext()){
			Loop loop = temp.get(it.next());
			x = loop.getX();
			y = loop.getY();
			int a = (x+(index*m_step))*m_resolution;
			int a_end = a+m_resolution;
			int b = (y+(index*m_step))*m_resolution;
			int b_end =b+m_resolution;
			String newName = loop.getChr()+"\t"+a+"\t"+b;
			if(Math.abs(a-b) > m_diagSize){
				loop.setCoordinates(a, a_end, b, b_end);
				loop.setName(newName);
				m_data.put(newName, loop);
			}
		}
		return m_data;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	private boolean testStripNeighbour(int i){
		if(m_countNonZero.get(i) > 0 && m_countNonZero.get(i+1) > 0 && m_countNonZero.get(i-1) > 0 ){
				//&& m_countNonZero.get(i+2) > 0 && m_countNonZero.get(i-2) > 0){
			return true;
		}
		else return false;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param newKey
	 * @return
	 */
	private boolean testProximalLoop(String chr, int x, int y, String newKey){
		boolean test= false;
		for(int i = x-m_resolution; i <= x+m_resolution; i = i+m_resolution){
			for(int j = y-m_resolution; j <= y+m_resolution; j = j+m_resolution){
				String key = chr+i+"\t"+j;
				if (newKey != key && m_data.containsKey(key)){
					//System.out.println("plopi");
					test = true;
				}
			}
		}
		return test;
	}
	/**
	 * 
	 * @param i
	 * @return
	 */
	/*private boolean testStrip(int i){
		if(m_countNonZero.get(i) > 0 ){	return true; }
		else return false;
	}*/
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Loop> getData(){ return this.m_data;}
	
	/**
	 * 
	 * @param data
	 */
	public void setData(HashMap<String,Loop> data){ this.m_data = data;}
}