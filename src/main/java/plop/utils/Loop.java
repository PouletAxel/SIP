package plop.utils;

import java.util.ArrayList;

/**
 * Class making loops object with its coordinate, value, and parameters
 * 
 * @author axel poulet
 *
 */
public class Loop {
	/** chromosome name.*/
	private String _chr;
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
	/** value of the avg of the diff  between loops value and the neighbourhood 8.*/
	private float _neigbhoord1 = -1;
	/** value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private float _paScoreAvg = -1;
	private float _paScoreAvgdev = -1;
	/** value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private float _paScoreMed = -1;
	/** value of the avg of the differential between loops value and the neighbourhood 24.*/
	private float _neigbhoord2 = -1;
	/**  value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private float _regPaScoreMed = -1;
	/**  value of the peak analysis value inspirate from Rao&Huntley et al., 2014, but the score is compute foreach loop and not for a set of loops.*/
	private float _regPaScoreAvg = -1;
	/** Average value of the neighbourhood 9.*/
	private float _avg = -1;
	/** Value of the loop*/
	private float _peakValue = -1;
	/** Standard deviation value of the neighbourhood 9.*/
	private float _std = -1;
	/** FDR score 1*/
	private float _paScoreFDR = -1;
	/**reg fdr socre 1 */
	private float _regPaScoreFDR = -1;
	/** FDR score 2 */
	private float _paScoreFDR2 = -1;
	/** reg fdr socre 2 */
	private float _regPaScoreFDR2 = -1;
	/** FDR score 3*/
	private float _paScoreFDR3 = -1;
	/**reg fdr socre 3 */
	private float _regPaScoreFDR3 = -1;
	
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
	//0				1	2	3			4	5	6		7			8						9				10						11						12	13	14
	//chromosome1	x1	x2	chromosome2	y1	y2	color	APScoreAvg	ProbabilityofEnrichment	RegAPScoreAvg	Avg_diffMaxNeihgboor_1	Avg_diffMaxNeihgboor_2	avg	std	value
	
	public Loop(String loop){
		String[] tLoop = loop.split("\t");
		this._chr = tLoop[0];
		this._name = loop;
		this._x = Integer.parseInt(tLoop[1]);
		this._xEnd = Integer.parseInt(tLoop[2]);
		this._y = Integer.parseInt(tLoop[4]);
		this._yEnd= Integer.parseInt(tLoop[5]); 
	}

	/**
	 *  Loop constructor
	 * @param name String name of the loop
	 * @param x int x coordinate
	 * @param y int y coordinate
	 * @param chr String name of the chromosome
	 * @param value float
	 * @param resolution int
	 */
	public Loop(String name, int x, int y, String chr, float value, int resolution){
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
	 * @param avg float Average
	 * @param std float Standard deviation
	 * @param value float 
	 */
	public Loop(String name, int x, int y, String chr, float avg, float std, float value){
		this.setName(name);
		this.setX(x);
		this.setY(y);
		this._chr = chr;
		this._avg = avg;
		this._std = std;
		this._peakValue = value;
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
	 *
	 * @return loop String
	 */
	public String loopToString(){
		String loop = _chr+"\t"+_name+"\t"+_x+"\t"+_y+"\t"+ _resolution+"\t"+_matrixSize+"\t"+ _diagSize+"\t"+
				_xEnd+"\t"+ _yEnd+"\t"+_neigbhoord1+"\t"+_paScoreAvg+"\t"+_paScoreAvgdev+"\t"+_paScoreMed
				+"\t"+_neigbhoord2+"\t"+_regPaScoreMed+"\t"+_regPaScoreAvg+"\t"+_avg+"\t"+_peakValue
				+"\t"+_std+"\t"+_paScoreFDR+"\t"+_regPaScoreFDR+"\t"+_paScoreFDR2+"\t"+_regPaScoreFDR2+"\t"
				+_paScoreFDR3+"\t"+_regPaScoreFDR3;
		return loop;
	}

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
	public int getDiagonalSize(){ return this._diagSize; }
		
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
	public float getValue(){ return this._peakValue; }
	/**
	 * Getter of the n 8 average value 
	 * @return double average of n 8 average
	 */
	public float getAvg(){	return this._avg; }
	
	/**
	 * Getter of the n 8 standard deviation 
	 * @return double standard deviation
	 */
	public float getStd(){	return this._std; }

	/**
	 * Getter of avg differential n 8 
	 * @return double of the differential avg
	 */
	public float getNeigbhoord1() { return this._neigbhoord1; }
	
	/**
	 * Setter of avg differential n 8 
	 * @param neigbhoord1 double differential avg
	 */
	public void setNeigbhoord1(float neigbhoord1){ this._neigbhoord1 = neigbhoord1; }
	
		
	/**
	 * Getter of avg differential n 24 
	 * @return double differential avg
	 */
	public float getNeigbhoord2(){	return _neigbhoord2; }
	
	/**
	 * Setter of avg differential n 24 
	 * @param neigbhoord2 double differential avg
	 */
	public void setNeigbhoord2(float neigbhoord2){	this._neigbhoord2 = neigbhoord2;}

	/**
	 * Getter of the peak analysis loop 
	 * @return double PA score
	 */
	public float getPaScoreAvg(){	return this._paScoreAvg; }
	public float getPaScoreAvgdev(){	return this._paScoreAvgdev; }
	/**
	 *	Setter  of the peak analysis loop score
	 * @param paScore double PA score
	 */
	public void setPaScoreAvg(float paScore){ this._paScoreAvg = paScore; }

	/**
	 *
	 * @param paScoredev
	 */
	public void setPaScoreAvgdev(float paScoredev){ this._paScoreAvgdev = paScoredev; }
	
	/**
	 * Getter of the peak analysis loop 
	 * @return double PA score
	 */
	public double getPaScoreMed(){ return this._paScoreMed; }
	
	/**
	 *	Setter  of the peak analysis loop score
	 * @param paScore
	 */
	public void setPaScoreMed(float paScore){ this._paScoreMed = paScore; }
	
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
	 * Getter of loop coordinate, return arraylist 0: x; 1: x_end; 2: y; 3: y_end
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
	public float getRegionalPaScoreMed(){ return this._regPaScoreMed; }

	/**
	 * 
	 * @param m_RpaScore
	 */
	public void setRegionalPaScoreMed(float m_RpaScore){ this._regPaScoreMed = m_RpaScore; }

	/**
	 * Getter of regional peak analysis score 
	 * @return double regional PA score
	 */
	public float getRegionalPaScoreAvg(){ return this._regPaScoreAvg; }
	
	/**
	 * Setter of regional PA score
	 * @param rpaScore
	 */
	public void setRegionalPaScoreAvg(float rpaScore){ this._regPaScoreAvg = rpaScore; }
	
	/**
	 * Getter of the name of the chromosome 
	 * @return String chr
	 */
	public String getChr(){ return this._chr; }

	/**
	 * Setter of the avg of th n 8 
	 * @param avg double 
	 */
	public void setAvg(float avg) { this._avg=avg; }

	/**
	 * 
	 * @return
	 */
	public float getRegionalPaScoreAvgFDR(){ return this._regPaScoreFDR;}
	/**
	 * 
	 * @return
	 */
	public float getRegionalPaScoreAvgFDR2(){ return this._regPaScoreFDR2; }
	/**
	 * 
	 * @return
	 */
	public float getRegionalPaScoreAvgFDR3(){ return this._regPaScoreFDR3; }
	
	/**
	 * 
	 * @param rpaScoreFDR
	 */
	public void setRegionalPaScoreAvgFDR(float rpaScoreFDR){ this._regPaScoreFDR = rpaScoreFDR; }
	/**
	 * 
	 * @param rpaScoreFDR2
	 */
	public void setRegionalPaScoreAvgFDR2(float rpaScoreFDR2){ this._regPaScoreFDR2 = rpaScoreFDR2; }
	/**
	 * 
	 * @param rpaScoreFDR3
	 */
	public void setRegionalPaScoreAvgFDR3(float rpaScoreFDR3){ this._regPaScoreFDR3 = rpaScoreFDR3; }
	/**
	 * 
	 * @return
	 */
	public float getPaScoreAvgFDR(){	return this._paScoreFDR; }
	/**
	 * 
	 * @return
	 */
	public float getPaScoreAvgFDR2(){	return this._paScoreFDR2; }
	/**
	 * 
	 * @return
	 */
	public float getPaScoreAvgFDR3(){	return this._paScoreFDR3; }
	/**
	 * 
	 * @param paScoreFDR
	 */
	public void setPaScoreAvgFDR(float paScoreFDR){ this._paScoreFDR = paScoreFDR; }
	/**
	 * 
	 * @param paScoreFDR2
	 */
	public void setPaScoreAvgFDR2(float paScoreFDR2){ this._paScoreFDR2 = paScoreFDR2; }
	/**
	 * 
	 * @param paScoreFDR3
	 */
	public void setPaScoreAvgFDR3(float paScoreFDR3){ this._paScoreFDR3 = paScoreFDR3; }
	
}
