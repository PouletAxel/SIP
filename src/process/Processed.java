package process;


import cli.CLIOptionProcessed;
import gui.GuiAnalysis;
import multiProcesing.ProcessDetectLoops;
import multiProcesing.ProcessDumpData;
import org.apache.commons.cli.CommandLine;
import sip.SIPInter;
import sip.SIPIntra;
import utils.MultiResProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class Processed {
    /** */
    private SIPIntra _sipIntra;
    /** */
    private SIPInter _sipInter;
    /** */
    private CommandLine _cmd;
    /** */
    private String _input;
    /** */
    private String _output;
    /** */
    private String _chrSizeFile;
    /** */
    private int _nbZero;
    /** */
    private boolean _delImages;
    /** */
    private int _cpu;
    /** */
    private boolean _isGui;
    /** */
    private GuiAnalysis _guiAnalysis;
    /** */
    private String _interOrIntra;
    /** */
    private ParametersCheck _parameterCheck;
     /** */
    String _log;

    /**
     *
     * @param args
     * @throws Exception
     */
    public Processed(String args []){
        _isGui = false;
        CLIOptionProcessed cli = new CLIOptionProcessed(args);
        _cmd = cli.getCommandLine();
        _input = _cmd.getOptionValue("input");
        _output = _cmd.getOptionValue("output");
        _log = _output+File.separator+"log.txt";
        _delImages = true;
        _nbZero = 6;
        _cpu = 1;
    }

    /**
     *
     * @param guiAnalysis
     */
    public Processed(GuiAnalysis guiAnalysis ){
        _isGui = true;
        _guiAnalysis = guiAnalysis;
        _input =  this._guiAnalysis.getInput();
        _output = this._guiAnalysis.getOutputDir();
        _log = _output+File.separator+"log.txt";
        _delImages = true;
        _nbZero = 6;
        _cpu = 1;
    }
    /**
     *
     * Normalisation method to dump the the data with hic method (KR,NONE.VC,VC_SQRT)
     *
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void run() throws IOException, InterruptedException {

        String allParam;

        if(_isGui) {
            if(this._guiAnalysis.isInter())  _interOrIntra = "inter";
            else  _interOrIntra = "intra";
            _chrSizeFile = this._guiAnalysis.getChrSizeFile();
            _nbZero = this._guiAnalysis.getNbZero();
            _delImages = this._guiAnalysis.isDeletTif();
            _cpu = this._guiAnalysis.getNbCpu();
        }else {
            /* common required parameters*/
            _interOrIntra = _cmd.getOptionValue("lt");
            _chrSizeFile = _cmd.getOptionValue("chrSize");
            /* common optional parameters */
            if (_cmd.hasOption("nbZero")) _nbZero = Integer.parseInt(_cmd.getOptionValue("nbZero"));
            if (_cmd.hasOption("delete"))_delImages = Boolean.parseBoolean(_cmd.getOptionValue("delImages"));
            if (_cmd.hasOption("cpu")) _cpu = Integer.parseInt(_cmd.getOptionValue("cpu"));
        }
        File file = new File(_output);
        if(!file.exists()) file.mkdir();
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_log)));

        _parameterCheck = new ParametersCheck(_input, _chrSizeFile, _interOrIntra, writer,true);

        if(_interOrIntra.equals("intra"))
            allParam = runIntra();
        else
            allParam = runInter();


        writer.write(allParam);
        writer.close();


    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String runIntra() throws IOException, InterruptedException {
        /* Param spe intra chromosomal loop*/
        if(_isGui)
            this.setSipIntraGUI();
        else
            this.setSipIntraCLI();

        _sipIntra.setIsGui(_isGui);
        _sipIntra.setIsProcessed(true);
        _sipIntra.setIsCooler(false);


        String allParam = "SIPHiC processed: \n" +
                "input: "+_input+"\n" +
                "output: "+_output+"\n"+
                "inter or intra chromosomal: "+ _interOrIntra +"\n" +
                "gauss: "+this._sipIntra.getGauss()+"\n"+
                "min: "+this._sipIntra.getMin()+"\n"+
                "max: "+this._sipIntra.getMax()+"\n"+
                "matrix size: "+this._sipIntra.getMatrixSize()+"\n"+
                "diagonal size: "+this._sipIntra.getDiagonalSize()+"\n"+
                "resolution: "+this._sipIntra.getResolution()+"\n"+
                "saturated pixel: "+this._sipIntra.getSaturatedPixel()+"\n"+
                "threshold: "+this._sipIntra.getThresholdMaxima()+"\n"+
                "number of zero: "+this._nbZero+"\n"+
                "factor: "+ _sipIntra.getFactor() +"\n"+
                "fdr: "+this._sipIntra.getFdr()+"\n"+
                "delete images: "+_delImages+"\n"+
                "cpu: "+ _cpu+"\n" +
                "isDroso: "+this._sipIntra.isDroso()+"\n";

        System.out.println("########### Starting dump Step inter chromosomal interactions");

        _parameterCheck.optionalParametersValidity(_sipIntra);
        _parameterCheck.speOption(_sipIntra);

        System.out.println("########### Start loop detection\n");
        MultiResProcess multi = new MultiResProcess(_sipIntra, _chrSizeFile);
        multi.run();
        System.out.println("###########End loop detection step\n");
        return allParam;
    }


    /**
     *
     * @throws IOException
     */
    private String runInter() throws IOException, InterruptedException {


        this.setSipInter();
        _sipInter.setIsGui(_isGui);
        _sipInter.setIsProcessed(true);
        _sipInter.setIsCooler(false);


        String allParam = "SIPHiC processed: \n" +
                "input: "+_input+"\n" +
                "output: "+_output+"\n"+
                "inter or intra chromosomal: "+ _interOrIntra +"\n" +
                "gauss: "+this._sipInter.getGauss()+"\n"+
                "matrix size: "+this._sipInter.getMatrixSize()+"\n"+
                "resolution: "+this._sipInter.getResolution()+"\n"+
                "threshold: "+this._sipInter.getThresholdMaxima()+"\n"+
                "number of zero :"+_nbZero+"\n"+
                "fdr "+this._sipInter.getFdr()+"\n"+
                "delete images "+_delImages+"\n"+
                "cpu "+ _cpu+"\n";
        _parameterCheck.optionalParametersValidity(_sipInter);

        String loopFileRes = _sipInter.getOutputDir()+"finalLoops.txt";

        ProcessDetectLoops detectLoops = new ProcessDetectLoops();
        detectLoops.go(_sipInter, loopFileRes);

        return allParam;

    }


    /**
     *
     */
    private void setSipIntraCLI() throws IOException {
        double min = 2.0;
        double max = 2.0;
        double gauss = 1.5;
        int matrixSize = 2000;
        double thresholdMax = 2800;
        double fdr = 0.01;
        int resolution = 5000;
        int diagSize = 6;
        double saturatedPixel = 0.01;
        boolean isDroso = false;
        int factorParam = 1;

        if (_cmd.hasOption("min")) min = Double.parseDouble(_cmd.getOptionValue("min"));
        if (_cmd.hasOption("max")) max = Double.parseDouble(_cmd.getOptionValue("max"));
        if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
        if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
        if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
        if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
        if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
        if (_cmd.hasOption("diagonal")) diagSize = Integer.parseInt(_cmd.getOptionValue("diagonal"));
        if (_cmd.hasOption("saturated")) saturatedPixel = Double.parseDouble(_cmd.getOptionValue("saturated"));
        if (_cmd.hasOption("isDroso")) isDroso = Boolean.parseBoolean(_cmd.getOptionValue("isDroso"));
        if (_cmd.hasOption("factor")){
            factorParam = Integer.parseInt(_cmd.getOptionValue("factor"));
            _parameterCheck.checkFactor(factorParam);
        }

        _sipIntra = new SIPIntra(_input,_output, _chrSizeFile, gauss, min, max, resolution, saturatedPixel,
                thresholdMax, diagSize, matrixSize, _nbZero, factorParam, fdr, isDroso,_delImages, _cpu);
    }
    /**
     *
     *
     */
    private void setSipIntraGUI(){
        _sipIntra = new SIPIntra(_input, _output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getMin(),
                _guiAnalysis.getMax(), _guiAnalysis.getResolution(), _guiAnalysis.getSaturatedPixel(),
                _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getDiagSize(), _guiAnalysis.getMatrixSize(),
                _nbZero, _guiAnalysis.getFactorChoice(), _guiAnalysis.getFDR(), _guiAnalysis.isDroso(),_delImages, _cpu);


    }

    /**
     *
     *
     */
    private void setSipInter() {

        if(_isGui){
            _sipInter = new SIPInter(_input,_output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getResolution(),
                    _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getMatrixSize(), _nbZero, _guiAnalysis.getFDR(), _delImages,_cpu);

        }else{
            double gauss = 1;
            int matrixSize = 500;
            double thresholdMax = 0.9;
            double fdr = 0.025;
            int resolution = 100000;
            if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
            if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
            if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
            if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
            if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
            _sipInter = new SIPInter(_input,_output, _chrSizeFile, gauss, resolution,  thresholdMax, matrixSize, _nbZero, fdr, _delImages,_cpu);
        }


    }
}
