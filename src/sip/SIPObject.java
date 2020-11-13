package sip;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Analyse and detect a whole genome HiC with .hic file or already processed data.
 * The class is used for the observed and oMe method.
 *
 *
 * @author axel poulet
 *
 */
public class SIPObject {


    /** String path of the input data*/
    private String _input;
    /** Path of the output file*/
    private String _output;
    /** Strength of the gaussian filter*/
    private double _gauss;
    /** double fdr value choose by the user*/
    private double _fdr;
    /** Image size*/
    private int _matrixSize;
    /** Resolution of the bin dump in base*/
    private int _resolution;
    /** Threshold for the maxima detection*/
    private double _thresholdMaxima;
    /** Number of pixel = 0 allowed around the loop*/
    private int _nbZero;
    /** is processed booelan*/
    private boolean _isProcessed;
    /** if is gui analysis*/
    private boolean _isGui;
    /** if data set is mcool format*/
    private boolean _isCooler;
    /** */
    private boolean _keepTif;
    /** HashMap of the chr size, the key = chr name, value = size of chr*/
    private HashMap<String,Integer> _chrSizeHashMap =  new HashMap<String,Integer>();
    /** */
    private String _chrSizeFile;

    /**
     *
     * @param input
     * @param output
     * @param gauss
     * @param resolution
     * @param thresholdMax
      * @param matrixSize
     * @param nbZero
     * @param fdr
     */
    public SIPObject (String input, String output, double gauss, int resolution, double thresholdMax, int matrixSize, int nbZero,  double fdr, String chrSizeFile){
        if(!output.endsWith(File.separator))  output = output+File.separator;
        if(!input.endsWith(File.separator))   input = input+File.separator;
        this._input = input;
        this._output = output;
        this._gauss = gauss;
        this._resolution = resolution;
        this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        this._fdr = fdr;
        this._chrSizeFile = chrSizeFile;
        setChrSize(this._chrSizeFile);
    }

    /**
     *
     * @param output
     * @param gauss
     * @param resolution
     * @param thresholdMax
     * @param matrixSize
     * @param nbZero
     * @param fdr
     * @param keepTif
     */
    public SIPObject ( String output, double gauss, int resolution, double thresholdMax, int matrixSize, int nbZero,  double fdr,boolean keepTif, String chrSizeFile ){

        this._output = output;
        this._gauss = gauss;
        this._resolution = resolution;
        this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        this._keepTif = keepTif;
        _fdr = fdr;
        this._chrSizeFile = chrSizeFile;
        setChrSize(this._chrSizeFile);



    }

    public SIPObject(){

    }


    /**
     * getter of fdr parameter
     * @return fdr value
     */
    public double getFdr() { return	this._fdr; }

    /**
     * setter of fdr value
     * @param fdr new fdr value
     *
     */
    public void setFdr(double fdr) { this._fdr = fdr; }
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
     * @param outputDir path of output directory
     */
    public void setOutputDir(String outputDir){	this._output = outputDir;}

    /**
     * Getter of the gaussian blur strength
     * @return double gaussian filter strength
     */
    public double getGauss(){ return this._gauss; }

    /**
     * Setter of the gaussian blur strength
     * @param gauss new gaussian filter strength
     */
    public void setGauss(double gauss){ this._gauss = gauss; }

    /**
     * Getter of resolution of the bin
     * @return bin size
     */
    public int getResolution(){	return this._resolution;}

    /**
     * Setter of resolution of the bin
     * @param resolution bin size
     */
    public void setResolution(int resolution){	this._resolution = resolution;}

    /**
     * Setter of size of the matrix
     * @param size image size
     */
    public void setMatrixSize(int size){ this._matrixSize = size; }

    /**
     * Getter of threshold for the loop detection
     * @return threshold
     */
    public double getThresholdMaxima(){ return _thresholdMaxima;}
    /**
     * Setter of threshold for the detection of the maxima
     * @param thresholdMaxima threshold
     */
    public void setThresholdMaxima(double thresholdMaxima) { this._thresholdMaxima = thresholdMaxima;}

    /**
            * Getter of getNbZero
	 * @return int nb of zero allowed around the loops
	 */
    public int getNbZero(){ return this._nbZero;}

    /**
     * setter of nb of zero
     * @param nbZero int nb of zero allowed around the loops
     */
    public void setNbZero(int nbZero){ this._nbZero = nbZero;}

    /**
     * getter boolean isProcessed
     * true input is SIP processed dat
     * @return boolean
     */
    public boolean isProcessed() { return _isProcessed;}

    /**
     * setter boolean isProcessed
     * @param isProcessed boolean
     */
    public void setIsProcessed(boolean isProcessed) { this._isProcessed = isProcessed;}

    /**
     * getter isCooler
     * true: input is mcool dataset
     * @return boolean
     */
    public boolean isCooler() { return _isCooler;}

    /**
     * setter isCooler
     * @param cool boolean
     */
    public void setIsCooler(boolean cool) { this._isCooler = cool;}

    /**
     * getter isGui
     * true: program run with GUI
     * @return boolean
     */
    public boolean isGui() { return _isGui;}

    /**
     * setter isGui
     * @param _isGui boolean
     */

    public void setIsGui(boolean _isGui) { this._isGui = _isGui;}


    public static void docError (){
        String doc = ("#SIP Version 1 run with java 8\n"
                + "\nUsage:\n"
                + "\thic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
                + "\tcool <mcoolFile> <chrSizeFile> <Output> <cooltoolsPath> <coolerPath> [options]\n"
                + "\tprocessed <Directory with processed data> <chrSizeFile> <Output> [options]\n"
                + "\nParameters:\n"
                + "\t chrSizeFile: path to the chr size file, with the same name of the chr as in the hic file (i.e. chr1 does not match Chr1 or 1)\n"
                + "\t-res: resolution in bp (default 5000 bp)\n"
                + "\t-mat: matrix size to use for each chunk of the chromosome (default 2000 bins)\n"
                + "\t-d: diagonal size in bins, remove the maxima found at this size (eg: a size of 2 at 5000 bp resolution removes all maxima"
                + " detected at a distance inferior or equal to 10kb) (default 6 bins).\n"
                + "\t-g: Gaussian filter: smoothing factor to reduce noise during primary maxima detection (default 1.5)\n"
                + "\t-cpu: Number of CPU used for SIP processing (default 1)\n"
                + "\t-factor: Multiple resolutions can be specified using:\n"
                + "\t\t-factor 1: run only for the input res (default)\n"
                + "\t\t-factor 2: res and res*2\n"
                + "\t\t-factor 3: res and res*5\n"
                + "\t\t-factor 4: res, res*2 and res*5\n"
                + "\t-max: Maximum filter: increases the region of high intensity (default 2)\n"
                + "\t-min: Minimum filter: removes the isolated high value (default 2)\n"
                + "\t-sat: % of saturated pixel: enhances the contrast in the image (default 0.01)\n"
                + "\t-t Threshold for loops detection (default 2800)\n"
                + "\t-nbZero: number of zeros: number of pixels equal to zero that are allowed in the 24 pixels surrounding the detected maxima (default 6)\n"
                + "\t-norm: <NONE/VC/VC_SQRT/KR> (default KR)\n"
                + "\t-del: true or false, whether not to delete tif files used for loop detection (default true)\n"
                + "\t-fdr: Empirical FDR value for filtering based on random sites (default 0.01)\n"
                + "\t-isDroso: default false, if true apply extra filter to help detect loops similar to those found in D. mel cells\n"
                + "\t-h, --help print help\n"
                + "\nCommand line eg:\n"
                + "\tjava -jar SIP_HiC.jar hic <hicFile> <chrSizeFile> <Output> <juicerToolsPath> [options]\n"
                + "\tjava -jar SIP_HiC.jar cool <mcoolFile> <chrSizeFile> <Output> <cooltoolsPath> <coolerPath> [options]\n"
                + "\tjava -jar SIP_HiC.jar processed <Directory with processed data> <chrSizeFile> <Output> [options]\n"
                + "\nAuthors:\n"
                + "Axel Poulet\n"
                + "\tDepartment of Molecular, Cellular  and Developmental Biology Yale University 165 Prospect St\n"
                + "\tNew Haven, CT 06511, USA\n"
                + "M. Jordan Rowley\n"
                + "\tDepartment of Genetics, Cell Biology and Anatomy, University of Nebraska Medical Center Omaha,NE 68198-5805\n"
                + "\nContact: pouletaxel@gmail.com OR jordan.rowley@unmc.edu");

        System.out.println(doc);
    }

    /**
     * getter of chrSize hashMap
     * @return hashMap chr name => chr size
     */
    public HashMap<String,Integer> getChrSizeHashMap(){return this._chrSizeHashMap;}


    /**
     * getter of chrSize hashMap
     * @return hashMap chr name => chr size
     */
    public void setChrSizeHashMap(HashMap<String,Integer> chrSize){this._chrSizeHashMap = chrSize;}

    /**
     *  Initialize chrSize hashMap
     *
     * @param chrSizeFile path to the chrFile
     */
    public void  setChrSize(String chrSizeFile) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(chrSizeFile));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null){
                sb.append(line);
                String[] parts = line.split("\\t");
                String chr = parts[0];
                int size = Integer.parseInt(parts[1]);
                _chrSizeHashMap.put(chr, size);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param keepTif
     */
    public void setIsKeepTif(boolean keepTif) {
        this._keepTif = keepTif;
    }
}
