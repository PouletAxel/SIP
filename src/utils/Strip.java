package utils;

public class Strip {

	private String _name;
	private String _chr;
	private int _x;
	private int _xEnd;
	private int _y;
	private int _yEnd;
	private int _size;
	private double _stripValue;
	private double _stripStd;
	private double _leftNeigStrip;
	private double _rightNeigStrip;
	
	/**
	 * 
	 * @param Name
	 * @param x
	 * @param xEnd
	 * @param y
	 * @param yEnd
	 */
	public Strip(String name, String chr, int x, int xEnd,int y, int yEnd){
		this._chr = chr;
		this._name = name;
		this._x = x;
		this._xEnd = xEnd;
		this._y = y;
		this._yEnd = yEnd;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName(){ return this._name;}
	/**
	 * 
	 * @param name
	 */
	public void setName(String name){ this._name = name;}
	
	/**
	 * 
	 * @return
	 */
	public String getChr(){ return this._chr;}
	/**
	 * 
	 * @param name
	 */
	public void setChr(String name){ this._chr = name;}
	 
	/**
	 * 
	 * @return
	 */
	public int getSize(){	return this._size;}
	/**
	 * 
	 * @param x
	 */
	public void setSize(int x){ this._size = x;}
	/**
	 * 
	 * @return
	 */
	public int getX(){	return this._x;}
	/**
	 * 
	 * @param x
	 */
	public void setX(int x){ this._x = x;}
	
	/**
	 * 
	 * @return
	 */
	public int getXEnd(){ return this._xEnd;}
	
	/**
	 * 
	 * @param xEnd
	 */
	public void setXEnd(int xEnd){this._xEnd = xEnd;}
	
	/**
	 * 
	 * @return
	 */
	public int getY(){ return this._y; }
	
	/**
	 * 
	 * @param y
	 */
	public void setY(int y){ this._y = y; }
	
	/**
	 * 
	 * @return
	 */
	public int getYEnd(){ return this._yEnd; }
	
	/**
	 * 
	 * @param yEnd
	 */
	public void setYEnd(int yEnd){ this._yEnd = yEnd; }
	
	public double getStripValue() {return this._stripValue;}
	public void setStripValue( double value){ this._stripValue = value;} 
	
	public double getStripStd() {return this._stripStd;}
	public void setStripStd( double value){ this._stripStd = value;} 
	
	public double getLeftNeigStrip() {return this._leftNeigStrip;}
	public void setLeftNeigStrip(double value){ this._leftNeigStrip = value;}
	
	public double getRightNeigStrip() {return this._rightNeigStrip;}
	public void setRightNeigStrip(double value){ this._rightNeigStrip = value;}
}
