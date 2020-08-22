package utils;

import java.io.*;
import java.util.*;

/**
 * Analyse and detect a whole genome HiC with .hic file or already processed data.
 * The class is used for the observed and oMe method.
 *
 * MorpholibJ method used
 *
 * Collection of mathematical morphology methods and plugins for ImageJ, created at the INRA-IJPB Modeling and Digital Imaging lab.
 * David Legland, Ignacio Arganda-Carreras, Philippe Andrey; MorphoLibJ: integrated library and plugins for mathematical morphology with ImageJ.
 * Bioinformatics 2016; 32 (22): 3532-3534. doi: 10.1093/bioinformatics/btw413
 *
 * @author axel poulet
 *
 */

public class SIPInter {

    /** String path of the input data*/
    private String _input;
    /** Path of the output file*/
    private String _output;
    /** Image size*/
    private int _matrixSize = 0;
    /** Resolution of the bin dump in base*/
    private int _resolution;
    /** Threshold for the maxima detection*/
    private int _thresholdMaxima;
    /** HashMap of the chr size, the key = chr name, value = size of chr*/
    private HashMap<String,Integer> _chrSize =  new HashMap<String,Integer>();
    /** Number of pixel = 0 allowed around the loop*/
    private int _nbZero = -1;
    /** fdr value */
    private double _fdr;
    /** is processed booelan*/
    private boolean _isProcessed = false;
    /** if is gui analysis*/
    private boolean _isGui = false;
    /** if rfdr*/
    private boolean _isDroso = false;
    private double _medianAP = 0;
    private double _medianAPReg = 0;
    private boolean _isCooler = false;
    private double _gauss = 1;
    public SIPInter() {
    }

    /**
    * SIPInter constructor
    *
    */

    public SIPInter(String output, HashMap<String, Integer> chrSize, double gauss, int resolution, int thresholdMax,
                     int matrixSize, int nbZero, double fdr, boolean isProcessed) {
        if(!output.endsWith(File.separator))
            output = output+File.separator;
        this._output = output;
        this._input = output;
        this._chrSize = chrSize;
        this._gauss = gauss;
        this._matrixSize = matrixSize;
        this._resolution = resolution;
        this._thresholdMaxima = thresholdMax;
        this._nbZero = nbZero;
        this._fdr = fdr;
        this._isProcessed = isProcessed;
    }

    /**

     *
     * @param input
     * @param output
     * @param chrSize
     * @param gauss
     * @param resolution
     * @param thresholdMax
     * @param matrixSize
     * @param nbZero
     * @param fdr
     * @param isProcessed
     */
    public SIPInter(String input, String output, HashMap<String, Integer> chrSize, double gauss,  int resolution,
                         int thresholdMax, int matrixSize, int nbZero,  double fdr, boolean isProcessed) {
            if(output.endsWith(File.separator) == false)
                output = output+File.separator;
            if(input.endsWith(File.separator) == false)
                input = input+File.separator;
            this._output = output;
            this._input = input;
            this._chrSize = chrSize;
            this._gauss = gauss;
            this._matrixSize = matrixSize;
            this._resolution = resolution;
            this._thresholdMaxima = thresholdMax;
            this._nbZero = nbZero;
            this._fdr = fdr;
            this._isProcessed = isProcessed;
        }


        /**
         * Save the result file in tabulated file
         *
         * @param pathFile String path for the results file
         * @param first boolean to know idf it is teh first chromo
         * @param data
         * @throws IOException
         */
        public void saveFile(String pathFile, HashMap<String,Loop> data, boolean first) throws IOException{
            FDR fdrDetection = new FDR ();
            fdrDetection.run(this._fdr, data);
            double RFDRcutoff = fdrDetection.getRFDRcutoff();
            double FDRcutoff = fdrDetection.getFDRcutoff();
            boolean supToTen = false;
            if(this._isDroso){
                median(data,FDRcutoff);
                System.out.println("Filtering value at "+this._fdr+" FDR is "+FDRcutoff+" APscore ");
                if(_medianAPReg > 10){
                    supToTen = true;
                    _medianAPReg = _medianAPReg/4;
                    _medianAP = _medianAP/10;
                }
            }
            else
                System.out.println("Filtering value at "+this._fdr+" FDR is "+FDRcutoff+" APscore and "+RFDRcutoff+" RegionalAPscore\n");
            BufferedWriter writer;
            if(first) writer = new BufferedWriter(new FileWriter(new File(pathFile), true));
            else{
                writer = new BufferedWriter(new FileWriter(new File(pathFile)));
                writer.write("chromosome1\tx1\tx2\tchromosome2\ty1\ty2\tcolor\tAPScoreAvg\tProbabilityofEnrichment\tRegAPScoreAvg\tAvg_diffMaxNeihgboor_1\tAvg_diffMaxNeihgboor_2\tavg\tstd\tvalue\n");
            }

            if(data.size()>0){
                Set<String> key = data.keySet();
                Iterator<String> it = key.iterator();
                while (it.hasNext()){
                    String name = it.next();
                    Loop loop = data.get(name);
                    ArrayList<Integer> coord = loop.getCoordinates();
                    if(this._isDroso){
                        if(loop.getPaScoreAvg()> 1.2 && loop.getPaScoreAvg() > 1 && loop.getPaScoreAvg() > FDRcutoff && loop.getPaScoreAvgdev() > .9 && (loop.getNeigbhoord1() > 1 || loop.getNeigbhoord2() > 1)){
                            if(supToTen){
                                if(loop.getRegionalPaScoreAvg() >= (_medianAPReg-_medianAPReg*0.7) && loop.getRegionalPaScoreAvg() <= (_medianAPReg*2)&& loop.getPaScoreAvg() <= (_medianAP*2)){
                                    writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
                                            +"\t"+loop.getPaScoreAvg()+"\t"+loop.getPaScoreAvgdev()+"\t"+loop.getRegionalPaScoreAvg()+"\t"
                                            +loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"
                                            +loop.getStd()+"\t"+loop.getValue()+"\n");
                                }
                            }else{
                                if( loop.getRegionalPaScoreAvg() >= (_medianAPReg-_medianAPReg*0.5) && loop.getRegionalPaScoreAvg() <= (_medianAPReg*2)&& loop.getPaScoreAvg() <= (_medianAP*2)){
                                    writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
                                            +"\t"+loop.getPaScoreAvg()+"\t"+loop.getPaScoreAvgdev()+"\t"+loop.getRegionalPaScoreAvg()+"\t"
                                            +loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"
                                            +loop.getStd()+"\t"+loop.getValue()+"\n");
                                }
                            }
                        }
                    }else{
                        if(loop.getPaScoreAvg()> 1.2 && loop.getPaScoreAvg() > 1 && loop.getPaScoreAvg() > FDRcutoff && loop.getRegionalPaScoreAvg() > RFDRcutoff && loop.getPaScoreAvgdev() > .9){
                            writer.write(loop.getChr()+"\t"+coord.get(2)+"\t"+coord.get(3)+"\t"+loop.getChr()+"\t"+coord.get(0)+"\t"+coord.get(1)+"\t0,0,0"
                                    +"\t"+loop.getPaScoreAvg()+"\t"+loop.getPaScoreAvgdev()+"\t"+loop.getRegionalPaScoreAvg()+"\t"
                                    +loop.getNeigbhoord1()+"\t"+loop.getNeigbhoord2()+"\t"+loop.getAvg()+"\t"
                                    +loop.getStd()+"\t"+loop.getValue()+"\n");
                        }
                    }
                }
                writer.close();
            }
        }



        /**
         * Full the list with file in directory
         * @param dir
         * @return
         * @throws IOException
         */

        public File[] fillList(String dir) throws IOException{
            File folder = new File(dir);
            File[] listOfFiles = folder.listFiles();
            return listOfFiles;
        }


        /**
         * Test the normalized vector by chromosme
         * @param normFile
         */
        public HashMap<Integer, String> getNormValueFilter(String normFile){
            BufferedReader br;
            int lineNumber = 0;
            HashMap<Integer, String> vector = new HashMap<Integer, String>();
            try {
                br = new BufferedReader(new FileReader(normFile));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null){
                    sb.append(line);
                    if((line.equals("NaN")|| line.equals("NAN") || line.equals("nan") || line.equals("na")  || Double.parseDouble(line) < 0.30)){
                        vector.put(lineNumber*this._resolution, "plop");
                    }
                    ++lineNumber;
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) { e.printStackTrace();}
            return vector;
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
                System.out.println("AP\t"+_medianAP+"\nAPREG\t"+_medianAPReg);
            }
        }


        public double getFdr() { 	return	this._fdr; }
        public void setFdr(double fdr) { 		this._fdr = fdr; }
        /**
         * Getter of the input dir
         * @return path of the input dir
         */
        public String getInputDir(){ return this._input; }

        /**
         * Getter of the matrix size
         *
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
         * @param outputDir
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
         * @return
         */
        public int getResolution(){	return this._resolution;}

        /**
         * Setter of resolution of the bin
         * @param resolution
         */
        public void setResolution(int resolution){	this._resolution = resolution;}

        /**
         * Setter of size of the matrix
         * @param size
         */
        public void setMatrixSize(int size){ this._matrixSize = size; }

        /**
         * Getter of threshold for the detction of the maxima
         * @return
         */
        public int getThresholdMaxima(){ return _thresholdMaxima;}
        /**
         * Setter of threshold for the detection of the maxima
         * @param thresholdMaxima
         */
        public void setThresholdMaxima(int thresholdMaxima) { this._thresholdMaxima = thresholdMaxima;}





        /**
         * Getter of getNbZero
         * @return
         */
        public int getNbZero(){ return this._nbZero;}


        public void setNbZero(int nbZero){ this._nbZero = nbZero;}



        public boolean isDroso(){return this._isDroso;}
        public void setIsDroso(boolean droso){	this._isDroso = droso;}

        public HashMap<String,Integer> getChrSizeHashMap(){return this._chrSize;}
        public void setChrSizeHashMap(HashMap<String,Integer> chrSize){this._chrSize = chrSize;}



        /**
         *
         * @return
         */
        public boolean isProcessed() { return _isProcessed;}
        public void setIsProcessed(boolean isProcessed) { this._isProcessed = isProcessed;}

        /**
         *
         * @return
         */
        public boolean isCooler() { return _isCooler;}
        public void setIsCooler(boolean cool) { this._isCooler = cool;}

        /**
         *
         * @return
         */
        public boolean isGui() { return _isGui;}
        public void setIsGui(boolean _isGui) { this._isGui = _isGui;}
}
