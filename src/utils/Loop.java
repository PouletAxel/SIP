package utils;

import java.util.ArrayList;

public class Loop {
	private String m_chr ="";
	private String m_name;
	private int m_x;
	private int m_y;
	private int m_xEnd;
	private int m_yEnd;
	private double m_paScoreMed = -1;
	private double m_paScoreAvg = -1;
	private double m_RpaScoreMed = -1;
	private double m_RpaScoreAvg = -1;
	private double m_percentage = -1;
	private double m_percentageZero = -1;
	private double m_nbZeroInTheImage = -1;
	
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
	 * @return
	 */
	public String getName(){ return this.m_name; }

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) { this.m_name = name; }
		
	/**
	 * 
	 * @return
	 */
	public int getX(){	return m_x;	}

	/**
	 * 
	 * @param m_x
	 */
	public void setX(int m_x){	this.m_x = m_x;	}
	
	/**
	 * 
	 * @return
	 */
	public int getY(){	return m_y;	}
	
	/**
	 * 
	 * @param m_y
	 */
	public void setY(int m_y){ this.m_y = m_y;	}
	

	/**
	 * 
	 * @return
	 */
	public double getPaScoreMed(){ return m_paScoreMed; }
	
	/**
	 * 
	 * @param m_paScore
	 */
	public void setPaScoreMed(double m_paScore){ this.m_paScoreMed = m_paScore;}
	
	/**
	 * 
	 * @return
	 */
	public double getPaScoreAvg(){ return m_paScoreAvg; }
	
	/**
	 * 
	 * @param m_paScore
	 */
	public void setPaScoreAvg(double m_paScore){ this.m_paScoreAvg = m_paScore;}
	
	public void setCoordinates(int x, int x_end, int y, int y_end){
		this.m_x = x;
		this.m_xEnd = x_end;
		this.m_y = y;
		this.m_yEnd =y_end;
	}
	
	public ArrayList<Integer> getCoordinates(){
		ArrayList<Integer> listCoord = new ArrayList<Integer>();
		listCoord.add(this.m_x);
		listCoord.add(this.m_xEnd);
		listCoord.add(this.m_y);
		listCoord.add(this.m_yEnd);
		return listCoord;
	}

	public double getRegionalPaScoreMed(){ return m_RpaScoreMed;}

	public void setRegionalPaScoreMed(double m_RpaScore) { this.m_RpaScoreMed = m_RpaScore;}

	public double getRegionalPaScoreAvg() {	return m_RpaScoreAvg; }

	public void setRegionalPaScoreAvg(double m_RpaScore) { this.m_RpaScoreAvg = m_RpaScore; }
	
	public String getChr() {return m_chr;}

	public double getPercentage() {	return m_percentage;}

	public void setPercentage(double m_percentage) { this.m_percentage = m_percentage; }

	public void setPercentageOfZero(double i) {	m_percentageZero = i;}
	public double getPercentageOfZero() {return m_percentageZero;}

	public double getNbZeroInTheImage() {
		return m_nbZeroInTheImage;
	}

	public void setNbZeroInTheImage(double m_nbZeroInTheImage) {
		this.m_nbZeroInTheImage = m_nbZeroInTheImage;
	}
}
