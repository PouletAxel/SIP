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
	private String _chr ="";
	/** loops name: chr	start end value.*/
	private String _name;
	/** x coordinate.*/
	private int _x;
	/** y coordinate.*/
	private int _y;
	/** loop resolution.*/
	private int _resolution;
	/** size of the image.*/
	private int _matrixSize;
	/** diagonal size.*/
	private int _diagSize;
	/** x coordinate+resolution.*/
	private int _xEnd;
	/** y coordinate+resolution.*/
	private int _yEnd;
	/** */
	private Strip _stripX;
	/** */
	private Strip _stripY;
	/** value of the avg of the diff  between loops value and the neighbourhood 8.*/
	private double _neigbhoord1 = -1;
	/** value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double _paScoreAvg = -1;
	/** value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double _paScoreMed = -1;
	/** value of the avg of the differential between loops value and the neighbourhood 24.*/
	private double _neigbhoord2 = -1;
	/**  value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double _regPaScoreMed = -1;
	/**  value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private double _regPaScoreAvg = -1;
	/** Average value of the neighbourhood 9.*/
	private double _avg = -1;
	/** Value of the loop*/
	private double _peakValue = -1;
	/** Standard deviation value of the neighbourhood 9.*/
	private double _std = -1;
	
	
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
		this._chr = chr;
	}
	
	/**
	 * Loop constructor
	 * @param name String name of the loop
	 * @param chr String name of the chromosome
	 * @param x int x coordinate
	 * @param y int y coordinate
	 */
	
	public Loop(String name, int x, int y, String chr, int value, int resolution){
		this.setName(name);
		this.setX(x);
		this.setY(y);
		this._chr = chr;
		this._peakValue = value;
		this._resolution = resolution;
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
		this._chr = chr;
		this._avg = avg;
		this._std = std;
		this._peakValue = peakValue;
	}
	
	/**
	 * Getter of the name loop
	 * @return String name of the loop
	 */
	public String getName(){ return this._name; }

	/**
	 * Setter of the name loop
	 * @param name String
	 */
	public void setName(String name){ this._name = name;}
		
	/**
	 * Getter of the x coordinate
	 * @return int x coordinate
	 */
	public int getX(){	return this._x;}

	/**
	 * Setter of the loop resolution
	 * @param x int loop resolution
	 */
	public void setResolution(int x){ this._resolution = x; }
	
	/**
	 * Getter of the loop resolution
	 * @return int resolution
	 */
	public int getResolution(){	return this._resolution;}
	
	/**
	 * Setter of the matrix size
	 * @param x int size of the matrix
	 */
	public void setMatrixSize(int x){ this._matrixSize = x; }
	
	/**
	 * Getter of the matrix size
	 * @return int matrix size
	 */
	public int getMatrixSize(){ return this._matrixSize; }
	
	/**
	 * Setter of the diagonal size
	 * @param x int diagonal size
	 */
	public void setDiagSize(int x){ 	this._diagSize = x;}
	
	/**
	 * Getter of the diagonal size
	 * @return int diagonal size
	 */
	public int getDiagSize(){ return this._diagSize; }
		
	/**
	 * Setter of x coordinate
	 * @param x
	 */
	public void setX(int x){ this._x = x; }
	/**
	 * Getter of y loop coordinate's
	 * @return int y loop coordinate's
	 */
	public int getY(){ return this._y; }
	
	/**
	 * Setter of the y loops coordinate's
	 * @param y int loop coordinate's 
	 */
	public void setY(int y){ this._y = y; }
	
	/**
	 * Getter of the loop(x,y) value
	 * @return double loop value
	 */
	public double getValue(){ return this._peakValue; }
	/**
	 * Getter of the n 8 average value 
	 * @return double average of n 8 average
	 */
	public double getAvg(){	return this._avg; }
	
	/**
	 * Getter of the n 8 standard deviation 
	 * @return double standard deviation
	 */
	public double getStd(){	return this._std; }

	/**
	 * Getter of avg differential n 8 
	 * @return double of the differential avg
	 */
	public double getNeigbhoord1() { return this._neigbhoord1; }
	
	/**
	 * Setter of avg differential n 8 
	 * @param neigbhoord1 double differential avg
	 */
	public void setNeigbhoord1(double neigbhoord1){ this._neigbhoord1 = neigbhoord1; }
	/**
	 * Getter of avg differential n 8 
	 * @return double of the differential avg
	 */
	public Strip getStripX(){ return this._stripX;  }
	
	/**
	 * Setter of avg differential n 8 
	 * @param neigbhoord1 double differential avg
	 */
	public void setStripX(Strip strip){ this._stripX = strip; }
	
	/**
	 * Getter of avg differential n 8 
	 * @return double of the differential avg
	 */
	public Strip getStripY(){	return this._stripY; }
		
	/**
	 * Setter of avg differential n 8 
	 * @param neigbhoord1 double differential avg
	 */
	public void setStripY(Strip strip){ this._stripY = strip; }
		
	/**
	 * Getter of avg differential n 24 
	 * @return double differential avg
	 */
	public double getNeigbhoord2(){	return _neigbhoord2; }
	
	/**
	 * Setter of avg differential n 24 
	 * @param neigbhoord2 double differential avg
	 */
	public void setNeigbhoord2(double neigbhoord2){	this._neigbhoord2 = neigbhoord2;}

	/**
	 * Getter of the peak analysis loop 
	 * @return double PA score
	 */
	public double getPaScoreAvg(){	return this._paScoreAvg; }
	
	/**
	 *	Setter  of the peak analysis loop score
	 * @param m_paScore double PA score
	 */
	public void setPaScoreAvg(double paScore){ this._paScoreAvg = paScore; }
	
	/**
	 * Getter of the peak analysis loop 
	 * @return double PA score
	 */
	public double getPaScoreMed(){ return this._paScoreMed; }
	
	/**
	 *	Setter  of the peak analysis loop score
	 * @param m_paScore double PA score
	 */
	public void setPaScoreMed(double paScore){ this._paScoreMed = paScore; }
	
	/**
	 * Setter of the loop coordinate
	 * @param x int x coordinate
	 * @param x_end int x end coordinate
	 * @param y	int y coordinate
	 * @param y_end y end coordinate
	 */
	public void setCoordinates(int x, int x_end, int y, int y_end){
		this._x = x;
		this._xEnd = x_end;
		this._y = y;
		this._yEnd =y_end;
	}
	
	/**
	 * Getter of loop corrdinate, return arraylist 0: x; 1: x_end; 2: y; 3: y_end 
	 * @return ArrayList of integer
	 */
	public ArrayList<Integer> getCoordinates(){
		ArrayList<Integer> listCoord = new ArrayList<Integer>();
		listCoord.add(this._x);
		listCoord.add(this._xEnd);
		listCoord.add(this._y);
		listCoord.add(this._yEnd);
		return listCoord;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getRegionalPaScoreMed(){ return this._regPaScoreMed; }

	/**
	 * 
	 * @param m_RpaScore
	 */
	public void setRegionalPaScoreMed(double m_RpaScore){ this._regPaScoreMed = m_RpaScore; }

	/**
	 * Getter of regional peak analysis score 
	 * @return doubl reginal PA score
	 */
	public double getRegionalPaScoreAvg(){ return this._regPaScoreAvg; }
	
	/**
	 * Setter of regional PA score
	 * @param m_RpaScore double
	 */
	public void setRegionalPaScoreAvg(double rpaScore){ this._regPaScoreAvg = rpaScore; }
	
	/**
	 * Getter of the name of the chromosome 
	 * @return String chr
	 */
	public String getChr(){ return this._chr; }

	/**
	 * Setter of the avg of th n 8 
	 * @param avg double 
	 */
	public void setAvg(double avg) { this._avg=avg; }
	
}
