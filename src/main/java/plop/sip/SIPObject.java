package plop.sip;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    /** if is java.plop.gui analysis*/
    private boolean _isGui;
    /** if data set is mcool format*/
    private boolean _isCooler;
    /** */
    private boolean _delImage;
    /** HashMap of the chr size, the key = chr name, value = size of chr*/
    private HashMap<String,Integer> _chrSizeHashMap =  new HashMap<String,Integer>();
    /** */
    private String _chrSizeFile;
    /** */
    private ArrayList<String> _listChr =  new ArrayList<>();
    private int _cpu;

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
    public SIPObject (String input, String output, double gauss, int resolution, double thresholdMax, int matrixSize, int nbZero,  double fdr, String chrSizeFile,
                      boolean delTif, int cpu){
        if(!output.endsWith(File.separator))  output = output+File.separator;
        if(!input.endsWith(File.separator))   input = input+File.separator;
        this._input = input;
        this._output = output;
        System.out.println("dans SIPObject: "+output);
        this._gauss = gauss;
        this._resolution = resolution;
        this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        this._fdr = fdr;
        this._chrSizeFile = chrSizeFile;
        setChrSize(this._chrSizeFile);
        this._delImage = delTif;
        this._cpu = cpu;
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
     */
    public SIPObject ( String output, double gauss, int resolution, double thresholdMax, int matrixSize, int nbZero,  double fdr,
          boolean delTif, int cpu){
        if(!output.endsWith(File.separator))  output = output+File.separator;
        this._output = output;
        this._gauss = gauss;
        this._resolution = resolution;
        this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        _fdr = fdr;
        this._delImage = delTif;
        this._cpu = cpu;
    }

    public SIPObject(){

    }


    /**
     * getter of cpu parameter
     * @return nb of cpu
     */
    public String getChrSizeFile() { return	this._chrSizeFile; }

    /**
     * getter of cpu parameter
     * @return nb of cpu
     */
    public int getCpu() { return	this._cpu; }

    /**
     * setter of cpu number
     * @param cpu number
     *
     */
    public void setCpu(int cpu) { this._cpu = cpu; }



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
	 * @return int nb of zero allowed around the java.plop.loops
	 */
    public int getNbZero(){ return this._nbZero;}

    /**
     * setter of nb of zero
     * @param nbZero int nb of zero allowed around the java.plop.loops
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




    /**
     * getter of chrSize hashMap
     * @return hashMap chr name => chr size
     */
    public HashMap<String,Integer> getChrSizeHashMap(){return this._chrSizeHashMap;}

    /**
     * getter of chrSize hashMap
     * @return hashMap chr name => chr size
     */
    public ArrayList<String> getChrList(){return this._listChr;}


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
                _listChr.add(chr);
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
    public void setDelImage(boolean keepTif) {
        this._delImage = keepTif;
    }
    public boolean isDelImage() { return _delImage; }
}
