package plop.process;


import plop.cli.CLIOptionProcessed;
import plop.gui.GuiAnalysis;
import plop.multiProcessing.ProcessDetectLoops;
import org.apache.commons.cli.CommandLine;
import plop.sip.SIPIntra;
import plop.utils.MultiResProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class Processed {
    /** */
    private SIPIntra _sipIntra;
    /** */
    private CommandLine _cmd;
    /** */
    private String _input;
    /** */
    private String _output;
    /** */
    private String _chrSizeFile;
    /** */
    private boolean _delImages = true;
    /** */
    private int _cpu = 1;
    /** */
    private boolean _isGui;
    /** */
    private GuiAnalysis _guiAnalysis;
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

        String allParam ="";

        if(_isGui) {
            _chrSizeFile = this._guiAnalysis.getChrSizeFile();
            _delImages = this._guiAnalysis.isDeletTif();
            _cpu = this._guiAnalysis.getNbCpu();
        }else {
            /* common required parameters*/
            _chrSizeFile = _cmd.getOptionValue("chrSize");
            /* common optional parameters */
            if (_cmd.hasOption("keepImage"))_delImages = false;
            if (_cmd.hasOption("cpu")) _cpu = Integer.parseInt(_cmd.getOptionValue("cpu"));
        }
        File file = new File(_output);
        if(!file.exists()) file.mkdir();
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_log)));

        _parameterCheck = new ParametersCheck(_input, _chrSizeFile, writer,true);
         allParam = runIntra();



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


        _parameterCheck.optionalParametersValidity(_sipIntra);
        _parameterCheck.speOption(_sipIntra);

        System.out.println("########### Start loop detection\n");
        MultiResProcess multi = new MultiResProcess(_sipIntra, _chrSizeFile);
        multi.run();
        System.out.println("###########End loop detection step\n");
        return "SIPHiC processed: \n" + "input: "+_input+"\n" +  "output: "+_output+"\n"+
                "gauss: "+this._sipIntra.getGauss()+"\n"+
                "min: "+this._sipIntra.getMin()+"\n"+ "max: "+this._sipIntra.getMax()+"\n"+ "matrix size: "+this._sipIntra.getMatrixSize()+"\n"+
                "diagonal size: "+this._sipIntra.getDiagonalSize()+"\n"+ "resolution: "+this._sipIntra.getResolution()+"\n"+
                "saturated pixel: "+this._sipIntra.getSaturatedPixel()+"\n"+ "threshold: "+this._sipIntra.getThresholdMaxima()+"\n"+
                "number of zero: "+this._sipIntra.getNbZero()+"\n"+ "factor: "+ _sipIntra.getFactor() +"\n"+ "fdr: "+this._sipIntra.getFdr()+"\n"+ "delete images: "+_delImages+"\n"+
                "cpu: "+ _cpu+"\n" + "isDroso: "+this._sipIntra.isDroso()+"\n";
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
        int nbZero = 6;

        if (_cmd.hasOption("min")) min = Double.parseDouble(_cmd.getOptionValue("min"));
        if (_cmd.hasOption("max")) max = Double.parseDouble(_cmd.getOptionValue("max"));
        if (_cmd.hasOption("nbZero")) nbZero = Integer.parseInt(_cmd.getOptionValue("nbZero"));
        if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
        if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
        if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
        if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
        if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
        if (_cmd.hasOption("diagonal")) diagSize = Integer.parseInt(_cmd.getOptionValue("diagonal"));
        if (_cmd.hasOption("saturated")) saturatedPixel = Double.parseDouble(_cmd.getOptionValue("saturated"));
        if (_cmd.hasOption("isDroso")) isDroso = true;
        if (_cmd.hasOption("factor")){
            factorParam = Integer.parseInt(_cmd.getOptionValue("factor"));
            _parameterCheck.checkFactor(factorParam);
        }

        _sipIntra = new SIPIntra(_input,_output, _chrSizeFile, gauss, min, max, resolution, saturatedPixel,
                thresholdMax, diagSize, matrixSize, nbZero, factorParam, fdr, isDroso,_delImages, _cpu);
    }
    /**
     *
     *
     */
    private void setSipIntraGUI(){
        _sipIntra = new SIPIntra(_input, _output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getMin(),
                _guiAnalysis.getMax(), _guiAnalysis.getResolution(), _guiAnalysis.getSaturatedPixel(),
                _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getDiagSize(), _guiAnalysis.getMatrixSize(),
                this._guiAnalysis.getNbZero(), _guiAnalysis.getFactorChoice(), _guiAnalysis.getFDR(), _guiAnalysis.isDroso(),_delImages, _cpu);


    }

}
