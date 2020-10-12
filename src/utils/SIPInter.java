package utils;

import java.io.*;
import java.util.*;

/**
 *
 *
 * @author axel poulet
 *
 */

public class SIPInter{


    /** String path of the input data*/
    private String _input;
    /** Path of the output file*/
    private String _output;
    /** Strength of the gaussian filter*/
    private double _gauss;
    /** Strength of the min filter*/
    private double _min;
    /** Strength of the max filter*/
    private double _max;
    /** % of staurated pixel after enhance contrast*/
    private double _saturatedPixel;
    /** Image size*/
    private int _matrixSize;
    /** Resolution of the bin dump in base*/
    private int _resolution;
    /** Threshold for the maxima detection*/
    private int _thresholdMaxima;
    /** HashMap of the chr size, the key = chr name, value = size of chr*/
    private HashMap<String,Integer> _chrSize;
    /** Size of the step to process each chr (step = matrixSize/2)*/
    private int _step;
    /** Number of pixel = 0 allowed around the loop*/
    private int _nbZero;
    /** is processed booelan*/
    private boolean _isProcessed;
    /** if is gui analysis*/
    private boolean _isGui;
    private boolean _isCooler;
    /** */
    private boolean _keepTif;

    public SIPInter(String output, HashMap<String, Integer> chrsize, double gauss, double min, double max, int resolution,
                    double saturatedPixel, int thresholdMax, int matrixSize, int nbZero, boolean keepTif) {

        this._output = output;
        this._chrSize = chrsize;
        this._gauss = gauss;
        this._min = min;
        this._max = max;
        this._resolution = resolution;
        this._saturatedPixel = saturatedPixel;
        this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        this._keepTif = keepTif;

    }


    /**
     * Getter of the input dir
     * @return path of the input dir
     */
    public String getInputDir(){ return this._input; }

    /**
     * Getter of the matrix size
     * @return the size of the image
     */
    public int getMatrixSize(){ return this._matrixSize; }


    /**
     * Getter of step
     * @return the step
     */
    public int getStep(){ return this._step;}

    /**
     * Setter of the path of the input directory
     * @param inputDir String of the input directory
     */
    public void setInputDir(String inputDir){ this._input = inputDir; }

    /**
     * Getter of the path of the output directory
     * @return path
     */
    public String getOutputDir(){ return this._output; }

    /**
     * Setter of the path of the output directory
     * @param outputDir String new path for the output directory
     */
    public void setOutputDir(String outputDir){	this._output = outputDir;}

    /**
     * Getter of the gaussian blur strength
     * @return double gaussian
     */
    public double getGauss(){ return this._gauss; }

    /**
     * Setter of the gaussian blur strength
     * @param gauss double
     */
    public void setGauss(double gauss){ this._gauss = gauss; }


    /**
     * Getter of the min filter strength
     * @return double strength of the min filter
     */
    public double getMin(){ return this._min;}

    /**
     * Setter of the min filter strength
     * @param min double min
     */
    public void setMin(double min){ this._min = min;}

    /**
     * Getter of the max filter strength
     * @return double max filter
     */
    public double getMax(){	return this._max; }

    /**
     * Setter of the min filter strength
     * @param max double max
     */
    public void setMax(double max){	this._max = max;}

    /**
     * Getter % of saturated pixel for the contrast enhancement
     * @return double percentage of saturated
     */
    public double getSaturatedPixel(){ return this._saturatedPixel; }

    /**
     * Setter % of saturated pixel for the contrast enhancement
     * @param saturatedPixel double saturatedPixel
     */
    public void setSaturatedPixel(double saturatedPixel){ this._saturatedPixel = saturatedPixel; }

    /**
     * Getter of resolution of the bin
     * @return int resolution of the image
     */
    public int getResolution(){	return this._resolution;}

    /**
     * Setter of resolution of the bin
     * @param resolution int new resolution
     */
    public void setResolution(int resolution){	this._resolution = resolution;}

    /**
     * Setter of size of the matrix
     * @param size int new size of the matrix
     */
    public void setMatrixSize(int size){ this._matrixSize = size; }

    /**
     * setter step between image
     * @param step int step
     */
    public void setStep(int step){ this._step = step;}

    /**
     * Getter of threshold for the detection of the regional maxima
     * @return int threshold
     */
    public int getThresholdMaxima(){ return _thresholdMaxima;}
    /**
     * Setter of threshold for the detection of the maxima
     * @param thresholdMaxima int new threshold
     */
    public void setThresholdMaxima(int thresholdMaxima) { this._thresholdMaxima = thresholdMaxima;}


    /**
     * Getter of NbZero
     * @return int nb of zero
     */
    public int getNbZero(){ return this._nbZero;}

    /**
     * Setter of nbZero
     * @param nbZero int new nb of zero
     */
    public void setNbZero(int nbZero){ this._nbZero = nbZero;}



    /**
     * Getter is isProcessed
     * @return boolean
     */
    public boolean isProcessed() { return _isProcessed;}

    /**
     * setter isProcessed
     * @param isProcessed boolean
     */
    public void setIsProcessed(boolean isProcessed) { this._isProcessed = isProcessed;}

    /**
     *getter is cooler
     * @return boolean isCooler
     */
    public boolean isCooler() { return _isCooler;}

    /**
     * Setter isCooler
     * @param cool boolean
     */
    public void setIsCooler(boolean cool) { this._isCooler = cool;}

    /**
     * getter isGui
     * @return boolean
     */
    public boolean isGui() { return _isGui;}

    /**
     * setter is gui
     * @param isGui  boolean
     */
    public void setIsGui(boolean isGui) { this._isGui = isGui;}

    /**
     * getter of  _chrSize
     * @return  HashMap<String,Integer>
     */
    public HashMap<String,Integer> getChrSizeHashMap(){return this._chrSize;}
}


