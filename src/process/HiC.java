package process;


import cli.CLIHelper;
import cli.CLIOptionHiC;
import gui.GuiAnalysis;
import multiProcesing.ProcessDetectLoops;
import multiProcesing.ProcessDumpHic;
import org.apache.commons.cli.CommandLine;
import sip.SIPInter;
import sip.SIPIntra;
import utils.MultiResProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 */
public class HiC {
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
    private boolean _delImages = true;
    /** */
    private int _cpu =1;
    /** */
    private boolean _isGui;
    /** */
    private GuiAnalysis _guiAnalysis;
    /** */
    private String _juicerTool;
    /** */
    private String _interOrIntra;
    /** */
    private ParametersCheck _parameterCheck;
    /** */
    private String _juicerNorm = "KR";
    /** */
    private String _log;

    /**
     *
     * @param args
     * @throws Exception
     */
    public HiC(String args []){
        _isGui = false;
        CLIOptionHiC cli = new CLIOptionHiC(args);
       _cmd = cli.getCommandLine();
       _input = _cmd.getOptionValue("input");
       _output = _cmd.getOptionValue("output");
       System.out.println(_input+"\n"+_output+"\n"+_cmd.getOptionValue("j"));
       _log = _output+File.separator+"log.txt";
    }

    /**
     *
     * @param guiAnalysis
     */
    public HiC(GuiAnalysis guiAnalysis ){
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
        String allParam;
        if(_isGui) {
            _juicerTool = this._guiAnalysis.getJuicerTool();
            if(this._guiAnalysis.isInter())  _interOrIntra = "inter";
            else  _interOrIntra = "intra";
            _chrSizeFile = this._guiAnalysis.getChrSizeFile();
            if(this._guiAnalysis.isNONE()) _juicerNorm = "NONE";
            else if (this._guiAnalysis.isVC()) _juicerNorm = "VC";
            else if (this._guiAnalysis.isVC_SQRT()) _juicerNorm = "VC_SQRT";
            _delImages = this._guiAnalysis.isDeletTif();
            _cpu = this._guiAnalysis.getNbCpu();
        }else {
            /* common required parameters*/
            _juicerTool = _cmd.getOptionValue("juicerTool");
            _interOrIntra = _cmd.getOptionValue("lt");
            System.out.println(_interOrIntra);
            _chrSizeFile = _cmd.getOptionValue("chrSize");
            /* common optional parameters */
            if (_cmd.hasOption("norm")) _juicerNorm = _cmd.getOptionValue("norm");
            if (_cmd.hasOption("keepImage"))_delImages = false;
            if (_cmd.hasOption("cpu")) _cpu = Integer.parseInt(_cmd.getOptionValue("cpu"));
        }

        File file = new File(_output);
        if(!file.exists()) file.mkdir();

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_log)));

        _parameterCheck = new ParametersCheck(_input, _chrSizeFile, _interOrIntra, writer,false);
        _parameterCheck.testHiCOption(_juicerTool, _juicerNorm);

        if(_interOrIntra.equals("intra"))
            allParam = runIntra();
        else
            allParam = runInter();

       ;
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
        if(_isGui) this.setSipIntraGUI();
        else  this.setSipIntraCLI();

        _sipIntra.setIsGui(_isGui);
        _sipIntra.setIsProcessed(false);
        _sipIntra.setIsCooler(false);


       _parameterCheck.optionalParametersValidity(_sipIntra);
       _parameterCheck.speOption(_sipIntra);
        System.out.println("########### Starting dump Step inter chromosomal interactions");
        ProcessDumpHic processDumpData = new ProcessDumpHic();
        processDumpData.go(_input, _sipIntra, _juicerTool, _juicerNorm);
        System.out.println("########### End of the dump step\n");

        System.out.println("########### Start loop detection\n");
        MultiResProcess multi = new MultiResProcess(_sipIntra, _chrSizeFile);
        multi.run();
        System.out.println("###########End loop detection step\n");

       // System.out.println(allParam);
        return  "SIPHiC hic: \n" +    "input: "+_input+"\n" + "output: "+_output+"\n"+  "juiceBox: "+ _juicerTool +"\n"+
                "norm: "+ _juicerNorm +"\n" + "inter or intra chromosomal: "+ _interOrIntra +"\n" + "gauss: "+this._sipIntra.getGauss()+"\n"+
                "min: "+this._sipIntra.getMin()+"\n"+ "max: "+this._sipIntra.getMax()+"\n"+ "matrix size: "+this._sipIntra.getMatrixSize()+"\n"+
                "diagonal size: "+this._sipIntra.getDiagonalSize()+"\n"+ "resolution: "+this._sipIntra.getResolution()+"\n"+ "saturated pixel: "+this._sipIntra.getSaturatedPixel()+"\n"+
                "threshold: "+this._sipIntra.getThresholdMaxima()+"\n"+ "number of zero: "+this._sipIntra.getNbZero()+"\n"+ "factor: "+ _sipIntra.getFactor() +"\n"+
                "fdr: "+this._sipIntra.getFdr()+"\n"+ "delete images: "+_delImages+"\n"+  "cpu: "+ _cpu+"\n" + "isDroso: "+this._sipIntra.isDroso()+"\n";
    }


    /**
     *
     * @throws IOException
     */
    private String runInter() throws IOException, InterruptedException {
        ProcessDumpHic processDumpData = new ProcessDumpHic();

        this.setSipInter();
        _sipInter.setIsGui(_isGui);
        _sipInter.setIsProcessed(false);
        _sipInter.setIsCooler(false);

        _parameterCheck.optionalParametersValidity(_sipInter);
        System.out.println("########### Starting dump Step inter chromosomal interactions");
        processDumpData.go(_input,_sipInter, _juicerTool, _juicerNorm);
        System.out.println("########### !!! End dump Step inter chromosomal interactions");
        String loopFileRes = _sipInter.getOutputDir()+"finalLoops.txt";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(loopFileRes)));
        ProcessDetectLoops detectLoops = new ProcessDetectLoops();

        System.out.println("########### Starting loop detection");
        detectLoops.go(_sipInter, loopFileRes);
        System.out.println("########### !!!!!!! end loops detection");
        return "SIPHiC hic: \n" +  "input: "+_input+"\n" +  "output: "+_output+"\n"+ "juiceBox: "+ _juicerTool +"\n"+
                "norm: "+ _juicerNorm +"\n" + "inter or intra chromosomal: "+ _interOrIntra +"\n" +
                "gauss: "+this._sipInter.getGauss()+"\n"+ "matrix size: "+this._sipInter.getMatrixSize()+"\n"+
                "resolution: "+this._sipInter.getResolution()+"\n"+  "threshold: "+this._sipInter.getThresholdMaxima()+"\n"+
                "number of zero :"+_sipInter.getNbZero()+"\n"+ "fdr "+this._sipInter.getFdr()+"\n"+ "delete images "+_delImages+"\n"+
                "cpu "+ _cpu+"\n";

    }


    /**
     *
     */
    private void setSipIntraCLI(){
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
        try {
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
            if (_cmd.hasOption("isDroso")){ isDroso = true; }
            if (_cmd.hasOption("factor")){
                factorParam = Integer.parseInt(_cmd.getOptionValue("factor"));
                _parameterCheck.checkFactor(factorParam);
            }
             _sipIntra = new SIPIntra(_output, _chrSizeFile, gauss, min, max, resolution, saturatedPixel,
                             thresholdMax, diagSize, matrixSize, nbZero, factorParam, fdr, isDroso,_delImages, _cpu);
        }catch (NumberFormatException ex) {
                System.out.println("\n!!!!!! Error number is needed : \n"+ex.toString()+"\n");
                CLIHelper.getHelperAllInfos();
        } catch (IOException e) {   e.printStackTrace(); }
    }
    /**
     *
     *
     */
    private void setSipIntraGUI(){

          _sipIntra = new SIPIntra(_output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getMin(),
               _guiAnalysis.getMax(), _guiAnalysis.getResolution(), _guiAnalysis.getSaturatedPixel(),
               _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getDiagSize(), _guiAnalysis.getMatrixSize(),
                  this._guiAnalysis.getNbZero(), _guiAnalysis.getFactorChoice(), _guiAnalysis.getFDR(), _guiAnalysis.isDroso(),_delImages, _cpu);


    }

    /**
     *
     *
     */
    private void setSipInter(){

        if(_isGui){
            _sipInter = new SIPInter(_output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getResolution(),
                    _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getMatrixSize(), _guiAnalysis.getNbZero(), _guiAnalysis.getFDR(), _delImages,_cpu);

        }else{
            double gauss = 1;
            int matrixSize = 500;
            double thresholdMax = 0.9;
            double fdr = 0.025;
            int resolution = 100000;
            int nbZero = 3;
            if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
            if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
            if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
            if (_cmd.hasOption("nbZero")) nbZero = Integer.parseInt(_cmd.getOptionValue("nbZero"));
            if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
            if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
            _sipInter = new SIPInter(_output, _chrSizeFile, gauss, resolution,  thresholdMax, matrixSize, nbZero, fdr, _delImages,_cpu);
        }


    }
}
