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

    private double _fdr;
    private double _medianAP;
    private double _medianAPReg;

    /** Image size*/
    private int _matrixSize;
    /** Resolution of the bin dump in base*/
    private int _resolution;
    /** Threshold for the maxima detection*/
    private double _thresholdMaxima;
    /** HashMap of the chr size, the key = chr name, value = size of chr*/
    private HashMap<String,Integer> _chrSize = new HashMap<>();
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

    /**
     *
     *  constructor for hic file
     *
     * @param output
     * @param chrsize
     * @param gauss
     * @param resolution
     * @param thresholdMax
     * @param matrixSize
     * @param nbZero
     * @param keepTif
     * @param fdr
     * @throws IOException
     */
    public SIPInter(String output,String chrsize, double gauss,  int resolution, double thresholdMax, int matrixSize, int nbZero, boolean keepTif,double fdr) throws IOException {

        this._output = output;
        setChrSize(chrsize);
        this._gauss = gauss;
        this._resolution = resolution;
         this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        this._keepTif = keepTif;
        _fdr = fdr;
    }

    /**
     *  constructor for processed data
     *
     * @param output
     * @param chrsize
     * @param gauss
     * @param resolution
     * @param thresholdMax
     * @param matrixSize
     * @param nbZero
     * @param keepTif
     */
    public SIPInter(String input,String output,String chrsize, double gauss, int resolution,
                 double thresholdMax, int matrixSize, int nbZero, boolean keepTif, double fdr) throws IOException {

        this._input = input;
        this._output = output;
        this._gauss = gauss;
        setChrSize(chrsize);
        this._resolution = resolution;
        this._thresholdMaxima = thresholdMax;
        this._matrixSize = matrixSize;
        this._nbZero = nbZero;
        this._keepTif = keepTif;
        _fdr = fdr;

    }

    /**
     *
     * @param pathFile
     * @param hLoop
     * @param first
     * @throws IOException
     */
    public void writeResu(String pathFile, HashMap<String,Loop> hLoop, boolean first) throws IOException {
        FDR fdrDetection = new FDR ();
        fdrDetection.run(this._fdr, hLoop);
        double RFDRcutoff = fdrDetection.getRFDRcutoff();
        double FDRcutoff = fdrDetection.getFDRcutoff();
        System.out.println("Filtering value at "+this._fdr+" FDR is "+FDRcutoff+" APscore and "+RFDRcutoff+" RegionalAPscore\n");
        BufferedWriter writer;
        if(first) writer = new BufferedWriter(new FileWriter(new File(pathFile), true));
        else{
            writer = new BufferedWriter(new FileWriter(new File(pathFile)));
            writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAvg\tAPRegScoreAvg\tAvg_diffMaxNeighbour_1\tAvg_diffMaxNeighbour_2\tavg\tstd\tvalue\tvalueDiff\tnbOfZero\tProbabilityofEnrichment\n");
        }

        if(hLoop.size()>0) {
            Set<String> key = hLoop.keySet();
            Iterator<String> it = key.iterator();
            while (it.hasNext()) {
                Loop loop = hLoop.get(it.next());
                ArrayList<Integer> coord = loop.getCoordinates();
                if (loop.getPaScoreAvg() > FDRcutoff && loop.getRegionalPaScoreAvg() > RFDRcutoff && loop.getValueDiff() > 1.3 && loop.getValue() >= 8) {
                    writer.write(loop.getChr() + "\t" + coord.get(0) + "\t" + coord.get(1) + "\t" + loop.getChr2() + "\t" + coord.get(2) + "\t" + coord.get(3) + "\t0,0,0"
                            + "\t" + loop.getPaScoreAvg() + "\t" + loop.getRegionalPaScoreAvg() + "\t" + loop.getNeigbhoord1() + "\t" + loop.getNeigbhoord2() + "\t" + loop.getAvg() + "\t"
                            + loop.getStd() + "\t" + loop.getValue() + "\t" +loop.getValueDiff()  + "\t" + loop.getNbOfZero() +"\t"+loop.getPaScoreAvgdev()+"\n");
                }
            }
        }
        writer.close();
    }

    /**
     *
     * @return
     */
    private void median(HashMap<String,Loop> data, double fdrCutoff){
        Set<String> key = data.keySet();
        Iterator<String> it = key.iterator();
        ArrayList<Float> n1 = new ArrayList<Float> ();
        ArrayList<Float> n2 = new ArrayList<Float> ();
        int nb = 0;
        while (it.hasNext()){
            String name = it.next();
            Loop loop = data.get(name);
            if(loop.getPaScoreAvg()> 1.2 && loop.getPaScoreAvg() > 1 && loop.getPaScoreAvg() > fdrCutoff && loop.getPaScoreAvgdev() > .9){
                n1.add(loop.getPaScoreAvg());
                n2.add(loop.getRegionalPaScoreAvg());
                nb++;
            }
        }
        if(nb>0){
            n1.sort(Comparator.naturalOrder());
            n2.sort(Comparator.naturalOrder());
            double pos1 = Math.floor((n1.size() - 1.0) / 2.0);
            double pos2 = Math.ceil((n1.size() - 1.0) / 2.0);
            if (pos1 == pos2 ) 	_medianAP = n1.get((int)pos1);
            else _medianAP = (n1.get((int)pos1) + n1.get((int)pos2)) / 2.0 ;
            pos1 = Math.floor((n2.size() - 1.0) / 2.0);
            pos2 = Math.ceil((n2.size() - 1.0) / 2.0);
            if (pos1 == pos2 ) 	_medianAPReg = n2.get((int)pos1);
            else _medianAPReg = (n2.get((int)pos1) + n2.get((int)pos2)) / 2.0 ;
           // System.out.println("AP\t"+_medianAP+"\nAPREG\t"+_medianAPReg);
        }
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
    public double getThresholdMaxima(){ return _thresholdMaxima;}
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
     *
     * @param chrSizeFile
     * @return
     * @throws IOException
     */
    public void  setChrSize(String chrSizeFile) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(chrSizeFile));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null){
            sb.append(line);
            String[] parts = line.split("\\t");
            String chr = parts[0];
            int size = Integer.parseInt(parts[1]);
            _chrSize.put(chr, size);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        br.close();
    }

    /**
     * \
     * @return
     */
    public HashMap<String, Integer> getChrSize() {
        return _chrSize;
    }
}


