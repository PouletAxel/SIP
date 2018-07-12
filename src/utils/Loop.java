package utils;

import java.util.ArrayList;

/**
 * 
 * @author plop
 *
 */
public class Loop {
	/** */
	private String m_chr ="";
	/** */
	private String m_name;
	/** */
	private int m_x;
	/** */
	private int m_y;
	/** */
	private int m_resolution;
	/** */
	private int m_matrixSize;
	/** */
	private int m_diagSize;
	/** */
	private int m_xEnd;
	/** */
	private int m_yEnd;
	/** */
	private double m_neigbhoord1 = -1;
	/** */
	private double m_paScoreAvg = -1;
	/** */
	private double m_neigbhoord2 = -1;
	/** */
	private double m_RpaScoreAvg = -1;
	/** */
	private double m_percentage = -1;
	/** */
	private double m_percentageZero = -1;
	/** */
	private double m_nbZeroInTheImage = -1;
	/** */
	private double m_avg = -1;
	/** */
	private double m_std = -1;
	
	
	/**
	 * 
	 * @param name
	 * @param chr
	 * @param x
	 * @param y
	 */
	public Loop(String name, int x, int y, String chr){
		this.setName(name);
		this.setX(x);
		this.setY(y);
		m_chr = chr;
	}
	
	/**
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param chr
	 * @param avg
	 * @param std
	 */
	public Loop(String name, int x, int y, String chr, double avg, double std){
		this.setName(name);
		this.setX(x);
		this.setY(y);
		m_chr = chr;
		m_avg = avg;
		m_std = std;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		return this.m_name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.m_name = name;
	}
		
	/**
	 * 
	 * @return
	 */
	public int getX(){
		return m_x;
	}

	/**
	 * 
	 * @param m_x
	 */
	public void setResolution(int x){
		this.m_resolution = x;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getResolution(){
		return m_resolution;
	}
	
	/**
	 * 
	 * @param m_x
	 */
	public void setMatrixSize(int x){
		this.m_matrixSize = x;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMatrixSize(){
		return m_matrixSize;
	}
	
	/**
	 * 
	 * @param m_x
	 */
	public void setDiagSize(int x){
		this.m_diagSize = x;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDiagSize(){
		return m_diagSize;
	}
		
	/**
	 * 
	 * @param m_x
	 */
	public void setX(int x){
		this.m_x = x;
	}
	/**
	 * 
	 * @return
	 */
	public int getY(){
		return m_y;
	}
	
	/**
	 * 
	 * @param m_y
	 */
	public void setY(int y){
		this.m_y = y;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getAvg(){
		return m_avg;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getStd(){
		return m_std;
	}

	/**
	 * 
	 * @return
	 */
	public double getNeigbhoord1(){
		return m_neigbhoord1; 
	}
	
	/**
	 * 
	 * @param neigbhoord1
	 */
	public void setNeigbhoord1(double neigbhoord1){
		this.m_neigbhoord1 = neigbhoord1;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getNeigbhoord2(){
		return m_neigbhoord2; 
	}
	
	/**
	 * 
	 * @param neigbhoord1
	 */
	public void setNeigbhoord2(double neigbhoord2){
		this.m_neigbhoord2 = neigbhoord2;
	}
	/**
	 * 
	 * @return
	 */
	public double getPaScoreAvg(){
		return m_paScoreAvg;
	}
	
	/**
	 * 
	 * @param m_paScore
	 */
	public void setPaScoreAvg(double paScore){
		this.m_paScoreAvg = paScore;
	}
	
	/**
	 * 
	 * @param x
	 * @param x_end
	 * @param y
	 * @param y_end
	 */
	public void setCoordinates(int x, int x_end, int y, int y_end){
		this.m_x = x;
		this.m_xEnd = x_end;
		this.m_y = y;
		this.m_yEnd =y_end;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Integer> getCoordinates(){
		ArrayList<Integer> listCoord = new ArrayList<Integer>();
		listCoord.add(this.m_x);
		listCoord.add(this.m_xEnd);
		listCoord.add(this.m_y);
		listCoord.add(this.m_yEnd);
		return listCoord;
	}
	
	/*
	public double getRegionalPaScoreMed(){
		return m_RpaScoreMed;
	}

	public void setRegionalPaScoreMed(double m_RpaScore){
		this.m_RpaScoreMed = m_RpaScore;
	}*/

	/**
	 * 
	 * @return
	 */
	public double getRegionalPaScoreAvg(){
		return m_RpaScoreAvg;
	}
	
	/**
	 * 
	 * @param m_RpaScore
	 */
	public void setRegionalPaScoreAvg(double rpaScore){
		this.m_RpaScoreAvg = rpaScore;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getChr(){
		return m_chr;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getPercentage(){
		return m_percentage;
	}
	
	/**
	 * 
	 * @param m_percentage
	 */
	public void setPercentage(double percentage){
		this.m_percentage = percentage;
	}
	
	/**
	 * 
	 * @param i
	 */
	public void setPercentageOfZero(double i){
		m_percentageZero = i;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getPercentageOfZero(){
		return m_percentageZero;
	}

	/**
	 * 
	 * @return
	 */
	public double getNbZeroInTheImage() {
		return m_nbZeroInTheImage;
	}

	/**
	 * 
	 * @param m_nbZeroInTheImage
	 */
	public void setNbZeroInTheImage(double nbZeroInTheImage) {
		this.m_nbZeroInTheImage = nbZeroInTheImage;
	}

	public void setAvg(double avg) {
		m_avg=avg;
		
	}
}
