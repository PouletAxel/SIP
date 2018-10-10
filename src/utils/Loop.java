package utils;

import java.util.ArrayList;

/**
 * Class making loops object with its coordinate, value, and parameters
 * 
 * @author axel poulet
 *
 */
public class Loop {
	/** chromosome name.*/
	private String m_chr ="";
	/** loops name: chr	start end value.*/
	private String m_name;
	/** x coordinate.*/
	private int m_x;
	/** y coordinate.*/
	private int m_y;
	/** loop resolution.*/
	private int m_resolution;
	/** size of the image.*/
	private int m_matrixSize;
	/** diagonal size.*/
	private int m_diagSize;
	/** x coordinate+resolution.*/
	private int m_xEnd;
	/** y coordinate+resolution.*/
	private int m_yEnd;
	
	/** value of the avg of the diff  between loops value and the neighbourhood 8.*/
	private double m_neigbhoord1 = -1;
	/** value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double m_paScoreAvg = -1;
	/** value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double m_paScoreMed = -1;
	/** value of the avg of the differential between loops value and the neighbourhood 24.*/
	private double m_neigbhoord2 = -1;
	/** value of the avg of the differential between loops value and the neighbourhood 24.*/
	private double m_neigbhoord3 = -1;
	/**  value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double m_RpaScoreMed = -1;
	/**  value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double m_RpaScoreAvg = -1;
	/** Average value of the neighbourhood 9.*/
	private double m_avg = -1;
	/** Value of the loop*/
	private double m_peakValue = -1;
	/** Standard deviation value of the neighbourhood 9.*/
	private double m_std = -1;
	
	
	/**
	 * Loop constructor
	 * @param name String name of the loop
	 * @param chr String name of the chromosome
	 * @param x int x coordinate
	 * @param y int y coordinate
	 */
	
	public Loop(String name, int x, int y, String chr){
		this.setName(name);
		this.setX(x);
		this.setY(y);
		m_chr = chr;
	}
	
	/**
	 * Loop constructor
	 * @param name String name of the loop
	 * @param x int x coordinate
	 * @param y int y coordinate
	 * @param chr String Chromosme name
	 * @param avg double Average
	 * @param std double Standard deviation
	 */
	public Loop(String name, int x, int y, String chr, double avg, double std, double peakValue){
		this.setName(name);
		this.setX(x);
		this.setY(y);
		m_chr = chr;
		m_avg = avg;
		m_std = std;
		m_peakValue = peakValue;
	}
	
	
	
	/**
	 * Getter of the name loop
	 * @return String name of the loop
	 */
	public String getName(){
		return this.m_name;
	}

	/**
	 * Setter of the name loop
	 * @param name String
	 */
	public void setName(String name){
		this.m_name = name;
	}
		
	/**
	 * Getter of the x coordinate
	 * @return int x coordinate
	 */
	public int getX(){
		return m_x;
	}

	/**
	 * Setter of the loop resolution
	 * @param x int loop resolution
	 */
	public void setResolution(int x){
		this.m_resolution = x;
	}
	
	/**
	 * Getter of the loop resolution
	 * @return int resolution
	 */
	public int getResolution(){
		return m_resolution;
	}
	
	/**
	 * Setter of the matrix size
	 * @param x int size of the matrix
	 */
	public void setMatrixSize(int x){
		this.m_matrixSize = x;
	}
	
	/**
	 * Getter of the matrix size
	 * @return int matrix size
	 */
	public int getMatrixSize(){
		return m_matrixSize;
	}
	
	/**
	 * Setter of the diagonal size
	 * @param x int diagonal size
	 */
	public void setDiagSize(int x){
		this.m_diagSize = x;
	}
	
	/**
	 * Getter of the diagonal size
	 * @return int diagonal size
	 */
	public int getDiagSize(){
		return m_diagSize;
	}
		
	/**
	 * Setter of x coordinate
	 * @param x
	 */
	public void setX(int x){
		this.m_x = x;
	}
	/**
	 * Getter of y loop coordinate's
	 * @return int y loop coordinate's
	 */
	public int getY(){
		return m_y;
	}
	
	/**
	 * Setter of the y loops coordinate's
	 * @param y int loop coordinate's 
	 */
	public void setY(int y){
		this.m_y = y;
	}
	
	/**
	 * Getter of the loop(x,y) value
	 * @return double loop value
	 */
	public double getValue(){
		return m_peakValue;
	}
	/**
	 * Getter of the n 8 average value 
	 * @return double average of n 8 average
	 */
	public double getAvg(){
		return m_avg;
	}
	
	/**
	 * Getter of the n 8 standard deviation 
	 * @return double standard deviation
	 */
	public double getStd(){
		return m_std;
	}

	/**
	 * Getter of avg differential n 8 
	 * @return double of the differential avg
	 */
	public double getNeigbhoord1(){
		return m_neigbhoord1; 
	}
	
	/**
	 * Setter of avg differential n 8 
	 * @param neigbhoord1 double differential avg
	 */
	public void setNeigbhoord1(double neigbhoord1){
		this.m_neigbhoord1 = neigbhoord1;
	}
	
	/**
	 * Getter of avg differential n 24 
	 * @return double differential avg
	 */
	public double getNeigbhoord2(){
		return m_neigbhoord2; 
	}
	
	/**
	 * Setter of avg differential n 24 
	 * @param neigbhoord2 double differential avg
	 */
	public void setNeigbhoord2(double neigbhoord2){
		this.m_neigbhoord2 = neigbhoord2;
	}
	/**
	 * Getter of avg differential n 24 
	 * @return double differential avg
	 */
	public double getNeigbhoord3(){
		return m_neigbhoord3; 
	}
	
	/**
	 * Setter of avg differential n 24 
	 * @param neigbhoord2 double differential avg
	 */
	public void setNeigbhoord3(double neigbhoord3){
		this.m_neigbhoord3 = neigbhoord3;
	}
	/**
	 * Getter of the peak analysis loop 
	 * @return double PA score
	 */
	public double getPaScoreAvg(){
		return m_paScoreAvg;
	}
	
	/**
	 *	Setter  of the peak analysis loop score
	 * @param m_paScore double PA score
	 */
	public void setPaScoreAvg(double paScore){
		this.m_paScoreAvg = paScore;
	}
	
	/**
	 * Getter of the peak analysis loop 
	 * @return double PA score
	 */
	public double getPaScoreMed(){
		return m_paScoreMed;
	}
	
	/**
	 *	Setter  of the peak analysis loop score
	 * @param m_paScore double PA score
	 */
	public void setPaScoreMed(double paScore){
		this.m_paScoreMed = paScore;
	}
	
	/**
	 * Setter of the loop coordinate
	 * @param x int x coordinate
	 * @param x_end int x end coordinate
	 * @param y	int y coordinate
	 * @param y_end y end coordinate
	 */
	public void setCoordinates(int x, int x_end, int y, int y_end){
		this.m_x = x;
		this.m_xEnd = x_end;
		this.m_y = y;
		this.m_yEnd =y_end;
	}
	
	/**
	 * Getter of loop corrdinate, return arraylist 0: x; 1: x_end; 2: y; 3: y_end 
	 * @return ArrayList of integer
	 */
	public ArrayList<Integer> getCoordinates(){
		ArrayList<Integer> listCoord = new ArrayList<Integer>();
		listCoord.add(this.m_x);
		listCoord.add(this.m_xEnd);
		listCoord.add(this.m_y);
		listCoord.add(this.m_yEnd);
		return listCoord;
	}
	
	
	public double getRegionalPaScoreMed(){
		return m_RpaScoreMed;
	}

	public void setRegionalPaScoreMed(double m_RpaScore){
		this.m_RpaScoreMed = m_RpaScore;
	}

	/**
	 * Getter of regional peak analysis score 
	 * @return doubl reginal PA score
	 */
	public double getRegionalPaScoreAvg(){
		return m_RpaScoreAvg;
	}
	
	/**
	 * Setter of regional PA score
	 * @param m_RpaScore double
	 */
	public void setRegionalPaScoreAvg(double rpaScore){
		this.m_RpaScoreAvg = rpaScore;
	}
	
	/**
	 * Getter of the name of the chromosome 
	 * @return String chr
	 */
	public String getChr(){
		return m_chr;
	}

	/**
	 * Setter of the avg of th n 8 
	 * @param avg double 
	 */
	public void setAvg(double avg) {
		m_avg=avg;
		
	}
}
