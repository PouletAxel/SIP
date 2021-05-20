package plop.process;


import plop.cli.CLIOptionCool;
import plop.gui.GuiAnalysis;
import plop.multiProcessing.ProcessDumpCooler;
import plop.multiProcessing.ProcessDetectLoops;
import org.apache.commons.cli.CommandLine;
import plop.sip.SIPInter;
import plop.sip.SIPIntra;
import plop.utils.MultiResProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 */
public class Cool {
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
    private String _coolTool;
    /** */
    private String _interOrIntra;
    /** */
    private ParametersCheck _parameterCheck;
    /** */
    private  String _cooler;
    /** */
    String _log;

    /**
     *
     * @param args
     * @throws Exception
     */
    public Cool(String args []){
        _isGui = false;
        CLIOptionCool cli = new CLIOptionCool(args);
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
    public Cool(GuiAnalysis guiAnalysis ){
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
            _cooler = this._guiAnalysis.getCooler();
            if(this._guiAnalysis.isInter())  _interOrIntra = "inter";
            else  _interOrIntra = "intra";
            _chrSizeFile = this._guiAnalysis.getChrSizeFile();
            _coolTool = this._guiAnalysis.getCooltools();
            _nbZero = this._guiAnalysis.getNbZero();
            _delImages = this._guiAnalysis.isDeletTif();
            _cpu = this._guiAnalysis.getNbCpu();
        }else {
            /* common required parameters*/

            _cooler = _cmd.getOptionValue("cooler");
            _coolTool = _cmd.getOptionValue("cooltools");
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

        _parameterCheck = new ParametersCheck(_input, _chrSizeFile, _interOrIntra, writer,false);

        _parameterCheck.testCoolOption(_coolTool, _cooler);

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
        _sipIntra.setIsProcessed(false);
        _sipIntra.setIsCooler(true);

        ProcessDumpCooler processDumpData = new ProcessDumpCooler();
        String allParam = "SIPHiC hic: \n" +
                "input: "+_input+"\n" +
                "output: "+_output+"\n"+
                "cooler: "+ _cooler +"\n"+
                "cooltools: "+ _coolTool +"\n" +
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

        processDumpData.go(_coolTool,_cooler, _sipIntra, _input);
        System.out.println("########### End of the dump step\n");

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
        ProcessDumpCooler processDumpData = new ProcessDumpCooler();

        this.setSipInter();
        _sipInter.setIsGui(_isGui);
        _sipInter.setIsProcessed(false);
        _sipInter.setIsCooler(true);

        _parameterCheck.optionalParametersValidity(_sipInter);
        System.out.println("########### Starting dump Step inter chromosomal interactions");
        processDumpData.go(_cooler, _sipInter, _input);
        System.out.println("########### !!! End dump Step inter chromosomal interactions");
        String loopFileRes = _sipInter.getOutputDir()+"finalLoops.txt";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(loopFileRes)));
        ProcessDetectLoops detectLoops = new ProcessDetectLoops();

        System.out.println("########### Starting loop detection");
        detectLoops.go(_sipInter, loopFileRes);
        System.out.println("########### !!!!!!! end java.plop.loops detection");
        return "SIPHiC hic: \n" +  "input: "+_input+"\n" +  "output: "+_output+"\n"+ "cooler: "+ _cooler +"\n"+
                "inter or intra chromosomal: "+ _interOrIntra +"\n" +
                "gauss: "+this._sipInter.getGauss()+"\n"+ "matrix size: "+this._sipInter.getMatrixSize()+"\n"+
                "resolution: "+this._sipInter.getResolution()+"\n"+  "threshold: "+this._sipInter.getThresholdMaxima()+"\n"+
                "number of zero :"+_sipInter.getNbZero()+"\n"+ "fdr "+this._sipInter.getFdr()+"\n"+ "delete images "+_delImages+"\n"+
                "cpu "+ _cpu+"\n";
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

        _sipIntra = new SIPIntra(_output, _chrSizeFile, gauss, min, max, resolution, saturatedPixel,
                thresholdMax, diagSize, matrixSize, _nbZero, factorParam, fdr, isDroso,_delImages, _cpu);
    }
    /**
     *
     *
     */
    private void setSipIntraGUI(){
        _sipIntra = new SIPIntra(_output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getMin(),
                _guiAnalysis.getMax(), _guiAnalysis.getResolution(), _guiAnalysis.getSaturatedPixel(),
                _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getDiagSize(), _guiAnalysis.getMatrixSize(),
                _nbZero, _guiAnalysis.getFactorChoice(), _guiAnalysis.getFDR(), _guiAnalysis.isDroso(),_delImages, _cpu);
    }

    /**
     *
     *
     */
    private void setSipInter(){

        if(_isGui){
            _sipInter = new SIPInter(_output, _chrSizeFile, _guiAnalysis.getGaussian(), _guiAnalysis.getResolution(),
                    _guiAnalysis.getThresholdMaxima(), _guiAnalysis.getMatrixSize(), _nbZero, _guiAnalysis.getFDR(), _delImages,_cpu);

        }else{
            double gauss = 1;
            int matrixSize = 500;
            double thresholdMax = 10;
            double fdr = 0.025;
            int resolution = 100000;
            if (_cmd.hasOption("gaussian")) gauss = Double.parseDouble(_cmd.getOptionValue("gaussian"));
            if (_cmd.hasOption("matrixSize")) matrixSize = Integer.parseInt(_cmd.getOptionValue("matrixSize"));
            if (_cmd.hasOption("threshold")) thresholdMax = Double.parseDouble(_cmd.getOptionValue("threshold"));
            if (_cmd.hasOption("fdr")) fdr = Double.parseDouble(_cmd.getOptionValue("fdr"));
            if (_cmd.hasOption("resolution")) resolution = Integer.parseInt(_cmd.getOptionValue("resolution"));
            _sipInter = new SIPInter(_output, _chrSizeFile, gauss, resolution,  thresholdMax, matrixSize, _nbZero, fdr, _delImages,_cpu);
        }


    }
}
